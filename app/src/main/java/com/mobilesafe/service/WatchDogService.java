package com.mobilesafe.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.mobilesafe.activity.EnterPwdActivity;
import com.mobilesafe.db.dao.AppLockDao;
import com.mobilesafe.utils.LogUtil;

import java.util.List;

/**
 * 看门狗服务，不停监视当前当前手机运行程序信息
 * Created by cclin on 2016/9/11.
 */
public class WatchDogService extends Service{

    private static final String TAG = "[WatchDogService] ";
    private ActivityManager activityManager;
    private boolean isRunning;
    private AppLockDao appLockDao; // 数据库操作类
    private InnerReceiver receiver;// 声明自定义广播接收器
    private DataChangeReceiver dataChangeReceiver;
    private UsageStatsManager usageStatsManager;
    private String tempStopProtectPackname; // 临时停止保护的包名
    private List<String> protectPacknames;// 当前应用需要保护，弹出来一个输入密码界面
    private Intent enterPwdIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.d(TAG+"onCreate");
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        appLockDao = AppLockDao.getIntance(this); // 获取操作类
        protectPacknames = appLockDao.findAll(); // 将数据库记录加载到内存中
        isRunning = true; // 启动子线程
        enterPwdIntent = new Intent(getApplicationContext(), EnterPwdActivity.class); // 实例化Intent
        enterPwdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 服务是没有任务栈信息的，在服务开启activity，需要创建一个新的堆栈

        if (isNoSwitch()) { // 判断当前应用是否开启“有权查看使用权限的应用”这个选项
            Intent intent = new Intent( Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        receiver = new InnerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.mobilesafe.tempstop");
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, intentFilter); // 注册广播

        dataChangeReceiver = new DataChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.mobilesafe.applockchange");
        registerReceiver(dataChangeReceiver, filter);

        new Thread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                while (isRunning){
//                    List<ActivityManager.RunningTaskInfo> infos = activityManager.getRunningTasks(100);
//                    LogUtil.d("infos.size = "+infos.size());
//                    String packname = infos.get(0).topActivity.getPackageName();

                    String packname = getRunningApp(); // 获取当前应用包名
                    LogUtil.d(TAG+"当前用户操作程序:"+packname);

                    if (packname!=null && protectPacknames.contains(packname)){ // 查询数据库消耗资源，查询内存快一点
                        if (packname.equals(tempStopProtectPackname)){ // 判断这个应用程序是否需要临时停止保护
                            LogUtil.d("当前应用需要临时保护");
                        }else {
                            enterPwdIntent.putExtra("packname", packname); // 传递包名
                            startActivity(enterPwdIntent); // 启动跳转
                        }
                    }

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG+"onDestroy");
        isRunning = false;
        unregisterReceiver(receiver);
        receiver = null;
        unregisterReceiver(dataChangeReceiver);
        dataChangeReceiver = null;
        super.onDestroy();
    }

    /**
     * 内部广播接收者用于处理服务于activity之间通信
     */
    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            LogUtil.d("action = "+action);
            switch (action){
                case Intent.ACTION_SCREEN_OFF:
                    LogUtil.d(TAG+"屏幕锁屏了");
                    tempStopProtectPackname = null; // 清空缓存，再次进入程序锁保护模式
                    break;
                case Intent.ACTION_SCREEN_ON:
                    LogUtil.d(TAG+"屏幕解锁了");
                    break;
                case "com.mobilesafe.tempstop":
                    tempStopProtectPackname = intent.getStringExtra("packname");
                    break;
                default:break;
            }

        }
    }

    /**
     * 用于重新加载数据库记录到内存，便于程序锁查询
     */
    private class DataChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d("action = "+action);
            protectPacknames = appLockDao.findAll(); // 将数据库记录加载到内存中
        }
    }

    /**
     * 获取最近使用的应用包名
     * @return
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getRunningApp() {
        long ts = System.currentTimeMillis();
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,ts-2000, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return null;
        }
        UsageStats recentStats = null;
        for (UsageStats usageStats : queryUsageStats) {
            if (recentStats == null ||
                    recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                recentStats = usageStats;
            }
        }
        return recentStats.getPackageName();
    }

    /**
     * 判断调用该设备中“有权查看使用权限的应用”这个选项的APP有没有打开
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager)
                getApplicationContext() .getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return true;
        }
        return false;
    }
}

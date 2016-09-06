package com.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mobilesafe.utils.LogUtil;

import java.util.List;

/**
 * Created by cclin on 2016/9/6.
 */
public class AutoCleanService extends Service {

    private final String TAG = "AutoCleanService: ";

    private ScreenOffReceiver receiver; // 屏幕锁屏广播接收者
    private ActivityManager activityManager; // 活动管理器

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        LogUtil.d(TAG+"onCreate");
        receiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter); // 注册广播

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG+"onDestroy");
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG+"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private class ScreenOffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(TAG+"屏幕锁屏了，开始自动后台清理");

//            List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
//            List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo info: infos){
//                LogUtil.d("包名："+info.processName);
//                activityManager.killBackgroundProcesses(info.processName); // 获取包名(进程名)，杀掉后台进程
//            }
            List<ActivityManager.RunningServiceInfo> infos = activityManager.getRunningServices(1000);
            for (ActivityManager.RunningServiceInfo serviceInfo: infos){
                String packname = serviceInfo.service.getPackageName(); // 服务组件的包名
                LogUtil.d("包名："+packname);
                activityManager.killBackgroundProcesses(packname); // 获取包名(进程名)，杀掉后台进程
            }
        }
    }
}

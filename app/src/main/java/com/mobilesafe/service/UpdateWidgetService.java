package com.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.mobilesafe.R;
import com.mobilesafe.receiver.MyWidget;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cclin on 2016/9/7.
 */
public class UpdateWidgetService extends Service{

    private final String TAG = "UpdateWidgetService: ";
    private Timer timer; // 定时器
    private TimerTask timerTask; // 定时器任务
    private AppWidgetManager awm; // widget管理器
    private ScreenStateReceiver mScreenStateReceiver; // 定义一个广播状态接收者

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        LogUtil.d(TAG+"onCreate");

        awm = AppWidgetManager.getInstance(this); // 获取管理器
        startTimer(); // 启动定时器

        mScreenStateReceiver = new ScreenStateReceiver(); // 实例化广播接收者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenStateReceiver, intentFilter); // 注册receiver
        super.onCreate();
    }

    /**
     * 启动定时器
     */
    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                LogUtil.d("更新widget!!");
                ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class); // 组件名称
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                views.setTextViewText(R.id.process_count, "正在运行的进程："+ SystemInfoUtils.getRunningServicesCount(getApplicationContext())+"个");

                long size = SystemInfoUtils.getAvailMemory(getApplicationContext());
                views.setTextViewText(R.id.process_memory, "可用内存："+ Formatter.formatFileSize(getApplicationContext(), size));

                Intent intent = new Intent();
                intent.setAction("com.mobilesafe.killall");

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
                awm.updateAppWidget(provider, views); // 更新远程某个组件的某个控件
            }
        };
        timer.schedule(timerTask, 0, 3000);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG+"onDestroy");
        if (mScreenStateReceiver!=null) {
            unregisterReceiver(mScreenStateReceiver);
            mScreenStateReceiver = null;
        }
        stopTimer(); // 停止定时器
        super.onDestroy();
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        if (timer!=null && timerTask!=null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

    /**
     * 用于监听屏幕状态广播接收者
     */
    private class ScreenStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); // 获取动作
            LogUtil.d(TAG+"action = "+action);

            switch (action){
                case Intent.ACTION_SCREEN_OFF:
                    LogUtil.d(TAG+"屏幕锁屏了");
                    stopTimer();
                    break;
                case Intent.ACTION_SCREEN_ON:
                    LogUtil.d(TAG+"屏幕解锁了");
                    startTimer();
                    break;
                default:break;
            }
        }
    }
}

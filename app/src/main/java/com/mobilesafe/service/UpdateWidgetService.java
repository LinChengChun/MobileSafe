package com.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
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
    private Timer timer;
    private TimerTask timerTask;
    private AppWidgetManager awm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        LogUtil.d(TAG+"onCreate");

        awm = AppWidgetManager.getInstance(this);
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
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG+"onDestroy");

        timer.cancel();
        timerTask.cancel();
        timer = null;
        timerTask = null;

        super.onDestroy();
    }
}

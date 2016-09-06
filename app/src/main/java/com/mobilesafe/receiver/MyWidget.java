package com.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.mobilesafe.service.UpdateWidgetService;
import com.mobilesafe.utils.LogUtil;

/**
 * Created by cclin on 2016/9/6.
 */
public class MyWidget extends AppWidgetProvider{
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("MyWidget: onReceive");
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LogUtil.d("MyWidget: onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Intent service = new Intent(context, UpdateWidgetService.class);
        context.startService(service);
        LogUtil.d("启动更新Widget服务");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Intent service = new Intent(context, UpdateWidgetService.class);
        context.stopService(service);
        LogUtil.d("停止更新Widget服务");
        super.onDisabled(context);
    }
}

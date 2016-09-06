package com.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobilesafe.utils.LogUtil;

import java.util.List;

/**
 * Created by cclin on 2016/9/7.
 */
public class KillAllReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("自定义的广播消息接收到了");

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = activityManager.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo serviceInfo: infos){
            String packname = serviceInfo.service.getPackageName(); // 服务组件的包名
            LogUtil.d("包名："+packname);
            activityManager.killBackgroundProcesses(packname); // 获取包名(进程名)，杀掉后台进程
        }
    }
}

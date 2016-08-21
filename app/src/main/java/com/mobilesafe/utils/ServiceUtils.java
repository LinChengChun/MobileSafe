package com.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/15.
 */
public class ServiceUtils {

    /**
     * 校验某个服务是否还在运行
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName){

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = activityManager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info: infos){
            String name = info.service.getClassName();
            if (!TextUtils.isEmpty(serviceName) && serviceName.equals(name)){
                return true;
            }
        }
        return false;
    }
}

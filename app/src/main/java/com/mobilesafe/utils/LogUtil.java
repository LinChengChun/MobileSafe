package com.mobilesafe.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/7/2.
 */
public class LogUtil {
    private static String TAG = "Trim";
    private static boolean isDebug = true;

    public static void i(String msg){
        if (isDebug)
            Log.i(TAG, msg);
    }
}

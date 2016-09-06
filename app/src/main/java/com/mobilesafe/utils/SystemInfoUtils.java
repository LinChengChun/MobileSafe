package com.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 系统信息的工具类
 * Created by cclin on 2016/9/4.
 */
public class SystemInfoUtils {

    /**
     * 获取正在运行的进程的数量
     * @param context 上下文
     * @return
     */
    public static int getRunningProcessCount(Context context){
        // PackageManager 包管理器 相当于程序管理器
        // ActivityManager 进程管理器 管理手机活动信息，获取动态内容
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos  = am.getRunningAppProcesses();
        return infos.size();
    }

    /**
     * 获取正在运行的进程的数量
     * @param context 上下文
     * @return
     */
    public static int getRunningServicesCount(Context context){
        // PackageManager 包管理器 相当于程序管理器
        // ActivityManager 进程管理器 管理手机活动信息，获取动态内容
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos  = am.getRunningServices(10000);
        return infos.size();
    }

    /**
     * 获取系统可用内存
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);

        return memoryInfo.availMem;
    }

    /**
     * 获取系统总内存
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context){
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(memoryInfo);

//        return memoryInfo.totalMem;
        File file = new File("proc/meminfo");
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            for (char c: line.toCharArray()){
                if (c>= '0' && c<='9')
                    sb.append(c);
            }

            return Long.parseLong(sb.toString())<<10; // 单位为kb，应该乘以1024转换成b
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

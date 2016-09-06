package com.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.mobilesafe.bean.TaskInfo;
import com.mobilesafe.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供手机进程信息
 * Created by cclin on 2016/9/4.
 */
public class TaskInfoProvider {

    /**
     * 用于获取和填充进程信息集合
     * @param context
     * @return
     */
    public static List<TaskInfo> getTaskInfo(Context context){

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); // 获取活动管理器
        PackageManager pm = context.getPackageManager(); // 获取包管理器
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>(); // 创建一个集合对象，用于存储所有进程信息

        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses(); // 获取所有运行进程信息
        for (ActivityManager.RunningAppProcessInfo processInfo: infos){

            TaskInfo taskInfo = new TaskInfo(); // 创建一个进程信息对象
            // 应用程序包名
            String packname = processInfo.processName; // 进程名
            LogUtil.d("packname = "+packname);
            taskInfo.setPackname(packname);

            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});
            long memsize = memoryInfos[0].getTotalPrivateDirty()<<10;
            LogUtil.d("memsize = "+memsize);
            taskInfo.setMemorySize(memsize);

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
                Drawable icon = applicationInfo.loadIcon(pm); // 获取应用小图标
                taskInfo.setIcon(icon);

                String name = applicationInfo.loadLabel(pm).toString(); // 获取应用名称
                taskInfo.setName(name);

                if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 0){
                    // 用户进程
                    taskInfo.setUserTask(true);
                }else {
                    // 系统进程
                    taskInfo.setUserTask(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            taskInfos.add(taskInfo);
        }
        return taskInfos;
    }

    /**
     * 获取系统所有运行服务信息，用来代替所有运行进程信息
     * @param context
     * @return List<TaskInfo>
     */
    public static List<TaskInfo> getServiceInfo(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); // 获取活动管理器
        PackageManager pm = context.getPackageManager(); // 获取包管理器

        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>(); // 创建一个集合对象，用于存储所有进程信息
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(10000);

        for (ActivityManager.RunningServiceInfo serviceInfo: infos){
            TaskInfo taskInfo = new TaskInfo(); // 创建一个进程信息对象

            String packname = serviceInfo.service.getPackageName(); // 服务组件的包名
            LogUtil.d("packname = "+packname);
            LogUtil.d(serviceInfo.process); // 服务运行所在进程
            taskInfo.setPackname(packname);

            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{serviceInfo.pid});
            long memsize = memoryInfos[0].getTotalPrivateDirty()<<10;
            LogUtil.d("memsize = "+memsize);
            taskInfo.setMemorySize(memsize);

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packname, 0);
                Drawable icon = applicationInfo.loadIcon(pm); // 获取应用小图标
                taskInfo.setIcon(icon);

                String name = applicationInfo.loadLabel(pm).toString(); // 获取应用名称
                taskInfo.setName(name);

                if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM) == 0){
                    // 用户进程
                    taskInfo.setUserTask(true);
                }else {
                    // 系统进程
                    taskInfo.setUserTask(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            taskInfos.add(taskInfo); // 添加到集合中
        }

//        LogUtil.d("size = "+infos.size());
        return taskInfos;
    }
}

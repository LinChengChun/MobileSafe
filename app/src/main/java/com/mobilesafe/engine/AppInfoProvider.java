package com.mobilesafe.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.mobilesafe.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务方法，用于获取安装的所有应用信息的提供类
 * Created by Administrator on 2016/8/31.
 */
public class AppInfoProvider {

    /**
     * 获取所有安装的应用程序信息
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context){

        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        PackageManager manager = context.getPackageManager(); // 获取包管理器

        List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
        for (PackageInfo packageInfo: packageInfos){
            AppInfo appInfo = new AppInfo();
            appInfo.setIcon(packageInfo.applicationInfo.loadIcon(manager)); // 加载图片资源
            appInfo.setName(packageInfo.applicationInfo.loadLabel(manager).toString());
            appInfo.setPackname(packageInfo.packageName);
//            appInfo.setRom(packageInfo.applicationInfo.);
            appInfos.add(appInfo);
        }
        return appInfos;
    }

}

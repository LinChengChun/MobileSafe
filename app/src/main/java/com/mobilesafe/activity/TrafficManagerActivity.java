package com.mobilesafe.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;

import java.util.List;

/**
 * 流量管理器页面
 * Created by cclin on 2016/9/12.
 */
public class TrafficManagerActivity extends BaseActivity {
    @Override
    protected int initLayout() {
        return R.layout.activity_traffic_manager;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        // 1.获取包管理器
        PackageManager pm = getPackageManager();
        // 2.遍历手机操作系统 获取所有的应用程序uid
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);

        for (ApplicationInfo info:applicationInfos){
            int uid = info.uid;
            long tx = TrafficStats.getUidTxBytes(uid);
            long rx = TrafficStats.getUidRxBytes(uid);
            // 方法返回值 -1，代表的是应用程序没有产生流量或者操作系统不支持流量统计
        }

        TrafficStats.getMobileTxBytes();// 手机3g,2g接口，上传的总流量
        TrafficStats.getMobileRxBytes();// 手机3g,2g接口，下载的总流量
        TrafficStats.getTotalTxBytes(); // 手机全部网络接口，包括wifi,3g,2g上传的总流量
        TrafficStats.getTotalRxBytes(); // 手机全部网络接口，包括wifi,3g,2g下载的总流量


    }
}

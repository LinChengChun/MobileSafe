package com.mobilesafe.activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.adapter.AppBaseAdapter;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.AppTrafficInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 流量管理器页面
 * Created by cclin on 2016/9/12.
 */
public class TrafficManagerActivity extends BaseActivity {

    @BindView(R.id.lv_app_traffic_state)
    ListView lvAppTrafficState;

    TrafficManagerAdapter adapter; // 适配器
    List<AppTrafficInfo> mAppTrafficInfos; // 用于存储设备所有应用流量信息的集合

    @Override
    protected int initLayout() {
        return R.layout.activity_traffic_manager;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 1.获取包管理器
                PackageManager pm = getPackageManager();
                // 2.遍历手机操作系统 获取所有的应用程序uid
                List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(0);

                mAppTrafficInfos = new ArrayList<AppTrafficInfo>(); // 实例化集合

                for (ApplicationInfo info:applicationInfos){
                    int uid = info.uid;
                    long tx = TrafficStats.getUidTxBytes(uid);
                    long rx = TrafficStats.getUidRxBytes(uid);

                    AppTrafficInfo appTrafficInfo = new AppTrafficInfo();
                    appTrafficInfo.setAppName(info.loadLabel(pm).toString());
                    appTrafficInfo.setUploadTraffic(Formatter.formatFileSize(TrafficManagerActivity.this, tx));
                    appTrafficInfo.setDownloadTraffic(Formatter.formatFileSize(TrafficManagerActivity.this, rx));
//            LogUtil.d(info.loadLabel(pm).toString()+ "上传流量："+ android.text.format.Formatter.formatFileSize(TrafficManagerActivity.this, tx));
//            LogUtil.d(info.loadLabel(pm).toString()+ "下载流量："+ android.text.format.Formatter.formatFileSize(TrafficManagerActivity.this, rx));
                    // 方法返回值 -1，代表的是应用程序没有产生流量或者操作系统不支持流量统计

                    mAppTrafficInfos.add(appTrafficInfo); // 把每一个应用的流量信息添加到集合里边
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new TrafficManagerAdapter(TrafficManagerActivity.this, mAppTrafficInfos);
                        lvAppTrafficState.setAdapter(adapter);
                    }
                });
            }
        }).start();



//        TrafficStats.getMobileTxBytes();// 手机3g,2g接口，上传的总流量
//        TrafficStats.getMobileRxBytes();// 手机3g,2g接口，下载的总流量
//        TrafficStats.getTotalTxBytes(); // 手机全部网络接口，包括wifi,3g,2g上传的总流量
//        TrafficStats.getTotalRxBytes(); // 手机全部网络接口，包括wifi,3g,2g下载的总流量

    }

    /**
     * 流量管理器ListView控件的适配器
     */
    class TrafficManagerAdapter extends AppBaseAdapter<AppTrafficInfo>{

        public TrafficManagerAdapter(Context context, List<AppTrafficInfo> list) {
            super(context, list);
        }

        @Override
        public View getItemView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = View.inflate(TrafficManagerActivity.this, R.layout.list_item_trafficinfo, null);
                viewHolder = new ViewHolder();
                viewHolder.tvTrafficAppName = (TextView) convertView.findViewById(R.id.tv_traffic_app_name);
                viewHolder.tvUploadTrafficSize = (TextView) convertView.findViewById(R.id.tv_upload_traffic_size);
                viewHolder.tvDownloadTrafficSize = (TextView) convertView.findViewById(R.id.tv_download_traffic_size);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            AppTrafficInfo appTrafficInfo = mAppTrafficInfos.get(position);
            viewHolder.tvTrafficAppName.setText(appTrafficInfo.getAppName());
            viewHolder.tvUploadTrafficSize.setText("上传流量:"+appTrafficInfo.getUploadTraffic());
            viewHolder.tvDownloadTrafficSize.setText("下载流量:"+appTrafficInfo.getDownloadTraffic());

            return convertView;
        }
    }

    /**
     * 静态内部缓存类，用于保存控件实例
     */
    static class ViewHolder{
        TextView tvTrafficAppName;
        TextView tvUploadTrafficSize;
        TextView tvDownloadTrafficSize;
    }
}

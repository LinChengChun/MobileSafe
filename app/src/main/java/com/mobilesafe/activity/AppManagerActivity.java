package com.mobilesafe.activity;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.adapter.AppInfosAdapter;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.AppInfo;
import com.mobilesafe.engine.AppInfoProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/31.
 */
public class AppManagerActivity extends BaseActivity {

    @BindView(R.id.tv_avail_rom)
    TextView tvAvailRom;

    @BindView(R.id.tv_avail_sd)
    TextView tvAvailSd;

    @BindView(R.id.ll_app_loading)
    LinearLayout llAppLoading;

    @BindView(R.id.lv_app_manager)
    ListView lvAppManager;

    private List<AppInfo> appInfos; // 集合，用于存储所有应用信息
    private AppInfosAdapter adapter; // 适配器

    @Override
    protected int initLayout() {
        return R.layout.activity_app_manager;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this); // 使用 view 注入框架
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        long availRomSize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath()); // 获取可用内存大小
        long availSdSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()); // 获取SD卡可用内存大小
        tvAvailRom.setText("内存可用: "+ Formatter.formatFileSize(AppManagerActivity.this, availRomSize));
        tvAvailSd.setText("SD卡可用: "+ Formatter.formatFileSize(AppManagerActivity.this, availSdSize));

        llAppLoading.setVisibility(View.VISIBLE); // 开始加载时，显示进度条
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this); // 获取系统已安装所有应用信息集合
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llAppLoading.setVisibility(View.INVISIBLE); // 加载完毕应该隐藏进度条
                        adapter = new AppInfosAdapter(AppManagerActivity.this, appInfos); // 实例化一个适配器对象
                        lvAppManager.setAdapter(adapter);
                    }
                });
            }
        }).start();

    }

    /**
     * 用于获取可用空间大小
     * @param path 内存和SD卡路径
     * @return long型的可用空间大小
     */
    private long getAvailSpace(String path){
        StatFs statFs = new StatFs(path);
        long size = statFs.getBlockSizeLong(); // 获取块大小
        long allcount = statFs.getBlockCountLong(); // 获取块个数
        long count = statFs.getAvailableBlocksLong(); // 获取可用块个数
        return size * count;
    }
}

package com.mobilesafe.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cclin on 2016/9/16.
 */
public class CleanCacheActivity extends BaseActivity{

    @BindView(R.id.pb_clean_cache)
    ProgressBar pbCleanCache;

    @BindView(R.id.tv_scan_status)
    TextView tvScanStatus;

    @BindView(R.id.ll_clean_cache)
    LinearLayout llCleanCache;

    @BindView(R.id.ll_root_clean_cache)
    LinearLayout llRootCleanCache;

    @BindView(R.id.sv_clean_cache)
    ScrollView svCleanCache;

    private PackageManager pm;

    @Override
    protected int initLayout() {
        return R.layout.activity_clean_cache;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    @Override
    protected void initListener() {
        svCleanCache.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                LogUtil.d("svCleanCache onTouch: "+MotionEvent.actionToString(event.getAction()));
                return false;
            }
        });

        llCleanCache.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LogUtil.d("llCleanCache onTouch: "+MotionEvent.actionToString(event.getAction()));
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        scanCache(); // 扫描缓存
    }

    /**
     * 用于扫描手机里所有应用的缓存
     */
    private void scanCache(){
        pm = getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {


//                Method[] methods = PackageManager.class.getMethods(); // 利用反射获取得到缓存的方法
//                for (Method method: methods){
//                    LogUtil.d("method:"+method.getName()+";para:"+method);
//                    if ("getPackageSizeInfo".equals(method.getName())){
//                        getPackageSizeInfoMethod = method;
//                    }
//                }

                try {
                    Method getPackageSizeInfoMethod = null;
                    getPackageSizeInfoMethod = PackageManager.class.
                            getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class); // 利用反射获取getPackageSizeInfo方法
                    List<PackageInfo> packageInfos = pm.getInstalledPackages(0); // 获取系统已安装的应用包信息
                    pbCleanCache.setMax(packageInfos.size()); // 设置进度条最大值
                    int progress = 0;

                    for (final PackageInfo packageInfo: packageInfos) { // 扫描每一个包信息
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvScanStatus.setText("正在扫描："+packageInfo.applicationInfo.loadLabel(pm).toString());
                            }
                        });

                        getPackageSizeInfoMethod.invoke(pm, packageInfo.packageName, new MyStatsObserver());
                        Thread.sleep(20);
                        progress++;
                        pbCleanCache.setProgress(progress); // 更新进度条，可以在子线程中更新
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvScanStatus.setText("扫描完毕。。。");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class MyStatsObserver extends IPackageStatsObserver.Stub{

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            final long cache = pStats.cacheSize;
            long code = pStats.codeSize;
            long data = pStats.dataSize;

            LogUtil.d("cache:"+ Formatter.formatFileSize(CleanCacheActivity.this, cache));
            LogUtil.d("code:"+ Formatter.formatFileSize(CleanCacheActivity.this, code));
            LogUtil.d("data:"+ Formatter.formatFileSize(CleanCacheActivity.this, data));

            final String packname = pStats.packageName; // 获取包名
            LogUtil.d("packname = "+packname);

            final ApplicationInfo appInfo; // 获取应用信息
            try {
                appInfo = pm.getApplicationInfo(packname, 0); // 根据包名获取应用信息
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cache > 0){
                            // 更新界面
                            View view = View.inflate(CleanCacheActivity.this, R.layout.list_item_cacheinfo, null);
                            TextView tvCacheName = (TextView) view.findViewById(R.id.tv_cache_name);
                            TextView tvCacheSize= (TextView) view.findViewById(R.id.tv_cache_size);
                            ImageView ivCacheDelete = (ImageView) view.findViewById(R.id.iv_cache_delete);


                            tvCacheSize.setText("缓存大小:"+Formatter.formatFileSize(CleanCacheActivity.this, cache)); // 设置应用缓存大小
                            tvCacheName.setText(appInfo.loadLabel(pm)); // 设置应用名称

                            ivCacheDelete.setOnClickListener(new View.OnClickListener() { // 设置删除按钮点击逻辑
                                @Override
                                public void onClick(View v) {
                                    LogUtil.d("ivCacheDelete onClick");
                                    try {
                                        Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                                        method.invoke(pm, packname, new MyDataObserver()); // 获取deleteApplicationCacheFiles方法
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            view.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    LogUtil.d("view onTouch "+MotionEvent.actionToString(event.getAction()));
                                    return false;
                                }
                            });

                            ivCacheDelete.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    LogUtil.d("ivCacheDelete onTouch "+MotionEvent.actionToString(event.getAction()));
                                    return false;
                                }
                            });

                            llCleanCache.addView(view, 0); // 把子View添加到父控件
                        }
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyDataObserver extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            LogUtil.d("packageName:"+packageName);
            LogUtil.d("succeeded:"+succeeded);
            LogUtil.d("清除缓存完成");
        }
    }

    /**
     * 利用android系统漏洞，申请分配比较大的内存，触发系统清泪机制，清理手机全部缓存
     * @param view
     */
    public void clearAll(View view) {
        Method[] methods = PackageManager.class.getMethods();
        for (Method method:methods){
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm, 2000, new MyDataObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtil.d("CleanCacheActivity Event: "+MotionEvent.actionToString(ev.getAction()));
        return super.dispatchTouchEvent(ev);
    }

}

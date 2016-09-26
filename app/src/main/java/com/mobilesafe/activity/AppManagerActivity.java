package com.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.adapter.AppInfosAdapter;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.AppInfo;
import com.mobilesafe.db.dao.AppLockDao;
import com.mobilesafe.engine.AppInfoProvider;
import com.mobilesafe.utils.DesityUtil;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/31.
 */
public class AppManagerActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.tv_avail_rom)
    TextView tvAvailRom;

    @BindView(R.id.tv_avail_sd)
    TextView tvAvailSd;

    @BindView(R.id.ll_app_loading)
    LinearLayout llAppLoading;

    @BindView(R.id.lv_app_manager)
    ListView lvAppManager;

    @BindView(R.id.tv_status)
    TextView tvStatus;

    private List<AppInfo> appInfos; // 集合，用于存储所有应用信息

    private List<AppInfo> userAppInfos; // 用户应用集合
    private List<AppInfo> systemAppInfos; // 系统应用集合

    private AppInfosAdapter adapter; // 适配器
    private AppLockDao appLockDao; // 数据库操作类

    private PopupWindow popupWindow; // 弹出式窗体
    private LinearLayout llStart; // 用于启动应用
    private LinearLayout llUninstall; // 用于卸载应用
    private LinearLayout llShare; // 用于分享
    private AppInfo appInfo; // 声明一个AppInfo，用于表示当前点击条目对应的应用信息

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

        // 监听ListView滚动事件
        lvAppManager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userAppInfos != null && systemAppInfos != null){
                    if (firstVisibleItem>userAppInfos.size()){
                        tvStatus.setText("系统应用: "+systemAppInfos.size()+"个");
                    }else{
                        tvStatus.setText("用户应用: "+userAppInfos.size()+"个");
                    }
                }
                dismissPopupWindow(popupWindow);
            }
        });

        lvAppManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    return;
                }else if (position == userAppInfos.size()+1){
                    return;
                }else if (position <= userAppInfos.size()){ // 用户应用
                    int newposition = position-1;
                    appInfo = userAppInfos.get(newposition);
                }else { // 系统应用
                    int newposition = position-1-userAppInfos.size()-1;
                    appInfo = systemAppInfos.get(newposition);
                }
                LogUtil.d("OnItemClick: "+appInfo.getPackname());
//                PromptManager.showShortToast(AppManagerActivity.this, appInfo.getName());

                dismissPopupWindow(popupWindow); // 销毁PopupWindow
                View contentView = View.inflate(AppManagerActivity.this, R.layout.popup_app_item, null); // 加载一个自定义View
                llStart = (LinearLayout) contentView.findViewById(R.id.ll_start);
                llUninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                llShare = (LinearLayout) contentView.findViewById(R.id.ll_share);

                llStart.setOnClickListener(AppManagerActivity.this); // 设置 监听器
                llUninstall.setOnClickListener(AppManagerActivity.this);
                llShare.setOnClickListener(AppManagerActivity.this);

                popupWindow = new PopupWindow(contentView, -2, -2); // new 一个弹出式窗体对象
                // 动画效果的播放必须要求窗体有背景颜色
                // 透明色也是一种颜色
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 设置弹出式窗体背景
                int[] location = new int[2];
                view.getLocationInWindow(location); // 获取当前被点击view的在窗体中的位置
                LogUtil.d("location[0] = "+location[0]+";location[1] = "+location[1]);
                int dip = 60;
                int px = DesityUtil.dip2px(AppManagerActivity.this, dip); // 单位转换 dp<->px
                LogUtil.d("px = "+px);
                popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP, location[0]+px , location[1]);

                // 实例化一个缩放动画
                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(300);

                // 实例化一个透明度动画
                AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
                aa.setDuration(300);

//                int fromX = location[0]+px;
//                int toX = fromX+100;
//                TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, fromX, Animation.RELATIVE_TO_SELF, toX,
//                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
//                ta.setDuration(1000);

                // 实例化一个动画集合，用于设置
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(sa);
                set.addAnimation(aa);
//                set.addAnimation(ta);
                contentView.startAnimation(set);

            }
        });

        lvAppManager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    return true;
                }else if (position == userAppInfos.size()+1){
                    return true;
                }else if (position <= userAppInfos.size()){ // 用户应用
                    int newposition = position-1;
                    appInfo = userAppInfos.get(newposition);
                }else { // 系统应用
                    int newposition = position-1-userAppInfos.size()-1;
                    appInfo = systemAppInfos.get(newposition);
                }
                LogUtil.d("长点击了: "+appInfo.getPackname());

                AppInfosAdapter.ViewHolder viewHolder = (AppInfosAdapter.ViewHolder) view.getTag();
                // 判断条目是否存在程序锁数据库界面
                if (appLockDao.find(appInfo.getPackname())){
                    // 被锁定的程序，解除锁定，更新界面为打开的小锁图片
                    appLockDao.delete(appInfo.getPackname());
                    viewHolder.getIvStatus().setImageResource(R.drawable.unlock);
                }else {
                    // 锁定程序，更新界面为关闭的锁
                    appLockDao.add(appInfo.getPackname());
                    viewHolder.getIvStatus().setImageResource(R.drawable.lock);
                }
                return true;
            }
        });
    }

    @Override
    protected void initData() {

        appLockDao = AppLockDao.getIntance(getApplicationContext()); // 获取Dao实例

        long availRomSize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath()); // 获取可用内存大小
        long availSdSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath()); // 获取SD卡可用内存大小
        tvAvailRom.setText("内存可用: "+ Formatter.formatFileSize(AppManagerActivity.this, availRomSize));
        tvAvailSd.setText("SD卡可用: "+ Formatter.formatFileSize(AppManagerActivity.this, availSdSize));

        fillData(); // 刷新ListView 数据
    }

    /**
     * 用于获取可用空间大小
     * @param path 内存和SD卡路径
     * @return long型的可用空间大小
     */
    private long getAvailSpace(String path){
        StatFs statFs = new StatFs(path);
        long size = 0; // 获取块大小
        long count = 0; // 获取可用块个数
        long allcount = 0; // 获取全部块个数
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            size = statFs.getBlockSizeLong();
            allcount = statFs.getBlockCountLong();
            count = statFs.getAvailableBlocksLong();
        }else {
            size = statFs.getBlockSize();
            allcount = statFs.getBlockCount();
            count = statFs.getAvailableBlocks();
        }

        return size * count;
    }

    /**
     * 关闭下拉窗体
     * @param window
     */
    private void dismissPopupWindow(PopupWindow window){
        if ( null!=window && window.isShowing() ){ // 滚动时检测是否已经显示
            window.dismiss();
            window = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPopupWindow(popupWindow);
    }

    @Override
    public void onClick(View v) {
        dismissPopupWindow(popupWindow);
        switch (v.getId()){
            case R.id.ll_start:
                LogUtil.d("启动应用: "+appInfo.getName());
                startApplication();
                break;

            case R.id.ll_uninstall:
                LogUtil.d("卸载应用: "+appInfo.getName());
                if (appInfo.isUserApp())
                    uninstallApplication();
                else {
                    PromptManager.showShortToast(AppManagerActivity.this, "系统应用只有获取Root权限才能卸载");
                }
                break;

            case R.id.ll_share:
                LogUtil.d("分享应用: "+appInfo.getName());
                shareApplication();
                break;
        }
    }

    /**
     * 用于分享一个应用程序
     */
    private void shareApplication() {
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件，名字叫："+appInfo.getName());
        startActivity(intent);
    }

    /**
     * 用于卸载应用
     */
    private void uninstallApplication() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setAction(Intent.ACTION_DELETE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:"+appInfo.getPackname()));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fillData(); // 刷新ListView 数据
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 用于填充列表数据
     */
    private void fillData() {
        llAppLoading.setVisibility(View.VISIBLE); // 开始加载时，显示进度条
        tvStatus.setVisibility(View.INVISIBLE); // 开始加载时，隐藏显示应用个数
        new Thread(new Runnable() {
            @Override
            public void run() {
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this); // 获取系统已安装所有应用信息集合
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();

                for (AppInfo appInfo:appInfos){
                    if (appInfo.isUserApp()){ // 判断是否是用户应用
                        userAppInfos.add(appInfo); // 添加到用户应用集合
                    }else {
                        systemAppInfos.add(appInfo); // 添加到系统应用集合
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        llAppLoading.setVisibility(View.INVISIBLE); // 加载完毕应该隐藏进度条
                        if (null == adapter) {
                            adapter = new AppInfosAdapter(AppManagerActivity.this, appInfos, userAppInfos, systemAppInfos); // 实例化一个适配器对象
                            lvAppManager.setAdapter(adapter);
                        }else {
                            adapter.notifyDataChange(appInfos, userAppInfos, systemAppInfos); // 已经实例化过适配器，则刷新ListView
                        }
                        tvStatus.setText("用户应用："+userAppInfos.size()+"个"); // 集合填充完毕，则开始显示
                        tvStatus.setVisibility(View.VISIBLE); // 加载完毕时，显示应用个数
                    }
                });
            }
        }).start();
    }

    /**
     * 用于启动其他应用
     */
    private void startApplication() {
        PackageManager pm = getPackageManager();
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        查询系统所有具有启动能力的应用
//        pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);

        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
        if (null != intent)
            startActivity(intent);
    }
}

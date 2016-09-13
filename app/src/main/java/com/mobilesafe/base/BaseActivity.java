package com.mobilesafe.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * 抽取BaseActivity 管理所有activity 方便退出
 */
public abstract class BaseActivity extends FragmentActivity {
    // 管理运行的所有的activity
    public final static List<BaseActivity> mActivities = new LinkedList<BaseActivity>();
    public static BaseActivity mActivity;
//    public static BlackNumberDao mBlackNumberDao; // 数据库操作类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(initLayout());

//        mBlackNumberDao = BlackNumberDao.getIntance(getBaseContext()); // 单例模式

        synchronized (BaseActivity.class) {
            mActivities.add(this);
        }

        initView();//1.初始化 view 控件
        initData();//2.初始化 填充
        initActionBar();//3.初始化 操作栏
        initListener();//4.初始化 监听器
    }

    @Override
    protected void onResume() {
        super.onResume();

        Display display = getWindowManager().getDefaultDisplay();

        mActivity = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivity = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synchronized (BaseActivity.class) {
            mActivities.remove(this);
        }
    }

    /**
     * 检索一个view
     */
    protected <T extends View> T retrieveView(int viewId) {
        return (T) findViewById(viewId);
    }

    /**
     * 干掉所有的Activity
     */
    public void killAll() {
        // 复制了一份mActivities 集合
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new LinkedList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
        // 杀死当前的进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 初始化布局
     */
    protected abstract int initLayout();

    /**
     * 初始化Window
     */
    protected void initWindow() {
    }

    /**
     * 初始化View
     */
    protected void initView() {

    }

    /**
     * 初始化ActionBar
     */
    protected void initActionBar() {

    }

    /**
     * 初始化监听器
     */
    protected void initListener() {

    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

}

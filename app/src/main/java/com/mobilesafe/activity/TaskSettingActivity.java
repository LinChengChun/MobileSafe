package com.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.service.AutoCleanService;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.ServiceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cclin on 2016/9/6.
 */
public class TaskSettingActivity extends BaseActivity{

    @BindView(R.id.cb_show_system)
    CheckBox cbShowSystem;

    @BindView(R.id.cb_auto_clean)
    CheckBox cbAutoClean;

    private SharedPreferences sp; // 存储设置信息

    @Override
    protected int initLayout() {
        return R.layout.activity_task_setting;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this); // 绑定当前页面
    }

    @Override
    protected void initListener() {
        cbShowSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit(); // 获取编辑器
                editor.putBoolean("showsystem", isChecked);
                editor.commit(); // 提交保存修改
            }
        });

        cbAutoClean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 锁屏广播事件是一个特殊的广播事件，在清单文件配置广播接收者是不会生效的，只能在代码里面注册才会生效
                Intent service = new Intent(TaskSettingActivity.this, AutoCleanService.class); // 定义一个显式Intent
                if (isChecked){
                    LogUtil.d("启动锁屏自动清理服务");
                    startService(service); // 启动服务
                }else {
                    LogUtil.d("关闭锁屏自动清理服务");
                    stopService(service); // 停止服务
                }
            }
        });
    }

    @Override
    protected void initData() {
        sp = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), MODE_PRIVATE); // 获取
        cbShowSystem.setChecked(sp.getBoolean("showsystem", false)); // 进入页面应该设置上一次设置的默认值
    }

    @Override
    protected void onStart() {
        boolean isRunning = ServiceUtils.isServiceRunning(TaskSettingActivity.this, "com.mobilesafe.service.AutoCleanService");
        cbAutoClean.setChecked(isRunning); // 设置默认CheckBox状态
        super.onStart();
    }
}

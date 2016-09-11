package com.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cclin on 2016/9/11.
 */
public class EnterPwdActivity extends BaseActivity {

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.iv_icon)
    ImageView ivIcon;

    String packageName; // 用来保存当前用户启动的应用包名

    @Override
    protected int initLayout() {
        return R.layout.activity_enter_pwd;
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
        packageName = getIntent().getStringExtra("packname"); // 获取包名参数
        LogUtil.d("packageName: "+packageName);

        PackageManager pm = this.getPackageManager(); // 获取系统包管理器
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0); // 根据包名获取应用信息
            tvName.setText(applicationInfo.loadLabel(pm)); // 设置应用名称
            ivIcon.setImageDrawable(applicationInfo.loadIcon(pm)); // 设置应用图标
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void click(View view) {
        String pwd = etPassword.getText().toString().trim(); // 获取输入框文本，并去除空格处理
        if (TextUtils.isEmpty(pwd)){
            PromptManager.showShortToast(EnterPwdActivity.this, "输入密码不能为空");
            return;
        }

        if ("123".equals(pwd)){
            // 告诉看门狗这个程序密码输入正确，可以临时停止保护
            Intent intent = new Intent();
            intent.setAction("com.mobilesafe.tempstop");
            intent.putExtra("packname", packageName);
            sendBroadcast(intent); // 发出广播，暂时停止保护

            finish();// 关闭当前输入密码页面
        }else {
            PromptManager.showShortToast(EnterPwdActivity.this, "输入密码错误");
        }
    }

    @Override
    public void onBackPressed() {
//        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY"/>
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_MONKEY);
        startActivity(intent); // 如果用户没有输入密码，按返回键应该跳转到桌面

        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();// 关闭当前输入密码页面
    }
}

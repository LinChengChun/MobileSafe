package com.android.example;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DevicePolicyManager devicePolicyManager = null;
    ComponentName mDeviceAdminSample = null; // 组件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE); // 获取设备管理员服务
        mDeviceAdminSample = new ComponentName(MainActivity.this, DeviceAdminSampleReceiver.class);
    }

    public void lockScreen(View view) {
        devicePolicyManager.lockNow(); // 锁屏
//        devicePolicyManager.resetPassword("123", 0)  // 设配屏幕密码
    }

    /**
     * 用代码去开启管理员
     * @param view
     */
    public void openAdmin(View view) {
        if (!devicePolicyManager.isAdminActive(mDeviceAdminSample)) {
            // 创建一个Intent
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            // 我要激活谁 ComponentName：标志一个特殊的组件
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "使能我，可以启动一键锁屏人生");
            startActivity(intent);
        }else {
            Toast.makeText(getApplicationContext(), "已经添加为设备管理器",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 一个特殊的广播接收器
     */
    public class DeviceAdminSampleReceiver extends DeviceAdminReceiver{
        void showToast(Context context, String msg) {
            String status = context.getString(R.string.admin_receiver_status, msg);
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        }

        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return context.getString(R.string.admin_receiver_status_disable_warning);
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_disabled));
        }

        @Override
        public void onPasswordChanged(Context context, Intent intent) {
            showToast(context, context.getString(R.string.admin_receiver_status_pw_changed));
        }
    }


}

package com.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.MediaInfo;
import com.mobilesafe.receiver.DeviceAdminSampleReceiver;
import com.mobilesafe.service.AddressService;
import com.mobilesafe.service.CallSmsSafeService;
import com.mobilesafe.service.WatchDogService;
import com.mobilesafe.ui.SettingClickView;
import com.mobilesafe.ui.SettingItemView;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.ServiceUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class SettingActivity extends BaseActivity {

    // 设置是否自动更新
    @BindView(R.id.siv_update)
    SettingItemView sivUpdate;

    // 设置是否设置报警音乐
    @BindView(R.id.siv_setAlarmMusic)
    SettingItemView sivSetAlarmMusic;

    // 设置是否打开设备管理员权限
    @BindView(R.id.siv_openDeviceAdmin)
    SettingItemView sivOpenDeviceAdmin;

    // 电话地址查询服务设置
    @BindView(R.id.siv_openPhoneAddressService)
    SettingItemView sivOpenPhoneAddressService;

    // 设归宿地提示框风格
    @BindView(R.id.scv_setAddressBackground)
    SettingClickView scvSetAddressBackground;

    // 黑名单拦截设置
    @BindView(R.id.siv_call_sms_safe)
    SettingItemView sivCallSmsSafe;

    // 看门狗设置
    @BindView(R.id.siv_watch_dog)
    SettingItemView sivWatchDog;

    private SharedPreferences sharedPreferences; // 定义一个私有共享属性
    private final int REQUEST_MEDIA = 123;

    private DevicePolicyManager devicePolicyManager = null; // 声明一个设备安全权限管理者
    private ComponentName mDeviceAdminComponent = null; // 声明一个组件名称

    private String[] items = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

    private int which; // 单选框 条目 位置

    @Override
    protected int initLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        // 从sharedPreferences中读取默认配置
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(getResources().getString(R.string.update), false)){// 设置默认状态
            sivUpdate.setChecked(true);
        }else {
            sivUpdate.setChecked(false);
        }

        if (sharedPreferences.getBoolean("isSetAlarmMusic", false)){// 设置默认状态
            sivSetAlarmMusic.setChecked(true);
        }else {
            sivSetAlarmMusic.setChecked(false);
        }

        if (sharedPreferences.getBoolean("isOpenDeviceAdmin", false)){// 设置默认状态
            sivOpenDeviceAdmin.setChecked(true);
        }else {
            sivOpenDeviceAdmin.setChecked(false);
        }

        // 判断 查询归宿地服务 是否启动  移植到onResume中判断
//        boolean isRunning = ServiceUtils.isServiceRunning(SettingActivity.this,
//                "com.mobilesafe.service.AddressService");
//        sivOpenPhoneAddressService.setChecked(isRunning);


        // 查询上一次设置的归宿地提示框风格，默认设置为上一次设置选项
        which = sharedPreferences.getInt("addressDialogStyle", 0);
        scvSetAddressBackground.setTvDesc(items[which]);

        LogUtil.d("SettingActivity onCreate...");
        // 黑名单拦截设置
//        sivCallSmsSafe

    }

    @Override
    protected void initData() {
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE); // 获取系统设备安全服务
        mDeviceAdminComponent = new ComponentName(this, DeviceAdminSampleReceiver.class); // 实例化组件
    }

    @Override
    protected void initListener() {
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检测是否已经CheckBox状态被选择
                SharedPreferences.Editor editor = sharedPreferences.edit(); // 申请编辑器
                if (sivUpdate.isChecked()){// 假如已经被选择，则取消选项
                    sivUpdate.setChecked(false);
                }else {
                    sivUpdate.setChecked(true);
                }
                editor.putBoolean(getResources().getString(R.string.update), sivUpdate.isChecked());
                editor.commit(); // commit change
            }
        });

        sivSetAlarmMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检测是否已经CheckBox状态被选择
                SharedPreferences.Editor editor = sharedPreferences.edit(); // 申请编辑器
                if (sivSetAlarmMusic.isChecked()){// 假如已经被选择，则取消选项
                    sivSetAlarmMusic.setChecked(false);
                }else {
                    startSearchMediaActivity(); // 跳转到搜索音频页面
                    sivSetAlarmMusic.setChecked(true);
                }
                editor.putBoolean("isSetAlarmMusic", sivSetAlarmMusic.isChecked());
                editor.commit(); // commit change
            }
        });

        sivOpenDeviceAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检测是否已经CheckBox状态被选择
                SharedPreferences.Editor editor = sharedPreferences.edit(); // 申请编辑器
                if (sivOpenDeviceAdmin.isChecked()){// 假如已经被选择，则取消选项
                    if (devicePolicyManager.isAdminActive(mDeviceAdminComponent)) { // 判断当前具备管理权限的组件是否在激活状态
                        LogUtil.i("取消该组件对设备管理权限");
                        devicePolicyManager.removeActiveAdmin(mDeviceAdminComponent); // 取消该组件对设备管理权限
                    }
                    sivOpenDeviceAdmin.setChecked(false);
                }else { // 假如还没激活权限，则启动激活流程
                    startAddDeviceAdmin(); // 启动激活设备管理权限接收器
                    sivOpenDeviceAdmin.setChecked(true);
                }
                editor.putBoolean("isOpenDeviceAdmin", sivOpenDeviceAdmin.isChecked());
                editor.commit(); // commit change
            }
        });

        // 设置归宿地查询监听
        sivOpenPhoneAddressService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AddressService.class);
                if (sivOpenPhoneAddressService.isChecked()){// 假如已经被选择，则取消选项
                    stopService(intent);
                    sivOpenPhoneAddressService.setChecked(false);
                }else {
                    startService(intent);
                    sivOpenPhoneAddressService.setChecked(true);
                }
            }
        });

        scvSetAddressBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("归宿地提示框风格");
                builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichOnClick) {
                        SharedPreferences.Editor editor = sharedPreferences.edit(); // 申请编辑器
                        editor.putInt("addressDialogStyle", whichOnClick);
                        editor.commit(); // commit change

                        scvSetAddressBackground.setTvDesc(items[whichOnClick]); // 设置描述信息
                        which = whichOnClick;
                        dialog.dismiss(); // 选择完方案后对话框消失
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        sivCallSmsSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callSmsSafeIntent = new Intent(SettingActivity.this, CallSmsSafeService.class);
                if (sivCallSmsSafe.isChecked()){// 假如已经被选择，则取消选项
                    stopService(callSmsSafeIntent); // 停止黑名单拦截服务
                    sivCallSmsSafe.setChecked(false);
                }else {
                    startService(callSmsSafeIntent); // 启动黑名单拦截服务
                    sivCallSmsSafe.setChecked(true);
                }
            }
        });

        sivWatchDog.setOnClickListener(new View.OnClickListener() {
            Intent watchIntent = new Intent(SettingActivity.this, WatchDogService.class);
            @Override
            public void onClick(View v) {
                if (sivWatchDog.isChecked()){// 假如已经被选择，则取消选项
                    stopService(watchIntent); // 停止看门狗服务
                    sivWatchDog.setChecked(false);
                }else {
                    startService(watchIntent); // 启动看门狗服务
                    sivWatchDog.setChecked(true);
                }
            }
        });
    }

    /**
     * 进入铃声选择页面
     */
    private void startSearchMediaActivity(){
        Intent intent = new Intent(SettingActivity.this, SearchMediaActivity.class);
        startActivityForResult(intent, REQUEST_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i("requestCode = "+requestCode+";resultCode = "+resultCode+";Activity.RESULT_OK ="+Activity.RESULT_OK);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_MEDIA){
//                Bundle bundle = data.getExtras();
//                MediaInfo mediaInfo = (MediaInfo) data.getSerializableExtra("media");
                LogUtil.i("this is onActivityResult。。。");
                MediaInfo mediaInfo = data.getParcelableExtra("media"); // get data from intent

//                Uri uri = mediaInfo.getUri();
                LogUtil.i(mediaInfo.getName());
//                LogUtil.i(uri.toString());

                String path = mediaInfo.getPath(); // 读取文件在内存中路径
                LogUtil.i(path);

                SharedPreferences.Editor editor = sharedPreferences.edit(); // 申请编辑器
                editor.putString("musicPath", path); // save content to sharedpreferences
                editor.commit(); // commit change
            }
        }
    }

    private void startAddDeviceAdmin(){
        if (!devicePolicyManager.isAdminActive(mDeviceAdminComponent)) { // 判断当前具备管理权限的组件是否在激活状态
            // 创建一个Intent
            Intent in = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            // 我要激活谁 ComponentName：标志一个特殊的组件
            in.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminComponent);
            in.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "使能我，可以启动一键锁屏人生");
//            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LogUtil.i("SettingActivity is going to startActivity");
            startActivity(in);
        }else {
            Toast.makeText(SettingActivity.this, "已经添加为设备管理器",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("SettingActivity onResume...");
        // 判断 查询归宿地服务 是否启动
        boolean isRunning = ServiceUtils.isServiceRunning(SettingActivity.this,
                "com.mobilesafe.service.AddressService");
        sivOpenPhoneAddressService.setChecked(isRunning);

        // 判断 查询黑名单拦截服务 是否启动
        boolean isCallSmsServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this,
                "com.mobilesafe.service.CallSmsSafeService");
        sivCallSmsSafe.setChecked(isCallSmsServiceRunning); // 恢复上一次状态

        // 判断 看门狗服务 是否启动
        boolean isWatchDogServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this,
                "com.mobilesafe.service.WatchDogService");
        sivWatchDog.setChecked(isWatchDogServiceRunning); // 恢复上一次状态
    }
}

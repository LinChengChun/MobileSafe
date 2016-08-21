package com.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mobilesafe.R;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cbSecureMode; // 用于监控是否打开监控模式
    private Boolean isOpenSafeMode = false;

    @Override
    protected int initLayout() {
        return R.layout.activity_setup4;
    }

    @Override
    protected void initView() {
        cbSecureMode = retrieveView(R.id.cb_secure); // 获取CheckBox实例
        isOpenSafeMode = sp.getBoolean("isOpenSafeMode", false);

        cbSecureMode.setChecked(isOpenSafeMode);
        if (isOpenSafeMode){
            cbSecureMode.setText("您已经开启防盗保护");
        }else {
            cbSecureMode.setText("您没有开启防盗保护");
        }

        cbSecureMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.i("isChecked = "+isChecked);
                SharedPreferences.Editor editor = sp.edit();
                // 假如已经选择，那么应该保存到 SharedPreference
                editor.putBoolean("isOpenSafeMode", isChecked);// save configed to SharedPreference
                editor.commit();

                if (isChecked){
                    cbSecureMode.setText("您已经开启防盗保护");
                }else {
                    cbSecureMode.setText("您没有开启防盗保护");
                }
            }
        });

    }

    public void success(View view) {
//        Intent i = new Intent(this, Setup4Activity.class);
//        startActivity(i);

        isOpenSafeMode = sp.getBoolean("isOpenSafeMode", false);
        if (!isOpenSafeMode){
            PromptManager.showShortToast(this, "请开启防盗保护");
            return;
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configed", true);// save configed to SharedPreference
        editor.commit();// commit change
        PromptManager.showShortToast(this, "设置完成");
        finish();
    }

    public void prev(View view) {
        Intent i = new Intent(this, Setup3Activity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }

    @Override
    public void showNext() {

    }

    @Override
    public void showPrev() {prev(null);
    }
}

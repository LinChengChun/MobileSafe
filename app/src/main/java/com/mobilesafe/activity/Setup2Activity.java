package com.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.mobilesafe.R;
import com.mobilesafe.ui.SettingItemView;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Setup2Activity extends BaseSetupActivity {

    @BindView(R.id.btn_prev)
    Button btnPrev;
    @BindView(R.id.btn_next)
    Button btnNext;
    private SettingItemView sivBundledSim;

    private TelephonyManager telephonyManager = null; // 声明一个电话管理器

    @Override
    protected int initLayout() {
        return R.layout.activity_setup2;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        sivBundledSim = retrieveView(R.id.siv_bundled_sim);
    }

    @Override
    protected void initListener() {
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);// 获取系统服务

        boolean status = sp.getBoolean("isBundledSim", false);// 获取sim卡是否绑定状态
        if (status){
            sivBundledSim.setChecked(true);//
        }else {
            sivBundledSim.setChecked(false);// 默认 sim卡不绑定
        }

        // 为sivBundledSim设置监听器，显示切换CheckBox状态
        sivBundledSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();// 获取编辑器

                if (sivBundledSim.isChecked()){// 假如已经被选择，则取消选项
                    sivBundledSim.setChecked(false);
                    editor.putString("sim", null);
                }else {
                    sivBundledSim.setChecked(true);
                    //得到SiM卡的序列号
                    String sim = telephonyManager.getSimSerialNumber();
                    LogUtil.i(sim);
                    editor.putString("sim", sim);
                }
                editor.putBoolean("isBundledSim", sivBundledSim.isChecked());
                editor.commit(); // commit change
            }
        });
    }

    public void next(View view) {
        String sim = sp.getString("sim", null);
        if (TextUtils.isEmpty(sim)){// sim卡没有绑定
            PromptManager.showShortToast(getApplicationContext(), "请绑定当前SIM卡");
            return;
        }

        Intent i = new Intent(this, Setup3Activity.class);
        startActivity(i);
        finish();
        // 此方法在finish和startActivity后调用
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    public void prev(View view) {
        Intent i = new Intent(this, Setup1Activity.class);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.tran_prev_in, R.anim.tran_prev_out);
    }
    @Override
    public void showNext() {
        next(null);
    }

    @Override
    public void showPrev() {prev(null);
    }
}

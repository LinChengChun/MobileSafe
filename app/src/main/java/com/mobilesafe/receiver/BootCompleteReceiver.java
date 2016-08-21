package com.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mobilesafe.R;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

/**
 * Created by Administrator on 2016/7/7.
 */
public class BootCompleteReceiver extends BroadcastReceiver{

    private SharedPreferences sp = null;
    private TelephonyManager manager = null;
    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.d("BootCompleteReceiver ---> onReceive");

        switch (intent.getAction()){
            case Intent.ACTION_BOOT_COMPLETED:
                sp = context.getSharedPreferences(context.getResources().getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE); // 获取SharedPreferences
                manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String oldSim = sp.getString("sim", null);// 获取已保存的sim卡序列号
                if (!TextUtils.isEmpty(oldSim)){ // 已经绑定
                    String currentSim = manager.getSimSerialNumber();// 获取当前Sim卡序列号

                    if (currentSim.equals(oldSim)){
                        LogUtil.d("Sim卡绑定正常，可以使用");
                        PromptManager.showLongToast(context, "Sim卡绑定正常，可以使用");
                    }else {
                        LogUtil.d("Sim卡异常，抱歉无法使用本手机");
                        PromptManager.showLongToast(context, "Sim卡异常，抱歉无法使用本手机");
                    }
                }
                break;
            default:break;
        }
    }
}

package com.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobilesafe.db.dao.NumberAddressQueryUtils;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

/**
 * Created by Administrator on 2016/8/17.
 */
public class OutCallReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("开始拨打电话");
        String outPhone = getResultData(); // 拿到拨出去的电话号码
        // 查询数据库
        String address = NumberAddressQueryUtils.queryNumber(outPhone); // 进行地址查询
        PromptManager.showLongToast(context, address); // 弹出提示

    }
}

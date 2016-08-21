package com.mobilesafe.activity;

import android.content.Intent;
import android.view.View;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;

/**
 * Created by Administrator on 2016/8/6.
 */
public class AtoolsActivity extends BaseActivity{
    @Override
    protected int initLayout() {
        return R.layout.activity_atools;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 进入号码归属地查询页面
     * @param view
     */
    public void startNumberAddressQuery(View view) {
        Intent intent = new Intent(AtoolsActivity.this, NumberAddressQueryActivity.class);
        startActivity(intent);
    }
}

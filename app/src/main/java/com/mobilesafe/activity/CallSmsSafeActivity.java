package com.mobilesafe.activity;

import android.widget.ListView;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/23.
 */
public class CallSmsSafeActivity extends BaseActivity {

    @BindView(R.id.lv_callsms_safe)
    ListView lvCallSmsSafe;

    @Override
    protected int initLayout() {
        return R.layout.activity_call_sms_safe;
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
    }
}

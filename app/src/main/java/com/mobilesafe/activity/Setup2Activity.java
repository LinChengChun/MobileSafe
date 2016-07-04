package com.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Setup2Activity extends BaseActivity {

    @BindView(R.id.btn_prev)
    Button btnPrev;
    @BindView(R.id.btn_next)
    Button btnNext;

    @Override
    protected int initLayout() {
        return R.layout.activity_setup2;
    }

    @Override
    protected void initView() {

    }

    public void next(View view) {
        Intent i = new Intent(this, Setup3Activity.class);
        startActivity(i);
        finish();
    }

    public void prev(View view) {
        Intent i = new Intent(this, Setup1Activity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

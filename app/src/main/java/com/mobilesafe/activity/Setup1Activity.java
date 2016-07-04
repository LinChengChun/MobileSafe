package com.mobilesafe.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Setup1Activity extends BaseActivity {

    @BindView(R.id.btn_next1)
    Button btnNext1;

    @Override
    protected int initLayout() {
        return R.layout.activity_setup1;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
    }

    public void next(View view){
        Intent i = new Intent(this, Setup2Activity.class);
        startActivity(i);
        finish();
    }

}

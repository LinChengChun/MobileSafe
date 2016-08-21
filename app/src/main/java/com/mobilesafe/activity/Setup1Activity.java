package com.mobilesafe.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Setup1Activity extends BaseSetupActivity {

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

    @Override
    public void showNext() {
        next(null);
    }

    @Override
    public void showPrev() {
    }

    public void next(View view){
        Intent i = new Intent(this, Setup2Activity.class);
        startActivity(i);
        finish();
        // 此方法在finish和startActivity后调用
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

}

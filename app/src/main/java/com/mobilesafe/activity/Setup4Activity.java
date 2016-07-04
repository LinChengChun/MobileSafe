package com.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.utils.PromptManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class Setup4Activity extends BaseActivity {

    @BindView(R.id.btn_prev)
    Button btnPrev;
    @BindView(R.id.btn_success)
    Button btnSuccess;

    private SharedPreferences sp = null;

    @Override
    protected int initLayout() {
        return R.layout.activity_setup4;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public void success(View view) {
//        Intent i = new Intent(this, Setup4Activity.class);
//        startActivity(i);
//
        sp = getSharedPreferences(getResources().getString(R.string.config), Context.MODE_PRIVATE);
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
    }
}

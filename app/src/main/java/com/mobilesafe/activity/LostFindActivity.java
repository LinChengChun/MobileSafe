package com.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class LostFindActivity extends Activity {

    @BindView(R.id.restart_navigation)
    TextView restartNavigation;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences(getResources().getString(R.string.config), Context.MODE_PRIVATE);
        boolean configed = sp.getBoolean("configed", false);
        // 判断一下是否做过设置向导页面
        if (configed) { // 已经做过向导
            setContentView(R.layout.activity_lost_find);
        } else {
            // 还没有做过向导
            Intent i = new Intent(LostFindActivity.this, Setup1Activity.class);
            startActivity(i);
            finish();
            // 关闭当前页面
        }

        ButterKnife.bind(this);
    }

    public void restart_navigation(View view){
        Intent i = new Intent(LostFindActivity.this, Setup1Activity.class);
        startActivity(i);
        finish();
    }
}

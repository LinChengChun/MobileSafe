package com.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class LostFindActivity extends Activity {

    @BindView(R.id.restart_navigation)
    TextView restartNavigation; // 用于重新进入设置引导界面
    @BindView(R.id.tv_safe_num)
    TextView tvSafeNum; // 用于显示安全密码的控件
    @BindView(R.id.iv_lock)
    ImageView ivLock; // 用于显示是否上锁的控件

    private SharedPreferences sp; // 用于存储配置信息
    private String safeNum = null;
    private boolean isOpenSafeMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE); // 根据名称检索sharedPreferences
        boolean configed = sp.getBoolean("configed", false);
        // 判断一下是否做过设置向导页面
        if (configed) { // 已经做过向导
            setContentView(R.layout.activity_lost_find);

            ButterKnife.bind(this);

            safeNum = sp.getString("safeNum", null); // 从配置文件中读取是否有设置安全密码
            if ( null == safeNum){
                tvSafeNum.setText("110");
            }else {
                tvSafeNum.setText(safeNum);
            }

            isOpenSafeMode = sp.getBoolean("isOpenSafeMode", false);
            if (isOpenSafeMode){
                ivLock.setImageDrawable(getResources().getDrawable(R.drawable.lock));
            }else {
                ivLock.setImageDrawable(getResources().getDrawable(R.drawable.unlock));
            }

        } else {
            // 还没有做过向导
            Intent i = new Intent(LostFindActivity.this, Setup1Activity.class);
            startActivity(i);
            finish();
            // 关闭当前页面
        }


    }

    /**
     * 重新进入设置引导界面
     * @param view
     */
    public void restart_navigation(View view) {
        Intent i = new Intent(LostFindActivity.this, Setup1Activity.class);
        startActivity(i);
        finish();
    }
}

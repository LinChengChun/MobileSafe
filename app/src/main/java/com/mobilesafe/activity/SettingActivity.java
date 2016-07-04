package com.mobilesafe.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.ui.SettingItemView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/4.
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.siv_update)
    SettingItemView sivUpdate;
    private SharedPreferences sharedPreferences; // 定义一个私有共享属性

    @Override
    protected int initLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.config), Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(getResources().getString(R.string.update), false)){// 设置默认状态
            sivUpdate.setChecked(true);
        }else {
            sivUpdate.setChecked(false);
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initListener() {
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 检测是否已经CheckBox状态被选择
                SharedPreferences.Editor editor = sharedPreferences.edit(); // 申请编辑器
                if (sivUpdate.isChecked()){// 假如已经被选择，则取消选项
                    sivUpdate.setChecked(false);
                }else {
                    sivUpdate.setChecked(true);
                }
                editor.putBoolean(getResources().getString(R.string.update), sivUpdate.isChecked());
                editor.commit(); // commit change
            }
        });
    }


}

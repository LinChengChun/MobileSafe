package com.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.utils.LogUtil;

/**
 * Created by Administrator on 2016/7/4.
 */
public class SettingItemView extends RelativeLayout {

    private View rootView = null; // 定义一个View
    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbStatus; // 定义一个CheckBox
    private String title;
    private String desc_on;
    private String desc_off;

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.mobilesafe", "titles");// 获取自定义属性值
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.mobilesafe", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.mobilesafe", "desc_off");
        LogUtil.i(title+desc_on+desc_off);
        tvTitle.setText(title);// 设置标题
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        // 根据layout文件，创建一个View，并添加到父类中，其父类是SettingItemView
        rootView = LayoutInflater.from(context).inflate(R.layout.item_setting_layout, this, true);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        tvDesc = (TextView) rootView.findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) rootView.findViewById(R.id.cb_status);
    }

    /**
     * 检测是否选择自动更新
     */
    public boolean isChecked(){
        return cbStatus.isChecked();
    }

    /**
     * 设置 CheckBox 状态
     * @param status
     */
    public void setChecked(boolean status){
        if (status){
            this.setTvDesc(desc_on);//自动更新已经打开
        }else {
            this.setTvDesc(desc_off);//自动更新已经关闭
        }
        cbStatus.setChecked(status);
    }

    /**
     * 设置提示信息
     * @param msg
     */
    public void setTvDesc(String msg){
        tvDesc.setText(msg);
    }
}

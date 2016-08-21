package com.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.utils.LogUtil;

/**
 * Created by Administrator on 2016/7/4.
 *
 * 1、自定义类继承ViewGroup
 * 2、实现父类构造方法，根据布局，构造自定义View，并把子View添加到本父类中
 * 3、自定义开发API，为外部提供操作方法
 *
 * 4、自定义控件属性
 * 5、自定义命名空间    xmlns:Lin
 * 6、自定义性属类型，属性类型声明   attrs.xml
 * 7、使用自定义属性进行配置
 * 8、在构造方法中通过AttributeSet 读取属性值
 */
public class SettingClickView extends RelativeLayout {

    private View rootView = null; // 定义一个View
    private TextView tvTitle;
    private TextView tvDesc;
    private String title;
    private String desc_on;
    private String desc_off;

    public SettingClickView(Context context) {
        super(context);
        initView(context);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.mobilesafe", "titles");// 获取自定义属性值
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.mobilesafe", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.mobilesafe", "desc_off");
        LogUtil.i(title+desc_on+desc_off);
        tvTitle.setText(title);// 设置标题
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        // 根据layout文件，创建一个View，并添加到父类中，其父类是SettingItemView
        rootView = LayoutInflater.from(context).inflate(R.layout.setting_click_view, this, true);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        tvDesc = (TextView) rootView.findViewById(R.id.tv_desc);

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
    }

    /**
     * 设置提示信息
     * @param msg
     */
    public void setTvDesc(String msg){
        tvDesc.setText(msg);
    }

    /**
     * 设置提示信息
     * @param msg
     */
    public void setTvTitle(String msg) {
        tvTitle.setText(msg);
    }
}

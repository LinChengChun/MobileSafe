package com.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.mobilesafe.utils.LogUtil;

/**
 * Created by cclin on 2016/9/22.
 */
public class ClickScrollView extends ScrollView{


    public ClickScrollView(Context context) {
        super(context);
    }

    public ClickScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        LogUtil.d("ClickScrollView onInterceptTouchEvent result = "+result);
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        LogUtil.d("ClickScrollView dispatchTouchEvent result = "+result);
        return result;
    }
}

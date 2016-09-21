package com.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.mobilesafe.utils.LogUtil;

/**
 * Created by cclin on 2016/9/22.
 */
public class ClickLinearLayout extends LinearLayout {
    public ClickLinearLayout(Context context) {
        super(context);
    }

    public ClickLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        LogUtil.d("ClickLinearLayout onInterceptTouchEvent result = "+result);
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        LogUtil.d("ClickLinearLayout dispatchTouchEvent result = "+result);
        return result;
    }
}

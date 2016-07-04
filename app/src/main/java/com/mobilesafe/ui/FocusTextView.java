package com.mobilesafe.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/7/4.
 */
public class FocusTextView extends TextView{

    public FocusTextView(Context context) {
        super(context);
        init();
    }

    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.setSingleLine();
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    @Override
    public boolean isFocused() {
        return true;// 默认返回已经聚焦
    }
}

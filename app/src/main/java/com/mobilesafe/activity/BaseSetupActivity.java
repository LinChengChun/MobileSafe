package com.mobilesafe.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

/**
 * Created by Administrator on 2016/7/5.
 */
public abstract class BaseSetupActivity extends BaseActivity {

    // 1、声明一个手势识别器
    private GestureDetector gestureDetector = null;

    // 声明一个SharedPreferencees
    protected SharedPreferences sp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sp = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE); // 获取SharedPreferences
        super.onCreate(savedInstanceState);

        // 2、实例化这个手势识别器
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                float oldX = e1.getRawX();
                float oldY = e1.getRawY();
                float newX = e2.getRawX();
                float newY = e2.getRawY();

                LogUtil.i("oldX = "+oldX+";oldY = "+oldY+";newX = "+newX+";newY = "+newY);
                LogUtil.i("get method: oldX = "+e1.getX()+";oldY = "+e1.getY()+";newX = "+e2.getX()+";newY = "+e2.getY());

                if (Math.abs(velocityX)<200){ // 屏蔽在x滑动很慢的情形
                    PromptManager.showShortToast(getApplicationContext(), "帅哥，滑动的太慢了");
                    return true;
                }

                if (Math.abs(newY-oldY)>300){ // 屏蔽斜边滑动情形
                    PromptManager.showShortToast(getApplicationContext(), "帅哥，不能这样滑");
                    return true;
                }

                if (newX-oldX > 200){
                    PromptManager.showShortToast(getApplicationContext(), "从左往右滑，上一步");
                    showPrev(); // 跳到上一步
                }else if (oldX-newX > 200){
                    PromptManager.showShortToast(getApplicationContext(), "从右往左滑，下一步");
                    showNext();// 跳到下一步
                }
                return false;
            }
        });
    }

    public abstract void showNext();

    public abstract void showPrev();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event); // 把activity的事件，传递到手势识别器
        return super.onTouchEvent(event);
    }
}

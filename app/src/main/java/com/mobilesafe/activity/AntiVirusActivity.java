package com.mobilesafe.activity;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cclin on 2016/9/13.
 */
public class AntiVirusActivity extends BaseActivity {

    @BindView(R.id.iv_scan)
    ImageView ivScan;

    @BindView(R.id.pb_scan)
    ProgressBar pbScan;

    @Override
    protected int initLayout() {
        return R.layout.activity_anti_virus;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE); // 持续旋转
        ivScan.startAnimation(rotateAnimation);

        pbScan.setMax(100);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<=100; i++){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pbScan.setProgress(i);
                }
            }
        }).start();
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
    }
}

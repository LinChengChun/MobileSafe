package com.mobilesafe.activity;

import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.db.dao.NumberAddressQueryUtils;
import com.mobilesafe.utils.LogUtil;

/**
 * 号码地址查询页面
 * Created by Administrator on 2016/8/6.
 */
public class NumberAddressQueryActivity extends BaseActivity{

    private EditText etNumberAddressQuery; // 号码查询输入框
    private Button btnNumberAddressQuery;
    private TextView tvResult; // 用于显示查询结果

    /**
     * 系统提供的振动服务
     */
    private Vibrator vibrator;

    @Override
    protected int initLayout() {
        return R.layout.activity_number_address_query;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        etNumberAddressQuery = retrieveView(R.id.et_NumberAddressQuery);
        tvResult = retrieveView(R.id.tv_Result);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); // 获取系统权限
    }

    @Override
    protected void initListener() {
        etNumberAddressQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                LogUtil.d("beforeTextChanged: "+s.toString());
            }

            /**
             * 当文本发生变化的时候回调
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && s.length()>=3){
                    String address = NumberAddressQueryUtils.queryNumber(s.toString());
                    tvResult.setText(address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogUtil.d("afterTextChanged: "+s.toString());
            }
        });
    }

    /**
     * 查询号码归属地
     * @param view
     */
    public void numberAddressQuery(View view) {
        String num = etNumberAddressQuery.getText().toString().trim(); // 去掉空格
        if (TextUtils.isEmpty(num)){
            LogUtil.i("查询的号码为空请重新输入");
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//            shake.setInterpolator(new Interpolator() {
//                @Override
//                public float getInterpolation(float input) {
//                    return input+input;
//                }
//            });
            etNumberAddressQuery.startAnimation(shake);

            //当电话号码为空的时候，就去振动手机提醒用户
//            vibrator.vibrate(2000);
            long[] pattern = {2000,2000,3000,3000,1000,2000};
            //-1不重复 0循环振动 1从数组坐标1开始
            vibrator.vibrate(pattern, -1);

        }else {
            LogUtil.i("您要查询的号码为："+num);
            // 去数据库查询号码归属地
            // 1.网络查询；2.本地查询
            String location = NumberAddressQueryUtils.queryNumber(num); // 查询电话号码归属地
            tvResult.setText(location);
        }
    }
}

package com.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mobilesafe.R;
import com.mobilesafe.db.dao.NumberAddressQueryUtils;
import com.mobilesafe.utils.LogUtil;

/**
 * Created by Administrator on 2016/8/15.
 */
public class AddressService extends Service{

    /**
     * 监听来电
     */
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    private OutCallReceiver mOutCallReceiver;
    private WindowManager windowManager;
    private View view;
    private WindowManager.LayoutParams params; // 成员变量用来定义布局参数的

    // Toast背景，用来控制电话位置查询吐司风格
    private int[] idDrawables = {R.drawable.call_locate_white, R.drawable.call_locate_orange,
            R.drawable.call_locate_blue, R.drawable.call_locate_gray,R.drawable.call_locate_green};
    private SharedPreferences sharedPreferences; // 定义一个私有共享属性

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public AddressService() {
        LogUtil.d("AddressService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("AddressService: onCreate");
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // 监听来电
        myPhoneStateListener = new MyPhoneStateListener(); // 实例化服务
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // 用代码去注册广播接收者
        mOutCallReceiver = new OutCallReceiver();// 实例化广播接收者
        IntentFilter intentFilter = new IntentFilter(); // 意图过滤器
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL"); // 添加指定动作
        registerReceiver(mOutCallReceiver, intentFilter); // 注册广播接收者

        // 获取窗口管理器
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("AddressService: onDestroy");
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);// 停止监听
        myPhoneStateListener = null;

        // 用代码注销广播接收者
        unregisterReceiver(mOutCallReceiver);// 注销广播接收者
        mOutCallReceiver = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    // 定义一个内部广播接收器
    class OutCallReceiver extends BroadcastReceiver {

        public OutCallReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d("开始拨打电话");
            // 1、拿到拨出去的电话号码
            String outPhone = getResultData();
            // 2、查询数据库
            String address = NumberAddressQueryUtils.queryNumber(outPhone); // 进行地址查询
            // 3、弹出提示
//            PromptManager.showLongToast(context, address);
            showToast(context, address); // 自定义吐司
        }
    }


    /**
     * 自定义吐司显示
     * @param context
     * @param address
     */
    public void showToast(Context context, CharSequence address){
        view = View.inflate(context, R.layout.address_show, null);
        TextView textView = (TextView) view.findViewById(R.id.tv_address);

        // 给view 设置触摸监听器
        view.setOnTouchListener(new View.OnTouchListener() {
            // 定义手指的初始化位置
            int startX;
            int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN: // 手指按下控件
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        LogUtil.d("开始的位置：startX = "+startX+";startY = "+startY);
                        break;
                    case MotionEvent.ACTION_UP: // 手指离开控件
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("positionX", params.x);
                        editor.putInt("positionY", params.y);
                        editor.commit();// 记录上一次提示框位置
                        break;
                    case MotionEvent.ACTION_MOVE: // 手指在屏幕上移动
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        LogUtil.d("结束的位置：newX = "+newX+";newY = "+newY);
                        int defX = newX-startX;
                        int defY = newY-startY;
                        LogUtil.d("手指的偏移量：defX = "+defX+";defY = "+defY);
                        params.x += defX;
                        params.y += defY;
                        if (params.x < 0)
                            params.x = 0;
                        if (params.y < 0)
                            params.y = 0;
                        if (params.x > windowManager.getDefaultDisplay().getWidth()-view.getWidth())
                            params.x = windowManager.getDefaultDisplay().getWidth()-view.getWidth();
                        if (params.y > windowManager.getDefaultDisplay().getHeight()-view.getHeight())
                            params.y = windowManager.getDefaultDisplay().getHeight()-view.getHeight();

                        windowManager.updateViewLayout(view, params);
                        startX = (int) event.getRawX();// 更新起始位置
                        startY = (int) event.getRawY();
                        break;
                }
                return true;// 事件处理完毕了。不要让父控件布局相应处理事件
            }
        });
        LogUtil.d("弹出吐司");
        int which = sharedPreferences.getInt("addressDialogStyle", 0); // 从sharedPreferences中读取背景id
        view.setBackgroundResource(idDrawables[which]); // 根据设置的风格，配置到实际吐司显示中
        textView.setText(address);

        params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;// 包裹类型
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;

        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.gravity = Gravity.TOP+Gravity.LEFT;
        params.x = sharedPreferences.getInt("positionX", 0);
        params.y = sharedPreferences.getInt("positionY", 0);
        // android系统里面具有电话优先级的一种窗体类型，记得添加权限
//        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE; // 可设置电话优先级的类型
        windowManager.addView(view, params);
    }

    /**
     * 自定义PhoneStateListener对电话呼叫状态进行监听
     */
    class MyPhoneStateListener extends PhoneStateListener{

        public MyPhoneStateListener() {
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
        }

        /**
         * 第一个参数是状态，第二个参数是来电
         * @param state
         * @param incomingNumber
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state){
                case TelephonyManager.CALL_STATE_RINGING: // 电话铃声响起的时候，来电时
                    String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
                    LogUtil.d("onCallStateChanged: "+address);
//                    PromptManager.showShortToast(getApplicationContext(), address);
                    showToast(getApplicationContext(), address); // 自定义吐司
                    break;
                case TelephonyManager.CALL_STATE_IDLE: // 电话为闲置状态
                    LogUtil.d("onCallStateChanged: 电话为闲置状态");
                    if (null != view){
                        windowManager.removeView(view);
                    }
                    break;
                default:break;
            }
        }
    }
}

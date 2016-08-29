package com.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.mobilesafe.activity.CallSmsSafeActivity;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.db.dao.BlackNumberDao;
import com.mobilesafe.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 黑名单拦截服务
 * Created by Administrator on 2016/8/27.
 */
public class CallSmsSafeService extends Service {

    private InnerSmsReceiver receiver; // declare
    private BlackNumberDao dao; // 数据库操作管理类对象
    private TelephonyManager telephonyManager; // 电话管理器
    private MyListener listener; // 声明一个电话状态监听器

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("CallSmsSafeService: onCreate");

        receiver = new InnerSmsReceiver(); // 实例化广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(99999999); // 设置广播优先级
        registerReceiver(receiver, filter); // 注册广播
        dao = BaseActivity.mBlackNumberDao;  // 实例化数据库业务类

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE); // 获取电话管理器服务
        listener = new MyListener(); // 实例化监听器
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("CallSmsSafeService: onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("CallSmsSafeService: onDestroy");
        if (null != receiver) // 注销广播接收者
            unregisterReceiver(receiver);
    }

    private class InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d("内部广播接收者，短信到来了");
            // 检查发件人是否是黑名单号码，设置短信拦截 全部拦截

            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object:objects){
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object); // 获取每一个短信对象
                String sender = message.getOriginatingAddress(); // 获取发送者
                String result = dao.findMode(sender.substring(3)); // 查询该电话号码拦截方式

                LogUtil.d("CallSmsSafeService sender "+sender+";sender.substring: "+sender.substring(3));
                LogUtil.d("CallSmsSafeService result "+result);

                if("2".equals(result) || "3".equals(result)){
                    LogUtil.d("黑名单号码，拦截短信");
                    abortBroadcast();// 截止广播的发送
                }
            }
        }
    }

    private class MyListener extends PhoneStateListener{

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state){
                case TelephonyManager.CALL_STATE_RINGING: // 电话铃声响起的时候，来电时
                    String result = dao.findMode(incomingNumber); // 查询拦截模式
                    if ("1".equals(result) || "3".equals(result)){
                        LogUtil.d("该电话已经被设置为电话拦截，挂断电话");
                        getContentResolver().registerContentObserver(CallLog.CONTENT_URI, true, new CallLogObserver(incomingNumber, new Handler()));
                        endCall(); // 结束 通话 另外一个进程运行的远程服务方法，方法调用后，呼叫记录可能还没有生成
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE: // 电话闲置
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: //
                    break;
            }

            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 利用内容提供者删除通话记录
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        // 呼叫记录uri的路径
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number=?", new String[]{incomingNumber});

    }

    private class CallLogObserver extends ContentObserver{

        String incomingNumber;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public CallLogObserver(String incomingNumber, Handler handler) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            LogUtil.d("数据库内容发送变化，应该删除通话记录");
            deleteCallLog(incomingNumber); // 删除通话记录
            getContentResolver().unregisterContentObserver(this); // this代表当前自己，注销该观察者
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
        }
    }

    /**
     * 结束通话，挂断电话
     */
    public void endCall(){
        try {
            Class clazz = CallSmsSafeActivity.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(iBinder).endCall(); // IBinder强转换为ITelePhony对象
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}

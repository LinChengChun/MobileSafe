package com.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.mobilesafe.R;
import com.mobilesafe.service.GPSService;
import com.mobilesafe.utils.LogUtil;
import com.mobilesafe.utils.PromptManager;

import java.io.File;

/**
 * Created by Administrator on 2016/7/21.
 */
public class SMSReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE);
        LogUtil.i("SMSReceiver onReceive...");
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        String sender; // 发送者
        String body; // 短信内容
        String safeNum = sp.getString("safeNum", null); // 读取安全号码
        MediaPlayer mMediaPlayer = null;

        for (Object b:objects){
            SmsMessage sms = SmsMessage.createFromPdu((byte[])b);

            sender = sms.getOriginatingAddress();
            body = sms.getMessageBody();

            LogUtil.i("发送者："+sender);
            LogUtil.i("短信内容："+body);

            if (!TextUtils.isEmpty(safeNum) && sender.contains(safeNum)){ // 如果是安全号码发来指令，则执行。
                switch (body){
                    case "#*location*#":
                        LogUtil.i("GPS追踪");

                        // 启动服务
                        Intent i = new Intent(context, GPSService.class);
                        context.startService(i);
                        String lastlocation = sp.getString("lastlocation", null); // 从SharedPreferences中读取定位信息
                        LogUtil.i("lastlocation: "+lastlocation);

                        if (TextUtils.isEmpty(lastlocation)){
                            SmsManager.getDefault().sendTextMessage(sender, null, "getting location...", null, null);
                        }else {
                            SmsManager.getDefault().sendTextMessage(sender, null, lastlocation, null, null);
                        }

                        abortBroadcast();
                        break;
                    case "#*alarm*#": // 播放报警音乐
                        LogUtil.i("播放报警音乐");
                        Uri mediaUri = null;
                        String path = sp.getString("musicPath", null);
                        LogUtil.i("path: "+path);

                        if (!TextUtils.isEmpty(path)){
//                            mediaUri = Uri.parse(uriString);
                            mediaUri = Uri.fromFile(new File(path)); // 根据路径获取 该音频文件的本地uri
                        }
                        LogUtil.i("mediaUri: "+mediaUri.toString());
                        if (mediaUri!=null){
                            mMediaPlayer = MediaPlayer.create(context, mediaUri);
                            mMediaPlayer.setLooping(false);
                            mMediaPlayer.setVolume(1.0f, 1.0f);
                            mMediaPlayer.start();
                            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    if (mp!=null){
                                        mp.release(); // release MediaPlayer
                                    }
                                }
                            });
                            LogUtil.i("start to play media");
                        }

                        abortBroadcast();// 截止广播的发送
                        break;
                    case "#*wipedata*#":
                        LogUtil.i("远程数据销毁");
                        // nothing to be done,because it is dangers
                        abortBroadcast();
                        break;
                    case "#*lockscreen*#":
                        LogUtil.i("远程锁屏");
                        boolean isOpenDeviceAdmin = sp.getBoolean("isOpenDeviceAdmin", false);
                        if (isOpenDeviceAdmin){
                            LogUtil.i("SMSReceiver is going to lockNow");
                            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE); // 获取系统设备安全服务
                            devicePolicyManager.lockNow(); // 锁屏
                        }else {
                            PromptManager.showShortToast(context, "DevicePolicyManager is not enable");
                        }

                        abortBroadcast();
                        break;
                }
            }
        }
    }
}

package com.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mobilesafe.R;
import com.mobilesafe.utils.LogUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/2.
 */
public class GPSService extends Service{

    private final String TAG = "cclin";
    private LocationManager lm = null;// 用到的位置服务
    private MyLocationListener listener = null; // 监听器
    private SharedPreferences sp = null; // 存储

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("GPSService onCreate()...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("GPSService onStartCommand()...");
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // 获取系统定位服务

        listener = new MyLocationListener(); // 实例化自定义监听器
        // 给位置提供者设置条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String providerType = lm.getBestProvider(criteria, true);
        Log.i(TAG, "providerType = "+providerType);
        lm.requestLocationUpdates(providerType, 0, 0, listener);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        lm.removeUpdates(listener);// 取消监听
        listener = null;
    }

    private class MyLocationListener implements LocationListener {

        /**
         * 位置改变时调用
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            String longitude = "j:"+location.getLongitude()+"\n"; // 获得经度
            String latitude = "w:"+location.getLatitude()+"\n"; // 获得纬度
            String accuracy = "a:"+location.getAccuracy()+"\n"; // 获得精度

            Log.i(TAG, longitude+longitude+accuracy);

            // 把标准GPS坐标转换成火星坐标
            InputStream is;
            PointDouble dstPointDouble; // 转换结果的点
            try {
                is = getAssets().open("axisoffset.dat");
                ModifyOffset offset = ModifyOffset.getInstance(is);
                dstPointDouble = offset.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));
                longitude = "j:"+dstPointDouble.x+"\n";
                latitude = "w:"+dstPointDouble.y+"\n";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            sp = getSharedPreferences(getResources().getString(R.string.SharedPreferencesConfig), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit(); // 获取编辑器
            editor.putString("lastlocation", longitude+latitude+accuracy);
            editor.commit(); // commit change
        }

        /**
         * 状态改变时调用，开--关，关--开
         * @param s
         * @param i
         * @param bundle
         */
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i(TAG, "onStatusChanged "+s);
        }

        /**
         * 使能时调用
         * @param s
         */
        @Override
        public void onProviderEnabled(String s) {
            Log.i(TAG, "onProviderEnabled "+s);
        }

        /**
         * 关闭时调用
         * @param s
         */
        @Override
        public void onProviderDisabled(String s) {
            Log.i(TAG, "onProviderDisabled "+s);
        }
    }
}

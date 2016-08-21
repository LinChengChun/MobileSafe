package colin.gpsdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "cclin";
    private LocationManager lm = null;// 用到的位置服务
    private MyLocationListener listener = null; // 监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请READ_CONTACTS权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        List<String> provider = lm.getAllProviders();
        for (String temp: provider)
            Log.i(TAG, temp);


//        listener = new MyLocationListener();
//        // 给位置提供者设置条件
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//
//        String providerType = lm.getBestProvider(criteria, true);
//        Log.i(TAG, "providerType = "+providerType);
//        lm.requestLocationUpdates(providerType, 0, 0, listener);
    }

    public void stopService(View view) {
        Intent i = new Intent(MainActivity.this, GPSService.class);
        stopService(i);
    }

    public void startService(View view) {
        Intent i = new Intent(MainActivity.this, GPSService.class);
        startService(i);
    }

    private class MyLocationListener implements LocationListener {

        /**
         * 位置改变时调用
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            String longitude = ""+location.getLongitude(); // 获得经度
            String latitude = ""+location.getLatitude(); // 获得纬度
            String accuracy = ""+location.getAccuracy(); // 获得精度
//            Log.i(TAG, longitude+longitude+accuracy);
            TextView textView = new TextView(MainActivity.this);
            textView.setText(longitude+"\n"+latitude+"\n"+accuracy);
            setContentView(textView);
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




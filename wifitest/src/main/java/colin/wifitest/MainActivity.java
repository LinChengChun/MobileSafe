package colin.wifitest;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "cclin";
    private Menu mMenu; // 全局菜单选项
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mMenu.removeItem(103);
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 101, 1, "语文");
        menu.add(Menu.NONE, 102, 2, "数学");
        menu.add(Menu.NONE, 103, 3, "英语");
        menu.add(Menu.NONE, 104, 4, "历史");
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, ""+item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

//        Log.i("cclin", wifiInfo.getLinkSpeed()+"Mbps");

        String ssid = null;
        String bssid = null;
        String ipaddr = null;
        String wifiName = null;
//        int encrypt = NetInfo.NoPass;
        String passwd = null;
        boolean hidden = false;
//        System.out.println(ipaddr.length());
        WifiInfo winfo = wifiManager.getConnectionInfo();
        if (winfo != null) {
            ssid = winfo.getSSID();
            bssid = winfo.getBSSID();
            wifiName = ssid;
            if (wifiName == null)
                wifiName = bssid;
            if (wifiName != null && wifiName.length() > 0) {
                int myIp = winfo.getIpAddress();
//                if (myIp != 0)
//                    ipaddr = Utils.getIpString(myIp);
            }
            int nid = winfo.getNetworkId();
            Log.d(TAG, "networkId = " + nid);
            WifiConfiguration wc = null;
            List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
            if (existingConfigs != null)
                for (WifiConfiguration existingConfig : existingConfigs) {
                    Log.d(TAG, "netId = " + existingConfig.networkId);
                    if (existingConfig.networkId == nid) {
                        wc = existingConfig;
                        break;
                    }
                }
            if (wc != null) {
                hidden = wc.hiddenSSID;
                Log.d(TAG, "PSK=" + wc.preSharedKey + ", wepKey0="+ wc.wepKeys[0]);
                // if(wc.allowedAuthAlgorithms.get(WifiConfiguration.AuthAlgorithm.OPEN))
                // {
                if (wc.preSharedKey != null) {
//                    encrypt = NetInfo.WPA;
                    passwd = wc.preSharedKey;
                }
                // else
                // if(wc.allowedAuthAlgorithms.get(WifiConfiguration.AuthAlgorithm.SHARED))
                // {
                else if (wc.wepKeys != null && wc.wepKeys[0] != null) {
//                    encrypt = NetInfo.WEP;
                    passwd = wc.wepKeys[0];
                }
                if (wc.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)
                        || passwd == null) {
//                    encrypt = NetInfo.NoPass;
                }
                Log.d(TAG, "wifi ap ssid=" + ssid + ", pass=" + passwd);
            }
        }
    }

    public void updateOnUiThread(View view) {
        MenuItem item = mMenu.findItem(102); // find item by id
        if (item.isVisible()){
            item.setVisible(false);
        }else {
            item.setVisible(true);
        }
    }

    public void updateOnWorkThread(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MenuItem item = mMenu.findItem(101);
                        if (item.isVisible()){
                            item.setVisible(false);
                        }else {
                            item.setVisible(true);
                        }

                    }
                }, 0);
//                mHandler.sendMessage(mHandler.obtainMessage(0));
                mMenu.removeItem(103);
            }
        }).start();
    }
}

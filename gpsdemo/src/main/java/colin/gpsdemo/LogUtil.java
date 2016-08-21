package colin.gpsdemo;

import android.util.Log;

/**
 * Created by Administrator on 2016/7/2.
 */
public class LogUtil {
    private static String TAG = "cclin";
    private static boolean isDebug = true;

    public static void i(String msg){
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg){
        if (isDebug)
            Log.d(TAG, msg);
    }
}

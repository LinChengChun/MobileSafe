package colin.wifitest;

import android.app.Application;
import android.os.Handler;

/**
 * 自定义Application
 */
public class BaseApplication extends Application {

    private static BaseApplication mApplication;
    // 保存主线程ID
    private static int mainTid;
    // 用于处理主线程消息
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mainTid = android.os.Process.myTid();
        mHandler = new Handler();

        // 初始化异常捕获
        CatchExceptionHandler.getInstance().init(getApplicationContext());
    }

    public static BaseApplication getApplication() {
        return mApplication;
    }

    public static int getMainTid() {
        return mainTid;
    }

    public static Handler getHandler() {
        return mHandler;
    }

}

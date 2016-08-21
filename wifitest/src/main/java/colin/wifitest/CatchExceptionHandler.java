package colin.wifitest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 异常处理
 */
public class CatchExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CatchExceptionHandler INSTANCE = new CatchExceptionHandler(); // 饿汉式单例模式
    private Context mContext = null;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CatchExceptionHandler() {

    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CatchExceptionHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handlerException(ex) && mDefaultHandler != null){
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }else {
            String err = "[" + ex.getMessage() + "]";

            showExceptionToast("程序异常导致重启"+err); // 新创建一个线程显示Toast提示框
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE); // 获取系统闹钟服务
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("crash", true);
            PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 200, restartIntent); // 1秒钟后重启应用

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            System.gc();
        }
    }

    /**
     * 自定义的异常捕获处理方法，错误处理，收集错误信息，发送错误报告等操作均在此完成
     * @param ex
     * @return
     */
    private boolean handlerException(Throwable ex){
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.close();
        String result = writer.toString();
        Log.e("uncaughtException", result);// 在日志表里打印出来
        writeError(result);

        // 发送错误报告到后台
        return true;
    }

    /**
     * 把日期转换为字符串作为log文件名
     * @return
     */
    public static String string2Date() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(new Date());
    }

    /**
     * 把异常信息打印到本地文件
     * @param errorMsg
     */
    private void writeError(String errorMsg) {
        String s = new String();
        String s1 = new String();
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator + string2Date() + ".log");
            if (file.exists()) {
            } else {
                file.createNewFile();// 不存在则创建
            }
            BufferedReader input = new BufferedReader(new FileReader(file));

            while ((s = input.readLine()) != null) {
                s1 += s + "\n";
            }
            input.close();
            s1 += errorMsg;

            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(s1);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showExceptionToast(final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

    }

}
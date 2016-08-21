package com.mobilesafe.exception;

import android.content.Context;
import android.util.Log;

import com.mobilesafe.utils.FileUtils;
import com.mobilesafe.utils.LogUtil;

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
    private static CatchExceptionHandler INSTANCE = new CatchExceptionHandler();

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
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.close();
        String result = writer.toString();
        Log.i("uncaughtException", result);// 在日志表里打印出来
//        if (Config.IS_DEBUG) {
            writeError(result);
//        }

        // 如果用户没有处理则让系统默认的异常处理器来处理
        mDefaultHandler.uncaughtException(thread, ex);
    }

    /**
     * 转换时间为字符串
     * @return
     */
    public static String string2Date() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(new Date());
    }

    private void writeError(String errorMsg) {
        String s = new String();
        String s1 = new String();
        try {
            File file = new File(FileUtils.getDebugDir() + string2Date() + ".log");
            if (file.exists()) {
            } else {
                file.createNewFile();// 不存在则创建
            }
            BufferedReader input = new BufferedReader(new FileReader(file));

            while ((s = input.readLine()) != null) {
                s1 += s + "\n";
                LogUtil.d(s);
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

}
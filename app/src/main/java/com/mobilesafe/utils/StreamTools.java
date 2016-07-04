package com.mobilesafe.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2016/7/2.
 */
public class StreamTools {

    /**
     * 从输入流中读取到内存输出流中。假如输入流中有回车，读取失败。不能正常转换为字符串
     * @param is
     * @return
     * @throws IOException
     */
    public static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buf = new byte[1024]; // buffer
        int len = 0;
        while ((len=bis.read(buf, 0, buf.length)) != -1){
            bos.write(buf, 0, len);
        }
        String result = new String(buf, "utf-8");
        LogUtil.i(result);
        bos.close();
        return result;
    }

    public static String readFromReader(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        String result = "";
        while ( (line = reader.readLine())!=null ){
            LogUtil.i(line);
            result += line;
        }
        LogUtil.i(result);
        reader.close();
        return result;
    }
}

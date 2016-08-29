package com.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import com.mobilesafe.bean.SmsInfomation;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 短信的工具类
 * Created by Administrator on 2016/8/28.
 */
public class SmsUtils {

    /**
     * 备份短信回调接口
     */
    public interface BackUpCallBack{
        /**
         * 开始备份时，设置进度条最大值
         * @param max 总长度
         */
        public void beforeBackup(int max);

        /**
         * 备份短信时，更新进度条
         * @param progress
         */
        public void onSmsBackup(int progress);
    }

    /**
     * 备份用户的短信；备注：抛出异常，让调用本方法的用户知道出现错误
     * @param context 上下文
     */
    public static void backupSms(Context context, BackUpCallBack backUpCallBack) throws IOException, InterruptedException {
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileOutputStream fos = new FileOutputStream(file);
        ContentResolver resolver = context.getContentResolver(); // 获取内容
        // 把用户的短信一条一条读出来，按照一定的格式写到文件中
        XmlSerializer xmlSerializer = Xml.newSerializer(); // 获取xml文件的生成器(序列化器)
        xmlSerializer.setOutput(fos, "utf-8"); // 设置输出流编码格式 初始化生成器

        xmlSerializer.startDocument("utf-8", true); // 写xml文件头，独立的xml文件
        xmlSerializer.startTag(null, "smss");

        Uri uri = Uri.parse("content://sms/"); // 主机名为sms，参数为null
        Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);

        // 开始备份，设置进度条对话框最大值
        int max = cursor.getCount();
        int progress = 0;
        backUpCallBack.beforeBackup(max); // 设置进度条最大值

        xmlSerializer.attribute(null, "max", ""+max); // 设置所有短信长度属性

        while (cursor.moveToNext()){
            Thread.sleep(10);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            LogUtil.d("body: "+body);
            LogUtil.d("address: "+address);
            LogUtil.d("type: "+type);
            LogUtil.d("date: "+date);

            xmlSerializer.startTag(null, "sms");

            xmlSerializer.startTag(null, "body");
            xmlSerializer.text(null == body? "信息内容为空":body);
            xmlSerializer.endTag(null, "body");

            xmlSerializer.startTag(null, "address");
            xmlSerializer.text(null == address? "发送者为空":address);
            xmlSerializer.endTag(null, "address");

            xmlSerializer.startTag(null, "type");
            xmlSerializer.text(null == type? "短信内容为空":type);
            xmlSerializer.endTag(null, "type");

            xmlSerializer.startTag(null, "date");
            xmlSerializer.text(null == date? "时间为空":date);
            xmlSerializer.endTag(null, "date");

            xmlSerializer.endTag(null, "sms");

            progress++;
            LogUtil.d("progress = "+progress);
            backUpCallBack.onSmsBackup(progress); // 备份过程中，增加进度条
        }
        xmlSerializer.endTag(null, "smss");
        xmlSerializer.endDocument(); // 面向对象，有开头有结尾
        fos.close();
    }

    private static String dateToString(long date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(date));
    }

    /**
     * 还原短信
     * @param context
     */
    public static void restoreSms(Context context) throws IOException, XmlPullParserException {

        // 1、读取SD卡xml的文件
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileInputStream fis = new FileInputStream(file); // 获取一个文件输入流
        XmlPullParser parser = Xml.newPullParser(); // 实例化pull解析器，基于事件解析一行一行解析
        parser.setInput(fis, "utf-8");
        // 2、读取max
//        int max = Integer.valueOf(parser.getAttributeValue(null, "max")); // 从xml文件读取短信条数
//        LogUtil.d("xml max = "+max);
        // 3、读取每一条短信信息，body,address,type,date

        String tag;
        List<SmsInfomation> smsInfomationList = new ArrayList<SmsInfomation>();
        int eventType = parser.getEventType(); // 获取当前事件类型
        while (eventType != XmlPullParser.END_DOCUMENT) { // 文档结束
            tag = parser.getName(); // 返回当前标签的名称
            LogUtil.d("current Tag: "+tag);
            SmsInfomation smsInfomation = null; // 用于保存一条短信

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT: // 文档开始
                    break;
                case XmlPullParser.START_TAG:
                    if ("sms".equals(tag)){
                        // 一条短信开始的标签
                        smsInfomation = new SmsInfomation();
                        LogUtil.d("new smsInfomation: "+smsInfomation);
                        break;
                    }
                    if ("body".equals(tag)){
                        // 短信内容标签
                        String body = parser.nextText(); // 当前标签对应的属性内容
                        LogUtil.d("body = "+body);
//                        smsInfomation.setBody(body);
                    }else if ("address".equals(tag)){
                        // 发送者标签
                        String address = parser.nextText();
                        LogUtil.d("address = "+address);
                    }else if ("type".equals(tag)){
                        // 短信类型标签
                        String type = parser.nextText();
                        LogUtil.d("type = "+type);
                    }else if ("date".equals(tag)){
                        // 时间标签
                        String date = parser.nextText();
                        LogUtil.d("date = "+date);
                    }

                    break;
                case XmlPullParser.TEXT:
                    break;
                case XmlPullParser.END_TAG:
                    if ("sms".equals(tag)){
                        smsInfomationList.add(smsInfomation); // 添加到集合中
                    }
                    break;
                default:break;
            }
            eventType = parser.next(); // 获取下一个解析事件，逐行扫描
        }

        for (SmsInfomation infomation: smsInfomationList){
            LogUtil.d(infomation.toString());
        }
        // 4、插入到数据库
//        ContentResolver resolver = context.getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put("body", "");
//        values.put("address", "");
//        values.put("type", "");
//        values.put("date", "");
//        Uri uri = Uri.parse("content://sms/"); // 主机名为sms，参数为null
//        resolver.insert(uri, values); // 将一条短信插入到数据库中
    }
}

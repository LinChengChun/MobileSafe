package com.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobilesafe.utils.LogUtil;

/**
 * Created by Administrator on 2016/8/6.
 */
public class NumberAddressQueryUtils {
    private static String path = "data/data/com.mobilesafe/files/address.db";
    /**
     * 传一个号码进来，返回一个归属地回去
     * @param num
     * @return
     */
    public static String queryNumber(String num){
        String address = num;
        LogUtil.i("num = "+num);
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        // 手机号码归类：13、14、15、16、18
        if (num.matches("^1[34568]\\d{9}$")){
            String sql_cammand = "select location from data2 where id = (select outkey from data1 where id = ?)";
            Cursor cursor = database.rawQuery(sql_cammand, new String[]{num.substring(0,7)});
            while (cursor.moveToNext()){
                String location = cursor.getString(0); // 获取location列对应的数据
                address = location;
            }
            cursor.close();
        }else {
            switch (num.length()){ // 识别输入电话号码的长度
                case 3: // 110
                    address = "报警号码";
                    break;
                case 4: // 5554
                    address = "模拟器";
                    break;
                case 5: // 10086
                    address = "客服电话";
                    break;
                case 7:
                case 8: // 83551234
                    address = "本地号码";
                    break;
                default: // 处理长途电话
                    if (num.length()>=10 && num.startsWith("0")){
                        String sql = "select location from data2 where area = ?";
                        Cursor cursor = database.rawQuery(sql, new String[]{num.substring(1,3)}); // 查询 010,020之类的
                        while (cursor.moveToNext()){
                            String location = cursor.getString(0);
                            LogUtil.i("查询 010,020之类 location = "+location);
                            address = location.substring(0, location.length()-2); // 截取前面两个位置，去掉后面2个具体服务商
                        }
                        cursor.close();

                        cursor = database.rawQuery(sql, new String[]{num.substring(1, 4)});// 查询 0768,0755之类的
                        while (cursor.moveToNext()){
                            String location = cursor.getString(0); // 查询区号
                            LogUtil.i("查询 0768,0755之类 location = "+location);
                            address = location.substring(0, location.length()-2);;
                        }
                        cursor.close();
                    }
                    break;
            }
        }
        return address;
    }
}

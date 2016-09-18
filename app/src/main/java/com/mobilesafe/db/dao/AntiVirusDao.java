package com.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 黑名单数据库的增删改查业务类
 * Created by Administrator on 2016/8/23.
 */
public class AntiVirusDao {

    public static boolean isVirus(String md5){
        boolean result = false;
        String path = "data/data/com.mobilesafe/files/antivirus.db";
        // 1.打开病毒数据库文件
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
        if (cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

}
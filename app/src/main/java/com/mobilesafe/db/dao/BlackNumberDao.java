package com.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobilesafe.db.BlackNumberDBOpenHelper;

/**
 * 黑名单数据库的增删改查业务类
 * Created by Administrator on 2016/8/23.
 */
public class BlackNumberDao {

    private BlackNumberDBOpenHelper helper; // 定义一个帮助类

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public BlackNumberDao(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    /**
     * 查询黑名单号码是否存在
     *
     * @param number
     * @return
     */
    public boolean exists(String number) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase(); // 获取(创建或打开)可读数据库
        Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 添加黑名单号码
     * @param number 黑名单号码
     * @param mode 拦截模式
     */
    public void add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("number", number);
        value.put("mode", mode);
        db.insert("blacknumber", null, value);
        db.close();
    }

    /**
     * 修改黑名单号码拦截模式
     * @param number 要修改的号码
     * @param newmode 新的拦截模式
     */
    public void update(String number, String newmode){

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("mode", newmode);
        db.update("blacknumber", value, "where number=?", new String[]{number});
        db.close();
    }

    /**
     * 删除黑名单号码
     * @param number 要删除的黑名单号码
     */
    public void delete(String number){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "where number=?", new String[]{number});
        db.close();
    }
}
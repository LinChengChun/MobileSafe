package com.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobilesafe.bean.BlackNumberInfo;
import com.mobilesafe.db.BlackNumberDBOpenHelper;
import com.mobilesafe.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据库的增删改查业务类
 * Created by Administrator on 2016/8/23.
 */
public class BlackNumberDao {

    private static BlackNumberDao mBlackNumberDao;
    private BlackNumberDBOpenHelper helper; // 定义一个帮助类

    /**
     * 构造方法
     * @param context 上下文
     */
    private BlackNumberDao(Context context) {
        helper = new BlackNumberDBOpenHelper(context);
    }

    public synchronized static BlackNumberDao getIntance(Context context){
        if (mBlackNumberDao == null)
            mBlackNumberDao = new BlackNumberDao(context);
        return mBlackNumberDao;
    }
    /**
     * 查询黑名单号码是否存在
     *
     * @param number
     * @return
     */
    public boolean find(String number) {
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
     * 查询黑名单号码拦截模式
     * @param number
     * @return 返回号码拦截模式，如果
     */
    public String findMode(String number) {
        String mode = null;
        LogUtil.d(number);
        SQLiteDatabase db = helper.getReadableDatabase(); // 获取(创建或打开)可读数据库
        Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
        if (cursor.moveToNext()) {
            mode = cursor.getString(2);
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 预先加载所有的黑名单号码信息到内存中
     * @return
     */
    public List<BlackNumberInfo> loadAllBlackNumber() {
        List<BlackNumberInfo> mList = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase(); // 获取(创建或打开)可读数据库
        Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null ); // 根据id号降序排列
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setNumber(number); // cursor.getColumnName(0)
            info.setMode(mode); // cursor.getColumnName(1)
            LogUtil.d(info.toString());
            mList.add(info); //  添加到集合中
        }
        cursor.close();
        db.close();
        return mList;
    }

    /**
     * 添加黑名单号码
     * @param number 黑名单号码
     * @param mode 拦截模式
     */
    public void add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        LogUtil.d("number = "+number+";mode = "+mode);
        ContentValues value = new ContentValues();
        value.put("number", number);
        value.put("mode", mode);
        db.insert(BlackNumberDBOpenHelper.TableName, null, value);
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
        db.update(BlackNumberDBOpenHelper.TableName, value, "number=?", new String[]{number});
        db.close();
    }

    /**
     * 删除黑名单号码
     * @param number 要删除的黑名单号码
     */
    public void delete(String number){
        SQLiteDatabase db = helper.getWritableDatabase();
        if (null == number)
            db.delete(BlackNumberDBOpenHelper.TableName, null, null);
        else
            db.delete(BlackNumberDBOpenHelper.TableName, "number=?", new String[]{number});
        db.close();
    }

    /**
     * 查询所有全部黑名单号码
     * @return
     */
    public List<BlackNumberInfo> findAll(){
        // 加入延时模拟加载过程
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<BlackNumberInfo> mList = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase(); // 获取(创建或打开)可读数据库
        Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null ); // 根据id号降序排列
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setNumber(number); // cursor.getColumnName(0)
            info.setMode(mode); // cursor.getColumnName(1)
            LogUtil.d(info.toString());
            mList.add(info); //  添加到集合中
        }
        cursor.close();
        db.close();
        return mList;
    }

    /**
     * 查询部分黑名单号码
     * @param offeset 从哪个位置开始查询
     * @param maxnumber 总共加载多少个记录
     * @return
     */
    public List<BlackNumberInfo> findPart(int offeset, int maxnumber){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<BlackNumberInfo> mList = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase(); // 获取(创建或打开)可读数据库
        Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset ?",
                new String[]{String.valueOf(maxnumber), String.valueOf(offeset)}); // 根据id号降序排列
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setNumber(number); // cursor.getColumnName(0)
            info.setMode(mode); // cursor.getColumnName(1)
            LogUtil.d(info.toString());
            mList.add(info); //  添加到集合中
        }
        cursor.close();
        db.close();
        return mList;
    }
}
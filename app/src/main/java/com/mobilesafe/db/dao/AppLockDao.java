package com.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobilesafe.db.AppLockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序锁的dao
 * Created by cclin on 2016/9/11.
 */
public class AppLockDao {
    private AppLockDBOpenHelper helper; // 数据库帮助类，用于创建数据库
    private static AppLockDao appLockDao; // 数据库操作类，用于管理数据库
    private Context mContext;
    /**
     * 构造方法
     * @param context 上下文
     */
    private AppLockDao(Context context) {
        this.helper = new AppLockDBOpenHelper(context);
        this.mContext = context;
    }

    /**
     * 外部获取dao实例
     * @param context
     * @return
     */
    public static synchronized AppLockDao getIntance(Context context){
        if (appLockDao == null){
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    /**
     * 添加一个要锁定的应用程序的包名
     * @param packname
     */
    public void add(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        db.insert(AppLockDBOpenHelper.TableName, null, values);
        db.close();
        Intent intent = new Intent();
        intent.setAction("com.mobilesafe.applockchange");
        mContext.sendBroadcast(intent); // 发送广播
    }

    /**
     * 删除一个要锁定的应用程序的包名
     * @param packname
     */
    public void delete(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(AppLockDBOpenHelper.TableName, "packname=?", new String[]{packname});
        db.close();
        Intent intent = new Intent();
        intent.setAction("com.mobilesafe.applockchange");
        mContext.sendBroadcast(intent); // 发送广播
    }

    /**
     * 查询一条程序锁包名记录是否存在
     * @param packname
     * @return
     */
    public boolean find(String packname){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AppLockDBOpenHelper.TableName, null, "packname=?", new String[]{packname}, null, null, null);
        if (cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部程序锁包名
     * @return List<String>
     */
    public List<String> findAll(){
        List<String> protectPacknames = new ArrayList<String>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(AppLockDBOpenHelper.TableName, new String[]{"packname"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            protectPacknames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return protectPacknames;
    }
}

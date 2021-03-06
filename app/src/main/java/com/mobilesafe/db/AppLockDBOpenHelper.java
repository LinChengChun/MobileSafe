package com.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/8/23.
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper{

    private static final String DatabaseName = "applock.db";
    public static final String TableName = "applock";
    /**
     * 数据库创建构造方法 数据库名称 blacknumber.db
     * @param context
     */
    public AppLockDBOpenHelper(Context context) {
        super(context, DatabaseName, null, 1); // 创建数据库，该方法不会立即创建，直到getWritableDatabase被调用
    }

    /**
     * 第一次创建数据库时会调用，一般用来创建表格和初始化表格格式
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TableName +
                " (_id integer primary key autoincrement," +
                "packname varchar(20)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

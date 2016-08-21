package com.mobilesafe.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobilesafe.R;
import com.mobilesafe.adapter.SelectContactListViewAdapter;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.User;
import com.mobilesafe.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/7.
 */
public class SelectContactActivity extends BaseActivity{

    private ListView lvSelectContact = null; // 定义ListView控件
    private SelectContactListViewAdapter adapter = null; // 适配器
    private List<User> mListUser = null; // 定义一个集合
    private User mSelectUser = null;

    List<Map<String, String>> mListData = null; // 用于保存联系人信息的集合

    @Override
    protected int initLayout() {
        return R.layout.activity_select_contact;
    }

    @Override
    protected void initView() {
        lvSelectContact = retrieveView(R.id.lv_select_contact); // 检索指定id
    }

    @Override
    protected void initActionBar() {
    }

    @Override
    protected void initListener() {
        lvSelectContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectUser = mListUser.get(position);
                LogUtil.i(mSelectUser.getName()+","+mSelectUser.getNumber());
                onBackup(); // 返回上一个页面
            }
        });
    }

    @Override
    protected void initData() {
        mListUser = new ArrayList<User>();
        getContactInfo(mListUser); // 获取联系人列表
//        for (int i=0; i<10; i++){
//            User user = new User(String.valueOf(i), String.valueOf(i*100));// ""+i;String.valueOf(i);Integer.toString(i)
//            mListUser.add(user);
//        }

        adapter = new SelectContactListViewAdapter(SelectContactActivity.this, mListUser);
        lvSelectContact.setAdapter(adapter);
    }

    /**
     * 选择完联系人，可以返回上一个页面
     */
    private void onBackup(){
        Intent intent = new Intent();
        intent.putExtra("SelectContact", "this is a test!!!!!");
        intent.putExtra("user", mSelectUser);
        setResult(123, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        onBackup();
    }

    /**
     * 通过 内容解析器 获取电话联系人信息
     * @return
     */
    private List<Map<String, String>> getContactInfo(){

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ContentResolver resolver = getContentResolver(); // 获取内容解析器

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");// 获取 contact_id
        Uri uriData = Uri.parse("content://com.android.contacts/data");// 获取 电话号码、姓名

        Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()){
            String contact_id = cursor.getString(0);
            if (!TextUtils.isEmpty(contact_id)){

                Map<String, String> map = new HashMap<String, String>(); // 定义一个Map集合
                Cursor dataCursor = resolver.query(uriData, new String[]{"data1","mimetype"},
                        "contact_id=?", new String[]{contact_id}, null); // 获取游标

                while (dataCursor.moveToNext()){
                    String data1 = dataCursor.getString(0);// String result = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                    String mimetype = dataCursor.getString(1);
                    LogUtil.i(data1+":"+mimetype);

                    if ("vnd.android.cursor.item/name".equals(mimetype)){ // 假如类型是 name 则添加为姓名
                        map.put("name", data1);
                    }else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)){// 假如类型是phone_v2 则添加为号码
                        map.put("num", data1);
                    }
                }
                list.add(map);// 添加到list集合中
                dataCursor.close(); // 记得关闭游标
            }
        }
        cursor.close();// 记得关闭游标
        return list;
    }

    /**
     * 通过 内容解析器 获取电话联系人信息
     * @return
     */
    private void getContactInfo(List<User> list){

        ContentResolver resolver = getContentResolver(); // 获取内容解析器

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");// 获取 contact_id
        Uri uriData = Uri.parse("content://com.android.contacts/data");// 获取 电话号码、姓名

        if (!list.isEmpty())
            list.clear();// 清空列表

        Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
        while (cursor.moveToNext()){
            String contact_id = cursor.getString(0);

            if (!TextUtils.isEmpty(contact_id)){
                // 实例化一个联系人对象
                User user = new User();

                Cursor dataCursor = resolver.query(uriData, new String[]{"data1","mimetype"},
                        "contact_id=?", new String[]{contact_id}, null); // 获取游标

                while (dataCursor.moveToNext()){
                    String data1 = dataCursor.getString(0);// String result = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                    String mimetype = dataCursor.getString(1);
                    LogUtil.i(data1+":"+mimetype);

                    if ("vnd.android.cursor.item/name".equals(mimetype)){ // 假如类型是 name 则添加为姓名
                        user.setName(data1);       //map.put("name", data1);
                    }else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)){// 假如类型是phone_v2 则添加为号码
                        user.setNumber(data1); //map.put("num", data1);
                    }
                }
                list.add(user);// 添加到list集合中
                dataCursor.close(); // 记得关闭游标
            }
        }
        cursor.close();// 记得关闭游标
    }
}

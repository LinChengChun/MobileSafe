package com.mobilesafe.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobilesafe.R;
import com.mobilesafe.adapter.SelectMediaAdapter;
import com.mobilesafe.base.BaseActivity;
import com.mobilesafe.bean.MediaInfo;
import com.mobilesafe.utils.DBUtils;
import com.mobilesafe.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/23.
 */
public class SearchMediaActivity extends BaseActivity{

    private ListView lvSelectMedia = null; // 定义ListView控件
    private List<MediaInfo> mListMedia = null; // 定义一个集合
    private MediaInfo mSelectMedia = null;
    private SelectMediaAdapter adapter = null;

    @Override
    protected int initLayout() {
        return R.layout.activity_select_media;
    }

    @Override
    protected void initView() {
        lvSelectMedia = retrieveView(R.id.lv_select_media); // 检索ListView id
    }

    @Override
    protected void initListener() {
        lvSelectMedia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.i(view.toString()+ "onItemClick" + position);
                mSelectMedia = mListMedia.get(position); // get the click item
            }
        });
        lvSelectMedia.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectMedia = mListMedia.get(position); // get the click item
                onBackup(); // 长按返回上一页面
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        mListMedia = new ArrayList<MediaInfo>();

        DBUtils.searchMedia(SearchMediaActivity.this, mListMedia); // get Media data

        adapter = new SelectMediaAdapter(SearchMediaActivity.this, mListMedia);

        lvSelectMedia.setAdapter(adapter);
    }

    /**
     * 选择完某一个音频，就返回上一个页面
     */
    private void onBackup(){
        Intent intent = new Intent();
        intent.putExtra("media", mSelectMedia);
//        intent.putExtra("mediaUri", mSelectMedia.getUri());
//        intent.putExtra("mediaUriString", mSelectMedia.getUri().toString());
        intent.putExtra("test", "this is a test");
        setResult(RESULT_OK, intent);
        LogUtil.i("this is SearchMediaActivity onBackup");
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        onBackup(); // 返回上一页面
    }
}

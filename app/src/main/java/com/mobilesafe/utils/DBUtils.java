package com.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.mobilesafe.bean.MediaInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/7/23.
 */
public class DBUtils {

    public static void searchMedia(Context context, List<MediaInfo> mListMedia){

        if (!mListMedia.isEmpty()) // 假如集合中不为空，即清空集合
            mListMedia.clear();

        ContentResolver contentResolver = context.getContentResolver(); // 获取 内容解决者 ContentResolver对象
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // 获取指定音频资源路径

        Cursor cursor = contentResolver.query(uri, null, null, null, null); // 获取游标
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                Uri mediaUri = android.provider.MediaStore.Audio.Media.getContentUri(thisTitle);
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                LogUtil.i("path = "+path);
                LogUtil.i("searchMedia:"+thisTitle+":"+thisId);
                LogUtil.i("mediaUri = "+mediaUri);
                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setName(thisTitle);
                mediaInfo.setResID(thisId);
                mediaInfo.setUri(mediaUri);
                mediaInfo.setPath(path);

                mListMedia.add(mediaInfo); // add to list
                // ...process entry...
            } while (cursor.moveToNext());
        }

        cursor.close();
    }
}

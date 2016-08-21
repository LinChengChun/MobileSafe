package com.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mobilesafe.utils.LogUtil;

/**
 * Created by Administrator on 2016/7/23.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    private static final String ACTION_PLAY = "com.example.action.PLAY";
    private MediaPlayer mMediaPlayer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(getClass()+"onCreate...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogUtil.i(getClass()+"onStartCommand...");
        if (intent.getAction().equals(ACTION_PLAY)) {
            mMediaPlayer = new MediaPlayer(); // initialize it here
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtil.i(getClass()+"onDestroy...");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        LogUtil.i(getClass()+"onLowMemory...");
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.i(getClass()+"onUnbind...");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        LogUtil.i(getClass()+"onRebind...");
        super.onRebind(intent);
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }


}

package com.yihai.caotang.service;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.yihai.caotang.data.Constants;
import com.yihai.caotang.utils.FileUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SoundTrackPlayer implements MediaPlayer.OnErrorListener {
    private static final String TAG = "SoundTrackPlayer";

    private final WeakReference<SoundTrackService> mService;
    private MediaPlayer mSoundTrackPlayer;   //音轨播放
    private MediaPlayer mBackgroundPlayer; //背景音乐播放
    private Handler mHandler;

    private boolean mIsInitialized = false;
    private boolean isFirstLoad = true;
    boolean mIllegalState = false;

    private Handler handler = new Handler();

    public SoundTrackPlayer(final SoundTrackService service) {
        mService = new WeakReference<>(service);
    }

    /******************************
     * 音轨播放
     ******************************/

    /**
     * 初始化音轨播放
     */
    private void initSoundtrack() {
        mSoundTrackPlayer = new MediaPlayer();
        mSoundTrackPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
        mSoundTrackPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mHandler.sendEmptyMessage(SoundTrackService.TRACK_ENDED);
            }
        });
    }

    /**
     * 设置音轨路径
     *
     * @param path
     * @return
     */
    public boolean setSoundTrackPath(final String path) {
        //初始化播放器
        initSoundtrack();
        //设置音轨路径
        try {
            mSoundTrackPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mSoundTrackPlayer.setDataSource(path);
            if (mIllegalState) {
                mIllegalState = false;
            }
        } catch (final IOException todo) {
            return mIsInitialized = false;
        } catch (final IllegalArgumentException todo) {
            return mIsInitialized = false;
        } catch (final IllegalStateException todo) {
            todo.printStackTrace();
            if (!mIllegalState) {
                mSoundTrackPlayer = null;
                mSoundTrackPlayer = new MediaPlayer();
                mSoundTrackPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
                setSoundTrackPath(path);
                mIllegalState = true;
            } else {
                mIllegalState = false;
                return mIsInitialized = false;
            }
        }

        mSoundTrackPlayer.setOnErrorListener(this);
        return mIsInitialized = true;
    }

    /**
     * 播放音轨
     */
    public void start() {
        try {
            mSoundTrackPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException ex) {

        }
        mSoundTrackPlayer.start();
    }

    /**
     * 停止播放音轨
     */
    public void stop() {
        if (mSoundTrackPlayer == null) {
            return;
        }
        mSoundTrackPlayer.stop();
        mSoundTrackPlayer.reset();
        mSoundTrackPlayer = null;
        mIsInitialized = false;
    }

    /**
     * 释放资源
     */
    public void release() {
        mSoundTrackPlayer.release();
    }

    /**
     * 获取音乐长度
     *
     * @return
     */
    public long duration() {
        return mSoundTrackPlayer.getDuration();
    }

    /**
     * 获取音乐当前播放位置
     *
     * @return
     */
    public long position() {
        try {
            return mSoundTrackPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 设置音量
     *
     * @param vol
     */
    public void setVolume(final float vol) {
        try {
            mSoundTrackPlayer.setVolume(vol, vol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /******************************
     *
     * 背景音乐
     *
     ******************************/

    /**
     * 播放背景音乐
     */
    public void startBackground() {
        //初始化播放器
        mBackgroundPlayer = MediaPlayer.create(mService.get().getApplicationContext(), FileUtils.getRealFileUri(Constants.BACKGROUND_MUSIC_PATH));
        mBackgroundPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
        mBackgroundPlayer.setOnErrorListener(this);
        mBackgroundPlayer.setLooping(true);
        mBackgroundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mBackgroundPlayer.seekTo(0);   //循环播放
            }
        });
        mBackgroundPlayer.start();
    }

    /**
     * 停止播放背景音乐
     */
    public void stopBackground() {
        if (mBackgroundPlayer != null) {
            mBackgroundPlayer.stop();
            mBackgroundPlayer.reset();
        }
    }

    /******************************
     * 其他
     ******************************/

    public void setHandler(final Handler handler) {
        mHandler = handler;
    }

    public boolean isInitialized() {
        return mIsInitialized;
    }

    @Override
    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
        Log.w(TAG, "Music Server Error what: " + what + " extra: " + extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                final SoundTrackService service = mService.get();
                mIsInitialized = false;
                mSoundTrackPlayer.release();
                mSoundTrackPlayer = new MediaPlayer();
                mSoundTrackPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
                Message msg = mHandler.obtainMessage(SoundTrackService.SERVER_DIED);
                mHandler.sendMessageDelayed(msg, 200);
                return true;
            default:
                break;
        }
        return false;
    }

}

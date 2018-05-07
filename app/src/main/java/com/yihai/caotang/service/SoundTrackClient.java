package com.yihai.caotang.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.yihai.caotang.ISoundTrackService;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SoundTrackClient {

    public static final int MSG_PLAY_SOUND_TRACK = 1;
    public static final int MSG_STOP_SOUND_TRACK = 2;
    public static final int MSG_PLAY_BACKGROUND = 3;
    public static final int MSG_STOP_BACKGROUND = 4;

    private static SoundTrackClient mInstance;

    private ISoundTrackService mService;
    private Context mContext;
    private CountDownLatch mCountDownLatch;
    private WorkHandler mWorkHandler;
    private HandlerThread mWorkThread;

    public static SoundTrackClient getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SoundTrackClient.class) {
                if (mInstance == null) {
                    mInstance = new SoundTrackClient(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private SoundTrackClient(Context c) {
        this.mContext = c;
        mWorkThread = new HandlerThread("soundtrackc-lient");
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper(), this);
    }

    /************************
     * 音轨播放
     **************************/
    public void play(final String soundUri) {
        Bundle b = new Bundle();
        b.putString("uri", soundUri);
        Message msg = mWorkHandler.obtainMessage(MSG_PLAY_SOUND_TRACK);
        msg.setData(b);
        msg.sendToTarget();
    }

    private void playImpt(final String soundUri) {
        if (mService == null) {
            bindServiceSync();
        }
        try {
            mService.play(soundUri);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        Message msg = mWorkHandler.obtainMessage(MSG_STOP_SOUND_TRACK);
        msg.sendToTarget();
    }

    private void stopImpt() {
        if (mService == null) {
            bindServiceSync();
        }
        try {
            mService.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayingSoundTrack() {
        if (mService != null) {
            try {
                return mService.isPlayingSoundTrack();
            } catch (final RemoteException ex) {
                ex.printStackTrace();
            } catch (final IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public long position() {
        if (mService != null) {
            try {
                return mService.position();
            } catch (final RemoteException ex) {
                ex.printStackTrace();
            } catch (final IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }

    public long duration() {
        if (mService != null) {
            try {
                return mService.duration();
            } catch (final RemoteException ex) {
                ex.printStackTrace();
            } catch (final IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }

    /************************
     * 背景音乐播放
     **************************/
    public void playBackground() {
        Message msg = mWorkHandler.obtainMessage(MSG_PLAY_BACKGROUND);
        msg.sendToTarget();
    }

    private void playBackgroundImpt() {
        if (mService == null) {
            bindServiceSync();
        }
        try {
            mService.playBackground();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopBackground() {
        Message msg = mWorkHandler.obtainMessage(MSG_STOP_BACKGROUND);
        msg.sendToTarget();
    }

    private void stopBackgroundImpt() {
        if (mService == null) {
            bindServiceSync();
        }
        try {
            mService.stopBackground();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void bindServiceSync() {
        //检查线程
        checkRuntime(true);
        //线程等待计数
        mCountDownLatch = new CountDownLatch(1);
        if (mContext.bindService(new Intent(mContext, SoundTrackService.class), mConnection, Context.BIND_AUTO_CREATE)) {
            waitServiceReady();
        } else {

        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ISoundTrackService.Stub.asInterface(service);
            notifyServiceReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    /**
     * 通知线程Service连接成功
     * 线程计数器计数-1
     */
    private void notifyServiceReady() {
        if (mCountDownLatch != null) {
            mCountDownLatch.countDown();
            mCountDownLatch = null;
        }
    }

    /**
     * 等待Service连接
     * 线程计数器等待
     */
    private void waitServiceReady() {
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkRuntime(boolean async) {
        Looper targetLooper = async ? mWorkThread.getLooper() : Looper.getMainLooper();
        Looper curLooper = Looper.myLooper();
        if (curLooper != targetLooper) {
            throw new RuntimeException();
        }
    }

    private class WorkHandler extends Handler {
        private WeakReference<SoundTrackClient> mClient;

        public WorkHandler(Looper looper, SoundTrackClient client) {
            super(looper);
            mClient = new WeakReference<>(client);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PLAY_SOUND_TRACK:
                    String uri = msg.getData().getString("uri");
                    mClient.get().playImpt(uri);
                    break;
                case MSG_STOP_SOUND_TRACK:
                    mClient.get().stopImpt();
                    break;
                case MSG_PLAY_BACKGROUND:
                    mClient.get().playBackgroundImpt();
                    break;
                case MSG_STOP_BACKGROUND:
                    mClient.get().stopBackgroundImpt();
                    break;
                default:
                    break;
            }
        }
    }
}

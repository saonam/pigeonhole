package com.yihai.caotang.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.yihai.caotang.IHearBeatService;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class HeartBeatClient {

    public static final int MSG_START = 1;
    public static final int MSG_STOP = 2;

    private static HeartBeatClient mInstance;

    private IHearBeatService mService;
    private Context mContext;
    private CountDownLatch mCountDownLatch;
    private WorkHandler mWorkHandler;
    private HandlerThread mWorkThread;

    public static HeartBeatClient getInstance(Context context) {
        if (mInstance == null) {
            synchronized (HeartBeatClient.class) {
                if (mInstance == null) {
                    mInstance = new HeartBeatClient(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private HeartBeatClient(Context c) {
        this.mContext = c;
        mWorkThread = new HandlerThread("heartbeat-lient");
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper(), this);
    }

    public void start() {
        Message msg = mWorkHandler.obtainMessage(MSG_START);
        msg.sendToTarget();
    }

    private void startImpt() {
        if (mService == null) {
            bindServiceSync();
        }
        try {
            mService.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        Message msg = mWorkHandler.obtainMessage(MSG_STOP);
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


    private void bindServiceSync() {
        //检查线程
        checkRuntime(true);
        //线程等待计数
        mCountDownLatch = new CountDownLatch(1);
        if (mContext.bindService(new Intent(mContext, HeartBeatService.class), mConnection, Context.BIND_AUTO_CREATE)) {
            waitServiceReady();
        } else {

        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IHearBeatService.Stub.asInterface(service);
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

    /**
     * 检查运行时
     * @param async
     */
    private void checkRuntime(boolean async) {
        Looper targetLooper = async ? mWorkThread.getLooper() : Looper.getMainLooper();
        Looper curLooper = Looper.myLooper();
        if (curLooper != targetLooper) {
            throw new RuntimeException();
        }
    }

    /**
     * 命令handler
     */
    private class WorkHandler extends Handler {
        private WeakReference<HeartBeatClient> mClient;

        public WorkHandler(Looper looper, HeartBeatClient client) {
            super(looper);
            mClient = new WeakReference<>(client);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START:
                    mClient.get().startImpt();
                    break;
                case MSG_STOP:
                    mClient.get().stopImpt();
                    break;
                default:
                    break;
            }
        }
    }
}

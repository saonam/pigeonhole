package com.huijimuhei.beacon;

import android.content.BroadcastReceiver;
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

import com.huijimuhei.beacon.utils.proxy.ProxyBulk;
import com.huijimuhei.beacon.utils.proxy.ProxyInterceptor;
import com.huijimuhei.beacon.utils.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class BeaconClient implements IBeaconClient, ProxyInterceptor {
    public static final int MSG_INVOKE_PROXY = 1;
    public static final int MSG_INCOMING_LOCATION = 2;
    private static final int MSG_REG_RECEIVER = 3;

    private static IBeaconClient mInstance;
    private Context mContext;
    private CountDownLatch mCountDownLatch;

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;

    private IBeaconService mService;
    private LocationBroadcase mBroadcast;

    public static IBeaconClient getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BeaconClient.class) {
                if (mInstance == null) {
                    BeaconClient client = new BeaconClient(context.getApplicationContext());
                    mInstance = ProxyUtils.getProxy(client, IBeaconClient.class, client);
                }
            }
        }
        return mInstance;
    }

    private BeaconClient(Context context) {
        mContext = context;
        mWorkThread = new HandlerThread("beacon_client");
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper());
        mWorkHandler.obtainMessage(MSG_REG_RECEIVER).sendToTarget();
    }


    /**
     * 开始搜索
     */
    public void startScan() {
        if (mService == null) {
            bindServiceSync();
        }
        try {
            mService.startScan();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结束搜索
     */
    @Override
    public void stopScan() {
        if (mService != null) {
            try {
                mService.stopScan();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void warm() {
        if (mService == null) {
            bindServiceSync();
        }
        try {
            mService.warm();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Service连接绑定
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IBeaconService.Stub.asInterface(service);
            notifyBluetoothManagerReady();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };


    /*********************
     * 广播
     *********************/
    private void registBoardcast() {
    }

    private void unRegistBoardcast() {

    }

    private class LocationBroadcase extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    /*********************
     * 工作线程
     *********************/
    /**
     * 映射方式执行函数
     * 此用法是为了方便在ui线程与工作线程之间转换
     *
     * @param object
     * @param method
     * @param args
     * @return
     */
    @Override
    public boolean onIntercept(Object object, Method method, Object[] args) {
        mWorkHandler.obtainMessage(MSG_INVOKE_PROXY, new ProxyBulk(object, method, args))
                .sendToTarget();
        return true;
    }

    /**
     * 绑定服务
     */
    public void bindServiceSync() {
        //检查线程
        checkRuntime(true);
        //线程等待计数
        mCountDownLatch = new CountDownLatch(1);
        //绑定service
        if (mContext.bindService(new Intent(mContext, BeaconService.class), mConnection, Context.BIND_AUTO_CREATE)) {
            waitBluetoothManagerReady();
        } else {
        }
    }

    /**
     * 通知线程Service连接成功
     * 线程计数器计数-1
     */
    private void notifyBluetoothManagerReady() {
        if (mCountDownLatch != null) {
            mCountDownLatch.countDown();
            mCountDownLatch = null;
        }
    }

    /**
     * 等待Service连接
     * 线程计数器等待
     */
    private void waitBluetoothManagerReady() {
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查运行线程是否为工作线程
     * 避免countdownwatch出错
     *
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
     * 工作线程Handler
     */
    private class WorkHandler extends Handler {

        private static final int MSG_INVOKE_PROXY = 1;

        private WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INVOKE_PROXY:
                    ProxyBulk.safeInvoke(msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}

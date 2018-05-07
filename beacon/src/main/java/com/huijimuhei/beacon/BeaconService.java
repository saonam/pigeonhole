package com.huijimuhei.beacon;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

public class BeaconService extends Service {
    public static final int MSG_START_SCAN = 1;
    public static final int MSG_STOP_SCAN = 2;

    private IBinder mBinder;
    private HandlerThread mWorkThread;
    private Handler mWorkHandler;
    private BeaconScanner mScanner;

    public BeaconService() {
        mBinder = new BeaconServiceStub(this);
        mWorkThread = new HandlerThread("BeaconServiceThread", Thread.MAX_PRIORITY);
        mWorkThread.start();
        mWorkHandler = new WorkHandler(mWorkThread.getLooper());
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mScanner = new BeaconScanner(BeaconService.this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mScanner.onDestory();
        mWorkThread.quit();
        super.onDestroy();
    }

    protected void startScan() {
        mScanner.warmUp();
        mScanner.startScan();
        mScanner.startObtain();
    }

    protected void stopScan() {
        mScanner.stopScan();
    }

    protected void warm() {
        mScanner.warmUp();
    }

    private class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_SCAN:
                    startScan();
                    break;
                case MSG_STOP_SCAN:
                    break;
                default:
                    break;
            }
        }
    }

    public class BeaconServiceStub extends IBeaconService.Stub {

        private final WeakReference<BeaconService> mService;

        public BeaconServiceStub(BeaconService service) {
            this.mService = new WeakReference<>(service);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
            try {
                super.onTransact(code, data, reply, flags);
            } catch (final RuntimeException e) {
                e.printStackTrace();
                File file = new File(mService.get().getCacheDir().getAbsolutePath() + "/err/");
                if (!file.exists()) {
                    file.mkdirs();
                }
                try {
                    PrintWriter writer = new PrintWriter(mService.get().getCacheDir().getAbsolutePath() + "/err/" + System.currentTimeMillis() + "_aidl.log");
                    e.printStackTrace(writer);
                    writer.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        CommonUtils.sendTextMail("err aidl log from " + CommonUtils.getUniquePsuedoID(), CommonUtils.getDeviceInfo() + Log.getStackTraceString(e));
                    }
                }).start();

                throw e;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void startScan() throws RemoteException {
            mService.get().startScan();
        }

        @Override
        public void stopScan() throws RemoteException {
            mService.get().stopScan();
        }

        @Override
        public void warm() throws RemoteException {
            mService.get().warm();
        }

    }
}

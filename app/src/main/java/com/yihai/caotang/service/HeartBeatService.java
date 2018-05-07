package com.yihai.caotang.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.IHearBeatService;
import com.yihai.caotang.adapter.landscape.LandscapeAntiqueListAdapter;
import com.yihai.caotang.client.RequestClient;
import com.yihai.caotang.data.JsonResponse;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;
import com.yihai.caotang.data.session.Session;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.data.session.SessionResponse;
import com.yihai.caotang.event.MapEvent;
import com.yihai.caotang.event.TimeLimitNotifyEvent;
import com.yihai.caotang.utils.CoordsUtils;
import com.yihai.caotang.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import xiaofei.library.hermeseventbus.HermesEventBus;

import static com.huijimuhei.beacon.BeaconScanner.ACCURACY_GPS;

public class HeartBeatService extends Service {
    private static final String TAG = "HeartBeat";

    /**
     * 心跳间隔
     */
    private static final int HEART_BEAT_DURATION = 5000;

    /**
     * 心跳间隔
     */
    private static final int TIME_LIMITE_INTERVAL = 15;

    private IBinder mBinder;
    private Handler mLoopHandler;
    private AfterPostHeartBeatTask mAfterLoopTask;
    private AMapLocationClient locationClient = null;

    public HeartBeatService() {
        mBinder = new HeartBeatServiceStub(HeartBeatService.this);
        mLoopHandler = new Handler();
        mAfterLoopTask = new AfterPostHeartBeatTask();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void startHeartBeat() {
        mLoopHandler.postDelayed(mAfterLoopTask, HEART_BEAT_DURATION);
    }

    /**
     * 定位
     */
    private void startLocate() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation loc) {
                if (null != loc) {
                    //有错误pass
                    if (loc.getErrorCode() != 0) {
                        SessionManager.getInstance().getSession().setGpsCorrect(false);
                        return;
                    }
                    //更新坐标
                    SessionManager.getInstance().getSession().setGpsCorrect(true);
                    SessionManager.getInstance().updateLoacation(loc.getLongitude(), loc.getLatitude(), loc.getBearing());
                    SessionManager.getInstance().getSession().setLastLocAt(System.currentTimeMillis());
                    Log.d("bearing", String.format("%f--%f--%f", loc.getLongitude(), loc.getLatitude(), loc.getBearing()));
//                    //若定位经度为gps
//                    if (SessionManager.getInstance().getSession().getAccuracy() == ACCURACY_GPS) {
//                        //检查是否在景点内
//                        List<LandScape> landScapes = LandScapeManager.getInstance().getLandscapes();
//                        for (LandScape landscape : landScapes) {
//                            if (landscape.getBorders().size() == 0) {
//                                continue;
//                            }
//                            if (CoordsUtils.PtInPolygon(new LatLng(loc.getLatitude(), loc.getLongitude()), landscape.getBorderLatLng())) {
//                                EventBus.getDefault().post(new MapEvent(1, landscape));
//                            }
//                        }
//                    }
                    //首页更新位置
                    EventBus.getDefault().post(new MapEvent(0, null));
                }
            }
        });
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止心跳
     */
    private void stop() {
        mLoopHandler.removeMessages(1);
        mLoopHandler.removeCallbacks(mAfterLoopTask);
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient = null;
        }
    }

    /**
     * 心跳请求
     */
    private void postHeartBeat() {
        RequestClient.postHeartBeat(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JsonResponse<SessionResponse> wrap = JsonResponse.parse(responseString, SessionResponse.class);
                Logger.i(wrap.getMsg());
            }
        });
    }

    /**
     * 检查租借时间
     */
    private void checkLendTimeLength() {
        if (AppController.DEBUG) {
            return;
        }
        //检查是否已经用户确认
        if (SessionManager.getInstance().getSession().isTimeLimitedTriggered()) {
            return;
        }

        String startAt = SessionManager.getInstance().getSession().getStartAt();
        long total = SessionManager.getInstance().getSession().getTimeLength();
        long usedTime = DateUtils.tillNowOfMin(startAt);
        long leftTime = total * 60 - usedTime;
        if (leftTime < TIME_LIMITE_INTERVAL) {
            HermesEventBus.getDefault().post(new TimeLimitNotifyEvent(leftTime));
        }
    }

    /**
     * 地图定位默认设置
     *
     * @return
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }


    /**
     * 循环task
     */
    protected class AfterPostHeartBeatTask implements Runnable {
        @Override
        public void run() {
            if (AppController.DEBUG) {
                return;
            }
            //心跳请求
            postHeartBeat();
            //检查租借时间
            checkLendTimeLength();
            //再次启动循环
            startHeartBeat();
        }
    }

    protected class HeartBeatServiceStub extends IHearBeatService.Stub {
        private final WeakReference<HeartBeatService> mService;

        public HeartBeatServiceStub(HeartBeatService service) {
            this.mService = new WeakReference<>(service);
        }

        @Override
        public void start() throws RemoteException {
            this.mService.get().startHeartBeat();
            this.mService.get().startLocate();
        }

        @Override
        public void stop() throws RemoteException {
            this.mService.get().stop();
        }
    }
}

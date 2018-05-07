package com.yihai.caotang;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.huijimuhei.beacon.BeaconClient;
import com.huijimuhei.beacon.data.BleDevice;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.yihai.caotang.client.RequestClient;
import com.yihai.caotang.data.JsonResponse;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;
import com.yihai.caotang.data.session.Session;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.data.session.SessionResponse;
import com.yihai.caotang.data.sysinfo.SysInfoManager;
import com.yihai.caotang.event.ActionReceiveEvent;
import com.yihai.caotang.event.BatteryNotifyEvent;
import com.yihai.caotang.event.BeaconTriggerEvent;
import com.yihai.caotang.event.EntryLandscapeEvent;
import com.yihai.caotang.event.PositionedEvent;
import com.yihai.caotang.receiver.BatteryNotifyRecevier;
import com.yihai.caotang.receiver.FenceTriggerReceiver;
import com.yihai.caotang.receiver.LandScapeLogReceiver;
import com.yihai.caotang.receiver.PositionReceiver;
import com.yihai.caotang.receiver.SpotTriggerReceiver;
import com.yihai.caotang.receiver.TimeLimitNotifyRecevier;
import com.yihai.caotang.receiver.WifiRecevier;
import com.yihai.caotang.service.HeartBeatClient;
import com.yihai.caotang.ui.AboutUsActivity;
import com.yihai.caotang.ui.MainActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.ui.dialog.LoadingDialog;
import com.yihai.caotang.ui.landscape.LandScapeDetailActivity;
import com.yihai.caotang.ui.landscape.PoetryFillGameActivity;
import com.yihai.caotang.ui.landscape.QRScanActivity;
import com.yihai.caotang.ui.landscape.UnityPlayerActivity;
import com.yihai.caotang.ui.manage.LockScreenActivity;
import com.yihai.caotang.ui.media.VideoPlayActivity;
import com.yihai.caotang.utils.CoordsUtils;
import com.yihai.caotang.utils.DateUtils;
import com.yihai.caotang.utils.SysInfoUtils;
import com.yihai.caotang.utils.ToastUtils;
import com.yihai.caotang.widgets.CoinView;
import com.yihai.caotang.widgets.GameEntryButtonView;

import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import cz.msebera.android.httpclient.Header;
import xiaofei.library.hermeseventbus.HermesEventBus;

import static com.huijimuhei.beacon.BeaconScanner.ACTION_BEACON_FENCE_TRIGGERED;
import static com.huijimuhei.beacon.BeaconScanner.ACTION_BEACON_POSITIONED;
import static com.huijimuhei.beacon.BeaconScanner.ACTION_BEACON_SPOT_TRIGGERED;
import static com.yihai.caotang.event.ActionReceiveEvent.ACTION_BEENING_CHANGED;
import static com.yihai.caotang.event.ActionReceiveEvent.ACTION_RETURN;
import static com.yihai.caotang.event.ActionReceiveEvent.ACTION_TIME_LIMITE;
import static com.yihai.caotang.receiver.LandScapeLogReceiver.ACTION_UPDATE_LANDSCAPE_LOG;
import static com.yihai.caotang.ui.landscape.UnityPlayerActivity.EXTRA_DATA;
import static com.yihai.caotang.ui.landscape.UnityPlayerActivity.EXTRA_MODE;

/**
 * 业务流程控制
 * 设备状态监控
 */
public class AppController {
    private static AppController mInstance = null;

    public static final boolean DEBUG = true;

    /**
     * 退出景点离开unity
     */
    public static final int FLAG_LEAVE_UNITY = 0;

    /**
     * 变换景点，改变unity的scene
     */
    public static final int FLAG_CHANGE_UNITY_SCENE = 1;

    /**
     * 退出景点离开unity
     */
    public static final String ACTION_LEAVE_UNITY = "com.yihai.caotang.leaveunity";

    /**
     * 变换景点，改变unity的scene
     */
    public static final String ACTION_CHANGE_UNITY_SCENE = "com.yihai.caotang.changescene";

    private BatteryNotifyRecevier mBatteryReceiver;         //电量监控
    private WifiRecevier mWifiRecevier;                     //WIFI监控
    private FenceTriggerReceiver mFenceTriggerRecevier;     //beacon报警
    private PositionReceiver mPositionTriggerReceiver;      //beacon位置触发
    private SpotTriggerReceiver mSpotTriggerReceiver;       //beacon触发
    private TimeLimitNotifyRecevier mTimeLimitReceiver;     //时间限制
    private LandScapeLogReceiver mLandScapeLogReceiver;     //unity景点次数刷新

    private LoadingDialog loadingDialog;                //进度dialog
    protected CoinView mCoinView;                       //钱币
    protected GameEntryButtonView mGameEntryButton;     //游戏按键
    public Stack<Activity> mActStack;                   //activity生命周期栈
    private final Context mContext;                     //上下文

    public static void init(Context context) {
        if (mInstance == null) {
            synchronized (AppController.class) {
                mInstance = new AppController(context);
            }
        }
    }

    public static AppController getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("you must init app monitor first");
        }
        return mInstance;
    }

    private AppController(Context context) {
        this.mContext = context;
        this.mActStack = new Stack<>();

        //注册监听
        registReceiver();

        //Coin inition
        mCoinView = new CoinView();
        mCoinView.setListener(new CoinView.AnimateListener() {
            @Override
            public void onFinish(boolean isHit) {
                if (isHit) {
                    SessionManager.getInstance().increaseStarNum(2);
                }
            }
        });

        //Game Entry Button inition
        mGameEntryButton = new GameEntryButtonView();
        mGameEntryButton.setListener(new GameEntryButtonView.OnClickListener() {
            @Override
            public void onClick(View view, LandScape landScape) {
                enterGame(landScape);
            }
        });
    }

    /**************
     *
     * 业务逻辑
     *
     **************/

    /**
     * 归还后复原
     */
    public void reset() {
        //Session复位
        SessionManager.getInstance().reset();
        //关闭音乐
        AppContext.getInstance().stopSoundTrack();
        //调整屏幕亮度
        SysInfoUtils.changeAppBrightness(80, mContext);
        // 心跳请求
        HeartBeatClient.getInstance(mContext).stop();
        // beacon定位
        BeaconClient.getInstance(mContext).stopScan();
    }

    /**
     * 出租成功后执行
     */
    public void didAfterLend() {
        //调整屏幕亮度
        SysInfoUtils.changeAppBrightness(SessionManager.getInstance().getSession().getBrightness(), mContext);
        //开始心跳请求
        HeartBeatClient.getInstance(mContext).start();
        //开始beacon定位
        BeaconClient.getInstance(mContext).startScan();
    }

    /**
     * 不续借的情况
     * 监控在lockscreen中reset
     * 1.heartbeat停止
     * 2.beacon定位停止
     * 3.session复位(LockScreenActivity做)
     */
    public void didAfterNotUsing() {
        //不再使用状态提醒
        SessionManager.getInstance().getSession().setUsing(false);
        //音乐
        AppContext.getInstance().stopBackground();
        AppContext.getInstance().stopSoundTrack();
        //跳转至锁屏界面
        startActivity(AboutUsActivity.newIntent(AboutUsActivity.MODE_NOT_USING));
        finishBackgroundActivity();
    }

    /**
     * 销毁监控\音乐\心跳\beacon
     */
    public void onTerminate() {
        //广播
        unRegistReceivers();
        //音乐
        AppContext.getInstance().stopBackground();
        AppContext.getInstance().stopSoundTrack();
        //心跳请求
        HeartBeatClient.getInstance(mContext).stop();
        //beacon定位
        BeaconClient.getInstance(mContext).startScan();
    }

    /***********
     * 广播监听
     *
     * *********/
    private void registReceiver() {
        //注册电量监控
        mBatteryReceiver = new BatteryNotifyRecevier(this);
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBatteryReceiver, batteryFilter);

        //注册wifi监控
        mWifiRecevier = new WifiRecevier();
        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mWifiRecevier, wifiFilter);

        //Beacon位置触发
        mPositionTriggerReceiver = new PositionReceiver(this);
        IntentFilter positionFilter = new IntentFilter();
        positionFilter.addAction(ACTION_BEACON_POSITIONED);
        mContext.registerReceiver(mPositionTriggerReceiver, positionFilter);

        //Beacon预警监控
        mFenceTriggerRecevier = new FenceTriggerReceiver(this);
        IntentFilter alertFilter = new IntentFilter();
        alertFilter.addAction(ACTION_BEACON_FENCE_TRIGGERED);
        mContext.registerReceiver(mFenceTriggerRecevier, alertFilter);

        //Beacon触发监控
        mSpotTriggerReceiver = new SpotTriggerReceiver(this);
        IntentFilter spotFilter = new IntentFilter();
        spotFilter.addAction(ACTION_BEACON_SPOT_TRIGGERED);
        mContext.registerReceiver(mSpotTriggerReceiver, spotFilter);

        //时间限制　
        mTimeLimitReceiver = new TimeLimitNotifyRecevier(this);
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(ACTION_TIME_LIMITE);
        mContext.registerReceiver(mTimeLimitReceiver, timeFilter);

        //景点次数
        mLandScapeLogReceiver= new LandScapeLogReceiver(this);
        IntentFilter logFilter = new IntentFilter();
        logFilter.addAction(ACTION_UPDATE_LANDSCAPE_LOG);
        mContext.registerReceiver(mLandScapeLogReceiver, logFilter);
    }

    private void unRegistReceivers() {
        if (mBatteryReceiver != null) {
            mContext.unregisterReceiver(mBatteryReceiver);
            mBatteryReceiver = null;
        }
        if (mWifiRecevier != null) {
            mContext.unregisterReceiver(mWifiRecevier);
            mWifiRecevier = null;
        }
        if (mFenceTriggerRecevier != null) {
            mContext.unregisterReceiver(mFenceTriggerRecevier);
            mFenceTriggerRecevier = null;
        }
        if (mSpotTriggerReceiver != null) {
            mContext.unregisterReceiver(mSpotTriggerReceiver);
            mSpotTriggerReceiver = null;
        }
        if (mTimeLimitReceiver != null) {
            mContext.unregisterReceiver(mTimeLimitReceiver);
            mTimeLimitReceiver = null;
        }
        if (mLandScapeLogReceiver != null) {
            mContext.unregisterReceiver(mLandScapeLogReceiver);
            mLandScapeLogReceiver = null;
        }
    }

    /*******************
     * 事件响应，包括：
     * 1。电量提醒
     * 2。beacon触发
     *3。jpush命令：归还
     *******************/

    /**
     * JPUSH命令
     */
    public void onActionEvent(ActionReceiveEvent event) {
        switch (event.getAction()) {
            case ACTION_RETURN: //设备归还
                postReturn();
                break;
            case ACTION_BEENING_CHANGED: //被替换
                if (SessionManager.getInstance().getSession().getStatus() == Session.PAD_STATUS_LENGDING) {
                    //跳转
                    startActivity(LockScreenActivity.newIntent());
                    finishBackgroundActivity();
                } else {
                    ToastUtils.show(currentActivity(), "设备并未在租用状态,无法被替换!");
                }
                break;
            case "test":
//                enterLandscape(LandScapeManager.getInstance().getLandscape(4));
//                quitLandscape(SessionManager.getInstance().getSession().getCurLandscape());
                break;
        }
    }

    /**
     * 归还确认
     */
    private void postReturn() {
        showLoading();
        RequestClient.postReturn(SysInfoManager.getSysInfo().getPadIdStr(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(currentActivity(), "网络错误归还失败请重试");
                dismissLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dismissLoading();
                JsonResponse<SessionResponse> wrap = JsonResponse.parse(responseString, SessionResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(currentActivity(), wrap.getMsg());
                    //再次执行
                    postReturn();
                }
                startActivity(LockScreenActivity.newIntent());
                finishBackgroundActivity();
                reset();
            }
        });
    }


    /****************
     * 电量通知
     *
     ****************/
    public void onBatteryNotifyEvent(BatteryNotifyEvent event) {
        if (DEBUG) {
            return;
        }

        switch (event.getEvent()) {
            case BatteryNotifyEvent.EVENT_BATTERY_LOCK:
                //不再使用状态提醒
                SessionManager.getInstance().getSession().setUsing(false);
                //跳转至锁屏界面
                startActivity(AboutUsActivity.newIntent(AboutUsActivity.MODE_LOW_BATTERY));
                //关闭页面
                finishBackgroundActivity();
                break;
//            case BatteryNotifyEvent.EVENT_BATTERY_WARNING:
//                //是否在使用
//                if (!SessionManager.getInstance().getSession().isUsing()) {
//                    return;
//                }
//
//                new ConfirmDialog(currentActivity())
//                        .setText("设备电量低，如继续使用到电量下限则锁定设备，如继续使用则前往替换点替换设备，是否替换")
//                        .setConfirmButton("前往替换", new ConfirmDialog.OnDialogBtnClickListener() {
//                            @Override
//                            public void onClick(View v, Dialog dialog) {
//                                dialog.dismiss();
//                                //不再使用状态提醒
//                                SessionManager.getInstance().getSession().setUsing(false);
//                                //跳转锁屏
//                                startActivity(AboutUsActivity.newIntent(AboutUsActivity.MODE_LOW_BATTERY));
//                                //结束页面
//                                finishBackgroundActivity();
//                            }
//                        })
//                        .setCancelButton("继续使用", new ConfirmDialog.OnDialogBtnClickListener() {
//                            @Override
//                            public void onClick(View v, Dialog dialog) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
//                break;
        }

    }

    /******************
     * Beacon特殊触发事件
     * 报警通知
     * 特定点触发
     *
     ******************/
    public void onBeaconTriggerEvent(BeaconTriggerEvent event) {
        switch (event.getEvent()) {
            case BeaconTriggerEvent.EVENT_FENCE_TRIGGER:
                //是否已触发
                if (SessionManager.getInstance().getSession().isFenceWarningTriggered()) {
                    return;
                }
                SessionManager.getInstance().getSession().setFenceWarningTriggered(true);
                //跳转锁屏页
                startActivity(AboutUsActivity.newIntent(AboutUsActivity.MODE_FENCE_TRIGGER));
                finishBackgroundActivity();
                //网络请求通知
                postFenceWarning(event.getDevice());
                //不再使用状态提醒
                SessionManager.getInstance().getSession().setUsing(false);
                //音乐
                AppContext.getInstance().stopBackground();
                AppContext.getInstance().stopSoundTrack();
                break;
            case BeaconTriggerEvent.EVENT_SPOT_TRIGGER:
//                if (!SessionManager.getInstance().getSession().isUsing()) {
//                    return;
//                }
//
//                if (event.getType() == BleDevice.SPOT_PARAM_ROUTER) {
//
//                    //是否已经进入
//                    if (isSpotTriggered) {
//                        return;
//                    }
//                    //跳转
//                    isSpotTriggered = true;
//                    String routerType = event.getDevice().getParam2();
//                    switch (routerType) {
//                        case BleDevice.ROUTER_PARAM_QRCODE:
//                            startActivity(QRScanActivity.newIntent());
//                            break;
//                        case BleDevice.ROUTER_PARAM_VIDEO:
//                            startActivity(VideoPlayActivity.newIntent(VideoPlayActivity.VIDEO_YINGBI));
//                            break;
//                        case BleDevice.ROUTER_PARAM_VR:
//                            LandScape landscape = LandScapeManager.getInstance().getLandscape(Integer.valueOf(event.getDevice().getParam3()));
//                            startVr(landscape);
//                            break;
//                        case BleDevice.ROUTER_PARAM_POTRY_GAME:
//                            LandScape landscape2 = LandScapeManager.getInstance().getLandscape(Integer.valueOf(event.getDevice().getParam3()));
//                            startActivity(PoetryFillGameActivity.newIntent(landscape2));
//                    }
//                } else if (event.getType() == BleDevice.SPOT_PARAM_SOUND_TRACK) {
//                    AppContext.getInstance().playSoundTrack(Constants.SD_ROOT + "soundtrack/ch/" + event.getDevice().getParam3());//TODO 多国语言化
//                }
                break;
        }

    }

    /**
     * 报警通知
     *
     * @param beacon
     */
    private void postFenceWarning(final BleDevice beacon) {
        RequestClient.postWarning(SessionManager.getInstance().getLocationLatLng().latitude, SessionManager.getInstance().getLocationLatLng().longitude, String.valueOf(beacon.getId()), String.valueOf(beacon.getLand_scape_id()), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                postFenceWarning(beacon);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String res = responseString;
                Logger.d(res);
            }
        });
    }

    /***
     * Beacon景点触发事件
     **/
    public void onBeaconPositionedEvent(PositionedEvent event) {
        if (!SessionManager.getInstance().getSession().isUsing()) {
            return;
        }

        LandScape curLandscape = SessionManager.getInstance().getSession().getCurLandscape();
        LandScape locLandscape = SessionManager.getInstance().getSession().getLocLandscape();
        LandScape lastLandscape = SessionManager.getInstance().getSession().getLastLandscape();

        //如果当前已进入景点
        if (curLandscape != null) {
            //定位在不同景点，进入定位景点
            if (locLandscape != null && !curLandscape.equals(locLandscape)) {
                if (AppController.DEBUG) {
                    ToastUtils.show(mContext, "进入景点" + locLandscape.getName());
                }
                //refresh
                curLandscape = locLandscape;
                SessionManager.getInstance().getSession().setCurLandscape(locLandscape);
                SessionManager.getInstance().getSession().setLastLandscape(curLandscape);
                //通知
                HermesEventBus.getDefault().post(new EntryLandscapeEvent(EntryLandscapeEvent.ENTRY, curLandscape));
            }
            /**
             * 用户决定是否退出，不自动化
             */
            //定位不在景点内,离开
//            if (locLandscape == null) {
//                //Gps辅助检查
//                LandScape landScape = checkGps();
//                if (landScape == null || !landScape.equals(curLandscape)) {
//                    ToastUtils.show(mContext, "离开景点");
//                    //refresh
//                    SessionManager.getInstance().getSession().setCurLandscape(null);
//                    SessionManager.getInstance().getSession().setLastLandscape(curLandscape);
//                    quitLandscape(curLandscape);
//                    curLandscape = null;
//                }
//            }
        } else if (locLandscape != null) {
            //进入上一个景点，不触发
            if (locLandscape == lastLandscape) {
                return;
            }
            if (AppController.DEBUG) {
                ToastUtils.show(mContext, "进入定位景点" + locLandscape.getName());
            }
            //refresh
            curLandscape = locLandscape;
            SessionManager.getInstance().getSession().setCurLandscape(locLandscape);
            //通知弹窗
            HermesEventBus.getDefault().post(new EntryLandscapeEvent(EntryLandscapeEvent.ENTRY, curLandscape));
        }
    }

    /**
     * 检查是否在景点内
     *
     * @return
     */
    private LandScape checkGps() {
        double lat = SessionManager.getInstance().getSession().getLat();
        double lng = SessionManager.getInstance().getSession().getLng();

        for (LandScape landscape : LandScapeManager.getInstance().getLandscapes()) {
            if (landscape.getBorders().size() == 0) {
                continue;
            }

            if (CoordsUtils.PtInPolygon(new LatLng(lat, lng), landscape.getBorderLatLng())) {
                return landscape;
            }
        }
        return null;
    }

    /**
     * 进入景点
     *
     * @param landscape
     */
    public void enterLandscape(LandScape landscape) {

        //更新景点进入次数
        int times = SessionManager.getInstance().getSession().getLandScapeLog().get(landscape.getId());
        SessionManager.getInstance().getSession().getLandScapeLog().put(landscape.getId(), times + 1);

//        //是否显示游戏图标
//        if (landscape.getHasGame() == 1) {
//            showGameEntryButton();
//            mGameEntryButton.setLandScape(landscape);
//        }

        //是否到达大门离开
        if (isNearGate(landscape)) {
            return;
        }

        //播放介绍音频
        AppContext.getInstance().stopSoundTrack();
        AppContext.getInstance().playSoundTrack(landscape.getRealSoundTrackPath());

        //页面跳转
        if (landscape.getDisplayType().equals("QRCODE")) {
            startActivity(QRScanActivity.newIntent());
        } else if (landscape.getDisplayType().equals("VIDEO")) {
            startActivity(VideoPlayActivity.newIntent(VideoPlayActivity.VIDEO_YINGBI, landscape));
        } else if (landscape.getDisplayType().equals("DETAIL")) {
            startActivity(LandScapeDetailActivity.newIntent(landscape));
        } else if (landscape.getDisplayType().equals("MAP")) {
            //显示钱币
            int times2 = SessionManager.getInstance().getSession().getLandScapeLog().get(landscape.getId());
            if (times2 == 1) {
                showCoin();
            }
        } else {
            //启动vr界面,判断当前是否已经在vr界面中
            startActivity(UnityPlayerActivity.newIntent(landscape, UnityPlayerActivity.MODE_RECOG));
        }

    }

    /**
     * 更新景点进入次数
     *
     * @param landScapeId
     */
    public void updateLandscapeLog(int landScapeId) {
        int times = SessionManager.getInstance().getSession().getLandScapeLog().get(landScapeId);
        SessionManager.getInstance().getSession().getLandScapeLog().put(landScapeId, times + 1);
    }

    /**
     * 离开景点
     */
    public void quitLandscape(LandScape landscape) {
        //显示钱币
        int times = SessionManager.getInstance().getSession().getLandScapeLog().get(landscape.getId());
        if (AppController.DEBUG) {
            ToastUtils.show(mContext, "quiteLandscape:" + String.valueOf(times));
        }
        if (times == 1) {
            showCoin();
        }
//        SessionManager.getInstance().getSession().setCurLandscape(null);
//        notifyUnity(FLAG_LEAVE_UNITY);
//        finishBackgroundActivityButMain();

//        if (landscape.getHasGame() == 1) {
//            dismissGameEntryButton();
//        }
    }

    /**
     * 进入游戏
     *
     * @param landscape
     */
    private void enterGame(LandScape landscape) {
        if (landscape.getGameType().equals("POETRY")) {
            startActivity(PoetryFillGameActivity.newIntent());
        } else {
            startActivity(UnityPlayerActivity.newIntent(landscape, UnityPlayerActivity.MODE_GAME));
        }
    }

    /**
     * 通知unity
     *
     * @param flag
     */
    private void notifyUnity(int flag) {
        notifyUnity(flag, null, 0);
    }

    /**
     * 通知unity
     *
     * @param flag
     * @param data
     * @param mode
     */
    private void notifyUnity(int flag, LandScape data, int mode) {
        Intent intent = new Intent();
        switch (flag) {
            case FLAG_CHANGE_UNITY_SCENE:
                intent.setAction(ACTION_CHANGE_UNITY_SCENE);
                intent.putExtra(EXTRA_DATA, data);
                intent.putExtra(EXTRA_MODE, mode);
                break;
            case FLAG_LEAVE_UNITY:
                intent.setAction(ACTION_LEAVE_UNITY);
                break;
        }

        mContext.sendBroadcast(intent);
    }

    /**
     * 是否接近大门
     * 5分钟内不提醒
     *
     * @param curLandscape
     */
    public boolean isNearGate(LandScape curLandscape) {
        if (curLandscape.getId() == 1 || curLandscape.getId() == 20 || curLandscape.getId() == 21) {
            //经过两个景点，在走到大门就提示结束(大门不算)
            int count = 0;
            Map map = SessionManager.getInstance().getSession().getLandScapeLog();
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                int times = (int) entry.getValue();
                if (times > 0) {
                    count++;
                }
            }
            if (count > 2) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 不续借确认
     */
    private void postNotKeepLend() {
        RequestClient.postNotKeepLend(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(currentActivity(), "网络错误归还失败");
                postNotKeepLend();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JsonResponse<SessionResponse> wrap = JsonResponse.parse(responseString, SessionResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(currentActivity(), wrap.getMsg());
                    //再次执行
                    postNotKeepLend();
                    return;
                }
                //关闭页面
                didAfterNotUsing();
            }
        });
    }
    /***************
     * Activity 控制
     **************/

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (mActStack == null) {
            mActStack = new Stack<>();
        }
        mActStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = mActStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = mActStack.lastElement();
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mActStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mActStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishBackgroundActivity() {
        for (int i = 0; i < mActStack.size(); i++) {
            if (null != mActStack.get(i)) {
                mActStack.get(i).finish();
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishBackgroundActivityButMain() {
        for (int i = 0; i < mActStack.size(); i++) {
            if (null != mActStack.get(i) && !mActStack.get(i).getClass().getName().equals(MainActivity.TAG)) {
                mActStack.get(i).finish();
            }
        }
    }

    private void startActivity(Intent intent) {
        Activity act = currentActivity();
        if (act != null) {
            act.startActivity(intent);
        }
    }

    /***************
     * 对话窗
     **************/
    private void showLoading() {
        Activity act = currentActivity();
        if (act != null) {
            if (loadingDialog == null && act.getFragmentManager() != null) {
                loadingDialog = LoadingDialog.build(act);
                loadingDialog.show();
            }
        }
    }

    public void dismissLoading() {
        Activity act = currentActivity();
        if (act != null) {
            if (loadingDialog != null && act.getFragmentManager() != null) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        }
    }

    private void showEntryConfirm() {
        Activity act = currentActivity();
        if (act != null) {
            if (loadingDialog == null && act.getFragmentManager() != null) {
                loadingDialog = LoadingDialog.build(act);
                loadingDialog.show();
            }
        }
    }


    /*******************
     * 钱币/游戏按钮
     *******************/
    public void showCoin() {
        mCoinView.show();
    }

    public void dismissCoin() {
        mCoinView.dismiss();
    }

    public void showGameEntryButton() {
        mGameEntryButton.show();
    }

    public void dismissGameEntryButton() {
        mGameEntryButton.dismiss();
    }
}

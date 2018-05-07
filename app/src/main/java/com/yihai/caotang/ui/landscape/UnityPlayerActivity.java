package com.yihai.caotang.ui.landscape;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huijimuhei.beacon.BeaconScanner;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.unity3d.player.UnityPlayer;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.R;
import com.yihai.caotang.adapter.OnItemClickLisener;
import com.yihai.caotang.adapter.antique.AntiqueListAdapter;
import com.yihai.caotang.data.antique.Antique;
import com.yihai.caotang.data.antique.AntiqueManager;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.event.BatteryNotifyEvent;
import com.yihai.caotang.event.EntryLandscapeEvent;
import com.yihai.caotang.event.TimeLimitNotifyEvent;
import com.yihai.caotang.receiver.LandScapeLogReceiver;
import com.yihai.caotang.service.SoundTrackService;
import com.yihai.caotang.ui.MainActivity;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.media.VideoPlayActivity;
import com.yihai.caotang.widgets.AntiqueIntroView;
import com.yihai.caotang.ui.manage.ConfigUserActivity;
import com.yihai.caotang.ui.repository.RepositoryListActivity;
import com.yihai.caotang.ui.task.TaskListActivity;
import com.yihai.caotang.utils.ToastUtils;
import com.yihai.caotang.utils.ViewUtils;
import com.yihai.caotang.widgets.ConfirmEntryView;
import com.yihai.caotang.widgets.GameEntryButtonView;
import com.yihai.caotang.widgets.LeavingTipView;
import com.yihai.caotang.widgets.NoGlowRecyclerView;
import com.yihai.caotang.widgets.SoundTrackMenuView;
import com.yihai.caotang.widgets.ToggleMenuView;
import com.yihai.caotang.widgets.VerticalListItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * 因为是新开进程
 * 直接把数据都传过来
 * 消息传递用广播/EventBus升级版
 */
public class UnityPlayerActivity extends BaseActivity {
    public static final String TAG = UnityPlayerActivity.class.getName();
    public static final int MODE_RECOG = 0;
    public static final int MODE_GAME = 1;
    public static final String EXTRA_DATA = "extra_data";
    public static final String EXTRA_MODE = "extra_mode";

    protected LandScape mData;
    protected int mMode;

    private UnityPlayer mUnityPlayer;
    private NoGlowRecyclerView mAntiqueListView;
    private ToggleMenuView mToggleMenuView;
    private SoundTrackMenuView mSoundTrackMenuView;
    private ConfirmEntryView mConfirmEntryView;
    private LeavingTipView mLeavingTipView;
    private ImageView mIvBtnBack;
    private GameEntryButtonView mBtnGameEntry;

    private RelativeLayout mFloatingContainer;
    private AntiqueIntroView mAntiqueIntroDialog;
    private WindowManager.LayoutParams mFloatingLayoutParams;
    protected WindowManager mWindowManager;

    private LeaveBroadcast mLeaveBroadcast;
    private SoundTrackBroadcast mSoundTrackBroadcast;
    private SpotTriggerBroadcast mSpotTriggerReceiver;

    private AntiqueListAdapter mAdapter;
    private ArrayList<Antique> mRecogAntiques;

    private Bundle mSavedInstanceState;
    private boolean isRecognized = false;
    private boolean isFloatingViewAdd = false;
    private boolean isForeground = true;
    protected long mExitTime = 0;
    private String mRoomNum = "Nah";

    public static Intent newIntent(LandScape data, int mode) {
        Intent intent = new Intent(AppContext.getInstance(),
                UnityPlayerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_MODE, mode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        mData = getIntent().getParcelableExtra(EXTRA_DATA);
        mMode = getIntent().getIntExtra(EXTRA_MODE, MODE_RECOG);
        mFloatingLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        initUnity();
        initDataBase();
        registerReceivers();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnityPlayer.quit();

        removeFloatingViews();
        unregisterReceiver(mLeaveBroadcast);
        unregisterReceiver(mSoundTrackBroadcast);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isForeground) {
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    addFloatingViews();
//                }
//            }, 13000);
        } else {
            if (!isFloatingViewAdd) {
                addFloatingViews();
            }
            isForeground = true;
        }
        mUnityPlayer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeFloatingViews();
//        AppContext.getInstance().stopSoundTrack();
        mUnityPlayer.pause();
        isForeground = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        mData = getIntent().getParcelableExtra(EXTRA_DATA);
        mMode = getIntent().getIntExtra(EXTRA_MODE, MODE_RECOG);
        changeDisplayScene(mMode == MODE_RECOG ? mData.getDisplayType() : mData.getGameType());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("resume", 1);
    }

    private void initUnity() {
        getWindow().setFormat(PixelFormat.RGBX_8888);
        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();
        changeDisplayScene(mMode == MODE_RECOG ? mData.getDisplayType() : mData.getGameType());
    }

    private void initDataBase() {
        mRecogAntiques = AntiqueManager.getInstance().getArRecognizeList();
    }


    private void changeDisplayScene(String code) {
        if (AppController.DEBUG) {
            ToastUtils.show(UnityPlayerActivity.this, "changeDisplayScene：" + code);
        }
        mUnityPlayer.UnitySendMessage("AndroidListener", "message", code);
    }

    public void UnityReceiver(final String code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //加载完成
                if (code.equals("LOADCOMPLETE")) {
                    if (!isFloatingViewAdd) {
                        addFloatingViews();
                    }
                    return;
                }

                //是否隐藏返回键
                if (code.equals("ARUISHOWING")) {
                    if (mIvBtnBack != null) {
                        mIvBtnBack.setVisibility(View.GONE);
                        mSoundTrackMenuView.setVisibility(View.GONE);
                    }
                    return;
                }

                if (code.equals("ARUIHIDING")) {
                    if (mIvBtnBack != null) {
                        mIvBtnBack.setVisibility(View.VISIBLE);
                        mSoundTrackMenuView.setVisibility(View.VISIBLE);
                    }
                    return;
                }

                //是否是游戏识别的金币奖励
                if (code.equals("FOUNDTARGET") || code.equals("PUZZLEWIN")) {
                    return;
                }

                //检查识别是否丢失
                if (code.equals("AR_LOST")) {
                    if (isRecognized) {
                        isRecognized = false;
                    }
                    return;
                }

                //获取文物
                Antique item = getArRecognizeAntique(code);
                needShowAntiqueDialog(item);
            }
        });
    }

    private Antique getArRecognizeAntique(String code) {
        for (Antique item : mRecogAntiques) {
            if (item.getAr_code().equals(code)) {
                return item;
            }
        }
        return null;
    }

    private void needShowAntiqueDialog(Antique item) {
        if (mAntiqueIntroDialog == null) {
            return;
        }

        if (item == null) {
            needDismissAntiqueDialog();
            return;
        }

        //没有文字图片屏蔽dialog
        if (TextUtils.isEmpty(item.getDisplay_content()) && TextUtils.isEmpty(item.getImage_uri())) {
            needDismissAntiqueDialog();
        } else {
            //显示介绍dialog
            if (!mAntiqueIntroDialog.isShowing()) {
                isRecognized = true;
                mAntiqueIntroDialog.show();
                mSoundTrackMenuView.setToggleClickable(false);
                mSoundTrackMenuView.hide();
            }
            //刷新内容
            mAntiqueIntroDialog.update(item);
        }
        //播放文物介绍
        AppContext.getInstance().playSoundTrack(item.getRealSoundTrackUri());
    }

    private void needDismissAntiqueDialog() {
        if (mAntiqueIntroDialog == null) {
            return;
        }
        if (mAntiqueIntroDialog.isShowing()) {
            mAntiqueIntroDialog.dismiss();
            AppContext.getInstance().stopSoundTrack();
            mSoundTrackMenuView.setToggleClickable(true);
        }
    }

    /*************************************
     * 浮动窗口
     *************************************/
    private void removeFloatingViews() {
        if (isFloatingViewAdd) {
            try {
                mWindowManager.removeViewImmediate(mFloatingContainer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            isFloatingViewAdd = false;
        }
    }

    private void addFloatingViews() {
        mFloatingLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        mFloatingLayoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        mFloatingLayoutParams.gravity = Gravity.TOP; // 调整悬浮窗口至右侧中间
        mFloatingLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mFloatingLayoutParams.width = ViewUtils.getWindowsWidth(UnityPlayerActivity.this);// ViewUtils.dip2px(UnityPlayerActivity.this, 149);
        mFloatingLayoutParams.height = ViewUtils.getWindowsHeight(UnityPlayerActivity.this);// ViewUtils.dip2px(UnityPlayerActivity.this, 446);

        mFloatingContainer = (RelativeLayout) getLayoutInflater().inflate(R.layout.include_unity_floating_view, null, false);
        mFloatingContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                needDismissAntiqueDialog();
                mUnityPlayer.injectEvent(event);
                return false;
            }
        });
        //初始化文物介绍
        mAntiqueIntroDialog = (AntiqueIntroView) mFloatingContainer.findViewById(R.id.antique_intro);
        mAntiqueIntroDialog.dismiss();

        //初始化列表
        mAntiqueListView = (NoGlowRecyclerView) mFloatingContainer.findViewById(R.id.list_images);
        mAntiqueListView.setVisibility(View.VISIBLE);
        mAdapter = new AntiqueListAdapter(new ArrayList<Antique>());
        mAdapter.setOnItemClickLisener(new OnItemClickLisener() {
            @Override
            public void OnItemClick(View view, int position) {
                needShowAntiqueDialog(mAdapter.getEntity(position));
            }

            @Override
            public void OnFunctionClick(View view, int position, int functionFlag) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(UnityPlayerActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mAntiqueListView.setLayoutManager(manager);
        mAntiqueListView.setHasFixedSize(true);
        mAntiqueListView.setClipToPadding(true);
        mAntiqueListView.setEmptyView(R.layout.include_empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);
        mAntiqueListView.setAdapter(mAdapter);
        mAntiqueListView.addItemDecoration(new VerticalListItemDecoration(UnityPlayerActivity.this, 0, 7));
        updateImageList();


        //初始化菜单
        mToggleMenuView = (ToggleMenuView) mFloatingContainer.findViewById(R.id.toggle_menu);
        mToggleMenuView.setVisibility(View.GONE);
        mToggleMenuView.setOnMenuItemClick(new ToggleMenuView.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int type) {
                switch (type) {
                    case ToggleMenuView.CLICK_TASK:
                        startActivity(TaskListActivity.newIntent(UnityPlayerActivity.this));
                        break;
                    case ToggleMenuView.CLICK_REPOSITORY:
                        startActivity(RepositoryListActivity.newIntent());
                        break;
                    case ToggleMenuView.CLICK_CONFIG:
                        startActivity(ConfigUserActivity.newIntent());
                        break;
                }
            }
        });

        //初始化音频菜单
        mSoundTrackMenuView = (SoundTrackMenuView) mFloatingContainer.findViewById(R.id.soundtrack_menu);

        //退出键
        mIvBtnBack = (ImageView) mFloatingContainer.findViewById(R.id.iv_back);
        mIvBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needDismissAntiqueDialog();
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    mConfirmEntryView.setVisibility(View.GONE);
                    mLeavingTipView.setVisibility(View.VISIBLE);
                    mLeavingTipView.show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    mLeavingTipView.setVisibility(View.GONE);
                    //通知弹窗
                    HermesEventBus.getDefault().postSticky(new EntryLandscapeEvent(EntryLandscapeEvent.LEAVE, mData));
                    finish();
                }
            }
        });

        //进入景点提示
        mConfirmEntryView = (ConfirmEntryView) mFloatingContainer.findViewById(R.id.confirm_view);
        mConfirmEntryView.update(mData);
        mConfirmEntryView.setConfirmListener(new ConfirmEntryView.OnBtnClickListener() {
            @Override
            public void onClick(View v, LandScape landScape) {
                mConfirmEntryView.setVisibility(View.GONE);
                enterLandscape(landScape);
            }
        });
        mConfirmEntryView.setCancelListener(new ConfirmEntryView.OnBtnClickListener() {
            @Override
            public void onClick(View v, LandScape landScape) {
                mConfirmEntryView.setVisibility(View.GONE);
            }
        });

        //退出景点提示
        mLeavingTipView = (LeavingTipView) mFloatingContainer.findViewById(R.id.leaving_view);
        mLeavingTipView.setListener(new LeavingTipView.OnLeavingListener() {
            @Override
            public void onLeaving() {
                if (mLeavingTipView != null) {
                    mLeavingTipView.setVisibility(View.GONE);
                }
            }
        });

        //添加浮动
        mWindowManager.addView(mFloatingContainer, mFloatingLayoutParams);
        isFloatingViewAdd = true;
    }

    private void updateImageList() {
        if (mData.getHasImageList() == 1) {
            mAntiqueListView.setVisibility(View.VISIBLE);
            mAdapter.replace(mData.getAntiques());
        } else {
            mAntiqueListView.setVisibility(View.GONE);
        }

    }

    /*************************************
     * UNITYPLAYER 生命周期
     *************************************/


    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }


    /*************************************
     * 广播
     *************************************/
    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEntryLandscapeEvevt(final EntryLandscapeEvent event) {
        if (event.getType() == EntryLandscapeEvent.ENTRY) {
            if (mConfirmEntryView == null) {
                return;
            }
            mConfirmEntryView.update(event.getLandScape());
            mConfirmEntryView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBatteryNotifyEvent(BatteryNotifyEvent event) {
        finish();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeLimitNotifyEvent(TimeLimitNotifyEvent event) {
        finish();
    }

    public void enterLandscape(LandScape landscape) {

//        //更新景点进入次数
//        int times = SessionManager.getInstance().getSession().getLandScapeLog().get(landscape.getId());
//        SessionManager.getInstance().getSession().getLandScapeLog().put(landscape.getId(), times + 1);

//        //是否显示游戏图标
//        if (landscape.getHasGame() == 1) {
//            showGameEntryButton();
//            mGameEntryButton.setLandScape(landscape);
//        }

        //广播更新点记录
        broadcastLandscapeLog(landscape.getId());

        //是否到达大门
        if (isNearGate(landscape)) {
            startActivity(MainActivity.newIntent());
            finish();
            return;
        }

        /**
         * 非unity播放音频通过stick在baseactivity中播放
         */
        //页面跳转
        if (landscape.getDisplayType().equals("QRCODE")) {
            startActivity(QRScanActivity.newIntent());
            HermesEventBus.getDefault().postSticky(new EntryLandscapeEvent(EntryLandscapeEvent.UNITY_TURN_BACK, landscape));
            finish();
        } else if (landscape.getDisplayType().equals("VIDEO")) {
            startActivity(VideoPlayActivity.newIntent(VideoPlayActivity.VIDEO_YINGBI));
            HermesEventBus.getDefault().postSticky(new EntryLandscapeEvent(EntryLandscapeEvent.UNITY_TURN_BACK, landscape));
            finish();
        } else if (landscape.getDisplayType().equals("MAP")) {
            startActivity(MainActivity.newIntent());
            HermesEventBus.getDefault().postSticky(new EntryLandscapeEvent(EntryLandscapeEvent.UNITY_TURN_BACK, landscape));
            finish();
        } else if (landscape.getDisplayType().equals("DETAIL")) {
            startActivity(LandScapeDetailActivity.newIntent(landscape));
            HermesEventBus.getDefault().postSticky(new EntryLandscapeEvent(EntryLandscapeEvent.UNITY_TURN_BACK, landscape));
            finish();
        } else {
            //播放介绍音频
            AppContext.getInstance().stopSoundTrack();
            AppContext.getInstance().playSoundTrack(landscape.getRealSoundTrackPath());
            //启动vr界面,判断当前是否已经在vr界面中
            if (landscape.getDisplayType().equals(mData.getDisplayType())) {
                mData = landscape;
                updateImageList();
            } else {
                mData = landscape;
                removeFloatingViews();
                changeDisplayScene(landscape.getDisplayType());
                updateImageList();
            }
        }
    }

    /**
     * 是否接近大门
     *
     * @param curLandscape
     */
    private boolean isNearGate(LandScape curLandscape) {
        if (curLandscape.getId() == 1 || curLandscape.getId() == 20 || curLandscape.getId() == 21) {
            return true;
        }
        return false;
    }

    private void registerReceivers() {
        //离开景点
        mLeaveBroadcast = new LeaveBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppController.ACTION_CHANGE_UNITY_SCENE);
        filter.addAction(AppController.ACTION_LEAVE_UNITY);
        registerReceiver(mLeaveBroadcast, filter);
        //音频zz
        mSoundTrackBroadcast = new SoundTrackBroadcast();
        registerReceiver(mSoundTrackBroadcast, new IntentFilter(SoundTrackService.PLAYSTATE_COMPLETE));
        //特殊点触发
        mSpotTriggerReceiver = new SpotTriggerBroadcast();
        registerReceiver(mSpotTriggerReceiver, new IntentFilter(BeaconScanner.ACTION_BEACON_SPOT_TRIGGERED));
    }

    public class LeaveBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppController.ACTION_LEAVE_UNITY)) {
                finish();
            } else if (intent.getAction().equals(AppController.ACTION_CHANGE_UNITY_SCENE)) {
                mData = intent.getParcelableExtra(EXTRA_DATA);
                mMode = intent.getIntExtra(EXTRA_MODE, MODE_RECOG);
                changeDisplayScene(mMode == MODE_RECOG ? mData.getDisplayType() : mData.getGameType());
            }
        }
    }

    private class SoundTrackBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mSoundTrackMenuView != null) {
                mSoundTrackMenuView.reset();
            }
        }
    }

    /**
     * 景点次数广播
     */
    private void broadcastLandscapeLog(int landscapeId) {
        Intent intent = new Intent(LandScapeLogReceiver.ACTION_UPDATE_LANDSCAPE_LOG);
        intent.putExtra("data", landscapeId);
        sendBroadcast(intent);
    }

    private long spotAt = -1;

    private class SpotTriggerBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (!mData.getDisplayType().equals("GJVR")) {
//                return;
//            }
//            BleDevice item = intent.getParcelableExtra("data");
//            long now = System.currentTimeMillis();
//            if (!item.getParam2().equals(mRoomNum)) {
//                if (now - spotAt < 3000) {
//                    mRoomNum = item.getParam2();
//                    mUnityPlayer.UnitySendMessage("AndroidListener", "message", mRoomNum);
//                    ToastUtils.show(UnityPlayerActivity.this, item.getParam2());
//                }
////                mUnityPlayer.UnitySendMessage("AndroidListener", "message", mRoomNum);
////                ToastUtils.show(UnityPlayerActivity.this, item.getParam2());
//            }
//            spotAt = now;
        }

    }

}

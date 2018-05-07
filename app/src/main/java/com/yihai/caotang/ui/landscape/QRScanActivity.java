package com.yihai.caotang.ui.landscape;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.R;
import com.yihai.caotang.client.RequestClient;
import com.yihai.caotang.data.JsonResponse;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.data.session.SessionResponse;
import com.yihai.caotang.event.BatteryNotifyEvent;
import com.yihai.caotang.event.EntryLandscapeEvent;
import com.yihai.caotang.event.TimeLimitNotifyEvent;
import com.yihai.caotang.ui.AboutUsActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class QRScanActivity extends AppCompatActivity {

    /**
     * 扫描fragment
     */
    public static final int PAGE_SCAN = 0;

    /**
     * 网页fragment
     */
    public static final int PAGE_WEB = 1;

    private SparseArray<Fragment> mChildFragments;
    private int mCurIndex;
    protected ArrayList<ConfirmDialog> confirmDialogs = new ArrayList<>();  //景点提示框

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                QRScanActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        ButterKnife.bind(this);
        initFragment();
        switchPage(PAGE_SCAN);
    }

    @Override
    public void onStart() {
        super.onStart();
        HermesEventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        HermesEventBus.getDefault().unregister(this);
        HermesEventBus.getDefault().destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        AppContext.getInstance().stopSoundTrack();
    }

    protected boolean isTimeLimitNotified = false;

    /*******************
     * 出租业务控制
     *
     * 即将到达结束时间
     * 为了在每个窗口都能提示，设计为在baseactivity中触发
     * 只要没确认，就每弹出一个窗口确认一次
     *******************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeLimitNotifyEvent(TimeLimitNotifyEvent event) {
        if (AppController.DEBUG) {
            return;
        }

        //检查是否已经用户确认
        if (SessionManager.getInstance().getSession().isTimeLimitedTriggered()) {
            return;
        }

        finish();
    }

    /****************
     * 电量通知
     *
     ****************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBatteryNotifyEvent(BatteryNotifyEvent event) {
        if (AppController.DEBUG) {
            return;
        }

        switch (event.getEvent()) {
            case BatteryNotifyEvent.EVENT_BATTERY_LOCK:
                //不再使用状态提醒
                SessionManager.getInstance().getSession().setUsing(false);
                //跳转至锁屏界面
                startActivity(AboutUsActivity.newIntent(AboutUsActivity.MODE_LOW_BATTERY));
                //关闭页面
                AppController.getInstance().finishBackgroundActivity();
                break;
            case BatteryNotifyEvent.EVENT_BATTERY_WARNING:
                //是否在使用
                if (!SessionManager.getInstance().getSession().isUsing()) {
                    return;
                }
                finish();
                break;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEntryLandscapeEvevt(final EntryLandscapeEvent event) {
        hideFloating();
        if (event.getType() == EntryLandscapeEvent.ENTRY) {
            if (!AppController.getInstance().isNearGate(event.getLandScape())) {
                ConfirmDialog dialog = new ConfirmDialog(QRScanActivity.this)
                        .setText("已靠近" + event.getLandScape().getName() + "，是否进入浏览模式")
                        .setCancelButton("放弃进入", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                showFloating();
                            }
                        })
                        .setConfirmButton("开始浏览", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                AppContext.getInstance().stopSoundTrack();
                                AppController.getInstance().enterLandscape(event.getLandScape());
                                finish();
                            }
                        });
                cleanDialog();
                dialog.show();
                confirmDialogs.add(dialog);
            } else {

                ConfirmDialog dialog = new ConfirmDialog(QRScanActivity.this)
                        .setText("已靠近大门，是否结束游览？")
                        .setConfirmButton("继续使用", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                showFloating();
                            }
                        });
//                        .setCancelButton("结束游览", new ConfirmDialog.OnDialogBtnClickListener() {
//                            @Override
//                            public void onClick(View v, Dialog dialog) {
//                                dialog.dismiss();
//                                if (!AppController.DEBUG) {
//                                    postNotKeepLend();
//                                }
//                            }
//                        });
                cleanDialog();
                dialog.show();
                confirmDialogs.add(dialog);
            }
        } else if (event.getType() == EntryLandscapeEvent.LEAVE) {
            if (AppController.DEBUG) {
                ToastUtils.show(QRScanActivity.this, "onEntryLandscapeEvevt:quite" + event.getLandScape().getName());
            }
            AppContext.getInstance().stopSoundTrack();
            AppController.getInstance().quitLandscape(event.getLandScape());
            HermesEventBus.getDefault().removeAllStickyEvents();
        } else if (event.getType() == EntryLandscapeEvent.UNITY_TURN_BACK) {
            /**
             * 考虑unity切换的情况
             * 1.需要播放音乐
             * 2.需要检查是否靠近门
             */
            //播放介绍音频
            AppContext.getInstance().stopSoundTrack();
            AppContext.getInstance().playSoundTrack(event.getLandScape().getRealSoundTrackPath());
            HermesEventBus.getDefault().removeAllStickyEvents();
        }
    }

    /**
     * 不续借确认
     */
    protected void postNotKeepLend() {
        RequestClient.postNotKeepLend(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(QRScanActivity.this, "网络错误归还失败");
                postNotKeepLend();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JsonResponse<SessionResponse> wrap = JsonResponse.parse(responseString, SessionResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(QRScanActivity.this, wrap.getMsg());
                    //再次执行
                    postNotKeepLend();
                    return;
                }
                //关闭页面
                AppController.getInstance().didAfterNotUsing();
            }
        });
    }

    /************
     * Fragment控制
     *************/
    private void initFragment() {
        mChildFragments = new SparseArray<>();

        //scan
        Fragment scan = getSupportFragmentManager().findFragmentByTag(String.valueOf(PAGE_SCAN));
        if (scan == null) {
            scan = QRScanFragment.newInstance();
        }
        mChildFragments.append(PAGE_SCAN, scan);

        //web
        Fragment web = getSupportFragmentManager().findFragmentByTag(String.valueOf(PAGE_WEB));
        if (web == null) {
            web = QRWebFragment.newInstance();
        }
        mChildFragments.append(PAGE_WEB, web);

    }

    private void hideFragments() {
        for (int i = 0; i < mChildFragments.size(); i++) {
            Fragment f = mChildFragments.valueAt(i);
            if (f.isAdded())
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(f)
                        .commit();
        }

    }

    public void switchPage(int position) {
        mCurIndex = position;
        hideFragments();
        Fragment f = mChildFragments.valueAt(position);
        if (!f.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, f, String.valueOf(mChildFragments.keyAt(position)))
                    .show(mChildFragments.get(position))
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(mChildFragments.get(position))
                    .commit();
        }
    }

    public void showWeb(String url) {
        ((QRWebFragment) mChildFragments.get(PAGE_WEB)).setUrl(url);
        switchPage(PAGE_WEB);
    }

    public void showFloating() {
        if (mCurIndex == PAGE_SCAN) {
            ((QRScanFragment) mChildFragments.get(PAGE_SCAN)).addFloatingViews();
        }
    }

    public void hideFloating() {
        if (mCurIndex == PAGE_SCAN) {
            ((QRScanFragment) mChildFragments.get(PAGE_SCAN)).removeFloatingViews();
        }
    }

    protected void cleanDialog() {
        for (ConfirmDialog dialog : confirmDialogs) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
        confirmDialogs.clear();
    }
}

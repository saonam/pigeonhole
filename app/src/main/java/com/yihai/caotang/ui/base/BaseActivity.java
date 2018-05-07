package com.yihai.caotang.ui.base;

import android.app.Dialog;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
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

import cz.msebera.android.httpclient.Header;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public abstract class BaseActivity extends AbstractActivity {

    public boolean isTimeLimitNotified = false;
    protected ArrayList<ConfirmDialog> confirmDialogs = new ArrayList<>();  //景点提示框

    /*******************
     * 出租业务控制
     *
     * 即将到达结束时间
     * 为了在每个窗口都能提示，设计为在baseactivity中触发
     * 只要没确认，就每弹出一个窗口确认一次
     *******************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeLimitNotifyEvent(TimeLimitNotifyEvent event) {
        //检查是否已经用户确认
        if (SessionManager.getInstance().getSession().isTimeLimitedTriggered()) {
            return;
        }

        //弹窗提示
        if (!isTimeLimitNotified) {
            ConfirmDialog dialog = new ConfirmDialog(BaseActivity.this)
                    .setText("使用时间即将结束是否继续使用")
                    .setConfirmButton("继续使用", new ConfirmDialog.OnDialogBtnClickListener() {
                        @Override
                        public void onClick(View v, Dialog dialog) {
                            dialog.dismiss();
                            SessionManager.getInstance().getSession().setTimeLimitedTriggered(true);
                        }
                    })
                    .setCancelButton("结束", new ConfirmDialog.OnDialogBtnClickListener() {
                        @Override
                        public void onClick(View v, Dialog dialog) {
                            dialog.dismiss();
                            postNotKeepLend();
                            SessionManager.getInstance().getSession().setTimeLimitedTriggered(true);
                        }
                    });
            cleanDialog();
            dialog.show();
            confirmDialogs.add(dialog);
            isTimeLimitNotified = true;
        }
    }

    /****************
     * 电量通知
     *
     ****************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBatteryNotifyEvent(BatteryNotifyEvent event) {

        switch (event.getEvent()) {
//            case BatteryNotifyEvent.EVENT_BATTERY_LOCK:
//                //不再使用状态提醒
//                SessionManager.getInstance().getSession().setUsing(false);
//                //跳转至锁屏界面
//                startActivity(AboutUsActivity.newIntent(AboutUsActivity.MODE_LOW_BATTERY));
//                //关闭页面
//                AppController.getInstance().finishBackgroundActivity();
//                break;
            case BatteryNotifyEvent.EVENT_BATTERY_WARNING:
                //是否在使用
                if (!SessionManager.getInstance().getSession().isUsing()) {
                    return;
                }
                ConfirmDialog dialog = new ConfirmDialog(BaseActivity.this)
                        .setText("设备电量低，如继续使用到电量下限则锁定设备，如继续使用则前往替换点替换设备，是否替换")
                        .setConfirmButton("前往替换", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                //不再使用状态提醒
                                SessionManager.getInstance().getSession().setUsing(false);
                                //跳转锁屏
                                startActivity(AboutUsActivity.newIntent(AboutUsActivity.MODE_LOW_BATTERY));
                                //结束页面
                                AppController.getInstance().finishBackgroundActivity();
                            }
                        })
                        .setCancelButton("继续使用", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
                cleanDialog();
                dialog.show();
                confirmDialogs.add(dialog);
                break;
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEntryLandscapeEvevt(final EntryLandscapeEvent event) {
        if (event.getType() == EntryLandscapeEvent.ENTRY) {
            if (!AppController.getInstance().isNearGate(event.getLandScape())) {
                ConfirmDialog dialog = new ConfirmDialog(BaseActivity.this)
                        .setText("已靠近" + event.getLandScape().getName() + "，是否进入浏览模式")
                        .setCancelButton("放弃进入", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setConfirmButton("开始浏览", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                AppController.getInstance().enterLandscape(event.getLandScape());
                            }
                        });
                cleanDialog();
                dialog.show();
                confirmDialogs.add(dialog);
            } else {
                ConfirmDialog dialog = new ConfirmDialog(BaseActivity.this)
                        .setText("靠近大门，如离开请归还设备")
                        .setConfirmButton("继续使用", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
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
                ToastUtils.show(BaseActivity.this, "onEntryLandscapeEvevt:quite" + event.getLandScape().getName());
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
                ToastUtils.show(BaseActivity.this, "网络错误归还失败");
                postNotKeepLend();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JsonResponse<SessionResponse> wrap = JsonResponse.parse(responseString, SessionResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(BaseActivity.this, wrap.getMsg());
                    //再次执行
                    postNotKeepLend();
                    return;
                }
                //关闭页面
                AppController.getInstance().didAfterNotUsing();
            }
        });
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

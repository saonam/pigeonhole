package com.yihai.caotang.ui.manage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.loopj.android.http.TextHttpResponseHandler;
import com.yihai.caotang.AppController;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.client.RequestClient;
import com.yihai.caotang.data.CmdResponse;
import com.yihai.caotang.data.JsonResponse;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.data.session.SessionResponse;
import com.yihai.caotang.data.sysinfo.SysInfoManager;
import com.yihai.caotang.event.ActionReceiveEvent;
import com.yihai.caotang.ui.MainActivity;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.ui.guide.GuideMainActivity;
import com.yihai.caotang.utils.DateUtils;
import com.yihai.caotang.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class LockScreenActivity extends BaseActivity {
    private static final long CMD_IGNORE_DURATION = 20000L;
    private RequestHandler mHandler;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                LockScreenActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        ButterKnife.bind(LockScreenActivity.this);
        mHandler = new RequestHandler(LockScreenActivity.this);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionReceive(ActionReceiveEvent event) {
        CmdResponse json = CmdResponse.parse(event.getJson(), CmdResponse.class);
        //检查命令推送时间,大于20秒的命令忽略
        long duration = DateUtils.tillNowOfSec(json.getTimestamp());
        if (duration > CMD_IGNORE_DURATION) {
            return;
        }
        switch (event.getAction()) {
            case ActionReceiveEvent.ACTION_LEND:
                //弹窗确认是否租借
                new ConfirmDialog(LockScreenActivity.this)
                        .setText("确认租借？")
                        .setConfirmButton("确认", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                mHandler.obtainMessage(RequestHandler.MSG_JPUSH_CMD_LEND).sendToTarget();
                            }
                        })
                        .show();
                break;
            case ActionReceiveEvent.ACTION_CHANGE:
                //替换
                new ConfirmDialog(LockScreenActivity.this)
                        .setText("确认更换？")
                        .setConfirmButton("确认", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                mHandler.obtainMessage(RequestHandler.MSG_JPUSH_CMD_CHANGE).sendToTarget();
                            }
                        })
                        .show();
        }
    }

    /**
     * 确认租借
     */
    private void confirmLend() {
        showLoading();
        RequestClient.postLend(SysInfoManager.getSysInfo().getPadIdStr(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(LockScreenActivity.this, "网络问题请重试");
                dismissLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dismissLoading();
                JsonResponse<SessionResponse> wrap = JsonResponse.parse(responseString, SessionResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(LockScreenActivity.this, wrap.getMsg());
                    return;
                }

                //租借确认完毕，app准备
                //更新session
                SessionManager.getInstance().update(wrap.getData().parse());
                //跳转至动画播放
                startActivity(GuideMainActivity.newIntent());
                //关闭页面
                finish();
                //启动controller
                AppController.getInstance().didAfterLend();
            }
        });
    }

    /**
     * 确认更换
     */
    private void confirmChange() {
        showLoading();
        RequestClient.postChange(SysInfoManager.getSysInfo().getPadIdStr(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(LockScreenActivity.this, "网络问题请重试");
                dismissLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dismissLoading();
                JsonResponse<SessionResponse> wrap = JsonResponse.parse(responseString, SessionResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(LockScreenActivity.this, wrap.getMsg());
                    return;
                }
                //更换确认完毕，app准备
                //更新session
                SessionManager.getInstance().update(wrap.getData().parse());
                startActivity(MainActivity.newIntent());
                AppController.getInstance().didAfterLend();
                SessionManager.getInstance().getSession().setUsing(true);
            }
        });
    }

    /**
     * 租借
     *
     * @param view
     */
    @OnClick(R.id.btn_lend)
    void onBtnLendClick(View view) {
        AppContext.getInstance().playEffect();
        ToastUtils.show(LockScreenActivity.this, "请将设备放置于RFID读卡器上，然后在后台管理系统中操作");
//        new ConfirmDialog(LockScreenActivity.this)
//                .setText("将设备放置于RFID读卡器上，或在后台管理系统中租借")
//                .setConfirmButton("确认租借", new ConfirmDialog.OnDialogBtnClickListener() {
//                    @Override
//                    public void onClick(View v, Dialog dialog) {
//                        dialog.dismiss();
//                        mHandler.obtainMessage(RequestHandler.MSG_REQUEST_LEND).sendToTarget();
//                    }
//                })
//                .setCancelButton("取消", new ConfirmDialog.OnDialogBtnClickListener() {
//                    @Override
//                    public void onClick(View v, Dialog dialog) {
//                        dialog.dismiss();
//                    }
//                }).show();
    }

    /**
     * [DEPRECATED]
     * 归还
     *
     * @param view
     */
    @OnClick(R.id.btn_return)
    void onBtnReturnClick(View view) {
        AppContext.getInstance().playEffect();
        ToastUtils.show(LockScreenActivity.this, "请将设备放置于RFID读卡器上，然后在后台管理系统中操作");
    }

    /**
     * 升级
     *
     * @param view
     */
    @OnClick(R.id.btn_update)
    void onBtnUpdateClick(View view) {
        AppContext.getInstance().playEffect();
        startActivity(UpdateActivity.newIntent());
    }

    /**
     * [DEPRECATED]
     * 替换
     *
     * @param view
     */
    @OnClick(R.id.btn_replace)
    void onBtnReplaceClick(View view) {
        AppContext.getInstance().playEffect();
        ToastUtils.show(LockScreenActivity.this, "请将设备放置于RFID读卡器上，然后在后台管理系统中操作");
    }

    /**
     * 设置页
     * 包括更新
     *
     * @param view
     */
    @OnClick(R.id.btn_setting)
    void onBtnConfigClick(View view) {
        AppContext.getInstance().playEffect();
        startActivity(ConfigAdminActivity.newIntent());
    }

    /**
     * 流程控制
     */
    private class RequestHandler extends Handler {

        private static final int MSG_JPUSH_CMD_LEND = 1;      //JPUSH命令租借
        private static final int MSG_JPUSH_CMD_CHANGE = 2;    //JPUSH命令更换

        private static final int MSG_REQUEST_LEND = 3;      //PAD强制请求租借
        private static final int MSG_REQUEST_CHANGE = 4;    //PAD强制请求更换
        private static final int MSG_REQUEST_RETURN = 5;      //PAD强制请求归还

        WeakReference<LockScreenActivity> mAct;

        public RequestHandler(LockScreenActivity act) {
            mAct = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_JPUSH_CMD_LEND:
                    //JPUSH出租请求确认
                    mAct.get().confirmLend();
                    break;
                case MSG_JPUSH_CMD_CHANGE:
                    //更换请求
                    mAct.get().confirmChange();
                    break;
                case MSG_REQUEST_LEND:
                    //强制出租
                    mAct.get().confirmLend();
                    break;
                case MSG_REQUEST_CHANGE:
                    //强制替换
                    mAct.get().confirmChange();
                    break;
                case MSG_REQUEST_RETURN:
                    //强制归还
                    break;
            }
        }
    }
}

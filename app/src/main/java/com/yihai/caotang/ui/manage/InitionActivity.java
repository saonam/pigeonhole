package com.yihai.caotang.ui.manage;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.R;
import com.yihai.caotang.client.BaseClient;
import com.yihai.caotang.client.RequestClient;
import com.yihai.caotang.data.DatabaseManager;
import com.yihai.caotang.data.JsonResponse;
import com.yihai.caotang.data.antique.AntiqueManager;
import com.yihai.caotang.data.game.GameManager;
import com.yihai.caotang.data.landscape.BorderManager;
import com.yihai.caotang.data.landscape.LandScapeManager;
import com.yihai.caotang.data.session.Session;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.data.session.SessionResponse;
import com.yihai.caotang.data.soundtrack.SoundTrackManager;
import com.yihai.caotang.data.sysinfo.SysInfo;
import com.yihai.caotang.data.sysinfo.SysInfoManager;
import com.yihai.caotang.data.sysinfo.SysInfoResponse;
import com.yihai.caotang.data.sysinfo.VersionResponse;
import com.yihai.caotang.data.task.TaskManager;
import com.yihai.caotang.event.ActionReceiveEvent;
import com.yihai.caotang.event.RegistrationReceiveEvent;
import com.yihai.caotang.ui.MainActivity;
import com.yihai.caotang.ui.base.AbstractActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.ui.guide.GuideMainActivity;
import com.yihai.caotang.utils.FileUtils;
import com.yihai.caotang.utils.SysInfoUtils;
import com.yihai.caotang.utils.ToastUtils;
import com.yihai.caotang.utils.UnZipUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;
import me.zhouzhuo.zzhorizontalprogressbar.ZzHorizontalProgressBar;

public class InitionActivity extends AbstractActivity {

    @Bind(R.id.progressbar)
    ZzHorizontalProgressBar progressBar;

    private RequestHandler mHandler;
    private int mOverTimes = 0;
    private boolean isMatched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inition);
        ButterKnife.bind(this);

        mHandler = new RequestHandler(InitionActivity.this);
        if (AppController.DEBUG) {
            /**
             * 演示版初始化
             */
            //数据库
            DatabaseManager.init(AppContext.getInstance());
            BorderManager.getInstance().warnUp();
            AntiqueManager.getInstance().warnUp();
            GameManager.getInstance().warnUp();
            LandScapeManager.getInstance().warnUp();
            SoundTrackManager.getInstance().warnUp();
            TaskManager.getInstance().warnUp();

            SessionManager.getInstance().reset();
            //调整屏幕亮度
            SysInfoUtils.changeAppBrightness(SessionManager.getInstance().getSession().getBrightness(), InitionActivity.this);
            //跳转首页
            startActivity(GuideMainActivity.newIntent());
            finish();
            //设备监控初始化
            AppController.getInstance().didAfterLend();
            Log.d("regID：", JPushInterface.getRegistrationID(InitionActivity.this));
        } else {
            /**
             * 部署版初始化
             */
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //检查数据是否存在
                    if (!DatabaseManager.exist()) {
                        initDownLoadInfo();
                        return;
                    }

                    //检查配对、初始化、握手
                    initSys();
                }
            }, 1000);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionReceive(ActionReceiveEvent event) {
        Logger.d(event.getAction(), event.getJson());
        //配对推送
        if (event.getAction().equals(ActionReceiveEvent.ACTION_MATCH)) {
            //停止重试
            isMatched = true;
            mHandler.removeMessages(RequestHandler.MSG_RETRY_MATCH);
            //解析json
            JsonResponse<SysInfoResponse> wrap = JsonResponse.parse(event.getJson(), SysInfoResponse.class);
            SysInfo info = SysInfoManager.getSysInfo();
            info.setRfidId(wrap.getData().getRfid_id());
            info.setMatched(true);

            //保存记录
            SysInfoManager.setSysInfo(info);
            ToastUtils.show(InitionActivity.this, "配对成功,请稍等");

            //进入下一步,握手
            mHandler.obtainMessage(RequestHandler.MSG_REQUEST_HAND_SHAKE).sendToTarget();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegistrationReceive(RegistrationReceiveEvent event) {
        Logger.d(event.getRegId());
    }

    /**
     * 初始化数据
     */
    private void initDownLoadInfo() {
        showLoading();
        RequestClient.getVersionCheck(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                dismissLoading();
                new ConfirmDialog(InitionActivity.this)
                        .setText("网络错误，请重试！")
                        .disableClose()
                        .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                initDownLoadInfo();
                            }
                        }).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dismissLoading();
                //解析数据
                JsonResponse<VersionResponse> wrap = JsonResponse.parse(responseString, VersionResponse.class);
                if (wrap.getResult() == 0) {
                    new ConfirmDialog(InitionActivity.this)
                            .setText(wrap.getMsg())
                            .disableClose()
                            .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                                @Override
                                public void onClick(View v, Dialog dialog) {
                                    initDownLoadInfo();
                                }
                            }).show();
                    return;
                }
                downloadData(wrap.getData().getDataver());
            }
        });
    }

    /**
     * 下载数据
     */
    private void downloadData(final String ver) {
        showProgressDialog();

        //创建文件夹
        try {
            FileUtils.createSDDir();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //开始下载
        String url = BaseClient.URL_DOWNLOAD_PATH + ver + ".zip";
        final String fileName = FileUtils.SDPATH + ver + ".zip";
        FileDownloader.setup(InitionActivity.this);
        FileDownloader.getImpl().create(url)
                .setPath(fileName)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        updateProgress(soFarBytes, totalBytes);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        ToastUtils.show(InitionActivity.this, "网络问题，开始重试");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        dismissProgress();
                        showLoading();
                        //下载成功解压
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UnZipUtils unzip = new UnZipUtils(FileUtils.SDPATH + ver + ".zip", FileUtils.SDPATH);
                                if (unzip.unzipFolder()) {
                                    InitionActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.show(InitionActivity.this, "解析成功");
                                            //删除压缩包
                                            FileUtils.delFile(fileName);
                                            //开始初始化系统
                                            mHandler.obtainMessage(RequestHandler.MSG_INIT_ENVIR).sendToTarget();
                                        }
                                    });
                                } else {
                                    InitionActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.show(InitionActivity.this, "解析失败");
                                        }
                                    });
                                }
                            }
                        }).start();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        dismissProgress();
                        new ConfirmDialog(InitionActivity.this)
                                .setText(e.getMessage())
                                .disableClose()
                                .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                                    @Override
                                    public void onClick(View v, Dialog dialog) {
                                        downloadData(ver);
                                    }
                                }).show();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Logger.d("warning");
                    }
                }).start();
    }

    /**
     * 初始化系统
     */
    private void initSys() {

        //数据库缓存热机,这个顺序不能乱
        AntiqueManager.getInstance().warnUp();
        BorderManager.getInstance().warnUp();
        GameManager.getInstance().warnUp();
        LandScapeManager.getInstance().warnUp();
        SoundTrackManager.getInstance().warnUp();
        TaskManager.getInstance().warnUp();

        //检查初始化、配对
        checkInitionAndMatch();
    }

    /**
     * 检查是否需要初始化或配对
     * 如果不需要，则握手
     */
    private void checkInitionAndMatch() {

        SysInfo info = SysInfoManager.getSysInfo();

        //如果初始化并配对，则开始握手
        if (info != null && info.isInited() && info.isMatched()) {
            mHandler.obtainMessage(RequestHandler.MSG_REQUEST_HAND_SHAKE).sendToTarget();
            return;
        }

        //如果没有初始化
        if (info == null || !info.isInited()) {
            new ConfirmDialog(InitionActivity.this)
                    .setText("PAD未初始化，是否开始初始化")
                    .disableClose()
                    .setConfirmButton("开始", new ConfirmDialog.OnDialogBtnClickListener() {
                        @Override
                        public void onClick(View v, Dialog dialog) {
                            dialog.dismiss();
                            mHandler.obtainMessage(RequestHandler.MSG_REQUEST_INIT).sendToTarget();
                        }
                    })
                    .show();
            return;
        }

        //如果没有配对
        if (!info.isMatched()) {
            new ConfirmDialog(InitionActivity.this)
                    .setText("PAD未配对，是否开始配对")
                    .disableClose()
                    .setConfirmButton("开始", new ConfirmDialog.OnDialogBtnClickListener() {
                        @Override
                        public void onClick(View v, Dialog dialog) {
                            dialog.dismiss();
                            mHandler.obtainMessage(RequestHandler.MSG_REQUEST_MATCH).sendToTarget();
                        }
                    }).show();
        }
    }

    /**
     * 初始化平板
     */
    private void postInitPad() {
        String regId = JPushInterface.getRegistrationID(InitionActivity.this);
        final String imei = SysInfoUtils.getImeiNumber(InitionActivity.this);
        final String appVer = SysInfoUtils.getAppVersion(InitionActivity.this);
        //初始化
        RequestClient.postInitDevice(regId, imei, appVer, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(InitionActivity.this, "初始化后端错误,重试中..");
                mOverTimes++;
                checkInitionAndMatch();
                dismissLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dismissLoading();
                //解析数据
                JsonResponse<SysInfoResponse> wrap = JsonResponse.parse(responseString, SysInfoResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(InitionActivity.this, wrap.getMsg());
                    return;
                }
                //解析数据
                SysInfo info = wrap.getData().parse();
                info.setAppVersion(appVer);
                info.setDataVersion(SysInfoUtils.getDataVersion());
                info.setImei(imei);
                info.setInited(true);
                //保存记录
                SysInfoManager.setSysInfo(info);
                //再次检查
                checkInitionAndMatch();
            }
        });
    }

    /**
     * 配对平板
     */
    private void postMatchPad() {
        showLoading();
        SysInfo info = SysInfoManager.getSysInfo();
        //如果没有配对
        RequestClient.postMatchDevice(String.format("%d", info.getPadId()), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                ToastUtils.show(InitionActivity.this, "pad配对失败！");
                checkInitionAndMatch();
                dismissLoading();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //解析数据
                JsonResponse<SysInfoResponse> wrap = JsonResponse.parse(responseString, SysInfoResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(InitionActivity.this, wrap.getMsg());
                    checkInitionAndMatch();
                    dismissLoading();
                    return;
                }
                //等待jpush推送
                //若失败，启动重新配对检查
                mHandler.obtainMessage(RequestHandler.MSG_RETRY_MATCH).sendToTarget();
            }
        });
    }

    /**
     * 与后端握手
     * 1.如果状态是出租的，就更新session，跳转到首页
     * 2.如果状态是没出租的，就到锁屏页
     */
    private void getHandShake() {
        RequestClient.getHandShake(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                dismissLoading();
                ToastUtils.show(InitionActivity.this, "握手失败");
                mOverTimes++;
                //重试
                if (mOverTimes < 5) {
                    getHandShake();
                } else {
                    new ConfirmDialog(InitionActivity.this)
                            .setText("请检查好网络再重试")
                            .disableClose()
                            .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                                @Override
                                public void onClick(View v, Dialog dialog) {
                                    getHandShake();
                                    mOverTimes = 0;
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dismissLoading();
                //解析数据
                JsonResponse<SessionResponse> wrap = JsonResponse.parse(responseString, SessionResponse.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(InitionActivity.this, wrap.getMsg());
                    //重试
                    getHandShake();
                    return;
                }

                //根据握手记录检查pad状态
                checkPadStatus(wrap.getData());
            }
        });

    }

    /**
     * 检查pad状态
     */
    private void checkPadStatus(SessionResponse response) {
        //TODO 当心初始化的时候有可能收到推送就跳转页面了，但实际上检查的请求才执行到这里，可能需要给一个标识位？
        if (InitionActivity.this == null) {
            return;
        }
        switch (response.getStatus()) {
            case Session.PAD_STATUS_FREE://闲置,进入管理员登陆页面
                startActivity(SplashActivity.newIntent());
                finish();
                break;
            case Session.PAD_STATUS_LENGDING://租借中,直接进入首页
                //更新session
                SessionManager.getInstance().update(response.parse());
                SessionManager.getInstance().getSession().setUsing(true);
                //启动监控
                AppController.getInstance().didAfterLend();
                //跳转首页
                startActivity(MainActivity.newIntent());
                finish();
                break;
            default:
                //不是闲置或租借中，其他都是无法使用的状态
                //弹窗提示错误
                new ConfirmDialog(InitionActivity.this)
                        .setText(Session.PAD_STATUS.get(response.getStatus()))
                        .show();
                break;
        }
    }

    /**
     * 启动重新配对检查
     */
    private void reMatchPad() {
        dismissLoading();
        if (!isMatched) {
            new ConfirmDialog(InitionActivity.this)
                    .setText("PAD配对失败,未收到数据")
                    .disableClose()
                    .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                        @Override
                        public void onClick(View v, Dialog dialog) {
                            mHandler.obtainMessage(RequestHandler.MSG_REQUEST_MATCH).sendToTarget();
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * 流程控制
     */
    private class RequestHandler extends Handler {

        private static final int MSG_INIT_ENVIR = 1;
        private static final int MSG_REQUEST_INIT = 2;
        private static final int MSG_REQUEST_MATCH = 3;
        private static final int MSG_REQUEST_HAND_SHAKE = 4;
        private static final int MSG_RETRY_MATCH = 5;

        WeakReference<InitionActivity> mAct;

        public RequestHandler(InitionActivity act) {
            mAct = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_INIT_ENVIR://下载数据后初始化
                    DatabaseManager.init(AppContext.getInstance());
                    mAct.get().initSys();
                    break;
                case MSG_REQUEST_INIT: //初始化
                    showLoading();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAct.get().postInitPad();
                        }
                    }, 4000);
                    progressBar.setProgress(50);
                    break;
                case MSG_REQUEST_MATCH://配对请求
                    mAct.get().postMatchPad();
                    progressBar.setProgress(70);
                    break;
                case MSG_REQUEST_HAND_SHAKE://握手
                    mAct.get().getHandShake();
                    progressBar.setProgress(100);
                    break;
                case MSG_RETRY_MATCH://重试握手
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAct.get().reMatchPad();
                        }
                    }, 15000L);
                    break;
            }
        }
    }
}

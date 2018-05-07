package com.yihai.caotang.ui.manage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.logger.Logger;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.client.BaseClient;
import com.yihai.caotang.client.RequestClient;
import com.yihai.caotang.data.JsonResponse;
import com.yihai.caotang.data.sysinfo.SysInfoManager;
import com.yihai.caotang.data.sysinfo.VersionResponse;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.ui.dialog.ProgressDialog;
import com.yihai.caotang.utils.DownLoadUtils;
import com.yihai.caotang.utils.FileUtils;
import com.yihai.caotang.utils.SysInfoUtils;
import com.yihai.caotang.utils.ToastUtils;
import com.yihai.caotang.utils.UnZipUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class UpdateActivity extends BaseActivity {
    @Bind(R.id.tv_app_ver)
    TextView tvAppVer;

    @Bind(R.id.tv_data_ver)
    TextView tvDataVer;

    private RequestHandler mHandler;
    private ProgressDialog progressDialog;
    private boolean isDownloading = false;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                UpdateActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        mHandler = new RequestHandler(UpdateActivity.this);
        tvAppVer.setText(String.format("APP版本:%s", SysInfoManager.getSysInfo().getAppVersion()));
        tvDataVer.setText(String.format("数据版本:%s", SysInfoManager.getSysInfo().getDataVersion()));
    }

    @OnClick(R.id.btn_check)
    void onCheckBtnClick(View view) {
        AppContext.getInstance().playEffect();
        checkVersion();
    }

    @OnClick(R.id.iv_back)
    void onBackBtnClick(View view) {
        if (isDownloading) {
            new ConfirmDialog(UpdateActivity.this)
                    .setText("正在更新，确定退出？")
                    .setCancelButton("取消", new ConfirmDialog.OnDialogBtnClickListener() {
                        @Override
                        public void onClick(View v, Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setConfirmButton("退出", new ConfirmDialog.OnDialogBtnClickListener() {
                        @Override
                        public void onClick(View v, Dialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    /**
     * 初始化数据
     */
    private void checkVersion() {
        RequestClient.getVersionCheck(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                dismissLoading();
                new ConfirmDialog(UpdateActivity.this)
                        .setText("网络错误，请重试！")
                        .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                checkVersion();
                            }
                        }).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //解析数据
                final JsonResponse<VersionResponse> wrap = JsonResponse.parse(responseString, VersionResponse.class);
                if (wrap.getResult() == 0) {
                    new ConfirmDialog(UpdateActivity.this)
                            .setText(wrap.getMsg())
                            .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                                @Override
                                public void onClick(View v, Dialog dialog) {
                                    dialog.dismiss();
                                    checkVersion();
                                }
                            }).show();
                    return;
                }

                //检查data版本
                String dataVer = SysInfoUtils.getDataVersion();
                if (!wrap.getData().getDataver().equals(dataVer)) {
                    ToastUtils.show(UpdateActivity.this, "有新的数据包,开始更新");

                    Bundle bundle = new Bundle();
                    bundle.putString("data", wrap.getData().getDataver());
                    Message msg = mHandler.obtainMessage(RequestHandler.MSG_REQUEST_UPDATE_DATA);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    return;
                }

                //检查App版本
                String appVer = SysInfoUtils.getAppVersion(UpdateActivity.this);
                if (!wrap.getData().getAppver().equals(appVer)) {
                    ToastUtils.show(UpdateActivity.this, "有新的APP安装包,开始更新");
                    Bundle bundle = new Bundle();
                    bundle.putString("data", wrap.getData().getAppver());
                    Message msg = mHandler.obtainMessage(RequestHandler.MSG_REQUEST_UPDATE_APP);
                    msg.setData(bundle);
                    msg.sendToTarget();
                    return;
                }

                //没有更新提示
                ToastUtils.show(UpdateActivity.this, "数据及APP均为最新版本");
            }
        });
    }

    /**
     * 下载数据
     */
    protected void updateData(final String ver) {
        isDownloading = true;
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
        FileDownloader.setup(UpdateActivity.this);
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
                        ToastUtils.show(UpdateActivity.this, "网络问题，开始重试");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        dismissProgress();
                        //下载成功解压
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                UnZipUtils unzip = new UnZipUtils(FileUtils.SDPATH + ver + ".zip", FileUtils.SDPATH);
                                if (unzip.unzipFolder()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.show(UpdateActivity.this, "数据包解析成功");
                                            //删除压缩包
                                            FileUtils.delFile(fileName);
                                            //下一步继续检验
                                            checkVersion();
                                            isDownloading = false;
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
                        progressDialog.dismiss();
                        new ConfirmDialog(UpdateActivity.this)
                                .setText(e.getMessage())
                                .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                                    @Override
                                    public void onClick(View v, Dialog dialog) {
                                        updateData(ver);
                                        isDownloading = false;
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
     * 下载Apk
     */
    protected void updateApk(final String ver) {
        isDownloading = true;
        showProgressDialog();
        //创建文件夹
        try {
            FileUtils.createSDDir();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //开始下载
        String url = BaseClient.URL_DOWNLOAD_PATH + ver + ".apk";
        final String fileName = FileUtils.SDPATH + ver + ".apk";
        FileDownloader.setup(UpdateActivity.this);
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
                        ToastUtils.show(UpdateActivity.this, "网络问题，开始重试");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        dismissProgress();
                        ToastUtils.show(UpdateActivity.this, "开始安装..");
                        DownLoadUtils.installApk(UpdateActivity.this, fileName);
                        isDownloading = false;
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        dismissProgress();
                        new ConfirmDialog(UpdateActivity.this)
                                .setText(e.getMessage())
                                .setConfirmButton("重试", new ConfirmDialog.OnDialogBtnClickListener() {
                                    @Override
                                    public void onClick(View v, Dialog dialog) {
                                        updateApk(ver);
                                        isDownloading = false;
                                    }
                                }).show();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Logger.d("warning");
                    }
                }).start();
    }

    private class RequestHandler extends Handler {
        private static final int MSG_REQUEST_UPDATE_DATA = 1;
        private static final int MSG_REQUEST_UPDATE_APP = 2;

        private WeakReference<UpdateActivity> mAct;

        public RequestHandler(UpdateActivity activity) {
            this.mAct = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REQUEST_UPDATE_DATA:
                    mAct.get().updateData(msg.getData().getString("data"));
                    break;
                case MSG_REQUEST_UPDATE_APP:
                    mAct.get().updateApk(msg.getData().getString("data"));
                    break;

            }
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null && getFragmentManager() != null) {
            progressDialog = new ProgressDialog(UpdateActivity.this);
            progressDialog.show();
        }
    }

    public void dismissProgress() {
        if (progressDialog != null && getFragmentManager() != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void updateProgress(int cur, int max) {
        if (progressDialog == null && getFragmentManager() != null) {
            progressDialog = new ProgressDialog(UpdateActivity.this);
            progressDialog.show();
        }
        progressDialog.setProgress(cur, max);
    }
}

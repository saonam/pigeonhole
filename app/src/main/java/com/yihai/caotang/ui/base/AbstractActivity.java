package com.yihai.caotang.ui.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.yihai.caotang.ui.dialog.LoadingDialog;
import com.yihai.caotang.ui.dialog.ProgressDialog;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public abstract class AbstractActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initScreen();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            System.out.println("ORIENTATION_LANDSCAPE="
                    + Configuration.ORIENTATION_LANDSCAPE);// 当前为横屏

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            System.out.println("ORIENTATION_PORTRAIT="
                    + Configuration.ORIENTATION_PORTRAIT);// 当前为竖屏

        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

    /**
     * 强制全屏
     */
    private void initScreen() {

        //隐藏导航栏\状态栏\标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //屏幕常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    /***************
     * 控制窗口
     **************/
    public void showLoading() {
        if (loadingDialog == null && getSupportFragmentManager() != null) {
            loadingDialog = LoadingDialog.build(this);
            loadingDialog.show();
        }
    }

    public void dismissLoading() {
        if (loadingDialog != null && getSupportFragmentManager() != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null && getSupportFragmentManager() != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.show();
        }
    }

    public void dismissProgress() {
        if (progressDialog != null && getSupportFragmentManager() != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void updateProgress(int cur, int max) {
        if (progressDialog == null && getSupportFragmentManager() != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.show();
        }
        progressDialog.setProgress(cur, max);
    }
}

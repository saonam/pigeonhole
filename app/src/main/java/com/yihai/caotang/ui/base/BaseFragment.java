package com.yihai.caotang.ui.base;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.yihai.caotang.event.ActionReceiveEvent;
import com.yihai.caotang.event.MapEvent;
import com.yihai.caotang.ui.dialog.LoadingDialog;
import com.yihai.caotang.ui.dialog.ProgressDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class BaseFragment extends Fragment {

    private LoadingDialog loadingDialog;
    private ProgressDialog progressDialog;

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
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapEvent(MapEvent event) {
        Log.d("BaseActivity", "triggered");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionEvent(ActionReceiveEvent event) {
        Log.d("BaseActivity", "triggered");
    }

    public void showLoading() {
        if (loadingDialog == null && getActivity() != null) {
            loadingDialog = LoadingDialog.build(getActivity());
            loadingDialog.show();
        }
    }

    public void dismissLoading() {
        if (loadingDialog != null && getActivity() != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null && getActivity() != null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.show();
        }
    }

    public void dismissProgress() {
        if (progressDialog != null && getActivity() != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void updateProgress(int cur, int max) {
        if (progressDialog == null && getActivity() != null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.show();
        }
        progressDialog.setProgress(cur, max);
    }
}

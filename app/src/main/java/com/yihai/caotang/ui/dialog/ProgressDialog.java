package com.yihai.caotang.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.yihai.caotang.R;
import com.yihai.caotang.utils.ViewUtils;

import me.zhouzhuo.zzhorizontalprogressbar.ZzHorizontalProgressBar;

import static android.view.Gravity.CENTER;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class ProgressDialog extends Dialog {
    private View mRoot;
    private ZzHorizontalProgressBar mProgressBar;

    public ProgressDialog(Context context) {
        super(context, R.style.TransparentBackgroundDialog);
        init();
    }

    private void init() {
        mRoot = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        mProgressBar = (ZzHorizontalProgressBar) mRoot.findViewById(R.id.progressbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mRoot);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = ViewUtils.dip2px(getContext(), 583);
        lp.height = ViewUtils.dip2px(getContext(), 294);
        lp.gravity = CENTER;
        getWindow().setAttributes(lp);
        setCancelable(false);  // 设置当返回键按下是否关闭对话框
        setCanceledOnTouchOutside(false);  // 设置当点击对话框以外区域是否关闭对话框
    }

    public void setProgress(int cur, int max) {
        mProgressBar.setMax(max);
        mProgressBar.setProgress(cur);
    }
}

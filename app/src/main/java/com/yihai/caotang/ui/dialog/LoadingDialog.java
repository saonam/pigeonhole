package com.yihai.caotang.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.yihai.caotang.R;


public final class LoadingDialog extends AlertDialog {

    private ImageView mOuterImg;
    private Animation mAnimation;
    private int mWidth;
    private int mHeight;

    public static LoadingDialog build(Context context) {
        return new LoadingDialog(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, R.style.LoadingDialog);
    }

    public LoadingDialog(Context context, int width, int height) {
        super(context);
        this.mWidth = width;
        this.mHeight = height;
    }

    public LoadingDialog(Context context, int width, int height, int theme) {
        super(context, theme);
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        getWindow().setLayout(mWidth, mHeight);
        getWindow().setGravity(Gravity.CENTER);

        initData();
        initView();
    }

    private void initData() {
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotate);
        setCancelable(false);  // 设置当返回键按下是否关闭对话框
        setCanceledOnTouchOutside(false);  // 设置当点击对话框以外区域是否关闭对话框
    }

    private void initView() {
        mOuterImg = (ImageView) findViewById(R.id.loading_outer_img);
    }

    @Override
    public void show() {
        super.show();
        if (mAnimation != null) {
            mOuterImg.startAnimation(mAnimation);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mAnimation != null) {
            mOuterImg.clearAnimation();
        }
    }
}

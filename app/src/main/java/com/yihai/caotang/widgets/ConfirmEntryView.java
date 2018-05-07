package com.yihai.caotang.widgets;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.ui.landscape.QRScanActivity;
import com.yihai.caotang.ui.landscape.UnityPlayerActivity;
import com.yihai.caotang.ui.media.VideoPlayActivity;
import com.yihai.caotang.utils.ViewUtils;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class ConfirmEntryView extends LinearLayout {

    private final static String TAG = "ConfirmEntryView";

    private View mRoot;
    private TextView mTvText;
    private Button mBtnConfirm;
    private Button mBtnCancel;
    private Button mBtnClose;

    private OnBtnClickListener mCancelListener;
    private OnBtnClickListener mConfirmListener;
    private boolean disableClose=false;

    private LandScape landscape = null;

    public ConfirmEntryView(Context context) {
        this(context, null);
    }

    public ConfirmEntryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ConfirmEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.widget_confirm_entry_view, this, true);
        initView();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initView() {

        mBtnConfirm = (Button) mRoot.findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) mRoot.findViewById(R.id.btn_cancel);
        mTvText = (TextView) mRoot.findViewById(R.id.tv_text);
        mBtnClose = (Button) mRoot.findViewById(R.id.btn_close);

        mBtnConfirm.setVisibility(View.VISIBLE);
        mBtnConfirm.setText("开始浏览");
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmListener.onClick(v,landscape);
                AppContext.getInstance().playEffect();
            }
        });

        mBtnCancel.setVisibility(View.VISIBLE);
        mBtnCancel.setText("放弃进入");
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCancelListener.onClick(v,landscape);
                AppContext.getInstance().playEffect();
            }
        });
        AppContext.getInstance().playEffect();


        mBtnClose.setVisibility(this.disableClose ? View.INVISIBLE : View.VISIBLE);

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCancelListener.onClick(v,landscape);
                AppContext.getInstance().playEffect();
            }
        });

    }

    public void update(LandScape landScape) {
        this.landscape=landScape;
        mTvText.setText("已靠近" + landScape.getName() + "，是否进入浏览模式");
    }

    public void setCancelListener(OnBtnClickListener mCancelListener) {
        this.mCancelListener = mCancelListener;
    }

    public void setConfirmListener(OnBtnClickListener mConfirmListener) {
        this.mConfirmListener = mConfirmListener;
    }

    public interface OnBtnClickListener {
        void onClick(View v,LandScape landScape);
    }
}

package com.yihai.caotang.widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.utils.ViewUtils;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class LeavingTipView extends LinearLayout {

    private final static String TAG = "LeavingTipView";
    private static final int SHOWING_DURATION = 2000;
    private View mRoot;
    private Handler handler;
    private OnLeavingListener listener;

    public LeavingTipView(Context context) {
        this(context, null);
    }

    public LeavingTipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public LeavingTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.widget_confirm_leave_view, this, true);
        handler = new Handler();
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

    }

    public void show() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onLeaving();
                }
            }
        }, SHOWING_DURATION);
    }

    public void setListener(OnLeavingListener listener) {
        this.listener = listener;
    }

    public interface OnLeavingListener {
        void onLeaving();
    }
}

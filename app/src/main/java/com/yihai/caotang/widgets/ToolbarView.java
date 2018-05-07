package com.yihai.caotang.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class ToolbarView extends RelativeLayout {
    private final static String TAG = "ToolbarView";

    private View mRoot;
    private ImageView mIvBack;
    private ImageView mIvTitle;
    private ImageView mIvCenterTitle;

    private boolean isCenter;
    private boolean mIsHidden = false;
    private OnMenuItemClickListener mListener;

    public ToolbarView(Context context) {
        this(context, null);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ToolbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.widget_tool_bar_view, this, true);
        initView();
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
        mIvTitle = (ImageView) mRoot.findViewById(R.id.iv_title);
        mIvBack = (ImageView) mRoot.findViewById(R.id.iv_back);
        mIvCenterTitle = (ImageView) mRoot.findViewById(R.id.iv_title_center);

        mIvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getInstance().playEffect();
                mListener.onClick(v, 1);
            }
        });
    }

    public void setTitle(Drawable drawable) {

        mIvCenterTitle.setVisibility(GONE);
        mIvTitle.setVisibility(VISIBLE);
        mIvTitle.setImageDrawable(drawable);
    }

    public void setCenterTitle(Drawable drawable) {
        mIvCenterTitle.setVisibility(VISIBLE);
        mIvTitle.setVisibility(GONE);
        mIvCenterTitle.setImageDrawable(drawable);
    }

    public void setmListener(OnMenuItemClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnMenuItemClickListener {
        void onClick(View view, int type);
    }
}

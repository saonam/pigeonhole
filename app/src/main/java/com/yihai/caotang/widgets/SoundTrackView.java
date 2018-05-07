package com.yihai.caotang.widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
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
public class SoundTrackView extends LinearLayout {

    private final static String TAG = "ToggleMenu";

    public static final int CLICK_CONFIG = 706;
    public static final int CLICK_TASK = 33;
    public static final int CLICK_REPOSITORY = 996;
    private final static int EXPAND_ANIMATION_DURATION = 300;

    private View mRoot;
    private View mContainerFunc;
    private ImageView mIvToggle;
    private ImageView mIvConfig;
    private ImageView mIvRepository;
    private ImageView mIvTask;

    private boolean mIsHidden = false;
    private OnMenuItemClickListener mListener;

    public SoundTrackView(Context context) {
        this(context, null);
    }

    public SoundTrackView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SoundTrackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.widget_toggle_menu_view, this, true);
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

        mContainerFunc = mRoot.findViewById(R.id.container_func);
        mIvToggle = (ImageView) mRoot.findViewById(R.id.iv_toggle_menu);
        mIvConfig = (ImageView) mRoot.findViewById(R.id.iv_config);
        mIvRepository = (ImageView) mRoot.findViewById(R.id.iv_repository);
        mIvTask = (ImageView) mRoot.findViewById(R.id.iv_task);

        mIvToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(mIsHidden);
                AppContext.getInstance().playEffect();
                mIsHidden = !mIsHidden;
            }
        });
        mIvConfig.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getInstance().playEffect();
                mListener.onClick(v, CLICK_CONFIG);
            }
        });
        mIvRepository.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getInstance().playEffect();
                mListener.onClick(v, CLICK_REPOSITORY);
            }
        });
        mIvTask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getInstance().playEffect();
                mListener.onClick(v, CLICK_TASK);
            }
        });
    }

    public void toggle(boolean isToggled) {
        final float translateDistance = getWidth() - mIvToggle.getWidth() - ViewUtils.dip2px(getContext(), 30);
        if (isToggled) {

            ObjectAnimator.ofFloat(mRoot, "translationX", -1 * translateDistance, 0f)
                    .setDuration(EXPAND_ANIMATION_DURATION)
                    .start();

            ObjectAnimator.ofFloat(mIvToggle, "rotation", 0f, 360f)
                    .setDuration(EXPAND_ANIMATION_DURATION)
                    .start();
        } else {

            ObjectAnimator.ofFloat(mRoot, "translationX", 0f, -1 * translateDistance)
                    .setDuration(EXPAND_ANIMATION_DURATION)
                    .start();
            ObjectAnimator.ofFloat(mIvToggle, "rotation", 360f, 0f)
                    .setDuration(EXPAND_ANIMATION_DURATION)
                    .start();
        }
    }

    public void setOnMenuItemClick(OnMenuItemClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnMenuItemClickListener {
        void onClick(View view, int type);
    }
}

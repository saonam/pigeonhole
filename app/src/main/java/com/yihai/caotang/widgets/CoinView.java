package com.yihai.caotang.widgets;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.utils.ViewUtils;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

/**
 * 钱币view
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class CoinView {

    private static final int ANIMATION_DURATION = 600;
    private static final int START_TOTAL_NUMBER = 50;
    private RelativeLayout mContainerView;
    private TextView mTvCoinInfo;
    private GifImageView mGifCoinView;

    private Context mContext;
    protected WindowManager.LayoutParams mCoinViewLayout;
    protected WindowManager mWindowManager;

    private int mWidth;
    private int mHeight;
    private boolean isAdded = false;
    private boolean isHitted = false;
    private Handler delayHandler;
    private AnimateListener listener;

    public CoinView() {
        this.mContext = AppContext.getInstance();
        mWindowManager = (WindowManager) AppContext.getInstance().getSystemService(Context.WINDOW_SERVICE);
        delayHandler = new Handler(Looper.getMainLooper());

        DisplayMetrics metric = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        mWidth = metric.widthPixels;
        mHeight = metric.heightPixels;
        mCoinViewLayout = new WindowManager.LayoutParams();
        initView();
    }

    private void initView() {

        //windowlayout 布局
        mCoinViewLayout.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        mCoinViewLayout.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        mCoinViewLayout.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mCoinViewLayout.gravity = Gravity.CENTER | Gravity.CENTER; // 调整悬浮窗口至右侧中间
        mCoinViewLayout.width = mWidth;
        mCoinViewLayout.height = mHeight;

        //relativelayout 容器
        mContainerView = new RelativeLayout(mContext);
        mContainerView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //gif铜钱
        mGifCoinView = new GifImageView(mContext);
        mGifCoinView.setImageResource(R.drawable.qianbi);
        RelativeLayout.LayoutParams coinParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        coinParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGifCoinView.setLayoutParams(coinParam);

        //点击马上执行动作
        mGifCoinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getInstance().playEffect();
                delayHandler.removeCallbacks(hideCoinIfNoTouchTask);
                isHitted = true;
                if (listener != null) {
                    listener.onFinish(isHitted);
                }
                startCoinTranslateOutAnimate();
            }
        });
        mContainerView.addView(mGifCoinView);

        //text成绩
        mTvCoinInfo = new TextView(mContext);
        RelativeLayout.LayoutParams txtParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        txtParam.topMargin = ViewUtils.dip2px(mContext, 40);
        mTvCoinInfo.setLayoutParams(txtParam);
        mTvCoinInfo.setTextSize(28);
        mTvCoinInfo.setTextColor(mContext.getResources().getColor(R.color.textColorPrimaryDark));
        mTvCoinInfo.setVisibility(View.GONE);
        mContainerView.addView(mTvCoinInfo);
    }

    public void show() {
        isHitted = false;
        mGifCoinView.setVisibility(View.VISIBLE);
        mWindowManager.addView(mContainerView, mCoinViewLayout);
        //3秒后无操作隐藏
        delayHandler.postDelayed(hideCoinIfNoTouchTask, 3000L);
        isAdded = true;
    }

    public void dismiss() {
        if (isAdded) {
            mWindowManager.removeViewImmediate(mContainerView);
            delayHandler.removeCallbacks(afterTextShowedTask);
            delayHandler.removeCallbacks(hideCoinIfNoTouchTask);
            isAdded = false;
            mTvCoinInfo.setVisibility(View.GONE);
        }

    }

    /**
     * 铜板滑出动画
     */
    private void startCoinTranslateOutAnimate() {

        //执行动画
        AnimationSet set = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(ANIMATION_DURATION);
        set.addAnimation(alphaAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, new Random().nextInt(500) * -1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.ABSOLUTE, -1000f);
        translateAnimation.setDuration(ANIMATION_DURATION);
        set.addAnimation(translateAnimation);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mGifCoinView.setVisibility(View.GONE);
                mTvCoinInfo.setVisibility(View.VISIBLE);
                mTvCoinInfo.setText(String.format("金币收集%d/%d", SessionManager.getInstance().getSession().getStarNum(), START_TOTAL_NUMBER));
                delayHandler.postDelayed(afterTextShowedTask, 2000L);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mGifCoinView.startAnimation(set);
    }

    public void setListener(AnimateListener listener) {
        this.listener = listener;
    }

    /**
     * 未点击铜币触发播放
     */
    Runnable hideCoinIfNoTouchTask = new Runnable() {
        @Override
        public void run() {
            startCoinTranslateOutAnimate();
        }
    };

    /**
     * 金币数量显示完毕后触发
     */
    Runnable afterTextShowedTask = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    public interface AnimateListener {
        void onFinish(boolean isHit);
    }
}

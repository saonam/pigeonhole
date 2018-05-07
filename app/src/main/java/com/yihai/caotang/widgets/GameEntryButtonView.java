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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.ui.landscape.PoetryFillGameActivity;
import com.yihai.caotang.ui.landscape.UnityPlayerActivity;
import com.yihai.caotang.utils.ViewUtils;

import java.util.Random;

import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class GameEntryButtonView {

    private Button mButton;

    private Context mContext;
    protected WindowManager.LayoutParams mCoinViewLayout;
    protected WindowManager mWindowManager;

    private boolean isAdded = false;
    private OnClickListener listener;
    private LandScape landScape;

    public GameEntryButtonView() {
        this.mContext = AppContext.getInstance();
        mWindowManager = (WindowManager) AppContext.getInstance().getSystemService(Context.WINDOW_SERVICE);
        mCoinViewLayout = new WindowManager.LayoutParams();
        initView();
    }

    private void initView() {

        //windowlayout 布局
        mCoinViewLayout.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        mCoinViewLayout.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        mCoinViewLayout.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mCoinViewLayout.gravity = Gravity.LEFT | Gravity.TOP;
        mCoinViewLayout.width=WRAP_CONTENT;
        mCoinViewLayout.height=WRAP_CONTENT;

        //按键
        mButton = new Button(mContext);
        mButton.setBackground(mContext.getResources().getDrawable(R.drawable.selector_btn_game_entery));
        ViewGroup.LayoutParams coinParam =new ViewGroup.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
        mButton.setLayoutParams(coinParam);

        //点击马上执行动作
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAdded = true;
                if (listener != null) {
                    listener.onClick(v, landScape);
                    dismiss();
                }
            }
        });

    }

    public void show() {
        if (!isAdded) {
            mButton.setVisibility(View.VISIBLE);
            mWindowManager.addView(mButton, mCoinViewLayout);
        }
    }

    public void dismiss() {
        if (isAdded) {
            mWindowManager.removeViewImmediate(mButton);
            isAdded = false;
        }

    }

    public LandScape getLandScape() {
        return landScape;
    }

    public void setLandScape(LandScape landScape) {
        this.landScape = landScape;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void onClick(View view, LandScape landScape);
    }
}

package com.yihai.caotang.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.adapter.OnItemClickLisener;
import com.yihai.caotang.adapter.soundtrack.SoundTrackListAdapter;
import com.yihai.caotang.data.soundtrack.SoundTrack;
import com.yihai.caotang.data.soundtrack.SoundTrackManager;
import com.yihai.caotang.service.SoundTrackClient;

import java.util.ArrayList;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SoundTrackMenuView extends RelativeLayout {
    FrameLayout container;
    View mRoot;
    private ImageView mIvToggleMenu;
    private NoGlowRecyclerView mListuv;
    private SoundTrackListAdapter mAdapter;

    private boolean mIsListVisible;
    private boolean mIsClickable;
    private boolean mIsPlaying;
    private int mPlayingItemPosition;
    private Handler mHandler;

    public SoundTrackMenuView(Context context) {
        this(context, null);
    }

    public SoundTrackMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundTrackMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRoot = inflate(getContext(), R.layout.widget_sound_track_view, this);
        mIsListVisible = false;
        mHandler = new Handler(Looper.getMainLooper());
        mIsClickable = true;
        initView();
        initList();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        int toggleMenuWidth = mIvToggleMenu.getMeasuredWidth();
//        int toggleMenuHeight = mIvToggleMenu.getMeasuredHeight();
//        MarginLayoutParams listLayout = (MarginLayoutParams) mListuv.getLayoutParams();
//        mListuv.layout(0, 0, getWidth() - listLayout.rightMargin, getHeight() - listLayout.bottomMargin);
//        mIvToggleMenu.layout(getMeasuredWidth() - toggleMenuWidth, getMeasuredHeight() - toggleMenuHeight, getMeasuredWidth(), getMeasuredHeight());
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
////        int lW = mListuv.getMeasuredWidth();
////        int lH = mListuv.getMeasuredHeight();
////        int toggleMenuWidth = mIvToggleMenu.getMeasuredWidth();
////        int toggleMenuHeight = mIvToggleMenu.getMeasuredHeight();
////
////        int heightRes = ViewUtils.dip2px(getContext(), 213f) + toggleMenuHeight / 2;
////        int widthRes = ViewUtils.dip2px(getContext(), 193f) + toggleMenuWidth / 2+500;
////        Logger.d("onMeasure:%d,%d,%d,%d,%d,%d", toggleMenuWidth, toggleMenuHeight, heightRes, widthRes, lW, lH);
//        setMeasuredDimension(430, 490);
//    }

    /**
     * 初始化view
     */
    private void initView() {
        mIvToggleMenu = (ImageView) mRoot.findViewById(R.id.iv_toggle_menu);
        mListuv = (NoGlowRecyclerView) mRoot.findViewById(R.id.list_sound_track);

        mIvToggleMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToggleClickable()) {
                    AppContext.getInstance().playEffect();
                    toggle();
                }
            }
        });

        mAdapter = new SoundTrackListAdapter(new ArrayList<SoundTrack>());
        mAdapter.setOnItemClickLisener(new OnItemClickLisener() {
            @Override
            public void OnItemClick(View view, int position) {
            }

            @Override
            public void OnFunctionClick(View view, int position, int functionFlag) {
                //获取数据
                mPlayingItemPosition = position;
                SoundTrack track = mAdapter.getEntity(position);
                mHandler.removeCallbacks(mUpdateProgress);
                //是否在播放
                if (!track.isPlaying()) {
                    track.setPlaying(true);
                    track.setPlayed(true);
                    mIsPlaying = true;
                    SoundTrackClient.getInstance(getContext()).play(track.getRealPath());
                    mHandler.postDelayed(mUpdateProgress, 500L);
                } else {
                    track.setPlaying(false);
                    track.setProgress(0);
                    mIsPlaying = false;
                    mHandler.removeCallbacks(mUpdateProgress);
                    SoundTrackClient.getInstance(AppContext.getInstance()).stop();
                }
                //更新列表
                mAdapter.notifyDataSetChanged();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mListuv.setLayoutManager(manager);
        mListuv.setHasFixedSize(true);
        mListuv.setClipToPadding(false);
        mListuv.setEmptyView(R.layout.include_empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);
        mListuv.setAdapter(mAdapter);
        mListuv.addItemDecoration(new VerticalListItemDecoration(getContext(), 8, 5));
    }

    private void toggle() {
        mListuv.setVisibility(mIsListVisible ? GONE : VISIBLE);
        mIsListVisible = !mIsListVisible;
    }

    public void hide() {
        mListuv.setVisibility(GONE);
        mIsListVisible = false;
    }

    private void initList() {
        mAdapter.replace(SoundTrackManager.getInstance().getCache());
    }

    /**
     * 播放结束重置列表
     */
    public void reset() {
//        if (mIsPlaying) {
//            return;
//        }

        SoundTrack item = mAdapter.getEntity(mPlayingItemPosition);
        mHandler.removeCallbacks(mUpdateProgress);
        item.setPlaying(false);
        item.setProgress(0);
        mAdapter.notifyDataSetChanged();
    }

    public boolean isToggleClickable() {
        return mIsClickable;
    }

    public void setToggleClickable(boolean mIsClickable) {
        this.mIsClickable = mIsClickable;
    }

    /**
     * 播放进度
     */
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {
            SoundTrack item = mAdapter.getEntity(mPlayingItemPosition);
            long position = SoundTrackClient.getInstance(getContext()).position();
            long duration = SoundTrackClient.getInstance(getContext()).duration();

            if (duration > 0 && duration < 627080716) {
                item.setProgress((int) (100 * position / duration));
                mAdapter.notifyDataSetChanged();
            }

            if (SoundTrackClient.getInstance(getContext()).isPlayingSoundTrack()) {
                mHandler.postDelayed(mUpdateProgress, 1000L);
            } else {
                mHandler.removeCallbacks(mUpdateProgress);
            }
        }
    };

    /**
     * 与所在页面同步生命周期
     */
    public void onPause() {
        if (mHandler != null) {
            SoundTrackClient.getInstance(AppContext.getInstance()).stop();
            mHandler.removeCallbacks(mUpdateProgress);
        }

        for (SoundTrack item : mAdapter.getObjects()) {
            item.setProgress(0);
            item.setPlaying(false);
        }
        mAdapter.notifyDataSetChanged();
        mListuv.setVisibility(mIsListVisible ? GONE : VISIBLE);
        mIsListVisible = !mIsListVisible;
    }
}

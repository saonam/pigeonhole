package com.yihai.caotang.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.yihai.caotang.R;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */

public class NoGlowRecyclerView extends UltimateRecyclerView {
    public NoGlowRecyclerView(Context context) {
        super(context);
        initView();
    }

    public NoGlowRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NoGlowRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        mRecyclerView.setFadingEdgeLength(0);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        setHasFixedSize(true);
        setClipToPadding(false);
        mRecyclerView.setFadingEdgeLength(0);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        setEmptyView(R.layout.include_empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);
    }
}

package com.yihai.caotang.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class RepositoryTabView extends LinearLayout {
    private final static String TAG = "RepositoryTabView";

    public static final int CLICK_LANDSCAPE = 0;
    public static final int CLICK_ANTIQUE = 1;
    public static final int CLICK_PEOPLE = 2;
    public static final int CLICK_POETRY = 3;

    private View mRoot;
    private Button mIvLandscape;
    private Button mIvAntique;
    private Button mIvPeople;
    private Button mIvPoetry;

    private OnTabItemClickListener mListener;

    public RepositoryTabView(Context context) {
        this(context, null);
    }

    public RepositoryTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public RepositoryTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.widget_repository_tab_view, this, true);
        initView();
    }

    public void setmListener(OnTabItemClickListener mListener) {
        this.mListener = mListener;
    }

    private void initView() {
        mIvLandscape = (Button) mRoot.findViewById(R.id.tab_landscape);
        mIvAntique = (Button) mRoot.findViewById(R.id.tab_antique);
        mIvPeople = (Button) mRoot.findViewById(R.id.tab_people);
        mIvPoetry = (Button) mRoot.findViewById(R.id.tab_poetry);

        mIvLandscape.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                AppContext.getInstance().playEffect();
                mListener.onClick(v, CLICK_LANDSCAPE);
            }
        });
        mIvAntique.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                AppContext.getInstance().playEffect();
                mListener.onClick(v, CLICK_ANTIQUE);
            }
        });
        mIvPeople.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                AppContext.getInstance().playEffect();
                mListener.onClick(v, CLICK_PEOPLE);
            }
        });
        mIvPoetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                AppContext.getInstance().playEffect();
                mListener.onClick(v, CLICK_POETRY);
            }
        });
    }

    public void setSelect(int page) {
        unSelectAll();
        switch (page) {
            case CLICK_LANDSCAPE:
                mIvLandscape.setSelected(true);
                break;
            case CLICK_ANTIQUE:
                mIvAntique.setSelected(true);
                break;
            case CLICK_PEOPLE:
                mIvPeople.setSelected(true);
                break;
            case CLICK_POETRY:
                mIvPoetry.setSelected(true);
                break;
        }
    }

    private void unSelectAll() {
        mIvLandscape.setSelected(false);
        mIvAntique.setSelected(false);
        mIvPeople.setSelected(false);
        mIvPoetry.setSelected(false);
    }

    public interface OnTabItemClickListener {
        void onClick(View view, int type);
    }

}

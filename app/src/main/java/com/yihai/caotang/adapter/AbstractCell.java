package com.yihai.caotang.adapter;

import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public abstract class AbstractCell extends UltimateRecyclerviewViewHolder {
    public View root;

    public AbstractCell(View itemView) {
        super(itemView);
        root = itemView;
    }
}

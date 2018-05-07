package com.yihai.caotang.adapter.landscape;

import android.view.View;
import android.widget.ImageView;

import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractCell;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class LandscapeListCell extends AbstractCell {
    public static final int layout = R.layout.card_landscape_antique;

    @Bind(R.id.iv_image)
    ImageView ivImage;

    public LandscapeListCell(View itemView, boolean isItem) {
        super(itemView);
        if (isItem) {
            ButterKnife.bind(this, itemView);
        }
    }
}

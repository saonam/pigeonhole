package com.yihai.caotang.adapter.antique;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractCell;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class AntiqueListCell extends AbstractCell {
    public static final int layout = R.layout.card_landscape_antique;

    @Bind(R.id.iv_image)
    ImageView ivImage;

    public AntiqueListCell(View itemView, boolean isItem) {
        super(itemView);
        if (isItem) {
            ButterKnife.bind(this, itemView);
        }
    }

}

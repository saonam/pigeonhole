package com.yihai.caotang.adapter.repository;

import android.view.View;
import android.widget.TextView;

import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractCell;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class RepositoryGridCell extends AbstractCell {
    public static final int layout = R.layout.card_repository;

    @Bind(R.id.iv_banner)
    CircleImageView ivBanner;

    @Bind(R.id.tv_name)
    TextView tvName;

    @Bind(R.id.tv_position)
    TextView tvPosition;

    public RepositoryGridCell(View itemView, boolean isItem) {
        super(itemView);
        if (isItem) {
            ButterKnife.bind(this, itemView);

        }
    }

}

package com.yihai.caotang.adapter.landscape;

import android.view.View;
import android.widget.TextView;

import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractCell;
import com.yihai.caotang.widgets.FontTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SimulatorListCell extends AbstractCell {
    public static final int layout = R.layout.card_simulator;

    @Bind(R.id.tv_name)
    TextView tvName;

    public SimulatorListCell(View itemView, boolean isItem) {
        super(itemView);
        if (isItem) {
            ButterKnife.bind(this, itemView);
        }
    }
}

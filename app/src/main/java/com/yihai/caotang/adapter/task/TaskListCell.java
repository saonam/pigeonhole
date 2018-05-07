package com.yihai.caotang.adapter.task;

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
public class TaskListCell extends AbstractCell {
    public static final int layout = R.layout.card_task;

    @Bind(R.id.tv_content)
    TextView tvContent;

    @Bind(R.id.iv_is_done)
    ImageView ivIsDone;

    public TaskListCell(View itemView, boolean isItem) {
        super(itemView);
        if (isItem) {
            ButterKnife.bind(this, itemView);
        }
    }

}

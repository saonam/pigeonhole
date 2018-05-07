package com.yihai.caotang.adapter.soundtrack;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractCell;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhouzhuo.zzhorizontalprogressbar.ZzHorizontalProgressBar;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SoundTrackListCell extends AbstractCell {
    public static final int layout = R.layout.card_sound_track;
    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Bind(R.id.iv_play_pause)
    ImageView ivPlayOrPause;

    @Bind(R.id.progress_position)
    ZzHorizontalProgressBar progress;

    public SoundTrackListCell(View itemView, boolean isItem) {
        super(itemView);
        if (isItem) {
            ButterKnife.bind(this, itemView);
        }
    }

}

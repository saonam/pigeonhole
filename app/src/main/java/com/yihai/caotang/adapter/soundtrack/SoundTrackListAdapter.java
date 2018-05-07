package com.yihai.caotang.adapter.soundtrack;

import android.view.View;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.data.soundtrack.SoundTrack;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SoundTrackListAdapter extends AbstractListAdapter<SoundTrack, SoundTrackListCell> {

    public SoundTrackListAdapter(List<SoundTrack> items) {
        super(items);
    }

    @Override
    protected int getNormalLayoutResId() {
        return SoundTrackListCell.layout;
    }

    @Override
    protected SoundTrackListCell newViewHolder(View view) {
        return new SoundTrackListCell(view, true);
    }

    @Override
    public SoundTrackListCell newHeaderHolder(View view) {
        return new SoundTrackListCell(view, false);
    }

    @Override
    public SoundTrackListCell newFooterHolder(View view) {
        return new SoundTrackListCell(view, false);
    }

    @Override
    protected void withBindHolder(SoundTrackListCell holder, final SoundTrack data, final int position) {
        holder.tvTitle.setText(data.getTitle());
        holder.ivPlayOrPause.setSelected(data.isPlaying());
        if (data.getProgress() != 0) {
            holder.progress.setProgress(data.getProgress());
        } else {
            holder.progress.setProgress(0);
        }
        holder.ivPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppContext.getInstance().playEffect();
                cleanState(data);
                onItemClickLisener.OnFunctionClick(view, position, 1);
            }
        });
    }

    /**
     * 清理状态
     * 被点击的留在具体点击事件中处理
     *
     * @param data
     */
    private void cleanState(SoundTrack data) {
        for (SoundTrack item : getObjects()) {
            if (item.equals(data)) {
                continue;
            }
            item.setProgress(0);
            item.setPlaying(false);
        }
    }
}

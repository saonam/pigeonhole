package com.yihai.caotang.adapter.landscape;

import android.view.View;
import android.widget.CompoundButton;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.data.game.GameOption;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class GameOptionListAdapter extends AbstractListAdapter<GameOption, GameOptionListCell> {

    public GameOptionListAdapter(List<GameOption> items) {
        super(items);
    }

    @Override
    protected int getNormalLayoutResId() {
        return GameOptionListCell.layout;
    }

    @Override
    protected GameOptionListCell newViewHolder(View view) {
        return new GameOptionListCell(view, true);
    }

    @Override
    public GameOptionListCell newHeaderHolder(View view) {
        return new GameOptionListCell(view, false);
    }

    @Override
    public GameOptionListCell newFooterHolder(View view) {
        return new GameOptionListCell(view, false);
    }

    @Override
    protected void withBindHolder(GameOptionListCell holder, GameOption data, final int position) {

        holder.radioButton.setText(data.getTitle());
        holder.radioButton.setChecked(data.isChecked());
        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppContext.getInstance().playEffect();
                onItemClickLisener.OnItemClick(buttonView, position);
            }
        });
    }
}

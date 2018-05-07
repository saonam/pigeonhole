package com.yihai.caotang.adapter.landscape;

import android.view.View;

import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.data.landscape.LandScape;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SimulatorListAdapter extends AbstractListAdapter<LandScape, SimulatorListCell> {

    public SimulatorListAdapter(List<LandScape> items) {
        super(items);
    }

    @Override
    protected int getNormalLayoutResId() {
        return SimulatorListCell.layout;
    }

    @Override
    protected SimulatorListCell newViewHolder(View view) {
        return new SimulatorListCell(view, true);
    }

    @Override
    public SimulatorListCell newHeaderHolder(View view) {
        return new SimulatorListCell(view, false);
    }

    @Override
    public SimulatorListCell newFooterHolder(View view) {
        return new SimulatorListCell(view, false);
    }

    @Override
    protected void withBindHolder(SimulatorListCell holder, final LandScape data, final int position) {
        holder.tvName.setText(data.getName());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLisener.OnItemClick(v, position);
            }
        });
    }

}

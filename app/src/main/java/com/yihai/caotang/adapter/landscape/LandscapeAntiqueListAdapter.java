package com.yihai.caotang.adapter.landscape;

import android.view.View;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.data.antique.Antique;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class LandscapeAntiqueListAdapter extends AbstractListAdapter<Antique, LandscapeListCell> {

    public LandscapeAntiqueListAdapter(List<Antique> items) {
        super(items);
    }

    @Override
    protected int getNormalLayoutResId() {
        return LandscapeListCell.layout;
    }

    @Override
    protected LandscapeListCell newViewHolder(View view) {
        return new LandscapeListCell(view, true);
    }

    @Override
    public LandscapeListCell newHeaderHolder(View view) {
        return new LandscapeListCell(view, false);
    }

    @Override
    public LandscapeListCell newFooterHolder(View view) {
        return new LandscapeListCell(view, false);
    }

    @Override
    protected void withBindHolder(LandscapeListCell holder, final Antique data, final int position) {
        AppContext.getInstance().load(holder.ivImage, data.getRealImageUri());

        holder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppContext.getInstance().playEffect();
                onItemClickLisener.OnFunctionClick(view, position, 1);
            }
        });
    }

}

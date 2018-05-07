package com.yihai.caotang.adapter.antique;

import android.view.View;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.data.antique.Antique;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class AntiqueListAdapter extends AbstractListAdapter<Antique, AntiqueListCell> {

    public AntiqueListAdapter(List<Antique> items) {
        super(items);
    }

    @Override
    protected int getNormalLayoutResId() {
        return AntiqueListCell.layout;
    }

    @Override
    protected AntiqueListCell newViewHolder(View view) {
        return new AntiqueListCell(view, true);
    }

    @Override
    public AntiqueListCell newHeaderHolder(View view) {
        return new AntiqueListCell(view, false);
    }

    @Override
    public AntiqueListCell newFooterHolder(View view) {
        return new AntiqueListCell(view, false);
    }

    @Override
    protected void withBindHolder(AntiqueListCell holder, Antique data, final int position) {
        AppContext.getInstance().load(holder.ivImage, data.getRealImageUri());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AppContext.getInstance().playEffect();
                onItemClickLisener.OnItemClick(view, position);
            }
        });
    }
}

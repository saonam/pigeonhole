package com.yihai.caotang.adapter.repository;

import android.view.View;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.data.antique.Antique;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class RepositoryGridAdapter extends AbstractListAdapter<Antique, RepositoryGridCell> {

    public RepositoryGridAdapter(List<Antique> items) {
        super(items);
    }

    @Override
    protected int getNormalLayoutResId() {
        return RepositoryGridCell.layout;
    }

    @Override
    protected RepositoryGridCell newViewHolder(View view) {
        return new RepositoryGridCell(view, true);
    }

    @Override
    public RepositoryGridCell newHeaderHolder(View view) {
        return new RepositoryGridCell(view, false);
    }

    @Override
    public RepositoryGridCell newFooterHolder(View view) {
        return new RepositoryGridCell(view, false);
    }

    @Override
    protected void withBindHolder(RepositoryGridCell holder, Antique data, final int position) {
        holder.tvName.setText(data.getName().trim());
        holder.tvPosition.setText(data.getLocation().trim());
        AppContext.getInstance().load(holder.ivBanner,data.getRealImageUri());

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppContext.getInstance().playEffect();
                onItemClickLisener.OnItemClick(view, position);
            }
        });
    }
}

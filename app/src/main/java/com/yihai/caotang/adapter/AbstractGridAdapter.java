package com.yihai.caotang.adapter;

import com.yihai.caotang.data.BaseData;
import com.marshalchen.ultimaterecyclerview.UltimateGridLayoutAdapter;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public abstract class AbstractGridAdapter<T extends BaseData, E extends AbstractCell> extends UltimateGridLayoutAdapter<T, E> {

    public List<T> mData;
    public OnItemClickLisener onItemClickLisener;

    public AbstractGridAdapter(List<T> items) {
        super(items);
        this.mData = items;
    }

    public void setOnItemClickLisener(OnItemClickLisener onItemClickLisener) {
        this.onItemClickLisener = onItemClickLisener;
    }

    /**
     * 更新数据
     *
     * @param data
     */
    public void replace(List<T> data) {
        this.source = data;
        notifyDataSetChanged();
    }

    /**
     * 获取某个item
     *
     * @param position
     * @return
     */
    public T getEntity(int position) {
        return getObjects().get(getRealPos(position));
    }

    public int getRealPos(int position) {
        return hasHeaderView() ? position - 1 : position;
    }
}

package com.yihai.caotang.adapter;

import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.yihai.caotang.data.BaseData;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public abstract class AbstractListAdapter<T extends BaseData, E extends AbstractCell> extends easyRegularAdapter<T, E> {

    public OnItemClickLisener onItemClickLisener;
    private List<T> mData;

    public AbstractListAdapter(List<T> items) {
        super(items);
        mData = items;
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
        this.source.clear();
        if (data != null && data.size() != 0) {
            this.source = data;
        }
        notifyDataSetChanged();
    }

    /**
     * 获取某个数据
     *
     * @param position
     * @return
     */
    public T getEntity(int position) {
        int realPos = hasHeaderView() ? position - 1 : position;
        return getObjects().get(realPos);
    }
}

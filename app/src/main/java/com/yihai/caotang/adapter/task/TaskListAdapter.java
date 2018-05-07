package com.yihai.caotang.adapter.task;

import android.view.View;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.data.task.Task;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class TaskListAdapter extends AbstractListAdapter<Task, TaskListCell> {

    public TaskListAdapter(List<Task> items) {
        super(items);
    }

    @Override
    protected int getNormalLayoutResId() {
        return TaskListCell.layout;
    }

    @Override
    protected TaskListCell newViewHolder(View view) {
        return new TaskListCell(view, true);
    }

    @Override
    public TaskListCell newHeaderHolder(View view) {
        return new TaskListCell(view, false);
    }

    @Override
    public TaskListCell newFooterHolder(View view) {
        return new TaskListCell(view, false);
    }

    @Override
    protected void withBindHolder(TaskListCell holder, Task data, final int position) {
        holder.tvContent.setText(data.getContent());
        int visitedTimes = SessionManager.getInstance().getSession().getLandScapeLog().get(data.getId());
        if (visitedTimes != 0) {
            holder.ivIsDone.setVisibility(View.VISIBLE);
        } else {
            holder.ivIsDone.setVisibility(View.INVISIBLE);
        }
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getInstance().playEffect();
                onItemClickLisener.OnItemClick(v, position);
            }
        });
    }
}

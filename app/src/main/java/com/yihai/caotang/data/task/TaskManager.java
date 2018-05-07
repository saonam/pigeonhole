package com.yihai.caotang.data.task;

import android.database.Cursor;

import com.yihai.caotang.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class TaskManager {
    private static TaskManager instance;
    private List<Task> mCache;

    public synchronized static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    private TaskManager() {
        mCache = new ArrayList<>();
    }

    /**
     * 热缓存
     * 其他操作直接走热缓存拿,不走数据库
     */
    public void warnUp() {

        String sql = String.format("select * from caotang_task where lang=%d", 1);//TODO  SessionManager.getInstance().getLang().getLang()
        if (mCache.size() == 0) {
            Cursor c = DatabaseManager.getInstance().rawQuery(sql);
            while (c.moveToNext()) {
                Task item = new Task();
                item.setId(c.getInt(c.getColumnIndex("id")));
                item.setContent(c.getString(c.getColumnIndex("content")));
                item.setLang(c.getInt(c.getColumnIndex("lang")));
                item.setLandScapeId(c.getInt(c.getColumnIndex("land_scape_id")));
                mCache.add(item);
            }
        }
    }

    public List<Task> getTasks() {
        return mCache;
    }

}

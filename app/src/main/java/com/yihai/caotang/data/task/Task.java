package com.yihai.caotang.data.task;

import android.database.Cursor;

import com.yihai.caotang.data.BaseData;
import com.yihai.caotang.data.DatabaseManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class Task extends BaseData implements Serializable {
    private String content;     //任务内容
    private int landScapeId;    //对应景点
    private int lang;           //语言

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLandScapeId() {
        return landScapeId;
    }

    public void setLandScapeId(int landScapeId) {
        this.landScapeId = landScapeId;
    }

    private static Task instance;
    private List<Task> mCahe;

    public synchronized static Task getInstance() {
        if (instance == null) {
            instance = new Task();
        }
        return instance;
    }

    public Task() {
        mCahe = new ArrayList<>();
    }

    public List<Task> getCache() {
        if (mCahe.size() != 0) {
            return mCahe;
        }

        String sql = String.format("select * from caotang_task where lang=%d", 1);//TODO  SessionManager.getInstance().getLang().getLang()
        Cursor c = DatabaseManager.getInstance().rawQuery(sql);
        while (c.moveToNext()) {
            Task res = new Task();
            res.setId(c.getInt(c.getColumnIndex("id")));
            res.setContent(c.getString(c.getColumnIndex("content")));
            res.setLang(c.getInt(c.getColumnIndex("lang")));
            res.setLandScapeId(c.getInt(c.getColumnIndex("land_scape_id")));
            mCahe.add(res);
        }
        return mCahe;
    }
}

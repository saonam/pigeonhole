package com.yihai.caotang.data.session;

import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.yihai.caotang.data.BaseData;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;

/**
 * 后端返回数据
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SessionResponse extends BaseData {

    /**
     * session_id : 0
     * pad_id : 39
     * status : 3
     * json:{}
     * started_at:2017-8-8 12:00:00
     * time_length : 0
     */

    private int session_id;
    private int pad_id;
    private int status;
    private String started_at;
    private String json;
    private int time_length;

    public String getStarted_at() {
        return started_at;
    }

    public void setStarted_at(String started_at) {
        this.started_at = started_at;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public void setPad_id(int pad_id) {
        this.pad_id = pad_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTime_length(int time_length) {
        this.time_length = time_length;
    }

    public int getSession_id() {
        return session_id;
    }

    public int getPad_id() {
        return pad_id;
    }

    public int getStatus() {
        return status;
    }

    public int getTime_length() {
        return time_length;
    }

    public Session parse() {
        Session item = new Session();
        item.setSessionId(this.session_id);
        item.setStatus(this.status);
        item.setTimeLength(this.time_length);
        item.setStartAt(this.started_at);
        // 这里解析一下拿到的SessionResponse的JSON数据，然后填充
        if (!TextUtils.isEmpty(this.json)) {
            SessionJSON request = new GsonBuilder().create().fromJson(this.json, SessionJSON.class);
            item.setBrightness(request.getBrightness());
            item.setBackgroundOpen(request.isBackgroundOpen());
            item.setAffectOpen(request.isAffectOpen());
            item.setLandScapeLog(request.getLandscapeLog());
            item.setStarNum(request.getStarNum());
            item.setSelTour(request.getSelTour());
        } else {
            //握手时未拿到json数据则默认初始化
            item.setBrightness(Session.DEFAULT_BRIGHTNESS);
            item.setBackgroundOpen(Session.DEFAULT_PLAY_BACKGROUND);
            item.setAffectOpen(Session.DEFAULT_PLAY_SOUND_AFFECT);
            item.setLang(1);//TODO 多国语言化
            item.setStarNum(0);
            item.getLandScapeLog().clear();
            for (LandScape landscape : LandScapeManager.getInstance().getLandscapes()) {
                item.getLandScapeLog().put(landscape.getId(), 0);
            }
        }
        return item;
    }
}

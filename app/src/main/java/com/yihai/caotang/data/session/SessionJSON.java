package com.yihai.caotang.data.session;

import com.google.gson.GsonBuilder;

import java.util.HashMap;

/**
 * 心跳请求的json
 * 尽量数据量少一点,减少流量
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SessionJSON {

    private HashMap<Integer, Integer> landscapeLog;     //浏览记录
    private boolean isBackgroundOpen;    //音乐是否打开
    private boolean isAffectOpen;   //音效是否打开
    private int brightness;         //屏幕亮度
    private int starNum;            //游戏星星读数
    private int lang;               //语言
    private int selTour;//选择的旅游线路

    public HashMap<Integer, Integer> getLandscapeLog() {
        return landscapeLog;
    }

    public void setLandscapeLog(HashMap<Integer, Integer> landscapeLog) {
        this.landscapeLog = landscapeLog;
    }

    public boolean isBackgroundOpen() {
        return isBackgroundOpen;
    }

    public void setBackgroundOpen(boolean backgroundOpen) {
        isBackgroundOpen = backgroundOpen;
    }

    public boolean isAffectOpen() {
        return isAffectOpen;
    }

    public void setAffectOpen(boolean affectOpen) {
        isAffectOpen = affectOpen;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getStarNum() {
        return starNum;
    }

    public void setStarNum(int starNum) {
        this.starNum = starNum;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public int getSelTour() {
        return selTour;
    }

    public void setSelTour(int selTour) {
        this.selTour = selTour;
    }

    /**
     * 获取心跳请求时的
     *
     * @return
     */
    public static synchronized String requestJson(Session session) {
        SessionJSON request = new SessionJSON();
        request.setLandscapeLog(session.getLandScapeLog());
        request.setBackgroundOpen(session.isBackgroundOpen());
        request.setAffectOpen(session.isAffectOpen());
        request.setBrightness(session.getBrightness());
        request.setStarNum(session.getStarNum());
        request.setLang(session.getLang());
        request.setSelTour(session.getSelTour());
        String json = new GsonBuilder().create().toJson(request, SessionJSON.class);
        return json;
    }
}

package com.yihai.caotang.data.soundtrack;

import com.yihai.caotang.data.Constants;
import com.yihai.caotang.data.BaseData;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SoundTrack extends BaseData {
    private String sound_uri;       //音频路径
    private int lang;               //语言
    private String title;           //标题
    private int catalog;            //类型
    private boolean isPlayed;       //是否已播放
    private boolean isPlaying;      //是否在播放
    private int progress;        //当前播放百分比

    public String getSound_uri() {
        return sound_uri;
    }

    public void setSound_uri(String sound_uri) {
        this.sound_uri = sound_uri;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((SoundTrack) obj).id;
    }

    @Override
    public String toString() {
        return "SoundTrack{" +
                "sound_uri='" + sound_uri + '\'' +
                ", lang=" + lang +
                ", title='" + title + '\'' +
                ", catalog=" + catalog +
                ", isPlayed=" + isPlayed +
                ", isPlaying=" + isPlaying +
                ", progress=" + progress +
                '}';
    }

    /**
     * 根据语音获取真实地址
     *
     * @return
     */
    public String getRealPath() {
        return Constants.SD_ROOT + "soundtrack/ch/" + this.sound_uri;
        //TODO 多语言
//        return Constants.SD_ROOT + "soundtrack/" + SessionManager.getInstance().getLang().getUri()+ this.image_uri;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}

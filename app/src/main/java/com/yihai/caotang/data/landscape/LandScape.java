package com.yihai.caotang.data.landscape;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.maps.model.LatLng;
import com.yihai.caotang.data.Constants;
import com.yihai.caotang.data.BaseData;
import com.yihai.caotang.data.antique.Antique;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class LandScape extends BaseData implements Parcelable {

    private String name;                //景点名称
    private String displayType;        //页面类型
    private String gameType;           //游戏类型
    private String soundTrackUri;     //音频路径
    private int hasGame;               //是否有游戏
    private int hasImageList;         //是否要显示图片列表
    private int starNum;               //星星数量
    private int lang;                   //语言
    private int geoCode;               //BEACON代码
    private int visitTimes;              //浏览次数
    private double lat;
    private double lng;

    private List<Antique> antiques;     //景点的文物
    private List<Border> borders;       //景点边界

    public int getVisitTimes() {
        return visitTimes;
    }

    public void setVisitTimes(int visitTimes) {
        this.visitTimes = visitTimes;
    }

    public int getGeoCode() {
        return geoCode;
    }

    public void setGeoCode(int geoCode) {
        this.geoCode = geoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getSoundTrackUri() {
        return soundTrackUri;
    }

    public void setSoundTrackUri(String soundTrackUri) {
        this.soundTrackUri = soundTrackUri;
    }

    public int getHasGame() {
        return hasGame;
    }

    public void setHasGame(int hasGame) {
        this.hasGame = hasGame;
    }

    public int getHasImageList() {
        return hasImageList;
    }

    public void setHasImageList(int hasImageList) {
        this.hasImageList = hasImageList;
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

    public List<Antique> getAntiques() {
        return antiques;
    }

    public void setAntiques(List<Antique> antiques) {
        this.antiques = antiques;
    }

    public List<Border> getBorders() {
        return borders;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setBorders(List<Border> borders) {
        this.borders = borders;
    }

    public List<LatLng> getBorderLatLng() {
        List<LatLng> res = new ArrayList<>();
        for (Border border : this.borders) {
            res.add(new LatLng(border.getLat(), border.getLng()));
        }
        return res;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null){
            return false;
        }
        return this.id == ((LandScape) obj).id;
    }

    /**
     * 根据语音获取真实的音频路径
     *
     * @return
     */
    public String getRealSoundTrackPath() {
        return Constants.SD_ROOT + "soundtrack/ch/" + this.soundTrackUri;
        //TODO 多语言
//        return Constants.SD_ROOT + "soundtrack/" + SessionManager.getInstance().getLang().getUri()+ this.image_uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.displayType);
        dest.writeString(this.soundTrackUri);
        dest.writeInt(this.hasGame);
        dest.writeInt(this.hasImageList);
        dest.writeString(this.gameType);
        dest.writeInt(this.starNum);
        dest.writeInt(this.lang);
        dest.writeInt(this.geoCode);
        dest.writeInt(this.visitTimes);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeTypedList(this.antiques);
        dest.writeTypedList(this.borders);
        dest.writeInt(this.id);
    }

    public LandScape() {
    }

    private LandScape(Parcel in) {
        this.name = in.readString();
        this.displayType = in.readString();
        this.soundTrackUri = in.readString();
        this.hasGame = in.readInt();
        this.hasImageList = in.readInt();
        this.gameType =in.readString();
        this.starNum = in.readInt();
        this.lang = in.readInt();
        this.geoCode = in.readInt();
        this.visitTimes = in.readInt();
        this.lat=in.readDouble();
        this.lng=in.readDouble();
        this.antiques = new ArrayList<>();
        in.readTypedList(this.antiques, Antique.CREATOR);
        this.borders = new ArrayList<>();
        in.readTypedList(this.borders, Border.CREATOR);
        this.id = in.readInt();
    }

    public static final Creator<LandScape> CREATOR = new Creator<LandScape>() {
        public LandScape createFromParcel(Parcel source) {
            return new LandScape(source);
        }

        public LandScape[] newArray(int size) {
            return new LandScape[size];
        }
    };
}

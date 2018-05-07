package com.yihai.caotang.data.antique;

import android.os.Parcel;
import android.os.Parcelable;

import com.yihai.caotang.data.Constants;
import com.yihai.caotang.data.BaseData;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class Antique extends BaseData implements Parcelable {
    private String name;                    //明细
    private String repository_content;      //知识点衍生介绍
    private String display_content;         //展现内容
    private String type;                    //种类
    private int catalog;                    //知识点类型   0景点 / 1文物 / 2人物 / 3诗歌
    private int land_scape_id;              //景点id
    private String land_scape_name;         //景点名称
    private int is_repository;              //是否有知识点衍生
    private String ar_code;                 //展现触发AR码
    private int ar_type;                    //AR展示类型
    private String image_uri;               //图片路径
    private String sound_track_uri;         //配音路径
    private int lang;                       //语言类型
    private String location;                //位置

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepository_content() {
        return repository_content;
    }

    public void setRepository_content(String repository_content) {
        this.repository_content = repository_content;
    }

    public String getDisplay_content() {
        return display_content;
    }

    public void setDisplay_content(String display_content) {
        this.display_content = display_content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCatalog() {
        return catalog;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    public int getLand_scape_id() {
        return land_scape_id;
    }

    public void setLand_scape_id(int land_scape_id) {
        this.land_scape_id = land_scape_id;
    }

    public String getLand_scape_name() {
        return land_scape_name;
    }

    public void setLand_scape_name(String land_scape_name) {
        this.land_scape_name = land_scape_name;
    }

    public int getIs_repository() {
        return is_repository;
    }

    public void setIs_repository(int is_repository) {
        this.is_repository = is_repository;
    }

    public String getAr_code() {
        return ar_code;
    }

    public void setAr_code(String ar_code) {
        this.ar_code = ar_code;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getSound_track_uri() {
        return sound_track_uri;
    }

    public void setSound_track_uri(String sound_track_uri) {
        this.sound_track_uri = sound_track_uri;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRealImageUri() {
        return Constants.SD_ROOT + "antique/" + this.image_uri;
    }

    public String getRealSoundTrackUri() {
        return Constants.SD_ROOT + "soundtrack/ch/" + this.sound_track_uri;
        //TODO 多语言
//        return Constants.SD_ROOT + "other/" + SessionManager.getInstance().getLang().getUri()+ this.image_uri;
    }

    public int getAr_type() {
        return ar_type;
    }

    public void setAr_type(int ar_type) {
        this.ar_type = ar_type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.repository_content);
        dest.writeString(this.display_content);
        dest.writeString(this.type);
        dest.writeInt(this.catalog);
        dest.writeInt(this.land_scape_id);
        dest.writeString(this.land_scape_name);
        dest.writeInt(this.is_repository);
        dest.writeString(this.ar_code);
        dest.writeInt(this.ar_type);
        dest.writeString(this.image_uri);
        dest.writeString(this.sound_track_uri);
        dest.writeInt(this.lang);
        dest.writeString(this.location);
        dest.writeInt(this.id);
    }

    public Antique() {
    }

    private Antique(Parcel in) {
        this.name = in.readString();
        this.repository_content = in.readString();
        this.display_content = in.readString();
        this.type = in.readString();
        this.catalog = in.readInt();
        this.land_scape_id = in.readInt();
        this.land_scape_name = in.readString();
        this.is_repository = in.readInt();
        this.ar_code = in.readString();
        this.ar_type = in.readInt();
        this.image_uri = in.readString();
        this.sound_track_uri = in.readString();
        this.lang = in.readInt();
        this.location = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<Antique> CREATOR = new Creator<Antique>() {
        public Antique createFromParcel(Parcel source) {
            return new Antique(source);
        }

        public Antique[] newArray(int size) {
            return new Antique[size];
        }
    };
}

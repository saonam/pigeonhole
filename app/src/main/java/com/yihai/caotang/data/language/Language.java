package com.yihai.caotang.data.language;

import android.os.Parcel;
import android.os.Parcelable;

import com.yihai.caotang.data.BaseData;

/**
 * TODO NEXT VERSION
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class Language extends BaseData implements Parcelable{
    private int lang;
    private String name;
    private String uri;

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.lang);
        dest.writeString(this.name);
        dest.writeString(this.uri);
        dest.writeInt(this.id);
    }

    public Language() {
    }

    private Language(Parcel in) {
        this.lang = in.readInt();
        this.name = in.readString();
        this.uri = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<Language> CREATOR = new Creator<Language>() {
        public Language createFromParcel(Parcel source) {
            return new Language(source);
        }

        public Language[] newArray(int size) {
            return new Language[size];
        }
    };
}


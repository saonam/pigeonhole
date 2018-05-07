package com.yihai.caotang.data.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.yihai.caotang.data.BaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class GameQuestion extends BaseData implements Parcelable {
    private int id;
    private String title;       //标题
    private String content;     //内容
    private int lang;           //语言
    private List<GameOption> options;   //选项

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public List<GameOption> getOptions() {
        return options;
    }

    public void setOptions(List<GameOption> options) {
        this.options = options;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeInt(this.lang);
        dest.writeTypedList(this.options);
        dest.writeInt(this.id);
    }

    public GameQuestion() {
    }

    private GameQuestion(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.content=in.readString();
        this.lang = in.readInt();
        this.options = new ArrayList<>();
        in.readTypedList(this.options, GameOption.CREATOR);
        this.id = in.readInt();
    }

    public static final Creator<GameQuestion> CREATOR = new Creator<GameQuestion>() {
        public GameQuestion createFromParcel(Parcel source) {
            return new GameQuestion(source);
        }
        public GameQuestion[] newArray(int size) {
            return new GameQuestion[size];
        }
    };
}

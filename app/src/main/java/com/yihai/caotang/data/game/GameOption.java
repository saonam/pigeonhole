package com.yihai.caotang.data.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.yihai.caotang.data.BaseData;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class GameOption extends BaseData implements Parcelable {
    private int id;
    private String title;       //选项
    private int lang;           //语言
    private int gameQuestionId; //题目id
    private boolean isCorrect;  //是否正确
    private boolean isChecked;  //是否选择

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

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

    public int getGameQuestionId() {
        return gameQuestionId;
    }

    public void setGameQuestionId(int gameQuestionId) {
        this.gameQuestionId = gameQuestionId;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.lang);
        dest.writeInt(this.gameQuestionId);
        dest.writeByte(isCorrect ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
    }

    public GameOption() {
    }

    private GameOption(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.lang = in.readInt();
        this.gameQuestionId = in.readInt();
        this.isCorrect = in.readByte() != 0;
        this.id = in.readInt();
    }

    public static final Creator<GameOption> CREATOR = new Creator<GameOption>() {
        public GameOption createFromParcel(Parcel source) {
            return new GameOption(source);
        }

        public GameOption[] newArray(int size) {
            return new GameOption[size];
        }
    };
}

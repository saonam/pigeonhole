package com.huijimuhei.beacon.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class Polygon implements Parcelable {
    private int id;
    private float lat;
    private float lng;
    private String code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeFloat(this.lat);
        dest.writeFloat(this.lng);
        dest.writeString(this.code);
    }

    public Polygon() {
    }

    private Polygon(Parcel in) {
        this.id = in.readInt();
        this.lat = in.readFloat();
        this.lng = in.readFloat();
        this.code = in.readString();
    }

    public static final Parcelable.Creator<Polygon> CREATOR = new Parcelable.Creator<Polygon>() {
        public Polygon createFromParcel(Parcel source) {
            return new Polygon(source);
        }

        public Polygon[] newArray(int size) {
            return new Polygon[size];
        }
    };

    public static List<Polygon> readDb() {
        List<Polygon> list = new ArrayList<>();
        String sql = "select * from beacon_polygon";
        Cursor c = DatabaseManager.getInstance().rawQuery(sql);
        while (c.moveToNext()) {
            Polygon res = new Polygon();
            res.setId(c.getInt(c.getColumnIndex("id")));
            res.setLat(c.getFloat(c.getColumnIndex("lat")));
            res.setLng(c.getFloat(c.getColumnIndex("lng")));
            res.setCode(c.getString(c.getColumnIndex("code")));
            list.add(res);
        }
        return list;
    }
}

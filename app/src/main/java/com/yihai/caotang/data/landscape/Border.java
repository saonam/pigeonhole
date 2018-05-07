package com.yihai.caotang.data.landscape;


import android.os.Parcel;
import android.os.Parcelable;

import com.yihai.caotang.data.BaseData;

/**
 * 地理类型
 */
public class Border extends BaseData implements Parcelable {
    private double lat;          //纬度
    private double lng;          //经度
    private int landscapeId;   //景点id

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

    public int getLandscapeId() {
        return landscapeId;
    }

    public void setLandscapeId(int landscapeId) {
        this.landscapeId = landscapeId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(this.landscapeId);
    }

    public Border() {
    }

    protected Border(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.landscapeId = in.readInt();
    }

    public static final Parcelable.Creator<Border> CREATOR = new Parcelable.Creator<Border>() {
        @Override
        public Border createFromParcel(Parcel source) {
            return new Border(source);
        }

        @Override
        public Border[] newArray(int size) {
            return new Border[size];
        }
    };
}

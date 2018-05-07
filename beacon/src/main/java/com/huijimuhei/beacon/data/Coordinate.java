package com.huijimuhei.beacon.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * 坐标bean
 */
public class Coordinate implements Parcelable {

    /*横坐标*/
    public double lat;

    /*纵坐标*/
    public double lng;


    @Override
    public String toString() {
        return "坐标Coordinate为 [纬度(latitude)=" + lat + ", 经度(longitude)=" + lng + "]";
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
    }

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Coordinate() {
    }

    private Coordinate(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        public Coordinate createFromParcel(Parcel source) {
            return new Coordinate(source);
        }

        public Coordinate[] newArray(int size) {
            return new Coordinate[size];
        }
    };
}

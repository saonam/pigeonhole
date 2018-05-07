package com.yihai.caotang.event;

import com.huijimuhei.beacon.data.BleDevice;

/**
 * Created by mac on 2017/8/27.
 */

public class PositionedEvent {
    private int landscapeId;
    private int accuracy;
    private double lat;
    private double lng;

    public PositionedEvent(int landscapeId, double lat, double lng, int accuracy) {
        this.landscapeId = landscapeId;
        this.accuracy = accuracy;
        this.lat = lat;
        this.lng = lng;
    }

    public PositionedEvent(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getLandscapeId() {
        return landscapeId;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}

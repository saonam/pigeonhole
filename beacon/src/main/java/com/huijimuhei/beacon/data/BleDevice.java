package com.huijimuhei.beacon.data;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import com.huijimuhei.beacon.utils.BeaconUtils;
import com.huijimuhei.beacon.utils.MD5Utils;
import com.huijimuhei.beacon.utils.Preconditions;

import static com.huijimuhei.beacon.utils.BeaconUtils.isBeacon;
import static com.huijimuhei.beacon.utils.BeaconUtils.strConvert;
import static com.huijimuhei.beacon.utils.BeaconUtils.unsignedByteToInt;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class BleDevice implements Comparable<BleDevice>, Parcelable {

    /**
     * Beacon触发类型
     * 地理定位
     */
    public static final int TYPE_LOCATION = 0;

    /**
     * Beacon触发类型
     * 点触发
     */
    public static final int TYPE_SPOT = 1;

    /**
     * Beacon触发类型
     * 地理围栏
     */
    public static final int TYPE_FENCE = 2;

    /************
     * [DEPRECATED]
     * 特殊触发
     ************/
    public static final String SPOT_PARAM_SOUND_TRACK = "SOUND";
    public static final String SPOT_PARAM_ROUTER = "ROUTE";
    public static final String ROUTER_PARAM_QRCODE = "QRCODE";
    public static final String ROUTER_PARAM_VIDEO = "VIDEO";
    public static final String ROUTER_PARAM_POTRY_GAME = "POTRYGAME";
    public static final String ROUTER_PARAM_POTTERY_GAME = "POTTERYGAME";
    public static final String ROUTER_PARAM_VR = "VR";

    private int id;
    private double lat;
    private double lng;
    private int rssi;
    private double distance;
    private int major;
    private int minor;
    private int mPower;
    private float height;
    private String mac;
    private String uuid;
    private int type;
    private int status;
    private int land_scape_id;
    private String remark;
    private String param1;
    private String param2;
    private String param3;
    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getmPower() {
        return mPower;
    }

    public void setmPower(int mPower) {
        this.mPower = mPower;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLand_scape_id() {
        return land_scape_id;
    }

    public void setLand_scape_id(int land_scape_id) {
        this.land_scape_id = land_scape_id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    @Override
    public boolean equals(Object paramObject) {
        if (paramObject == null) {
            return false;
        }
        if (getClass() != paramObject.getClass()) {
            return false;
        }

        BleDevice beacon = (BleDevice) paramObject;
        if (!this.mac.equals(beacon.mac)) {
            return false;
        }
        return true;
    }

    /**
     * [DEPRECATED]数组排序
     *
     * @param base
     * @return
     */
    @Override
    public int compareTo(BleDevice base) {
        // TODO Auto-generated method stub
        if (rssi > base.rssi) {
            return -1;
        } else {
            return 1;
        }
    }

    public BleDevice() {
    }

    @Override
    public String toString() {
        return String.format("id:%d,rssi:%d,distance:%f,minor:%s,mac;%s,lat:%f,lng:%f,mPower:%d,", this.id, this.rssi, this.distance, this.minor, this.mac, this.lat, this.lng, this.mPower);
    }

    /**
     * 解析蓝牙扫描beacon
     *
     * @param bluetoothDevice
     * @param rssi
     * @param data
     * @return
     */
    public static BleDevice parse(BluetoothDevice bluetoothDevice, int rssi, byte[] data) {
        if (!isBeacon(data)) {
            return null;
        }
        BleDevice beacon = new BleDevice();

        String hex = strConvert(data);
        Object[] arrayOfObject = new Object[5];
        arrayOfObject[0] = hex.substring(18, 26);
        arrayOfObject[1] = hex.substring(26, 30);
        arrayOfObject[2] = hex.substring(30, 34);
        arrayOfObject[3] = hex.substring(34, 38);
        arrayOfObject[4] = hex.substring(38, 50);

        String uuid = String.format("%s-%s-%s-%s-%s", arrayOfObject);
        int major = 256 * unsignedByteToInt(data[25]) + unsignedByteToInt(data[26]);
        int minor = 256 * unsignedByteToInt(data[27]) + unsignedByteToInt(data[28]);
        int mPower = data[29];
        beacon.setMajor(major);
        beacon.setMinor(minor);
        beacon.setmPower(mPower);
        beacon.setDistance(BeaconUtils.calDistance(rssi, mPower));
        beacon.setUuid(uuid);
        beacon.setMac(bluetoothDevice.getAddress());
        beacon.setRssi(rssi);
        return beacon;
    }

    /**
     * 根据高度计算水平距离
     *
     * @param height
     * @return
     */
    public double getMeasuredDistance(double height, double rawDistance) {
        /*基站到定位终端的水平距离*/
        return Math.sqrt(Math.pow(rawDistance, 2) - Math.pow(height, 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(this.major);
        dest.writeInt(this.minor);
        dest.writeInt(this.mPower);
        dest.writeFloat(this.height);
        dest.writeString(this.mac);
        dest.writeString(this.uuid);
        dest.writeInt(this.type);
        dest.writeInt(this.status);
        dest.writeInt(this.land_scape_id);
        dest.writeString(this.remark);
        dest.writeString(this.param1);
        dest.writeString(this.param2);
        dest.writeString(this.param3);
        dest.writeString(this.created_at);
        dest.writeInt(this.rssi);
        dest.writeDouble(this.distance);
    }

    private BleDevice(Parcel in) {
        this.id = in.readInt();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.major = in.readInt();
        this.minor = in.readInt();
        this.mPower = in.readInt();
        this.height = in.readFloat();
        this.mac = in.readString();
        this.uuid = in.readString();
        this.type = in.readInt();
        this.status = in.readInt();
        this.land_scape_id = in.readInt();
        this.remark = in.readString();
        this.param1 = in.readString();
        this.param2 = in.readString();
        this.param3 = in.readString();
        this.created_at = in.readString();
        this.rssi = in.readInt();
        this.distance = in.readDouble();
    }

    public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
        public BleDevice createFromParcel(Parcel source) {
            return new BleDevice(source);
        }

        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };
}

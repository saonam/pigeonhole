package com.yihai.caotang.data.sysinfo;

import com.google.gson.GsonBuilder;
import com.yihai.caotang.data.BaseData;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SysInfo extends BaseData {
    private String appVersion;      //系统版本
    private String dataVersion;     //数据版本
    private int padId;              //设备编号
    private String imei;            //主机IMEI号
    private String rfidId;          //rfid号
    private boolean isInited;       //是否初始化
    private boolean isMatched;      //是否配对

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getRfidId() {
        return rfidId;
    }

    public void setRfidId(String rfidId) {
        this.rfidId = rfidId;
    }

    public int getPadId() {
        return padId;
    }

    public String getPadIdStr() {
        return String.valueOf(padId);
    }

    public void setPadId(int padId) {
        this.padId = padId;
    }

    public boolean isInited() {
        return isInited;
    }

    public void setInited(boolean inited) {
        isInited = inited;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}

package com.yihai.caotang.data.sysinfo;

import com.yihai.caotang.data.BaseData;

/**
 * Pad配置表
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SysInfoResponse extends BaseData {


    /**
     * jpush_alias : Pad_39
     * deprecated_at : null
     * pad_id : 39
     * status : 3
     * last_lat : 0
     * last_lng : 0
     * imei : 357557084506573
     * created_at : {"nanos":441000000,"time":1500112212441,"minutes":50,"seconds":12,"hours":17,"month":6,"year":117,"timezoneOffset":-480,"day":6,"date":15}
     * rfid_id : 123456789012345
     * jpush_registration_id : 190e35f7e07376e5e3d
     * ver : 1.0
     */

    private String jpush_alias;
    private Object deprecated_at;
    private int pad_id;
    private int status;
    private double last_lat;
    private double last_lng;
    private String imei;
    //    private String created_at;
    private String rfid_id;
    private String jpush_registration_id;
    private String ver;

    public SysInfo parse() {
        SysInfo info = new SysInfo();
        info.setAppVersion(this.ver);
        info.setImei(this.imei);
        info.setPadId(this.pad_id);
        info.setRfidId(this.rfid_id);
        return info;
    }

    public void setJpush_alias(String jpush_alias) {
        this.jpush_alias = jpush_alias;
    }

    public void setDeprecated_at(Object deprecated_at) {
        this.deprecated_at = deprecated_at;
    }

    public void setPad_id(int pad_id) {
        this.pad_id = pad_id;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setLast_lat(double last_lat) {
        this.last_lat = last_lat;
    }

    public void setLast_lng(double last_lng) {
        this.last_lng = last_lng;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setRfid_id(String rfid_id) {
        this.rfid_id = rfid_id;
    }

    public void setJpush_registration_id(String jpush_registration_id) {
        this.jpush_registration_id = jpush_registration_id;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getJpush_alias() {
        return jpush_alias;
    }

    public Object getDeprecated_at() {
        return deprecated_at;
    }

    public int getPad_id() {
        return pad_id;
    }

    public int getStatus() {
        return status;
    }

    public double getLast_lat() {
        return last_lat;
    }

    public double getLast_lng() {
        return last_lng;
    }

    public String getImei() {
        return imei;
    }

    public String getRfid_id() {
        return rfid_id;
    }

    public String getJpush_registration_id() {
        return jpush_registration_id;
    }

    public String getVer() {
        return ver;
    }

}

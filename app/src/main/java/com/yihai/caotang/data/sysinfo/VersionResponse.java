package com.yihai.caotang.data.sysinfo;

import com.yihai.caotang.data.BaseData;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class VersionResponse extends BaseData{

    /**
     * appver : 1.11
     * dataver : 1.21
     */

    private String appver;
    private String dataver;

    public void setAppver(String appver) {
        this.appver = appver;
    }

    public void setDataver(String dataver) {
        this.dataver = dataver;
    }

    public String getAppver() {
        return appver;
    }

    public String getDataver() {
        return dataver;
    }
}

package com.yihai.caotang.event;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class RegistrationReceiveEvent {
    private String mRegId;

    public RegistrationReceiveEvent(String id) {
        mRegId = id;
    }

    public String getRegId() {
        return mRegId;
    }
}

package com.yihai.caotang.event;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class ActionReceiveEvent {

    public static final String ACTION_CHANGE = "change";
    public static final String ACTION_BEENING_CHANGED = "changed";
    public static final String ACTION_MATCH = "match";
    public static final String ACTION_LEND = "lend";
    public static final String ACTION_RETURN = "return";
    public static final String ACTION_LOW_BATTERY_WARNING = "lowbatterywarning";
    public static final String ACTION_LOW_BATTERY = "lowbattery";
    public static final String ACTION_NEAR_GATE = "neargate";
    public static final String ACTION_ALERT = "alert";
    public static final String ACTION_TIME_LIMITE = "timelimite";

    private String mAction;
    private String mJson;

    public ActionReceiveEvent(String action, String json) {
        mAction = action;
        mJson = json;
    }

    public String getAction() {
        return mAction;
    }

    public String getJson() {
        return mJson;
    }
}

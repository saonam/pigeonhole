package com.yihai.caotang.event;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class BatteryNotifyEvent {
    public static final int EVENT_BATTERY_WARNING = 0;
    public static final int EVENT_BATTERY_LOCK = 1;

    private int level;
    private int event;

    public BatteryNotifyEvent(int level, int event) {
        this.level = level;
        this.event = event;
    }

    public int getLevel() {
        return level;
    }

    public int getEvent() {
        return event;
    }
}

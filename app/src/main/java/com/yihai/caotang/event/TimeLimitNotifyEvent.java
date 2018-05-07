package com.yihai.caotang.event;

/**
 * 超时通知
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class TimeLimitNotifyEvent {

    private long mTimeLeft;

    public TimeLimitNotifyEvent(long timeLeft) {
        this.mTimeLeft = timeLeft;
    }

    public long getmTimeLeft() {
        return mTimeLeft;
    }
}

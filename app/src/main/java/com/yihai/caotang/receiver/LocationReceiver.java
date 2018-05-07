package com.yihai.caotang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huijimuhei.beacon.data.BleDevice;
import com.yihai.caotang.AppController;

/**
 * 【DEPRECATED】
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class LocationReceiver extends BroadcastReceiver {

    private AppController mController;

    public LocationReceiver(AppController mController) {
        this.mController = mController;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        BleDevice device = intent.getParcelableExtra("data");
        int heading = intent.getIntExtra("heading", 0);
    }

}

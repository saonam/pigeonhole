package com.yihai.caotang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.huijimuhei.beacon.data.BleDevice;
import com.yihai.caotang.AppController;
import com.yihai.caotang.event.BeaconTriggerEvent;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * 3秒内重复触发，响应边界报警
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class FenceTriggerReceiver extends BroadcastReceiver {

    private AppController mController;
    private long lastAt;

    public FenceTriggerReceiver(AppController mController) {
        this.mController = mController;
        lastAt = -1;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (lastAt == -1) {
            lastAt = System.currentTimeMillis();
            return;
        }
        if (System.currentTimeMillis() - lastAt > 3000) {
            lastAt = System.currentTimeMillis();
            return;
        }
        BleDevice device = intent.getParcelableExtra("data");
        mController.onBeaconTriggerEvent(new BeaconTriggerEvent(device, BeaconTriggerEvent.EVENT_FENCE_TRIGGER));
        lastAt = System.currentTimeMillis();
    }

}

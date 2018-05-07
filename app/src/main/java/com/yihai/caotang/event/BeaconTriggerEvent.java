package com.yihai.caotang.event;

import com.huijimuhei.beacon.data.BleDevice;

/**
 * Created by mac on 2017/8/27.
 */

public class BeaconTriggerEvent {
    public static final int EVENT_LOCATION = 0;
    public static final int EVENT_GATE_TRIGGER = 1;
    public static final int EVENT_SPOT_TRIGGER = 2;
    public static final int EVENT_FENCE_TRIGGER = 3;

    private BleDevice device;
    private int event;
    private String type;


    public BeaconTriggerEvent(BleDevice device, int event) {
        this.device = device;
        this.event = event;
    }

    public BeaconTriggerEvent(BleDevice device, int event, String type) {
        this.device = device;
        this.event = event;
        this.type = type;
    }

    public BleDevice getDevice() {
        return device;
    }

    public int getEvent() {
        return event;
    }

    public String getType() {
        return type;
    }
}

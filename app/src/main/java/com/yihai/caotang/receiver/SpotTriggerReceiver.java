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
 * 特定点位触发
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SpotTriggerReceiver extends BroadcastReceiver {
    private static final int DURATION_MAX_TIME = 4000;
    private BleDevice mCurTrigger;
    private long triggerAt;
    private boolean isTriggered;
    private AppController mController;

    public SpotTriggerReceiver(AppController controller) {
        triggerAt = System.currentTimeMillis();
        this.mController = controller;
        isTriggered = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        BleDevice trigger = intent.getParcelableExtra("data");
//
//        if (trigger.getParam2().equals(BleDevice.SPOT_PARAM_ROUTER)) {
//            //条形码、VR场景，二维码扫描直接穿透
//            if (Integer.valueOf(trigger.getParam3()).equals(BleDevice.ROUTER_PARAM_QRCODE) || Integer.valueOf(trigger.getParam3()).equals(BleDevice.ROUTER_PARAM_VR)) {
//                HermesEventBus.getDefault().post(new BeaconTriggerEvent(trigger, BeaconTriggerEvent.EVENT_SPOT_TRIGGER, BleDevice.SPOT_PARAM_ROUTER));
//                return;
//            }
//        }
//
//        //检查是否触发
//        if (mCurTrigger != null && mCurTrigger.equals(trigger)) {
//            if (!isTriggered) {
//                isTriggered = true;
//                if (trigger.getParam2().equals(BleDevice.SPOT_PARAM_SOUND_TRACK)) {
//                    mController.onBeaconTriggerEvent(new BeaconTriggerEvent(trigger, BeaconTriggerEvent.EVENT_SPOT_TRIGGER, BleDevice.SPOT_PARAM_SOUND_TRACK));
//                } else if (Integer.valueOf(trigger.getParam2()).equals(BleDevice.SPOT_PARAM_ROUTER)) {
//                    mController.onBeaconTriggerEvent(new BeaconTriggerEvent(trigger, BeaconTriggerEvent.EVENT_SPOT_TRIGGER, BleDevice.SPOT_PARAM_ROUTER));
//                }
//            }
////                //第n次收到该信号
////                long duration = System.currentTimeMillis() - triggerAt;
////                if (duration < DURATION_MAX_TIME && !isTriggered) {
////                    //两次间隔不小于3秒并且未被触发
////                    isTriggered = true;
////                    if (Integer.valueOf(trigger.getParam2()).equals(BleDevice.SPOT_PARAM_SOUND_TRACK)) {
////                        HermesEventBus.getDefault().post(new SpotTriggerEvent(trigger, BleDevice.SPOT_PARAM_SOUND_TRACK));
////                    } else {
////                        HermesEventBus.getDefault().post(new SpotTriggerEvent(trigger, BleDevice.SPOT_PARAM_ROUTER));
////                    }
////                } else {
////                    triggerAt = System.currentTimeMillis();
////                }
//        } else {
//            //收到新的信号
//            isTriggered = false;
//            mCurTrigger = trigger;
//            triggerAt = System.currentTimeMillis();
//        }
    }

}

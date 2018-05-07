package com.yihai.caotang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huijimuhei.beacon.data.BleDevice;
import com.orhanobut.logger.Logger;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.event.BeaconTriggerEvent;
import com.yihai.caotang.utils.ToastUtils;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * [DEPRECATED]
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class GateTriggerReceiver extends BroadcastReceiver {
    private static final int DURATION_MAX_TIME = 4000;
    private BleDevice mCurTrigger;
    private String mTriggerType;
    //    private long mTriggerAt;
    private boolean mIsTriggered;
    private AppController mController;

    public GateTriggerReceiver(AppController controller) {
//        mTriggerAt = -1;
        mIsTriggered = false;
        this.mController = controller;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        BleDevice trigger = intent.getParcelableExtra("data");
        String type = intent.getStringExtra("type");

        //检查该景点是否需要触发
        if (mCurTrigger == null) {
            //第一次收到该信号
            mCurTrigger = trigger;
//            mTriggerAt = System.currentTimeMillis();
            mTriggerType = type;
        } else if (mCurTrigger.equals(trigger) && type.equals(mTriggerType)) {
            //第n次收到该信号
            if (!mIsTriggered) {
                //两次间隔不小于3秒并且未被触发
                mIsTriggered = true;
                mController.onBeaconTriggerEvent(new BeaconTriggerEvent(mCurTrigger, BeaconTriggerEvent.EVENT_GATE_TRIGGER, type));
            }
//            //第n次收到该信号
//            long duration = System.currentTimeMillis() - mTriggerAt;
//            if (duration < DURATION_MAX_TIME && !mIsTriggered) {
//                //两次间隔不小于3秒并且未被触发
//                mIsTriggered = true;
//                HermesEventBus.getDefault().post(new GateTriggerEvent(mCurTrigger, heading, type));
//            } else {
//                mTriggerAt = System.currentTimeMillis();
//            }
        } else {
            //收到新的信号
            mIsTriggered = false;
            mCurTrigger = trigger;
            mTriggerType = type;
//            mTriggerAt = System.currentTimeMillis();
        }
    }

}

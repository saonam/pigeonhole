package com.yihai.caotang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huijimuhei.beacon.data.BleDevice;
import com.yihai.caotang.AppController;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;
import com.yihai.caotang.data.session.Session;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.event.PositionedEvent;

import static com.huijimuhei.beacon.BeaconScanner.ACCURACY_GPS;

/**
 * 景点位置触发
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class PositionReceiver extends BroadcastReceiver {

    private AppController mController;

    public PositionReceiver(AppController mController) {
        this.mController = mController;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int landscapeId = intent.getIntExtra("id", -1);
        int accuracy = intent.getIntExtra("accuracy", ACCURACY_GPS);

        //保存session
        SessionManager.getInstance().getSession().setAccuracy(accuracy);
        if (accuracy == ACCURACY_GPS) {
            if (SessionManager.getInstance().getSession().isGpsCorrect()) {
                SessionManager.getInstance().getSession().setLocLandscape(null);
            }
        } else {
            LandScape landScape = LandScapeManager.getInstance().getLandscape(landscapeId);
            SessionManager.getInstance().getSession().setLocLandscape(landScape);
        }

        //事件触发
        mController.onBeaconPositionedEvent(new PositionedEvent(accuracy));
    }

}

package com.yihai.caotang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yihai.caotang.AppController;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.event.PositionedEvent;

import static com.huijimuhei.beacon.BeaconScanner.ACCURACY_GPS;

/**
 * 在unity下进入景点，刷新景点次数的广播接收器
 *
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class LandScapeLogReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_LANDSCAPE_LOG = "com.yihai.caotang.landscapelog";
    private AppController mController;

    public LandScapeLogReceiver(AppController mController) {
        this.mController = mController;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int landscapeId = intent.getIntExtra("data", -1);
        if (landscapeId != -1) {
            //事件触发
            mController.updateLandscapeLog(landscapeId);
        }
    }

}

package com.yihai.caotang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.yihai.caotang.AppController;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.event.BatteryNotifyEvent;
import com.yihai.caotang.event.TimeLimitNotifyEvent;

/**
 * 电量监控
 */
public class TimeLimitNotifyRecevier extends BroadcastReceiver {
    private AppController mController;

    public TimeLimitNotifyRecevier(AppController controller) {
        this.mController = controller;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long duration = intent.getLongExtra("data", 0);
    }


}
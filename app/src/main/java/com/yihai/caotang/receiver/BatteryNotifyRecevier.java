package com.yihai.caotang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.yihai.caotang.AppController;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.event.ActionReceiveEvent;
import com.yihai.caotang.event.BatteryNotifyEvent;

import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * 电量监控
 */
public class BatteryNotifyRecevier extends BroadcastReceiver {
    private Integer curBattery;
    private AppController mController;

    public BatteryNotifyRecevier(AppController controller) {
        this.mController = controller;
        this.curBattery = 0;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppController.DEBUG) {
            return;
        }

        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        int levelPercent = (int) (((float) level / scale) * 100);

        //更新缓存
        updateCache(level);
        //检查电量
        if (level < 20 && level > 10) {
            //是否已触发
            if (SessionManager.getInstance().getSession().isLowBatteryWarningTriggered()) {
                return;
            }
            SessionManager.getInstance().getSession().setLowBatteryWarningTriggered(true);
            //低电量提醒
            HermesEventBus.getDefault().post(new BatteryNotifyEvent(level, BatteryNotifyEvent.EVENT_BATTERY_WARNING));
//            mController.onBatteryNotifyEvent(new BatteryNotifyEvent(level, BatteryNotifyEvent.EVENT_BATTERY_WARNING));
        } else if (level < 20) {
            //是否已触发
            if (SessionManager.getInstance().getSession().isLowBatteryLockTriggered()) {
                return;
            }
            SessionManager.getInstance().getSession().setLowBatteryLockTriggered(true);
            //低电量锁定
            HermesEventBus.getDefault().post(new BatteryNotifyEvent(level, BatteryNotifyEvent.EVENT_BATTERY_LOCK));
            mController.onBatteryNotifyEvent(new BatteryNotifyEvent(level, BatteryNotifyEvent.EVENT_BATTERY_LOCK));
        } else if (level > 90) {
            //如果充电了，取消session的设定
            SessionManager.getInstance().getSession().setLowBatteryLockTriggered(false);
            SessionManager.getInstance().getSession().setLowBatteryWarningTriggered(false);
        }
    }

    private synchronized void updateCache(int level) {
        curBattery = level;
        SessionManager.getInstance().getSession().setBattery(curBattery);
    }
}
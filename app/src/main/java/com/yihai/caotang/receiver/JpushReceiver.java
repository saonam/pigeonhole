package com.yihai.caotang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yihai.caotang.AppController;
import com.yihai.caotang.data.CmdResponse;
import com.yihai.caotang.event.ActionReceiveEvent;
import com.yihai.caotang.event.RegistrationReceiveEvent;
import com.yihai.caotang.utils.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 * 自定义接收器
 * ID：190e35f7e07376e5e3d
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JpushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private static final int CMD_IGNORE_DURATION = 2000;

    private AppController mController;

    public JpushReceiver() {
        this.mController=AppController.getInstance();
    }

//    public JpushReceiver(AppController mController) {
//        this.mController = mController;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                //[JpushReceiver] 接收Registration Id
                didAfterReceiveRegistration(bundle);
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                //[JpushReceiver] 接收到推送下来的自定义消息
                didAfterReceiveAction(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取到registration id
     */
    private void didAfterReceiveRegistration(Bundle bundle) {
        String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
        HermesEventBus.getDefault().post(new RegistrationReceiveEvent(regId));
    }


    /**
     * 获取到命令后的处理
     *
     * @param bundle
     */
    private void didAfterReceiveAction(Bundle bundle) {


        String jsonStr = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String action = "";
        try {
            String extraStr = bundle.getString(JPushInterface.EXTRA_EXTRA);
            JSONObject extraJson = new JSONObject(extraStr);
            action = (String) extraJson.get("action");
        } catch (JSONException e) {
            Log.e(TAG, "Get message extra JSON error!");
        }

        //走controller
        mController.onActionEvent(new ActionReceiveEvent(action, jsonStr));
        //走eventbus
        HermesEventBus.getDefault().post(new ActionReceiveEvent(action, jsonStr));
    }
}

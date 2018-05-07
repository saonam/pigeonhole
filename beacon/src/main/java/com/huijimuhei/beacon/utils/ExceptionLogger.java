package com.huijimuhei.beacon.utils;

import android.content.Context;

import java.io.File;
import java.io.PrintWriter;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class ExceptionLogger {

    public static void log(Context context, Exception e) {
        File file = new File(context.getCacheDir().getAbsolutePath() + "/err/");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            PrintWriter writer = new PrintWriter(context.getCacheDir().getAbsolutePath() + "/err/" + System.currentTimeMillis() + "_aidl.log");
            e.printStackTrace(writer);
            writer.close();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
//                        CommonUtils.sendTextMail("err aidl log from " + CommonUtils.getUniquePsuedoID(), CommonUtils.getDeviceInfo() + Log.getStackTraceString(e));
            }
        }).start();

    }
}

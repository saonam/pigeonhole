package com.yihai.caotang.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.yihai.caotang.AppContext;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class NetUtils {
    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                AppContext.getInstance().getSystemService(AppContext.getInstance().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) AppContext.getInstance().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();

        // 使用3G时无需mac地址，所以无mac地址时暂定处理成“3G”
        if (TextUtils.isEmpty(info.getMacAddress())) {
            return "3G";
        }
        return info.getMacAddress();
    }
}

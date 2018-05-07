package com.yihai.caotang.client;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yihai.caotang.data.session.Session;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.data.session.SessionJSON;
import com.yihai.caotang.data.sysinfo.SysInfoManager;
import com.yihai.caotang.utils.DateUtils;
import com.yihai.caotang.utils.MD5Utils;

/**
 * 收到JPUSH命令后向后端确认请求
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class RequestClient {

    /**
     * 心跳请求
     *
     * @param responseHandler
     */
    public static void postHeartBeat(TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_HEART_BEAT;
        RequestParams params = new RequestParams();
        Session session = SessionManager.getInstance().getSession();
        params.put("lat", session.getLat());
        params.put("lng", session.getLng());
        params.put("beacon", "");
        params.put("json", SessionJSON.requestJson(session));
        params.put("time_stamp", DateUtils.now());
        params.put("pad_id", SysInfoManager.getSysInfo().getPadId());
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 出租请求
     *
     * @param padId
     * @param responseHandler
     */
    public static void postLend(String padId, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_LEND;
        RequestParams params = new RequestParams();
        params.put("pad_id", padId);
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 续借请求
     *
     * @param responseHandler
     */
    public static void postNotKeepLend(TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_KEEP_LEND;
        RequestParams params = new RequestParams();
        params.put("pad_id", String.valueOf(SysInfoManager.getSysInfo().getPadId()));
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 更换请求
     *
     * @param padId
     * @param responseHandler
     */
    public static void postChange(String padId, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_CHANGE;
        RequestParams params = new RequestParams();
        params.put("pad_id", padId);
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 被更换请求
     *
     * @param padId
     * @param responseHandler
     */
    public static void postBeingChanged(String padId, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_BEING_CHANGE;
        RequestParams params = new RequestParams();
        params.put("pad_id", padId);
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 归还请求
     *
     * @param padId
     * @param responseHandler
     */
    public static void postReturn(String padId, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_RETURN;
        RequestParams params = new RequestParams();
        params.put("pad_id", padId);
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 登录
     *
     * @param name
     * @param pwd
     * @param responseHandler
     */
    public static void postLogin(String name, String pwd, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_LOGIN_IN;
        RequestParams params = new RequestParams();
        params.put("login_name", name);
        params.put("login_pwd", MD5Utils.md5(pwd));
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 初始化设备
     *
     * @param responseHandler
     */
    public static void postInitDevice(String regid, String imei, String ver, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_INIT_DEVICE;
        RequestParams params = new RequestParams();
        params.put("regID", regid);
        params.put("imei", imei);
        params.put("ver", ver);
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 配对设备
     *
     * @param responseHandler
     */
    public static void postMatchDevice(String padId, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_MATCH_DEVICE;
        RequestParams params = new RequestParams();
        params.put("pad_id", padId);
        BaseClient.post(url, params, responseHandler);
    }

    /**
     * 启动app握手
     *
     * @param responseHandler
     */
    public static void getHandShake(TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_GET_HAND_SHAKE + "/" + SysInfoManager.getSysInfo().getPadId();
        RequestParams params = new RequestParams();
        BaseClient.get(url, params, responseHandler);
    }

    /**
     * 报警请求
     *
     * @param lat
     * @param lng
     * @param beaconId
     * @param warn
     * @param responseHandler
     */
    public static void postWarning(double lat, double lng, String beaconId, String warn, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_WARNNING;
        RequestParams params = new RequestParams();
        params.put("pad_id", SysInfoManager.getSysInfo().getPadId());
        params.put("lat", lat);
        params.put("lng", lng);
        params.put("warn", warn);
        params.put("beacon_id",beaconId);
        params.put("timestamp", DateUtils.now());
        BaseClient.post(url, params, responseHandler);
    }


    /**
     * 版本检查
     *
     * @param responseHandler
     */
    public static void getVersionCheck(TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_GET_VERSION_CHECK;
        RequestParams params = new RequestParams();
        BaseClient.get(url, params, responseHandler);
    }
}

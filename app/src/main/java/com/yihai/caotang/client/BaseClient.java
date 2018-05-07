package com.yihai.caotang.client;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.utils.NetUtils;
import com.yihai.caotang.utils.ToastUtils;

public class BaseClient {

    private static final String URL_BASE = "http://101.201.238.255";//"http://192.168.31.5:8080";// "http://192.168.135.113:8080";//

    public static String URL_GET_HAND_SHAKE = "/ws/pad/status";                          //重启app握手
    public static String URL_POST_HEART_BEAT = "/ws/pad/log";                     //心跳请求
    public static String URL_POST_LOGIN_IN = "/ws/user/login";                           //登录
    public static String URL_POST_INIT_DEVICE = "/ws/pad/init";                         //初始化设备
    public static String URL_POST_MATCH_DEVICE = "/ws/pad/match";                        //配对设备
    public static String URL_POST_LEND = "/ws/pad/confirmlend";                          //租借
    public static String URL_POST_KEEP_LEND = "/ws/pad/keeplend";                       //续租
    public static String URL_POST_RETURN = "/ws/pad/return";                            //归还
    public static String URL_POST_CHANGE = "/ws/pad/change";                            //更换
    public static String URL_POST_BEING_CHANGE = "/ws/pad/changed";                      //被更换
    public static String URL_POST_WARNNING = "/ws/pad/warning";                        //报警
    public static String URL_GET_VERSION_CHECK = "/ws/app/ver";                         //版本检查
    public static String URL_DOWNLOAD_PATH = URL_BASE + "/data/";                                   //数据下载地址


    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (!NetUtils.isConnected()) {
            ToastUtils.show(AppContext.getInstance(), "网络未连接");
            return;
        }
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (!NetUtils.isConnected()) {
            ToastUtils.show(AppContext.getInstance(), "网络未连接");
            return;
        }
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void cancel() {
        client.cancelAllRequests(true);
    }

    private static String getAbsoluteUrl(String url) {
        return URL_BASE + url;
    }

}

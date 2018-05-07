package com.yihai.caotang.client;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * TODO 可能和发送请求不同
 * 强制向后端进行请求
 */
public class CommandClient {

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
     * @param padId
     * @param responseHandler
     */
    public static void postKeepLend(String padId, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_KEEP_LEND;
        RequestParams params = new RequestParams();
        params.put("pad_id", padId);
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
    public static void postBeingChange(String padId, TextHttpResponseHandler responseHandler) {
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
     * 报警请求
     *
     * @param lat
     * @param lng
     * @param padId
     * @param beaconId
     * @param warn
     * @param responseHandler
     */
    public static void postWarning(double lat, double lng, String padId, String beaconId, String warn, TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_WARNNING;
        RequestParams params = new RequestParams();
        BaseClient.post(url, params, responseHandler);
    }
}

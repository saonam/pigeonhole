package com.yihai.caotang.client;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class UpdateClient {
    /**
     * 检查app版本
     *
     * @param responseHandler
     */
    public static void postCheckUpdate(TextHttpResponseHandler responseHandler) {
        String url = BaseClient.URL_POST_INIT_DEVICE;
        RequestParams params = new RequestParams();
        params.put("tag", "init");
        BaseClient.post(url, params, responseHandler);
    }
}

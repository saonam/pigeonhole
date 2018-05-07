package com.yihai.caotang.adapter;

import android.view.View;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public interface OnItemClickLisener {

    void OnItemClick(View view, int position);

    void OnFunctionClick(View view, int position, int functionFlag);
}

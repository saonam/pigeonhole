package com.yihai.caotang.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class FontButton extends Button {
    public FontButton(Context context) {
        super(context);
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
//        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/arial.ttf"), style);
    }

}

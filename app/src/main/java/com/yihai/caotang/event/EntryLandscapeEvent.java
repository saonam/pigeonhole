package com.yihai.caotang.event;

import com.yihai.caotang.data.landscape.LandScape;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class EntryLandscapeEvent {
    public static final int ENTRY = 1;
    public static final int LEAVE = 2;
    public static final int UNITY_TURN_BACK =3;

    int type;
    LandScape landScape;

    public EntryLandscapeEvent(int type, LandScape landScape) {
        this.type = type;
        this.landScape = landScape;
    }

    public int getType() {
        return type;
    }
    public LandScape getLandScape() {
        return landScape;
    }
}

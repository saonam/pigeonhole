package com.huijimuhei.beacon;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public interface IBeaconClient {
    void startScan();

    void warm();

    void stopScan();

}

// IBeaconService.aidl
package com.huijimuhei.beacon;

interface IBeaconService {
    void startScan();
    void warm();
    void stopScan();
}

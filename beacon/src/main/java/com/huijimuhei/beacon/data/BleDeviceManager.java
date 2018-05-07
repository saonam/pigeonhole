package com.huijimuhei.beacon.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class BleDeviceManager {
    private static BleDeviceManager mInstance;
    private List<BleDevice> mCache;

    public synchronized static BleDeviceManager getInstance() {
        if (mInstance == null) {
            mInstance = new BleDeviceManager();
        }
        return mInstance;
    }

    private BleDeviceManager() {
        this.mCache = new ArrayList<>();
    }

    private void readDb() {
        String sql = "select * from beacon_device";
        Cursor c = DatabaseManager.getInstance().rawQuery(sql);
        while (c.moveToNext()) {
            BleDevice res = new BleDevice();
            res.setId(c.getInt(c.getColumnIndex("id")));
            res.setLat(c.getDouble(c.getColumnIndex("lat")));
            res.setLng(c.getDouble(c.getColumnIndex("lng")));
            res.setMinor(c.getInt(c.getColumnIndex("minor")));
            res.setMajor(c.getInt(c.getColumnIndex("major")));
            res.setHeight(c.getFloat(c.getColumnIndex("height")));
            res.setmPower(c.getInt(c.getColumnIndex("mPower")));
            res.setMac(c.getString(c.getColumnIndex("mac")));
            res.setUuid(c.getString(c.getColumnIndex("uuid")));
            res.setType(c.getInt(c.getColumnIndex("type")));
            res.setStatus(c.getInt(c.getColumnIndex("status")));
            res.setLand_scape_id(c.getInt(c.getColumnIndex("land_scape_id")));
            res.setParam1(c.getString(c.getColumnIndex("param1")));
            res.setParam2(c.getString(c.getColumnIndex("param2")));
            res.setParam3(c.getString(c.getColumnIndex("param3")));
            res.setRemark(c.getString(c.getColumnIndex("remark")));
            res.setCreated_at(c.getString(c.getColumnIndex("created_at")));
            mCache.add(res);
        }
    }

    public Map<String, BleDevice> getAll() {
        Map<String, BleDevice> res = new HashMap<>();
        if (mCache.size() == 0) {
            readDb();
        }
        for (BleDevice item : mCache) {
            String key = item.getMac();
            res.put(key, item);
        }
        return res;
    }

    public Map<String, BleDevice> getFence() {
        Map<String, BleDevice> res = new HashMap<>();
        if (mCache.size() == 0) {
            readDb();
        }
        for (BleDevice item : mCache) {
            if (item.getType() == BleDevice.TYPE_FENCE) {
                String key = item.getMac();
                res.put(key, item);
            }
        }
        return res;
    }

//    public Map<String, BleDevice> getGate() {
//        Map<String, BleDevice> res = new HashMap<>();
//        if (mCache.size() == 0) {
//            readDb();
//        }
//        for (BleDevice item : mCache) {
//            if (item.getType() == BleDevice.TYPE_GATE) {
//                String key = item.md5();
//                res.put(key, item);
//            }
//        }
//        return res;
//    }

    public Map<String, BleDevice> getLocate() {
        Map<String, BleDevice> res = new HashMap<>();
        if (mCache.size() == 0) {
            readDb();
        }
        for (BleDevice item : mCache) {
            if (item.getType() == BleDevice.TYPE_LOCATION) {
                String key = item.getMac();
                res.put(key, item);
            }
        }
        return res;
    }
}

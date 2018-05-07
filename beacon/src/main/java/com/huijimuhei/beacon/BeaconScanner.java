package com.huijimuhei.beacon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.huijimuhei.beacon.algorithm.TrilaterationAverage;
import com.huijimuhei.beacon.data.BleDevice;
import com.huijimuhei.beacon.data.BleDeviceManager;
import com.huijimuhei.beacon.data.Coordinate;
import com.huijimuhei.beacon.data.DatabaseManager;
import com.huijimuhei.beacon.sensor.HeadingDetector;
import com.huijimuhei.beacon.utils.BeaconUtils;

import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.BLUETOOTH_SERVICE;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class BeaconScanner {

    /**
     * Beacon扫描间隔
     */
    private static final int SCAN_LOOP_INTERVAL = 1000;

    /**
     * 扫描准备间隔时长
     */
    private static final int SCAN_BREAK_INTERVAL = 50;

    /**
     * 位置检查间隔
     */
    private static final int SCAN_POSITION_CHECK_INTERVAL = 2;

    /**
     * 找到beacon
     */
    public static final String ACTION_BEACON_FOUND = "com.huijimuhe.ibeacon.found";

    /**
     * beacon定位成功
     */
    public static final String ACTION_BEACON_POSITIONED = "com.huijimuhe.ibeacon.positioned";

    /**
     * beacon定位坐标成功
     */
    public static final String ACTION_BEACON_LOCATED = "com.huijimuhe.ibeacon.located";

    /**
     * 地理边界触发
     */
    public static final String ACTION_BEACON_FENCE_TRIGGERED = "com.huijimuhe.ibeacon.fence";

    /**
     * 特殊触发点触发
     */
    public static final String ACTION_BEACON_SPOT_TRIGGERED = "com.huijimuhe.ibeacon.spot";

    /**
     * 朝向改变
     */
    public static final String ACTION_HEADING_CHANGED = "com.huijimuhe.ibeacon.heading";

    /**
     * 发生错误
     */
    public static final String ACTION_BEACION_ERROR = "com.huijimuhe.ibeacon.error";

    /**
     * 扫描精度高
     */
    public static final int ACCURACY_HIGH = 1;

    /**
     * 扫描精度中
     */
    public static final int ACCURACY_MEDIUM = 2;

    /**
     * 扫描精度低
     */
    public static final int ACCURACY_LOW = 3;

    /**
     * 无扫描结果
     */
    public static final int ACCURACY_GPS = 4;

    private Context mContext;
    private Handler mWorkHandler;                                       //循环handler
    private DidAfterScanTask afterScanTask;                             //循环task

    private BluetoothAdapter adapter;                                   //蓝牙adapter
    private final BluetoothAdapter.LeScanCallback beaconScanCallback;   //扫描结果
    private HeadingDetector mHeadingDetector;                           //轨迹sensor

    private ArrayList<BleDevice> mBleDeviceFoundInLoop;                 //单次扫描循环内获取的beacon
    private Map<String, BleDevice> mBleDeviceCache;                     //所有的beacon
    private Map<Integer, Integer> mScanLocationLoopResult;                      //用于景点定位的结果
    private Map<String, Integer> mScanSpotLoopResult;                      //用于spot定位的结果

    private boolean isEntered = false;              //是否进入景点
    private int curLandscapeId = -1;                  //当前景点id
    private int loopCount = 0;                      //循环次数
    private int lostCount = 0;                      //丢失次数
    private int stepCount = 0;                      //记步
    private int mAccuarcy = ACCURACY_GPS;           //当前精度

    public BeaconScanner(Context c) {
        mContext = c;
        mWorkHandler = new Handler(Looper.getMainLooper());
        afterScanTask = new DidAfterScanTask();

        mBleDeviceFoundInLoop = new ArrayList<>();

        BluetoothManager localBluetoothManager = (BluetoothManager) c.getSystemService(BLUETOOTH_SERVICE);
        adapter = localBluetoothManager.getAdapter();
        beaconScanCallback = new BeaconScanCallback();

        mHeadingDetector = new HeadingDetector(mContext);

        mScanLocationLoopResult = new HashMap<>();
        mScanSpotLoopResult = new HashMap<>();
    }

    /*********************
     * 数据库
     *********************/

    /**
     * 初始化缓存数据
     */
    public synchronized void warmUp() {
        //缓存
        DatabaseManager.init(mContext);
        mBleDeviceCache = BleDeviceManager.getInstance().getAll();
    }

    /**
     * 启动扫描
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    protected void startScan() {
        if (!adapter.startLeScan(beaconScanCallback)) {
            broadcastError();
            adapter.stopLeScan(beaconScanCallback);
            adapter.startLeScan(beaconScanCallback);
        }
        mWorkHandler.postDelayed(afterScanTask, SCAN_LOOP_INTERVAL);
    }

    /**
     * 启动传感器
     */
    protected void startObtain(){
        mHeadingDetector.startObtain();
    }
    /**
     * 循环过程中停止扫描
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    protected void stopScanLoop() {
        this.adapter.stopLeScan(beaconScanCallback);
    }

    /**
     * 完全停止扫描
     * 结束传感器
     */
    @SuppressWarnings({"deprecation", "unchecked"})
    protected void stopScan() {
        this.adapter.stopLeScan(beaconScanCallback);
        this.mWorkHandler.removeCallbacks(this.afterScanTask);
        this.mHeadingDetector.stopObtain();
    }

    /**
     * 生命周期:销毁
     */
    protected void onDestory() {
        if (this.adapter != null) {
            stopScan();
            this.mWorkHandler.removeCallbacks(this.afterScanTask);
        }
    }

    /**
     * 蓝牙扫描监听
     */
    private class BeaconScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            //解析是否是beacon
            BleDevice beacon = BleDevice.parse(device, rssi, scanRecord);
            if (beacon == null || rssi < -100) {
                return;
            }

            //添加入扫描
            BleDevice match = mBleDeviceCache.get(beacon.getMac());
            if (match != null) {
                beacon.setId(match.getId());
                beacon.setLand_scape_id(match.getLand_scape_id());
                beacon.setLat(match.getLat());
                beacon.setLng(match.getLng());
                beacon.setHeight(match.getHeight());
                beacon.setParam1(match.getParam1());
                beacon.setParam2(match.getParam2());
                beacon.setParam3(match.getParam3());
                beacon.setType(match.getType());
                beacon.setRemark(match.getRemark());
                beacon.setDistance(BeaconUtils.calMeasuredDistance(beacon.getHeight(), beacon.getDistance()));
                mBleDeviceFoundInLoop.add(beacon);
            }
        }
    }

    private void broadcastError() {
        Intent intent = new Intent(ACTION_BEACION_ERROR);
        mContext.sendBroadcast(intent);
    }

    private class DidAfterScanTask implements Runnable {

        @Override
        public synchronized void run() {
            //停止扫描
            stopScanLoop();
            //查看扫描结果
            didAfterScan();
            //启动下一次扫描
            mWorkHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScan();
                }
            }, SCAN_BREAK_INTERVAL);
        }

        /**
         * 检查扫描结果
         */
        private void didAfterScan() {
            //无信号继续轮询
            if (mBleDeviceFoundInLoop.size() == 0) {
                //检查景点位置
                checkLandscape();
//                //检查spot
//                checkSpot(new ArrayList<BleDevice>());
                return;
            }
            //深拷贝
            ArrayList<BleDevice> temp = (ArrayList<BleDevice>) mBleDeviceFoundInLoop.clone();
            //数据按信号排序
            Collections.sort(temp);
            //广播结果
            broadcastFound(temp);
            //刷新位置扫描结果
            refreshPositionScanResult(temp);
            //检查景点位置
            checkLandscape();
            //检查是否到达边界
            checkFence(temp);
            //检查是否触发特定点
            checkSpot(temp);
            //计算坐标
            checkLocation(temp);
            //重置计算缓存
            mBleDeviceFoundInLoop.clear();
            temp = null;
        }

        /**
         * 刷新位置
         *
         * @param temp
         */
        private void refreshPositionScanResult(ArrayList<BleDevice> temp) {
            ArrayList<BleDevice> matches = new ArrayList<>();
            for (BleDevice item : temp) {
                if (item.getType() == BleDevice.TYPE_LOCATION) {
                    //距离判断
                    if (!isEntered) {
                        //未进入景点
                        if (item.getDistance() < Double.valueOf(item.getParam1())) {
                            matches.add(item);
                        }
                    } else {
                        //进入景点是否离开
                        if (item.getDistance() < Double.valueOf(item.getParam1()) * 1.3) {
                            matches.add(item);
                        }
                    }
                }
            }
            //至少2个match，检查是否在两个景点中间位置
            if (matches.size() > 1) {
                BleDevice first = matches.get(0);
                for (int i = 1; i < matches.size(); i++) {
                    if (first.getLand_scape_id() != matches.get(i).getLand_scape_id()) {
                        if (Math.abs(first.getDistance() - matches.get(i).getDistance()) < Double.valueOf(first.getParam1())) {
                            return;
                        }
                    }
                }
            }
            //加入扫描结果
            for (BleDevice item : matches) {
                addScanLocationLoopResult(item.getLand_scape_id());
            }

        }

        /**
         * 检查景点
         */
        private void checkLandscape() {
            if (loopCount == SCAN_POSITION_CHECK_INTERVAL) {
                //如果没有移动，不检查
                //TODO 归还再出租的时候，陀螺仪需要生命周期走一遍
                if (!isWalking()) {
                    //重新初始化
                    loopCount = 0;
                    mScanLocationLoopResult.clear();
                    return;
                }
                //没有景点扫描结果
                if (mScanLocationLoopResult.size() == 0) {
                    if (stepCount < 2) {
                        //走的很慢,重新初始化
                        loopCount = 0;
                        mScanLocationLoopResult.clear();
                        return;
                    } else {
                        //再次检查
                        loopCount++;
                        return;
                    }
                }
                //排序景点扫描结果
                List<Map.Entry<Integer, Integer>> counts = new ArrayList<>(mScanLocationLoopResult.entrySet());
                Collections.sort(counts, new Comparator<Map.Entry<Integer, Integer>>() {
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        return (o2.getValue() - o1.getValue());
                    }
                });
                //找出次数最多的景点
                Map.Entry<Integer, Integer> first = counts.get(0);
                //已经在该景点不广播
                if (first.getValue() >= 1) {//&& curLandscapeId != first.getKey()
                    curLandscapeId = first.getKey();
                    isEntered = true;
                    broadcastPosition(curLandscapeId, ACCURACY_HIGH);
                }
                //重新初始化
                loopCount = 0;
                mScanLocationLoopResult.clear();
                return;
            } else if (loopCount == SCAN_POSITION_CHECK_INTERVAL + 2) {
                //判断离开
                if (mScanLocationLoopResult.size() == 0) {//&& mAccuarcy != ACCURACY_HIGH
                    isEntered = false;
                    curLandscapeId = -1;
                    broadcastPosition(curLandscapeId, ACCURACY_GPS);
                }
                //重新初始化
                loopCount = 0;
                mScanLocationLoopResult.clear();
                return;
            }
            //计数增加
            loopCount++;
        }

        /**
         * 添加扫描结果
         *
         * @param id
         */
        private void addScanLocationLoopResult(int id) {
            if (mScanLocationLoopResult.get(id) == null) {
                mScanLocationLoopResult.put(id, 1);
            } else {
                mScanLocationLoopResult.put(id, mScanLocationLoopResult.get(id) + 1);
            }
        }

        /**
         * 是否在行走
         *
         * @return
         */
        private boolean isWalking() {
            int cur = mHeadingDetector.getStep();
            int gap = cur - stepCount;
            if (gap == 0) {
                stepCount = cur;
                return false;
            } else {
                stepCount = cur;
                return true;
            }
        }

        /**
         * 计算坐标
         */
        private void checkLocation(ArrayList<BleDevice> temp) {
            //检查是否可以进行计算
            if (temp.size() < 3) {
                return;
            }
            try {
                TrilaterationAverage trilaterationAverage = new TrilaterationAverage();
                RealVector result = trilaterationAverage.getNewAverage(temp);
                if (result != null) {
                    //广播坐标
                    broadcastLocation(new Coordinate(result.getEntry(0), result.getEntry(1)));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /**
         * 检查触发点
         */
        private void checkSpot(ArrayList<BleDevice> temp) {
            ArrayList<BleDevice> matches = new ArrayList<>();
            for (BleDevice item : temp) {
                if (item.getType() == BleDevice.TYPE_SPOT) {
                    if (item.getDistance() < Double.valueOf(item.getParam1())) {
                        matches.add(item);
//                        addScanSpotLoopResult(item.getMac());
                    }
                    break;
                }
            }
            if (matches.size() == 1) {
                broadcastSpot(matches.get(0));
            } else if (matches.size() > 1) {
//                BleDevice first = matches.get(0);
//                for (int i = 1; i < matches.size(); i++) {
//                    if (Math.abs(first.getDistance() - matches.get(i).getDistance()) < 0.3) {
//                        return;
//                    }
//                }
                broadcastSpot(matches.get(0));
            }
//            if (loopCount == SCAN_POSITION_CHECK_INTERVAL) {
//                //排序景点扫描结果
//                List<Map.Entry<String, Integer>> counts = new ArrayList<>(mScanSpotLoopResult.entrySet());
//                Collections.sort(counts, new Comparator<Map.Entry<String, Integer>>() {
//                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//                        return (o2.getValue() - o1.getValue());
//                    }
//                });
//                //找出次数最多的spot
//                Map.Entry<String, Integer> first = counts.get(0);
//                if (first.getValue() >= 2) {
//                    broadcastSpot(mBleDeviceCache.get(first.getKey()));
//                }
//                mScanSpotLoopResult.clear();
//            }


        }

        private void addScanSpotLoopResult(String mac) {
            if (mScanSpotLoopResult.get(mac) == null) {
                mScanSpotLoopResult.put(mac, 1);
            } else {
                mScanSpotLoopResult.put(mac, mScanSpotLoopResult.get(mac) + 1);
            }
        }

        /**
         * 检查边界
         */
        private void checkFence(ArrayList<BleDevice> temp) {
            for (BleDevice item : temp) {
                if (item.getType() == BleDevice.TYPE_FENCE) {
                    //TODO default setting 5m
                    if (item.getDistance() < 5) {
                        broadcastFence(item);
                    }
                    break;
                }
            }
        }

        /**
         * 位置广播
         */
        private void broadcastPosition(int landscapeId, int accuracy) {
            Intent intent = new Intent(ACTION_BEACON_POSITIONED);
            intent.putExtra("id", landscapeId);
            intent.putExtra("accuracy", accuracy);
            mContext.sendBroadcast(intent);
        }


        /**
         * 找到beacon广播
         */
        private void broadcastFound(ArrayList<BleDevice> data) {
            Intent intent = new Intent(ACTION_BEACON_FOUND);
            intent.putParcelableArrayListExtra("data", data);
            intent.putExtra("heading", mHeadingDetector.getAngle());
            mContext.sendBroadcast(intent);
        }

        /**
         * 位置广播
         */
        private void broadcastLocation(Coordinate loc) {
            Intent intent = new Intent(ACTION_BEACON_LOCATED);
            intent.putExtra("data", loc);
            intent.putExtra("heading", mHeadingDetector.getAngle());
            mContext.sendBroadcast(intent);
        }

        /**
         * 找到围栏广播
         */
        private void broadcastFence(BleDevice device) {
            Intent intent = new Intent(ACTION_BEACON_FENCE_TRIGGERED);
            intent.putExtra("data", device);
            intent.putExtra("heading", mHeadingDetector.getAngle());
            mContext.sendBroadcast(intent);
        }

        /**
         * 特定点广播
         */
        private void broadcastSpot(BleDevice device) {
            Intent intent = new Intent(ACTION_BEACON_SPOT_TRIGGERED);
            intent.putExtra("data", device);
            intent.putExtra("heading", mHeadingDetector.getAngle());
            mContext.sendBroadcast(intent);
        }
    }
}

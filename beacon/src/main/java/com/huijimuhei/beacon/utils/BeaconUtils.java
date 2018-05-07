package com.huijimuhei.beacon.utils;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class BeaconUtils {

    /**
     * 检查是否是beacon
     *
     * @param paramArrayOfByte
     * @return
     */
    public static boolean isBeacon(byte[] paramArrayOfByte) {
        return (paramArrayOfByte != null) && (paramArrayOfByte[5] == 76) && (paramArrayOfByte[6] == 0);
    }

    /**
     * 根据rssi、测量常量计算距离
     *
     * @param rssi
     * @param mPower
     * @return
     */
    public static double calDistance(double rssi, double mPower) {
        //TODO TEST THE TXPOWER VALUE
        mPower = -66;
        if (rssi >= -50) {
            return 1.0D;
        } else {
            double var1 = rssi / mPower;
            double var3 = 0.96D + Math.pow(Math.abs(rssi), 3.0D) % 10.0D / 150.0D;
            return var1 <= 1.0D ? Math.pow(var1, 9.98D) * var3 : (0.103D + 0.89978D * Math.pow(var1, 7.71D)) * var3;
        }
//        double d1;
//        if ((rssi >= 0) || (mPower >= 0)) {
//            d1 = -1.0D;
//        }
//
//        double d2 = rssi / mPower;
//        double d3 = 0.96D + Math.pow(Math.abs(rssi), 3.0D) % 10.0D / 150.0D;
//        if (d2 <= 1.0D) {
//            return d3 * Math.pow(d2, 9.98D);
//        }
//        d1 = Math.max(0.0D, d3 * (0.103D + 0.89978D * Math.pow(d2, 7.5D)));
//        return d1;
    }

    public static double calMeasuredDistance(double height, double rawDistance) {
        if (rawDistance < 1.5) {
            return rawDistance;
        }

        double res = Math.sqrt(Math.pow(rawDistance, 2) - Math.pow(height, 2));
        if (Double.isNaN(res)) {
            return rawDistance;
        } else {
            return res;
        }
    }

    public static String strConvert(byte[] paramArrayOfByte) {
        StringBuffer localStringBuffer = new StringBuffer();
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++) {
            byte b = paramArrayOfByte[j];
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Byte.valueOf(b);
            localStringBuffer.append(String.format("%02X", arrayOfObject));
        }
        return localStringBuffer.toString();
    }

    public static int unsignedByteToInt(byte paramByte) {
        return paramByte & 0xFF;
    }
}

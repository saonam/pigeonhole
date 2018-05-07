package com.huijimuhei.beacon.utils;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class VaildUtil {

    public static boolean isBrightBeaconBase1(int paramInt)
    {
        return (paramInt == 256) || (paramInt == 257) || (paramInt == 258);
    }

    public static boolean isBrightBeaconBase1(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer();
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++)
        {
            byte b = paramArrayOfByte[j];
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Byte.valueOf(b);
            localStringBuffer.append(String.format("%02X", arrayOfObject));
        }
        return localStringBuffer.toString().contains("1D03C5E2");
    }

    public static boolean isBrightBeaconBase2(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer();
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++)
        {
            byte b = paramArrayOfByte[j];
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Byte.valueOf(b);
            localStringBuffer.append(String.format("%02X", arrayOfObject));
        }
        return localStringBuffer.toString().contains("1E160A18");
    }

    public static boolean isBrightBeaconBase2Update(int paramInt)
    {
        return (paramInt == 772) || (paramInt == 773) || (paramInt == 774) || (paramInt == 775) || (paramInt == 776) || (paramInt == 777) || (paramInt == 778) || (paramInt == 784) || (paramInt == 785) || (paramInt == 786) || (paramInt == 1025) || (paramInt == 1026);
    }

    public static boolean isBrightBeaconBase3(byte[] paramArrayOfByte)
    {
        return (paramArrayOfByte[60] == 10) && (paramArrayOfByte[61] == 24);
    }

    public static boolean isBrightBeaconBase3_2(byte[] paramArrayOfByte)
    {
        return (paramArrayOfByte[59] == 10) && (paramArrayOfByte[60] == 24);
    }

    public static boolean isBrightBeaconBase3_3(byte[] paramArrayOfByte)
    {
        return 256 + paramArrayOfByte[59] == 187;
    }


    public static boolean isBrightBeaconV1(byte[] paramArrayOfByte)
    {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Byte.valueOf(paramArrayOfByte[52]);
        return "42".equals(String.format("%02X", arrayOfObject));
    }

    public static boolean isBrightBeaconV2(byte[] paramArrayOfByte)
    {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Byte.valueOf(paramArrayOfByte[45]);
        return "42".equals(String.format("%02X", arrayOfObject));
    }
}

package com.huijimuhei.beacon.utils;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class Preconditions {
    public static void checkArgument(boolean paramBoolean) {
        if (!paramBoolean) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean paramBoolean, Object paramObject) {
        if (!paramBoolean) {
            throw new IllegalArgumentException(String.valueOf(paramObject));
        }
    }

    public static void checkArgument(boolean paramBoolean, String paramString, Object... paramVarArgs) {
        if (!paramBoolean) {
            throw new IllegalArgumentException(format(paramString, paramVarArgs));
        }
    }

    public static <T> T checkNotNull(T paramT) {
        if (paramT == null) {
            throw new NullPointerException();
        }
        return paramT;
    }

    public static <T> T checkNotNull(T paramT, Object paramObject) {
        if (paramT == null) {
            throw new NullPointerException(String.valueOf(paramObject));
        }
        return paramT;
    }

    static String format(String paramString, Object... paramVarArgs) {
        String str = String.valueOf(paramString);
        StringBuilder localStringBuilder = new StringBuilder(str.length() + 16 * paramVarArgs.length);
        int i = 0;
        int i2;
        for (int j = 0; ; j = i2) {
            int i1 = 0;//原始是int i1;
            if (j < paramVarArgs.length) {
                i1 = str.indexOf("%s", i);
                if (i1 != -1) {
                }
            } else {
                localStringBuilder.append(str.substring(i));
                if (j >= paramVarArgs.length) {
                    break;
                }
                localStringBuilder.append(" [");
                int k = j + 1;
                localStringBuilder.append(paramVarArgs[j]);
                int n;
                for (int m = k; m < paramVarArgs.length; m = n) {
                    localStringBuilder.append(", ");
                    n = m + 1;
                    localStringBuilder.append(paramVarArgs[m]);
                }
            }
            localStringBuilder.append(str.substring(i, i1));
            i2 = j + 1;
            localStringBuilder.append(paramVarArgs[j]);
            i = i1 + 2;
        }
        localStringBuilder.append(']');
        label180:
        return localStringBuilder.toString();
    }
}

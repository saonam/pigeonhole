package com.huijimuhei.beacon.utils;


import com.huijimuhei.beacon.data.BleDevice;
import com.huijimuhei.beacon.data.Polygon;

import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class PolygonUtils {
    /**
     * 返回一个点是否在一个多边形区域内
     *
     * @param point
     * @return
     */
    public static boolean isInPolygon(BleDevice point, List<Polygon> polygons) {
        int nCross = 0;
        for (int i = 0; i < polygons.size(); i++) {
            Polygon p1 = polygons.get(i);
            Polygon p2 = polygons.get((i + 1) % polygons.size());
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.getLng() == p2.getLng()) // p1p2 与 y=p0.y平行
                continue;
            if (point.getLng() < Math.min(p1.getLng(), p2.getLng())) // 交点在p1p2延长线上
                continue;
            if (point.getLng() >= Math.max(p1.getLng(), p2.getLng())) // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标
            double x = (double) (point.getLng() - p1.getLng())
                    * (double) (p2.getLat() - p1.getLat())
                    / (double) (p2.getLng() - p1.getLng()) + p1.getLat();
            if (x > point.getLat())
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外
        return (nCross % 2 == 1);
    }
}

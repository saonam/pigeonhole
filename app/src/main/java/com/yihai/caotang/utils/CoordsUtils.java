package com.yihai.caotang.utils;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.yihai.caotang.AppContext;

import java.util.List;

/**
 * Created by zeng on 2017/9/8.
 */

public class CoordsUtils {
    public static LatLng Gps2LatLng(double lat, double lng) {
        CoordinateConverter converter = new CoordinateConverter(AppContext.getInstance());
        converter.from(CoordinateConverter.CoordType.GPS);
        try {
            converter.coord(new LatLng(lat, lng));
            return converter.convert();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return null;
    }

    public static LatLonPoint Gps2LatLonPoint(double lat, double lng) {
        CoordinateConverter converter = new CoordinateConverter(AppContext.getInstance());
        converter.from(CoordinateConverter.CoordType.GPS);
        try {
            converter.coord(new LatLng(lat, lng));
            LatLng coord = converter.convert();
            return new LatLonPoint(coord.latitude, coord.longitude);
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return null;
    }

    // 功能：判断点是否在多边形内
    // 方法：求解通过该点的水平线与多边形各边的交点
    // 结论：单边交点为奇数，成立!
    //参数：
    // POINT p   指定的某个点
    // LPPOINT ptPolygon 多边形的各个顶点坐标（首末点可以不一致）
    public static boolean PtInPolygon(LatLng point, List<LatLng> APoints) {
        int nCross = 0;
        for (int i = 0; i < APoints.size(); i++) {
            LatLng p1 = APoints.get(i);
            LatLng p2 = APoints.get((i + 1) % APoints.size());
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.longitude == p2.longitude)      // p1p2 与 y=p0.y平行
                continue;
            if (point.longitude < Math.min(p1.longitude, p2.longitude))   // 交点在p1p2延长线上
                continue;
            if (point.longitude >= Math.max(p1.longitude, p2.longitude))   // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标 --------------------------------------------------------------
            double x = (double) (point.longitude - p1.longitude) * (double) (p2.latitude - p1.latitude) / (double) (p2.longitude - p1.longitude) + p1.latitude;
            if (x > point.latitude)
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1);
    }
}

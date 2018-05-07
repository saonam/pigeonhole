package com.huijimuhei.beacon.algorithm;

import com.huijimuhei.beacon.data.Coordinate;
import com.huijimuhei.beacon.data.Round;
import com.huijimuhei.beacon.data.BleDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class WeightTrilateral {

    /*所有组合的总权值*/
    private double totalWeight;

    /**
     * 获取坐标
     *
     * @param beacons
     * @return
     */
    public Coordinate getLocation(List<BleDevice> beacons) {

        Coordinate res = new Coordinate();
        /*如果收到的基站个数小于3，不能定位，直接返回*/
        if (beacons == null) {
            return null;
        }

		/*求组合数*/
        Integer[] combinationArray = getCombination(beacons.size());
        CombineAlgorithm ca = null;

        try {
            ca = new CombineAlgorithm(combinationArray, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object[][] combination = ca.getResult();

        //加权总值,最后除以总权重
        double[] tempLocation = new double[2];

        for (int i = 0; i < combination.length; i++) {

			/*创建一个列表，用来对每个组合进行计算*/
            List<BleDevice> triBases = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                BleDevice bb = beacons.get((int) combination[i][j]);
                triBases.add(bb);
            }

            /*三个基站为一组通过距离加权后求出的坐标*/
             Coordinate tempCoord = calculate(new Round(triBases.get(0).getLat(), triBases.get(0).getLng(), triBases.get(0).getDistance()),
                    new Round(triBases.get(1).getLat(), triBases.get(1).getLng(), triBases.get(1).getDistance()),
                    new Round(triBases.get(2).getLat(), triBases.get(2).getLng(), triBases.get(2).getDistance())
            );
            if (tempCoord != null) {
                tempLocation[0] += tempCoord.getLat();
                tempLocation[1] += tempCoord.getLng();
            }
        }
        //除平均
        res.setLat(tempLocation[0] / totalWeight);
        res.setLng(tempLocation[1] / totalWeight);
        return res;
    }

    /**
     * 三角形质心定位算法实现
     * Triangle centroid location
     *
     * @param r1 坐标1为圆心,距离为半径
     * @param r2
     * @param r3
     * @return
     */
    private Coordinate calculate(Round r1, Round r2, Round r3) {
        Coordinate validCrossPoint1 = null;// 有效交叉点1
        Coordinate validCrossPoint2 = null;// 有效交叉点2
        Coordinate validCrossPoint3 = null;// 有效交叉点3
        Coordinate centroid = new Coordinate();//计算三点质心

        List<Coordinate> crossPoints1 = getCrossOverPoint(r1.getX(), r1.getY(), r1.getR(), r2.getX(), r2.getY(), r2.getR());// r1,r2交点
        if (crossPoints1 != null && !crossPoints1.isEmpty()) {
            for (Coordinate jd : crossPoints1) {//有交点
                if (validCrossPoint1 == null && Math.pow(jd.getLat() - r3.getX(), 2) + Math.pow(jd.getLng() - r3.getY(), 2) <= Math.pow(r3.getR(), 2)) {
                    validCrossPoint1 = jd;
                } else if (validCrossPoint1 != null) {
                    if (Math.pow(jd.getLat() - r3.getX(), 2) + Math.pow(jd.getLng() - r3.getY(), 2) <= Math.pow(r3.getR(), 2)) {
                        if (Math.sqrt(Math.pow(jd.getLat() - r3.getX(), 2) + Math.pow(jd.getLng() - r3.getY(), 2)) > Math.sqrt(Math.pow(validCrossPoint1.getLat() - r3.getX(), 2) + Math.pow(validCrossPoint1.getLng() - r3.getY(), 2))) {
                            validCrossPoint1 = jd;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            return null;
        }

        List<Coordinate> crossPoints2 = getCrossOverPoint(r1.getX(), r1.getY(), r1.getR(), r3.getX(), r3.getY(), r3.getR());// r1,r3交点
        if (crossPoints2 != null && !crossPoints2.isEmpty()) {
            for (Coordinate jd : crossPoints2) {//有交点
                if (validCrossPoint2 == null && Math.pow(jd.getLat() - r2.getX(), 2) + Math.pow(jd.getLng() - r2.getY(), 2) <= Math.pow(r2.getR(), 2)) {
                    validCrossPoint2 = jd;

                } else if (validCrossPoint2 != null) {
                    if (Math.pow(jd.getLat() - r2.getX(), 2) + Math.pow(jd.getLng() - r2.getY(), 2) <= Math.pow(r2.getR(), 2)) {
                        if (Math.pow(jd.getLat() - r2.getX(), 2) + Math.pow(jd.getLng() - r2.getY(), 2) > Math.sqrt(Math.pow(validCrossPoint2.getLat() - r2.getX(), 2) + Math.pow(validCrossPoint2.getLng() - r2.getY(), 2))) {
                            validCrossPoint1 = jd;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            return null;
        }

        List<Coordinate> crossPoints3 = getCrossOverPoint(r2.getX(), r2.getY(), r2.getR(), r3.getX(), r3.getY(), r3.getR());// r2,r3交点
        if (crossPoints3 != null && !crossPoints3.isEmpty()) {
            for (Coordinate jd : crossPoints3) {//有交点
                if (Math.pow(jd.getLat() - r1.getX(), 2) + Math.pow(jd.getLng() - r1.getY(), 2) <= Math.pow(r1.getR(), 2)) {
                    validCrossPoint3 = jd;
                } else if (validCrossPoint3 != null) {
                    if (Math.pow(jd.getLat() - r1.getX(), 2) + Math.pow(jd.getLng() - r1.getY(), 2) <= Math.pow(r1.getR(), 2)) {
                        if (Math.pow(jd.getLat() - r1.getX(), 2) + Math.pow(jd.getLng() - r1.getY(), 2) > Math.sqrt(Math.pow(validCrossPoint3.getLat() - r1.getX(), 2) + Math.pow(validCrossPoint3.getLng() - r1.getY(), 2))) {
                            validCrossPoint3 = jd;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            return null;
        }

        if (validCrossPoint1 != null && validCrossPoint2 != null && validCrossPoint3 != null) {
            //计算质心
            centroid.lat = (validCrossPoint1.lat + validCrossPoint2.lat + validCrossPoint3.lat) / 3;
            centroid.lng = (validCrossPoint1.lng + validCrossPoint2.lng + validCrossPoint3.lng) / 3;

            //再计算权值
            double[] disArray = new double[]{r1.getR(), r2.getR(), r3.getR()};
            Coordinate res = calculateWeight(disArray, centroid);
            return res;
        } else {
            return null;
        }
    }

    /**
     * 求权重
     *
     * @return 返回通过该组基站距离加权后的坐标
     */
    private Coordinate calculateWeight(double[] distanceArray, Coordinate rawLocation) {

		/*对应的权值*/
        double weight = 0;

        for (int i = 0; i < 3; i++) {
            weight += (1.0 / distanceArray[i]);
        }
        totalWeight += weight;

		/*实例化坐标数组*/
        Coordinate loc = new Coordinate();

		/*计算加权过后的坐标*/
        loc.setLng(rawLocation.getLng() * weight);
        loc.setLat(rawLocation.getLat() * weight);

        return loc;
    }

    /**
     * 求组合数
     *
     * @param baseNum
     * @return
     */
    private Integer[] getCombination(int baseNum) {
        Integer[] combination = new Integer[baseNum];
        for (int i = 0; i < baseNum; i++) {
            combination[i] = i;
        }
        return combination;
    }

    /**
     * 求两个圆的交点
     *
     * @param x1, 圆心1坐标
     * @param y1
     * @param r1    半径
     * @param x2
     * @param y2
     * @param r2
     * @return
     */
    private List<Coordinate> getCrossOverPoint(double x1, double y1, double r1, double x2, double y2, double r2) {

        Map<Integer, double[]> p = new HashMap<>();
        double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));// 两圆心距离
        if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) < (r1 + r2)) {// 两圆向交

        }
        List<Coordinate> points = new ArrayList<Coordinate>();//交点坐标
        Coordinate coor;
        if (d > r1 + r2 || d < Math.abs(r1 - r2)) {//相离或内含
            return null;
        } else if (x1 == x2 && y1 == y2) {//同心圆
            return null;// 同心圆 )
        } else if (y1 == y2 && x1 != x2) {
            double a = ((r1 * r1 - r2 * r2) - (x1 * x1 - x2 * x2)) / (2 * x2 - 2 * x1);
            if (d == Math.abs(r1 - r2) || d == r1 + r2) {// 只有一个交点时\
                coor = new Coordinate();
                coor.lat = a;
                coor.lng = y1;
                points.add(coor);
            } else {// 两个交点
                double t = r1 * r1 - (a - x1) * (a - x1);
                coor = new Coordinate();
                coor.lat = a;
                coor.lng = y1 + Math.sqrt(t);
                points.add(coor);
                coor = new Coordinate();
                coor.lat = a;
                coor.lng = y1 - Math.sqrt(t);
                points.add(coor);
            }
        } else if (y1 != y2) {
            double k, disp;
            k = (2 * x1 - 2 * x2) / (2 * y2 - 2 * y1);
            disp = ((r1 * r1 - r2 * r2) - (x1 * x1 - x2 * x2) - (y1 * y1 - y2 * y2)) / (2 * y2 - 2 * y1);// 直线偏移量
            double a, b, c;
            a = (k * k + 1);
            b = (2 * (disp - y1) * k - 2 * x1);
            c = (disp - y1) * (disp - y1) - r1 * r1 + x1 * x1;
            double disc;
            disc = b * b - 4 * a * c;// 一元二次方程判别式
            if (d == Math.abs(r1 - r2) || d == r1 + r2) {
                coor = new Coordinate();
                coor.lat = (-b) / (2 * a);
                coor.lng = k * coor.lat + disp;
                points.add(coor);
            } else {
                coor = new Coordinate();
                coor.lat = ((-b) + Math.sqrt(disc)) / (2 * a);
                coor.lng = k * coor.lat + disp;
                points.add(coor);
                coor = new Coordinate();
                coor.lat = ((-b) - Math.sqrt(disc)) / (2 * a);
                coor.lng = k * coor.lat + disp;
                points.add(coor);
            }
        }
        return points;
    }
}

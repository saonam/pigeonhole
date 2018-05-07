package com.yihai.caotang.data;

import android.os.Environment;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class Constants {

    /***********************
     * 系统设置
     */
    public static final String SD_ROOT = Environment.getExternalStorageDirectory() + "/caotang/";   //缓存路径
    public static final String BACKGROUND_MUSIC_PATH = SD_ROOT + "background.mp3";                  //背景音乐地址
    public static final String DATA_VERSION_PATH = SD_ROOT + "ver.txt";                             //数据版本
    /***********************
     * 地图设置
     */
    public static final LatLng MAP_CENTER = new LatLng(30.660076, 104.028553);                      //地图中心点
    public static final LatLng MAP_BOUND_SOUTH_WEST = new LatLng(30.662155, 104.02569);            //显示边界西南坐标
    public static final LatLng MAP_BOUND_NORTH_EAST = new LatLng(30.658252, 104.031307);           //显示边界东北坐标

    public static final LatLng OVERLAY_BOUND_SOUTH_WEST = new LatLng(30.66248, 104.025698);           //手绘图边界西南坐标
    public static final LatLng OVERLAY_BOUND_NORTH_EAST = new LatLng(30.658157, 104.031446);           //手绘图边界东北坐标

    public static final float MAP_ZOOM_LEVEL_DEFAULT = 20;                                          //地图缩放默认
    public static final float MAP_ZOOM_LEVEL_MAX = 20;                                              //地图缩放最大
    public static final float MAP_ZOOM_LEVEL_MIN = 18;                                               //地图缩放最小

    public static List<Integer> TOUR1;
    public static List<Integer> TOUR2;
    public static List<Integer> TOUR3;
    public static Map<Integer, List<Integer>> TOURS;

    static {
        TOUR1 = new ArrayList<>();
        TOUR1.add(1);
        TOUR1.add(2);
        TOUR1.add(3);
        TOUR1.add(4);
        TOUR1.add(5);
        TOUR1.add(6);
        TOUR1.add(7);
        TOUR1.add(8);
        TOUR1.add(9);
        TOUR1.add(10);
        TOUR1.add(12);
        TOUR1.add(21);
        TOUR1.add(22);


        TOUR2 = new ArrayList<>();
        TOUR2.add(1);
        TOUR2.add(2);
        TOUR2.add(3);
        TOUR2.add(4);
        TOUR2.add(5);
        TOUR2.add(6);
        TOUR2.add(7);
        TOUR2.add(8);
        TOUR2.add(9);
        TOUR2.add(10);
        TOUR2.add(12);
        TOUR2.add(13);
        TOUR2.add(14);
        TOUR2.add(15);
        TOUR2.add(16);
        TOUR2.add(17);
        TOUR2.add(21);
        TOUR2.add(22);

        TOUR3 = new ArrayList<>();
        TOUR3.add(1);
        TOUR3.add(2);
        TOUR3.add(3);
        TOUR3.add(4);
        TOUR3.add(5);
        TOUR3.add(6);
        TOUR3.add(7);
        TOUR3.add(8);
        TOUR3.add(9);
        TOUR3.add(10);
        TOUR3.add(12);
        TOUR3.add(13);
        TOUR3.add(14);
        TOUR3.add(15);
        TOUR3.add(16);
        TOUR3.add(17);
        TOUR3.add(18);
        TOUR3.add(19);
        TOUR3.add(21);
        TOUR3.add(22);

        TOURS = new HashMap<>();
        TOURS.put(1, TOUR1);
        TOURS.put(2, TOUR2);
        TOURS.put(3, TOUR3);

    }
//    /***********************
//     * 景点数据-点
//     */
//    public static final LatLng MARKER_DAJIE = new LatLng(30.659864, 104.026793);                    //大廨
//    public static final LatLng MARKER_SHISHUHUAYUANMEISHUGUAN = new LatLng(30.659287, 104.028376);  //诗书画院美术馆
//    public static final LatLng MARKER_WANFOLOU = new LatLng(30.659075, 104.029937);                 //万佛楼,
//    public static final LatLng MARKER_HUANHUACI = new LatLng(30.660016, 104.027893);                //浣花祠,
//    public static final LatLng MARKER_SHISHITANG = new LatLng(30.660016, 104.026911);               //诗史堂,
//    public static final LatLng MARKER_GONGBUCI = new LatLng(30.660473, 104.027223);                 //工部祠,
//    public static final LatLng MARKER_YILANTING = new LatLng(30.66111, 104.026284);                 //一览亭
//
//    /***********************
//     * 景点数据-线
//     */
//    public static final LatLng[] LINE_RECOMMEND_ONE = {                                             //推荐线路1
//            new LatLng(30.659402, 104.026477),
//            new LatLng(30.659795, 104.02674),
//            new LatLng(30.660238, 104.02703),
//            new LatLng(30.660164, 104.027212),
//            new LatLng(30.659873, 104.028145),
//            new LatLng(30.659647, 104.028076),
//            new LatLng(30.659075, 104.027882),
//            new LatLng(30.658955, 104.02881),
//            new LatLng(30.65833, 104.028844),
//    };
//
//    /***********************
//     * 景点数据-面
//     */
//    public static final LatLng[] POLYGON_DAJIE = {                                                  //大廨
//            new LatLng(30.659936, 104.026591),
//            new LatLng(30.660226, 104.0268),
//            new LatLng(30.660023, 104.027186),
//            new LatLng(30.660093, 104.027224),
//            new LatLng(30.660028, 104.027352),
//            new LatLng(30.659867, 104.02724),
//            new LatLng(30.659867, 104.02724),
//            new LatLng(30.659977, 104.027116),
//            new LatLng(30.659977, 104.027116),
//            new LatLng(30.659733, 104.026945),
//    };
//
//    public static final LatLng[] KRSPACE = {                                                  //大廨
//            new LatLng(30.660256, 104.082274),
//            new LatLng(30.65973, 104.083287),
//            new LatLng(30.658765, 104.082606),
//            new LatLng(30.659448, 104.080997)
//    };


}

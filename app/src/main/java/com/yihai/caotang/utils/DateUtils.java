package com.yihai.caotang.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class DateUtils {
    /**
     * 今天的日期
     *
     * @return
     */
    public static String today() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 当前时间
     * 年月日小时分钟
     *
     * @return
     */
    public static String now() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 当前时间
     * 小时:分钟:秒
     *
     * @return
     */
    public static String current() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateString = formatter.format(date);
        return dateString;
    }

    private static Date currentDate() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        //calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        return calendar.getTime(); //这个时间就是日期往后推一天的结果
    }

    /**
     * 启示时间到当前时间
     * 时间间隔(分钟)
     *
     * @return
     */
    public static long tillNowOfMin(String startAt) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = null;
        Date now = currentDate();
        try {
            start = df.parse(startAt);
            long duration = now.getTime() - start.getTime();//时间间隔，毫秒
            long res = (duration / 1000) / 60;      //时间间隔，分
            return res;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 启示时间到当前时间
     * 时间间隔(毫秒)
     *
     * @return
     */
    public static long tillNowOfSec(String startAt) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = null;
        Date now = currentDate();
        try {
            start = df.parse(startAt);
            long duration = now.getTime() - start.getTime();//时间间隔，毫秒
            return duration;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 格式化日期
     * 返回yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static String timeToDate(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (date == null) {
            return time;
        } else {
            return sdf.format(date);
        }
    }

    /**
     * 格式化日期
     * 返回yyyy-MM-dd HH:mm
     *
     * @param time
     * @return
     */
    public static String timeToTime(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (date == null) {
            return time;
        } else {
            return sdf.format(date);
        }
    }

    /**
     * 格式化日期
     * 返回HH:mm:ss
     *
     * @param time
     * @return
     */
    public static String timeToTimestamp(String time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        if (date == null) {
            return time;
        } else {
            return sdf.format(date);
        }
    }

    /**
     * 日期比较
     *
     * @param record    详情记录日期
     * @param timestamp 时间戳日期
     * @return
     */
    public static int compareTime(String record, String timestamp) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(record);
            Date dt2 = df.parse(timestamp);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 日期比较
     *
     * @param record    详情记录日期
     * @param timestamp 时间戳日期
     * @return
     */
    public static int compareDate(String record, String timestamp) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(record);
            Date dt2 = df.parse(timestamp);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
}

package com.yihai.caotang.data.session;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.data.BaseData;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.language.Language;

import net.grandcentrix.tray.AppPreferences;

import java.util.HashMap;

import static com.huijimuhei.beacon.BeaconScanner.ACCURACY_GPS;

/**
 * 跨进程需要
 * 可能会多进程使用的参数GETTER/SETTER加入持久
 * <p>
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class Session extends BaseData implements Parcelable {

    /**
     * default value
     */
    public static final int DEFAULT_BRIGHTNESS = 100;
    public static final boolean DEFAULT_PLAY_SOUND_AFFECT = true;
    public static final boolean DEFAULT_PLAY_BACKGROUND = false;
    public static final int DEFAULT_LANGUAGE = 1;

    /**
     * Tray
     */
    private static final String TRAY_SESSION_BACKGROUND = "session_background";
    private static final String TRAY_SESSION_SOUND_AFFECT = "session_soundaffect";
    private static final String TRAY_SESSION_BRIGHTNESS = "session_brightness";
    private static final String TRAY_SESSION_STAR_NUM = "session_star";
    private static final String TRAY_SESSION_LANDSCAPE_LOG = "session_landscape_log";
    private static final String TRAY_SESSION_TIME_LIMIT = "session_time_limit";
    private static final String TRAY_SESSION_LOW_BATTERY_WARNING = "session_low_battery_waring";
    private static final String TRAY_SESSION_LOW_BATTERY_LOCK = "session_low_battery_lock";
    private static final String TRAY_SESSION_FENCE_WARNING = "session_fence_waring";
    private static final String TRAY_SESSION_STATUS = "session_status";
    private static final String TRAY_SESSION_START_AT = "session_start_at";
    private static final String TRAY_SESSION_HEADING = "session_heading";
    private static final String TRAY_SESSION_IS_USING = "session_is_using";

    /**
     * Status
     */
    public static final int PAD_STATUS_UNINITED = 1;
    public static final int PAD_STATUS_UNMATCHED = 2;
    public static final int PAD_STATUS_FREE = 3;
    public static final int PAD_STATUS_CHARGING = 4;
    public static final int PAD_STATUS_LENGDING = 5;
    public static final int PAD_STATUS_UNRETURNED = 6;
    public static final int PAD_STATUS_ALERT = 7;
    public static final int PAD_STATUS_REPAIRING = 8;
    public static final int PAD_STATUS_DEPRECATED = 9;
    public static final int PAD_STATUS_LOST = 10;

    public static HashMap<Integer, String> PAD_STATUS = new HashMap<>();

    static {
        PAD_STATUS.put(PAD_STATUS_UNINITED, "未初始化");
        PAD_STATUS.put(PAD_STATUS_UNMATCHED, "未配对");
        PAD_STATUS.put(PAD_STATUS_FREE, "闲置中");
        PAD_STATUS.put(PAD_STATUS_CHARGING, "充电中");
        PAD_STATUS.put(PAD_STATUS_LENGDING, "租借中");
        PAD_STATUS.put(PAD_STATUS_UNRETURNED, "停租未归还");
        PAD_STATUS.put(PAD_STATUS_ALERT, "报警");
        PAD_STATUS.put(PAD_STATUS_REPAIRING, "维修中");
        PAD_STATUS.put(PAD_STATUS_DEPRECATED, "已报废");
        PAD_STATUS.put(PAD_STATUS_LOST, "丢失");
    }

    private int sessionId;       //会话id
    private boolean isBackgroundOpen;    //音乐是否打开
    private boolean isAffectOpen;   //音效是否打开
    private int brightness;         //屏幕亮度
    private int starNum;            //游戏星星读数
    private int lang;               //当前语言
    private double lat;              //坐标
    private double lng;              //坐标
    private float heading;            //朝向
    private int accuracy;           //定位精度
    private float battery;          //电量
    private int status;             //设备当前状态
    private int timeLength;         //租借时长
    private String startAt;         //租借起始时间
    private boolean isLowBatteryLockTriggered; //是否低电量锁屏
    private boolean isLowBatteryWarningTriggered; //是否低电量提示
    private boolean isTimeLimitedTriggered;    //是否已提示时间限制
    private boolean isFenceWarningTriggered;          //是否已报警提示
    private boolean isUsing;                    //是否在使用
    private boolean isGpsCorrect;               //gps信号是否正常
    private HashMap<Integer, Integer> landScapeLog;  //景点记录 land_scape_id(景点id):time(次数)
    private HashMap<Integer, Integer> soundTrackLog;  //播音记录 sound_track_id(音频id):time(次数)
    private Language language;                       //当前语言
    private LandScape curLandscape; //当前进入的景点
    private LandScape lastLandscape; //上一个景点
    private LandScape locLandscape; //定位所在的景点
    private long lastLocAt;  //最后定位时间
    private int selTour;//选择的推荐线路
    private AppPreferences mTray;

    public Session() {

        mTray = new AppPreferences(AppContext.getInstance());
        landScapeLog = new HashMap<>();
        soundTrackLog = new HashMap<>();
        language = new Language();
        isLowBatteryWarningTriggered = false;
        isTimeLimitedTriggered = false;
        isFenceWarningTriggered = false;
        isUsing = false;
        accuracy = ACCURACY_GPS;
    }

    public void setLandScapeLog(HashMap<Integer, Integer> landScapeLog) {
//        String json = new GsonBuilder().create().toJson(landScapeLog, new TypeToken<HashMap<Integer, Integer>>() {
//        }.getType());
//        mTray.put(TRAY_SESSION_LANDSCAPE_LOG, json);
        this.landScapeLog = landScapeLog;
    }

    public HashMap<Integer, Integer> getLandScapeLog() {
//        String json = mTray.getString(TRAY_SESSION_LANDSCAPE_LOG, "");
//        if (!TextUtils.isEmpty(json)) {
//            landScapeLog = new GsonBuilder().create().fromJson(json, new TypeToken<HashMap<Integer, Integer>>() {
//            }.getType());
//        }
        return landScapeLog;
    }

    public HashMap<Integer, Integer> getSoundTrackLog() {
        return soundTrackLog;
    }

    public void setSoundTrackLog(HashMap<Integer, Integer> soundTrackLog) {
        this.soundTrackLog = soundTrackLog;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public int getStatus() {
        status = mTray.getInt(TRAY_SESSION_STATUS, PAD_STATUS_FREE);
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        mTray.put(TRAY_SESSION_STATUS, status);
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isBackgroundOpen() {
        isBackgroundOpen = mTray.getBoolean(TRAY_SESSION_BACKGROUND, false);
        return isBackgroundOpen;
    }

    public void setBackgroundOpen(boolean backgroundOpen) {
        isBackgroundOpen = backgroundOpen;
        mTray.put(TRAY_SESSION_BACKGROUND, backgroundOpen);
    }

    public boolean isAffectOpen() {
        isAffectOpen = mTray.getBoolean(TRAY_SESSION_SOUND_AFFECT, false);
        return isAffectOpen;
    }

    public void setAffectOpen(boolean affectOpen) {
        isAffectOpen = affectOpen;
        mTray.put(TRAY_SESSION_SOUND_AFFECT, affectOpen);
    }

    public int getStarNum() {
//        starNum = mTray.getInt(TRAY_SESSION_STAR_NUM, 0);
        return starNum;
    }

    public void setStarNum(int starNum) {
        this.starNum = starNum;
//        mTray.put(TRAY_SESSION_STAR_NUM, starNum);
    }

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public float getHeading() {
        heading = mTray.getFloat(TRAY_SESSION_HEADING, 0);
        return heading;
    }

    public void setHeading(float heading) {
        this.heading = heading;
        mTray.put(TRAY_SESSION_HEADING, heading);
    }

    public int getBrightness() {
        brightness = mTray.getInt(TRAY_SESSION_BRIGHTNESS, DEFAULT_BRIGHTNESS);
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
        mTray.put(TRAY_SESSION_BRIGHTNESS, brightness);
    }

    public float getBattery() {
        return battery;
    }

    public void setBattery(float battery) {
        this.battery = battery;
    }

    public String getStartAt() {
        startAt = mTray.getString(TRAY_SESSION_START_AT, "");
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
        mTray.put(TRAY_SESSION_START_AT, startAt);
    }

    public LandScape getCurLandscape() {
        return curLandscape;
    }

    public void setCurLandscape(LandScape curLandscape) {
        this.curLandscape = curLandscape;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public LandScape getLocLandscape() {
        return locLandscape;
    }

    public void setLocLandscape(LandScape locLandscape) {
        this.locLandscape = locLandscape;
    }

    public LandScape getLastLandscape() {
        return lastLandscape;
    }

    public void setLastLandscape(LandScape lastLandscape) {
        this.lastLandscape = lastLandscape;
    }

    public long getLastLocAt() {
        return lastLocAt;
    }

    public void setLastLocAt(long lastLocAt) {
        this.lastLocAt = lastLocAt;
    }

    public boolean isLowBatteryWarningTriggered() {
//        try {
//            isLowBatteryWarningTriggered = mTray.getBoolean(TRAY_SESSION_LOW_BATTERY_WARNING);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        return isLowBatteryWarningTriggered;
    }

    public void setLowBatteryWarningTriggered(boolean lowBatteryWarningTriggered) {
        isLowBatteryWarningTriggered = lowBatteryWarningTriggered;
//        mTray.put(TRAY_SESSION_LOW_BATTERY_WARNING, lowBatteryWarningTriggered);
    }

    public boolean isTimeLimitedTriggered() {
//        isTimeLimitedTriggered = mTray.getBoolean(TRAY_SESSION_TIME_LIMIT, false);
        return isTimeLimitedTriggered;
    }

    public void setTimeLimitedTriggered(boolean timeLimitedTriggered) {
        isTimeLimitedTriggered = timeLimitedTriggered;
//        mTray.put(TRAY_SESSION_TIME_LIMIT, timeLimitedTriggered);
    }

    public boolean isFenceWarningTriggered() {

//        isFenceWarningTriggered = mTray.getBoolean(TRAY_SESSION_FENCE_WARNING, false);
        return isFenceWarningTriggered;
    }

    public void setFenceWarningTriggered(boolean fenceWarningTriggered) {
        isFenceWarningTriggered = fenceWarningTriggered;
//        mTray.put(TRAY_SESSION_FENCE_WARNING, fenceWarningTriggered);
    }

    public boolean isLowBatteryLockTriggered() {
//        try {
//            isLowBatteryLockTriggered = mTray.getBoolean(TRAY_SESSION_LOW_BATTERY_LOCK);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        return isLowBatteryLockTriggered;

    }

    public void setLowBatteryLockTriggered(boolean lowBatteryLockTriggered) {
        isLowBatteryLockTriggered = lowBatteryLockTriggered;
//        mTray.put(TRAY_SESSION_LOW_BATTERY_LOCK, lowBatteryLockTriggered);
    }

    public boolean isGpsCorrect() {
        return isGpsCorrect;
    }

    public void setGpsCorrect(boolean gpsCorrect) {
        isGpsCorrect = gpsCorrect;
    }

    public void setUsing(boolean using) {
        isUsing = using;
//        mTray.put(TRAY_SESSION_IS_USING, using);
    }

    public boolean isUsing() {
//        isUsing = mTray.getBoolean(TRAY_SESSION_IS_USING, false);
        return isUsing;
    }

    public int getSelTour() {
        return selTour;
    }

    public void setSelTour(int selTour) {
        this.selTour = selTour;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sessionId);
        dest.writeByte(isBackgroundOpen ? (byte) 1 : (byte) 0);
        dest.writeByte(isAffectOpen ? (byte) 1 : (byte) 0);
        dest.writeInt(this.brightness);
        dest.writeInt(this.starNum);
        dest.writeInt(this.lang);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeFloat(this.heading);
        dest.writeFloat(this.battery);
        dest.writeInt(this.status);
        dest.writeInt(this.timeLength);
        dest.writeString(this.startAt);
        dest.writeByte(this.isLowBatteryWarningTriggered ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTimeLimitedTriggered ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFenceWarningTriggered ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLowBatteryLockTriggered ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isUsing ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGpsCorrect ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.landScapeLog);
        dest.writeSerializable(this.soundTrackLog);
        dest.writeParcelable(this.curLandscape, flags);
        dest.writeParcelable(this.locLandscape, flags);
        dest.writeParcelable(this.lastLandscape, flags);
        dest.writeInt(this.accuracy);
        dest.writeLong(this.lastLocAt);
        dest.writeInt(this.selTour);
        dest.writeParcelable(this.language, flags);
        dest.writeInt(this.id);
    }

    private Session(Parcel in) {
        this.sessionId = in.readInt();
        this.isBackgroundOpen = in.readByte() != 0;
        this.isAffectOpen = in.readByte() != 0;
        this.brightness = in.readInt();
        this.starNum = in.readInt();
        this.lang = in.readInt();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.heading = in.readFloat();
        this.battery = in.readFloat();
        this.status = in.readInt();
        this.timeLength = in.readInt();
        this.startAt = in.readString();
        this.isLowBatteryWarningTriggered = in.readByte() != 0;
        this.isTimeLimitedTriggered = in.readByte() != 0;
        this.isFenceWarningTriggered = in.readByte() != 0;
        this.isLowBatteryLockTriggered = in.readByte() != 0;
        this.isUsing = in.readByte() != 0;
        this.isGpsCorrect = in.readByte() != 0;
        this.landScapeLog = (HashMap<Integer, Integer>) in.readSerializable();
        this.soundTrackLog = (HashMap<Integer, Integer>) in.readSerializable();
        this.curLandscape = in.readParcelable(LandScape.class.getClassLoader());
        this.locLandscape = in.readParcelable(LandScape.class.getClassLoader());
        this.lastLandscape = in.readParcelable(LandScape.class.getClassLoader());
        this.accuracy = in.readInt();
        this.lastLocAt = in.readLong();
        this.selTour = in.readInt();
        this.language = in.readParcelable(Language.class.getClassLoader());
        this.id = in.readInt();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        public Session createFromParcel(Parcel source) {
            return new Session(source);
        }

        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}

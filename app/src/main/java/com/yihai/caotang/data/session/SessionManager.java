package com.yihai.caotang.data.session;

import com.amap.api.maps.model.LatLng;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;

import java.util.HashMap;

/**
 * 会话缓存
 */
public class SessionManager {

    private static SessionManager mInstance;

    private Session session; //会话

    private SessionManager() {
        session = new Session();
        reset();
    }

    public synchronized static SessionManager getInstance() {
        if (mInstance == null) {
            mInstance = new SessionManager();
        }
        return mInstance;
    }

    /**
     * 初始化Session
     * 如果已经有，重置session
     * 这个是用在归还、初始化的时候
     */
    public void reset() {
        //语言-中文
        session.setLang(Session.DEFAULT_LANGUAGE);
        //屏幕亮度
        session.setBrightness(Session.DEFAULT_BRIGHTNESS);
        //音效是否打开
        session.setAffectOpen(Session.DEFAULT_PLAY_SOUND_AFFECT);
        //背景音乐是否打开
        session.setBackgroundOpen(Session.DEFAULT_PLAY_BACKGROUND);
        //所获游戏点数
        session.setStarNum(0);
        //是否在使用
        session.setUsing(false);
        //景点记录
        session.getLandScapeLog().clear();
        //状态提示
        session.setLowBatteryWarningTriggered(false);
        session.setLowBatteryLockTriggered(false);
        session.setFenceWarningTriggered(false);
        session.setTimeLimitedTriggered(false);
        session.setCurLandscape(null);
        //景点记录
        HashMap<Integer, Integer> logs = new HashMap<>();
        for (LandScape landscape : LandScapeManager.getInstance().getLandscapes()) {
            logs.put(landscape.getId(), 0);
        }
        session.setLandScapeLog(logs);
    }

    /**
     * 根据后端请求获得的session更新当前session
     * 这个是用在替换的时候
     *
     * @param session
     */
    public void update(Session session) {
        this.session = session;
    }

    /**
     * 获取session数据
     *
     * @return
     */
    public synchronized Session getSession() {
        return session;
    }

    /**
     * 更新坐标
     *
     * @param lng
     * @param lat
     */
    public void updateLoacation(double lng, double lat,float heading) {
        synchronized (session) {
            session.setLat(lat);
            session.setLng(lng);
            session.setHeading(heading);
        }
    }

    public LatLng getLocationLatLng(){
        return new LatLng(session.getLat(),session.getLng());
    }
//
//    /**
//     * 时间限制是否触发
//     */
//    public synchronized void setTrayStarNum(int num) {
//        mTray.put(TRAY_SESSION_STAR_NUM, num);
//    }
//

    /**
     * 增加游戏币数量
     */
    public synchronized void increaseStarNum(int num) {
        this.session.setStarNum(this.session.getStarNum() + num);
    }
}

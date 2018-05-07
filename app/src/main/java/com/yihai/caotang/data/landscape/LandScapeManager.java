package com.yihai.caotang.data.landscape;

import android.database.Cursor;

import com.yihai.caotang.data.DatabaseManager;
import com.yihai.caotang.data.antique.AntiqueManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */

public class LandScapeManager {
    private static LandScapeManager instance;
    private List<LandScape> mCache;

    public synchronized static LandScapeManager getInstance() {
        if (instance == null) {
            instance = new LandScapeManager();
        }
        return instance;
    }

    private LandScapeManager() {
        mCache = new ArrayList<>();
    }

    /**
     * 热缓存
     * 其他操作直接走热缓存拿,不走数据库
     */
    public void warnUp() {
        String sql = String.format("select * from caotang_land_scape where lang=%d", 1);//TODO  SessionManager.getInstance().getLang().getLang()
        Cursor c = DatabaseManager.getInstance().rawQuery(sql);
        while (c.moveToNext()) {
            LandScape item = new LandScape();
            item.setId(c.getInt(c.getColumnIndex("id")));
            item.setName(c.getString(c.getColumnIndex("name")));
            item.setDisplayType(c.getString(c.getColumnIndex("display_type")));
            item.setGameType(c.getString(c.getColumnIndex("game_type")));
            item.setSoundTrackUri(c.getString(c.getColumnIndex("sound_track_uri")));
            item.setHasGame(c.getInt(c.getColumnIndex("has_game")));
            item.setHasImageList(c.getInt(c.getColumnIndex("has_image_list")));
            item.setStarNum(c.getInt(c.getColumnIndex("star_num")));
            item.setSoundTrackUri(c.getString(c.getColumnIndex("sound_track_uri")));
            item.setLang(c.getInt(c.getColumnIndex("lang")));
            item.setLat(c.getDouble(c.getColumnIndex("lat")));
            item.setLng(c.getDouble(c.getColumnIndex("lng")));
            //默认值
            item.setVisitTimes(0);
            //文物列表
            if (item.getHasImageList() == 1) {
                item.setAntiques(AntiqueManager.getInstance().getLandScapeList(item.getId()));
            }
            //边界列表
            item.setBorders(BorderManager.getInstance().getBorder(item.getId()));
            mCache.add(item);
        }
    }

    /**
     * 根据地理位置获取景点数据
     *
     * @param id
     * @return
     */
    public LandScape getLandscape(int id) {
        if (mCache == null || mCache.size() == 0) {
            warnUp();
        }
        for (LandScape item : mCache) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取所有景点数据
     *
     * @return
     */
    public List<LandScape> getLandscapes() {
        if (mCache == null || mCache.size() == 0) {
            warnUp();
        }
        return mCache;
    }
}

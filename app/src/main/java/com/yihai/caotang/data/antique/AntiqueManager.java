package com.yihai.caotang.data.antique;

import android.database.Cursor;
import android.text.TextUtils;

import com.yihai.caotang.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class AntiqueManager {
    private static AntiqueManager instance;
    private List<Antique> mCache;

    public synchronized static AntiqueManager getInstance() {
        if (instance == null) {
            instance = new AntiqueManager();
        }
        return instance;
    }

    private AntiqueManager() {
        mCache = new ArrayList<>();
    }

    /**
     * 热缓存
     * 其他操作直接走热缓存拿,不走数据库
     */
    public void warnUp() {
        String sql = String.format("select * from caotang_antique where lang=%d", 1);//TODO  SessionManager.getInstance().getLang().getLang()
        Cursor c = DatabaseManager.getInstance().rawQuery(sql);
        while (c.moveToNext()) {
            Antique item = new Antique();
            item.setId(c.getInt(c.getColumnIndex("id")));
            item.setName(c.getString(c.getColumnIndex("name")));
            item.setRepository_content(c.getString(c.getColumnIndex("repository_content")));
            item.setDisplay_content(c.getString(c.getColumnIndex("display_content")));
            item.setType(c.getString(c.getColumnIndex("type")));
            item.setCatalog(c.getInt(c.getColumnIndex("catalog")));
            item.setLand_scape_id(c.getInt(c.getColumnIndex("land_scape_id")));
            item.setLand_scape_name(c.getString(c.getColumnIndex("land_scape_name")));
            item.setIs_repository(c.getInt(c.getColumnIndex("is_repository")));
            item.setAr_code(c.getString(c.getColumnIndex("ar_code")));
            item.setAr_type(c.getInt(c.getColumnIndex("ar_type")));
            item.setImage_uri(c.getString(c.getColumnIndex("image_uri")));
            item.setSound_track_uri(c.getString(c.getColumnIndex("sound_track_uri")));
            item.setLang(c.getInt(c.getColumnIndex("lang")));
            item.setLocation(c.getString(c.getColumnIndex("location")));
            mCache.add(item);
        }
    }

    /**
     * 知识库获取文物列表
     *
     * @param catalog
     * @return
     */
    public List<Antique> getRepositoryList(int catalog) {
        List<Antique> res = new ArrayList<>();
        if (mCache == null || mCache.size() == 0) {
            warnUp();
        }
        for (Antique item : mCache) {
            if (item.getCatalog() == catalog && item.getIs_repository() == 1) {
                res.add(item);
            }
        }
        return res;
    }

    /**
     * AR识别文物列表
     *
     * @return
     */
    public ArrayList<Antique> getArRecognizeList() {
        ArrayList<Antique> res = new ArrayList<>();
        if (mCache == null || mCache.size() == 0) {
            warnUp();
        }
        for (Antique item : mCache) {
            if (!TextUtils.isEmpty(item.getAr_code())) {
                res.add(item);
            }
        }
        return res;
    }

    /**
     * AR识别中，获取景点文物列表
     *
     * @param land_scape_id
     * @return
     */
    public List<Antique> getLandScapeList(int land_scape_id) {
        List<Antique> res = new ArrayList<>();
        if (mCache == null || mCache.size() == 0) {
            warnUp();
        }
        for (Antique item : mCache) {
            if (item.getLand_scape_id() == land_scape_id) {
                res.add(item);
            }
        }
        return res;
    }
}

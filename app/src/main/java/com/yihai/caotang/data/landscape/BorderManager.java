package com.yihai.caotang.data.landscape;

import android.database.Cursor;

import com.yihai.caotang.data.DatabaseManager;
import com.yihai.caotang.data.game.GameOption;
import com.yihai.caotang.data.game.GameQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class BorderManager {
    private static BorderManager instance;
    private List<Border> mCache;

    public synchronized static BorderManager getInstance() {
        if (instance == null) {
            instance = new BorderManager();
        }
        return instance;
    }

    private BorderManager() {
        mCache = new ArrayList<>();
    }

    /**
     * 热缓存
     * 其他操作直接走热缓存拿,不走数据库
     */
    public void warnUp() {
        String sql = String.format("select * from caotang_border");//TODO  SessionManager.getInstance().getLang().getLang()
        Cursor c = DatabaseManager.getInstance().rawQuery(sql);
        while (c.moveToNext()) {
            Border item = new Border();
            item.setId(c.getInt(c.getColumnIndex("id")));
            item.setLat(c.getDouble(c.getColumnIndex("lat")));
            item.setLng(c.getDouble(c.getColumnIndex("lng")));
            item.setLandscapeId(c.getInt(c.getColumnIndex("land_scape_id")));
            mCache.add(item);
        }
    }

    public List<Border> getBorder(int landscapeId) {
        if (mCache == null || mCache.size() == 0) {
            warnUp();
        }
        List<Border> res=new ArrayList<>();
        for(Border border:mCache){
            if(border.getLandscapeId()==landscapeId){
                res.add(border);
            }
        }
        return res;
    }

}

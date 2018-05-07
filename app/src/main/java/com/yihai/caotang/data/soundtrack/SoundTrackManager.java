package com.yihai.caotang.data.soundtrack;

import android.database.Cursor;

import com.yihai.caotang.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class SoundTrackManager {
    private static SoundTrackManager instance;
    private List<SoundTrack> mCache;

    public synchronized static SoundTrackManager getInstance() {
        if (instance == null) {
            instance = new SoundTrackManager();
        }
        return instance;
    }

    private SoundTrackManager() {
        mCache = new ArrayList<>();
    }

    /**
     * 热缓存
     * 其他操作直接走热缓存拿,不走数据库
     */
    public void warnUp() {
        String sql = String.format("select * from caotang_sound_track where lang=%d", 1);//TODO  SessionManager.getInstance().getLang().getLang()
        if (mCache.size() == 0) {
            Cursor c = DatabaseManager.getInstance().rawQuery(sql);
            while (c.moveToNext()) {
                SoundTrack item = new SoundTrack();
                item.setId(c.getInt(c.getColumnIndex("id")));
                item.setTitle(c.getString(c.getColumnIndex("title")));
                item.setLang(c.getInt(c.getColumnIndex("lang")));
                item.setSound_uri(c.getString(c.getColumnIndex("sound_uri")));
                item.setPlaying(false);
                item.setPlayed(false);
                mCache.add(item);
            }
        }
    }

    public List<SoundTrack> getCache() {
        if (mCache == null || mCache.size() == 0) {
            warnUp();
        }
        return mCache;
    }

}

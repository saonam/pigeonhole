package com.yihai.caotang.data.game;

import android.database.Cursor;

import com.yihai.caotang.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class GameManager {
    private static GameManager instance;
    private List<GameQuestion> mCache;

    public synchronized static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    private GameManager() {
        mCache = new ArrayList<>();
    }

    /**
     * 热缓存
     * 其他操作直接走热缓存拿,不走数据库
     */
    public void warnUp() {
        String sql = String.format("select * from caotang_game_question where lang=%d", 1);//TODO  SessionManager.getInstance().getLang().getLang()
        Cursor c = DatabaseManager.getInstance().rawQuery(sql);
        while (c.moveToNext()) {
            GameQuestion item = new GameQuestion();
            item.setId(c.getInt(c.getColumnIndex("id")));
            item.setTitle(c.getString(c.getColumnIndex("title")));
            item.setContent(c.getString(c.getColumnIndex("content")));
            item.setLang(c.getInt(c.getColumnIndex("lang")));
            //游戏选项
            item.setOptions(getOptions(item.getId()));
            mCache.add(item);
        }
    }

    private List<GameOption> getOptions(int questionId) {
        List<GameOption> options = new ArrayList<>();
        String sql = String.format("select * from caotang_game_option where lang=%d and game_question_id=%d", 1, questionId);//TODO  SessionManager.getInstance().getLang().getLang()
        Cursor c = DatabaseManager.getInstance().rawQuery(sql);
        while (c.moveToNext()) {
            GameOption item = new GameOption();
            item.setId(c.getInt(c.getColumnIndex("id")));
            item.setTitle(c.getString(c.getColumnIndex("title")));
            item.setLang(c.getInt(c.getColumnIndex("lang")));
            item.setGameQuestionId(c.getInt(c.getColumnIndex("game_question_id")));
            item.setCorrect(c.getInt(c.getColumnIndex("is_correct")) == 1);
            options.add(item);
        }
        return options;
    }

    /**
     * 根据地理位置获取景点数据
     *
     * @return
     */
    public List<GameQuestion> getQuestions() {
        if (mCache == null || mCache.size() == 0) {
            warnUp();
        }

        return mCache;
    }

}

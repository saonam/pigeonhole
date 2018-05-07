package com.yihai.caotang.data.sysinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yihai.caotang.AppContext;

/**
 * Pref保存
 */
public class SysInfoManager {
    private static final String PREF_STORE_XML = "caotang";
    private static final String PREF_SYS_INFO_JSON = "sysinfojson";

    private static SysInfo cache;

    /***********************************
     * 系统信息
     ***********************************/
    public static boolean setSysInfo(SysInfo data) {
        cache = data;
        SharedPreferences store = AppContext.getInstance().getApplicationContext().getSharedPreferences(PREF_STORE_XML, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = store.edit();
        editor.putString(PREF_SYS_INFO_JSON, data.toString());
        editor.commit();
        return true;
    }

    public static SysInfo getSysInfo() {
        if (cache != null) {
            return cache;
        }
        SharedPreferences store = AppContext.getInstance().getApplicationContext().getSharedPreferences(PREF_STORE_XML, Context.MODE_PRIVATE);
        String json = store.getString(PREF_SYS_INFO_JSON, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        } else {
            SysInfo info = new Gson().fromJson(json, SysInfo.class);
            cache = info;
            return info;
        }
    }

    /**
     * 初始化系统设置
     */
    public static void initSysInfo() {

    }
}

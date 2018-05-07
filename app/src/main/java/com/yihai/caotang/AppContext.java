package com.yihai.caotang;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.yihai.caotang.data.DatabaseManager;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.service.SoundTrackClient;
import com.yihai.caotang.utils.FileUtils;
import com.yihai.caotang.utils.SysInfoUtils;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cn.jpush.android.api.JPushInterface;
import xiaofei.library.hermeseventbus.HermesEventBus;

import static com.yihai.caotang.utils.FileUtils.SDPATH;

/**
 * Copyright (C) 2016 Huijimuhe Technologies. All rights reserved.
 * Contact: 20903213@qq.com Zengweizhou
 */
public class AppContext extends Application {

    private static final int LANG_CHINA = 11;
    private static final int LANG_ENGLISH = 12;
    private static final int LANG_JAPAN = 13;
    private static final int LANG_KOREA = 14;


    public static final int SOUND_AFFECT_DEFAULT = 1;

    public static final int SOUND_AFFECT_DIALOG_ALERT = 2;

    public static final int SOUND_AFFECT_DIALOG_OPEN = 3;

    private static AppContext mInstance;
    SoundPool sp;
    int soundID_1;
    int soundID_2;
    int soundID_3;
    public static AppContext getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        Log.d("AppContext", "onCreate");
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
        mInstance = this;
        initSys();
        registerActivityMonitor();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //关闭监听
        AppController.getInstance().onTerminate();
    }


    /**
     * 初始化系统
     */
    private void initSys() {

        //Logger初始化
        Logger.addLogAdapter(new AndroidLogAdapter());

        //音效
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundID_1 = sp.load(getApplicationContext(), R.raw.click, 1);
        soundID_2 = sp.load(getApplicationContext(), R.raw.dialog, 1);
        soundID_3 = sp.load(getApplicationContext(), R.raw.pop, 1);

        //数据库初始化
        if (DatabaseManager.exist()) {
            DatabaseManager.init(AppContext.getInstance());
        }

        //Hermes初始化
        HermesEventBus.getDefault().init(this);

        if (SysInfoUtils.isAppMainProcess()) {
            //JPUSH初始化
            JPushInterface.init(AppContext.getInstance()); // 初始化 JPush
            JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
            //AppController
            AppController.init(this);
        }
    }

    /**
     * 监听activity状态
     */
    private void registerActivityMonitor() {
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (SysInfoUtils.isAppMainProcess()) {
                    AppController.getInstance().addActivity(activity);

                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (SysInfoUtils.isAppMainProcess()) {
                    AppController.getInstance().finishActivity(activity);
                }
            }
        });
    }

    /****************
     * 音效
     *******************/
    public void playEffect() {
        if (SessionManager.getInstance().getSession().isAffectOpen()) {
            sp.play(soundID_1, 1f, 1f, 1, 0, 1.0f);
        }
    }
    public void playDialogEffect() {
        if (SessionManager.getInstance().getSession().isAffectOpen()) {
            sp.play(soundID_2, 1f, 1f, 1, 0, 1.0f);
        }
    }
    public void playDialogPopEffect() {
        if (SessionManager.getInstance().getSession().isAffectOpen()) {
            sp.play(soundID_3, 1f, 1f, 1, 0, 1.0f);
        }
    }
    /*******************
     * 音乐
     *******************/
    public void playSoundTrack(String url) {
        //检查文件是否存在
        if (FileUtils.exist(url)) {
            SoundTrackClient.getInstance(this).play(url);
        }
    }

    public void stopSoundTrack() {
        SoundTrackClient.getInstance(this).stop();
    }

    public void playBackground() {
        SoundTrackClient.getInstance(this).playBackground();
    }

    public void stopBackground() {
        SoundTrackClient.getInstance(this).stopBackground();
    }

    /*******************
     * 图片
     *******************/
    public void load(ImageView view, String uri) {
        Glide.with(this)
                .load(new File(uri))
                .dontAnimate()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(view);
    }

    /****************
     * 异常捕获
     ****************/
    public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
        private static final String TAG = "CustomExceptionHandler";

        private Thread.UncaughtExceptionHandler mDefaultUEH;

        public CustomExceptionHandler() {
            Log.d(TAG, "------------ CustomExceptionHandler ------------");
            mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e(TAG, "------------ uncaughtException ------------ " + ex.getMessage());
            exportLog(ex);
            mDefaultUEH.uncaughtException(thread, ex);
        }

        private void exportLog(Throwable ex) {
            File dir = new File(SDPATH + "log/");
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                if (!dir.exists()) {
                    dir.mkdir();
                }
            }
            try {
                Date date = new Date();//取时间
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                date = calendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String dateString = formatter.format(date);
                PrintWriter writer = new PrintWriter(SDPATH + "log/" + dateString + "_error.log");
                ex.printStackTrace(writer);
                writer.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

}


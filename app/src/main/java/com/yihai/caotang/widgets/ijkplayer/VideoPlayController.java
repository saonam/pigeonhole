package com.yihai.caotang.widgets.ijkplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * ========================================
 * <p>
 * 版 权：dou361.com 版权所有 （C） 2015
 * <p>
 * 作 者：陈冠明
 * <p>
 * 个人网站：http://www.dou361.com
 * <p>
 * 版 本：1.0
 * <p>
 * 创建日期：2016/4/14
 * <p>
 * 描 述：
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class VideoPlayController {

    /**
     * 打印日志的TAG
     */
    private static final String TAG = VideoPlayController.class.getSimpleName();
    /**
     * 全局上下文
     */
    private final Context mContext;
    /**
     * 依附的容器Activity
     */
    private final Activity mActivity;
    /**
     * 原生的Ijkplayer
     */
    private final VideoPlayerView videoView;

    /**
     * 当前状态
     */
    private int status = PlayStateParams.STATE_IDLE;
    /**
     * 视频显示比例,默认保持原视频的大小
     */
    private int currentShowType = PlayStateParams.fitparent;
    /**
     * 播放总时长
     */
    private long duration;
    /**
     * 当前声音大小
     */
    private int volume;
    /**
     * 设备最大音量
     */
    private final int mMaxVolume;
    /**
     * 获取当前设备的宽度
     */
    private final int screenWidthPixels;
    /**
     * 记录播放器竖屏时的高度
     */
    private final int initHeight;
    /**
     * 当前亮度大小
     */
    private float brightness;
    /**
     * 当前播放地址
     */
    private String currentUrl;
    /**
     * 记录进行后台时的播放状态0为播放，1为暂停
     */
    private int bgState;
    /**
     * 第三方so是否支持，默认不支持，true为支持
     */
    private boolean playerSupport;

    /**
     * 音频管理器
     */
    private final AudioManager audioManager;

    /**========================================视频的监听方法==============================================*/

    /**
     * 视频播放时信息回调
     */
    private IMediaPlayer.OnInfoListener onInfoListener;

    /**
     * 新的调用方法，适用非Activity中使用VideoPlayerController，例如fragment、holder中使用
     */
    public VideoPlayController(Activity activity, VideoPlayerView view) {
        this.mActivity = activity;
        this.mContext = activity;
        this.videoView = view;
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport = true;
        } catch (Throwable e) {
            Log.e(TAG, "loadLibraries error", e);
        }
        screenWidthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


        try {
            int e = Settings.System.getInt(this.mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            float progress = 1.0F * (float) e / 255.0F;
            android.view.WindowManager.LayoutParams layout = this.mActivity.getWindow().getAttributes();
            layout.screenBrightness = progress;
            mActivity.getWindow().setAttributes(layout);
        } catch (Settings.SettingNotFoundException var7) {
            var7.printStackTrace();
        }
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                if (onInfoListener != null) {
                    onInfoListener.onInfo(mp, what, extra);
                }
                return true;
            }
        });

        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initHeight = videoView.getLayoutParams().height;
    }

    /**==========================================Activity生命周期方法回调=============================*/


    /**
     * @Override protected void onDestroy() {
     * super.onDestroy();
     * if (player != null) {
     * player.onDestroy();
     * }
     * }
     */
    public VideoPlayController onDestroy() {
        videoView.stopPlayback();
        return this;
    }

    /*** ==========================================对外的方法=============================*/

    /**
     * 设置播放信息监听回调
     */
    public VideoPlayController setOnInfoListener(IMediaPlayer.OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
        return this;
    }


    /**
     * 设置播放区域拉伸类型
     */
    public VideoPlayController setScaleType(int showType) {
        currentShowType = showType;
        videoView.setAspectRatio(currentShowType);
        return this;
    }

    /**
     * 设置播放地址
     * 单个视频地址时
     */
    public VideoPlayController setPlaySource(String url) {
        this.currentUrl = url;
        return this;
    }

    /**
     * 开始播放
     */
    public VideoPlayController startPlay() {

        videoView.setRender(videoView.RENDER_TEXTURE_VIEW);
        videoView.setVideoPath(currentUrl);

        if (playerSupport) {
            videoView.start();
        }
        return this;
    }

    /**
     * 获取视频播放总时长
     */
    public long getDuration() {
        duration = videoView.getDuration();
        return duration;
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        int i = (int) (index * 1.0 / mMaxVolume * 100);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }

    }
}

package com.yihai.caotang.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.yihai.caotang.ISoundTrackService;

import java.lang.ref.WeakReference;

/**
 * 音乐播放服务
 */
public class SoundTrackService extends Service {
    public static final String TAG = "SoundTrackService";

    public static final String PLAYSTATE_CHANGED = "com.yihai.caotang.playstatechanged";
    public static final String PLAYSTATE_POSITION = "com.yihai.caotang.playstateposition";
    public static final String PLAYSTATE_COMPLETE = "com.yihai.caotang.playstatecomplete";

    public static final int CMDSTOP = 10;
    public static final int CMDPLAY = 11;

    public static final int SERVER_DIED = 20;
    public static final int TRACK_ENDED = 21;
    public static final int FADEDOWN = 22;
    public static final int FADEUP = 23;

    private IBinder mBinder;

    private HandlerThread mPlayerThread;
    private PlayerHandler mPlayerHandler;

    private AudioManager mAudioManager;

    private SoundTrackPlayer mPlayer;

    private boolean mIsInitialized = false;
    private boolean mSoundTrackIsPlaying = false;
    private boolean mBackgroundIsPlaying = false;
    private String curPlayingPath;

    public SoundTrackService() {
        mBinder = new SoundTrackServiceStub(this);

        mPlayerThread = new HandlerThread("soundTrackPlayerThread");
        mPlayerThread.start();
        mPlayerHandler = new PlayerHandler(this, mPlayerThread.getLooper());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new SoundTrackPlayer(this);
        mPlayer.setHandler(mPlayerHandler);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayerHandler.removeCallbacksAndMessages(null);
        mPlayerThread.quit();
    }

    private void play(String uri) {
        if (uri.equals(curPlayingPath)) {
            return;
        }

        curPlayingPath = uri;
        if (mSoundTrackIsPlaying) {
            mPlayer.stop();
            notifyComplete();
        }

        mPlayer.setSoundTrackPath(uri);
        mPlayer.start();
        mSoundTrackIsPlaying = true;
    }

    private void stop() {
        mPlayer.stop();
        mSoundTrackIsPlaying = false;
        curPlayingPath = "";
    }

    private void playBackground() {
        if (mBackgroundIsPlaying) {
            mPlayer.stopBackground();
        }
        mPlayer.startBackground();
        mBackgroundIsPlaying = true;
    }

    private void stopBackground() {
        mPlayer.stopBackground();
        mBackgroundIsPlaying = false;
    }

    private void sendPlayposition(int position, int duration) {
        Intent intent = new Intent(PLAYSTATE_POSITION);
        intent.putExtra("position", position);
        intent.putExtra("duration", duration);
        sendBroadcast(intent);
    }

    public long duration() {
        if (mPlayer.isInitialized()) {
            return mPlayer.duration();
        }
        return -1;
    }

    public long position() {
        if (mPlayer.isInitialized()) {
            return mPlayer.position();
        }
        return -1;
    }

    public boolean isPlayingSoundTrack() {
        return mSoundTrackIsPlaying;
    }

    /**
     * 音轨重制
     */
    public void notifyComplete() {
        Intent intent = new Intent(PLAYSTATE_COMPLETE);
        sendBroadcast(intent);
    }

    private static final class PlayerHandler extends Handler {
        private final WeakReference<SoundTrackService> mService;
        private float mCurrentVolume = 1.0f;

        public PlayerHandler(final SoundTrackService service, final Looper looper) {
            super(looper);
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(final Message msg) {
            final SoundTrackService service = mService.get();
            if (service == null) {
                return;
            }

            synchronized (service) {
                switch (msg.what) {
                    case FADEDOWN:
                        mCurrentVolume -= .05f;
                        if (mCurrentVolume > .2f) {
                            sendEmptyMessageDelayed(FADEDOWN, 10);
                        } else {
                            mCurrentVolume = .2f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case FADEUP:
                        mCurrentVolume += .01f;
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(FADEUP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case SERVER_DIED:
                        break;
                    case TRACK_ENDED:
                        service.curPlayingPath = "";
                        service.mSoundTrackIsPlaying = false;
                        service.notifyComplete();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public class SoundTrackServiceStub extends ISoundTrackService.Stub {
        private final WeakReference<SoundTrackService> mService;

        public SoundTrackServiceStub(SoundTrackService service) {
            this.mService = new WeakReference<>(service);
        }

        @Override
        public void play(String uri) throws RemoteException {
            mService.get().play(uri);
        }

        @Override
        public void stop() throws RemoteException {
            mService.get().stop();
        }

        @Override
        public void playBackground() throws RemoteException {
            mService.get().playBackground();
        }

        @Override
        public void stopBackground() throws RemoteException {
            mService.get().stopBackground();
        }

        @Override
        public long duration() throws RemoteException {
            return mService.get().duration();
        }

        @Override
        public long position() throws RemoteException {
            return mService.get().position();
        }

        @Override
        public boolean isPlayingSoundTrack() {
            return mService.get().isPlayingSoundTrack();
        }
    }
}

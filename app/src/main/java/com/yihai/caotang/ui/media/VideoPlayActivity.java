package com.yihai.caotang.ui.media;

import android.content.Intent;
import android.os.Bundle;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.R;
import com.yihai.caotang.data.Constants;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.event.EntryLandscapeEvent;
import com.yihai.caotang.ui.MainActivity;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.widgets.ijkplayer.PlayStateParams;
import com.yihai.caotang.widgets.ijkplayer.VideoPlayController;
import com.yihai.caotang.widgets.ijkplayer.VideoPlayerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class VideoPlayActivity extends BaseActivity {

    public static final int VIDEO_SPLASH = 1;
    public static final int VIDEO_YINGBI = 2;

    @Bind(R.id.videoplayer)
    VideoPlayerView playerView;
    VideoPlayController playController;

    int mType;
    LandScape mData;

    public static Intent newIntent(int type) {
        Intent intent = new Intent(AppContext.getInstance(),
                VideoPlayActivity.class);
        intent.putExtra("type", type);
        return intent;
    }


    public static Intent newIntent(int type, LandScape landScape) {
        Intent intent = new Intent(AppContext.getInstance(),
                VideoPlayActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data", landScape);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        mType = getIntent().getIntExtra("type", VIDEO_SPLASH);
        mData = getIntent().getParcelableExtra("data");
        playController = new VideoPlayController(this, playerView)
                .setPlaySource(getPath())
                .setScaleType(PlayStateParams.fillparent)
                .setOnInfoListener(new IMediaPlayer.OnInfoListener() {
                                       @Override
                                       public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                                           if (i == 336) {
                                               if (mType == VIDEO_SPLASH) {
                                                   SessionManager.getInstance().getSession().setUsing(true);
                                                   startActivity(MainActivity.newIntent());
                                               }
                                               finish();
                                               return true;
                                           }
                                           return false;
                                       }
                                   }

                )
                .startPlay();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mType == VIDEO_SPLASH) {
            SessionManager.getInstance().getSession().setUsing(true);
        }else{
            HermesEventBus.getDefault().postSticky(new EntryLandscapeEvent(EntryLandscapeEvent.LEAVE, mData));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playController != null) {
            playController.onDestroy();
        }
    }

    private String getPath() {
        if (mType == VIDEO_SPLASH) {
            return Constants.SD_ROOT + "Open.mp4";
        } else {
            return Constants.SD_ROOT + "Qinghua.mp4";
        }
    }
}

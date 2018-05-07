package com.yihai.caotang.ui.media;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.data.landscape.LandScape;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageBrowserActivity extends AppCompatActivity {
    private static final String EXTRA_DATA = "extradata";

    @Bind(R.id.iv_photo)
    ImageView ivPhoto;

    private String mUri;

    public static Intent newIntent(LandScape data) {
        Intent intent = new Intent(AppContext.getInstance(),
                VideoPlayActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browser);
        ButterKnife.bind(this);
        mUri = getIntent().getStringExtra(EXTRA_DATA);
    }

}

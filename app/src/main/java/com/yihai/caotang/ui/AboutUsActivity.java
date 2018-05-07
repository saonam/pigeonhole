package com.yihai.caotang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.media.VideoPlayActivity;
import com.yihai.caotang.utils.SysInfoUtils;
import com.yihai.caotang.widgets.FontButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends BaseActivity {


    /**
     * 不使用，准备更还锁屏
     */
    public static final int MODE_NOT_USING = 2;

    /**
     * 触发地理边界
     */
    public static final int MODE_FENCE_TRIGGER = 3;

    /**
     * 低电量锁屏
     */
    public static final int MODE_LOW_BATTERY = 4;

    int mMode;

    @Bind(R.id.btn_submit)
    FontButton btnSubmit;

    public static Intent newIntent(int mode) {
        Intent intent = new Intent(AppContext.getInstance(),
                AboutUsActivity.class);
        intent.putExtra("mode", mode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        mMode = getIntent().getIntExtra("mode", MODE_NOT_USING);

        initView();
    }

    private void initView() {
        switch (mMode) {
            case MODE_LOW_BATTERY:
                //低电量
                btnSubmit.setText("低电量锁屏");
                SysInfoUtils.changeAppBrightness(5, AboutUsActivity.this);
                btnSubmit.setClickable(false);
                break;
            case MODE_NOT_USING:
                //退回之前
                btnSubmit.setText("请归还");
                SysInfoUtils.changeAppBrightness(5, AboutUsActivity.this);
                btnSubmit.setClickable(false);
                break;
            case MODE_FENCE_TRIGGER:
                //警戒
                btnSubmit.setText("你已进入违法边界！！！");
                btnSubmit.setClickable(false);
                break;
        }
    }

    @OnClick(R.id.btn_submit)
    void onBtnSubmiteClick(View view) {
        switch (mMode) {
            case MODE_LOW_BATTERY:
                break;
            case MODE_NOT_USING:
                break;
        }
    }
}

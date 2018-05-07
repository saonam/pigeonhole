package com.yihai.caotang.ui.manage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.utils.SysInfoUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfigUserActivity extends BaseActivity {

    @Bind(R.id.seekbar_bright)
    SeekBar seekBar;

    @Bind(R.id.group_background)
    RadioGroup groupBackground;

    @Bind(R.id.group_sound_affect)
    RadioGroup groupSoundAffect;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                ConfigUserActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_user);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick(R.id.iv_back)
    void onBackClick(View view) {
        finish();
        AppContext.getInstance().playEffect();
    }

    private void initView() {
        //设置音乐
        if (SessionManager.getInstance().getSession().isBackgroundOpen()) {
            groupBackground.check(R.id.radio_background_open);
        } else {
            groupBackground.check(R.id.radio_background_close);
        }
        groupBackground.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppContext.getInstance().playEffect();
                switch (checkedId) {
                    case R.id.radio_background_open:
                        SessionManager.getInstance().getSession().setBackgroundOpen(true);
                        AppContext.getInstance().playBackground();
                        break;
                    case R.id.radio_background_close:
                        SessionManager.getInstance().getSession().setBackgroundOpen(false);
                        AppContext.getInstance().stopBackground();
                }
            }
        });
        //设置音效
        if (SessionManager.getInstance().getSession().isAffectOpen()) {
            groupSoundAffect.check(R.id.radio_affect_open);
        } else {
            groupSoundAffect.check(R.id.radio_affect_close);
        }
        groupSoundAffect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                AppContext.getInstance().playEffect();
                switch (checkedId) {
                    case R.id.radio_affect_open:
                        SessionManager.getInstance().getSession().setAffectOpen(true);
                        break;
                    case R.id.radio_affect_close:
                        SessionManager.getInstance().getSession().setAffectOpen(false);
                }
            }
        });
        //设置亮度
        //最大刻度
        seekBar.setMax(255);
        //设置初始的Progress
        seekBar.setProgress(SessionManager.getInstance().getSession().getBrightness());
        //监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //改变亮度
                SysInfoUtils.changeAppBrightness(progress, ConfigUserActivity.this);
                SessionManager.getInstance().getSession().setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}

package com.yihai.caotang.ui.manage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfigAdminActivity extends BaseActivity {

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                ConfigAdminActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_admin);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_back)
    void backClick(View v) {
        finish();
        AppContext.getInstance().playEffect();
    }

    @OnClick(R.id.btn_check)
    void checkBtnClick(View view) {
        showLoading();

        //检查网络

        //检查数据库

        dismissLoading();
    }
}

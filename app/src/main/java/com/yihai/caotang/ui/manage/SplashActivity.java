package com.yihai.caotang.ui.manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    public static final int PAGE_FORM = 0;
    public static final int PAGE_SPLASH = 1;

    private SparseArray<Fragment> mChildFragments;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                SplashActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initFragment();
        switchPage(PAGE_SPLASH);
    }

    private void initFragment() {
        mChildFragments = new SparseArray<>();

        //splash
        Fragment splash = getSupportFragmentManager().findFragmentByTag(String.valueOf(PAGE_SPLASH));
        if (splash == null) {
            splash = SplashWelcomeFragment.newInstance();
        }
        mChildFragments.append(PAGE_SPLASH, splash);

        //splash
        Fragment form = getSupportFragmentManager().findFragmentByTag(String.valueOf(PAGE_FORM));
        if (form == null) {
            form = SplashFormFragment.newInstance();
        }
        mChildFragments.append(PAGE_FORM, form);

        //save to activity
//        for (int i = 0; i < mChildFragments.size(); i++) {
//            Fragment f = mChildFragments.valueAt(i);
//            if (!f.isAdded())
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .add(R.id.container, f, String.valueOf(mChildFragments.keyAt(i)))
//                        .toggle(f)
//                        .commit();
//        }
    }

    private void hideFragments() {
        for (int i = 0; i < mChildFragments.size(); i++) {
            Fragment f = mChildFragments.valueAt(i);
            if (f.isAdded())
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(f)
                        .commit();
        }

    }

    public void switchPage(int position) {
        hideFragments();
        Fragment f = mChildFragments.valueAt(position);
        if (!f.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, f, String.valueOf(mChildFragments.keyAt(position)))
                    .show(mChildFragments.get(position))
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(mChildFragments.get(position))
                    .commit();
        }
    }
}

package com.yihai.caotang.ui.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.media.VideoPlayActivity;
import com.yihai.caotang.ui.repository.RepositoryListFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yihai.caotang.widgets.RepositoryTabView.CLICK_LANDSCAPE;

public class GuideMainActivity extends BaseActivity {
    private SparseArray<Fragment> mChildFragments;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                GuideMainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        ButterKnife.bind(this);
        mChildFragments = new SparseArray<>();

        Fragment fragment1 = getSupportFragmentManager().findFragmentByTag("1");
        if (fragment1 == null) {
            fragment1 = GuideImageFragment.newInstance(R.drawable.guide_1);
        }
        ((GuideImageFragment) fragment1).setListener(new GuideImageFragment.onClickListener() {
            @Override
            public void onClick() {
                switchPage(2);
            }
        });
        mChildFragments.append(1, fragment1);

        Fragment fragment2 = getSupportFragmentManager().findFragmentByTag("2");
        if (fragment2 == null) {
            fragment2 = GuideImageFragment.newInstance(R.drawable.guide_2);
        }
        ((GuideImageFragment) fragment2).setListener(new GuideImageFragment.onClickListener() {
            @Override
            public void onClick() {
                switchPage(3);
            }
        });
        mChildFragments.append(2, fragment2);

        Fragment fragment3 = getSupportFragmentManager().findFragmentByTag("3");
        if (fragment3 == null) {
            fragment3 = GuideImageFragment.newInstance(R.drawable.guide_3);
        }
        ((GuideImageFragment) fragment3).setListener(new GuideImageFragment.onClickListener() {
            @Override
            public void onClick() {
                switchPage(4);
            }
        });
        mChildFragments.append(3, fragment3);

        Fragment fragment4 = getSupportFragmentManager().findFragmentByTag("4");
        if (fragment4 == null) {
            fragment4 = GuideTourFragment.newInstance();
        }
        ((GuideTourFragment) fragment4).setListener(new GuideTourFragment.onClickListener() {
            @Override
            public void onClick() {
                switchPage(5);
            }
        });

        mChildFragments.append(4, fragment4);

        Fragment fragment5 = getSupportFragmentManager().findFragmentByTag("5");
        if (fragment5 == null) {
            fragment5 = GuideEntryFragment.newInstance();
        }
        mChildFragments.append(5, fragment5);

        //save to activity
        for (int i = 0; i < mChildFragments.size(); i++) {
            Fragment f = mChildFragments.valueAt(i);
            if (!f.isAdded())
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, f, String.valueOf(mChildFragments.keyAt(i)))
                        .hide(f)
                        .commit();
        }

        hideFragments();
        switchPage(1);
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


    private void switchPage(int position) {
        hideFragments();
        getSupportFragmentManager().beginTransaction().show(mChildFragments.get(position)).commit();
    }

}

package com.yihai.caotang.ui.repository;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.View;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.widgets.RepositoryTabView;
import com.yihai.caotang.widgets.ToolbarView;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.yihai.caotang.widgets.RepositoryTabView.CLICK_ANTIQUE;
import static com.yihai.caotang.widgets.RepositoryTabView.CLICK_LANDSCAPE;
import static com.yihai.caotang.widgets.RepositoryTabView.CLICK_PEOPLE;
import static com.yihai.caotang.widgets.RepositoryTabView.CLICK_POETRY;

public class RepositoryListActivity extends BaseActivity {

    private static final String EXTRA_DATA = "extradata";

    @Bind(R.id.toolbar)
    ToolbarView toolbar;

    @Bind(R.id.tab)
    RepositoryTabView tab;

    private SparseArray<Fragment> mChildFragments;
    private int mCurPageIndex;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                RepositoryListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_list);
        ButterKnife.bind(this);
        initToolbar();
        initTab();
        initFragment();
        switchPage(CLICK_LANDSCAPE);
    }

    private void initToolbar() {
        toolbar.setTitle(AppContext.getInstance().getResources().getDrawable(R.drawable.ic_title_repository));
        toolbar.setmListener(new ToolbarView.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int type) {
                finish();
            }
        });
    }

    private void initTab() {
        tab.setmListener(new RepositoryTabView.OnTabItemClickListener() {
            @Override
            public void onClick(View view, int type) {
                switch (type) {
                    case CLICK_LANDSCAPE:
                        switchPage(CLICK_LANDSCAPE);
                        break;
                    case CLICK_ANTIQUE:
                        switchPage(CLICK_ANTIQUE);
                        break;
                    case CLICK_PEOPLE:
                        switchPage(CLICK_PEOPLE);
                        break;
                    case CLICK_POETRY:
                        switchPage(CLICK_POETRY);
                        break;
                }
            }
        });
    }

    private void initFragment() {
        mChildFragments = new SparseArray<>();
        //景观
        Fragment index = getSupportFragmentManager().findFragmentByTag(String.valueOf(CLICK_LANDSCAPE));
        if (index == null) {
            index = RepositoryListFragment.newInstance(RepositoryListFragment.CATALOG_LANDSCAPE);
        }
        mChildFragments.append(CLICK_LANDSCAPE, index);
        //文物
        Fragment antique = getSupportFragmentManager().findFragmentByTag(String.valueOf(CLICK_ANTIQUE));
        if (antique == null) {
            antique = RepositoryListFragment.newInstance(RepositoryListFragment.CATALOG_ANTIQUE);
        }
        mChildFragments.append(CLICK_ANTIQUE, antique);
        //人物
        Fragment people = getSupportFragmentManager().findFragmentByTag(String.valueOf(CLICK_PEOPLE));
        if (people == null) {
            people = RepositoryListFragment.newInstance(RepositoryListFragment.CATALOG_PEOPLE);
        }
        mChildFragments.append(CLICK_PEOPLE, people);
        //诗词
        Fragment poetry = getSupportFragmentManager().findFragmentByTag(String.valueOf(CLICK_POETRY));
        if (poetry == null) {
            poetry = RepositoryListFragment.newInstance(RepositoryListFragment.CATALOG_POETRY);
        }
        mChildFragments.append(CLICK_POETRY, poetry);

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
        mCurPageIndex = position;
        tab.setSelect(position);
        hideFragments();
        getSupportFragmentManager().beginTransaction().show(mChildFragments.get(position)).commit();
    }
}

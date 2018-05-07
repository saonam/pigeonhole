package com.yihai.caotang.ui.repository;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.adapter.OnItemClickLisener;
import com.yihai.caotang.adapter.repository.RepositoryGridAdapter;
import com.yihai.caotang.data.antique.Antique;
import com.yihai.caotang.data.antique.AntiqueManager;
import com.yihai.caotang.ui.base.BaseLazyFragment;
import com.yihai.caotang.ui.task.TaskListActivity;
import com.yihai.caotang.widgets.GridItemDecoration;
import com.yihai.caotang.widgets.NoGlowRecyclerView;
import com.yihai.caotang.widgets.VerticalListItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RepositoryListFragment extends BaseLazyFragment {
    private static final String KEY_CATALOG = "key_catalog";

    /**
     * type-景点
     */
    public static final int CATALOG_LANDSCAPE = 0;

    /**
     * type-文物
     */
    public static final int CATALOG_ANTIQUE = 1;

    /**
     * type-人物
     */
    public static final int CATALOG_PEOPLE = 2;

    /**
     * type-诗词
     */
    public static final int CATALOG_POETRY = 3;

    @Bind(R.id.ultimate_recycler_view)
    public NoGlowRecyclerView listuv;

    private AbstractListAdapter mAdapter;
    private int mCatalog;

    public static RepositoryListFragment newInstance(int catalog) {
        RepositoryListFragment fragment = new RepositoryListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CATALOG, catalog);
        fragment.setArguments(args);
        return fragment;
    }

    public RepositoryListFragment() {
        // Required empty public constructor
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repo_list, container, false);
        ButterKnife.bind(this, view);
        mCatalog = getArguments().getInt(KEY_CATALOG);
        initList();
        return view;
    }

    private void initList() {
        mAdapter = new RepositoryGridAdapter(new ArrayList<Antique>());
        mAdapter.setOnItemClickLisener(new OnItemClickLisener() {
            @Override
            public void OnItemClick(View view, int position) {
                AppContext.getInstance().playEffect();
                startActivity(RepositoryDetailActivity.newIntent((Antique) mAdapter.getEntity(position)));
            }

            @Override
            public void OnFunctionClick(View view, int position, int functionFlag) {

            }
        });
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 5);
        listuv.setLayoutManager(manager);
        listuv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        List<Antique> res = AntiqueManager.getInstance().getRepositoryList(mCatalog);
        mAdapter.replace(res);
    }
}

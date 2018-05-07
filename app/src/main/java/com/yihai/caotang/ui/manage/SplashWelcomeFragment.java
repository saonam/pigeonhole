package com.yihai.caotang.ui.manage;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashWelcomeFragment extends BaseFragment {

    public static SplashWelcomeFragment newInstance() {
        SplashWelcomeFragment fragment = new SplashWelcomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SplashWelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash_welcome, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_jump_form)
    void onBtnBackClick(View view) {
        ((SplashActivity) getActivity()).switchPage(SplashActivity.PAGE_FORM);
    }
}

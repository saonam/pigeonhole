package com.yihai.caotang.ui.manage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseLazyFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigAdminFormFragment extends BaseLazyFragment {


    public ConfigAdminFormFragment() {
        // Required empty public constructor
    }


    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config_admin_form, container, false);
    }

    @Override
    protected void initData() {

    }
}

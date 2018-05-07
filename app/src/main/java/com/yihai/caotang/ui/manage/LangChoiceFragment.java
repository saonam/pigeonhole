package com.yihai.caotang.ui.manage;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseFragment;

public class LangChoiceFragment extends BaseFragment {


    public LangChoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lang_choice, container, false);
    }

}

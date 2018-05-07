package com.yihai.caotang.ui.guide;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huijimuhei.beacon.utils.ToastUtils;
import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseFragment;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.ui.media.VideoPlayActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideImageFragment extends BaseFragment {

    private final static String LAYOUT_ID = "layoutid";
    private int resId;
    private onClickListener mListener;


    public static GuideImageFragment newInstance(int layoutId) {
        GuideImageFragment pane = new GuideImageFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    public GuideImageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guide_image, container, false);
        resId=getArguments().getInt(LAYOUT_ID);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_guide);
        imageView.setImageDrawable(getResources().getDrawable(resId));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick();
            }
        });
        return view;
    }



    public void setListener(onClickListener listener) {
        this.mListener = listener;
    }

    public interface onClickListener {
        void onClick();
    }
}

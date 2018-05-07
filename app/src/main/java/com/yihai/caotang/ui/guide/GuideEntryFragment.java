package com.yihai.caotang.ui.guide;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.yihai.caotang.R;
import com.yihai.caotang.ui.media.VideoPlayActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class GuideEntryFragment extends Fragment {

    public static GuideEntryFragment newInstance() {
        GuideEntryFragment pane = new GuideEntryFragment();
        Bundle args = new Bundle();
        pane.setArguments(args);
        return pane;
    }

    public GuideEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guide_entry, container, false);
        ButterKnife.bind(this, view);
        view.findViewById(R.id.btn_entry).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                Animation animation = null;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    animation = AnimationUtils.loadAnimation(getActivity(), R.anim.but_scale_down);
                    view.startAnimation(animation);
                }
                //抬起操作
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    animation = AnimationUtils.loadAnimation(getActivity(), R.anim.but_scale_up);
                    view.startAnimation(animation);
                }
                //移动操作
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                }
                return false;
            }
        });
        return view;
    }

    @OnClick(R.id.btn_entry)
    void onBtnSubmiteClick(View view) {
        startActivity(VideoPlayActivity.newIntent(VideoPlayActivity.VIDEO_SPLASH));
        getActivity().finish();
    }
}

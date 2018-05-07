package com.yihai.caotang.ui.guide;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yihai.caotang.R;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.widgets.FontButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideTourFragment extends Fragment {

    private onClickListener mListener;

    @Bind(R.id.iv_tour)
    ImageView ivTour;

    @Bind(R.id.tour_1)
    FontButton btnT1;
    @Bind(R.id.tour_2)
    FontButton btnT2;
    @Bind(R.id.tour_3)
    FontButton btnT3;


    boolean selected = false;

    public static GuideTourFragment newInstance() {
        GuideTourFragment pane = new GuideTourFragment();
        Bundle args = new Bundle();
        pane.setArguments(args);
        return pane;
    }

    public GuideTourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_guide_tour, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.tour_1)
    void onBtnSubmiteClick1(View view) {
        ivTour.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tour_1));
        selected = true;
        btnT1.setSelected(true);
        btnT2.setSelected(false);
        btnT3.setSelected(false);
        SessionManager.getInstance().getSession().setSelTour(1);
    }

    @OnClick(R.id.tour_2)
    void onBtnSubmiteClick2(View view) {
        ivTour.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tour_15));
        selected = true;
        btnT1.setSelected(false);
        btnT2.setSelected(true);
        btnT3.setSelected(false);
        SessionManager.getInstance().getSession().setSelTour(2);
    }

    @OnClick(R.id.tour_3)
    void onBtnSubmiteClick3(View view) {
        ivTour.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.tour_2));
        selected = true;
        btnT1.setSelected(false);
        btnT2.setSelected(false);
        btnT3.setSelected(true);
        SessionManager.getInstance().getSession().setSelTour(3);
    }

    @OnClick(R.id.btn_confirm)
    void onBtnSubmiteClick4(View view) {
        if (selected) {
            mListener.onClick();
        } else {
            new ConfirmDialog(getActivity())
                    .setText("请选择路线")
                    .setConfirmButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
                        @Override
                        public void onClick(View v, Dialog dialog) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    public void setListener(onClickListener listener) {
        this.mListener = listener;
    }

    public interface onClickListener {
        void onClick();
    }
}

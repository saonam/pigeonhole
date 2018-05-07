package com.yihai.caotang.ui.manage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.loopj.android.http.TextHttpResponseHandler;
import com.yihai.caotang.R;
import com.yihai.caotang.client.RequestClient;
import com.yihai.caotang.data.JsonResponse;
import com.yihai.caotang.data.sysinfo.SysInfo;
import com.yihai.caotang.ui.base.BaseFragment;
import com.yihai.caotang.ui.dialog.LoadingDialog;
import com.yihai.caotang.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFormFragment extends BaseFragment {

    @Bind(R.id.et_password)
    EditText etPassword;

    @Bind(R.id.et_name)
    EditText etName;

    public static SplashFormFragment newInstance() {
        SplashFormFragment fragment = new SplashFormFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SplashFormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash_form, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_login)
    void onBtnLoginClick(View view) {
        final LoadingDialog dialog = LoadingDialog.build(getContext());
        showLoading();

        RequestClient.postLogin(etName.getText().toString(), etPassword.getText().toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                dismissLoading();
                ToastUtils.show(getActivity(), "登录网络错误，请重试");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                dismissLoading();
                JsonResponse<SysInfo> wrap = JsonResponse.parse(responseString, SysInfo.class);
                if (wrap.getResult() == 0) {
                    ToastUtils.show(getActivity(), wrap.getMsg());
                    return;
                }
                startActivity(LockScreenActivity.newIntent());
                getActivity().finish();
            }
        });
    }

    @OnClick(R.id.iv_back)
    void onBtnBackClick(View view) {
        ((SplashActivity) getActivity()).switchPage(SplashActivity.PAGE_SPLASH);
    }
}

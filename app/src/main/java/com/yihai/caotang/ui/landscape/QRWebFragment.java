package com.yihai.caotang.ui.landscape;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.ui.base.BaseFragment;

public class QRWebFragment extends BaseFragment {

    private WebView mWebView;
    private ProgressBar mProgress;
    private String mUrl;

    public QRWebFragment() {
        // Required empty public constructor
    }

    public static QRWebFragment newInstance() {
        QRWebFragment fragment = new QRWebFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_qrweb, container, false);
        initUI(root);
        initWeb();
        return root;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!TextUtils.isEmpty(mUrl)) {
                mWebView.loadUrl(mUrl);
            }
        }
    }

    private void initUI(View view) {
        //Set up the toolbar
        //bind view
        mWebView = (WebView) view.findViewById(R.id.webview);
        //progressbar
        mProgress = (ProgressBar) view.findViewById(R.id.progressbar);
        view.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppContext.getInstance().playEffect();
                ((QRScanActivity) getActivity()).switchPage(QRScanActivity.PAGE_SCAN);
            }
        });
        if (!TextUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl);
        }
    }

    private void initWeb() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClientEx());
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgress.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == mProgress.getVisibility()) {
                        mProgress.setVisibility(View.VISIBLE);
                    }
                    mProgress.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    private class WebViewClientEx extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }
}

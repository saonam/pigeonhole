package com.yihai.caotang.ui.landscape;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.R;
import com.yihai.caotang.adapter.OnItemClickLisener;
import com.yihai.caotang.adapter.antique.AntiqueListAdapter;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;
import com.yihai.caotang.event.EntryLandscapeEvent;
import com.yihai.caotang.ui.manage.ConfigUserActivity;
import com.yihai.caotang.ui.repository.RepositoryListActivity;
import com.yihai.caotang.ui.task.TaskListActivity;
import com.yihai.caotang.utils.ToastUtils;
import com.yihai.caotang.utils.ViewUtils;
import com.yihai.caotang.widgets.AntiqueIntroView;
import com.yihai.caotang.widgets.ConfirmEntryView;
import com.yihai.caotang.widgets.LeavingTipView;
import com.yihai.caotang.widgets.NoGlowRecyclerView;
import com.yihai.caotang.widgets.SoundTrackMenuView;
import com.yihai.caotang.widgets.ToggleMenuView;
import com.yihai.caotang.widgets.VerticalListItemDecoration;
import com.yihai.caotang.widgets.zxing.BarCodeScannerFragment;

import xiaofei.library.hermeseventbus.HermesEventBus;

public class QRScanFragment extends BarCodeScannerFragment {

    /**
     * 草堂官网地址
     */
    private static final String MAIN_SITE_URL = "42.121.2.169";

    private RelativeLayout mFloatingContainer;
    private LeavingTipView leavingTipView;
    private WindowManager.LayoutParams mFloatingLayoutParams;
    protected WindowManager mWindowManager;

    private Bundle mSavedInstanceState;
    private boolean isRecognized = false;
    private boolean isFloatingViewAdd = false;
    private boolean isForeground = true;
    protected long mExitTime = 0;
    private String mRoomNum = "Nah";
    private LandScape mData;

    public QRScanFragment() {
        // Required empty public constructor
    }

    public static QRScanFragment newInstance() {
        QRScanFragment fragment = new QRScanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = LandScapeManager.getInstance().getLandscape(19);
        mSavedInstanceState = savedInstanceState;
        mFloatingLayoutParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        this.setmCallBack(new IResultCallback() {
            @Override
            public void result(Result lastResult) {
                //检查网址是否合法
                String url = lastResult.toString();
                int index = url.indexOf(MAIN_SITE_URL);
                if (index < 0) {
                    ToastUtils.show(getActivity(), "错误的二维码");
                } else {
                    ((QRScanActivity) getActivity()).showWeb(url);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isForeground) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addFloatingViews();
                }
            }, 1000);
        } else {
            if (!isFloatingViewAdd) {
                addFloatingViews();
            }
            isForeground = true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        removeFloatingViews();
        isForeground = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            removeFloatingViews();
        }else {
            addFloatingViews();
        }
    }

    /*************************************
     * 浮动窗口
     *************************************/
    public void removeFloatingViews() {
        if (isFloatingViewAdd) {
            try {
                mWindowManager.removeViewImmediate(mFloatingContainer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            isFloatingViewAdd = false;
        }
    }

    public void addFloatingViews() {
        mFloatingLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 设置window type
        mFloatingLayoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        mFloatingLayoutParams.gravity = Gravity.TOP; // 调整悬浮窗口至右侧中间
        mFloatingLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mFloatingLayoutParams.width = ViewUtils.getWindowsWidth(getActivity());// ViewUtils.dip2px(UnityPlayerActivity.this, 149);
        mFloatingLayoutParams.height = ViewUtils.getWindowsHeight(getActivity());// ViewUtils.dip2px(UnityPlayerActivity.this, 446);

        mFloatingContainer = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.include_unity_floating_view, null, false);
        mFloatingContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        //退出键
        mFloatingContainer.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    leavingTipView.setVisibility(View.VISIBLE);
                    leavingTipView.show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    leavingTipView.setVisibility(View.GONE);
                    //通知弹窗
                    HermesEventBus.getDefault().postSticky(new EntryLandscapeEvent(EntryLandscapeEvent.LEAVE, mData));
                    getActivity().finish();
                }
            }
        });

        //扫描提示
        mFloatingContainer.findViewById(R.id.scan_tip).setVisibility(View.VISIBLE);
        //屏蔽内容
        mFloatingContainer.findViewById(R.id.confirm_view).setVisibility(View.GONE);
        mFloatingContainer.findViewById(R.id.antique_intro).setVisibility(View.GONE);
        mFloatingContainer.findViewById(R.id.list_images).setVisibility(View.GONE);
        mFloatingContainer.findViewById(R.id.toggle_menu).setVisibility(View.GONE);
        mFloatingContainer.findViewById(R.id.soundtrack_menu).setVisibility(View.GONE);
        //退出景点提示
        leavingTipView = (LeavingTipView) mFloatingContainer.findViewById(R.id.leaving_view);
        leavingTipView.setListener(new LeavingTipView.OnLeavingListener() {
            @Override
            public void onLeaving() {
                if (leavingTipView != null) {
                    leavingTipView.setVisibility(View.GONE);
                }
            }
        });

        //添加浮动
        mWindowManager.addView(mFloatingContainer, mFloatingLayoutParams);
        isFloatingViewAdd = true;
    }
}

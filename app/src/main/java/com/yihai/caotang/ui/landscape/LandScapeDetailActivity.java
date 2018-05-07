package com.yihai.caotang.ui.landscape;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.R;
import com.yihai.caotang.adapter.OnItemClickLisener;
import com.yihai.caotang.adapter.antique.AntiqueListAdapter;
import com.yihai.caotang.data.antique.Antique;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.event.EntryLandscapeEvent;
import com.yihai.caotang.ui.MainActivity;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.ui.dialog.LeavingTipDialog;
import com.yihai.caotang.utils.SysInfoUtils;
import com.yihai.caotang.utils.ToastUtils;
import com.yihai.caotang.widgets.NoGlowRecyclerView;
import com.yihai.caotang.widgets.ToolbarView;
import com.yihai.caotang.widgets.VerticalListItemDecoration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class LandScapeDetailActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    ToolbarView toolbar;

    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Bind(R.id.iv_banner)
    ImageView ivBanner;

    @Bind(R.id.tv_poetry)
    TextView tvPoetry;

    @Bind(R.id.tv_content)
    TextView tvContent;


    @Bind(R.id.listuv)
    NoGlowRecyclerView mAntiqueListView;

    private LeavingTipDialog mLeavingTip;
    private LandScape mData;
    private AntiqueListAdapter mAdapter;
    protected long mExitTime = 0;

    public static Intent newIntent(LandScape landScape) {
        Intent intent = new Intent(AppContext.getInstance(),
                LandScapeDetailActivity.class);
        intent.putExtra("data", landScape);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_scape_detail);
        ButterKnife.bind(this);
        mData = getIntent().getParcelableExtra("data");
        mLeavingTip = new LeavingTipDialog(LandScapeDetailActivity.this);

        initToolbar();
        initList();

        if (mData.getAntiques().size() != 0) {
            updateContent(mData.getAntiques().get(0), false);
        }
    }

    @Override
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEntryLandscapeEvevt(final EntryLandscapeEvent event) {
        if (event.getType() == EntryLandscapeEvent.ENTRY) {
            if (!AppController.getInstance().isNearGate(event.getLandScape())) {
                ConfirmDialog dialog = new ConfirmDialog(LandScapeDetailActivity.this)
                        .setText("已靠近" + event.getLandScape().getName() + "，是否进入浏览模式")
                        .setCancelButton("放弃进入", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setConfirmButton("开始浏览", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                AppContext.getInstance().stopSoundTrack();
                                AppController.getInstance().enterLandscape(event.getLandScape());
                                finish();
                            }
                        });
                cleanDialog();
                dialog.show();
                confirmDialogs.add(dialog);
            } else {
                ConfirmDialog dialog = new ConfirmDialog(LandScapeDetailActivity.this)
                        .setText("已靠近大门，是否结束游览？")
                        .setConfirmButton("继续使用", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                            }
                        });
//                        .setCancelButton("结束游览", new ConfirmDialog.OnDialogBtnClickListener() {
//                            @Override
//                            public void onClick(View v, Dialog dialog) {
//                                dialog.dismiss();
//                                if (!AppController.DEBUG) {
//                                    postNotKeepLend();
//                                }
//                            }
//                        });
                cleanDialog();
                dialog.show();
                confirmDialogs.add(dialog);
            }
        } else if (event.getType() == EntryLandscapeEvent.LEAVE) {
            if (AppController.DEBUG) {
                ToastUtils.show(LandScapeDetailActivity.this, "onEntryLandscapeEvevt:quite" + event.getLandScape().getName());
            }
            AppContext.getInstance().stopSoundTrack();
            AppController.getInstance().quitLandscape(event.getLandScape());
            HermesEventBus.getDefault().removeAllStickyEvents();
        } else if (event.getType() == EntryLandscapeEvent.UNITY_TURN_BACK) {
            /**
             * 考虑unity切换的情况
             * 1.需要播放音乐
             * 2.需要检查是否靠近门
             */
            //播放介绍音频
            AppContext.getInstance().stopSoundTrack();
            AppContext.getInstance().playSoundTrack(event.getLandScape().getRealSoundTrackPath());
            HermesEventBus.getDefault().removeAllStickyEvents();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initToolbar() {
        int resId = SysInfoUtils.getResId(LandScapeDetailActivity.this, mData.getId());
        toolbar.setCenterTitle(AppContext.getInstance().getResources().getDrawable(resId));
        toolbar.setmListener(new ToolbarView.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int type) {
                //通知弹窗
                HermesEventBus.getDefault().postSticky(new EntryLandscapeEvent(EntryLandscapeEvent.LEAVE, mData));
                finish();
            }
        });
    }

    private void updateContent(Antique item) {
        updateContent(item, true);
    }

    private void updateContent(Antique item, boolean play) {
        //标题
        tvTitle.setText(item.getName().trim());
        //图片
        if (!TextUtils.isEmpty(item.getImage_uri())) {
            ivBanner.setVisibility(View.VISIBLE);
            AppContext.getInstance().load(ivBanner, item.getRealImageUri());
        } else {
            ivBanner.setVisibility(View.GONE);
        }

        //内容
        if (TextUtils.isEmpty(item.getRepository_content())) {
            tvPoetry.setVisibility(View.GONE);
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            String content = item.getRepository_content().replace("\\n", "\n");
            int index = content.indexOf("<poetry>");
            if (index < 0) {
                tvPoetry.setVisibility(View.GONE);
                tvContent.setText(content);
            } else {
                tvPoetry.setVisibility(View.VISIBLE);
                tvPoetry.setText(content.substring(0, index));
                tvContent.setText(content.substring(index + 8, content.length()));
            }
        }

        //语音介绍
        if (!TextUtils.isEmpty(item.getSound_track_uri()) && play) {
            AppContext.getInstance().playSoundTrack(item.getRealSoundTrackUri());
        } else {
            AppContext.getInstance().stopSoundTrack();
        }
    }

    private void initList() {
        mAdapter = new AntiqueListAdapter(mData.getAntiques());
        mAdapter.setOnItemClickLisener(new OnItemClickLisener() {
            @Override
            public void OnItemClick(View view, int position) {
                updateContent(mAdapter.getEntity(position));
            }

            @Override
            public void OnFunctionClick(View view, int position, int functionFlag) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(LandScapeDetailActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mAntiqueListView.setLayoutManager(manager);
        mAntiqueListView.setHasFixedSize(true);
        mAntiqueListView.setClipToPadding(true);
        mAntiqueListView.setEmptyView(R.layout.include_empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);
        mAntiqueListView.setAdapter(mAdapter);
        mAntiqueListView.addItemDecoration(new VerticalListItemDecoration(LandScapeDetailActivity.this, 0, 7));
    }
}

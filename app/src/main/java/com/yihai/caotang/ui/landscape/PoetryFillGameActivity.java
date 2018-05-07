package com.yihai.caotang.ui.landscape;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.adapter.OnItemClickLisener;
import com.yihai.caotang.adapter.landscape.GameOptionListAdapter;
import com.yihai.caotang.data.game.GameManager;
import com.yihai.caotang.data.game.GameOption;
import com.yihai.caotang.data.game.GameQuestion;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PoetryFillGameActivity extends BaseActivity {

    @Bind(R.id.tv_question)
    TextView tvQuestion;

    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Bind(R.id.ultimate_recycler_view)
    public UltimateRecyclerView listuv;

    private AbstractListAdapter mAdapter;
    private int curIndex = 0;
    private int correctCount = 0;
    private List<GameQuestion> mQuestions;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                PoetryFillGameActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poetry_fill_game);
        ButterKnife.bind(this);

        initList();
        initData();
    }

    @OnClick(R.id.iv_back)
    void onBackClick(View view) {
        finish();
        AppContext.getInstance().playEffect();
    }

    private void initList() {
        mAdapter = new GameOptionListAdapter(new ArrayList<GameOption>());
        mAdapter.setOnItemClickLisener(new OnItemClickLisener() {
            @Override
            public void OnItemClick(View view, int position) {
                GameOption option = (GameOption) mAdapter.getEntity(position);
                if (option.isCorrect()) {
                    correctCount++;
                }
                curIndex++;
                if (curIndex == 4) {
                    summary();
                } else {
                    tvQuestion.setText(mQuestions.get(curIndex).getTitle());
                    mAdapter.replace(mQuestions.get(curIndex).getOptions());
                }
            }

            @Override
            public void OnFunctionClick(View view, int position, int functionFlag) {

            }
        });
        GridLayoutManager manager = new GridLayoutManager(PoetryFillGameActivity.this, 2);
        listuv.setLayoutManager(manager);
        listuv.setHasFixedSize(true);
        listuv.setClipToPadding(true);
        listuv.mRecyclerView.setFadingEdgeLength(0);
        listuv.mRecyclerView.setVerticalFadingEdgeEnabled(false);
        listuv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        listuv.setEmptyView(R.layout.include_empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);
        listuv.setAdapter(mAdapter);
    }

    protected void initData() {
        mQuestions = GameManager.getInstance().getQuestions();
        Collections.shuffle(mQuestions);
        mAdapter.replace(mQuestions.get(curIndex).getOptions());
        tvTitle.setText(mQuestions.get(curIndex).getTitle());
        tvQuestion.setText(mQuestions.get(curIndex).getContent());
    }

    private void summary() {
        //计算获得星星数量
        int starNum = correctCount * 3;
        SessionManager.getInstance().increaseStarNum(starNum);
        new ConfirmDialog(PoetryFillGameActivity.this)
                .disableClose()
                .setText(starNum == 0 ? "很遗憾没有答对一题" : String.format("恭喜你答对%d题", starNum))
                .setConfirmButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
                    @Override
                    public void onClick(View v, Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }
}

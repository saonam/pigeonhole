package com.yihai.caotang.ui.repository;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.data.antique.Antique;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.widgets.NoGlowRecyclerView;
import com.yihai.caotang.widgets.ToolbarView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RepositoryDetailActivity extends BaseActivity {
    private static final String EXTRA_DATA = "extradata";

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

    private Antique mData;

    public static Intent newIntent(Antique repo) {
        Intent intent = new Intent(AppContext.getInstance(),
                RepositoryDetailActivity.class);
        intent.putExtra(EXTRA_DATA, repo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_detail);
        ButterKnife.bind(RepositoryDetailActivity.this);

        mData = getIntent().getParcelableExtra(EXTRA_DATA);
        initToolbar();
        initData();
    }


    private void initToolbar() {
        toolbar.setTitle(AppContext.getInstance().getResources().getDrawable(R.drawable.ic_title_repository));
        toolbar.setmListener(new ToolbarView.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int type) {
                finish();
            }
        });
    }

    private void initData() {
        //标题
        tvTitle.setText(mData.getName().trim());
        //图片
        if (!TextUtils.isEmpty(mData.getImage_uri())) {
            ivBanner.setVisibility(View.VISIBLE);
            AppContext.getInstance().load(ivBanner, mData.getRealImageUri());
        } else {
            ivBanner.setVisibility(View.GONE);
        }

        //内容
        if (TextUtils.isEmpty(mData.getRepository_content())) {
            tvPoetry.setVisibility(View.GONE);
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            String content = mData.getRepository_content().replace("\\n", "\n");
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
    }
}

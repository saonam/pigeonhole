package com.yihai.caotang.ui.landscape;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yihai.caotang.AppContext;
import com.yihai.caotang.R;
import com.yihai.caotang.event.EntryLandscapeEvent;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.task.TaskListActivity;
import com.yihai.caotang.widgets.ToolbarView;

import butterknife.Bind;
import butterknife.ButterKnife;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class CoinCollectionActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    ToolbarView toolbar;

    @Bind(R.id.tv_content)
    TextView mTvContent;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context,
                CoinCollectionActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_collection);
        ButterKnife.bind(this);

        initToolbar();
        initContent();
    }

    private void initToolbar() {
        toolbar.setTitle(AppContext.getInstance().getResources().getDrawable(R.drawable.ic_title_coin));
        toolbar.setmListener(new ToolbarView.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int type) {
                finish();
            }
        });
    }

    private void initContent() {
    }

}

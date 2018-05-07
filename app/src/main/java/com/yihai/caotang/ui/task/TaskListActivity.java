package com.yihai.caotang.ui.task;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.adapter.OnItemClickLisener;
import com.yihai.caotang.adapter.landscape.LandscapeAntiqueListAdapter;
import com.yihai.caotang.adapter.task.TaskListAdapter;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.data.task.Task;
import com.yihai.caotang.ui.MainActivity;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.utils.SysInfoUtils;
import com.yihai.caotang.utils.ToastUtils;
import com.yihai.caotang.widgets.NoGlowRecyclerView;
import com.yihai.caotang.widgets.VerticalListItemDecoration;
import com.yihai.caotang.widgets.ToolbarView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaskListActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    ToolbarView toolbar;

    @Bind(R.id.ultimate_recycler_view)
    public NoGlowRecyclerView listuv;

    public AbstractListAdapter mAdapter;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context,
                TaskListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);
        initToolbar();
        initList();
        initData();
    }

    private void initToolbar() {
        toolbar.setTitle(AppContext.getInstance().getResources().getDrawable(R.drawable.ic_title_task));
        toolbar.setmListener(new ToolbarView.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int type) {
                finish();
            }
        });
    }

    private void initList() {
        mAdapter = new TaskListAdapter(new ArrayList<Task>());
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listuv.setLayoutManager(manager);
        listuv.setAdapter(mAdapter);
        listuv.addItemDecoration(new VerticalListItemDecoration(TaskListActivity.this, 12, 25));
        mAdapter.setOnItemClickLisener(new OnItemClickLisener() {
            @Override
            public void OnItemClick(View view, int position) {
                Task task = (Task) mAdapter.getEntity(position);
                LandScape selLandScape = LandScapeManager.getInstance().getLandscape(task.getLandScapeId());
                LandScape curLandscape = SessionManager.getInstance().getSession().getCurLandscape();

                if (AppController.DEBUG) {
                    AppController.getInstance().enterLandscape(selLandScape);
                    return;
                }

                if (curLandscape != null && selLandScape.equals(curLandscape)) {
                    AppController.getInstance().enterLandscape(selLandScape);
                } else {
                    new ConfirmDialog(TaskListActivity.this)
                            .setConfirmButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
                                @Override
                                public void onClick(View v, Dialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .setText("请前往景点" + selLandScape.getName() + "浏览")
                            .show();
                }
//                else if (landScape.equals(curLandscape)) {
//                    new ConfirmDialog(TaskListActivity.this)
//                            .setCancelButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
//                                @Override
//                                public void onClick(View v, Dialog dialog) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setText("您当前正在浏览该景点")
//                            .show();
//                }
            }

            @Override
            public void OnFunctionClick(View view, int position, int functionFlag) {

            }
        });
    }

    protected void initData() {
        mAdapter.replace(Task.getInstance().getCache());
    }


}

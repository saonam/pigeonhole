package com.yihai.caotang.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

import com.yihai.caotang.utils.ViewUtils;

public class VerticalListItemDecoration extends RecyclerView.ItemDecoration {

    private int width;
    private int height;

    public VerticalListItemDecoration(Context c, int width, int height) {
        this.width = ViewUtils.dip2px(c, width);
        this.height = ViewUtils.dip2px(c, height);
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {

        if (itemPosition == 0) {
            outRect.top = 0;
        }
    }
}

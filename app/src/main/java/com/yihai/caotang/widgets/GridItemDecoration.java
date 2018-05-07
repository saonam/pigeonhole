package com.yihai.caotang.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yihai.caotang.utils.ViewUtils;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int col;
    private int height;

    public GridItemDecoration(Context c, int col, int height) {
        this.col = col;
        this.height = ViewUtils.dip2px(c, height);
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        int spanCount = getSpanCount(parent);
         if (isLastColum(itemPosition, spanCount)) {
            // 如果是最后一列，则不需要绘制右边
            outRect.set(0, 0, 0, 71);
        } else {
            //普遍情况下
            outRect.set(0, 0, 59, 71);
        }

    }

    /**
     * 获取行数
     *
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    /**
     * 是否是最后一行
     */
    private boolean isLastRow(int pos, int spanCount, int childCount) {
        childCount = childCount - childCount % spanCount;
        if (pos >= childCount) {
            //最后一行
            return true;
        }
        return false;
    }

    /**
     * 判断是否是最后一列
     */
    private boolean isLastColum(int pos, int spanCount) {
        if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
        {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是第一列
     */
    private boolean isFirstColum(int pos, int spanCount) {
        return pos % spanCount == 0;
    }
}

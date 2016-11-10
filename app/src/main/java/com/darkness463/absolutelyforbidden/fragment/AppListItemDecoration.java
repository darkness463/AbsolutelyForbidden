package com.darkness463.absolutelyforbidden.fragment;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by darkn on 2016/4/27.
 */
public class AppListItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public AppListItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = space;
        }
    }
}

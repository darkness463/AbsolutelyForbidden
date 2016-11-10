package com.darkness463.absolutelyforbidden.fragment;

import android.support.v7.widget.RecyclerView;

import com.darkness463.absolutelyforbidden.common.event.ScrollEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by darkn on 2016/4/28.
 */
public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    public static final int SCROLL_DOWN = 0;
    public static final int SCROLL_UP = 1;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 20) {
            EventBus.getDefault().post(new ScrollEvent(SCROLL_DOWN));
        } else if (dy < -20) {
            EventBus.getDefault().post(new ScrollEvent(SCROLL_UP));
        }
    }
}

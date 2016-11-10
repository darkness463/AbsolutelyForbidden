package com.darkness463.absolutelyforbidden.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darkness463.absolutelyforbidden.R;
import com.darkness463.absolutelyforbidden.adapt.RecyclerViewAdapter;
import com.darkness463.absolutelyforbidden.common.event.AppLoadSuccessEvent;
import com.darkness463.absolutelyforbidden.common.log.MyLog;
import com.darkness463.absolutelyforbidden.common.model.AppInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by darkn on 2016/4/19.
 */
public class AppListFragment extends Fragment {

    public static final int TYPE_DISABLE = 2;
    public static final int TYPE_ENABLE = 1;
    public static final String KEY_TYPE = "type";

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private int type;

    public static AppListFragment newInstance(int type) {
        Bundle args = new Bundle();
        AppListFragment fragment = new AppListFragment();
        args.putInt(KEY_TYPE, type);
//        args.putSerializable("appInfos", (ArrayList<AppInfo>) appInfos);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, null);

        EventBus.getDefault().register(this);

//        ArrayList<AppInfo> appInfos = (ArrayList<AppInfo>) getArguments().getSerializable("appInfos");

        type = getArguments().getInt(KEY_TYPE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_app_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

//        int space = getResources().getDimensionPixelSize(R.dimen.app_list_item_space);
//        mRecyclerView.addItemDecoration(new AppListItemDecoration(space));

        if (type == TYPE_DISABLE) {
            RecyclerViewScrollListener scrollListener = new RecyclerViewScrollListener();
            mRecyclerView.addOnScrollListener(scrollListener);
        }

        mAdapter = new RecyclerViewAdapter(this.getContext(), type);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Subscribe
    public void onEvent(AppLoadSuccessEvent event) {
        MyLog.i("on receive AppLoadSuccessEvent: "+type);
        final List<AppInfo> disabledApps = event.disabledApps;
        final List<AppInfo> enabledApps = event.enabledApps;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type == TYPE_DISABLE) {
                    mAdapter.setData(disabledApps);
                } else {
                    mAdapter.setData(enabledApps);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

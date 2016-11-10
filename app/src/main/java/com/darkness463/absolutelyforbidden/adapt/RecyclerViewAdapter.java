package com.darkness463.absolutelyforbidden.adapt;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darkness463.absolutelyforbidden.R;
import com.darkness463.absolutelyforbidden.common.asynctask.LoadAppTask;
import com.darkness463.absolutelyforbidden.common.event.DisableAllEvent;
import com.darkness463.absolutelyforbidden.common.event.ItemCancelEvent;
import com.darkness463.absolutelyforbidden.common.event.ItemSelectEvent;
import com.darkness463.absolutelyforbidden.common.event.PageChangedEvent;
import com.darkness463.absolutelyforbidden.common.log.MyLog;
import com.darkness463.absolutelyforbidden.common.model.AppInfo;
import com.darkness463.absolutelyforbidden.common.utils.DbUtil;
import com.darkness463.absolutelyforbidden.common.utils.PackageUtil;
import com.darkness463.absolutelyforbidden.common.utils.SharedPrefsUtil;
import com.darkness463.absolutelyforbidden.common.utils.ShellUtil;
import com.darkness463.absolutelyforbidden.fragment.AppListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darkn on 2016/4/19.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DefaultViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<AppInfo> appInfos;
//    private int selectPos = -1;
    private List<Integer> selectPos = new ArrayList<>();

//    private AppInfo selectItem;
    private int type;

    public RecyclerViewAdapter(Context context, int type) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
//        this.appInfos = appInfos;
        this.type = type;
        EventBus.getDefault().register(this);
    }

    @Override
    public RecyclerViewAdapter.DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DefaultViewHolder(mLayoutInflater.inflate(R.layout.app_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.DefaultViewHolder holder, int position) {
        AppInfo appInfo = appInfos.get(position);
        holder.tvAppName.setText(appInfo.getAppName());
        holder.ivAppIcon.setImageBitmap(appInfo.getIcon());
        holder.checkBox.setChecked(appInfo.isChecked()) ;
        if (selectPos.contains(position)) {
//        if (selectPos == position) {
//        if (selectItem != null && selectItem.equals(appInfo)) {
            holder.itemLinearLayout.setBackgroundColor(
                    ContextCompat.getColor(mContext, R.color.item_select));
        } else {
            holder.itemLinearLayout.setBackgroundColor(
                    ContextCompat.getColor(mContext, R.color.item_default));
        }

        if (type == AppListFragment.TYPE_ENABLE) {
            holder.tvAppStatus.setVisibility(View.GONE);
            holder.deleteApp.setImageResource(R.drawable.ic_forbidden);
        } else {
            if (appInfo.isEnable()) {
                holder.tvAppStatus.setText("未禁锢");
                holder.tvAppStatus.setTextColor(
                        ContextCompat.getColor(mContext, R.color.red));
                holder.deleteApp.setImageResource(R.drawable.ic_forbidden);
            } else {
                holder.tvAppStatus.setText("已禁锢");
                holder.tvAppStatus.setTextColor(
                        ContextCompat.getColor(mContext, R.color.green));
                holder.deleteApp.setImageResource(R.drawable.ic_delete);
            }
        }
    }

    @Override
    public int getItemCount() {
        return appInfos == null ? 0 : appInfos.size();
    }

    public void setData(List<AppInfo> data) {
//        selectItem = null;
//        selectPos = -1;
        selectPos.clear();
        appInfos = data;
        notifyDataSetChanged();
    }

    public class DefaultViewHolder extends RecyclerView.ViewHolder {


        private LinearLayout itemLinearLayout;
        private LinearLayout infoLinearLayout;
        private CheckBox checkBox;
        private ImageView ivAppIcon;
        private TextView tvAppName;
        private TextView tvAppStatus;

        private ImageView deleteApp;
        private ImageView launchApp;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_item_app_list_item);
            infoLinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_info_app_list_item);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_app_list_item);
            ivAppIcon = (ImageView) itemView.findViewById(R.id.iv_app_list_item);
            tvAppName = (TextView) itemView.findViewById(R.id.tv_name_app_list_item);
            tvAppStatus = (TextView) itemView.findViewById(R.id.tv_status_app_list_item);

            deleteApp = (ImageView) itemView.findViewById(R.id.iv_delete_app_list_item);
            deleteApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppInfo appInfo = appInfos.get(getAdapterPosition());
                    if (type == AppListFragment.TYPE_ENABLE) {
                        DbUtil.insert(appInfo.getPackageName());
                        ShellUtil.disableApp(mContext, appInfo.getPackageName());
                        Toast.makeText(mContext, appInfo.getAppName()+"已禁锢", Toast.LENGTH_SHORT).show();
                        boolean getSys = SharedPrefsUtil.getSys(mContext);
                        new LoadAppTask(mContext, getSys).execute();
                    } else if (type == AppListFragment.TYPE_DISABLE) {
                        if (appInfo.isEnable()) {
                            ShellUtil.disableApp(mContext, appInfo.getPackageName());
                            appInfo.setEnable(false);
                            notifyDataSetChanged();
                            Toast.makeText(mContext, appInfo.getAppName()+"已禁锢", Toast.LENGTH_SHORT).show();
                        } else {
                            DbUtil.delete(appInfo.getPackageName());
                            ShellUtil.enableApp(mContext, appInfo.getPackageName());
                            Toast.makeText(mContext, appInfo.getAppName()+"已开启", Toast.LENGTH_SHORT).show();
                            boolean getSys = SharedPrefsUtil.getSys(mContext);
                            new LoadAppTask(mContext, getSys).execute();
                        }
                    }
                }
            });
            launchApp = (ImageView) itemView.findViewById(R.id.iv_launch_app_list_item);
            launchApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppInfo appInfo = appInfos.get(getAdapterPosition());
                    if (appInfo.isEnable()) {
                        PackageUtil.launchApp(appInfo.getPackageName(), mContext);
                    } else {
                        appInfo.setEnable(true);
                        notifyDataSetChanged();
                        ShellUtil.enableAppAndRun(mContext, appInfo.getPackageName());
                    }
                }
            });

//            checkRelativeLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AppInfo appInfo = appInfos.get(getAdapterPosition());
//                    appInfo.setChecked(!appInfo.isChecked());
//                    notifyDataSetChanged();
//                }
//            });

            infoLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    AppInfo appInfo = appInfos.get(position);
//                    if (selectItem != null && selectItem.equals(appInfo)) {
//                    if (selectPos == position) {
                    if (selectPos.contains(position)) {
//                        selectPos = -1;
//                        selectItem = null;
                        selectPos.remove((Object) position);
                        appInfo.setChecked(false);
                        EventBus.getDefault().post(
                                new ItemCancelEvent(appInfos.get(position).getPackageName()));
                    } else {
//                        if (selectPos != -1) {
//                            appInfos.get(selectPos).setChecked(false);
//                        }
//                        selectPos = position;
////                        selectItem = appInfo;
//                        for (AppInfo app : appInfos) {
//                            app.setChecked(false);
//                        }
                        selectPos.add(position);
                        appInfo.setChecked(true);
                        MyLog.i("pkgName:" + appInfos.get(position).getPackageName());
                        EventBus.getDefault().post(
                                new ItemSelectEvent(appInfos.get(position).getPackageName()));
                    }
//                    if (selectPos.size() == 0) {
//                        EventBus.getDefault().post(new AllItemCancelSelectEvent());
//                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Subscribe
    public void onEvent(PageChangedEvent event) {
        if (selectPos.size() > 0 && type == event.page) {
            MyLog.i("on receive PageChangedEvent: "+event.page);
            selectPos.clear();
            for (AppInfo app : appInfos) {
                app.setChecked(false);
            }
            notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onEvent(DisableAllEvent event) {
        if (type == AppListFragment.TYPE_DISABLE) {
            List<String> pkgs = new ArrayList<>();
            for (AppInfo appInfo : appInfos) {
                if (appInfo.isEnable()) {
                    pkgs.add(appInfo.getPackageName());
                    appInfo.setEnable(false);
                }
            }
            if (pkgs.size() > 0) {
                ShellUtil.disableApp(mContext, pkgs);
                notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "无未禁锢应用", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

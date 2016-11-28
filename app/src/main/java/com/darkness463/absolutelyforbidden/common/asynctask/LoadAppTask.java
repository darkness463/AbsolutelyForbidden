package com.darkness463.absolutelyforbidden.common.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.darkness463.absolutelyforbidden.R;
import com.darkness463.absolutelyforbidden.common.event.AppLoadSuccessEvent;
import com.darkness463.absolutelyforbidden.common.model.AppInfo;
import com.darkness463.absolutelyforbidden.common.utils.BitmapUtil;
import com.darkness463.absolutelyforbidden.common.utils.DbUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by darkn on 2016/4/26.
 */
public class LoadAppTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private PackageManager pm;
    private boolean getSys;
    private ProgressDialog progressDialog;

    public LoadAppTask(Context context, boolean getSys) {
        this.context = context;
        pm = context.getPackageManager();
        this.getSys = getSys;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.dialog_loading_app));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<String> disabledPkgs = DbUtil.getAllApps();

        List<AppInfo> disabledApps = new ArrayList<>();
        List<String> writeToDbList = new ArrayList<>();  // the apps that disabled from other ways
        List<AppInfo> enabledApps = new ArrayList<>();

        List<ApplicationInfo> applications = pm.getInstalledApplications(0);
        Collections.sort(applications, new ApplicationInfo.DisplayNameComparator(pm));
        for (ApplicationInfo info : applications) {
            try {
                // don't add myself
                if (info.packageName.equals(context.getPackageName())) {
                    continue;
                }

                Bitmap icon = BitmapUtil.drawableToBitmap(info.loadIcon(pm));
                if (icon == null) {
                    icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_default);
                }
                boolean sysApp = (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                if (!getSys && sysApp) {
                    continue;
                }
                AppInfo appInfo = new AppInfo();
                appInfo.setPackageName(info.packageName);
                appInfo.setAppName(info.loadLabel(pm).toString());
                appInfo.setIcon(icon);
                appInfo.setSysApp(sysApp);
                appInfo.setEnable(info.enabled);

                if (disabledPkgs.contains(info.packageName)) {
                    disabledApps.add(appInfo);
                } else {
                    if (info.enabled) {
                        enabledApps.add(appInfo);
                    } else {
                        disabledApps.add(appInfo);
                        writeToDbList.add(appInfo.getPackageName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(new AppLoadSuccessEvent(disabledApps, enabledApps));
        DbUtil.insertMulti(writeToDbList);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}

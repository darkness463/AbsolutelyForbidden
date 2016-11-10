package com.darkness463.absolutelyforbidden.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by darkn on 2016/4/19.
 */
public class PackageUtil {

    public static List<ApplicationInfo> loadSortedPackages(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.getInstalledApplications(0);
    }

    public static void disableApp(String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        ShellUtil.runShellCommand("pm disable "+packageName, context);
    }

    public static void enableApp(String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        ShellUtil.runShellCommand("pm enable "+packageName, context);
    }

    public static void launchApp(String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            return;
        }

        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

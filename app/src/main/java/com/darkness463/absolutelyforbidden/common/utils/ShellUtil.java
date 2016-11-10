package com.darkness463.absolutelyforbidden.common.utils;

import android.content.Context;

import com.darkness463.absolutelyforbidden.common.asynctask.DisableAppTask;
import com.darkness463.absolutelyforbidden.common.asynctask.EnableAndRunAppTask;
import com.darkness463.absolutelyforbidden.common.asynctask.EnableAppTask;
import com.darkness463.absolutelyforbidden.common.asynctask.ShellTask;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by darkn on 2016/4/18.
 */
public class ShellUtil {

    public static boolean checkRoot() {
        return Shell.SU.available();
    }

    public static void runShellCommand(String command, Context context) {
        runShellCommand(new String[]{command}, context);
    }

    public static void runShellCommand(String[] commands, Context context) {
        ShellTask task = new ShellTask();
        task.setContext(context).execute(commands);
    }

    public static void enableAppAndRun(Context context, String packageName) {
        EnableAndRunAppTask task = new EnableAndRunAppTask(context, packageName);
        task.execute();
    }

    public static void disableApp(Context context, String pkg) {
        List<String> pkgs = new ArrayList<>();
        pkgs.add(pkg);
        DisableAppTask task = new DisableAppTask(context, pkgs);
        task.execute();
    }

    public static void disableApp(Context context, List<String> pkgs) {
        DisableAppTask task = new DisableAppTask(context, pkgs);
        task.execute();
    }

    public static void enableApp(Context context, String packageName) {
        List<String> pkgs = new ArrayList<>();
        pkgs.add(packageName);
        enableApp(context, pkgs);
    }

    public static void enableApp(Context context, List<String> pkgs) {
        EnableAppTask task = new EnableAppTask(context, pkgs);
        task.execute();
    }

}


package com.darkness463.absolutelyforbidden.common.model;

import android.graphics.Bitmap;

/**
 * Created by darkn on 2016/4/19.
 */
public class AppInfo {

    private String packageName;
    private String appName;
    private Bitmap icon;
    private boolean isSysApp;
    private boolean enable;

    private boolean isChecked;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public boolean isSysApp() {
        return isSysApp;
    }

    public void setSysApp(boolean sysApp) {
        isSysApp = sysApp;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", icon=" + icon +
                ", isSysApp=" + isSysApp +
                ", enable=" + enable +
                ", isChecked=" + isChecked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AppInfo) {
            AppInfo cpAppInfo = (AppInfo) o;
            return packageName.equals(cpAppInfo.getPackageName());
        }
        return super.equals(o);
    }
}

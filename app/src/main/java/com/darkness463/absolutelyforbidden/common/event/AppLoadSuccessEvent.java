package com.darkness463.absolutelyforbidden.common.event;

import com.darkness463.absolutelyforbidden.common.model.AppInfo;

import java.util.List;

/**
 * Created by darkn on 2016/4/26.
 */
public class AppLoadSuccessEvent {

    public List<AppInfo> disabledApps;
    public List<AppInfo> enabledApps;

    public AppLoadSuccessEvent(List<AppInfo> disabledApps,
                               List<AppInfo> enabledApps) {
        this.disabledApps = disabledApps;
        this.enabledApps = enabledApps;
    }

}

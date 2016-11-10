package com.darkness463.absolutelyforbidden.common.log;

import android.util.Log;

/**
 * Created by darkn on 2016/4/20.
 */
public class MyLog {

    private static final String TAG = "darkness463";

    public static void i(String log) {
        Log.i(TAG, log);
    }

    public static void e(String log) {
        Log.e(TAG, log);
    }

}

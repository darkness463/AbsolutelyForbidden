package com.darkness463.absolutelyforbidden.common.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefsUtil {

    private final static String DEFAULT_PREF = "Setting";
    public final static String KEY_GET_SYS = "get_sys_app";

    public static void putValue(Context context, String key, int value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(DEFAULT_PREF, Context.MODE_PRIVATE).edit();
        sp.putInt(key, value);
        sp.apply();
    }

    public static void putValue(Context context, String key, boolean value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(DEFAULT_PREF, Context.MODE_PRIVATE).edit();
        sp.putBoolean(key, value);
        sp.apply();
    }

    public static void putValue(Context context, String key, String value) {
        SharedPreferences.Editor sp = context.getSharedPreferences(DEFAULT_PREF, Context.MODE_PRIVATE).edit();
        sp.putString(key, value);
        sp.apply();
    }

    public static int getValue(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(DEFAULT_PREF, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static boolean getValue(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(DEFAULT_PREF, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static String getValue(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(DEFAULT_PREF, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static boolean getSys(Context context) {
        return getValue(context, KEY_GET_SYS, false);
    }

}

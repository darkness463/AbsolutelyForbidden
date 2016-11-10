package com.darkness463.absolutelyforbidden.common.concurrent;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class HandlerExecutor {

    private static HandlerThread sThread = new HandlerThread("darkness463-HandlerExecutor");
    private static Handler sHandlder;

    static {
        sThread.start();
        sHandlder = new Handler(sThread.getLooper());
    }

    public static Handler getHandler() {
        if (sHandlder == null) {
            return new Handler(sThread.getLooper());
        }
        return sHandlder;
    }

    public static Looper getLooper() {
        if (sThread == null) {
            return Looper.getMainLooper();
        }
        return sThread.getLooper();
    }

}

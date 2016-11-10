package com.darkness463.absolutelyforbidden.common.context;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.darkness463.absolutelyforbidden.common.log.MyLog;
import com.darkness463.absolutelyforbidden.db.DBHelper;

public class ApplicationContext {

    private static Context sContext;
    private static Handler sHandler;
    private static DBHelper sDBHelper;

    public static Context getContext() {
        return sContext;
    }

    public synchronized static void init(Context context) {
        if (sContext != null || context == null) {
            return;
        }
        sContext = context.getApplicationContext();

        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }

        if (sDBHelper == null) {
            sDBHelper = new DBHelper(sContext);
        }
    }

    public static void runOnUiThread(Runnable runnable) {
        if (sHandler != null) {
            sHandler.post(runnable);
        } else {
            MyLog.e("ApplicationContext runOnUiThread(Task): handler==null!");
        }
    }

    public static void runOnUiThread(Runnable runnable, long delayMillis) {
        if (sHandler != null) {
            sHandler.postDelayed(runnable, delayMillis);
        } else {
            MyLog.e("ApplicationContext runOnUiThread(Task,delay): handler==null!");
        }
    }

    public static void removeTaskOnUiThread(Runnable runnable) {
        if (sHandler != null) {
            sHandler.removeCallbacks(runnable);
        } else {
            MyLog.e("ApplicationContext removeTaskOnUiThread(): handler==null!");
        }
    }

    public static DBHelper getDBHelper() {
        if (sContext == null) {
            throw new IllegalStateException(
                    "could not getDBHelper before setContext()!");
        }

        return sDBHelper;
    }

}

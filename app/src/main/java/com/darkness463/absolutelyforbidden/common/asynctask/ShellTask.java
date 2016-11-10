package com.darkness463.absolutelyforbidden.common.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by darkn on 2016/4/26.
 */
public class ShellTask extends AsyncTask<String, Void, List<String>> {

    private Context context;

    public ShellTask setContext(Context context) {
        this.context = context;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(String... params) {
        if (Shell.SU.available()) {
            return Shell.SU.run(params);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
    }
}


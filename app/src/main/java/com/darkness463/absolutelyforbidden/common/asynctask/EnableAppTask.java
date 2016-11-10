package com.darkness463.absolutelyforbidden.common.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.darkness463.absolutelyforbidden.R;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by darkn on 2016/4/26.
 */
public class EnableAppTask extends AsyncTask<Void, Void, List<String>> {

    private Context context;
    private List<String> pkgs;
    private ProgressDialog progressDialog;

//    public EnableAndRunAppTask setContext(Context context, String packageName) {
//        this.context = context;
//        this.packageName = packageName;
//        return this;
//    }

    public EnableAppTask(Context context, List<String> pkgs) {
        this.context = context;
        this.pkgs = pkgs;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.dialog_launching_app));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        if (Shell.SU.available()) {
            List<String> commands = new ArrayList<>(pkgs.size());
            for (String pkg : pkgs) {
                commands.add("pm enable "+pkg);
            }
            return Shell.SU.run(commands);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}


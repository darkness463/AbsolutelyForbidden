package com.darkness463.absolutelyforbidden.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.darkness463.absolutelyforbidden.BuildConfig;
import com.darkness463.absolutelyforbidden.R;

public class AboutActivity extends AppCompatActivity {

    private static final String GITHUB_URL = "https://github.com/darkness463/AbsolutelyForbidden";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
    }

    private void initView() {
        TextView version = (TextView) findViewById(R.id.tv_version);
        version.setText("版本：" + BuildConfig.VERSION_NAME);
    }

    public void viewGithub(View view) {
        Uri uri = Uri.parse(GITHUB_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

package com.huanchengfly.icebridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.utils.BridgeUtil;
import com.stericson.RootTools.RootTools;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSharedPreferences("app_data", MODE_PRIVATE).getBoolean("first", true)) {
            startActivity(new Intent(this, IntroActivity.class));
            finish();
            return;
        }
        init();
        MaterialToolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void init() {
        BridgeUtil.init(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
}

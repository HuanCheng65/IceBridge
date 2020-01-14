package com.huanchengfly.icebridge.activities;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.huanchengfly.icebridge.R;

public class EnhanceModeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialToolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_enhance_mode);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_enhance_mode;
    }
}

package com.huanchengfly.icebridge.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public abstract class BaseActivity extends AppCompatActivity {
    public static final int NO_LAYOUT = 0;
    public final static int REQUEST_CODE_PERMISSIONS = 0x233;

    @Override
    public int checkSelfPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() != NO_LAYOUT) {
            setContentView(getLayoutId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @LayoutRes
    protected abstract int getLayoutId();
}

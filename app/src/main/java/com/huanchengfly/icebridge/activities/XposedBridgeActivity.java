package com.huanchengfly.icebridge.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.base.MyApplication;
import com.huanchengfly.icebridge.engines.BaseEngine;
import com.huanchengfly.icebridge.engines.EngineManager;
import com.huanchengfly.icebridge.utils.PackageUtil;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class XposedBridgeActivity extends BaseActivity {
    public static final String INTENT_ACTION = "com.huanchengfly.icebridge.intent.action.XPOSED_BRIDGE";
    public static final String EXTRA_PACKAGE = "package";

    private String packageName;

    private BaseEngine mEngine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = getIntent().getStringExtra(EXTRA_PACKAGE);
        mEngine = EngineManager.getEngine(this);
        start();
    }

    private void bridge() {
        if (packageName == null) {
            finish();
            return;
        }
        mEngine.setEnabled(packageName, true, new BaseEngine.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MyApplication.getInstance(), getString(R.string.toast_retry_share, PackageUtil.getAppName(XposedBridgeActivity.this, packageName)), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure() {
                finish();
            }
        });
    }

    private void requestPermission() {
        mEngine.requestPermission(new BaseEngine.Callback() {
            @Override
            public void onSuccess() {
                bridge();
            }

            @Override
            public void onFailure() {
                Toast.makeText(XposedBridgeActivity.this, R.string.toast_permission_cancel, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void start() {
        if (mEngine.checkPermission() != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        bridge();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bridge;
    }
}

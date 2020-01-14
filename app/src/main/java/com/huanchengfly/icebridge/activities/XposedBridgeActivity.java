package com.huanchengfly.icebridge.activities;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.base.MyApplication;
import com.huanchengfly.icebridge.utils.PackageUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;

import java.util.List;

public class XposedBridgeActivity extends BaseActivity {
    public static final String INTENT_ACTION = "com.huanchengfly.icebridge.intent.action.XPOSED_BRIDGE";
    public static final String EXTRA_PACKAGE = "package";

    private String packageName;
    private Rationale<List<String>> mRationale = (context, permissions, executor) -> new AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle(R.string.title_dialog_permission)
            .setMessage(R.string.message_dialog_permission)
            .setPositiveButton(R.string.button_sure, (dialog, which) -> executor.execute())
            .setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                Toast.makeText(context, R.string.toast_permission_cancel, Toast.LENGTH_SHORT).show();
                executor.cancel();
            })
            .create()
            .show();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = getIntent().getStringExtra(EXTRA_PACKAGE);
        start();
    }

    private void bridge() {
        if (packageName == null) {
            finish();
            return;
        }
        IceBox.setAppEnabledSettings(this, true, packageName);
        Toast.makeText(MyApplication.getInstance(), getString(R.string.toast_retry_share, PackageUtil.getAppName(this, packageName)), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void requestPermission() {
        if (checkSelfPermission(IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            AndPermission.with(this)
                    .runtime()
                    .permission(IceBox.SDK_PERMISSION)
                    .rationale(mRationale)
                    .onGranted(data -> bridge())
                    .onDenied(data -> {
                        boolean hasAlwaysDeniedPermission = AndPermission.hasAlwaysDeniedPermission(this, data);
                        new AlertDialog.Builder(this)
                                .setCancelable(false)
                                .setTitle(R.string.title_dialog_permission)
                                .setMessage(R.string.message_dialog_permission)
                                .setPositiveButton(R.string.button_sure, (dialog, which) -> {
                                    if (hasAlwaysDeniedPermission) {
                                        AndPermission.with(this)
                                                .runtime()
                                                .setting()
                                                .onComeback(this::start)
                                                .start();
                                    } else {
                                        start();
                                    }
                                })
                                .setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                                    Toast.makeText(this, R.string.toast_permission_cancel, Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .create()
                                .show();
                    })
                    .start();
        }
    }

    private void start() {
        if (checkSelfPermission(IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
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

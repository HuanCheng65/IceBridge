package com.huanchengfly.icebridge.utils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.huanchengfly.icebridge.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.Arrays;
import java.util.List;

public final class Util {
    private Util() {
    }

    public static void requestPermission(Context context, Action<List<String>> onGranted, Action<List<String>> onDenied) {
        if (AndPermission.hasPermissions(context, IceBox.SDK_PERMISSION)) {
            onGranted.onAction(Arrays.asList(IceBox.SDK_PERMISSION));
        }
        AndPermission.with(context)
                .runtime()
                .permission(IceBox.SDK_PERMISSION)
                .onGranted(onGranted)
                .onDenied(data -> {
                    boolean hasAlwaysDeniedPermission = AndPermission.hasAlwaysDeniedPermission(context, data);
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setTitle(R.string.title_dialog_permission)
                            .setMessage(R.string.message_dialog_permission)
                            .setPositiveButton(R.string.button_sure, (dialog, which) -> {
                                if (hasAlwaysDeniedPermission) {
                                    AndPermission.with(context)
                                            .runtime()
                                            .setting()
                                            .onComeback(() -> requestPermission(context, onGranted))
                                            .start();
                                } else {
                                    requestPermission(context, onGranted);
                                }
                            })
                            .setNegativeButton(R.string.button_cancel, (dialog, which) -> {
                                Toast.makeText(context, R.string.toast_permission_cancel, Toast.LENGTH_SHORT).show();
                                if (onDenied != null) onDenied.onAction(data);
                            })
                            .create()
                            .show();
                })
                .start();
    }

    public static void requestPermission(Context context, Action<List<String>> onGranted) {
        requestPermission(context, onGranted, null);
    }

    public static boolean isEnhanceModeActive(Context context) {
        return isXposedActive() || isExpModuleActive(context);
    }

    public static boolean isXposedActive() {
        return false;
    }

    public static boolean isExpModuleActive(Context context) {
        boolean isExp = false;
        if (context == null) {
            throw new IllegalArgumentException("context must not be null!!");
        }
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
            Bundle result = null;
            try {
                result = contentResolver.call(uri, "active", null, null);
            } catch (RuntimeException e) {
                // TaiChi is killed, try invoke
                try {
                    Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Throwable e1) {
                    return false;
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null);
            }

            if (result == null) {
                return false;
            }
            isExp = result.getBoolean("active", false);
        } catch (Throwable ignored) {
        }
        return isExp;
    }
}

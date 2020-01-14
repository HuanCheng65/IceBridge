package com.huanchengfly.icebridge.engines;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.utils.PackageUtil;
import com.huanchengfly.icebridge.utils.Util;

public final class IceBoxEngine extends BaseEngine {
    public static final int STATUS_NOT_INSTALL_ICEBOX = 0;
    public static final int STATUS_TOO_OLD_ICEBOX = 1;
    public static final int STATUS_ICEBOX_NOT_ACTIVE = 2;
    public static final int STATUS_NO_PERMISSION = 3;
    public static final int STATUS_WORKING = 4;

    public IceBoxEngine(Context context) {
        super(context);
    }

    @Override
    public void setEnabled(String pkgName, boolean enabled, Callback callback) {
        IceBox.setAppEnabledSettings(getContext(), true, pkgName);
        callback.onSuccess();
    }

    @Override
    public int getEngineStatus() {
        if (IceBox.queryWorkMode(getContext()) == IceBox.WorkMode.MODE_NOT_AVAILABLE) {
            return STATUS_NOT_AVAILABLE;
        } else if (ContextCompat.checkSelfPermission(getContext(), IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            return STATUS_NEED_PERMISSION;
        }
        return BaseEngine.STATUS_WORKING;
    }

    private int getMyStatus() {
        if (IceBox.queryWorkMode(getContext()) == IceBox.WorkMode.MODE_NOT_AVAILABLE) {
            if (!PackageUtil.checkAppInstalled(getContext(), IceBox.PACKAGE_NAME)) {
                return STATUS_NOT_INSTALL_ICEBOX;
            } else if (PackageUtil.getVersionCode(getContext(), IceBox.PACKAGE_NAME) < IceBox.AVAILABLE_VERSION_CODE) {
                return STATUS_TOO_OLD_ICEBOX;
            } else {
                return STATUS_ICEBOX_NOT_ACTIVE;
            }
        } else if (ContextCompat.checkSelfPermission(getContext(), IceBox.SDK_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            return STATUS_NO_PERMISSION;
        }
        return STATUS_WORKING;
    }

    @Override
    public CharSequence getStatusDescription() {
        switch (getMyStatus()) {
            case STATUS_NOT_INSTALL_ICEBOX:
                return getContext().getString(R.string.status_icebox_not_installed);
            case STATUS_TOO_OLD_ICEBOX:
                return getContext().getString(R.string.status_icebox_old);
            case STATUS_ICEBOX_NOT_ACTIVE:
                return getContext().getString(R.string.status_icebox_not_available);
            case STATUS_NO_PERMISSION:
                return getContext().getString(R.string.status_need_permission);
            case STATUS_WORKING:
                if (Util.isEnhanceModeActive(getContext())) {
                    return getContext().getString(R.string.status_description_icebox_mode_enhance);
                }
                return getContext().getString(R.string.status_description_icebox_mode);
        }
        return null;
    }

    @Override
    public int checkPermission() {
        return ContextCompat.checkSelfPermission(getContext(), IceBox.SDK_PERMISSION);
    }

    @Override
    public void requestPermission(Callback callback) {
        Util.requestPermission(getContext(), data -> {
            callback.onSuccess();
            notifyStatusChanged();
        }, data -> callback.onFailure());
    }
}

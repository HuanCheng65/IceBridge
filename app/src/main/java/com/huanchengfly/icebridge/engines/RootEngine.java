package com.huanchengfly.icebridge.engines;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.utils.Util;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class RootEngine extends BaseEngine {
    public static final String TAG = RootEngine.class.getSimpleName();

    public static final int STATUS_NO_ROOT = 0;
    public static final int STATUS_NOT_GIVEN_ROOT = 1;
    public static final int STATUS_WORKING = 2;

    public RootEngine(Context context) {
        super(context);
    }

    public static final String PACKAGE_MANAGER_COMMAND_BASE = "pm ";

    @Override
    public void setEnabled(String pkgName, boolean enabled, Callback callback) {
        String commandStr = PACKAGE_MANAGER_COMMAND_BASE;
        String command2Str = PACKAGE_MANAGER_COMMAND_BASE;
        try {
            int flags = IceBox.getAppEnabledSetting(getContext(), pkgName);
            if ((flags != 0 && !enabled) || (flags == 0 && enabled)) {
                callback.onSuccess();
                return;
            }
            if (!enabled) {
                commandStr += "disable-user ";
            } else if (enabled && flags == IceBox.FLAG_PM_DISABLE_USER) {
                commandStr += "enable ";
            } else if (enabled && flags == IceBox.FLAG_PM_HIDE) {
                commandStr += "unhide ";
            } else if (enabled && flags == IceBox.FLAG_PM_HIDE + IceBox.FLAG_PM_DISABLE_USER) {
                commandStr += "enable ";
                command2Str += "unhide ";
            }
            Command command;
            commandStr += pkgName;
            if (PACKAGE_MANAGER_COMMAND_BASE.equalsIgnoreCase(command2Str)) {
                command = new Command(0, commandStr) {
                    @Override
                    public void commandCompleted(int id, int exitcode) {
                        super.commandCompleted(id, exitcode);
                        if (exitcode == 0) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure();
                        }
                    }
                };
            } else {
                command2Str += pkgName;
                command = new Command(0, commandStr, command2Str) {
                    @Override
                    public void commandCompleted(int id, int exitcode) {
                        super.commandCompleted(id, exitcode);
                        if (exitcode == 0) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure();
                        }
                    }
                };
            }
            try {
                RootTools.getShell(true).add(command);
            } catch (IOException | RootDeniedException | TimeoutException ex) {
                ex.printStackTrace();
                callback.onFailure();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    @Override
    public int getEngineStatus() {
        if (!RootTools.isRootAvailable()) {
            return STATUS_NOT_AVAILABLE;
        } else if (!RootTools.isAccessGiven(0, 0)) {
            return STATUS_NEED_PERMISSION;
        }
        return BaseEngine.STATUS_WORKING;
    }

    private int getMyStatus() {
        if (!RootTools.isRootAvailable()) {
            return STATUS_NO_ROOT;
        } else if (!RootTools.isAccessGiven(0, 0)) {
            return STATUS_NOT_GIVEN_ROOT;
        }
        return STATUS_WORKING;
    }

    @Override
    public CharSequence getStatusDescription() {
        switch (getMyStatus()) {
            case STATUS_NO_ROOT:
                return getContext().getString(R.string.status_description_no_root);
            case STATUS_NOT_GIVEN_ROOT:
                return getContext().getString(R.string.status_description_not_given_root);
            case STATUS_WORKING:
                if (Util.isEnhanceModeActive(getContext())) {
                    return getContext().getString(R.string.status_description_root_working_enhance);
                }
                return getContext().getString(R.string.status_description_root_working);
        }
        return null;
    }

    @Override
    public int checkPermission() {
        if (RootTools.isAccessGiven() && RootTools.isRootAvailable()) {
            return PackageManager.PERMISSION_GRANTED;
        }
        return PackageManager.PERMISSION_DENIED;
    }

    @Override
    public void requestPermission(Callback callback) {
        if (RootTools.isAccessGiven()) {
            Toast.makeText(getContext(), R.string.got_root_permission, Toast.LENGTH_SHORT).show();
            callback.onSuccess();
            notifyStatusChanged();
        } else {
            callback.onFailure();
        }
    }
}

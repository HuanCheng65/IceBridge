package com.huanchengfly.icebridge.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.adapters.ChooseAppAdapter;
import com.huanchengfly.icebridge.beans.BridgeInfo;
import com.huanchengfly.icebridge.beans.BridgeInfo.Bridge;
import com.huanchengfly.icebridge.beans.BridgeInfo.IntentFilter;
import com.huanchengfly.icebridge.engines.BaseEngine;
import com.huanchengfly.icebridge.engines.EngineManager;
import com.huanchengfly.icebridge.utils.BridgeUtil;
import com.huanchengfly.icebridge.utils.PackageUtil;

import java.util.ArrayList;
import java.util.List;

public class BridgeActivity extends BaseActivity {
    public static final String TAG = "Bridge";

    private BridgeInfo bridgeInfo;

    private BaseEngine mEngine;

    private void start() {
        mEngine = EngineManager.getEngine(this);
        if (mEngine.checkPermission() != PackageManager.PERMISSION_GRANTED) {
            mEngine.requestPermission(new BaseEngine.Callback() {
                @Override
                public void onSuccess() {
                    bridge();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(BridgeActivity.this, R.string.request_permission_failure, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else bridge();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BridgeUtil.init(this);
        bridgeInfo = BridgeUtil.getBridgeInfo(this);
        start();
    }

    private void bridge() {
        Intent intent = getIntent();
        if (intent.getScheme() == null) {
            finish();
            return;
        }
        boolean needFinish = true;
        for (Bridge bridge : bridgeInfo.getBridges()) {
            if (hasIntent(intent, bridge)) {
                needFinish = false;
                List<String> packages = getCurrentPackages(bridge);
                if (packages.size() == 0) {
                    continue;
                } else if (packages.size() == 1) {
                    start(intent, packages.get(0));
                    break;
                }
                RecyclerView recyclerView = new RecyclerView(this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                ChooseAppAdapter appAdapter = new ChooseAppAdapter(this, packages, 0);
                recyclerView.setAdapter(appAdapter);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_select_apps)
                        .setView(recyclerView)
                        .setCancelable(false)
                        .setPositiveButton(R.string.button_sure, (dialog, which) -> {
                            start(intent, packages.get(appAdapter.getSelectedPosition()));
                        })
                        .show();
                break;
            }
        }
        if (needFinish) finish();
    }

    @SuppressLint("WrongConstant")
    private void start(Intent intent, String pkg) {
        mEngine.setEnabled(pkg, true, new BaseEngine.Callback() {
            @Override
            public void onSuccess() {
                intent.setComponent(null);
                final PackageManager packageManager = getPackageManager();
                List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
                for (ResolveInfo resolveInfo : list) {
                    if (TextUtils.equals(resolveInfo.activityInfo.packageName, pkg)) {
                        intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                        break;
                    }
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure() {
                finish();
            }
        });
    }

    private List<String> getCurrentPackages(Bridge bridge) {
        List<String> packages = new ArrayList<>();
        for (String packageName : bridge.getPackages()) {
            if (PackageUtil.checkAppInstalled(this, packageName)) {
                packages.add(packageName);
            }
        }
        return packages;
    }

    private boolean hasIntent(Intent intent, Bridge bridge) {
        for (IntentFilter intentFilter : bridge.getIntentFilters()) {
            if (hasIntent(intent, intentFilter)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasIntent(Intent intent, IntentFilter intentFilter) {
        List<String> schemes = intentFilter.getSchemes();
        List<String> hosts = intentFilter.getHosts();
        List<String> paths = intentFilter.getPaths();
        List<String> pathPrefixes = intentFilter.getPathPrefixes();
        Uri uri = intent.getData();
        if (uri == null) {
            return false;
        }
        String currentScheme = uri.getScheme();
        String currentHost = uri.getHost();
        String currentPath = uri.getPath();
        boolean hasIntent = false;
        if (schemes != null && currentScheme != null && schemes.contains(currentScheme.toLowerCase())) {
            hasIntent = true;
            if (hosts != null) {
                hasIntent = currentHost != null && hosts.contains(currentHost.toLowerCase());
            }
            if (hasIntent && pathPrefixes != null) {
                hasIntent = false;
                if (currentPath != null) {
                    for (String prefix : pathPrefixes) {
                        if (currentPath.toLowerCase().startsWith(prefix)) {
                            hasIntent = true;
                            break;
                        }
                    }
                }
            }
            if (hasIntent && paths != null) {
                hasIntent = currentPath != null && paths.contains(currentPath.toLowerCase());
            }
        }
        return hasIntent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bridge;
    }
}
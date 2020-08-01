package com.huanchengfly.icebridge.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.huanchengfly.about.utils.DisplayUtil;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.adapters.GridChooseResolveInfoAdapter;
import com.huanchengfly.icebridge.dividers.SpacesItemDecoration;
import com.huanchengfly.icebridge.engines.BaseEngine;
import com.huanchengfly.icebridge.engines.EngineManager;

import java.util.ArrayList;
import java.util.List;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class ShareBridgeActivity extends BaseActivity {
    private BaseEngine mEngine;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine = EngineManager.getEngine(this);
        Intent intent = getIntent();
        intent.setComponent(null);
        List<ResolveInfo> resolveInfoList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resolveInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES | PackageManager.MATCH_DISABLED_COMPONENTS);
        } else {
            resolveInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES | PackageManager.GET_DISABLED_COMPONENTS);
        }
        List<ResolveInfo> resolveInfos = new ArrayList<>();
        List<ResolveInfo> enabledResolveInfos = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            if (resolveInfo.activityInfo == null) {
                continue;
            }
            if (resolveInfo.activityInfo.packageName.equalsIgnoreCase(getPackageName())) {
                continue;
            }
            if (IceBox.getAppEnabledSetting(resolveInfo.activityInfo.applicationInfo) == 0) {
                enabledResolveInfos.add(resolveInfo);
            } else {
                resolveInfos.add(resolveInfo);
            }
        }
        boolean showEnableApp = getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("show_enable_app", true);
        if (showEnableApp) resolveInfos.addAll(enabledResolveInfos);
        if (resolveInfos.size() == 0) {
            Toast.makeText(this, R.string.no_apps_for_share, Toast.LENGTH_SHORT).show();
            finish();
        } else if (resolveInfos.size() == 1) {
            start(intent, resolveInfos.get(0));
        } else {
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.addItemDecoration(new SpacesItemDecoration(DisplayUtil.dp2px(this, 8)));
            recyclerView.getItemAnimator().setAddDuration(0);
            recyclerView.getItemAnimator().setChangeDuration(0);
            recyclerView.getItemAnimator().setMoveDuration(0);
            recyclerView.getItemAnimator().setRemoveDuration(0);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setLayoutManager(new GridLayoutManager(this, getSpanCount(resolveInfos.size())));
            GridChooseResolveInfoAdapter appAdapter = new GridChooseResolveInfoAdapter(this, resolveInfos);
            recyclerView.setAdapter(appAdapter);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_select_apps)
                    .setView(recyclerView)
                    //.setCancelable(false)
                    .setPositiveButton(R.string.button_sure, (dialog, which) -> {
                        start(intent, resolveInfos.get(appAdapter.getSelectedPosition()));
                    })
                    .setOnDismissListener(dialog -> finish())
                    .show();
        }
    }

    private int getSpanCount(int itemCount) {
        if (itemCount < 2) {
            return 1;
        } else if (itemCount == 2) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bridge;
    }


    private void start(Intent intent, ResolveInfo resolveInfo) {
        mEngine.setEnabled(resolveInfo.activityInfo.packageName, true, new BaseEngine.Callback() {
            @Override
            public void onSuccess() {
                intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure() {
                finish();
            }
        });
    }
}

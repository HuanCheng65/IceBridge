package com.huanchengfly.icebridge.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.activities.AboutActivity;
import com.huanchengfly.icebridge.activities.BridgePreferencesActivity;
import com.huanchengfly.icebridge.activities.EnhanceModeActivity;
import com.huanchengfly.icebridge.api.CommonAPICallback;
import com.huanchengfly.icebridge.api.MyAPI;
import com.huanchengfly.icebridge.beans.BridgeInfo;
import com.huanchengfly.icebridge.beans.UpdateInfo;
import com.huanchengfly.icebridge.engines.BaseEngine;
import com.huanchengfly.icebridge.engines.EngineManager;
import com.huanchengfly.icebridge.utils.BridgeUtil;
import com.huanchengfly.icebridge.utils.PackageUtil;
import com.huanchengfly.icebridge.utils.Util;

public class MainFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = MainFragment.class.getSimpleName();
    static final int STATE_NO_UPDATE = 0;
    static final int STATE_CHECKING_UPDATE = 1;
    static final int STATE_HAS_UPDATE = 2;
    static final int STATE_ERROR = 3;
    static final int STATE_DOWNLOADING = 4;
    static final int STATE_INSTALLING = 5;
    static final int STATE_DOWNLOAD_ERROR = 6;

    private UpdateInfo mUpdateInfo;
    private ImageView configUpdateIcon;
    private ImageView appUpdateIcon;
    private ProgressBar configUpdateProgress;
    private ProgressBar appUpdateProgress;
    private TextView configUpdateTitle;
    private TextView appUpdateTitle;
    private TextView configUpdateSubtitle;
    private TextView appUpdateSubtitle;
    private MaterialButton configUpdateButton;
    private MaterialButton appUpdateButton;
    private ImageView statusIcon;
    private TextView status;
    private RelativeLayout statusBg;
    private SwipeRefreshLayout mRefreshLayout;
    private CardView statusCard;
    private NavigationView navigationView;
    private boolean updateInfoLoaded;

    private BaseEngine mEngine;

    public MainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEngine = EngineManager.getEngine(getAttachContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private void refresh() {
        mRefreshLayout.setRefreshing(true);
        mUpdateInfo = null;
        updateInfoLoaded = false;
        refreshStatus();
        refreshUpdateInfo();
        MyAPI.getInstance().checkUpdate(new CommonAPICallback<UpdateInfo>() {
            @Override
            public void onSuccess(UpdateInfo updateInfo) {
                mUpdateInfo = updateInfo;
                updateInfoLoaded = true;
                mRefreshLayout.setRefreshing(false);
                refreshUpdateInfo();
            }

            @Override
            public void onFailure(int errorCode, String errorMsg) {
                mUpdateInfo = null;
                updateInfoLoaded = true;
                mRefreshLayout.setRefreshing(false);
                refreshUpdateInfo();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statusCard = view.findViewById(R.id.status_card);
        configUpdateIcon = view.findViewById(R.id.config_update_icon);
        appUpdateIcon = view.findViewById(R.id.app_update_icon);
        configUpdateProgress = view.findViewById(R.id.config_update_progress);
        appUpdateProgress = view.findViewById(R.id.app_update_progress);
        configUpdateTitle = view.findViewById(R.id.config_update_title);
        appUpdateTitle = view.findViewById(R.id.app_update_title);
        configUpdateSubtitle = view.findViewById(R.id.config_update_subtitle);
        appUpdateSubtitle = view.findViewById(R.id.app_update_subtitle);
        configUpdateButton = view.findViewById(R.id.config_update_button);
        appUpdateButton = view.findViewById(R.id.app_update_button);
        statusIcon = view.findViewById(R.id.status_icon);
        status = view.findViewById(R.id.status);
        statusBg = view.findViewById(R.id.status_bg);
        mRefreshLayout = view.findViewById(R.id.main_refresh);
        navigationView = view.findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(this);
        statusCard.setOnClickListener(this);
        configUpdateButton.setOnClickListener(this);
        appUpdateButton.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(getAttachContext().getResources().getColor(R.color.colorAccent));
        refresh();
    }

    private void refreshStatus() {
        switch (mEngine.getEngineStatus()) {
            case BaseEngine.STATUS_NOT_AVAILABLE:
                statusBg.setBackgroundTintList(ColorStateList.valueOf(getAttachContext().getResources().getColor(R.color.red)));
                status.setTextColor(getAttachContext().getResources().getColor(R.color.red));
                statusIcon.setImageResource(R.drawable.ic_round_error);
                break;
            case BaseEngine.STATUS_NEED_PERMISSION:
                statusBg.setBackgroundTintList(ColorStateList.valueOf(getAttachContext().getResources().getColor(R.color.color_warning)));
                status.setTextColor(getAttachContext().getResources().getColor(R.color.color_warning));
                statusIcon.setImageResource(R.drawable.ic_round_warning);
                break;
            case BaseEngine.STATUS_WORKING:
                statusBg.setBackgroundTintList(ColorStateList.valueOf(getAttachContext().getResources().getColor(R.color.green)));
                status.setTextColor(getAttachContext().getResources().getColor(R.color.green));
                statusIcon.setImageResource(R.drawable.ic_round_check_circle);
                break;
        }
        status.setText(mEngine.getStatusDescription());
    }

    private void setConfigUpdateState(@State int state) {
        switch (state) {
            case STATE_NO_UPDATE:
                configUpdateIcon.setImageResource(R.drawable.ic_round_check_circle);
                configUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.green));
                configUpdateIcon.setVisibility(View.VISIBLE);
                configUpdateProgress.setVisibility(View.GONE);
                configUpdateTitle.setText(getAttachContext().getString(R.string.title_no_update, getAttachContext().getString(R.string.title_config)));
                configUpdateSubtitle.setText(getAttachContext().getString(R.string.tip_update, BridgeUtil.getVersionName(getAttachContext()), mUpdateInfo.getConfigVersionName()));
                configUpdateSubtitle.setVisibility(View.VISIBLE);
                configUpdateButton.setVisibility(View.GONE);
                break;
            case STATE_CHECKING_UPDATE:
                configUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.colorAccent));
                configUpdateIcon.setVisibility(View.GONE);
                configUpdateProgress.setVisibility(View.VISIBLE);
                configUpdateTitle.setText(getAttachContext().getString(R.string.title_checking_update));
                configUpdateSubtitle.setText("");
                configUpdateSubtitle.setVisibility(View.GONE);
                configUpdateButton.setVisibility(View.GONE);
                break;
            case STATE_HAS_UPDATE:
                configUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.colorAccent));
                configUpdateIcon.setImageResource(R.drawable.ic_round_cloud_upload);
                configUpdateIcon.setVisibility(View.VISIBLE);
                configUpdateProgress.setVisibility(View.GONE);
                configUpdateTitle.setText(getAttachContext().getString(R.string.title_update, getAttachContext().getString(R.string.title_config)));
                configUpdateSubtitle.setText(getAttachContext().getString(R.string.tip_update, BridgeUtil.getVersionName(getAttachContext()), mUpdateInfo.getConfigVersionName()));
                configUpdateSubtitle.setVisibility(View.VISIBLE);
                configUpdateButton.setVisibility(View.VISIBLE);
                break;
            case STATE_ERROR:
                configUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.red));
                configUpdateIcon.setImageResource(R.drawable.ic_round_warning);
                configUpdateIcon.setVisibility(View.VISIBLE);
                configUpdateProgress.setVisibility(View.GONE);
                configUpdateTitle.setText(getAttachContext().getString(R.string.title_check_update_error));
                configUpdateSubtitle.setText("");
                configUpdateSubtitle.setVisibility(View.GONE);
                configUpdateButton.setVisibility(View.GONE);
                break;
            case STATE_DOWNLOAD_ERROR:
                configUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.red));
                configUpdateIcon.setImageResource(R.drawable.ic_round_warning);
                configUpdateIcon.setVisibility(View.VISIBLE);
                configUpdateProgress.setVisibility(View.GONE);
                configUpdateTitle.setText(getAttachContext().getString(R.string.title_download_update_error));
                configUpdateSubtitle.setText("");
                configUpdateSubtitle.setVisibility(View.GONE);
                configUpdateButton.setVisibility(View.GONE);
                break;
            case STATE_DOWNLOADING:
                configUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.colorAccent));
                configUpdateIcon.setVisibility(View.GONE);
                configUpdateProgress.setVisibility(View.VISIBLE);
                configUpdateTitle.setText(getAttachContext().getString(R.string.title_downloading_update));
                configUpdateSubtitle.setText("");
                configUpdateSubtitle.setVisibility(View.GONE);
                configUpdateButton.setVisibility(View.GONE);
                break;
            case STATE_INSTALLING:
                configUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.colorAccent));
                configUpdateIcon.setVisibility(View.GONE);
                configUpdateProgress.setVisibility(View.VISIBLE);
                configUpdateTitle.setText(getAttachContext().getString(R.string.title_installing_update));
                configUpdateSubtitle.setText("");
                configUpdateSubtitle.setVisibility(View.GONE);
                configUpdateButton.setVisibility(View.GONE);
                break;
        }
    }

    private void setAppUpdateState(@State int state) {
        switch (state) {
            case STATE_NO_UPDATE:
                appUpdateIcon.setImageResource(R.drawable.ic_round_check_circle);
                appUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.green));
                appUpdateIcon.setVisibility(View.VISIBLE);
                appUpdateProgress.setVisibility(View.GONE);
                appUpdateTitle.setText(getAttachContext().getString(R.string.title_no_update, getAttachContext().getString(R.string.app_name) + " "));
                appUpdateSubtitle.setText(getAttachContext().getString(R.string.tip_update, PackageUtil.getVersionName(getAttachContext()), mUpdateInfo.getAppVersionName()));
                appUpdateSubtitle.setVisibility(View.VISIBLE);
                appUpdateButton.setVisibility(View.GONE);
                break;
            case STATE_CHECKING_UPDATE:
                appUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.colorAccent));
                appUpdateIcon.setVisibility(View.GONE);
                appUpdateProgress.setVisibility(View.VISIBLE);
                appUpdateTitle.setText(getAttachContext().getString(R.string.title_checking_update));
                appUpdateSubtitle.setText("");
                appUpdateSubtitle.setVisibility(View.GONE);
                appUpdateButton.setVisibility(View.GONE);
                break;
            case STATE_HAS_UPDATE:
                appUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.colorAccent));
                appUpdateIcon.setImageResource(R.drawable.ic_round_cloud_upload);
                appUpdateIcon.setVisibility(View.VISIBLE);
                appUpdateProgress.setVisibility(View.GONE);
                appUpdateTitle.setText(getAttachContext().getString(R.string.title_update, getAttachContext().getString(R.string.app_name)));
                appUpdateSubtitle.setText(getAttachContext().getString(R.string.tip_update, PackageUtil.getVersionName(getAttachContext()), mUpdateInfo.getAppVersionName()));
                appUpdateSubtitle.setVisibility(View.VISIBLE);
                appUpdateButton.setVisibility(View.VISIBLE);
                break;
            case STATE_ERROR:
                appUpdateIcon.setImageResource(R.drawable.ic_round_warning);
                appUpdateIcon.setImageTintList(getAttachContext().getResources().getColorStateList(R.color.red));
                appUpdateIcon.setVisibility(View.VISIBLE);
                appUpdateProgress.setVisibility(View.GONE);
                appUpdateTitle.setText(getAttachContext().getString(R.string.title_check_update_error));
                appUpdateSubtitle.setText("");
                appUpdateSubtitle.setVisibility(View.GONE);
                appUpdateButton.setVisibility(View.GONE);
                break;
        }
    }

    private void refreshUpdateInfo() {
        if (mUpdateInfo == null) {
            if (updateInfoLoaded) {
                setAppUpdateState(STATE_ERROR);
                setConfigUpdateState(STATE_ERROR);
            } else {
                setAppUpdateState(STATE_CHECKING_UPDATE);
                setConfigUpdateState(STATE_CHECKING_UPDATE);
            }
            return;
        }
        if (mUpdateInfo.getAppVersion() > PackageUtil.getVersionCode(getAttachContext())) {
            setAppUpdateState(STATE_HAS_UPDATE);
        } else {
            setAppUpdateState(STATE_NO_UPDATE);
        }
        if (mUpdateInfo.getConfigVersion() > BridgeUtil.getVersionCode(getAttachContext())) {
            setConfigUpdateState(STATE_HAS_UPDATE);
        } else {
            setConfigUpdateState(STATE_NO_UPDATE);
        }
    }

    private boolean isXposedActive() {
        return Util.isEnhanceModeActive(getAttachContext());
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.status_card:
                Util.requestPermission(getAttachContext(), data -> refreshStatus());
                break;
            case R.id.config_update_button:
                setConfigUpdateState(STATE_DOWNLOADING);
                MyAPI.getInstance().updateConfig(new CommonAPICallback<BridgeInfo>() {
                    @Override
                    public void onSuccess(BridgeInfo bridgeInfo) {
                        setConfigUpdateState(STATE_INSTALLING);
                        BridgeUtil.write(getAttachContext(), bridgeInfo);
                        Snackbar.make(mRefreshLayout, getAttachContext().getString(R.string.toast_update_config_success, BridgeUtil.getVersionName(getAttachContext())), BaseTransientBottomBar.LENGTH_SHORT).show();
                        refresh();
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg) {
                        setConfigUpdateState(STATE_DOWNLOAD_ERROR);
                    }
                });
                break;
            case R.id.app_update_button:
                if (mUpdateInfo == null) break;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mUpdateInfo.getAppDownloadUrl())));
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                startActivity(new Intent(getAttachContext(), AboutActivity.class));
                break;
            case R.id.menu_bridge_preferences:
                startActivity(new Intent(getAttachContext(), BridgePreferencesActivity.class));
                break;
            case R.id.menu_enhance_mode:
                startActivity(new Intent(getAttachContext(), EnhanceModeActivity.class));
                break;
        }
        return false;
    }

    @IntDef({STATE_NO_UPDATE, STATE_CHECKING_UPDATE, STATE_HAS_UPDATE, STATE_ERROR, STATE_DOWNLOADING, STATE_INSTALLING, STATE_DOWNLOAD_ERROR})
    @interface State {
    }
}

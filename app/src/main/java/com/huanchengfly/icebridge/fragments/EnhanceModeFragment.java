package com.huanchengfly.icebridge.fragments;

import android.os.Bundle;

import androidx.preference.SwitchPreference;

import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.databases.AppConfig;
import com.huanchengfly.icebridge.utils.Util;

import org.litepal.LitePal;

public class EnhanceModeFragment extends PreferencesFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("preferences");
        addPreferencesFromResource(R.xml.preferences);
        refreshStatus();
        SwitchPreference enhancePreference = findPreference(R.string.key_enhance_mode_status);
        enhancePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enabled = (boolean) newValue;
            new AppConfig("enhance_mode_enabled", enabled ? "1" : "0").saveOrUpdate("key = ?", "enhance_mode_enabled");
            refreshStatus();
            return true;
        });
    }

    private void refreshStatus() {
        SwitchPreference enhancePreference = findPreference(R.string.key_enhance_mode_status);
        if (isXposedActive()) {
            if (isEnabled()) {
                enhancePreference.setSummary(R.string.summary_enhance_mode_active);
            } else {
                enhancePreference.setSummary(R.string.summary_enhance_mode_active_not_enable);
            }
        } else {
            enhancePreference.setSummary(R.string.summary_enhance_mode);
        }
        enhancePreference.setEnabled(isXposedActive());
        enhancePreference.setChecked(isEnabled());
    }

    private boolean isEnabled() {
        AppConfig appConfig = LitePal.where("key = ?", "enhance_mode_enabled").findFirst(AppConfig.class);
        if (appConfig == null) {
            return true;
        }
        return "1".equals(appConfig.getValue());
    }

    private boolean isXposedActive() {
        return Util.isXposedActive();
    }
}

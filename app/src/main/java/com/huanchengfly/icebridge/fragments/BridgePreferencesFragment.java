package com.huanchengfly.icebridge.fragments;

import android.os.Bundle;

import com.huanchengfly.icebridge.R;

public class BridgePreferencesFragment extends PreferencesFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName("preferences");
        addPreferencesFromResource(R.xml.bridge_preferences);
    }
}

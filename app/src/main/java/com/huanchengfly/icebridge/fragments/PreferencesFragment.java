package com.huanchengfly.icebridge.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public abstract class PreferencesFragment extends PreferenceFragmentCompat {
    private Context attachContext;

    /*
     * onAttach(Context) is not called on pre API 23 versions of Android and onAttach(Activity) is deprecated
     * Use onAttachToContext instead
     */
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */
    @CallSuper
    private void onAttachToContext(Context context) {
        attachContext = context;
    }

    @NonNull
    protected Context getAttachContext() {
        return attachContext;
    }

    protected <T extends Preference> T findPreference(@StringRes int keyStringId) {
        return (T) findPreference(getAttachContext().getString(keyStringId));
    }
}

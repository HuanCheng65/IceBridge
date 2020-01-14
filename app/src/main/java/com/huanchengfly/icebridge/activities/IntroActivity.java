package com.huanchengfly.icebridge.activities;

import android.annotation.SuppressLint;
import android.content.Intent;

import com.huanchengfly.icebridge.fragments.intro.WelcomeFragment;
import com.huanchengfly.icebridge.fragments.intro.WorkModeFragment;

public class IntroActivity extends BaseIntroActivity {
    @Override
    protected void onCreateIntro() {
        getAdapter().addFragment(new WelcomeFragment());
        getAdapter().addFragment(new WorkModeFragment());
    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onFinish() {
        getSharedPreferences("app_data", MODE_PRIVATE).edit().putBoolean("first", false).commit();
        startActivity(new Intent(this, MainActivity.class));
    }
}

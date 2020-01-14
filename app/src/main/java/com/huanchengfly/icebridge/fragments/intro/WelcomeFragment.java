package com.huanchengfly.icebridge.fragments.intro;

import androidx.annotation.Nullable;

import com.huanchengfly.icebridge.R;

public class WelcomeFragment extends BaseIntroFragment {
    public WelcomeFragment() {}

    @Nullable
    @Override
    CharSequence getTitle() {
        return getAttachContext().getString(R.string.title_intro_welcome);
    }

    @Nullable
    @Override
    CharSequence getSubtitle() {
        return null;
    }
}

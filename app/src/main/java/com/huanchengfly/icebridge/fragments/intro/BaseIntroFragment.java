package com.huanchengfly.icebridge.fragments.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.activities.BaseIntroActivity;
import com.huanchengfly.icebridge.fragments.BaseFragment;

public abstract class BaseIntroFragment extends BaseFragment {
    private static final int NO_CUSTOM_LAYOUT = -1;

    @Nullable
    abstract CharSequence getTitle();

    @Nullable
    abstract CharSequence getSubtitle();

    public CharSequence getNextButton() {
        return getAttachContext().getString(R.string.button_next_default);
    }

    protected int getCustomLayoutResId() {
        return NO_CUSTOM_LAYOUT;
    }

    protected void initCustomLayout(LinearLayout container) {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_intro, container, false);
        if (NO_CUSTOM_LAYOUT != getCustomLayoutResId()) {
            LinearLayout customLayoutContainer = contentView.findViewById(R.id.custom_layout);
            inflater.inflate(getCustomLayoutResId(), customLayoutContainer, true);
        }
        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (NO_CUSTOM_LAYOUT != getCustomLayoutResId()) {
            initCustomLayout(view.findViewById(R.id.custom_layout));
        }
        TextView title = view.findViewById(R.id.title);
        TextView subtitle = view.findViewById(R.id.subtitle);
        title.setText(getTitle());
        subtitle.setText(getSubtitle());
    }

    protected void setNextButtonEnabled(boolean enabled) {
        if (getAttachContext() instanceof BaseIntroActivity) {
            ((BaseIntroActivity) getAttachContext()).setNextButtonEnabled(enabled);
        }
    }

    public void onVisible() {}

    public boolean getDefaultNextButtonEnabled() {
        return true;
    }

    public boolean onNext() {
        return false;
    }

    public void next() {
        if (getAttachContext() instanceof BaseIntroActivity) {
            ((BaseIntroActivity) getAttachContext()).next();
        }
    }
}

package com.huanchengfly.icebridge.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.huanchengfly.icebridge.R;

public class SingleChooseView extends RelativeLayout implements Checkable {
    private static String TAG = SingleChooseView.class.getSimpleName();

    private TextView title;
    private TextView subtitle;
    private ImageView icon;
    private MaterialRadioButton radioButton;

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled()) {
            return;
        }
        super.setEnabled(enabled);
        title.setEnabled(enabled);
        subtitle.setEnabled(enabled);
        radioButton.setEnabled(enabled);
    }

    public SingleChooseView(Context context) {
        this(context, null);
    }

    public SingleChooseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleChooseView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SingleChooseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.layout_single_choose, this, true);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        icon = findViewById(R.id.icon);
        radioButton = findViewById(R.id.radio);
    }

    public void setIcon(Drawable drawable) {
        icon.setImageDrawable(drawable);
        icon.setVisibility(drawable == null ? GONE : VISIBLE);
    }

    public void setTitle(CharSequence charSequence) {
        title.setText(charSequence);
    }

    public void setSubtitle(CharSequence charSequence) {
        subtitle.setText(charSequence);
    }

    public void setTitle(@StringRes int resId) {
        title.setText(resId);
    }

    public void setSubtitle(@StringRes int resId) {
        subtitle.setText(resId);
    }

    @Override
    public void setChecked(boolean checked) {
        radioButton.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return radioButton.isChecked();
    }

    @Override
    public void toggle() {
        radioButton.toggle();
    }
}

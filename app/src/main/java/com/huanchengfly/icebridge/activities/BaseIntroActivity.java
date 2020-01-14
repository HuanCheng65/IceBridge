package com.huanchengfly.icebridge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.adapters.ViewPagerAdapter;
import com.huanchengfly.icebridge.fragments.intro.BaseIntroFragment;
import com.huanchengfly.icebridge.widgets.MyViewPager;

public abstract class BaseIntroActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private MaterialButton nextButton;
    private MaterialButton prevButton;
    private ViewPagerAdapter adapter;
    private MyViewPager myViewPager;

    public void setNextButtonEnabled(boolean enabled) {
        if (enabled) {
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.INVISIBLE);
        }
    }

    private void refreshButtonState(int position) {
        BaseIntroFragment introFragment = (BaseIntroFragment) adapter.getItem(position);
        setNextButtonEnabled(introFragment.getDefaultNextButtonEnabled());
        if (introFragment.getNextButton() != null) {
            nextButton.setText(introFragment.getNextButton());
        } else {
            if (position + 1 >= adapter.getCount()) {
                nextButton.setText(R.string.button_next_last);
            } else {
                nextButton.setText(R.string.button_next_default);
            }
        }
        if (position > 0 && adapter.getCount() > 0) {
            prevButton.setVisibility(View.VISIBLE);
        } else {
            prevButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        nextButton = findViewById(R.id.button_next);
        prevButton = findViewById(R.id.button_prev);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        myViewPager = findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        myViewPager.addOnPageChangeListener(this);
        myViewPager.setCanScroll(false);
        onCreateIntro();
        myViewPager.setAdapter(adapter);
    }

    public ViewPagerAdapter getAdapter() {
        return adapter;
    }

    protected abstract void onCreateIntro();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_next:
                if (adapter.getCurrentFragmentPosition() + 1 >= adapter.getCount()) {
                    finish();
                    onFinish();
                    break;
                }
                if (((BaseIntroFragment) adapter.getItem(adapter.getCurrentFragmentPosition() + 1)).onNext()) {
                    break;
                }
                myViewPager.setCurrentItem(adapter.getCurrentFragmentPosition() + 1, true);
                break;
            case R.id.button_prev:
                if (adapter.getCurrentFragmentPosition() > 0) {
                    myViewPager.setCurrentItem(adapter.getCurrentFragmentPosition() - 1, true);
                }
                break;
        }
    }

    protected void onFinish() {}

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        refreshButtonState(position);
        ((BaseIntroFragment) adapter.getItem(position)).onVisible();
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    public void next() {
        if (myViewPager.getCurrentItem() + 1 < adapter.getCount()) {
            myViewPager.setCurrentItem(myViewPager.getCurrentItem() + 1, true);
        }
    }
}

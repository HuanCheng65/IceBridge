package com.huanchengfly.about;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huanchengfly.about.adapter.AboutPageAdapter;
import com.huanchengfly.icebridge.R;

import java.util.ArrayList;
import java.util.List;

import static com.huanchengfly.about.AboutPage.Icon.NO_TINT;

public class AboutPage {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final View mView;
    private final RecyclerView mRecyclerView;
    private final List<Item> itemList;
    private View mHeaderView;
    private AboutPageAdapter aboutPageAdapter;

    @SuppressLint("InflateParams")
    public AboutPage(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mView = mInflater.inflate(R.layout.about_page, null);
        mRecyclerView = mView.findViewById(R.id.about_recycler_view);
        aboutPageAdapter = new AboutPageAdapter(mContext);
        aboutPageAdapter.setHasStableIds(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(aboutPageAdapter);
        itemList = new ArrayList<>();
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public AboutPage setHeaderView(View view) {
        this.mHeaderView = view;
        return this;
    }

    public AboutPage setHeaderView(@LayoutRes int layoutId) {
        return this.setHeaderView(View.inflate(mContext, layoutId, null));
    }

    public View into(ViewGroup viewGroup) {
        aboutPageAdapter.setItemList(itemList);
        aboutPageAdapter.setHeaderView(mHeaderView);
        viewGroup.addView(mView);
        return mView;
    }

    public AboutPage addTitle(CharSequence title) {
        itemList.add(new Item(title, true));
        return this;
    }

    public AboutPage addTitle(CharSequence title, @ColorInt int color) {
        itemList.add(new Item(title, true).setTitleTextColor(color));
        return this;
    }

    public AboutPage addTitle(@StringRes int resId) {
        return addTitle(mContext.getString(resId));
    }

    public AboutPage addItem(Item item) {
        itemList.add(item);
        return this;
    }

    public static class Item {
        public static final int TYPE_TITLE = 10;
        public static final int TYPE_ITEM = 11;

        private int type;
        private CharSequence title;
        private CharSequence subtitle;
        private int titleTextColor;
        private int subtitleTextColor;
        private Icon icon;
        private View.OnClickListener onClickListener;

        public Item() {
            setTitleTextColor(-1);
            setSubtitleTextColor(-1);
        }

        public Item(CharSequence title) {
            this(title, false);
        }

        public Item(CharSequence title, boolean isTitle) {
            this();
            setTitle(title);
            setSubtitle(null);
            setIcon((Icon) null);
            setType(isTitle ? TYPE_TITLE : TYPE_ITEM);
            if (isTitle) {
                setTitleTextColor(0xFF4477E0);
            }
        }

        public Item(CharSequence title, CharSequence subtitle) {
            this();
            setTitle(title);
            setSubtitle(subtitle);
            setIcon((Icon) null);
            setType(TYPE_ITEM);
        }

        public Item(CharSequence title, CharSequence subtitle, @DrawableRes int drawableId) {
            this(title, subtitle, new Icon().setIconDrawable(drawableId, true));
        }

        public Item(CharSequence title, CharSequence subtitle, @DrawableRes int drawableId, @ColorInt int tint) {
            this(title, subtitle, new Icon().setIconDrawable(drawableId, true).setIconTint(tint));
        }

        public Item(CharSequence title, CharSequence subtitle, Icon icon) {
            this();
            setTitle(title);
            setSubtitle(subtitle);
            setIcon(icon);
            setType(TYPE_ITEM);
        }

        public int getTitleTextColor() {
            return titleTextColor;
        }

        public Item setTitleTextColor(@ColorInt int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public int getSubtitleTextColor() {
            return subtitleTextColor;
        }

        public Item setSubtitleTextColor(@ColorInt int subtitleTextColor) {
            this.subtitleTextColor = subtitleTextColor;
            return this;
        }

        public int getType() {
            return type;
        }

        public Item setType(int type) {
            this.type = type;
            return this;
        }

        public Item setIntent(Intent intent) {
            setOnClickListener(v -> {
                try {
                    v.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            });
            return this;
        }

        public View.OnClickListener getOnClickListener() {
            return onClickListener;
        }

        public Item setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        public CharSequence getTitle() {
            return title;
        }

        public Item setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public CharSequence getSubtitle() {
            return subtitle;
        }

        public Item setSubtitle(CharSequence subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Icon getIcon() {
            return icon;
        }

        public Item setIcon(int drawable) {
            return setIcon(drawable, true);
        }

        public Item setIcon(@Nullable Icon icon) {
            this.icon = icon;
            return this;
        }

        public Item setIcon(int drawable, boolean isIcon) {
            return setIcon(drawable, isIcon, NO_TINT);
        }

        public Item setIcon(int drawable, @ColorInt int tint) {
            return setIcon(drawable, true, tint);
        }

        public Item setIcon(int drawable, boolean isIcon, @ColorInt int tint) {
            return setIcon(new Icon().setIconDrawable(drawable, isIcon).setIconTint(tint));
        }
    }

    public static class Icon {
        public static final int TYPE_ICON = 0;
        public static final int TYPE_IMAGE = 1;

        public static final int NO_TINT = -1;

        private int type;
        private int drawable;
        private int tint;

        public Icon() {
            setIconTint(NO_TINT);
        }

        public int getType() {
            return type;
        }

        public int getDrawable() {
            return drawable;
        }

        public int getIconTint() {
            return tint;
        }

        public Icon setIconTint(@ColorInt int color) {
            this.tint = color;
            return this;
        }

        public Icon setIconDrawable(@DrawableRes int drawable, boolean isIcon) {
            this.drawable = drawable;
            this.type = isIcon ? TYPE_ICON : TYPE_IMAGE;
            return this;
        }
    }
}

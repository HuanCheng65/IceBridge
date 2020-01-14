package com.huanchengfly.about.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.huanchengfly.about.AboutPage;
import com.huanchengfly.about.ViewHolder;
import com.huanchengfly.about.utils.DisplayUtil;
import com.huanchengfly.icebridge.R;

public class AboutPageAdapter extends BaseAdapter<AboutPage.Item> {
    public AboutPageAdapter(Context context) {
        super(context);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_about;
    }

    @Override
    protected void convert(ViewHolder viewHolder, AboutPage.Item item, int position) {
        int textColor = mContext.getResources().getColor(R.color.text_color_primary);
        int secondaryTextColor = mContext.getResources().getColor(R.color.text_color_secondary);
        viewHolder.setOnClickListener(R.id.item_about_root, item.getOnClickListener());
        viewHolder.setVisibility(R.id.item_about_icon_holder, item.getIcon() == null ? View.GONE : View.VISIBLE);
        viewHolder.setVisibility(R.id.item_about_divider, item.getType() == AboutPage.Item.TYPE_TITLE && position > 0 ? View.VISIBLE : View.GONE);
        viewHolder.setVisibility(R.id.item_about_subtitle, item.getSubtitle() == null ? View.GONE : View.VISIBLE);
        viewHolder.setText(R.id.item_about_title, item.getTitle());
        viewHolder.setText(R.id.item_about_subtitle, item.getSubtitle());
        viewHolder.setTextColor(R.id.item_about_title, item.getTitleTextColor() != -1 ? item.getTitleTextColor() : textColor);
        viewHolder.setTextColor(R.id.item_about_subtitle, item.getSubtitleTextColor() != -1 ? item.getSubtitleTextColor() : secondaryTextColor);
        if (item.getType() == AboutPage.Item.TYPE_ITEM && item.getIcon() != null) {
            switch (item.getIcon().getType()) {
                case AboutPage.Icon.TYPE_ICON:
                    ImageView iconView = viewHolder.getView(R.id.item_about_icon);
                    iconView.setImageResource(item.getIcon().getDrawable());
                    if (item.getIcon().getIconTint() != AboutPage.Icon.NO_TINT) iconView.setImageTintList(ColorStateList.valueOf(item.getIcon().getIconTint()));
                    RelativeLayout.LayoutParams iconLayoutParams = (RelativeLayout.LayoutParams) iconView.getLayoutParams();
                    iconLayoutParams.width = iconLayoutParams.height = DisplayUtil.dp2px(mContext, 24);
                    iconView.setLayoutParams(iconLayoutParams);
                    break;
                case AboutPage.Icon.TYPE_IMAGE:
                    ImageView imageView = viewHolder.getView(R.id.item_about_icon);
                    RelativeLayout.LayoutParams imageLayoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                    imageLayoutParams.width = imageLayoutParams.height = DisplayUtil.dp2px(mContext, 40);
                    imageView.setLayoutParams(imageLayoutParams);
                    Glide.with(mContext)
                            .load(item.getIcon().getDrawable())
                            .apply(RequestOptions.circleCropTransform()
                                    .placeholder(R.drawable.bg_placeholder_circle))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView);
                    break;
            }
        }
    }
}

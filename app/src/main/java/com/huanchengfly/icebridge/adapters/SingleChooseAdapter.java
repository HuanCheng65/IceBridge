package com.huanchengfly.icebridge.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.utils.MyViewHolder;
import com.huanchengfly.icebridge.widgets.SingleChooseView;

import java.lang.ref.WeakReference;
import java.util.List;

public class SingleChooseAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private WeakReference<Context> contextWeakReference;
    private int selectedPos;
    private List<ItemBean> itemBeans;
    private OnItemSelectedListener onItemSelectedListener;

    public int getSelectedPosition() {
        return selectedPos;
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return onItemSelectedListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public SingleChooseAdapter(Context context, List<ItemBean> itemBeans) {
        this(context, itemBeans, -1);
    }

    public SingleChooseAdapter(Context context, List<ItemBean> itemBeans, int defaultSelectedPos) {
        this.contextWeakReference = new WeakReference<>(context);
        this.itemBeans = itemBeans;
        this.selectedPos = defaultSelectedPos;
    }

    public Context getContext() {
        return contextWeakReference.get();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MyViewHolder.create(getContext(), R.layout.item_single_choose);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemBean itemBean = itemBeans.get(position);
        SingleChooseView singleChooseView = holder.getView(R.id.single_choose);
        singleChooseView.setOnClickListener(v -> {
            ((SingleChooseView) v).setChecked(true);
            int oldPos = selectedPos + 0;
            notifyItemChanged(oldPos);
            selectedPos = position;
            if (getOnItemSelectedListener() != null) {
                getOnItemSelectedListener().onSelected(position, itemBean);
            }
        });
        singleChooseView.setChecked(position == selectedPos);
        singleChooseView.setEnabled(itemBean.isEnabled());
        singleChooseView.setTitle(itemBean.getTitle());
        singleChooseView.setSubtitle(itemBean.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return itemBeans.size();
    }

    public static class ItemBean {
        private CharSequence title;
        private CharSequence subtitle;
        private boolean enabled;

        public CharSequence getTitle() {
            return title;
        }

        public void setTitle(CharSequence title) {
            this.title = title;
        }

        public void setSubtitle(CharSequence subtitle) {
            this.subtitle = subtitle;
        }

        public void setTitle(Context context, @StringRes int resId) {
            this.title = context.getString(resId);
        }

        public void setSubtitle(Context context, @StringRes int resId) {
            this.subtitle = context.getString(resId);
        }

        public CharSequence getSubtitle() {
            return subtitle;
        }

        public ItemBean(CharSequence title, CharSequence subtitle) {
            this(title, subtitle, true);
        }

        public ItemBean(Context context, @StringRes int title, @StringRes int subtitle) {
            this(context, title, subtitle, true);
        }

        public ItemBean(CharSequence title, CharSequence subtitle, boolean enabled) {
            setTitle(title);
            setSubtitle(subtitle);
            setEnabled(enabled);
        }

        public ItemBean(Context context, @StringRes int title, @StringRes int subtitle, boolean enabled) {
            setTitle(context, title);
            setSubtitle(context, subtitle);
            setEnabled(enabled);
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public interface OnItemSelectedListener {
        void onSelected(int position, ItemBean itemBean);
    }
}

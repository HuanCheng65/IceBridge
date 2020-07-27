package com.huanchengfly.icebridge.adapters;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.utils.MyViewHolder;

import java.lang.ref.WeakReference;
import java.util.List;

public class GridChooseResolveInfoAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private WeakReference<Context> contextWeakReference;
    private int selectedPos;
    private List<ResolveInfo> mResolveInfos;
    private OnItemSelectedListener<ResolveInfo> mOnItemSelectedListener;

    public int getSelectedPosition() {
        return selectedPos;
    }

    public OnItemSelectedListener<ResolveInfo> getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener<ResolveInfo> onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    public GridChooseResolveInfoAdapter(Context context, List<ResolveInfo> resolveInfos) {
        this(context, resolveInfos, -1);
    }

    public GridChooseResolveInfoAdapter(Context context, List<ResolveInfo> resolveInfos, int defaultSelectedPos) {
        this.contextWeakReference = new WeakReference<>(context);
        this.mResolveInfos = resolveInfos;
        this.selectedPos = defaultSelectedPos;
    }

    public Context getContext() {
        return contextWeakReference.get();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MyViewHolder.create(getContext(), R.layout.item_grid_choose);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position == selectedPos) {
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryTrans)));
        } else {
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.white)));
        }
        ResolveInfo resolveInfo = mResolveInfos.get(position);
        TextView title = holder.getView(R.id.title);
        TextView subtitle = holder.getView(R.id.subtitle);
        ImageView icon = holder.getView(R.id.icon);
        View disabled = holder.getView(R.id.disabled);
        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPos;
            notifyItemChanged(oldPos);
            selectedPos = position;
            notifyItemChanged(position);
            if (getOnItemSelectedListener() != null) {
                getOnItemSelectedListener().onSelected(position, resolveInfo);
            }
        });
        title.setText(resolveInfo.loadLabel(getContext().getPackageManager()));
        icon.setImageDrawable(resolveInfo.loadIcon(getContext().getPackageManager()));
        subtitle.setVisibility(View.GONE);
        if (IceBox.getAppEnabledSetting(resolveInfo.activityInfo.applicationInfo) == 0) {
            disabled.setVisibility(View.GONE);
        } else {
            disabled.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mResolveInfos.size();
    }

    public interface OnItemSelectedListener<T> {
        void onSelected(int position, T t);
    }
}

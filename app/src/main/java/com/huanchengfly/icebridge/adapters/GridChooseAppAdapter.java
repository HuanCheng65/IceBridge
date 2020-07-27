package com.huanchengfly.icebridge.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.utils.MyViewHolder;
import com.huanchengfly.icebridge.utils.PackageUtil;

import java.lang.ref.WeakReference;
import java.util.List;

public class GridChooseAppAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private WeakReference<Context> contextWeakReference;
    private int selectedPos;
    private List<String> packages;
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

    public GridChooseAppAdapter(Context context, List<String> packages) {
        this(context, packages, -1);
    }

    public GridChooseAppAdapter(Context context, List<String> packages, int defaultSelectedPos) {
        this.contextWeakReference = new WeakReference<>(context);
        this.packages = packages;
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
        String pkgName = packages.get(position);
        TextView title = holder.getView(R.id.title);
        TextView subtitle = holder.getView(R.id.subtitle);
        ImageView icon = holder.getView(R.id.icon);
        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPos;
            notifyItemChanged(oldPos);
            selectedPos = position;
            notifyItemChanged(selectedPos);
            if (getOnItemSelectedListener() != null) {
                getOnItemSelectedListener().onSelected(position, pkgName);
            }
        });
        title.setText(PackageUtil.getAppName(getContext(), pkgName));
        icon.setImageDrawable(PackageUtil.getAppIcon(getContext(), pkgName));
        subtitle.setText(pkgName);
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public interface OnItemSelectedListener {
        void onSelected(int position, String pkgName);
    }
}

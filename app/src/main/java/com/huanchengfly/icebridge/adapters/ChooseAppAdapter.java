package com.huanchengfly.icebridge.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.utils.MyViewHolder;
import com.huanchengfly.icebridge.utils.PackageUtil;
import com.huanchengfly.icebridge.widgets.SingleChooseView;

import java.lang.ref.WeakReference;
import java.util.List;

public class ChooseAppAdapter extends RecyclerView.Adapter<MyViewHolder> {
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

    public ChooseAppAdapter(Context context, List<String> packages) {
        this(context, packages, -1);
    }

    public ChooseAppAdapter(Context context, List<String> packages, int defaultSelectedPos) {
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
        return MyViewHolder.create(getContext(), R.layout.item_single_choose);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String pkgName = packages.get(position);
        SingleChooseView singleChooseView = holder.getView(R.id.single_choose);
        singleChooseView.setOnClickListener(v -> {
            ((SingleChooseView) v).setChecked(true);
            int oldPos = selectedPos + 0;
            notifyItemChanged(oldPos);
            selectedPos = position;
            if (getOnItemSelectedListener() != null) {
                getOnItemSelectedListener().onSelected(position, pkgName);
            }
        });
        singleChooseView.setChecked(position == selectedPos);
        singleChooseView.setTitle(PackageUtil.getAppName(getContext(), pkgName));
        singleChooseView.setIcon(PackageUtil.getAppIcon(getContext(), pkgName));
        singleChooseView.setSubtitle(pkgName);
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public interface OnItemSelectedListener {
        void onSelected(int position, String pkgName);
    }
}

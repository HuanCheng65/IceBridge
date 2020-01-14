package com.huanchengfly.icebridge.fragments.intro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.adapters.SingleChooseAdapter;
import com.huanchengfly.icebridge.utils.OSUtils;
import com.huanchengfly.icebridge.utils.PackageUtil;
import com.stericson.RootTools.RootTools;

import static com.huanchengfly.icebridge.adapters.SingleChooseAdapter.ItemBean;

import java.util.ArrayList;
import java.util.List;

public class WorkModeFragment extends BaseIntroFragment {
    private SingleChooseAdapter singleChooseAdapter;

    public WorkModeFragment() {}

    @Override
    public boolean getDefaultNextButtonEnabled() {
        return false;
    }

    @Override
    protected int getCustomLayoutResId() {
        return R.layout.layout_work_mode;
    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void initCustomLayout(LinearLayout container) {
        super.initCustomLayout(container);
        RecyclerView recyclerView = container.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getAttachContext()));
        if (recyclerView.getItemAnimator() instanceof SimpleItemAnimator) ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        List<ItemBean> itemBeans = new ArrayList<>();
        itemBeans.add(getIceBoxItemBean());
        itemBeans.add(getRootItemBean());
        SharedPreferences.Editor editor = getAttachContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        singleChooseAdapter = new SingleChooseAdapter(getAttachContext(), itemBeans);
        singleChooseAdapter.setOnItemSelectedListener((position, itemBean) -> {
            switch (position) {
                case 0:
                    setNextButtonEnabled(true);
                    editor.putInt(SP_WORK_MODE, MODE_ICEBOX).commit();
                    break;
                case 1:
                    if (RootTools.isRootAvailable() && RootTools.isAccessGiven()) {
                        Toast.makeText(getAttachContext(), R.string.got_root_permission, Toast.LENGTH_SHORT).show();
                    }
                    setNextButtonEnabled(true);
                    editor.putInt(SP_WORK_MODE, MODE_ROOT).commit();
                    break;
            }
        });
        recyclerView.setAdapter(singleChooseAdapter);
    }

    private ItemBean getIceBoxItemBean() {
        ItemBean itemBean = new ItemBean(getAttachContext(), R.string.title_work_mode_ice_box, R.string.subtitle_work_mode_ice_box_not_available, false);
        if (PackageUtil.checkAppInstalled(getAttachContext(), IceBox.PACKAGE_NAME) && PackageUtil.getVersionCode(getAttachContext(), IceBox.PACKAGE_NAME) >= IceBox.AVAILABLE_VERSION_CODE) {
            itemBean.setEnabled(true);
            if (OSUtils.isMIUI11OrLater()) {
                itemBean.setSubtitle(getAttachContext(), R.string.subtitle_work_mode_ice_box_is_miui);
            } else {
                itemBean.setSubtitle(getAttachContext(), R.string.subtitle_work_mode_ice_box);
            }
        }
        return itemBean;
    }

    private ItemBean getRootItemBean() {
        ItemBean itemBean = new ItemBean(getAttachContext(), R.string.title_work_mode_root, R.string.subtitle_work_mode_root);
        if (!RootTools.isRootAvailable()) {
            itemBean.setEnabled(false);
            itemBean.setSubtitle(getAttachContext(), R.string.device_no_root);
        }
        return itemBean;
    }

    @Nullable
    @Override
    CharSequence getTitle() {
        return getAttachContext().getString(R.string.title_intro_work_mode);
    }

    @Nullable
    @Override
    CharSequence getSubtitle() {
        return getAttachContext().getString(R.string.subtitle_intro_work_mode);
    }

    public static final String SP_WORK_MODE = "work_mode";
    public static final int MODE_ICEBOX = 0;
    public static final int MODE_ROOT = 1;
}

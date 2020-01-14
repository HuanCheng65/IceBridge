package com.huanchengfly.icebridge.utils;

import android.content.Context;

import com.huanchengfly.icebridge.beans.BridgeInfo;
import com.huanchengfly.icebridge.databases.AppConfig;

import org.litepal.LitePal;

public final class BridgeUtil {
    public static final String SP_BRIDGE_CONFIG = "bridge_config";
    public static final String ASSET_BRIDGE_JSON = "bridge.json";
    public static final String SP_VERSION = "version";
    public static final String SP_CONFIG = "config";

    public static final String TAG = "BridgeUtil";

    private BridgeUtil() {
    }

    public static String getVersion(Context context) {
        try {
            return String.valueOf(getBridgeInfo(context).getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(parse(AssetUtil.getStringFromAsset(context, ASSET_BRIDGE_JSON)).getVersion());
    }

    public static int getVersionCode(Context context) {
        try {
            return getBridgeInfo(context).getVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parse(AssetUtil.getStringFromAsset(context, ASSET_BRIDGE_JSON)).getVersion();
    }

    public static String getVersionName(Context context) {
        return "v" + getVersion(context);
    }

    public static BridgeInfo getBridgeInfo(Context context) {
        return parse(getJSON(context));
    }

    public static String getJSON(Context context) {
        AppConfig appConfig = LitePal.where("key = ?", SP_CONFIG).findFirst(AppConfig.class);
        if (appConfig == null) {
            return AssetUtil.getStringFromAsset(context, ASSET_BRIDGE_JSON);
        }
        return appConfig.getValue();
    }

    public static BridgeInfo parse(String json) {
        return GsonUtil.getGson().fromJson(json, BridgeInfo.class);
    }

    public static void init(Context context) {
        BridgeInfo bridgeInfo = parse(AssetUtil.getStringFromAsset(context, ASSET_BRIDGE_JSON));
        int nowVersion = context.getSharedPreferences(SP_BRIDGE_CONFIG, Context.MODE_PRIVATE)
                .getInt(SP_VERSION, 0);
        int newVersion = bridgeInfo.getVersion();
        if (newVersion > nowVersion) {
            reset(context);
        }
    }

    public static void reset(Context context) {
        BridgeInfo bridgeInfo = parse(AssetUtil.getStringFromAsset(context, ASSET_BRIDGE_JSON));
        write(context, bridgeInfo);
    }

    public static void write(Context context, BridgeInfo bridgeInfo) {
        context.getSharedPreferences(SP_BRIDGE_CONFIG, Context.MODE_PRIVATE)
                .edit()
                .putInt(SP_VERSION, bridgeInfo.getVersion())
                .apply();
        new AppConfig(SP_CONFIG, bridgeInfo.toString()).saveOrUpdate("key = ?", SP_CONFIG);
    }
}

package com.huanchengfly.icebridge.beans;

import com.google.gson.annotations.SerializedName;

public class UpdateInfo {
    @SerializedName("config_version")
    private int configVersion;
    @SerializedName("config_version_name")
    private String configVersionName;
    @SerializedName("config_download_url")
    private String configDownloadUrl;
    @SerializedName("app_version")
    private int appVersion;
    @SerializedName("app_version_name")
    private String appVersionName;
    @SerializedName("app_download_url")
    private String appDownloadUrl;

    public int getConfigVersion() {
        return configVersion;
    }

    public String getConfigVersionName() {
        return configVersionName;
    }

    public String getConfigDownloadUrl() {
        return configDownloadUrl;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public String getAppDownloadUrl() {
        return appDownloadUrl;
    }
}

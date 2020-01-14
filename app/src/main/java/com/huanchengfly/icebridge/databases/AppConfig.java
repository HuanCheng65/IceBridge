package com.huanchengfly.icebridge.databases;

import org.litepal.crud.LitePalSupport;

public class AppConfig extends LitePalSupport {
    private String key;
    private String value;

    public AppConfig(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

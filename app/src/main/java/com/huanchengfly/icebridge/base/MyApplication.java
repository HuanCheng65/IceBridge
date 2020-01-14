package com.huanchengfly.icebridge.base;

import org.litepal.LitePalApplication;

public class MyApplication extends LitePalApplication {
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}

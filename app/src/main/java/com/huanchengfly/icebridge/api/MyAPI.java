package com.huanchengfly.icebridge.api;

import com.huanchengfly.icebridge.beans.BridgeInfo;
import com.huanchengfly.icebridge.beans.UpdateInfo;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.GsonResponseHandler;

import okhttp3.OkHttpClient;

public final class MyAPI {
    private static MyAPI instance;
    private MyOkHttp myOkHttp;

    private MyAPI() {
        this.myOkHttp = new MyOkHttp(new OkHttpClient.Builder()
                .build());
    }

    public static MyAPI getInstance() {
        if (instance == null) {
            synchronized (MyAPI.class) {
                if (instance == null) {
                    instance = new MyAPI();
                }
            }
        }
        return instance;
    }

    public void checkUpdate(CommonAPICallback<UpdateInfo> commonAPICallback) {
        myOkHttp.get()
                .url(HttpConstant.URL_UPDATE)
                .enqueue(new GsonResponseHandler<UpdateInfo>() {
                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        commonAPICallback.onFailure(statusCode, error_msg);
                    }

                    @Override
                    public void onSuccess(int statusCode, UpdateInfo response) {
                        commonAPICallback.onSuccess(response);
                    }
                });
    }

    public void updateConfig(CommonAPICallback<BridgeInfo> commonAPICallback) {
        myOkHttp.get()
                .url(HttpConstant.URL_CONFIG)
                .enqueue(new GsonResponseHandler<BridgeInfo>() {
                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        commonAPICallback.onFailure(statusCode, error_msg);
                    }

                    @Override
                    public void onSuccess(int statusCode, BridgeInfo response) {
                        commonAPICallback.onSuccess(response);
                    }
                });
    }
}

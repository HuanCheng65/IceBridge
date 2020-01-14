package com.huanchengfly.icebridge.api;

public interface CommonAPICallback<T> {
    void onSuccess(T t);

    void onFailure(int errorCode, String errorMsg);
}
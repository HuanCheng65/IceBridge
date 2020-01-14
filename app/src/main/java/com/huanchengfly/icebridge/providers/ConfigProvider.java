package com.huanchengfly.icebridge.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huanchengfly.icebridge.databases.AppConfig;

import org.litepal.LitePal;

import java.util.Map;

public class ConfigProvider extends ContentProvider {
    public static final String TAG = "ConfigProvider";

    private static final String AUTHORITY = "com.huanchengfly.icebridge.providers.ConfigProvider";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/config");
    private static final int MATCH_CODE = 200;
    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "config", MATCH_CODE);
    }

    private void notifyChange() {
        getContext().getContentResolver().notifyChange(URI, null);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = uriMatcher.match(uri);
        if (match == MATCH_CODE) {
            Log.i(TAG, "query");
            try {
                return LitePal.findBySQL("select * from AppConfig");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) == MATCH_CODE && values != null) {
            Log.i(TAG, "insert");
            for (Map.Entry<String, Object> entry : values.valueSet()) {
                new AppConfig(entry.getKey(), (String) entry.getValue()).saveOrUpdate("key = ?", entry.getKey());
            }
            notifyChange();
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

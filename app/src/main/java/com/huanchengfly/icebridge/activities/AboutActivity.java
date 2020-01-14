package com.huanchengfly.icebridge.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.huanchengfly.about.AboutPage;
import com.huanchengfly.icebridge.R;
import com.huanchengfly.icebridge.utils.PackageUtil;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MaterialToolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_about);
        }
        int colorIcon = getResources().getColor(R.color.colorAccent);
        View view = new AboutPage(this)
                .addTitle(getString(R.string.title_developer), colorIcon)
                .addItem(new AboutPage.Item("@幻了个城fly", "开发者")
                        .setIcon(R.drawable.avatar_huanchengfly, false)
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("coolmarket://u/603089"))
                                .setPackage("com.coolapk.market")))
                .addItem(new AboutPage.Item("@不看私信和at的年轻开发者", "图标绘制")
                        .setIcon(R.drawable.avatar_icebox, false)
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("coolmarket://u/184454"))
                                .setPackage("com.coolapk.market")))
                .addTitle(getString(R.string.title_app_info), colorIcon)
                .addItem(new AboutPage.Item("当前版本", PackageUtil.getVersionName(this), R.drawable.ic_round_info, colorIcon))
                .addItem(new AboutPage.Item("去酷安查看", "检查更新、评论或反馈", R.drawable.ic_round_local_mall, colorIcon)
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.huanchengfly.icebridge"))
                                .setPackage("com.coolapk.market")))
                .addTitle(getString(R.string.title_support_me), colorIcon)
                .addItem(new AboutPage.Item("支付宝捐赠")
                        .setIcon(R.drawable.ic_alipay, colorIcon)
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://qr.alipay.com/FKX06385UK8W8T8X2MG827"))))
                .addItem(new AboutPage.Item("支付宝领红包")
                        .setIcon(R.drawable.ic_archive, colorIcon)
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://qr.alipay.com/c1x06336wvvmfwjwlzbq4a5"))))
                .addTitle(getString(R.string.title_license), colorIcon)
                .addItem(new AboutPage.Item("heruoxin/IceBox-SDK")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/heruoxin/IceBox-SDK"))))
                .addItem(new AboutPage.Item("square/okhttp", "An HTTP client for Android, Kotlin, and Java.")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/square/okhttp"))))
                .addItem(new AboutPage.Item("google/gson", "A Java serialization/deserialization library to convert Java Objects into JSON and back")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/google/gson"))))
                .addItem(new AboutPage.Item("bumptech/glide", "An image loading and caching library for Android focused on smooth scrolling")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/bumptech/glide"))))
                .addItem(new AboutPage.Item("tsy12321/MyOkHttp", "对Okhttp3进行二次封装,对外提供了POST请求、GET请求、PATCH请求、PUT请求、DELETE请求、上传文件、下载文件、取消请求、Raw/Json/Gson返回、后台下载管理等功能")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tsy12321/MyOkHttp"))))
                .addItem(new AboutPage.Item("LitePalFramework/LitePal", "An Android library that makes developers use SQLite database extremely easy.")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/LitePalFramework/LitePal"))))
                .addItem(new AboutPage.Item("yanzhenjie/AndPermission", "\uD83C\uDF53 Permissions manager for Android platform.")
                        .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yanzhenjie/AndPermission"))))
                .into(findViewById(R.id.main));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }
}

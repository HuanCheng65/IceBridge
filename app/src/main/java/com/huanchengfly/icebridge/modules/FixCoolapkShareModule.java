package com.huanchengfly.icebridge.modules;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FixCoolapkShareModule implements IXposedHookLoadPackage {
    public static final String PACKAGE_COOLAPK = "com.coolapk.market";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (PACKAGE_COOLAPK.equals(loadPackageParam.packageName)) {
            XposedHelpers.findAndHookMethod("com.coolapk.market.util.PackageUtils", loadPackageParam.classLoader, "getShareApps", Context.class, new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    Context context = (Context) methodHookParam.args[0];
                    Intent intent = new Intent(Intent.ACTION_SEND, null)
                            .addCategory(Intent.CATEGORY_DEFAULT)
                            .setType("text/plain");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL | PackageManager.MATCH_DISABLED_COMPONENTS);
                    } else {
                        return context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_DISABLED_COMPONENTS);
                    }
                }
            });
        }
    }
}

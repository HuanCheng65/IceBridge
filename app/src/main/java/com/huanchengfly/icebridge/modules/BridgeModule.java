package com.huanchengfly.icebridge.modules;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.catchingnow.icebox.sdk_client.IceBox;
import com.huanchengfly.icebridge.BuildConfig;
import com.huanchengfly.icebridge.activities.XposedBridgeActivity;
import com.huanchengfly.icebridge.beans.BridgeInfo;
import com.huanchengfly.icebridge.utils.BridgeUtil;
import com.huanchengfly.icebridge.utils.PackageUtil;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BridgeModule implements IXposedHookLoadPackage {
    public static final String TAG = "BridgeModule";
    public static final String PACKAGE_NAME = "com.huanchengfly.icebridge";

    public boolean isMatch(String s, String p) {
        int sl = s.length();
        int pl = p.length();
        int i = 0, j = 0;
        int marks = -1, markp = -1;
        while (i < sl) {
            if (j != pl && (s.charAt(i) == p.charAt(j) || p.charAt(j) == '?')) {
                i++;
                j++;
            } else if (j != pl && p.charAt(j) == '*') {
                marks = i;
                markp = j;
                j++;
            } else if (markp != -1) {
                i = marks + 1;
                j = markp + 1;
                marks++;
            } else {
                return false;
            }
        }
        while (j < pl) {
            if (p.charAt(j) == '*')
                j++;
            else
                return false;
        }
        return true;
    }

    private String queryConfig(Context context, String key, String defValue) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.huanchengfly.icebridge.providers.ConfigProvider/config"), null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            try {
                while (cursor.moveToNext()) {
                    String configKey = cursor.getString(cursor.getColumnIndex("key"));
                    String value = cursor.getString(cursor.getColumnIndex("value"));
                    if (TextUtils.equals(key, configKey)) {
                        return value;
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return defValue;
    }

    private void hook(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        String json = queryConfig(context, BridgeUtil.SP_CONFIG, null);
        BridgeInfo bridgeInfo = BridgeUtil.parse(json);
        if (bridgeInfo == null) return;
        log("IceBridge: start hook");
        for (BridgeInfo.Hook hook : bridgeInfo.getHooks()) {
            hook(context, hook, loadPackageParam);
        }
    }

    private void log (String message) {
        if (BuildConfig.DEBUG) {
            XposedBridge.log(message);
        }
    }

    private void hook(Context context, BridgeInfo.Hook hook, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        for (String targetPackage : hook.getTargetPackages()) {
            if (!isMatch(loadPackageParam.packageName, targetPackage)) {
                continue;
            }
            log("IceBridge: start hook" + loadPackageParam.packageName);
            hook(context, hook.getTargetMethods(), getPackage(context, hook.getUnfreezePackages()), loadPackageParam);
        }
    }

    private String getPackage(Context context, List<String> packages) {
        String packageName = null;
        for (String pkg : packages) {
            if (PackageUtil.checkAppInstalled(context, pkg)) {
                packageName = pkg;
                break;
            }
        }
        return packageName;
    }

    private void hook(Context context, List<BridgeInfo.TargetMethod> targetMethods, String unfreezePackage, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (unfreezePackage == null) return;
        for (BridgeInfo.TargetMethod targetMethod : targetMethods) {
            log("IceBridge: start hook" + loadPackageParam.packageName + " class: " + targetMethod.getClassName());
            Class<?> clazz = XposedHelpers.findClassIfExists(targetMethod.getClassName(), loadPackageParam.classLoader);
            if (clazz == null) continue;
            log("IceBridge: start hook" + loadPackageParam.packageName + " class: " + targetMethod.getClassName() + " method: " + targetMethod.getMethodName());
            XposedHelpers.findAndHookMethod(clazz, targetMethod.getMethodName(), getParamObjectsAndHook(context, targetMethod, unfreezePackage, loadPackageParam.classLoader));
        }
    }

    private Class<?> getClass(String className, ClassLoader classLoader) {
        switch (className) {
            case "boolean":
                return boolean.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "String":
                return String.class;
            case "Activity":
                return Activity.class;
            case "androidx.fragment.app.Fragment":
                Class fragmentClazz = XposedHelpers.findClassIfExists(className, classLoader);
                if (fragmentClazz != null) {
                    return fragmentClazz;
                }
                return XposedHelpers.findClass("android.support.v4.app.Fragment", classLoader);
            default:
                return XposedHelpers.findClass(className, classLoader);
        }
    }

    private Object[] getParamObjectsAndHook(Context context, BridgeInfo.TargetMethod targetMethod, String unfreezePackage, ClassLoader classLoader) {
        List<Object> objects = new ArrayList<>();
        for (String className : targetMethod.getParamsClasses()) {
            objects.add(getClass(className, classLoader));
        }
        objects.add(new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                if (IceBox.getAppEnabledSetting(context, unfreezePackage) == 0) {
                    return XposedBridge.invokeOriginalMethod(methodHookParam.method, methodHookParam.thisObject, methodHookParam.args);
                }
                context.startActivity(new Intent(XposedBridgeActivity.INTENT_ACTION)
                        .putExtra(XposedBridgeActivity.EXTRA_PACKAGE, unfreezePackage)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return targetMethod.getReturnValue().get();
            }
        });
        return objects.toArray();
    }

    private boolean shouldHook(String pkg) {
        switch (pkg) {
            case "org.meowcat.edxposed.manager":
            case "de.robv.android.xposed.installer":
            case "com.solohsu.android.edxp.manager":
            case "com.android.systemui":
                return false;
            default:
                return true;
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (PACKAGE_NAME.equals(loadPackageParam.packageName)) {
            Class<?> clazz = XposedHelpers.findClassIfExists("com.huanchengfly.icebridge.utils.Util", loadPackageParam.classLoader);
            if (clazz == null) return;
            XposedHelpers.findAndHookMethod(clazz, "isXposedActive", XC_MethodReplacement.returnConstant(true));
        } else if (shouldHook(loadPackageParam.packageName)) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Context context = (Context) param.args[0];
                    boolean enhanceModeEnabled = "1".equals(queryConfig(context, "enhance_mode_enabled", "1"));
                    if (!enhanceModeEnabled) {
                        return;
                    }
                    try {
                        hook(context, loadPackageParam);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

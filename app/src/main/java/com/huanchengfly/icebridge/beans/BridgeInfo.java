package com.huanchengfly.icebridge.beans;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.huanchengfly.icebridge.utils.GsonUtil;

import java.util.List;

public class BridgeInfo {
    private int version;
    private List<Hook> hooks;

    @NonNull
    @Override
    public String toString() {
        return GsonUtil.getGson().toJson(this);
    }

    public List<Hook> getHooks() {
        return hooks;
    }

    public int getVersion() {
        return version;
    }

    public static class Hook {
        @SerializedName("unfreeze_packages")
        private List<String> unfreezePackages;
        @SerializedName("target_packages")
        private List<String> targetPackages;
        @SerializedName("target_methods")
        private List<TargetMethod> targetMethods;

        public List<String> getUnfreezePackages() {
            return unfreezePackages;
        }

        public List<String> getTargetPackages() {
            return targetPackages;
        }

        public List<TargetMethod> getTargetMethods() {
            return targetMethods;
        }
    }

    public static class TargetMethod {
        @SerializedName("class_name")
        private String className;
        @SerializedName("method_name")
        private String methodName;
        @SerializedName("params_classes")
        private List<String> paramsClasses;
        @SerializedName("return_value")
        private ReturnValue returnValue;

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public List<String> getParamsClasses() {
            return paramsClasses;
        }

        public ReturnValue getReturnValue() {
            return returnValue;
        }
    }

    public static class ReturnValue {
        private String type;
        private String value;

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public Object get() {
            switch (getType()) {
                case "boolean":
                    return Boolean.valueOf(getValue());
                case "int":
                    return Integer.valueOf(getValue());
                case "String":
                default:
                    return getValue();
            }
        }
    }
}

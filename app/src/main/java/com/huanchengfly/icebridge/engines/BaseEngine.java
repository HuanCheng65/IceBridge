package com.huanchengfly.icebridge.engines;

import android.content.Context;

import androidx.annotation.IntDef;

import java.lang.ref.WeakReference;

public abstract class BaseEngine {
     public static final int STATUS_NOT_AVAILABLE = 0;
     public static final int STATUS_NEED_PERMISSION = 1;
     public static final int STATUS_WORKING = 2;

     @IntDef({STATUS_NOT_AVAILABLE, STATUS_NEED_PERMISSION, STATUS_WORKING})
     @interface Status {}

     private WeakReference<Context> reference;

     public BaseEngine(Context context) {
          reference = new WeakReference<>(context);
     }

     public final Context getContext() {
          return reference.get();
     }

     public abstract void setEnabled(String pkgName, boolean enabled, Callback callback);

     public abstract @Status int getEngineStatus();

     public abstract CharSequence getStatusDescription();

     public abstract int checkPermission();

     public abstract void requestPermission(Callback callback);

     protected void notifyStatusChanged() {
          if (getContext() instanceof OnStatusChangedListener) {
               ((OnStatusChangedListener) getContext()).onStatusChanged(getEngineStatus());
          }
     }

     public interface OnStatusChangedListener {
          void onStatusChanged(int status);
     }

     public interface Callback {
          void onSuccess();

          void onFailure();
     }
}

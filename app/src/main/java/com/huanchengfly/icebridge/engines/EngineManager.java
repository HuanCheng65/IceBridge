package com.huanchengfly.icebridge.engines;

import android.content.Context;

import static com.huanchengfly.icebridge.fragments.intro.WorkModeFragment.SP_WORK_MODE;

public final class EngineManager {
    public static BaseEngine getEngine(Context context) {
        if (context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt(SP_WORK_MODE, 0) == 1) {
            return new RootEngine(context);
        }
        return new IceBoxEngine(context);
    }
}

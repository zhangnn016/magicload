package com.magic.load.magicloader.utils;

import android.util.Log;

/**
 * Created by niuniuzhang on 20,一月,2017.
 */

public class LogUtil {
    private static final boolean isDebug = true;

    public static final void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static final void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static final void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    public static final void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }
}

package com.yunos.tv.core.common;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yunos.tv.core.config.Config;

/**
 * @author hanqi
 */
public class AppDebug {

    public static void i(String tag, String msg) {
        if (Config.isDebug()) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Config.isDebug()) {
            Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Config.isDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Config.isDebug()) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable e) {
        if (Config.isDebug()) {
            Log.v(tag, msg, e);
        }
    }

    public static void w(String tag, String msg) {
        if (Config.isDebug()) {
            Log.w(tag, msg);
        }
    }

    public static void showToast(Context context, String title) {
        if (Config.isDebug())
            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
    }
}

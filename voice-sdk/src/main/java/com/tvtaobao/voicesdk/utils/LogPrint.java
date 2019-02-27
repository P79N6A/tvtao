package com.tvtaobao.voicesdk.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yunos.tv.core.config.Config;

/**
 * Created by pan on 2017/9/18.
 */

public class LogPrint {
    private static String TAG_TVTAO = "TVTao_";
    public static void i(String tag, String msg) {
        if (Config.isDebug()) {
            Log.i(TAG_TVTAO + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Config.isDebug()) {
            Log.e(TAG_TVTAO + tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Config.isDebug()) {
            Log.d(TAG_TVTAO + tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Config.isDebug()) {
            Log.v(TAG_TVTAO + tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable e) {
        if (Config.isDebug()) {
            Log.v(TAG_TVTAO + tag, msg, e);
        }
    }

    public static void w(String tag, String msg) {
        if (Config.isDebug()) {
            Log.w(TAG_TVTAO + tag, msg);
        }
    }

    public static void showToast(Context context, String title) {
        if (Config.isDebug())
            Toast.makeText(context, title, Toast.LENGTH_LONG).show();
    }
}

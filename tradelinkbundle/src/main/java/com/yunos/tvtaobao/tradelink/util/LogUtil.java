package com.yunos.tvtaobao.tradelink.util;

import android.util.Log;

/**
 * Created by chenjiajuan on 17/7/25.
 */

public class LogUtil {
    private static boolean isPrintLog = true;
    private static int LOG_MAXLENGTH = 2000;

    public static void e(String msg) {
        e("LogUtil", msg);
    }

    public static void e(String tagName, String msg) {
        if (isPrintLog) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.e(tagName + i, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.e(tagName + i, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }
}

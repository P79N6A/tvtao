package com.yunos.tvtaobao.tvlive.utils;

import android.content.Context;

/**
 * Created by libin on 16/9/21.
 */

public class Tools {

    public static int[] color = {0xff009999, 0xff009900, 0xff0066cc, 0xffcc00cc, 0xffcc3300};

    public static int compatiblePx(Context context, int defaultPx) {
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        if (widthPixels == 1280) {
            return defaultPx;
        } else {
            float scale = widthPixels / 1280f;
            return (int) (defaultPx * scale + 0.5f);
        }
    }
}

package com.yunos.tvtaobao.payment.utils;


import android.content.Context;
import android.util.DisplayMetrics;

public class UtilsDistance {


    /**
     * dp 2 px
     */
    public static int dp2px(Context context, int dpVal) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                dpVal, context.getResources().getDisplayMetrics());
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (dpVal * (dm.heightPixels / 720.0f));
    }

}

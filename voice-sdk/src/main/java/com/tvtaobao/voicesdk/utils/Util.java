package com.tvtaobao.voicesdk.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by pan on 2017/10/8.
 */

public class Util {

    /**
     * 不同的屏对应不对的DP 比小米1 = 1.33 小米2 ,3  = 2
     */
    public static int adjust_dp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        if(scale == (float) 2.0)  //小米 2，3  sw540的屏
        {
            return (int) (dpValue * 1.5 / scale);}
        else{
            return  (int) dpValue;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.d("jiangyg", "px2dip: scale:   " + scale);
        if(scale == (float) 1.3312501) {  //小米1的屏幕密度 ，小米1 dp转px 有 bug
            return (int) dpValue;
        }
        if(scale == (float) 2.0) //如果是小米 2 ,3 要调整dp  * 0.75
        {
            return (int) (adjust_dp(context,dpValue) * scale + 0.5f);
        } else {
            return (int) (dpValue * scale + 0.5f);
        }
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.d("jiangyg", "px2dip: scale:   " + scale);
        return (int) (pxValue / scale + 0.5f);
    }

    public static int compatiblePx(Context context, int defaultPx) {
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        Log.d("jiangyg", "compatiblePx: widthPixels:   " + widthPixels);
        if (widthPixels == 1280) {
            return defaultPx;
        } else {
            float scale = widthPixels / 1280f;
            return (int) (defaultPx * scale + 0.5f);
        }
    }


}

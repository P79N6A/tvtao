/**
 * $
 * PROJECT NAME: zhuanti
 * PACKAGE NAME: com.yunos.tv.zhuanti.activity.huabao
 * FILE NAME: DepthPageTransformer.java
 * CREATED TIME: 2014年8月29日
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.common;


import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2014年8月29日 下午3:41:55
 */
public class DepthPageTransformer implements PageTransformer {

    //    private String TAG = "DepthPageTransformer";

    @SuppressLint("NewApi")
    @Override
    public void transformPage(View view, float position) {

        float alpha = 0f;
        if (position < -1) { // [-Infinity,-1)This page is way off-screen to
                             // the left.
            alpha = 0f;
        } else if (position <= 0) { // [-1,0]
                                    // Use the default slide transition when
                                    // moving to the left page
            alpha = position + 1;
        } else if (position <= 1) { // (0,1]
                                    // Fade the page out.
            alpha = 1 - position;
        } else { // (1,+Infinity]
            alpha = 0f;
        }

        //        AppDebug.v(
        //                TAG,
        //                TAG + ".transformPage position = " + position + ",alpha = " + alpha + ",view.alpha = "
        //                        + view.getAlpha() + ", view = " + view.getTag());
        view.setAlpha(alpha * alpha);
    }
}

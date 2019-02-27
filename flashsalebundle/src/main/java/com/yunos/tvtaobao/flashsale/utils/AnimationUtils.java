
package com.yunos.tvtaobao.flashsale.utils;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.TextView;

import com.yunos.tv.core.CoreApplication;


public class AnimationUtils {

    public static Typeface TYPEFACE_FZLTXH, TYPEFACE_FZLTH;


    /**
     * 设置中文字体变粗
     * 
     * @param textView
     */
    public static void setFakeBold(TextView textView) {
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(true);
    }

    public static void setXHTypeface() {
        TYPEFACE_FZLTXH = Typeface.createFromAsset(CoreApplication.getApplication().getAssets(),
        		"fonts/FZLTXH.TTF");
    }

    public static Typeface getXHTypeface() {
        return TYPEFACE_FZLTXH;
    }


}

/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.util;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;


/**
 * @author Administrator
 *
 */
public final class MeasureTextUtil {
    
    
    /**
     * 获取字符串的宽度
     * @param text     字符串内容
     * @param textsize  字体大小
     * @return   返回宽度
     */
    public static float  getTextSizeWidth(String text, float textsize)
    {
        float width = 0;
        Paint mPaint  = new Paint();
        mPaint.setTextSize(textsize);
        width = mPaint.measureText(text); 
        mPaint = null; 
        return width; 
    }
    
    
    /**
     * 获取字符串的高度
     * @param text          字符串内容
     * @param textsize      字体大小
     * @return   返回高度
     */
    public static float getTextSizeHeight(String text, float textsize)
    {
        float height = 0; 
        Paint mPaint  = new Paint();
        mPaint.setTextSize(textsize); 
        FontMetrics fm = mPaint.getFontMetrics();
        height = (float) Math.ceil(fm.descent - fm.ascent);
        mPaint = null; 
        return height; 
    }
    
    
    
    /***
     * 
     * 计算当前的字符串内容是否能够在当前的宽度下显示，
     * 如果不能完全显示，则不能显示部分用  replaceStr 字符串替换
     * 
     * @param str             字符串内容 
     * @param textsize        字体大小 
     * @param replaceStr      替换的字符串
     * @param needWidth       当前的宽度
     * @return
     */
    public  static String onCalulatStringLen(String str, int textsize, String replaceStr, int needWidth)
    {
        
        String newStr = str;
        String newStrOld = "";
        
         
         
        Paint mPaint  = new Paint();
        mPaint.setTextSize(textsize); 
        
        float  textWight_goodname =  mPaint.measureText(str);
        float  biaojiLen = mPaint.measureText(replaceStr);
        
        if(textWight_goodname >=  needWidth)
        {
            int  len = newStr.length();
            if(len > 20)
            {
                len = 20;
            } 
            needWidth -= biaojiLen;
            
            while(textWight_goodname >  needWidth)
            {
                newStrOld =  str.substring(0, len);
                textWight_goodname =  mPaint.measureText(newStrOld);
                len--;
            }
            
            newStr = newStrOld + replaceStr; 
        } 
        
        mPaint = null;
        return newStr;
    }

}

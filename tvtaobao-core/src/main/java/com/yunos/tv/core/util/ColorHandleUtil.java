/**
 * 
 */
package com.yunos.tv.core.util;

import android.graphics.Color;


/**
 * @author yunzhong.qyz
 *
 */
public class ColorHandleUtil {
    
    /**
     * 
     * @param color
     * @param transparency
     * @return
     */
    public static  int  ColorTransparency(int color,  float transparency)
    {
       int mColor = 0; 
       if((transparency < 0.0) || (transparency > 100.0))
       {
           throw new IllegalArgumentException("transparency >= 0.0  and  transparency <= 100.0");
       }
       
       int  alpha  = (int) (255.0 * transparency); 
       mColor = Color.argb(alpha, color, color, color);
               
       return mColor;
       
    }
    
    
    
    /**
     * 
     * @param color
     * @param alpha
     * @return
     */
    public static  int  ColorTransparency(int color,  int alpha)
    {
       int mColor = 0;  
       mColor = Color.argb(alpha, color, color, color); 
       return mColor;
       
    }

}

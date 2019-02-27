package com.tvtaobao.voicesdk.utils;

import java.text.DecimalFormat;

/**
 * Created by xutingting on 2017/11/2.
 */

public class PriceUtil {
    static DecimalFormat df = new DecimalFormat("0.##");//格式化小数
    public static String getPrice(int price){
        float num= (float)price/100;
        String s = df.format(num);//返回的是String类型
        return s;
    }


}

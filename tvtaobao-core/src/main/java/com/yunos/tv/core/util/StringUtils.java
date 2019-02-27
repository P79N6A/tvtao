package com.yunos.tv.core.util;

/**
 * Created by chenjiajuan on 17/12/26.
 *
 * @describe 文本工具类
 */

public class StringUtils {

    /**
     * 去0
     * @param price
     * @return
     */
    public  static String resolvePrice(String price) {
        String text = price;
        String[] s = price.split("\\.");
        if (s.length == 2) {
            price = s[1];
            if (price.matches("[1-9]0")) {
                //30.20 ->30.2
                price = text.substring(0, text.indexOf(".") + 2);
            } else if (price.equals("00")) {
                //30.00 ->30
                price = s[0];
            } else if (price.equals("0")){
                //30.0->30
                price = s[0];
            }else {
                price=text;
            }
        }
        return price;

    }
}

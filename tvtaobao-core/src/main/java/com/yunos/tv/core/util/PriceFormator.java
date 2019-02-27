/**
 * 
 */
package com.yunos.tv.core.util;


import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * 价格的格式化工具,应用里的价格显示多需要特殊处理
 * @author tianxiang
 * @date 2012-11-1 上午9:58:21
 */
public class PriceFormator {

    /**
     * 格式化long型价格
     * @date 2012-11-19下午3:12:53
     * @param price
     * @return
     */
    private static String formatLong(Long price) {
        if (price == null) {
            return "";
        }
        long mod = price % 100;
        StringBuilder sb = new StringBuilder();
        sb.append("&#165;");
        sb.append(price / 100);
        sb.append(".");
        if (mod < 10) {
            sb.append("0");
        }
        sb.append(mod);

        return sb.toString();
    }

    /**
     * 格式化无货币符号的long型
     * @date 2012-11-19下午3:13:43
     * @param price
     * @return
     */
    public static String formatNoSymbolLong(Long price) {
        if (price == null) {
            return "";
        }
        long mod = price % 100;
        StringBuilder sb = new StringBuilder();
        sb.append(price / 100);
        sb.append(".");
        if (mod < 10) {
            sb.append("0");
        }
        sb.append(mod);

        return sb.toString();
    }

    /**
     * 格式化double类型
     * @param price
     * @param withSymbol
     * @return
     */
    public static String formatDoublePrice(double price, boolean withSymbol) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        StringBuilder sb = new StringBuilder(nf.format(price));
        int index = sb.lastIndexOf(".");
        if (index != -1) {
            int len = 2 - (sb.length() - index - 1);
            for (int i = 1; i <= len; i++) {
                sb.append("0");
            }
        } else {
            sb.append(".00");
        }
        if (withSymbol) {
            sb.insert(0, "¥ ");
        }

        return sb.toString();
    }

    /**
     * 格式化为小图显示模式,具体显示样式可见商品列表的小图模式
     * @date 2012-11-1上午10:00:15
     * @param price
     * @return
     */
    public static SpannableString formatSmallPrice(Long price, String... strings) {
        if (price == null) {
            return new SpannableString("");
        }
        String postFee = "";
        // strings不会为空。下面的if徒劳无功
        if (strings != null && strings.length > 0) {
            if (strings[0] != null) {
                postFee = strings[0];
            }
        }
        String str = formatLong(price) + postFee;
        int index3 = str.length();
        int index2 = str.indexOf(".");
        if (index2 < 0) {
            return new SpannableString(str);
        }

        int color = Color.argb(255, 195, 34, 61);
        SpannableString ss = new SpannableString(str);
        // 货币符号
        ss.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(12, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 整数
        ss.setSpan(new ForegroundColorSpan(color), 1, index2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(18, true), 1, index2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 小数
        ss.setSpan(new ForegroundColorSpan(color), index2, index3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(12, true), index2, index3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /**
     * 格式化为大图显示模式,具体显示样式可见商品列表的大图模式
     * @date 2012-11-1上午10:00:15
     * @param price
     * @return
     */
    public static SpannableString formatBigPrice(Long price) {
        String str = formatLong(price);
        int index3 = str.length();
        int index2 = str.lastIndexOf(".");
        // null 
        if (index2 < 0) {
            return new SpannableString(str);
        }
        int color = Color.WHITE;
        int bigFontSize = 18;
        int smallFontSize = 12;
        if (str.length() >= 8) {
            bigFontSize = 16;
            smallFontSize = 10;
        }
        SpannableString ss = new SpannableString(str);
        // 货币符号
        ss.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(smallFontSize, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 整数
        ss.setSpan(new ForegroundColorSpan(color), 1, index2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(bigFontSize, true), 1, index2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 小数
        ss.setSpan(new ForegroundColorSpan(color), index2, index3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(smallFontSize, true), index2, index3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    /**
     * 格式化原始价格
     * @date 2012-10-30下午3:11:05
     * @param text
     * @param originalPrice
     */
    public static void formatOriginalPrice(TextView text, Long originalPrice) {
        text.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        text.getPaint().setAntiAlias(true);// 抗锯齿
        String price = String.valueOf(originalPrice);
        if (price.length() > 2) {
            price = price.substring(0, price.length() - 2) + "." + price.substring(price.length() - 2);
        }

        text.setText(price);

    }

    /**
     * 格式化为小图显示模式价格+运费,具体显示样式可见商品列表的小图模式
     * @date 2012-11-1上午10:00:15
     * @param price
     * @return
     */
    public static SpannableString formatSmallTranPrice(Long price, int quantity, Long transportCharges) {

        if (transportCharges == null) {
            transportCharges = 0L;
        }
        if (price == null) {
            price = 0L;
        }

        return formatSmallPrice(price * quantity + transportCharges);
    }

    /**
     * 格式化为小图显示模式价格+运费,具体显示样式可见商品列表的小图模式
     * @date 2012-11-1上午10:00:15
     * @param quantity
     * @param transportCharges
     * @return
     */
    public static String formatSmallTranAndQuantity(int quantity, long transportCharges) {

        String transportChargesStr;

        transportChargesStr = transportCharges > 0 ? "(含快递 " + PriceFormator.formatNoSymbolLong(transportCharges)
                + " 元) " : "";
        return transportChargesStr + "共 " + quantity + " 件";
    }

    /**
     * 格式化无货币符号的long型
     * @date 2012-11-19下午3:13:43
     * @param price
     * @return
     */
    public static String formatNoSymbolLongNoDecimals(Long price) {
        if (price == null) {
            return "";
        }
        long mod = price % 100;
        StringBuilder sb = new StringBuilder();
        sb.append(price / 100);
        if (mod != 0) {
            sb.append(".");
            sb.append(mod);
        }

        return sb.toString();
    }
}

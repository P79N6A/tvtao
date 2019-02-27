/**
 *
 */
package com.yunos.tvtaobao.biz.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;

import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.businessview.R;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tianxiang
 * @date 2012-11-15 上午9:43:34
 */
public class StringUtil {
    private static final String TAG = "StringUtil";
    /**
     * 判断是否是url，目前只支持一下类型： com|net|cn|gov.cn|org|name|com.cn|
     * net.cn|org.cn|info|biz|cc|tv|hk|mobi
     */
    public static final String URL_MATCHER = "((http://)|(https://)){0,1}[\\w-\\.]+\\.(com|net|cn|gov\\.cn|org|name|com\\.cn|net\\.cn|org\\.cn|info|biz|cc|tv|hk|mobi)/{0,1}.*";
    /**
     * 判断是否是淘宝内部url，目前支持以下：
     * (taobao|tmall|alibaba|alipay|etao|koubei|juhuasuan|yunos|wasu).com
     */
    public static final String URL_INNER_MATCHER = "((http://)|(https://)){0,1}([\\w-\\.]+\\.){0,1}(taobao|tmall|alibaba|alipay|etao|koubei|juhuasuan|yunos|xiami|wasu)\\.(com|tv)/{0,1}.*";

    /**
     * cibn牌照域名
     */
    public static final String URL_CIBN_MATCHER = "(https://|http://){0,1}(\\w*\\.)+ott\\.cibntv\\.net/{0,1}.*";
    /**
     * 手机号码校验正则
     */
    public static final String MOBILE_NUMBER_MATCHER = "^(((13[0-9])|(15[0-3,5-9])|(18[0,5-6,7-9]))([0-9]{8}))$";
    /**
     * 邮编校验正则
     */
    public static final String POSTCODE_MATCHER = "([0-9]){6}+";


    public static int getLength(String str) {
        if (str == null) {
            return 0;
        }

        return str.length();
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isDigit(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 是否是url
     *
     * @param s
     * @return
     * @author mty
     */
    public static boolean isUrl(String s) {
        return s == null ? false : s.matches(URL_MATCHER);
    }

    /**
     * 淘宝内部url
     *
     * @param s
     * @return
     * @author mty
     */
    public static boolean isInnerUrl(String s) {
        return s == null ? false : (s.matches(URL_INNER_MATCHER) || s.matches(URL_CIBN_MATCHER));
    }

    /**
     * 是否手机号码
     *
     * @param s
     * @return
     * @date 2012-11-18下午12:52:50
     */
    public static boolean isMobileNumber(String s) {
        return s == null ? false : s.matches(MOBILE_NUMBER_MATCHER);
    }

    /**
     * 是否邮编
     *
     * @param s
     * @return
     * @date 2012-11-18下午12:52:50
     */
    public static boolean isPostCode(String s) {
        return s == null ? false : s.matches(POSTCODE_MATCHER);
    }

    /**
     * 在url不是以http开头时，比如taobao.com需要加上http，否则浏览器识别不出加载不出内容。 url格式化
     *
     * @param s
     * @return
     * @author mty
     */
    public static String fomatUrl(String s) {
        return s == null ? null : (s.startsWith("http://") || s
                .startsWith("https://")) ? s : "http://" + s;
    }

    /**
     * 从url中获取商品id,如下： http://a.m.tmall.com/i15686321872.htm?tg_key=jhs&v=0&sid=
     * b7aacb07329b654e:8 i 15686321872 就是要获取的id
     * <p>
     * 兼容[id=]和[itemId=]的形式
     *
     * @return
     * @author mty
     */
    public static String getItemIdFromUrl(String url) {
        if (url != null) {
            Matcher m = Pattern.compile(
                    "http://a.m.(taobao|tmall).com/i([0-9]{6,12}).htm")
                    .matcher(url);
            if (m.find() && m.groupCount() > 1) {
                return m.group(2);
            }
        }
        /*
         * if (url != null) { Matcher m =
		 * Pattern.compile(".*?com/i([0-9]+)\\.htm*").matcher(url); if
		 * (m.find()) { return m.group(1); } else { m =
		 * Pattern.compile(".*?itemId=([0-9]+)\\D*").matcher(url); if (m.find())
		 * { return m.group(1); } else { m =
		 * Pattern.compile(".*?id=([0-9]+)\\D*").matcher(url); if (m.find()) {
		 * return m.group(1); } } }
		 * 
		 * }
		 */
        return null;
    }

    public static String formatPrice(String price) {

        if (price.indexOf("-") != -1) {
            //格式化价格：166.00-188.80  ——> 166.00
            String[] strs = price.split("-");
            return strs[0];
        } else {
            //188.00
            return price;
        }
    }


    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }


    public static SpannableString formatPriceToSpan(Context context, String price) {
        String priceUnit = "¥";
        SpannableString spPrice = null;
        String current = price;
        spPrice = new SpannableString(priceUnit + current);
        int p = price.indexOf(".");
        int textSize = 26;
        if ((p >= 0 && p <= 4)) {
            textSize = (int) context.getResources().getDimension(R.dimen.sp_48);
            spPrice.setSpan(new AbsoluteSizeSpan(textSize), priceUnit.length(), priceUnit.length() + p,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (4 < p && p <= 6) {
            textSize = (int) context.getResources().getDimension(R.dimen.sp_30);
            spPrice.setSpan(new AbsoluteSizeSpan(textSize), priceUnit.length(), priceUnit.length() + p,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (p == -1) {
            if (price.length() >= 0 && price.length() <= 4) {
                textSize = (int) context.getResources().getDimension(R.dimen.sp_48);
                spPrice.setSpan(new AbsoluteSizeSpan(textSize), priceUnit.length(), spPrice.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (price.length() > 4 && price.length() <= 6) {
                textSize = (int) context.getResources().getDimension(R.dimen.sp_30);
                spPrice.setSpan(new AbsoluteSizeSpan(textSize), priceUnit.length(), spPrice.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                textSize = (int) context.getResources().getDimension(R.dimen.sp_36);
                String text = "无价之宝";
                spPrice = new SpannableString(text);
                spPrice.setSpan(new AbsoluteSizeSpan(textSize), 0, text.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        } else {
            textSize = (int) context.getResources().getDimension(R.dimen.sp_36);
            String text = "无价之宝";
            spPrice = new SpannableString(text);
            spPrice.setSpan(new AbsoluteSizeSpan(textSize), 0, text.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spPrice;
    }


    /**
     * 格式化数值
     *
     * @param value
     * @return
     */

    public static String formatValue(String value) {
        int integer = Integer.valueOf(value);
        float count = Float.valueOf(value);
        if (integer >= 10000 && integer < 1000000) {
            float countDouble = count / 10000;
            DecimalFormat df = new DecimalFormat("0.00");
            String filesize = df.format(countDouble);
            return filesize + "万";
        } else if (integer >= 1000000 && integer < 10000000) {
            float countDouble = count / 1000000;
            DecimalFormat df = new DecimalFormat("0.00");
            String filesize = df.format(countDouble);
            return filesize + "百万";

        } else if (10000000 <= integer && integer < 100000000) {
            float countDouble = count / 10000000;
            DecimalFormat df = new DecimalFormat("0.00");
            String filesize = df.format(countDouble);
            return filesize + "千万";

        } else {
            return Integer.valueOf((int) count) + "";
        }

    }

    public static String formatGautee(String str) {
        String currentStr = str;
        if (str.contains("天内发货") && str.contains("卖家承诺")) {
            //如果显示的服务中有“卖家承诺N天内发货”则改为“承诺N天内发货”
            String subString = str.substring(2);
            currentStr = subString;

        } else if (str.contains("小时内发货") && str.contains("卖家承诺")) {
            //如果显示的服务中有“卖家承诺24小时内发货”则改为“承诺24小时内发货”
            String subString = str.substring(2);
            currentStr = subString;
        } else if (str.contains("不支持") && str.contains("天退换") || str.contains("天无理由")) {
            String subString = str.substring(0, str.indexOf("天"));
            currentStr = subString + "天退换";
            /**
             不支持N天退换、不支持N天无理由===》不支持N天退换
             */
        } else if (str.contains("天退货") || str.contains("天无理由") || str.contains("天退换") || str.contains("天无理由退")) {
            String subString = str.substring(0, str.indexOf("天"));
            currentStr = subString + "天无理由退换货";
            /**
             *  N天退货、N天无理由、N天退换、N天无理由退===》N天无理由退换货
             */

        }

        return currentStr;

    }

}

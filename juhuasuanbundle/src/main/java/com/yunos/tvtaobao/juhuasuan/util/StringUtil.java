/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.util;


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
     * 判断是否是淘宝内部url，目前支持一下：
     * (taobao|tmall|alibaba|alipay|etao|koubei|juhuasuan).com
     */
    public static final String URL_INNER_MATCHER = "((http://)|(https://)){0,1}([\\w-\\.]+\\.){0,1}(taobao|tmall|alibaba|alipay|etao|koubei|juhuasuan)\\.com/{0,1}.*";
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

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 是否是url
     * @author mty
     * @param s
     * @return
     */
    public static boolean isUrl(String s) {
        return s == null ? false : s.matches(URL_MATCHER);
    }

    /**
     * 淘宝内部url
     * @author mty
     * @param s
     * @return
     */
    public static boolean isInnerUrl(String s) {
        return s == null ? false : s.matches(URL_INNER_MATCHER);
    }

    /**
     * 是否手机号码
     * @date 2012-11-18下午12:52:50
     * @param s
     * @return
     */
    public static boolean isMobileNumber(String s) {
        return s == null ? false : s.matches(MOBILE_NUMBER_MATCHER);
    }

    /**
     * 是否邮编
     * @date 2012-11-18下午12:52:50
     * @param s
     * @return
     */
    public static boolean isPostCode(String s) {
        return s == null ? false : s.matches(POSTCODE_MATCHER);
    }

    /**
     * 在url不是以http开头时，比如taobao.com需要加上http，否则浏览器识别不出加载不出内容。 url格式化
     * @author mty
     * @param s
     * @return
     */
    public static String fomatUrl(String s) {
        return s == null ? null : (s.startsWith("http://") || s.startsWith("https://")) ? s : "http://" + s;
    }

    /**
     * 从url中获取商品id,如下： http://a.m.tmall.com/i15686321872.htm?tg_key=jhs&v=0&sid=
     * b7aacb07329b654e:8 i 15686321872 就是要获取的id
     * 兼容[id=]和[itemId=]的形式
     * @author mty
     * @param startTime
     * @return
     */
    public static String getItemIdFromUrl(String url) {
        if (url != null) {
            Matcher m = Pattern.compile("http://a.m.(taobao|tmall).com/i([0-9]{6,12}).htm").matcher(url);
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
         * }
         */return null;
    }

    public static long convertToLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

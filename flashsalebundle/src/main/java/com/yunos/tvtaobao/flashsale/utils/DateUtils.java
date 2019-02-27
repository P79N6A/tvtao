package com.yunos.tvtaobao.flashsale.utils;

import android.content.Context;
import android.text.TextUtils;

import com.yunos.tvtaobao.flashsale.AppManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huangdaju on 17/7/13.
 */

public class DateUtils {

    /**
     * 时间校验正则,24点格式,22:00:00
     */
    public static final String TIME_MATCHER = "^(([0-9]{2}):([0-9]{2}):([0-9]{2}))$";

    /**
     * 时间转为毫秒数
     * @param dateStr
     *            yyyyMMddHHmmss
     * @return
     */
    final public static long string2Timestamp(String dateStr) {
        return string2Timestamp(dateStr, "yyyyMMddHHmmss");
    }

    final public static long string2Timestamp(String dateStr, String format) {
        if (TextUtils.isEmpty(dateStr)) {
            return 0;
        }
        long temp = 0;
        try {
            Date date = null;
            date = new SimpleDateFormat(format).parse(dateStr);
            if (date != null) {
                temp = date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 时间转为毫秒数
     * @param time
     *            yyyyMMddHHmmss
     * @return
     */
    final public static String timestamp2String(long time) {
        Date date = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sf.format(date);
    }

    final public static String formatConvert(String strTime) {
        long time = string2Timestamp(strTime, "yyyy-MM-dd HH:mm:ss");

        return timestamp2String(time);

    }

    /**
     * 是否是时间
     * @param time
     * @return
     */
    public static boolean isTimeString(String time) {
        return time == null ? false : time.matches(TIME_MATCHER);
    }

    /**
     * 毫秒数转为指定格式字符串
     * @param time
     * @return
     */
    public static String millisecond2String(long time) {
        StringBuilder sb = new StringBuilder();

        int hour, min, sec;

        time = time / 1000;
        sec = (int) (time % 60);
        time /= 60;
        min = (int) (time % 60);
        time /= 60;
        hour = (int) (time % 60);
        sb.append(String.format("%02d", hour));
        sb.append(":");
        sb.append(String.format("%02d", min));
        sb.append(":");
        sb.append(String.format("%02d", sec));

        return sb.toString();
    }

    /**
     * 时间戳转为指定格式字符串
     * @param time
     * @return
     */
    public static String timestamp2String(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(new Date(time));
        return dateStr;
    }

    /**
     * 从格式会2015042710270000格式中获取小时、分钟和秒数 返回10:27
     * @param format
     *            时间字符串
     * @return
     */
    public static String getTime(String format) {
        int size = null != format ? format.length() : 0;
        if (size > 6) {
            return format.substring(size - 6, size - 4) + ":" + format.substring(size - 4, size - 2);
        }
        return "";
    }

    /**
     * 根据当前时间，获取0点的时间戳
     * @return
     */
    public static long getTimeAtZero(Context context) {
        long curTime = AppManager.getInstance(context).getTimerManager().getCurTime();
        Date date = new Date(curTime);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date.getTime();
    }






}

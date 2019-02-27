package com.yunos.tvtaobao.biz.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    /**
     * 本地时间是否在给定的时间范围内
     * @param start 开始时间 yyyy-MM-dd HH:mm:ss
     * @param end 结束时间 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static boolean isBteenStartAndEnd(String start, String end) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        try {
            Date StartD = timeFormat.parse(start);
            Date endD = timeFormat.parse(end);

            boolean after = date.after(StartD);
            boolean before = date.before(endD);
            if (after && before) {
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 与当前时间比较,大于当前时间返回1,小于当前时间返回-1,等于时返回0
     * @param time
     */
    public static int compareToCurrentTime(String time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        try {
            Date timeDate = timeFormat.parse(time);
            return date.compareTo(timeDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
	
	    /**
     * 时间规则，比较服务器时间，判断是取服务器时间还是取客户端时间
     * @param serverCurrentTime
     * @return
     */
    public static long getCurrentTime(long serverCurrentTime) {
        long clientCurrentTime = System.currentTimeMillis();
        if (serverCurrentTime > clientCurrentTime || Math.abs(serverCurrentTime - clientCurrentTime) > 10) {
            return serverCurrentTime;
        }
        return clientCurrentTime;
    }

    /**
     * 获取本地时间,以时分显示00:00
     * @return
     */
    public static String getNativeTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date time = Calendar.getInstance().getTime();
        return format.format(time);
    }
}

package com.tvtaobao.voicesdk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xutingting on 2017/11/1.
 */

public class DateUtil {
    /**
     * 掉此方法输入所要转换的时间输入例如（"2014-06-14 16:09:00"）返回时间戳
     *
     * @param time
     * @return
     */
    public static String dateToStamp(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date;
        String stf = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
             stf = String.valueOf(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stf;
    }


    public static String dateToStampYearAndMonth(String timeStamp) {
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        long  l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"2014年06月14日16:09"）
     *
     * @param timeStamp
     * @return
     */
    //时间戳转字符串
    public static String getStrTime(String timeStamp){
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        long  l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l));//单位秒
        return timeString;
    }


}

package com.yunos.tvtaobao.zhuanti.utils;


import android.annotation.SuppressLint;
import android.util.Log;

import com.yunos.tv.core.common.AppDebug;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间处理工具
 * 
 * @author hanqi
 * @date 2014-5-28
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    private static String TAG="TimeUtil";

    /**
     * 将格式化的时间转换成long型的时间戳
     * 
     * @param time
     *            yyyy-MM-dd HH:mm:ss格式的时间
     * @return
     * @author hanqi
     * @date 2014-6-26
     */
    public static long getTime(String time) {
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return getTime(time, sfd);
    }

    /**
     * 将格式化的时间转换成long型的时间戳
     * 
     * @param time
     * @param format
     * @return
     * @author hanqi
     * @date 2014-6-26
     */
    public static long getTime(String time, DateFormat format) {
        long t = 0;
        try {
            Date d = format.parse(time);
            t = d.getTime();
        }
        catch (ParseException e) {

        }
        return t;
    }

    /**
     * 时间重新格式化工具，输出的新的时间格式为："yyyy:MM:dd HH:mm:ss"
     * 
     * @param time
     * @param format
     * @return
     * @author hanqi
     * @date 2014-5-28
     */
    public static String reFormatTime(String time, DateFormat format) {
        Date d = null;
        try {
            d = format.parse(time);
        }
        catch (ParseException e) {

        }
        SimpleDateFormat sfd = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        return sfd.format(d);
    }

    /**
     * 格式化 时间,long转换为HH:MM:SS
     * 
     * @return
     */
    public static String formatTime(long time) {
        int h = (int) (time / 1000 / 60 / 60);
        int m = (int) (time % (1000 * 60 * 60) / 1000 / 60);
        int s = (int) (time % (1000 * 60 * 60) % (1000 * 60) / 1000);
        String hour = null;
        String minute = null;
        String second = null;
        if (h < 10) {
            hour = String.format("0%d", h);
        }
        else {
            hour = String.valueOf(h);
        }

        if (m < 10) {
            minute = String.format("0%d", m);
        }
        else {
            minute = String.valueOf(m);
        }

        if (s < 10) {
            second = String.format(Locale.getDefault(), "0%d", s);
        }
        else {
            second = String.valueOf(s);
        }

        return String.format("%s:%s:%s", hour, minute, second);
    }

    /**
     * 格式化时长
     *
     * @param time
     * @return
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) time;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }



    /**
     * 计算开抢前倒计时
     * @param time 秒
     * @return
     */
    public static String formateCountTime(long time) {
        String reTime="";
        if (time<=60&&time>=0){
            reTime= String.valueOf(time);
        }else if (time>60){
            if (time%60==0){
                long minute=time/60;
                reTime=minute+"";
            }else {
                long minutes = time/ 60+1;
                reTime=minutes+"";
            }
        }
        Log.e(TAG,"reTime = "+reTime);
        return reTime;
    }
    /**
     * 计算下一个积分点相距的时间
     * ：1.时间间隔在30分钟之内，提示X分钟后下一波来袭；
     2.大于30分钟提示具体时间点，比如“下一波来袭 13：30”
     * @param time  秒
     * @return
     */

    public static String formateNextIntagTime(long time, String startTime) {
        AppDebug.e(TAG,"当日距离下一个点的 time = "+time);
        String reTime="";
        if (time<60&&time>=0){
            reTime=time+"秒后下一波来袭";
        }else if (time>=60){
            long minutes = time/ 60;
            if (minutes<=30){
                reTime=minutes+"分钟后下一波来袭";
            }else {
                String data2=timeStamp2Date(startTime,"HH:mm");
                reTime="下一波来袭"+data2;
            }
        }
        AppDebug.e(TAG,"reTime = "+reTime);
        return reTime;
    }


    /**
     * 计算两个时间戳之间的相隔的天数
     * @param startTime
     * @param sysTime
     * @return
     */
    public  static int generateDays(long startTime,long sysTime){
        int days=0;
        String t1 = String.valueOf(startTime/1000);
        String t2= String.valueOf(sysTime/1000);
        String date1 = timeStamp2Date(t1, "yyyy-MM-dd HH:mm:ss");
        String data2=timeStamp2Date(t2,"yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date3 = format.parse(date1);
            Date data4 = format.parse(data2);
             days= differentDays(date3,data4);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return days;
    }

    /**
     *
     * @param seconds 秒
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }
    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2){
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++) {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            AppDebug.e(TAG,"判断day2 - day1 :"+timeDistance + (day2-day1));
            return timeDistance + (day2-day1) ;
        } else{
            AppDebug.e(TAG,"判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }

    public  static String formatTime(long milliseconds, String format){
        Date date=new Date(milliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time= sdf.format(date);
        return  time;
    }
}

/**
 * 
 * @author qinglong.niql (niqinglong@gmail.com)
 *
 */
package com.aliyun.base.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimeUtils {
	public static final long DAY_MILLISE_SECONDS = 24 * 60 * 60 * 1000;

	public static final long HOUR_MILLISE_SECONDS = 60 * 60 * 1000;

	private static SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日");
	private static SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日");

	/*
	 * default,there are no params, return the format of the current time.
	 */
	public static String getDateFormat() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
		return sdf.format(new Date());
	}

	/**
	 * 返回指定格式的日期
	 * 
	 * @param pattern
	 *            比如:yyyy-MM-dd_HH-mm-ss
	 * @return
	 */
	public static String getDateFormat(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
		return sdf.format(new Date());
	}

	public static String getDateFormat(long milliseconds, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
		return sdf.format(milliseconds);
	}

	/*
	 * if there is a param, return the format of the your given time about
	 * milliseconds.
	 */
	public static String getDateFormat(long milliseconds) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		return sdf.format(new Date(milliseconds));
	}

	/*
	 * return milliseconds format of the given
	 * time(year,month,day,hour,minute,second).
	 */
	public static long getTimeInMillis(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		Calendar cl = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
		return cl.getTimeInMillis();
	}

	/*
	 * return the weibo format of the current time.
	 */
	public static String getTimeString(long postTime) {
		long now = System.currentTimeMillis();
		long intervalTime = now - postTime * 1000;
		int result = 0;

		if (intervalTime > DAY_MILLISE_SECONDS) {
			result = (int) (intervalTime / (DAY_MILLISE_SECONDS));
			if (result <= 3) {
				return result + "天前";
			} else {
				int year = new Date(postTime * 1000).getYear();
				if (year >= new Date().getYear()) {
					return format1.format(new Date(postTime * 1000));
				} else {
					return format2.format(new Date(postTime * 1000));
				}
			}
		} else if (intervalTime > HOUR_MILLISE_SECONDS) {
			result = (int) (intervalTime / (HOUR_MILLISE_SECONDS));
			if (result > 0) {
				return result + "小时前";
			}
		} else if (intervalTime > 60000) {
			result = (int) (intervalTime / (60000));
			if (result > 0) {
				return result + "分钟前";
			}
		}
		return "刚刚";
	}

	/**
	 * 
	 * @param seconds 毫秒数
	 * @return
	 */
	public static String getDuration(int seconds) {
		if (seconds == 0) {
			return "0秒";
		}
		StringBuilder sb = new StringBuilder("");
		int hs = seconds / 3600;
		int ms = seconds % 3600 / 60;
		int ss = seconds % 60;
		if (hs > 0) {
			sb.append(String.format("%d", hs) + "小时");
		}
		if (ms > 0) {
			sb.append(String.format("%2d", ms) + "分钟");
		}
		if (ss > 0) {
			sb.append(String.format("%2d", ss) + "秒");
		}
		return sb.toString();
	}

}

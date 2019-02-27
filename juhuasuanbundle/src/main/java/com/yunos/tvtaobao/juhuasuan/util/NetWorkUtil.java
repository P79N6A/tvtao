package com.yunos.tvtaobao.juhuasuan.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.yunos.tv.core.CoreApplication;

public class NetWorkUtil {
	/**
	 * 判断网络类型
	 * 
	 * @return 1表示沒有網絡,2表示2g网络,3表示3g网络或WIFI
	 */
	public static int getNetWorkType() {
		if (!isNetWorkAvailable()) {
			return 1;
		}

		if (isWifi()) {
			return 3;
		}

		TelephonyManager tm = null;
		try {
			tm = (TelephonyManager) CoreApplication.getApplication().getSystemService(
					Context.TELEPHONY_SERVICE);
		} catch (Exception e) {
		}
		if (tm == null) {
			return 1;
		}

		int type = tm.getNetworkType();
		switch (type) {
		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
			return 2;
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
			return 3;
		default:
			return 1;
		}
	}

	/**
	 * 网络是否可用
	 * 
	 * @date 2012-11-20下午1:55:23
	 * @return
	 */
	public static boolean isNetWorkAvailable() {
		boolean isAvailable = false;
		try {
			ConnectivityManager cm = getConnectivityManager();
			if(cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if(info != null) {
					isAvailable = info.isAvailable();
				}
			}
		} catch (Exception e) {
		}

		return isAvailable;
	}

	public static boolean isNetWorkAvailable(Context context) {
		if (!isNetWorkAvailable()) {
			return false;
		} else {
			return true;
		}
	}

	private static ConnectivityManager getConnectivityManager() {
		ConnectivityManager cm = null;
		try {
			cm = (ConnectivityManager) CoreApplication.getApplication().getSystemService(
					Context.CONNECTIVITY_SERVICE);
		} catch (Exception e) {
		}
		return cm;
	}

	/**
	 * 判断是否是wifi网络
	 * 
	 * @return
	 */
	public static boolean isWifi() {
		ConnectivityManager cm = getConnectivityManager();
		if (cm != null) {
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null && ni.getTypeName().equals("WIFI")) {
				return true;
			}
		}
		return false;
	}
}

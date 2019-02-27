package com.aliyun.base.info;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileInfo {

	/**
	 * 获取运营商名称
	 * 
	 * @return
	 */
	public static String getOperatorName(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperatorName();
	}

	/**
	 * 获取运营商编码
	 * 
	 * @return
	 */
	public static String getOperatorCode(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperator();
	}

	/**
	 * 获取运营商网络类型，常量值在TelephonyManager类中定义
	 * 
	 * @return
	 */
	public static int getOperatorNetworkType(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkType();
	}
	
	public static int getScreenWidthDip(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) (dm.widthPixels / dm.density + 0.5f);
	}
	
	public static int getScreenHightDip(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) (dm.heightPixels / dm.density + 0.5f);
	}
	/**
	 * // 屏幕密度（像素比例：0.75/1.0/1.5/2.0） perDip
	 * @return
	 */
	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}
	
	/**
	 * The screen density expressed as dots-per-inch 
	 * // 屏幕密度（每寸像素：120/160/240/320）
	 * @return 
	 */
	public static float getDensityDpi(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}
	
	public static int dip2px(Context context, float dip) {
		return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
	}
	
	public static float px2dip(Context context, int px) {
		return (int) (px / context.getResources().getDisplayMetrics().density + 0.5f);
	}
	
	/**
	 * 将px值转换为sp值，保证文字大小不变
	 */
	public static float px2sp(Context context, int pxValue) {
		return (int) (pxValue / context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 */
	public static int sp2px(Context context, float spValue) {
		return (int) (spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
	}

	public static int getScreenWidthPx(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeightPx(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 获取设备生产厂商及MODEL
	 * 
	 * @return
	 */
	public static String getDeviceName() {
		return Build.MANUFACTURER + " : " + Build.MODEL;
	}

	/**
	 * 获取Imei
	 * 
	 * @return
	 */
	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if (imei == null || "".equals(imei.trim())) {
			File installation = new File(Environment.getExternalStorageDirectory() + "/pwmob/", "INSTALLATION");
			try {
				if (!installation.exists()) {
					imei = UUID.randomUUID().toString();
					DataOutputStream out = new DataOutputStream(new FileOutputStream(installation));
					out.writeUTF(imei);
					out.close();
				} else {
					DataInputStream dis = new DataInputStream(new FileInputStream(installation));
					imei = dis.readUTF();
					dis.close();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return imei;
	}

	/**
	 * 获取内核版本号
	 * 
	 * @return
	 */
	public static String getFormattedKernelVersion() {
		String procVersionStr;

		try {
			BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
			try {
				procVersionStr = reader.readLine();
			} finally {
				reader.close();
			}

			final String PROC_VERSION_REGEX = "\\w+\\s+" + /* ignore: Linux */
			"\\w+\\s+" + /* ignore: version */
			"([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
			"\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /*
														 * group 2:
														 * (xxxxxx@xxxxx
														 * .constant)
														 */
			"\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
			"([^\\s]+)\\s+" + /* group 3: #26 */
			"(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
			"(.+)"; /* group 4: date */

			Pattern p = Pattern.compile(PROC_VERSION_REGEX);
			Matcher m = p.matcher(procVersionStr);

			if (!m.matches()) {
				return "Unavailable";
			} else if (m.groupCount() < 4) {
				return "Unavailable";
			} else {
				return (new StringBuilder(m.group(1)).append("\n").append(m.group(2)).append(" ").append(m.group(3)).append("\n").append(m
						.group(4))).toString();
			}
		} catch (IOException e) {
			return "Unavailable";
		}
	}

	/**
	 * 获取系统内存
	 * 
	 * @return
	 */
	public static String getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split("\\s+");
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return String.valueOf(initial_memory);
		// return Formatter.formatFileSize(getBaseContext(), initial_memory);//
		// Byte转换为KB或者MB，内存大小规格化
	}
	
	public static String getUUId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId, serial, androidId;

		deviceId = "" + tm.getDeviceId();
		serial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID uuid = new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | serial.hashCode());
		return uuid.toString();
	}
	
	public static String getIp() {
		String ip = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAdd = intf.getInetAddresses(); enumIpAdd.hasMoreElements();) {
					InetAddress inetAddress = enumIpAdd.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						ip = inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ip;
	}
}

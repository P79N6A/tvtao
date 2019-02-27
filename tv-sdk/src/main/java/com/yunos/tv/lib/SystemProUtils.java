package com.yunos.tv.lib;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.app.widget.focus.PositionManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import android.os.SystemProperties;

/**
 * 来自:http://ued.aliyun-inc.com/cloudapp/index.php?title=CloudUUID_%E5%AE%A2%E6%
 * 88%B7%E7%AB%AF%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97
 * 
 */
public class SystemProUtils {

	static int mFocusMode = PositionManager.FOCUS_STATIC_DRAW;

	public static void setGlobalFocusMode(int mode) {
		mFocusMode = mode;
	}

	public static int getGlobalFocusMode() {
		return mFocusMode;
	}

	public static boolean isQiyi = false;

	public static String getSystemVersion() {
		String version = Build.VERSION.RELEASE;
		String versionArray[] = version.split("-");
		return versionArray[0];
	}

	public static String getUUID() {
		return CloudUUIDWrapper.getCloudUUID();
//		try {
//			Class<?> cloudUuid = Class.forName("com.yunos.baseservice.clouduuid.CloudUUID");
//			Method m = cloudUuid.getMethod("getCloudUUID");
//			String result = (String) m.invoke(null);
//
//			if (TextUtils.isEmpty(result) || "false".equalsIgnoreCase(result)) {
//				return "unknow_tv_imei";
//			}
//			return result;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "unknow_tv_imei";
//		}

	}

	// 域名
	public static String getDomain() {
		final String prefix = "http://";
		final String posfix = "/";
		String default_domain = "http://api.yunos.wasu.tv/";
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			String result = (String) m.invoke(null, "ro.yunos.domain.aliyingshi", "falsenull");
			if ("falsenull".equals(result)) {
				Log.w("System", "domain yingshi  is falsenull, return default");
				return default_domain;
			}
			if (!TextUtils.isEmpty(result)) {
				return prefix+result.trim().replaceAll("/", "")+posfix;
			} else {
				return default_domain;
			}
		} catch (Exception e) {
			Log.w("System", "getDomain: error");
			return default_domain;
		}
	}

	public static String getDomainMTOP() {
		final String prefix = "http://";
		final String posfix = "/rest/api3.do?";
		String default_domainmtop = "http://m.yunos.wasu.tv/rest/api3.do?";
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			String result = (String) m.invoke(null, "ro.yunos.domain.aliyingshi.mtop", "falsenull");
			if ("falsenull".equals(result)) {
				Log.w("System", "getDomainMTOP falsenull,return default");
				return default_domainmtop;
			}
			if (!TextUtils.isEmpty(result)) {
				return prefix+result.trim().replaceAll("/", "")+posfix;
			} else {
				return default_domainmtop;
			}
		} catch (Exception e) {
			Log.w("System", "getDomainMTOP: error");
			return default_domainmtop;
		}
	}

	// 牌照TODO 1 wasu, 7 icntv
	public static String getLicense() {
		String liscence = "1";
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			String result = (String) m.invoke(null, "ro.yunos.domain.license", "falsenull");
			if ("falsenull".equals(result)) {
				Log.w("System", "domain yingshi mtop is unknow!!!");
				return liscence;
			}
			return TextUtils.isEmpty(result)?liscence:result.trim();
		} catch (Exception e) {
			Log.w("System", "getLicense: error");
			return liscence;
		}
	}

	public static String getLogoPath() {
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			String result = (String) m.invoke(null, "ro.yunos.domain.license.logo", "falsenull");
			if ("falsenull".equals(result)) {
				Log.w("System", "domain yingshi logo path is unknow!!!");
				return null;
			}
			return result.trim();

		} catch (Exception e) {
			Log.w("System", "getLogoPath: error");
			return null;
		}
	}

	/**
	 * ro.yunos.domain.aliyingshi.contents 视频来源0淘TV,3优酷，4搜狐,5爱奇艺 给魔盒用，默认不配置。一体机才会配置。
	 * 2.7魔盒参数0,3,4 ;
	 * 2.8一体机参数:0,3,4,5
	 * 代表客户端的能力，只升不降
	 * 
	 * @return
	 */
	public static String getContents() {
		String defaultContent = "0,3,4,5";
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			String result = (String) m.invoke(null, "ro.yunos.domain.aliyingshi.cts", "falsenull");
			if ("falsenull".equals(result)) {
				Log.w("System", "domain yingshi contents is unknow!!!");
				return defaultContent;
			}
			return TextUtils.isEmpty(result) ? defaultContent : result;
		} catch (Exception e) {
			Log.w("System", "getContents: error");
			return defaultContent;
		}
	}

	// 爱奇艺参数
	public static String getManufacture() {
		if (isQiyi) {
			return "ali_haiertv";
		} else {
			try {
				Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
				Method m = SystemProperties.getMethod("get", String.class, String.class);
				String result = (String) m.invoke(null, "ro.product.manufacturer", "falsenull");
				if ("falsenull".equals(result)) {
					Log.w("System", "manufactuer is unknow!!!");
					return "unknow_tv_manufactuer";
				}
				return TextUtils.isEmpty(result) ? "" : result;
			} catch (Exception e) {
				Log.w("System", "getManufacture: error");
				return "";
			}
		}
	}

	public static String getChip() {
		if (isQiyi) {
			return "amlogic_t866";
		} else {
			try {
				Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
				Method m = SystemProperties.getMethod("get", String.class, String.class);
				String result = (String) m.invoke(null, "ro.yunos.product.chip", "falsenull");
				if ("falsenull".equals(result)) {
					Log.w("System", "chip is unknow!!!");
					return "unknow_tv_chip";
				}
				return TextUtils.isEmpty(result) ? "" : result;
			} catch (Exception e) {
				Log.w("System", "getChip: error");
				return "";
			}
		}
	}

	public static String getMediaParams() {
		String strProp = getSystemProperties("ro.media.ability");
		if (strProp != null && !TextUtils.isEmpty(strProp)) {
			Log.d("media", "=====strProp====" + strProp);
			return strProp;
		} else {
			Log.w("media", "=====strProp null====");
		}
		return "";
	}

	public static String getDeviceName() {
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			// String result = (String) m.invoke(null, "ro.yunos.product.board", "falsenull");
			String result = (String) m.invoke(null, "ro.product.model", "falsenull");// Build.Model
			if ("falsenull".equals(result)) {
				return null;
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 通用的获取getprop中的某值
	 * 
	 * @param key
	 * @return
	 */
	public static String getSystemProperties(String key) {
		String value = null;
		Class<?> cls = null;
		try {
			cls = Class.forName("android.os.SystemProperties");
			Method hideMethod = cls.getMethod("get", String.class);
			Object object = cls.newInstance();
			value = (String) hideMethod.invoke(object, key);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
}

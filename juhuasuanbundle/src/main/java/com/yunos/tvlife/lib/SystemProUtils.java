package com.yunos.tvlife.lib;

import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import android.os.SystemProperties;

/**
 * 来自:http://ued.aliyun-inc.com/cloudapp/index.php?title=CloudUUID_%E5%AE%A2%E6%
 * 88%B7%E7%AB%AF%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97
 * 
 */
public class SystemProUtils {

	public static String getSystemVersion() {
		String version = Build.VERSION.RELEASE;
		String versionArray[] = version.split("-");
		return versionArray[0];
	}

	public static String getUUID() {
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
			String result = (String) m.invoke(null, "ro.aliyun.clouduuid", "falsenull");
			if ("falsenull".equals(result)) {
				return "unknow_tv_imei";
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getDeviceName() {
		try {
			Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
			Method m = SystemProperties.getMethod("get", String.class, String.class);
//			String result = (String) m.invoke(null, "ro.yunos.product.board", "falsenull");
			String result = (String) m.invoke(null, "ro.product.model", "falsenull");//Build.Model
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

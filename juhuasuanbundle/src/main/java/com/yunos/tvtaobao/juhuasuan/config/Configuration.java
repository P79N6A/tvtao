package com.yunos.tvtaobao.juhuasuan.config;

import android.content.res.AssetManager;

import com.yunos.tv.core.CoreApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件
 * 
 * @author yanlu.cqm
 * 
 */
public class Configuration {

	public static final String JU_CFG_PRINT_LOG_KEY = "isprintlog";
	public static final String JU_CFG_UPDATE_APP_KEY = "isupdateapp";

	private static Properties defaultProperty;

	static {
		init();
	}

	static void init() {
		defaultProperty = new Properties();

		defaultProperty.setProperty(JU_CFG_PRINT_LOG_KEY, "true");
		defaultProperty.setProperty(JU_CFG_UPDATE_APP_KEY, "true");
		InputStream is = null;
		String t4jProps = "juconfig.dat";

		try {
			AssetManager am = CoreApplication.getApplication().getAssets();
			is = am.open(t4jProps);
			defaultProperty.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
	}

	public static boolean getBoolean(String name) {
		String value = getProperty(name);
		return Boolean.valueOf(value);
	}

	public static int getIntProperty(String name) {
		String value = getProperty(name);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static long getLongProperty(String name) {
		String value = getProperty(name);
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	public static String getProperty(String name) {
		return defaultProperty.getProperty(name);
	}
}

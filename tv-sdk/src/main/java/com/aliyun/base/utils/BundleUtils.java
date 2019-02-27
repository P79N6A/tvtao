package com.aliyun.base.utils;

import android.os.Bundle;
import android.util.Log;

import java.util.Set;

public class BundleUtils {
	
	public static void print(Bundle b, String tag) {
		if (b == null) {
			return;
		}
		Set<String> keys = b.keySet();
		for (String key: keys) {
			if (key == null) {
				continue;
			}
			Log.i(tag, "BundleUtils -- print -- key:" + key + ",value:" + b.get(key).toString());
		}
	}
}

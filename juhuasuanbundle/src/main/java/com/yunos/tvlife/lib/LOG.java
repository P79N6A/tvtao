package com.yunos.tvlife.lib;

import android.util.Log;

public class LOG {

	public static void d(String TAG, boolean DEBUG, String info) {
		if (DEBUG) {
			Log.d(TAG, info);
		}
	}

	public static void i(String TAG, boolean DEBUG, String info) {
		if (DEBUG) {
			Log.i(TAG, info);
		}
	}
	
	public static void w(String TAG, boolean DEBUG, String info) {
		if (DEBUG) {
			Log.w(TAG, info);
		}
	}
}

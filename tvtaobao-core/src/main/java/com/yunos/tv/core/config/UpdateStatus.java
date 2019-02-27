package com.yunos.tv.core.config;

import android.os.Bundle;
import android.util.Log;


public enum UpdateStatus {
	
	// 需要启动更新界面
	START_ACTIVITY,
	FETUREDAILOG,
	UPDATE_DIALOG,
	// 其它状态
	UNKNOWN;
	
	private static final String TAG = "UpdateStatus";
	
	private static UpdateStatus sUpdateStatus = UNKNOWN;
	
	private static Bundle sBundle;

	public static void setUpdateStatus(UpdateStatus status, Bundle bundle) {
		Log.d(TAG, "setUpdateStatus: " + status + " Bundle: " + bundle);
		sUpdateStatus = status;
		sBundle = bundle;
	}
	
	public static UpdateStatus getUpdateStatus() {
		return sUpdateStatus;
	}
	
	public static Bundle getBundle() {
		return sBundle;
	}
}

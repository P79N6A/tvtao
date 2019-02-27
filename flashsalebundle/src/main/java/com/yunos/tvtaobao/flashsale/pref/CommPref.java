package com.yunos.tvtaobao.flashsale.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CommPref {
	private static final String TIMER_INFO = "timer_info";
	private static final String SERVER_REF = "server_ref";
	private static final String LOCAL_REF = "local_ref";
	

	private Context mContext;
	public CommPref(Context context) {
		mContext = context.getApplicationContext();
	}

	public long getServerRefTime() {
		SharedPreferences info = mContext.getSharedPreferences(TIMER_INFO,
				Context.MODE_PRIVATE);
		return info.getLong(SERVER_REF, -1);
	}

	public void setServerRefTime(long time) {
		SharedPreferences p = mContext.getSharedPreferences(TIMER_INFO,
				Context.MODE_PRIVATE);
		Editor e = p.edit();
		e.putLong(SERVER_REF, time);
		e.commit();
	}

	public long getLocalRefTime() {
		SharedPreferences info = mContext.getSharedPreferences(TIMER_INFO,
				Context.MODE_PRIVATE);
		return info.getLong(LOCAL_REF, -1);
	}

	public void setLocalRefTime(long time) {
		SharedPreferences p = mContext.getSharedPreferences(TIMER_INFO,
				Context.MODE_PRIVATE);
		Editor e = p.edit();
		e.putLong(LOCAL_REF, time);
		e.commit();
	}

}

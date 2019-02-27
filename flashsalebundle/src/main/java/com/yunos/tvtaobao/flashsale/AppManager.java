/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.yunos.tvtaobao.biz.listener.OnReminderListener;
import com.yunos.tvtaobao.flashsale.cache.MyConcernCache;
import com.yunos.tvtaobao.flashsale.cache.MyConcernCache.*;
import com.yunos.tvtaobao.flashsale.pref.CommPref;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.timer.TimerManager;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppManager {
	private Context mContext;
	private static AppManager INSTANCE;

	final private ExecutorService mExecutorService = Executors
			.newFixedThreadPool(5);

	private Handler mMainHandler = new Handler(Looper.getMainLooper());

	public Handler getMainHanlder() {
		return mMainHandler;
	}

	public ExecutorService getExecutorService() {
		return mExecutorService;
	}

	public Context getContext() {
		return mContext;
	}

	public static AppManager getInstance(Context con) {
		if (null == INSTANCE) {
			synchronized (AppManager.class) {
				if (null == INSTANCE) {
					INSTANCE = new AppManager(con);
				}
			}
		}
		return INSTANCE;
	}

	public AppManager(Context con) {
		mContext = con.getApplicationContext();
		mConcernCache = getMyConcernCache();
		getTimerManager();
	}

	private MyConcernCache mConcernCache;

	public MyConcernCache getMyConcernCache() {
		if (null == mConcernCache) {
			synchronized (MyConcernCache.class) {
				if (null == mConcernCache) {
					mConcernCache = new MyConcernCache(this);
				}
			}
		}
		return mConcernCache;
	}

	private TimerManager mTimerManager;

	public TimerManager getTimerManager() {
		if (null == mTimerManager) {
			synchronized (TimerManager.class) {
				if (null == mTimerManager) {
					mTimerManager = new TimerManager(mContext);
				}
			}
		}
		return mTimerManager;
	}

	private RequestManager mRequestManager;

	public RequestManager getRequestManager() {
		if (null == mRequestManager) {
			synchronized (RequestManager.class) {
				if (null == mRequestManager) {
					mRequestManager = new RequestManager();
				}
			}
		}
		return mRequestManager;
	}

	private CommPref mTimerPref;

	public CommPref getTimerPref() {
		if (mTimerPref == null) {
			synchronized (CommPref.class) {
				if (mTimerPref == null) {
					mTimerPref = new CommPref(mContext);
				}
			}
		}
		return mTimerPref;
	}

	public OnReminderListener getOnReminderListener() {
		return mOnReminderListener;
	}

	final private OnReminderListener mOnReminderListener = new OnReminderListener() {
		@Override
		public byte addReminder(String itemId, String strTime) {
			// TODO Auto-generated method stub
			long reminderTime = DateUtils.string2Timestamp(strTime);
			MyconcernInfo myconcernInfo = new MyconcernInfo(itemId,
					reminderTime - AppConfig.AHEAD_OF_TIME);
			byte ret = mConcernCache.insert(myconcernInfo);

			if (ret == OnReminderListener.RET_FULL) {
				StringBuffer sb = new StringBuffer();
				sb.append(mContext.getResources().getString(
						R.string.max_remind_begin));
				sb.append("" + MyConcernCache.MAX_REMIND_COUNT);
				sb.append(mContext.getResources().getString(
						R.string.max_remind_end));
				Toast.makeText(mContext, sb.toString(), Toast.LENGTH_LONG)
						.show();
			} else {
				startNextAlarm();
			}
			return ret;
		}

		@Override
		public void removeReminder(String itemId) {
			// TODO Auto-generated method stub
			if (mConcernCache.remove(itemId)) {
				startNextAlarm();
			}
		}

		@Override
		public byte getReminderState() {
			// TODO Auto-generated method stub
			return mConcernCache.getReminderState();
		}

		@Override
		public boolean hasReminder(String itemId) {
			// TODO Auto-generated method stub
			return mConcernCache.hasReminder(itemId);
		}

		@Override
		public long getServerCurrentTime() {
			// TODO Auto-generated method stub
			TimerManager timerManager = getTimerManager();
			return timerManager.getCurTime();
		}

	};

	/**
	 * 检测下一个闹钟
	 */
	public void startNextAlarm() {
		TimerManager timerManager = getTimerManager();
		long curTime = timerManager.getCurTime();
		MyconcernInfo myConcernInfo = getMyConcernCache().getNextAlarm(curTime);

		timerManager.startNextAlarm(curTime, myConcernInfo);

	}

	public static String getAppName() {
		return "tvtaobao";
	}
}

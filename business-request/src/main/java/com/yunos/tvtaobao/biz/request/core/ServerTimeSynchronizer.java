package com.yunos.tvtaobao.biz.request.core;


import android.content.Context;
import android.util.Log;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.request.BusinessRequest;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 服务器时间同步器
 */
public class ServerTimeSynchronizer {

    private static final String TAG = "ServerTimeSynchronizer";
    private static Timer timer;
    public static AtomicBoolean isServerTime = new AtomicBoolean(Boolean.FALSE);
    private static final Object mLock = new Object();
    public static Long diffTime = 0L;

    private static BusinessRequest mBusinessrequest = BusinessRequest.getBusinessRequest();

    public static long getCurrentTime() {
        AppDebug.v(TAG, TAG + ",getCurrentTime, isServerTime = " + isServerTime());
        //第一次调用的时候启动定时器
        if (!isServerTime()) {
            start();
        }
        synchronized (mLock) {
            AppDebug.v(TAG, TAG + ",getCurrentTime,currentTimeMillis = " + System.currentTimeMillis() + ",diffTime:"
                    + diffTime);
            return System.currentTimeMillis() + diffTime;
        }
    }

    public static synchronized void start() {
        updateTime();
    }

    public static synchronized void stop() {
        isServerTime.set(false);// 停止的时候，取消服务器时间标志
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
        }
    }

    public static boolean isServerTime() {
        return isServerTime.get();
    }

    /**
     * 同步服务器时间
     *
     * @date 2012-12-17上午10:40:04
     */
    public static void updateTime() {
        try {
            if (NetWorkUtil.isNetWorkAvailable()) {
                long threadId = Thread.currentThread().getId();
                AppDebug.i(TAG, TAG + ",updateTime threadId=" + threadId);
                if (threadId == 1) {
                    // is in main thread
                    asyncUpServerTime(CoreApplication.getApplication());
                } else {
                    final Long startTime = System.currentTimeMillis();
                    mBusinessrequest.requestSyncUpdatServerTime(new RequestListener<Long>() {

                        @Override
                        public void onRequestDone(Long data, int resultCode, String msg) {
                            AppDebug.v(TAG, TAG + ".updateTime.onRequestDone.data = " + data + ", resultCode = "
                                    + resultCode + ",msg = " + msg);
                            if (resultCode == 200) {
                                Long endTime = System.currentTimeMillis();
                                if (data != null) {
                                    synchronized (mLock) {
                                        diffTime = data - (startTime + (endTime - startTime) / 2);
                                        Log.v(TAG, TAG + ".updateTime.onRequestDone, get server diff:" + diffTime);
                                        isServerTime.set(true);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            AppDebug.e(TAG, TAG + ",updateTime e=" + e);
        }
    }

    public static void asyncUpServerTime(Context context) {
        if (NetWorkUtil.isNetWorkAvailable()) {
            final Long startTime = System.currentTimeMillis();
            mBusinessrequest.requestUpdatServerTime(new RequestListener<Long>() {

                @Override
                public void onRequestDone(Long data, int resultCode, String msg) {
                    AppDebug.v(TAG, TAG + ".asyncUpServerTime.onRequestDone.data = " + data + ", resultCode = "
                            + resultCode + ",msg = " + msg);
                    if (resultCode == 200) {
                        Long endTime = System.currentTimeMillis();
                        if (data != null) {
                            synchronized (mLock) {
                                diffTime = data - (startTime + (endTime - startTime) / 2);
                                Log.v(TAG, TAG + ".asyncUpServerTime.onRequestDone, get server diff:" + diffTime);
                                isServerTime.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}

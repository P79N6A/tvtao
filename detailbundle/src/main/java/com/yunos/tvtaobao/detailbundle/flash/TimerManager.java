package com.yunos.tvtaobao.detailbundle.flash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class TimerManager {
    private final static String TAG = "TimerManager";
    public static final long INTERVAL_ALARAM_CHECK_TIME = 2 * 60 * 1000; // 5min
//	public static final long INTERVAL_ALARAM_TIMEOUT = 3 * 60 * 1000; // 3MIN

    /**
     * 定时器1秒
     */
    public final static long TIMER_DELAY = 1000;

    /**
     * 基准时间, 服务器和本地运行时间
     */
    private long mServerRef;
    private long mLocalRef;
    private Timer mTimer;
    private boolean mHasRef = false;
    final private Object mSyn = new Object();
    private AtomicInteger mCurId = new AtomicInteger(0);
    //final private List<TimerInfo> mListener = new ArrayList<TimerInfo>();
    //final private List<TimerInfo> mTmpListener = new ArrayList<TimerInfo>();
    final private List<TimerInfo> mListener = new ArrayList<TimerInfo>();
    final private List<TimerInfo> mTmpListener = new ArrayList<TimerInfo>();
    private Context mContext;

    public TimerManager(Context con) {
        mContext = con.getApplicationContext();
//		startAlarmReminderCheck();
        onAlarmCheck(con);

        /** 特别需要注意，在AppManager里面不要直接创建，否则，空指针 */
        // CommPref pref = AppManager.getInstance(con).getTimerPref();
        // long serverRef = pref.getServerRefTime();
        // long localRef = pref.getLocalRefTime();
        //
        // if (serverRef > 0 && localRef > 0) {
        //
        // setRef(serverRef, localRef, false);
        // }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.setPriority(1000);
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                onAlarmCheck(context);
            }
        }, filter);
    }

    public long getCurTime() {
        if (!mHasRef) {
            return System.currentTimeMillis();
        }
        long curTime = SystemClock.elapsedRealtime();

        return mServerRef + curTime - mLocalRef;
    }

    public boolean hasReference() {
        return mHasRef;
    }


    public void setRef(long serverRef, long localRef) {
        setRef(serverRef, localRef, true);
    }

    public void setRef(long serverRef, long localRef, boolean save) {
        synchronized (mSyn) {
            if (!mHasRef) {
                mServerRef = serverRef;
                mLocalRef = localRef;
                mHasRef = true;
                if (save) {
                    CommPref pref = AppManager.getInstance(mContext)
                            .getTimerPref();
                    pref.setLocalRefTime(mLocalRef);
                    pref.setServerRefTime(mServerRef);
                }
//                AppDebug.i(
//                        TAG,
//                        "setRef:  serverRef: "
//                                + DateUtils.timestamp2String(serverRef)
//                                + " mLocalRef: " + mLocalRef);
                return;
            }
        }
        AppDebug.i(TAG, "setRef: currrent serverRef: " + serverRef
                + " localRef: " + localRef);
    }

    public int createTimer(long reminderTime, TimerListener listener,
                           Object userData) {
        if (null == listener) {
            throw new NullPointerException("not set reference ");
        }
        int id = mCurId.incrementAndGet();
        TimerInfo timer = new TimerInfo(id, reminderTime, listener, userData);

        synchronized (mSyn) {
            if (!mHasRef) {
                throw new IllegalArgumentException("not set reference ");
            }
            mListener.add(timer);
            if (null == mTimer) {
                long localTime = mServerRef
                        + (SystemClock.elapsedRealtime() - mLocalRef);

                // long localTime = System.currentTimeMillis();
                /** 精确计算当前时间，与1秒钟吻合 */
                localTime %= TIMER_DELAY;
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        processOnTimer();
                    }
                }, TIMER_DELAY - localTime, TIMER_DELAY);
                AppDebug.i(TAG, "start timer:  id = " + id + " time="
                        + reminderTime);
            }
        }
        return id;
    }

    private void processOnTimer() {
        mMainHandler.removeCallbacks(mMainRunnable);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onTimer();
        } else {
            mMainHandler.post(mMainRunnable);
        }
    }

    public void cancelTimer(int id) {
        synchronized (mSyn) {
            int size = mListener.size();
            for (int index = 0; index < size; index++) {
                TimerInfo info = mListener.get(index);
                if (info.mId == id) {
                    info.mCancel = true;
                    mListener.remove(index);
                    break;
                }
            }
            /** 检测是否取消定时器 */
            if (mListener.isEmpty()) {
                if (null != mTimer) {
                    AppDebug.i(TAG, "cancel timer");
                    mTimer.cancel();
                    mTimer = null;

                }
            }
        }
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Runnable mMainRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            onTimer();
        }
    };

    private void onTimer() {

        /** 主线程 */
        mTmpListener.clear();
        synchronized (mSyn) {
            mTmpListener.addAll(mListener);
        }

        int size = mTmpListener.size();
        TimerInfo info;
        for (int index = size - 1; index >= 0; index--) {
            info = mTmpListener.get(index);
            TimerListener l = info.mListener.get();

            if (info.mCancel || null == l) {
                cancelTimer(info.mId);
                AppDebug.i(TAG, "cancel timer id: " + info.mId
                        + " and lister: " + l);
            } else {
                /** 计算时间 */
                long curTime = SystemClock.elapsedRealtime();
                curTime += mServerRef - mLocalRef;
                long remaningTime = info.mReminderTime - curTime;
                if (remaningTime <= 0) {
                    /** 需要清除 */
                    cancelTimer(info.mId);

                    l.onEndTimer(info.mId, info.mUserData);
                    AppDebug.i(TAG, "timer out id: " + info.mId
                            + " and lister: " + l);
                } else {
                    l.onTimer(info.mId, remaningTime, info.mUserData);
                }
            }
        }
        mTmpListener.clear();
    }

    private static class TimerInfo {
        private WeakReference<TimerListener> mListener;
        private int mId;
        private Object mUserData;
        private long mReminderTime;
        private boolean mCancel = false;

        public TimerInfo(int id, long reminderTime, TimerListener listener,
                         Object userData) {
            mId = id;
            mListener = new WeakReference<TimerListener>(listener);
            mReminderTime = reminderTime;
            mUserData = userData;
        }
    }

    /*********************************************************************************/
    // 闹钟管理模块
    /*********************************************************************************/
    public final static String ACTION_ALARM_REMINDER = "com.yunos.flashsale.ALARM_REMINDER";
    public final static String ACTION_ALARM_CHECK = "com.yunos.flashsale.ALARM_CHECK";

    public final static String ALARM_PARAM_ID = "id";
    public final static String ALARM_PARAM_REMINDER_TIME = "reminder";

    /**
     * 当前需要定时
     */
    private MyConcernCache.MyconcernInfo mCurMyconcernInfo;

    /**
     * 定时闹钟, 5分钟循环
     */
    private Timer mAlarmReminderTimer;

    private void cancelAlarmReminder() {
        Timer timer = mAlarmReminderTimer;
        if (null != timer) {
            timer.cancel();
        }
        mAlarmReminderTimer = null;
    }

    private void startAlarmReminder(final MyConcernCache.MyconcernInfo info) {
        long curTime = getCurTime();
        /**提前0.1秒,防止与循环闹钟冲突*/
        long delay = info.mRemindTime - curTime - 1000;

        if (delay <= 0) {
            delay = 10;
        }
        //AppDebug.i(TAG, "set real alarm time: " + DateUtils.timestamp2String(info.mRemindTime));
        mAlarmReminderTimer = new Timer();
        mAlarmReminderTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                AppDebug.i(TAG, "real alarm time");
                onReminder(mContext, info);
            }
        }, delay);
    }

    public void onReminder(Context context, MyConcernCache.MyconcernInfo info) {
        //去掉提醒服务
//		Intent it = new Intent(TimerManager.ACTION_ALARM_REMINDER);
//		
//		it.setClass(mContext, ReminderService.class);
//		it.putExtra(TimerManager.ALARM_PARAM_ID, info.mItemId);
//		it.putExtra(TimerManager.ALARM_PARAM_REMINDER_TIME, info.mRemindTime);
//		
//		context.startService(it);
    }


    /**
     * 当发生变化后需要重新检测闹
     */
    public void onAlarmCheck(Context context) {
        //去掉提醒服务
//		Intent it = new Intent(TimerManager.ACTION_ALARM_CHECK);
//		it.setClass(mContext, ReminderService.class);
//		context.startService(it);
    }

    public void startNextAlarm(long curTime, MyConcernCache.MyconcernInfo curInfo) {
        synchronized (TimerManager.class) {
            if (null == curInfo) {
                /**取消循环闹钟*/
                cancelAlarmReminder();
//				cancelAlarmReminderCheck();
                mCurMyconcernInfo = null;
//                AppDebug.i(
//                        TAG,
//                        "not need alaram:  real time = "
//                                + DateUtils.millisecond2String(System
//                                .currentTimeMillis())
//                                + " caculate time="
//                                + DateUtils.millisecond2String(curTime));
            } else {
//				startAlarmReminderCheck();
                if (null != mCurMyconcernInfo) {
                    /** 说明已经设置定时闹钟 */
                    if (TextUtils.equals(mCurMyconcernInfo.mItemId,
                            curInfo.mItemId)
                            && mCurMyconcernInfo.mRemindTime == curInfo.mRemindTime) {
//                            AppDebug.i(
//                                    TAG,
//                                    "no operation, same timer: itemId:"
//                                            + curInfo.mItemId
//                                            + " reminder time: "
//                                            + DateUtils
//                                            .millisecond2String(curInfo.mRemindTime)
//                                            + "real time = "
//                                            + DateUtils.millisecond2String(System
//                                            .currentTimeMillis())
//                                            + " caculate time="
//                                            + DateUtils
//                                            .millisecond2String(curTime));
                        return;
                    }
                }
                cancelAlarmReminder();

                /** 检测闹钟 */
                if (curInfo.mRemindTime <= curTime + INTERVAL_ALARAM_CHECK_TIME) {
                    /** 设置定时闹钟 */
                    mCurMyconcernInfo = curInfo;
                    startAlarmReminder(curInfo);
                } else {
//                    AppDebug.i(
//                            TAG,
//                            "no need setting timer,  cur time: "
//                                    + DateUtils.timestamp2String(curTime)
//                                    + " real time: "
//                                    + DateUtils.timestamp2String(System
//                                    .currentTimeMillis())
//                                    + " itemId: "
//                                    + curInfo.mItemId
//                                    + " cur remind time:"
//                                    + DateUtils
//                                    .timestamp2String(curInfo.mRemindTime));
                }

            }
        }

    }
}

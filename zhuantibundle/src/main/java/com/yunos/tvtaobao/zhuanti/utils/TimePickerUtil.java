package com.yunos.tvtaobao.zhuanti.utils;

import android.os.Handler;

/**
 * Created by chenjiajuan on 17/4/21.
 *
 * 倒计时定时器
 */

public abstract class TimePickerUtil {

    private Thread mThread;
    private Handler mHandler;
    private long mPostSpan;

    public TimePickerUtil(long span) {
        mThread = Thread.currentThread();
        mHandler = new Handler();
        mPostSpan = span;
    }

    public TimePickerUtil() {
        this(10);
    }

    private long mTargetRunTime;

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if (currentTime < mTargetRunTime) {
                long delay = mPostSpan + (mTargetRunTime - currentTime);
                mHandler.postDelayed(mRunnable, delay);
                mTargetRunTime = System.currentTimeMillis() + delay;
            } else if (currentTime > mTargetRunTime) {
                long delay = mPostSpan + (mTargetRunTime - currentTime);
                if (delay < 1)
                    delay = 1;
                mHandler.postDelayed(mRunnable, delay);
                mTargetRunTime = System.currentTimeMillis() + delay;
            } else {
                mHandler.postDelayed(mRunnable, mPostSpan);
                mTargetRunTime = System.currentTimeMillis() + mPostSpan;
            }
            doOnUIThread();
        }

    };

    public abstract void doOnUIThread();

    private void checkThread() {
        Thread currentThread = Thread.currentThread();
        if (currentThread != mThread) {
            throw new RuntimeException("Call from different thread.");
        }
    }

    public void start(boolean startOnce) {
        checkThread();
        mHandler.removeCallbacks(mRunnable);
        if (startOnce) {
            mHandler.postDelayed(mRunnable, 0);
            mTargetRunTime = System.currentTimeMillis() + 0;
        } else {
            mHandler.postDelayed(mRunnable, mPostSpan);
            mTargetRunTime = System.currentTimeMillis() + mPostSpan;
        }
    }

    public void stop() {
        checkThread();
        mHandler.removeCallbacks(mRunnable);
    }

}
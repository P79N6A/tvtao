
package com.yunos.tvtaobao.flashsale.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public abstract class CountDownTimerCustom {

    /**
     * Millis since epoch when alarm should stop.
     */
    private volatile long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    /**
     * @param millisInFuture The number of millis in the future from the call to
     *            {@link #start()} until the countdown is done and
     *            {@link #onFinish()} is called.
     * @param countDownInterval The interval along the way to receive
     *            {@link #onTick(long)} callbacks.
     */
    public CountDownTimerCustom(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     */
    public final void cancel() {
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    public synchronized final CountDownTimerCustom start() {
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    public synchronized final CountDownTimerCustom pause() {
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        cancel();
        onAnimtionPause();
        return this;
    }

    /**
     * Callback fired on regular interval.
     * 
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    /**
     * Callback fired when the time is pause.
     */
    public abstract void onAnimtionPause();

    private static final int MSG = 1;

    // handles counting down
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (CountDownTimerCustom.this) {
                mMillisInFuture = mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (mMillisInFuture <= 0) {
                    onFinish();
                } else if (mMillisInFuture < mCountdownInterval) {
                    // no tick, just delay until done
                    sendMessageDelayed(obtainMessage(MSG), mMillisInFuture);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(mMillisInFuture);

                    // take into account user's onTick taking time to execute
                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0)
                        delay += mCountdownInterval;

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };
}

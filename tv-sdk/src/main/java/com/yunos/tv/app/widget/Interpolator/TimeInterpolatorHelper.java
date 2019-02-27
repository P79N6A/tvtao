package com.yunos.tv.app.widget.Interpolator;

import android.os.SystemClock;

/**
 * 时间加速器帮助类 初始状态和结束状态参数为时间 根据时间计算当前的位置
 * 
 * @author zhangle
 * 
 */
public class TimeInterpolatorHelper extends AbsInterpolatorHelper {
    
    private static final String TAG = "TimeInterpolator";

    private long mStartTime = 0;

    public TimeInterpolatorHelper() {

    }

    /**
     * 
     * @param interpolator
     *            加速器
     */
    public TimeInterpolatorHelper(TweenInterpolator interpolator) {
        super(interpolator);
    }

    public boolean track() {
        // 判断动画是否在上一次track的时候已经完成
        if (isRunning()) {
            boolean isTrack = mCurrent < mInterpolator.getDuration();
//            Log.d(TAG,"track isTrack:" + isTrack + ",duraction:" + mInterpolator.getDuration());
            if (isTrack) {
                mCurrent = (int) (SystemClock.elapsedRealtime() - mStartTime);
                //还在delay中,没有开始
                if(mCurrent < 0){
                    mCurrent = 0;
                }
                if (mCurrent >= mInterpolator.getDuration()) {
                    mCurrent = mInterpolator.getDuration();
                }
//                Log.d(TAG,"currentTime4:" + mCurrentTime);
            } else {
                mStatus = (mStatus == STATUS_FORWARDING) ? STATUS_FORWARD_FINISHED : STATUS_REVERSE_FINISHED;
            }
            return isTrack;
        } else {
            return false;
        }
    }

    /**
     * 前进变化
     * 
     * @return 如果当前状态处于向结束状态变化中或者本身就处于结束状态,则返回false.
     */
    public synchronized boolean start() {
//        Log.d(TAG,"start");
        if (mStatus != STATUS_FORWARD_FINISHED && mStatus != STATUS_FORWARDING) {
            mStartTime = SystemClock.elapsedRealtime();
            if (mStatus == STATUS_REVERSEING) { // 正在逆向变化中
                // 计算当前后退状态换算成前进过程需要经过多久
                int passed = mInterpolator.getDuration() - mCurrent;
                mStartTime -= passed;
            } else {
                // 如果动画已经结束,则增加延迟时间
                mStartTime += mForwardDelay;
            }
            mStatus = STATUS_FORWARDING;
            mCurrent = 0;
            return true;
        }
        return false;
    }

    /**
     * 反向变化
     * 
     * @return 如果当前状态处于向初始状态变化中或者本身就处于初始状态,则返回false.
     */
    public synchronized boolean reverse() {
//        Log.d(TAG,"reverse");
        if (mStatus != STATUS_REVERSEING && mStatus != STATUS_REVERSE_FINISHED ) {
            mStartTime = SystemClock.elapsedRealtime();
            if (mStatus == STATUS_FORWARDING) { // 正在前进中
                // 计算当前前进状态换算成后退过程需要经过多久
                int passed = mInterpolator.getDuration() - mCurrent;
                mStartTime -= passed;
            } else {
                mStartTime += mReverseDelay;
            }
            mStatus = STATUS_REVERSEING;
            mCurrent = 0;
            return true;
        }
        return false;
    }

}

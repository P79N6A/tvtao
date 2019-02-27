package com.yunos.tv.app.widget.Interpolator;

import android.util.Log;


/**
 * 帧加速器帮助类
 * @author zhangle
 *
 */
public class FrameInterpolatorHelper extends AbsInterpolatorHelper {
    private static final String TAG = "FrameInterpolator";
    
    private int mStartFrame = 0;
    private int mTotalFrames = 0;
    
    public FrameInterpolatorHelper(){
        
    }
    
    /**
     * 
     * @param interpolator
     *            加速器
     */
    public FrameInterpolatorHelper(TweenInterpolator interpolator){
        super(interpolator);
    }
    
    @Override
    public boolean track() {
        // 判断动画是否在上一次track的时候已经完成
        if (isRunning()) {
            mTotalFrames++;
            boolean isTrack = mCurrent < mInterpolator.getDuration();
            if (isTrack) {
                mCurrent = mTotalFrames - mStartFrame;
                //还在delay中,没有开始
                if(mCurrent < 0){
                    mCurrent = 0;
                }
                if (mCurrent >= mInterpolator.getDuration()) {
                    mCurrent = mInterpolator.getDuration();
                }
//                Log.d(TAG,"currentTime4:" + mCurrentTime);
            } else {
                //上一次track的时候已经完成动画了,所以直接打印mTotalFrames时多了最后一次isTrack为false的计数,实际的帧数要减去1
                Log.d(TAG,"totalFrames:" + (mTotalFrames - 1) + ",mStartFrame:" + mStartFrame);
                mStatus = (mStatus == STATUS_FORWARDING) ? STATUS_FORWARD_FINISHED : STATUS_REVERSE_FINISHED;
            }
            return isTrack;
        } else {
            return false;
        }
    }

    @Override
    public synchronized boolean start() {
//        Log.d(TAG,"start");
        if (mStatus != STATUS_FORWARD_FINISHED && mStatus != STATUS_FORWARDING) {
            mStartFrame = 0;
            if (mStatus == STATUS_REVERSEING) { // 正在逆向变化中
                // 计算当前后退状态换算成前进过程需要经过多少帧
                int passed = mInterpolator.getDuration() - mCurrent;
                mStartFrame = -passed;
            } else {
                mStartFrame = mForwardDelay;
            }
            mStatus = STATUS_FORWARDING;
            mCurrent = 0;
            mTotalFrames = 0;
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean reverse() {
//        Log.d(TAG,"reverse");
        if (mStatus != STATUS_REVERSEING && mStatus != STATUS_REVERSE_FINISHED ) {
            if (mStatus == STATUS_FORWARDING) { // 正在前进中
                // 计算当前前进状态换算成后退过程需要经过多久
                int passed = mInterpolator.getDuration() - mCurrent;
                mStartFrame = -passed;
            } else {
                mStartFrame = mReverseDelay;
            }
            mStatus = STATUS_REVERSEING;
            mCurrent = 0;
            mTotalFrames = 0;
            return true;
        }
        return false;
    }

}

package com.yunos.tv.app.widget.Interpolator;


/**
 * 加速器虚基类,时间加速器和帧加速器继承
 * 
 * @author zhangle
 * 
 */
public abstract class AbsInterpolatorHelper {
    
    private static final String TAG = "InterpolatorUtil";

    /**
     * 从初始状态到结束状态变化中
     */
    public static final int STATUS_FORWARDING = 1;

    /**
     * 前进变化完成,当前处于结束状态
     */
    public static final int STATUS_FORWARD_FINISHED = 2;

    /**
     * 从结束状态到初始状态变化中
     */
    public static final int STATUS_REVERSEING = 3;

    /**
     * 后退变化完成,当前处于初始状态
     */
    public static final int STATUS_REVERSE_FINISHED = 4;

    protected int mStatus = STATUS_REVERSE_FINISHED;
    protected int mForwardDelay;  //前进延迟的时间或帧数
    protected int mReverseDelay;    //后退延迟的时间或者帧数
    protected int mCurrent = 0; //当前值 时间或者帧数

    protected TweenInterpolator mInterpolator;

    public AbsInterpolatorHelper() {

    }

    /**
     * 
     * @param interpolator
     *            加速器
     */
    public AbsInterpolatorHelper(TweenInterpolator interpolator) {
        setInterpolator(interpolator);
    }

    /**
     * 设置加速器
     * 
     * @param interpolator
     *            加速器
     */
    public void setInterpolator(TweenInterpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * 获取当前的加速器
     * 
     * @return
     */
    public TweenInterpolator getInterpolator() {
        return mInterpolator;
    }

    /**
     * 设置前进启动延迟时间或者帧数
     * 
     * @param delay
     */
    public void setForwardDelay(int delay) {
        mForwardDelay = delay;
    }

    /**
     * 前进延迟时间或者帧数
     * 
     * @return
     */
    public int getForwardDelay() {
        return mForwardDelay;
    }

    /**
     * 设置后退启动延迟时间或者帧数
     * 
     * @param delay
     */
    public void setReverseDelay(int delay) {
        mReverseDelay = delay;
    }

    /**
     * 获取后退延迟时间或者帧数
     * 
     * @return
     */
    public int getReverseDelay() {
        return mReverseDelay;
    }

    /**
     * 获取当前状态
     * 
     * @return
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * 是否在动画中
     * 
     * @return
     */
    public boolean isRunning() {
        return mStatus == STATUS_FORWARDING || mStatus == STATUS_REVERSEING;
    }

    /**
     * 步进 每次获取值前先调用一下该方法,更新当前的状态
     * 
     * @return track是否成功,如果已经结束,则返回false.
     */
    public abstract boolean track();

    /**
     * 前进变化
     * 
     * @return 如果当前状态处于向结束状态变化中或者本身就处于结束状态,则返回false.
     */
    public abstract boolean start();

    /**
     * 反向变化
     * 
     * @return 如果当前状态处于向初始状态变化中或者本身就处于初始状态,则返回false.
     */
    public abstract boolean reverse();

    /**
     * 获取当前状态的值
     * 
     * @return
     */
    public float getCurrent() {
//        Log.d(TAG,"getValue currentTime:" + mCurrentTime + ",value:" + ((mStatus == STATUS_REVERSEING || mStatus == STATUS_REVERSE_FINISHED) ? mInterpolator.getReverseValue(mCurrentTime) : mInterpolator.getValue(mCurrentTime)) + ",reverse:" + (mStatus == STATUS_REVERSEING));
        return (mStatus == STATUS_FORWARDING || mStatus == STATUS_FORWARD_FINISHED) ? mInterpolator.getValue(mCurrent) : mInterpolator.getReverseValue(mCurrent);
    }
    
    /**
     * 根据当前过程获取目标值<br>
     * 如果在前进状态,则返回目标值<br>
     * 如果在后退状态,则返回起始值<br>
     * @return
     */
    public float getStatusTarget(){
        return (mStatus == STATUS_FORWARDING || mStatus == STATUS_FORWARD_FINISHED) ?  mInterpolator.getTarget() : mInterpolator.getStart();
    }
}

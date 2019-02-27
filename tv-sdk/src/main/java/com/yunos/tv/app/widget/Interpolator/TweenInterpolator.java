package com.yunos.tv.app.widget.Interpolator;

import android.view.animation.Interpolator;


public abstract class TweenInterpolator implements Interpolator{
    private static final String TAG = "TweenInterpolator";
    private float mStart;
    private float mTarget;
    private int mDuration;
    private float mChange;  //变化量

    public TweenInterpolator() {

    }

    public TweenInterpolator(float start, float target, int duration) {
        this.mStart = start;
        this.mTarget = target;
        this.mDuration = duration;
        
        this.mChange = mTarget - mStart;
    }
    
    /**
     * 设置持续时间或者帧数
     * @param duration
     */
    public void setDuration(int duration){
        this.mDuration = duration;
    }
    
    /**
     * 获取加速器持续时间或者帧数
     */
    public int getDuration(){
        return this.mDuration;
    }
    
    /**
     * 起始值
     * @return
     */
    public float getStart(){
        return this.mStart;
    }
    
    /**
     * 目标值
     * @return
     */
    public float getTarget(){
        return this.mTarget;
    }
    
    /**
     * 设置起始值和目标值
     * @param start
     * @param end
     */
    public void setStartAndTarget(float start,float target){
        this.mStart = start;
        this.mTarget = target;
        this.mChange = mTarget - mStart;
    }
    
    @Override
    public float getInterpolation(float input) {
//        return getValue((int)(input * mDuration));  //返回实际的值
        return interpolation(input, 0, 1, 1);   //返回0~1的值
    }
    
    /**
     * 根据当前时间或者帧数计算值
     * 
     * @param current 当前时间或者帧数
     * @return
     */
    public float getValue(int current) {
        return interpolation(current, mStart, mChange, mDuration);
    }
    
    /**
     * 按原来的缓动轨迹倒回去
     * @param current 倒的时间或者帧数
     * @return
     */
    public float getReverseValue(int current){
//        Log.d(TAG,"getReverseValue:" + (mDuration - current) + " " + mStart + " " +  mChange + " " + mDuration + ",result:" + interpolation(mDuration - current, mStart, mChange, mDuration));
        return interpolation(mDuration - current, mStart, mChange, mDuration);
    }
    
    /**
     * 缓动算法
     * 根据时间和目标状态计算当前状态
     * 
     * @param t
     *            当前时间或者帧数
     * @param b
     *            初始值
     * @param c
     *            变化量
     * @param d
     *            持续时间
     * @return
     */
    public abstract float interpolation(float t, float b, float c, float d);

}

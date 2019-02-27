package com.yunos.tv.app.widget;

import android.util.Log;

/**
 * 列表的连续滚动的效果，先进行匀速后进行减速
 * 如果正在运行连续启动动画，滚动的距离会进行叠加，
 * 当速度达到峰值后进入匀速，在最后减速阶段进入减速
 * @author tim
 */
public class ListLoopScroller {
	private final String TAG = "ListLoopScroller";
	private final boolean DEBUG = false;
	public final float DEFALUT_MAX_STEP = 300.0f;//默认单步最大的步长
	public final float SLOW_DOWN_RATIO = 3.0f;//减速单步跟正常单步的系数比
	public final float SLOW_DOWN_DISTANCE_RATIO = 4.0f;//减速距离的系数越大减速距离越短
	private int mStart;//开始位置
	private int mCurr;//当前位置
	private int mFinal;//本次动画的结束位置
	private boolean mFinished = true;//是否已经完成
	private float mMaxStep = DEFALUT_MAX_STEP;//单步最大的步长，即为滚动速度的峰值
	private int mCurrFrameIndex;//当前匀速的帧，减速不计入在内
	private int mTotalFrameCount;//总共匀速帧数，因为最后有减速的过程所以该值会小于实现行动的帧数
	private float mStep;//单步的步长，可以理解为速度
	private int mSlowDownIndex;//当前减速的帧数
	private float mSlowDownFrameCount;//减速的总帧数
	private int mSlowDownStart;//减速的开始位置
	private float mSlowDownDistance;//减速的距离
	private float mSlowDownStep;//减速的初始步长
	private long mStartTime;
	private float mSlowDownRatio = SLOW_DOWN_DISTANCE_RATIO;//减速距离的系数越大减速距离越短
	/**
	 * 构造方法
	 */
	public ListLoopScroller(){
	}

	
	/**
	 * 当前的位置
	 * @return
	 */
	public int getCurr(){
		return mCurr;
	}
	
	
	/**
	 * 取得最终的位置
	 * @return
	 */
	public int getFinal(){
		return mFinal;
	}
	
	
	/**
	 * 取得开始的值
	 * @return
	 */
	public int getStart(){
		return mStart;
	}
	
	
	/**
	 * 是否已经结束
	 * @return
	 */
	public boolean isFinished(){
		return mFinished;
	}
	
	/**
	 * 设置峰值的步长
	 */
	public void setMaxStep(float maxStep){
		mMaxStep = maxStep;
	}
	
	
	/**
	 * 减速距离比，越大减速距离越短，也就是减速越快
	 * @param ratio
	 */
	public void setSlowDownRatio(float ratio){
		if(!mFinished){
			throw new IllegalStateException(
                    "setSlowDownRatio before start"); 
		}
		if(mSlowDownRatio > 1.0f){
			mSlowDownRatio = ratio;
		}
		else{
			Log.e(TAG, "setSlowDownRatio value must > 1.0");
		}
	}
	
	
	/**
	 * 开始滚动，如果动画还在运行中会将滚动的距离叠加，当速度达到峰值作匀速
	 * @param start
	 * @param distance
	 * @param frameCount
	 */
	public void startScroll(int start, int distance, int frameCount){
		distance = mFinal - mCurr + distance;
		mTotalFrameCount = frameCount;
		//计算单步的步长
		mStep = (float)distance / (float)mTotalFrameCount;
		//向下
		//如果步长大于最大值，就取步长的最大值
		if(mStep > mMaxStep){
			mStep = mMaxStep;
			//通过步长反算需要的帧数
			mTotalFrameCount = (int)(distance / mStep);
			if(DEBUG){
			    Log.i(TAG, "startScroll change mTotalFrameCount="+mTotalFrameCount);
			}
		}
		//向上
		else if(mStep < -mMaxStep){
			mStep = -mMaxStep;
			//通过步长反算需要的帧数
			mTotalFrameCount = (int)(distance / mStep);
            if(DEBUG){
                Log.i(TAG, "startScroll change mTotalFrameCount="+mTotalFrameCount);
            }
		}
		//匀速的参数初始化
		mCurr = start;
		mStart = start;
		mFinal = mStart + distance;
		mFinished = false;
		mCurrFrameIndex = 0;
		//减速的参数初始化
		mSlowDownFrameCount = 0;
		mSlowDownStep = mStep / SLOW_DOWN_RATIO;
		computeSlowDownDistance();
        if(DEBUG){
            mStartTime = System.currentTimeMillis();
            Log.i(TAG, "startScroll mStep="+mStep+" mStart="+mStart+" mFinalY="+mFinal+" mTotalFrameCount="+mTotalFrameCount);
        }
	}

	
	/**
	 * 计算过程的值（先进行匀速再进行匀减速的过程）
	 * @return true动画未完成，否则完成
	 */
	public boolean computeScrollOffset(){
		//完成时直接返回
		if(mFinished){
			return false;
		}
		
		//当前的数帧大于总数
		if(mCurrFrameIndex >= mTotalFrameCount){
			finish();
			return false;
		}
		
		//减速计算过程
		if(mSlowDownFrameCount > 0){
			if(mSlowDownIndex > mSlowDownFrameCount){
				finish();
				return false;
			}
			mSlowDownIndex ++;
			if(mSlowDownIndex >= mSlowDownFrameCount){
				//最后一帧补齐,因为有误差所以最后一帧不进入计算
				mCurr = mFinal;
			}
			else{
				//匀减速直线运动 S=vt-（at^2）/2; v:初始速度 a: v/t(初始速度/总时间);
				mCurr =  mSlowDownStart + (int)(mSlowDownStep * mSlowDownIndex - (mSlowDownIndex * mSlowDownIndex * mSlowDownStep) / (2 * mSlowDownFrameCount));
			}
			if(DEBUG){
			Log.i(TAG, "computeScrollOffset slow down mTotalFrameCount="+mTotalFrameCount+
					" mCurrFrameIndex="+mCurrFrameIndex+" mCurr="+mCurr+" mSlowDownIndex="+mSlowDownIndex+ " mStep="+mStep);
			}
		}
		//匀速计算过程
		else{
			float p = (float)(mCurrFrameIndex + 1) / (float)mTotalFrameCount;
			//当前帧的动画位置
			mCurr = (int)(mStart + (mFinal - mStart) * p);
			mCurrFrameIndex ++;
			
			//判断是否进入减速的动画
			int leftDistance = mFinal - mCurr;
			if(leftDistance < 0){
				leftDistance = -leftDistance;
			}
			if(leftDistance < mSlowDownDistance){
				setSlowDown(leftDistance);
			}
			else{
				resetSlowDown();
			}
			if(DEBUG){
				Log.i(TAG, "computeScrollOffset mCurrFrameIndex="+mCurrFrameIndex+
					" mTotalFrameCount="+mTotalFrameCount+" currY="+mCurr+" mFinalY="+mFinal+ " mSlowDownFrameCount="+mSlowDownFrameCount);
			}
		}
		return true;
	}
	
	
	/**
	 * 完成动画
	 */
	public void finish(){
		if(mFinished == false){
            if(DEBUG){
                Log.i(TAG, "finish spend="+(System.currentTimeMillis() - mStartTime)+" frameCount="+(int)(mTotalFrameCount + mSlowDownFrameCount)
					+" mCurr="+mCurr+" final="+mFinal);
            }
			mCurr = mFinal;
			mCurrFrameIndex = mTotalFrameCount;
			mSlowDownFrameCount = 0;
			mFinished = true;
		}
	}
	
	/**
	 * 重置减速相关的参数
	 */
	private void resetSlowDown(){
		mSlowDownStart = 0;
		mSlowDownFrameCount = 0;
		mSlowDownIndex = 0;
	}
	
	
	/**
	 * 设置进入减速状态的相关参数
	 * @param distance 减速的距离
	 */
	private void setSlowDown(int distance){
		mSlowDownStart = mCurr;
		mSlowDownIndex = 0;
		//取得减速动画的帧数，因为是以之前匀速的速度做匀减速运算，所以帧数要比匀速的要多
		mSlowDownFrameCount = 2 * distance / mSlowDownStep;
		//保证为正数
		if(mSlowDownFrameCount < 0){
			mSlowDownFrameCount = -mSlowDownFrameCount;
		}
	}
	
	
	/**
	 * 计算减速的距离
	 */
	private void computeSlowDownDistance(){
		int distance = (mFinal - mStart) / 2;
		if(distance < 0){
			distance = -distance;
		}
		mSlowDownDistance = mStep * mStep / mSlowDownRatio;
		if(mSlowDownDistance > distance){
			Log.w(TAG, "computeSlowDownDistance mSlowDownDistance too big="+mSlowDownDistance+" distance="+distance+" mStep="+ mStep);
			mSlowDownDistance = distance;
		}
		if(DEBUG){
			Log.i(TAG, "computeSlowDownDistance mSlowDownDistance="+mSlowDownDistance);
		}
	}
	
	
}

package com.yunos.tv.app.widget;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


@SuppressLint("DrawAllocation")
public class SphereProgressEffect {   
	class DotPoint{
		float curAlpha;
		float x;
		float y;		
		int index;
		int curFrameIndex;
		
		public void updateProgress(float deltaProgress){			
			curAlpha += deltaProgress;
			if(curAlpha > 1.0f){
				curAlpha = 1.0f;
			}
			curFrameIndex ++;
		}
	}
	
	private float mDotRadius;
	private float mRotateRadius;
	

	private DotPoint mDotPoint[] = new DotPoint[8];
	
	private float mStartAlpha = 0.95f;
	private float mEndAlpha   = 0.1f;
	private float mNormallStepAlpha;
	
	
	private Paint mPaint = new Paint();
	private int mDrawColor = Color.argb(255, 78, 196, 255);    
    
    static final int mPathFrameCount = 30; 
    
	
    private static final boolean mTestMod = false;
    private int tempSpeed = 0;
	
    private float mTotalProgress = 0.0f;
    private float mRotateDegrees = 0.0f;
	
	public SphereProgressEffect(float rotateRadius, float dotRadius){		
		mRotateRadius = rotateRadius;
		
		mDotRadius = dotRadius;
		
		intialDotCircle();
	}
	
	
	private void intialDotCircle(){
		int len = mDotPoint.length;
		float stepAngle = (float)360.0f/len;
		
		double arc;
		float sinValue;
		float cosValue;
		
		mNormallStepAlpha = (mEndAlpha - mStartAlpha)/(mDotPoint.length - 2);
		
		float largerScaleRadius = 1.0f;
		if(mTestMod){
			largerScaleRadius = 1.5f;
		}
		
		
		for(int i = 0; i < len; i++){
			mDotPoint[i] = new DotPoint();			
			if(i != 0){
				mDotPoint[i].curAlpha = mStartAlpha + (i - 1)*mNormallStepAlpha;
			}else{
				mDotPoint[i].curAlpha = 1.0f;
			}
			
			mDotPoint[i].curFrameIndex = 240 - (i + 1)*mPathFrameCount;
			
			mDotPoint[i].index = i;
			
			arc = Math.PI*(stepAngle*i)/180;
			
			cosValue = (float)Math.cos(arc);
			sinValue = (float)Math.sin(arc);
			
			mDotPoint[i].x = mRotateRadius*cosValue*largerScaleRadius;
			mDotPoint[i].y = mRotateRadius*sinValue*largerScaleRadius;			
		}
	}
	
    
    
	public void setColor(int color){
		mDrawColor = color;
	}
	
	private void setRotateDegrees(float degrees){
		mRotateDegrees = degrees;
	}

	
	public void updateRotateProgress(float progress){		
		setTotalProgress(progress);
				
		if(mTestMod){
			setRotateDegrees(mTotalProgress*2*360/(mDotPoint.length*8));
		}else{
			setRotateDegrees(mTotalProgress*2.5f*360/(mDotPoint.length));
		}
		
		tempSpeed++;
		
		if(tempSpeed%8 != 0){
			if(mTestMod){
				return;
			}
		}

		
		for(int i = 0; i < mDotPoint.length; i++){
			//its last point
			if(mDotPoint[i].curFrameIndex >= (mDotPoint.length - 1)*mPathFrameCount){				
				float delta = (1.0f - mEndAlpha)/mPathFrameCount;
				mDotPoint[i].updateProgress(-delta);		
			}else{
				mDotPoint[i].updateProgress((-mNormallStepAlpha)/mPathFrameCount);
			}
			
			//以循环的方式来减少alpha值的误差
			if(mDotPoint[i].curFrameIndex >= mPathFrameCount*mDotPoint.length){
				mDotPoint[i].curAlpha = mEndAlpha;	
				
				//circle 周期
				mDotPoint[i].curFrameIndex -= mPathFrameCount*mDotPoint.length;
			}
		}		
	}
	
	private void setTotalProgress(float progress){
		mTotalProgress = progress;
	}
	
	private void drawRevolutionNormalCircle(Canvas canvas, float centerX, float centerY){
		canvas.save();
		
		canvas.rotate(mRotateDegrees, centerX, centerY); 
		
		canvas.translate(centerX, centerY);		
		
		
    	
    	for(int i = 0; i < mDotPoint.length; i++){
    		int alpha = (int)(mDotPoint[i].curAlpha*255);
    		if(alpha > 255){
    			alpha = 255;
    		}

    		mPaint.setColor(mDrawColor);
    		mPaint.setAlpha(alpha);    		
			mPaint.setAntiAlias(true);
			
			canvas.drawCircle(mDotPoint[i].x, mDotPoint[i].y, mDotRadius, mPaint);
    	}
    	
    	canvas.restore();
	}
	
	
    public void drawCircle(Canvas canvas, float centerX, float centerY){      

    	//canvas.drawARGB(255, 0, 0, 0);
    	   	
    	drawRevolutionNormalCircle(canvas, centerX, centerY);    	
    }
}  


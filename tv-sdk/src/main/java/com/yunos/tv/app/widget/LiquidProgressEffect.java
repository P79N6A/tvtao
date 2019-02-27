package com.yunos.tv.app.widget;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;


@SuppressLint("DrawAllocation")
public class LiquidProgressEffect {   
	class DotPoint{
		float curAlpha;
		float x;
		float y;		
		int index;
		int curFrameIndex;
		
		public void updateProgress(float deltaProgress){			
			curAlpha += deltaProgress;
			curFrameIndex ++;
		}
	}
	
	private float mDotRadius;
	private float mRotateRadius;
	

	private DotPoint mDotPoint[] = new DotPoint[8];
	
	private float mStartAlpha = 0.95f;
	private float mEndAlpha   = 0.1f;
	private float mNormallStepAlpha;
    
	private int mPathOffestAngle = 90;
	
	
	private Paint mPaint = new Paint();
	private int mDrawColor = Color.argb(255, 78, 196, 255);    
    
	private ArrayList<HighPrecisionPathInfo> mPathArray;
    static final int mPathFrameCount = 30; 
    
    //this datat will be reset when initial
    private int mTailAlpha[] = {
    					255, 255,255,255,255,
    					255,255,255,255,255,
    					255,255,255,255,255,//all 255
    					255,255,255,255,255,//all 255 link together
    					255,255,255,220,180,
    					150,120,80,70,60};
    private int mHeadAlpha[] = {
    					255, 255,255,255,255,
						255,255,255,255,255,
						255,255,255,255,255,//all 255
						255,255,255,255,255,//all 255 link together
						255,255,255,220,180,
						150,120,80,70,60};
    
	
    private static final boolean mTestMod = false;
    private int tempSpeed = 0;
	
    private int mTotalStep = 0;	
    private float mTotalProgress = 0.0f;
    private float mRotateDegrees = 0.0f;
	public LiquidProgressEffect(float rotateRadius, float dotRadius){		
		mRotateRadius = rotateRadius;
		
		mDotRadius = dotRadius;
		
		intialDotCircle();
		
		intitalHeadAndTailAlpha();
		
		Log.v("LiquidProgressEffect", "mDrawColor = " + mDrawColor);
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
	
    private void intitalHeadAndTailAlpha(){
    	int linkStart = 10;
    	int linkEnd   = 21;
    	int count;
    	
    	//link together before
    	count = linkStart;
    	for(int i = 0; i < count; i++){
    		mHeadAlpha[i] = 255;
    		mTailAlpha[i] = (int)(255*(mStartAlpha + (1.0f - mStartAlpha)*i/count));  //start -- 1.0f  
    	}
    	
    	//link together mid
    	count = linkEnd - linkStart;    	
    	for(int i = 0; i < count; i++){
    		mHeadAlpha[i + linkStart] = 255;
    		mTailAlpha[i + linkStart] = 255;    		
    	}
    
       	//link together after
    	count = mPathFrameCount - linkEnd;    	
    	for(int i = 0; i < count; i++){
    		mHeadAlpha[i + linkEnd] = (int)(255*(1.0f + (mEndAlpha  - 1.0f)*i/count));//
    		mTailAlpha[i + linkEnd] = 255;    		
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
		
		
		//water step
		mTotalStep++;
		
		
		for(int i = 0; i < mDotPoint.length; i++){
			mDotPoint[i].updateProgress((-mNormallStepAlpha)/mPathFrameCount);				
			
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
			
			if(mDotPoint[i].curFrameIndex <= (mDotPoint.length - 2)*mPathFrameCount){
				canvas.drawCircle(mDotPoint[i].x, mDotPoint[i].y, mDotRadius, mPaint);
			}
    	}
    	
    	canvas.restore();
	}
	
	private void drawWaterCircle(Canvas canvas, float centerX, float centerY){
        if(mPathArray != null && mPathArray.size() >= 1){
        	int index = mTotalStep%mPathFrameCount;
        	int waterStepIndex = mTotalStep/mPathFrameCount;
        	
        	if(index < 0){
        		index = 0;
        	}
        	
        	if(index >= mPathArray.size()){
        		index = mPathArray.size() - 1;
        	}
        	        	
        	
        	HighPrecisionPathInfo pathInfo = mPathArray.get(index);
	        if(pathInfo != null){
	        	//head
	        	canvas.save();
	        	
	        	canvas.rotate(mRotateDegrees, centerX, centerY);
	        	
	        	canvas.rotate(mPathOffestAngle + 45*waterStepIndex, centerX, centerY);    	
	        	

	        		        	
	        	//set head draw info
	        	mPaint.setColor(mDrawColor);
	        	mPaint.setAlpha(mHeadAlpha[index]);
	        	mPaint.setAntiAlias(true);
	        	
	        	if(pathInfo.mPathHead != null){
	        		if(pathInfo.mHeadPathIsCircle){
	    	        	//canvas.scale(2.0f, 2.0f);
	        			canvas.rotate(270, centerX, centerY);
	        			
	    	        	canvas.translate(centerX, centerY);

	        			canvas.drawCircle(mRotateRadius, 0, mDotRadius, mPaint);
	        		}else{
	    	        	canvas.translate(centerX, centerY);
	    	        	//canvas.scale(2.0f, 2.0f);
	        			canvas.drawPath(pathInfo.mPathHead, mPaint);
	        		}
	        	}
	        	
	        		    
	        	canvas.restore();
	        	
	        	
	        	//tail
	        	canvas.save();
	        	
	        	
	        	canvas.rotate(mRotateDegrees, centerX, centerY);
	        	
	        	canvas.rotate(mPathOffestAngle + 45*waterStepIndex, centerX, centerY);   
	        	
	        	//////////////////
	        	//set tail draw info
	        	mPaint.setColor(mDrawColor);
	        	mPaint.setAlpha(mTailAlpha[index]);
	        	mPaint.setAntiAlias(true);
	        	
	        	if(pathInfo.mPathTail != null){
	        		if(pathInfo.mTailPathIsCircle){
	       	        	//canvas.scale(2.0f, 2.0f);
	    	        	canvas.rotate(315, centerX, centerY);
	    	        			
	    	        	canvas.translate(centerX, centerY);
	 
	        			canvas.drawCircle(mRotateRadius, 0, mDotRadius, mPaint);
	        		}else{	        		
	    	        	canvas.translate(centerX, centerY);
	    	        	//canvas.scale(2.0f, 2.0f);
	        			canvas.drawPath(pathInfo.mPathTail, mPaint);
	        		}
	        	}
	        	
	        	canvas.restore();
	        }
        }
	}
	
    public void drawCircle(Canvas canvas, float centerX, float centerY){      

    	//canvas.drawARGB(255, 0, 0, 0);
    	   	
    	drawRevolutionNormalCircle(canvas, centerX, centerY);
    	
    	drawWaterCircle(canvas, centerX, centerY);    
    	
    }

    
    void setPathArray(ArrayList<HighPrecisionPathInfo> pathArray){
    	mPathArray = pathArray;
    }
}  


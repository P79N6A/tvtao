package com.yunos.tv.lib;

import java.util.ArrayList;


public class PosInfo{	
	public static final int MOVE_INVALID = -1;
	public static final int MOVE_LEFT  = 0;
	public static final int MOVE_RIGHT = 1;
	
	ArrayList<TransInfoKey> mList;
	
	public PosInfo(){			
		mList = new ArrayList<TransInfoKey>();
	}
	
	public PosInfo(float X, float Y, float ScaleX, float ScaleY, float Alpha, float key){
		mList = new ArrayList<TransInfoKey>();
		mList.add( new TransInfoKey( X, Y, ScaleX, Y, Alpha, 0.0f ));	
	}
	
	public void addKeyFrame(TransInfoKey keyFrame){
		if(mList == null){
			mList = new ArrayList<TransInfoKey>();
		}
		
		mList.add(keyFrame);		
	}
	
	//two point start and end
	public PosInfo(float startX, float endX, 
			       float startY, float endY, 
			       float startScaleX, float endScaleX,
			       float startScaleY, float endScaleY,
			       float startAlpha, float endAlpha){
		
		this(   new TransInfoKey( startX, startY, startScaleX, startScaleY, startAlpha, 0.0f ),
				new TransInfoKey(   endX,   endY,   endScaleX,   endScaleY,   endAlpha, 1.0f ));
	}
	
	public PosInfo(TransInfoKey...key){
		mList = new ArrayList<TransInfoKey>();
		int len = key.length;
		
		TransInfoKey [] keyArr = key.clone();
		for(int i = 0; i < len;i++){
			mList.add( keyArr[i] );	
		}		
	}
	
	public TransInfo evalute( float fraction ){
		if(fraction <= 0.0f){
			TransInfoKey a = mList.get(0);
			if(a != null){
				return a.mTransInfo;
			}
			
			return null;
		}else if(fraction >= 1.0f){
			int count = mList.size();
			if(count >= 1){
				TransInfoKey a = mList.get(count-1);
				if(a != null){
					return a.mTransInfo;
				}
				return null;
			}	

		}else{
			int count = mList.size();
			
			if(count == 2){
				return evalute( fraction, mList.get(0), mList.get(1));
			}else{//many key need special 
				TransInfoKey startTransKey;
				TransInfoKey endTransKey;
				float value;
				float startKey;
				float endKey;
				
				value = fraction;
				startTransKey = mList.get(0);
				startKey = startTransKey.mKey;
				
				for(int i = 1; i < count; i++ ){					
					endTransKey   = mList.get(i);				
					endKey   = endTransKey.mKey;
					
					if(value < endKey ){
						//change
						return evalute((value - startKey)/(endKey-startKey), startTransKey, endTransKey);
					}	
					
					startTransKey = endTransKey;
					startKey      = endKey;
				}
				
				//Log.v("PokerGroupView", "nullllllllllll pointer 4");
			}
		}
		
		return null;
	}
	
	private TransInfo evalute( float fraction, TransInfoKey startKey, TransInfoKey endKey ){
		float curX;
		float curY;
		float curScaleX;
		float curScaleY;
		float curAlpha;	
		
		TransInfo start = startKey.mTransInfo;
		TransInfo end   = endKey.mTransInfo;
		
		curX = start.x + fraction*( end.x - start.x);
		curY = start.y + fraction*( end.y - start.y);	
		
		curScaleX = start.scaleX + fraction*( end.scaleX - start.scaleX);
		curScaleY = start.scaleY + fraction*( end.scaleY - start.scaleY);
		
		curAlpha = start.alpha + fraction*( end.alpha - start.alpha);
		
		return new TransInfo(curX, curY, curScaleX, curScaleY, curAlpha);
	}
	
	public TransInfo getStartTrans(){
		if(mList != null){
			TransInfoKey keyFrameStart = mList.get(0);
			if(keyFrameStart != null){
				return keyFrameStart.mTransInfo;
			}
		}
		return null;
	}
	
	public TransInfo getEndTrans(){
		if(mList != null){
			int count = mList.size();
			TransInfoKey keyFrameEnd = mList.get(count - 1);
			if(keyFrameEnd != null){
				return keyFrameEnd.mTransInfo;
			}
		}
		return null;
	}
}

/*
public class PosInfo{
	public static final int MOVE_LEFT  = 0;
	public static final int MOVE_RIGHT = 1;
	

	public TransInfo mStart;
	public TransInfo mEnd;
	
	public PosInfo(){			
		this(0,0,
				0,0,
				0,0,
				0,0,
				0,0);
	}
	public PosInfo(float startX, float endX, 
			       float startY, float endY, 
			       float startScaleX, float endScaleX,
			       float startScaleY, float endScaleY,
			       float startAlpha, float endAlpha){
		
		mStart = new TransInfo( startX, startY, startScaleX, startScaleY, startAlpha);
		mEnd   = new TransInfo(endX,endY,endScaleX,endScaleY,endAlpha);
	}
	
	public PosInfo( TransInfo startValue, TransInfo endValue ){
		mStart = startValue;
		mEnd   = endValue;
	}
	
	public TransInfo evalute( float fraction ){
		float curX;
		float curY;
		float curScaleX;
		float curScaleY;
		float curAlpha;
		
		
		curX = mStart.x + fraction*( mEnd.x - mStart.x);
		curY = mStart.y + fraction*( mEnd.y - mStart.y);	
		
		curScaleX = mStart.scaleX + fraction*( mEnd.scaleX - mStart.scaleX);
		curScaleY = mStart.scaleY + fraction*( mEnd.scaleY - mStart.scaleY);
		
		curAlpha = mStart.alpha + fraction*( mEnd.alpha - mStart.alpha);
		
		return new TransInfo(curX, curY, curScaleX, curScaleY, curAlpha);
	}
	
	public TransInfo getStartTrans(){
		return mStart;
	}
	
	public TransInfo getEndTrans(){
		return mEnd;
	}
}
*/

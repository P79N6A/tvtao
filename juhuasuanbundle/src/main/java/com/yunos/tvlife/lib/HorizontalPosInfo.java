package com.yunos.tvlife.lib;

import android.graphics.Point;


public class HorizontalPosInfo{
	
	public final static int DEFAULT_IMAGE_WIDTH = 400;
	public final static int DEFAULT_IMAGE_HEIGHT = 600;
	
	private static class HSimpleTransInfo{
		public float X;
		public float Y;
		public float Scale;
		public float Alpha;
		
		public HSimpleTransInfo(float x, float y, float scale, float alpha){
			this.X     = x;
			this.Y     = y;
			this.Scale = scale;
			this.Alpha = alpha;
		}
		
		public HSimpleTransInfo(){
			this(0,0,0,0);
		}
	}
	
	//lower alpha value
	public static final float SELECTOR_ALPHA = 0.3f;
	
	//alpha change 1.0f -> SELECTOR_ALPHA time 
	public static final float SELECTOR_TIME_TO_DARK =  0.4f;
	
	//alpha change SELECTOR_ALPHA ->1.0f time 
	public static final float SELECTOR_TIME_FROM_DARK =  0.6f;
	

	//default canvas
	public static final int CANVAS_WIDTH  = 1620;
	public static final int CANVAS_HEIGHT = 1080;
	
	//selector rect position 
	//public static final float Y_POSITION = 450;//450//540//old y position
	public static final float X_CENTER = CANVAS_WIDTH/2;
	public static final float Y_CENTER = CANVAS_HEIGHT/2;
	
	private static BasePositionInfo mBaseInfo = new BasePositionInfo(0, 0, 0, 0, 0);
	
	public static int getCenterX(){
		return CANVAS_WIDTH/2;
	}
	
	
	public static int getCenterY(){
		return CANVAS_HEIGHT/2;
	}
	
	static private void getSelectPosInfo( HSimpleTransInfo info, int index ){
		//check input
		if(index + 1 < 0 || index + 1 >= mSelectPos.length ){
			//default
			info.X     = mSelectPos[4].X;
			info.Y     = mSelectPos[4].Y;
			info.Scale = mSelectPos[4].Scale;
			info.Alpha = mSelectPos[4].Alpha;
			
			return;
		}
		
		info.X     = mSelectPos[index+1].X;
		info.Y     = mSelectPos[index+1].Y;
		info.Scale = mSelectPos[index+1].Scale;
		info.Alpha = mSelectPos[index+1].Alpha;
		
	}
	
	static public  TransInfo getSelectPos(float originX, float originY, float destX, float destY, int nowSelectView, int targetImageWidth){
		float radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;
		
		HSimpleTransInfo info = new HSimpleTransInfo();
		
		getSelectPosInfo( info, nowSelectView );
		
		return new TransInfo( info.X*radio - originX + destX, info.Y*radio - originY + destY, info.Scale, info.Scale, info.Alpha);
	}
	
	
	static public PosInfo getAnimSelectPos(float originX, float originY, float destX, float destY, int moveDirection, int nowSelectView, int targetImageWidth ){		
		HSimpleTransInfo info = new HSimpleTransInfo();
		
		float startX = 0;
		float startScale = 0;
		float startAlpha = 1.0f;
		float startY = 0;
		
		float endX = 0;	
		float endY = 0;
		float endScale = 0;			
		float endAlpha = 1.0f;
		
		float radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;		

		
		int index;
		index = nowSelectView;
		
		
		if(moveDirection == PosInfo.MOVE_RIGHT){
			getSelectPosInfo(info, index);			
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;
			
			
			getSelectPosInfo(info, index + 1);
			endX = info.X;	
			endY = info.Y;
			endScale = info.Scale;			
			endAlpha = info.Alpha;
		}else{
			getSelectPosInfo(info, index);			
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;
			
			
			getSelectPosInfo(info, index - 1);
			endX = info.X;	
			endY = info.Y;
			endScale = info.Scale;			
			endAlpha = info.Alpha;
		}
		
		info = null;
		
		return SelectorAlphaChange(originX, originY, destX, destY, radio,
							startX, endX,
							startY, endY,
							startScale, endScale,
							startAlpha,endAlpha);
	}
	
	static public PosInfo getAnimStableSelectPos(float originX, float originY, float destX, float destY, int targetImageWidth){
		
		HSimpleTransInfo info = new HSimpleTransInfo();
		
		float startX = 0;
		float startY = 0;
		float startScale = 0;
		float startAlpha = 1.0f;
		
		float endX = 0;	
		float endY = 0;
		float endScale = 0;			
		float endAlpha = 1.0f;
		float radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;

		getSelectPosInfo(info, 3);			
		startX = info.X;
		startY = info.Y;
		startScale = info.Scale;
		startAlpha = info.Alpha;
		
		
		getSelectPosInfo(info, 3);
		endX = info.X;	
		endY = info.Y;
		endScale = info.Scale;			
		endAlpha = info.Alpha;
		
		info = null;
		
		return SelectorAlphaChange(originX, originY, destX, destY, radio,
									startX, endX,
									startY, endY,
									startScale, endScale,
									startAlpha,endAlpha
									);		
	}
	
	static private PosInfo SelectorAlphaChange(
				float originX, float originY, float destX, float destY, float radio,
				float startX, float endX, 
				float startY, float endY,
		        float startScale, float endScale,
		        float startAlpha, float endAlpha){
		
		TransInfoKey startKey = new TransInfoKey(startX*radio - originX + destX, 
												 startY*radio - originY + destY, 
										         startScale, 
										         startScale, 
										         startAlpha, 0.0f );
		
		TransInfoKey endKey = new TransInfoKey(endX*radio - originX + destX, 
											   endY*radio - originY + destY, 
									           endScale, 
									           endScale, 
									           endAlpha, 1.0f );
		
		
		
		TransInfoKey interPolateStart = new TransInfoKey(
				(startX + (endX-startX)*SELECTOR_TIME_TO_DARK)*radio - originX + destX, 
				startY*radio                                         - originY + destY, 
				startScale + SELECTOR_TIME_TO_DARK*(endScale - startScale), 
				startScale + SELECTOR_TIME_TO_DARK*(endScale - startScale), 
				SELECTOR_ALPHA, SELECTOR_TIME_TO_DARK );
		
		TransInfoKey interPolateEnd = new TransInfoKey(
				(startX + (SELECTOR_TIME_FROM_DARK)*(endX-startX))*radio  - originX + destX, 
				endY*radio                                                - originY + destY, 
				startScale + (SELECTOR_TIME_FROM_DARK)*(endScale - startScale), 
				startScale + (SELECTOR_TIME_FROM_DARK)*(endScale - startScale), 
		        SELECTOR_ALPHA, SELECTOR_TIME_FROM_DARK );
		
		return new PosInfo(startKey, interPolateStart, interPolateEnd, endKey);		
	}
	
	
	
	static private void getStablePosInfo( HSimpleTransInfo info, int index ){		
		
		if(index + 1 < 0 || index + 1 >= mStableInfoA6.length ){
			//default
			info.X     = mStableInfoA6[4].X;
			info.Y     = mStableInfoA6[4].Y;
			info.Scale = mStableInfoA6[4].Scale;
			info.Alpha = mStableInfoA6[4].Alpha;
			
			return;
		}
		
		//
		info.X     = mStableInfoA6[index + 1].X;
		info.Y     = mStableInfoA6[index + 1].Y;
		info.Scale = mStableInfoA6[index + 1].Scale;
		info.Alpha = mStableInfoA6[index + 1].Alpha;
		
	}
	

	
	
	
	
	static public PosInfo getAnimStablePos(float originX, float originY, float destX, float destY, int moveDirection, int viewIndex, int targetImageWidth ){
		PosInfo posInfo = null;
		
		HSimpleTransInfo info = new HSimpleTransInfo();
		
		float startX = 0;
		float startY = 0;
		float startScale = 0;
		float startAlpha = 1.0f;
		float endX = 0;	
		float endY = 0;
		float endScale = 0;			
		float endAlpha = 1.0f;
		
		float radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;
		

		
		int index;
		index = viewIndex;
		
		
		if(moveDirection == PosInfo.MOVE_RIGHT){
			getStablePosInfo(info, index);			
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;
			
			
			getStablePosInfo(info, index - 1);
			endX = info.X;	
			endY = info.Y;
			endScale = info.Scale;			
			endAlpha = info.Alpha;
		}else{
			getStablePosInfo(info, index - 1);			
			startX = info.X;
			startY = info.Y;
			startScale = info.Scale;
			startAlpha = info.Alpha;
			
			
			getStablePosInfo(info, index);
			endX = info.X;	
			endY = info.Y;
			endScale = info.Scale;			
			endAlpha = info.Alpha;
		}
		
		info = null;

		posInfo = new PosInfo(
				startX*radio - originX + destX, endX*radio - originX + destX, 
				startY*radio - originY + destY, endY*radio - originY + destY,
				startScale, endScale, 
				startScale, endScale, 
				startAlpha, endAlpha );
		
		return posInfo;
	}
	
static public PosInfo getAnimUnstablePos(float originX, float originY, float destX, float destY, int moveDirection, int viewSelectIndex, int viewIndex, int targetImageWidth ){
		
		float startX = 0;
		float startY = 0;
		float startScale = 0;
		float startAlpha = 1.0f;
		float endX = 0;	
		float endY = 0;
		float endScale = 0;			
		float endAlpha = 1.0f;
		
		
		int animationIndex = 0;
		
		float radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;
		
		if(moveDirection == PosInfo.MOVE_RIGHT){
			animationIndex = viewSelectIndex;
		}else{
			animationIndex = viewSelectIndex - 1;
		}
		
		//check input
		
		
		
		HSimpleTransInfo startInfo = null;
		HSimpleTransInfo endInfo  = null;
		
		HSimpleTransInfo [] infoArray = null;

		//check
		if(viewIndex < 0){
			viewIndex = 0;
		}
		
		if(viewIndex >= mUnstableInfoA6.length){
			viewIndex = mUnstableInfoA6.length - 1;
		}
		
		if(animationIndex + 1 < 0){
			animationIndex = -1;//
		}
		
		if(animationIndex + 2 >= mFramePos.length){
			animationIndex = mFramePos.length - 3;//
		}
		
		
		//
		infoArray = mFramePos[animationIndex + 1];
		startInfo = infoArray[viewIndex];
		
		infoArray = mFramePos[animationIndex + 2];
		endInfo = infoArray[viewIndex];		
		
		//
		startX     = startInfo.X;
		startY     = startInfo.Y; 
		startScale = startInfo.Scale;
		startAlpha = startInfo.Alpha;
		
		endX       = endInfo.X;	
		endY       = endInfo.Y;
		endScale   = endInfo.Scale;			
		endAlpha   = endInfo.Scale;
		
		
		PosInfo posInfo = null;
		
		if(moveDirection == PosInfo.MOVE_RIGHT){
			posInfo = new PosInfo(
					startX*radio - originX + destX, endX*radio - originX + destX,
					startY*radio - originY + destY, endY*radio - originY + destY,
					startScale, endScale,
					startScale, endScale,
					startAlpha, endAlpha);
		}else if(moveDirection == PosInfo.MOVE_LEFT){
			//exchange start and end
			posInfo = new PosInfo(
					endX*radio - originX + destX, startX*radio - originX + destX, 
					endY*radio - originY + destY, startY*radio - originY + destY,
					endScale, startScale, 
					endScale, startScale, 
					endAlpha, startAlpha );
		}

		return posInfo;
	}

	
	//initial
	static public TransInfo getInitialPosInfo(float originX, float originY, float destX, float destY, int viewSelectIndex, int viewIndex, int targetImageWidth ){
		boolean isStable = true;
		float radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;
		HSimpleTransInfo info = null;
		
		//check input
		if(viewSelectIndex + 1 < 0 || viewSelectIndex + 1 >= mFramePos.length ){
			return new TransInfo(0, 0, 0, 0, 0);
		}
		

		HSimpleTransInfo [] infoArray = mFramePos[viewSelectIndex + 1];
		
		//check input
		if(viewIndex < 0 || viewIndex >= infoArray.length ){
			return new TransInfo(0, 0, 0, 0, 0);
		}

		//get info
		info = infoArray[viewIndex];

		//shift
		return new TransInfo( info.X*radio - originX + destX, info.Y*radio - originY + destY, info.Scale, info.Scale, info.Alpha);
	}
	
	static class BasePositionInfo{
		 float originX;
		 float originY;
		 float destX;
		 float destY;
		 int targetImageWidth;
		 float radio;
		 public BasePositionInfo(float originX,
							 	 float originY,
							     float destX,
							     float destY,
							     int targetImageWidth){			 
			 set(originX, originY, destX, destY, targetImageWidth);
		 }
		 
		 public void set(float originX,
			 	 float originY,
			     float destX,
			     float destY,
			     int targetImageWidth){
			 this.originX = originX;
			 this.originY = originY;
			 this.destX = destX;
			 this.destY = destY;
			 this.targetImageWidth = targetImageWidth;	
			 this.radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;
		 }
	}
	
	public static final int STABLE_PRE_NUM  = 3;
	public static final int STABLE_NEXT_NUM = 2;
	public static final int STABLE_INDEX    = 3;
	public static final int ANI_TOTAL_NUM = STABLE_PRE_NUM + STABLE_NEXT_NUM + 1;
	
	public static int ANI_SELECT_NEGATIVE_POSITION = -1;
	
	//see A2 in pdf
	private static int ANI_STATUS_A2 = -1;
	private static int ANI_STATUS_A3 = 0;
	private static int ANI_STATUS_A4 = 1;
	private static int ANI_STATUS_A5 = 2;
	
	private static int ANI_STATUS_A6 = 3;
	
	private static int ANI_STATUS_A7 = 4;
	private static int ANI_STATUS_A8 = 5;	
	
	static public int whichCommonStatus(int arrayListNum, int selectPosition){
		
		if(selectPosition < -1){
			return ANI_STATUS_A2;
		}
		
		if(selectPosition > (arrayListNum - 1)){
			return ANI_STATUS_A8;
		}
		if(selectPosition <= STABLE_PRE_NUM){			
			return selectPosition;
		}
		
		if(selectPosition <= -1){
			return -1;
		}
		
		if(selectPosition >= arrayListNum - STABLE_NEXT_NUM){
			return STABLE_INDEX + 1 + selectPosition - (arrayListNum - STABLE_NEXT_NUM);
		}
		
		return STABLE_INDEX;		
	}
	
	
	
	static private boolean checkCommonStatus(int arrayListNum, int selectPosition){
		if(STABLE_INDEX == whichCommonStatus(arrayListNum, selectPosition)){
			return true;
		}
		
		return false;
	}
	
	static public int getVisableStartPosition(int arrayListNum, int selectedPosition){
		int startStablePosition  = HorizontalPosInfo.STABLE_PRE_NUM;//start
		int lastStablePosition   = arrayListNum - HorizontalPosInfo.STABLE_NEXT_NUM - 1;//last
		
		
		if(selectedPosition <= startStablePosition){
			return 0;
		}else if( selectedPosition >= lastStablePosition ){
			return arrayListNum - ANI_TOTAL_NUM;
		}else{
			return selectedPosition - STABLE_PRE_NUM;
		}		
	}
	
	
	static public int getVisableEndPosition(int arrayListNum, int selectedPosition){
		return getVisableStartPosition(arrayListNum, selectedPosition) + ANI_TOTAL_NUM;
	}
	
	/*
	static private int getViewIndex(int selectedPosition, int adapterIndex, int count){
		int startStablePosition = STABLE_PRE_NUM;//start
		int lastStablePosition   = count - STABLE_NEXT_NUM - 1;//last
		
		if(adapterIndex < (STABLE_INDEX + 1)){
			return selectedPosition + 1;
		}else if(adapterIndex >= lastStablePosition){
			return count - adapterIndex + 4;
		}else{
			return selectedPosition + 1 - (adapterIndex - (STABLE_INDEX + 1));
		}
	}
	*/
	
	static private int getViewIndex(int selectedPosition, int adapterViewIndex, int count){
		
		int startStablePosition = STABLE_PRE_NUM;//start
		int lastStablePosition   = count - STABLE_NEXT_NUM;//last
		
		if(selectedPosition < (STABLE_INDEX + 1)){
			return adapterViewIndex;
		}else if(selectedPosition >= lastStablePosition){
			//return adapterIndex - (count - STABLE_NEXT_NUM - STABLE_INDEX) + 1;
			return adapterViewIndex - (lastStablePosition - 1 - STABLE_INDEX);
		}else{
			return adapterViewIndex - (selectedPosition - STABLE_INDEX);
		}			
	}
	
	/*
	int ccccc = 11;
	String s = "";
	for(int i = 0; i < ccccc; i++ ){
		s = "";
		for(int j = -1; j < ccccc; j++ ){
			int vindex = getViewIndex(j, i, ccccc);
			s = s + "," + vindex;
		}
		Log.v("zhiwen", s );
		Log.v("zhiwen", "Next line");
	}
	s = "";
	for(int i = -2; i < ccccc + 1; i++ ){
		s = s + "," + whichCommonStatus(ccccc, i);
	}
	
	Log.v("zhiwen", s );
	*/
	
	
	
	static public PosInfo getUniversalAnimatorPosInfo(float originX, float originY, float destX, float destY, int targetImageWidth,
									int adapterViewIndex, int count,
									int selectedPosition, int newSelectedPosition){		
		if(count < STABLE_PRE_NUM + STABLE_NEXT_NUM + 1){
			return null;
		}		
			
		int step = 0;
		int sign = 1;
		
		step = newSelectedPosition - selectedPosition;
		if(step < 0){
			step = -step;
			sign = -1;
		}
		
		//new 
		
		mBaseInfo.set(originX, originY, destX, destY, targetImageWidth);
		BasePositionInfo baseInfo = mBaseInfo;
		PosInfo posInfo = new PosInfo();
		
		TransInfoKey keyFrame;
		float key = 0.0f;
		
		int curAniStatus  = 0;
		
		//
		int viewIndex;		
		
		int sel = 0;

		
	
		//how to get view index??????????????????
		//ani-status
		//-1,0,1,2,3	
		
			  //-1,0,1,2,3, 4, 4, 4, 4, 4, 5, 6
	  //selpos//-1,0,1,2,3, 4, 5, 6, 7, 8, 9,10
//adaptindex//0//0,0,0,0,0,-1,-2,-3,-4,-5,-5,-5
			//1//1,1,1,1,1,0,-1,-2,-3,-4,-4,-4
			//2//2,2,2,2,2,1,0,-1,-2,-3,-3
			//3//3,3,3,3,3,2,1,0,-1,-2,-2
		
			//stable start
			//4//4,4,4,4,4,3,2,1,0,-1,-1
		
			//5//5,5,5,5,5,4,3,2,1,0,0,		
			//6//6,6,6,6,6,5,4,3,2,1,1		
			//7//7,7,7,7,7,6,5,4,3,2,2
			//8//8,8,8,8,8,7,6,5,4,3,3		
			//stable end
		
			//9//9,9,9,9,9,8,7,6,5,4,4
			//10//10,10,10,10,10,9,8,7,6,5,5
		
		
		
		sel = selectedPosition;
		
		key = 0.0f;
		
		curAniStatus = whichCommonStatus(count, sel);
		
		viewIndex = getViewIndex(selectedPosition, adapterViewIndex, count);
		
		
		keyFrame = pointToStatus(key, curAniStatus, viewIndex, baseInfo);
		posInfo.addKeyFrame(keyFrame);
		
		
		for(int i = 0; i < step; i++ ){
			//selection add
			sel += sign;
			
			//key add
			key = 1.0f*(i+1)/step;
			
			curAniStatus = whichCommonStatus(count, sel);				
			
			viewIndex = getViewIndex(sel, adapterViewIndex, count);			

			keyFrame = pointToStatus(key, curAniStatus, viewIndex, baseInfo);			
			
			posInfo.addKeyFrame(keyFrame);			
		}		
		
		baseInfo = null;
		
		return posInfo;
	}
	
	
	//point to animation
	//viewIndex
	static private TransInfoKey pointToStatus(float key, int aniStatus, int viewIndex, BasePositionInfo baseInfo){
		TransInfoKey keyFrame;
		float radio = baseInfo.radio;
		HSimpleTransInfo info = null;		

		if(aniStatus != ANI_STATUS_A6){
			//this nead check
			HSimpleTransInfo [] infoArray = mFramePos[aniStatus + 1];
			
			//check input		
			int innerIndex = viewIndex;
			
			if(innerIndex < 0){
				info = mStableBoundInfo[0];//left bound
			}else if(innerIndex >= infoArray.length){//is need equal
				info = mStableBoundInfo[1];//right bound
			}else{
				//get info
				info = infoArray[innerIndex];
			}			
	
			//set
			keyFrame = new TransInfoKey(info.X*radio - baseInfo.originX + baseInfo.destX,
					     info.Y*radio - baseInfo.originY + baseInfo.destY,
					     info.Scale, info.Scale, info.Alpha, key);
			
			return keyFrame;
		}else{			
			int innerIndex = viewIndex + 1;
			
			if(innerIndex < 0){
				info = mStableBoundInfo[0];//left bound
			}else if(innerIndex >= mStableInfoA6.length){
				info = mStableBoundInfo[1];//right bound
			}else{
				info = mStableInfoA6[innerIndex];
			}
			
			keyFrame = new TransInfoKey(info.X*radio - baseInfo.originX + baseInfo.destX,
				     info.Y*radio - baseInfo.originY + baseInfo.destY,
				     info.Scale, info.Scale, info.Alpha, key);
			
			return keyFrame;
		}
	}
	
	
	private static void getLeftBoundLineCenter(Point pt, float destX, float destY, int targetImageWidth){
		float radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;
		HSimpleTransInfo info = mStableInfoA6[0];
		
		pt.x = (int)(info.X*radio + destX);
		pt.y = (int)(info.Y*radio + destY);	
	}
	
	private static void getRightBoundLineCenter(Point pt, float destX, float destY, int targetImageWidth){
		float radio = 1.0f*targetImageWidth/DEFAULT_IMAGE_WIDTH;
		HSimpleTransInfo info = mStableInfoA6[mStableInfoA6.length - 1];
		
		pt.x = (int)(info.X*radio + destX);
		pt.y = (int)(info.Y*radio + destY);			
	}
	

	
	static private TransInfoKey pointToSelectorStatus(float key, int aniStatus, BasePositionInfo baseInfo){
		HSimpleTransInfo info = new HSimpleTransInfo();
		
		float radio = baseInfo.radio;		

		getSelectPosInfo(info, aniStatus);			
		
		return new TransInfoKey(info.X*radio - baseInfo.originX + baseInfo.destX, 
								info.Y*radio - baseInfo.originY + baseInfo.destY, 
								info.Scale, 
								info.Scale, 
								info.Alpha, key );
	}
	
	
	
	static public PosInfo getUniversalSelectorPosInfo(float originX, float originY, float destX, float destY, int targetImageWidth, int count,
		int selectedPosition, int newSelectedPosition){		
		
		if(count < STABLE_PRE_NUM + STABLE_NEXT_NUM + 1){
			return null;
		}
		
			
		int step = 0;
		int sign = 1;
		
		step = newSelectedPosition - selectedPosition;
		if(step < 0){
			step = -step;
			sign = -1;
		}
		
		//new 
		BasePositionInfo baseInfo = new BasePositionInfo(originX, originY, destX, destY, targetImageWidth);
		PosInfo posInfo = new PosInfo();
		
		TransInfoKey keyFrame;
		TransInfoKey keyFrameStart;
		
		float key = 0.0f;
		
		int curAniStatus  = 0;
		
		int sel = 0;		
		
		
		sel = selectedPosition;
		
		curAniStatus = whichCommonStatus(count, sel);	
		
		key = 0.0f;
		keyFrameStart = pointToSelectorStatus(key, curAniStatus, baseInfo);
					
		if(step == 1){			
			//selection add
			sel += sign;
			
			curAniStatus = whichCommonStatus(count, sel);	
			
			//key add
			key = 1.0f;				
						
			keyFrame = pointToSelectorStatus(key, curAniStatus, baseInfo);			
			
			//alpha change
			SelectorAlphaChange(posInfo, keyFrameStart, keyFrame, baseInfo);
			//posInfo.addKeyFrame(keyFrameStart);
			//posInfo.addKeyFrame(keyFrame);
		}else{//do not include alpha change
			
			posInfo.addKeyFrame(keyFrameStart);
			
			
			for(int i = 0; i < step; i++ ){
				//selection add
				sel += sign;
				
				//key add
				key = 1.0f*(i+1)/step;
				
				curAniStatus = whichCommonStatus(count, sel);				
				keyFrame = pointToSelectorStatus(key, curAniStatus, baseInfo);			
				
				posInfo.addKeyFrame(keyFrame);			
			}		
			
		}
		
		baseInfo = null;
		
		return posInfo;
	}
	

	
	static private void SelectorAlphaChange(PosInfo posInfo, TransInfoKey startKey, TransInfoKey endKey, BasePositionInfo baseInfo){	
		TransInfo startTransInfo = startKey.mTransInfo;
		TransInfo endTransInfo   = endKey.mTransInfo;
		float radio = baseInfo.radio;			
		
		
			
		TransInfoKey interPolateStart = new TransInfoKey(
				(startTransInfo.x + (endTransInfo.x-startTransInfo.x)*SELECTOR_TIME_TO_DARK), 
				startTransInfo.y, 
				startTransInfo.scaleX + SELECTOR_TIME_TO_DARK*(endTransInfo.scaleX - startTransInfo.scaleX), 
				startTransInfo.scaleY + SELECTOR_TIME_TO_DARK*(endTransInfo.scaleY - startTransInfo.scaleY), 
				SELECTOR_ALPHA, SELECTOR_TIME_TO_DARK );
		
		TransInfoKey interPolateEnd = new TransInfoKey(
				(startTransInfo.x + (SELECTOR_TIME_FROM_DARK)*(endTransInfo.x-startTransInfo.x)), 
				endTransInfo.y, 
				startTransInfo.scaleX + (SELECTOR_TIME_FROM_DARK)*(endTransInfo.scaleX - startTransInfo.scaleX), 
				startTransInfo.scaleY + (SELECTOR_TIME_FROM_DARK)*(endTransInfo.scaleY - startTransInfo.scaleY), 
		        SELECTOR_ALPHA, SELECTOR_TIME_FROM_DARK );
		
		posInfo.addKeyFrame(startKey);
		posInfo.addKeyFrame(interPolateStart);
		posInfo.addKeyFrame(interPolateEnd);
		posInfo.addKeyFrame(endKey);
	}
	
	
	
	//initial
	static public TransInfo getUniversalInitialPosInfo(float originX, float originY, float destX, float destY,  int targetImageWidth,
			int count, int selectedPosition, int adapterViewIndex){		
		if(count < STABLE_PRE_NUM + STABLE_NEXT_NUM + 1){
			return null;
		}
		
		
		BasePositionInfo baseInfo = new BasePositionInfo(originX, originY, destX, destY, targetImageWidth);
		TransInfoKey keyFrame;
		float key = 0.0f;
		
		int curAniStatus  = 0;
		
		//
		int viewIndex;		
		
		int sel = 0;

		sel = selectedPosition;
		
		key = 0.0f;
		
		curAniStatus = whichCommonStatus(count, sel);
		
		viewIndex = getViewIndex(selectedPosition, adapterViewIndex, count);		
		
		keyFrame = pointToStatus(key, curAniStatus, viewIndex, baseInfo);
		
		TransInfo trans = new TransInfo();
		
		trans.clone(keyFrame.mTransInfo);
		
		return trans;
	}
	
	
	static public TransInfo getUniversalInitialSelectorPosInfo(float originX, float originY, float destX, float destY, int targetImageWidth, int count,
			int selectedPosition){		
			
			if(count < STABLE_PRE_NUM + STABLE_NEXT_NUM + 1){
				return null;
			}
			
			//new 
			BasePositionInfo baseInfo = new BasePositionInfo(originX, originY, destX, destY, targetImageWidth);
			
			TransInfoKey keyFrame;
			
			float key = 0.0f;
			
			int curAniStatus  = 0;
			
			int sel = 0;		
			
			
			sel = selectedPosition;
			
			curAniStatus = whichCommonStatus(count, sel);	
			
			key = 0.0f;
			keyFrame = pointToSelectorStatus(key, curAniStatus, baseInfo);
			
			TransInfo trans = new TransInfo();
			
			trans.clone(keyFrame.mTransInfo);
			
			return trans;
	}
	
	

	//center pos	
	private final static HSimpleTransInfo [] mUnstableInfoA2 = {
		new HSimpleTransInfo(148.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.9f,  1.0f),
		new HSimpleTransInfo(379.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.85f, 1.0f),
		new HSimpleTransInfo(597.6f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.8f,  1.0f), 
		new HSimpleTransInfo(802.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.75f, 1.0f),
		new HSimpleTransInfo(863.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(924.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f),
		};	
private final static HSimpleTransInfo [] mUnstableInfoA3 = {
		new HSimpleTransInfo(222.0f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
		new HSimpleTransInfo(506.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(684.6f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f), 
		new HSimpleTransInfo(849.6f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		new HSimpleTransInfo(896.7f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.55f, 1.0f),
		new HSimpleTransInfo(944.0f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.5f,  1.0f),
		};	
private final static HSimpleTransInfo [] mUnstableInfoA4 = {			
		new HSimpleTransInfo(122.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(406.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
		new HSimpleTransInfo(691.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(869.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f), 
		new HSimpleTransInfo(903.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		new HSimpleTransInfo(937.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.55f, 1.0f),
		};	
private final static HSimpleTransInfo [] mUnstableInfoA5 = {			
		new HSimpleTransInfo(115.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f), 
		new HSimpleTransInfo(294.0f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(578.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
		new HSimpleTransInfo(862.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(896.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f), 
		new HSimpleTransInfo(930.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		};	

//check bound
private final static HSimpleTransInfo [] mStableBoundInfo = {	
	new HSimpleTransInfo((102.7f - 5f)*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.55f,  1.0f),//Add
	new HSimpleTransInfo((930.8f + 5f)*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.55f,  1.0f),//Add
};
	
private final static HSimpleTransInfo [] mStableInfoA6 = {	
		new HSimpleTransInfo(102.7f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.55f,  1.0f),//Add
		new HSimpleTransInfo(109.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		new HSimpleTransInfo(143.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f), 
		new HSimpleTransInfo(321.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(605.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
		new HSimpleTransInfo(890.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(924.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		new HSimpleTransInfo(930.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.55f,  1.0f),//Add
		};
private final static HSimpleTransInfo [] mUnstableInfoA6 = {	
		new HSimpleTransInfo(109.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		new HSimpleTransInfo(143.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f), 
		new HSimpleTransInfo(321.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(605.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
		new HSimpleTransInfo(890.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(924.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		};
private final static HSimpleTransInfo [] mUnstableInfoA7 = {	
		new HSimpleTransInfo(102.6f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.55f,  1.0f),
		new HSimpleTransInfo(136.6f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		new HSimpleTransInfo(170.6f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f), 
		new HSimpleTransInfo(348.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		new HSimpleTransInfo(633.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
		new HSimpleTransInfo(917.6f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.7f,  1.0f),
		};	
private final static HSimpleTransInfo [] mUnstableInfoA8 = {	
		new HSimpleTransInfo(96.0f*1.5f  - X_CENTER, Y_CENTER - Y_CENTER, 0.5f,  1.0f),
		new HSimpleTransInfo(143.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.55f, 1.0f),
		new HSimpleTransInfo(190.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.6f,  1.0f),
		new HSimpleTransInfo(355.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.65f, 1.0f), 
		new HSimpleTransInfo(533.6f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.75f, 1.0f),
		new HSimpleTransInfo(818.0f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),			
		};

private final static HSimpleTransInfo [][] mFramePos = {
		mUnstableInfoA2,
		mUnstableInfoA3,
		mUnstableInfoA4,
		mUnstableInfoA5,
		mUnstableInfoA6,//unstatble
		mUnstableInfoA7,
		mUnstableInfoA8	};

private final static HSimpleTransInfo [] mSelectPos = {
	new HSimpleTransInfo(148.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 0.9f,  0.0f),
	new HSimpleTransInfo(222.0f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
	new HSimpleTransInfo(406.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
	new HSimpleTransInfo(578.4f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
	new HSimpleTransInfo(605.8f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
	new HSimpleTransInfo(633.2f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),
	new HSimpleTransInfo(818.0f*1.5f - X_CENTER, Y_CENTER - Y_CENTER, 1.0f,  1.0f),		//add	
};	
}
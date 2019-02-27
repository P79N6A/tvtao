package com.yunos.tv.app.widget;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;


public class HighPrecisionPathInfo{
	ArrayList<PointF> mArrHead;
	Path mPathHead;
	ArrayList<PointF> mArrTail;
	Path mPathTail;
	boolean mHeadPathIsCircle;
	boolean mTailPathIsCircle;
	public void log(){
		Log.v("HighPrecisionPathInfo", "mArrHead = " + mArrHead + " mArrTail = " + mArrTail);
	}
	
	public HighPrecisionPathInfo(){
		mHeadPathIsCircle = false;
		mTailPathIsCircle = false;
	}
}



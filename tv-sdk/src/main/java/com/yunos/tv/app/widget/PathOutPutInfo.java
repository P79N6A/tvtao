package com.yunos.tv.app.widget;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;


@SuppressLint("DrawAllocation")    
public class PathOutPutInfo{    		
	public ArrayList<Point> mArrHead;
	public ArrayList<Point> mArrTail;	    	
	
	
	public ArrayList<Point> getmArrHead(){
		return mArrHead;
	}
	
	public ArrayList<Point> getmArrTail(){
		return mArrTail;
	}
	
	public void setmArrHead(ArrayList<Point> arr){
		mArrHead = arr;
	}
	
	public void setmArrTail(ArrayList<Point> arr){
		mArrTail = arr;
	}
	
	public void log(){
		Log.v("dataMike", "mArrHead = " + mArrHead + " mArrTail = " + mArrTail);
	}
	
	/*
    @Override
    public String toString() {	    	
    	String str = "";
    	if(mArrHead != null){
    		str += "Head: ";
    		for(int i = 0; i < mArrHead.size(); i++ ){
    			str += "itme [i=" + i + ",data = " + mArrHead.get(i) + "]";
    		}
    	}
    	
    	if(mArrTail != null){
    		str += "Tail: ";
    		for(int i = 0; i < mArrTail.size(); i++ ){
    			str += "itme [i=" + i + ",data = " + mArrTail.get(i) + "]";
    		}
    	}
    	
    	return str;
    }
    */
}



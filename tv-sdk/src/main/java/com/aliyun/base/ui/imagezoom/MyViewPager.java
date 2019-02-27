package com.aliyun.base.ui.imagezoom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.lang.reflect.Field;

public class MyViewPager extends ViewPager implements TouchInterface {
	public static final String TAG = "MyViewPager";

	private boolean isIntercept = false;

	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int count = event.getPointerCount();
		boolean bool = false;

//		if (action == MotionEvent.ACTION_MOVE && count == 2) {
//			super.onInterceptTouchEvent(event);
//			bool = false;
//		} else if (action == MotionEvent.ACTION_DOWN || isIntercept) {
//			bool = super.onInterceptTouchEvent(event);
//			Log.i(TAG, "--------onInterceptTouchEvent-------------bool--" + bool + ",isIntercept=" + isIntercept);
//		} else {
//			// super.onInterceptTouchEvent(event);
//			bool = false;
//		}

		if (action == MotionEvent.ACTION_DOWN || isIntercept) {
			if (action == MotionEvent.ACTION_DOWN) {
				isIntercept = false;
			}
			bool = super.onInterceptTouchEvent(event);
		}
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.i(TAG, "----ACTION_POINTER_DOWN----onInterceptTouchEvent-------------bool--" + bool + ",isIntercept=" + isIntercept);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.i(TAG, "----ACTION_POINTER_UP----onInterceptTouchEvent-------------bool--" + bool + ",isIntercept=" + isIntercept);
			break;
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "----ACTION_DOWN----onInterceptTouchEvent-------------bool--" + bool + ",isIntercept=" + isIntercept);
			break;
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "----ACTION_UP----onInterceptTouchEvent-------------bool--" + bool + ",isIntercept=" + isIntercept);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "--ACTION_MOVE------onInterceptTouchEvent-------------bool--" + bool + ",isIntercept=" + isIntercept);
			break;
		}

		try {
			Field localField = ViewPager.class.getDeclaredField("mLastMotionX");
			localField.setAccessible(true);
			localField.set(this, Float.valueOf(event.getX()));

			// localField = ViewPager.class.getDeclaredField("mLastMotionY");
			// localField.setAccessible(true);
			// localField.set(this, Float.valueOf(event.getY()));
			//
			// localField = ViewPager.class.getDeclaredField("mInitialMotionX");
			// localField.setAccessible(true);
			// localField.set(this, Float.valueOf(event.getX()));
		} catch (IllegalAccessException localIllegalAccessException) {
		} catch (IllegalArgumentException localIllegalArgumentException) {
		} catch (NoSuchFieldException localNoSuchFieldException) {
		} catch (SecurityException localSecurityException) {
		}
		return bool;
	}

	@Override
	public void setTouch(boolean b) {
		isIntercept = b;
	}

}

package com.aliyun.base.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NativeWebView extends WebView {

	public NativeWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public NativeWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		boolean b = super.onTouchEvent(ev);
		skipDoubleTapDelay(ev);
		return b;
	}

	private void skipDoubleTapDelay(MotionEvent paramMotionEvent) {
		try {
			if (paramMotionEvent.getAction() != 1)
				return;
			Class localClass1 = WebView.class;
			Handler localHandler = (Handler) getPrivateObject(this, "mPrivateHandler", localClass1);
			if (!localHandler.hasMessages(5))
				return;
			localHandler.removeMessages(5);
			Class localClass2 = WebView.class;
			Class[] arrayOfClass = new Class[0];
			Method localMethod = getPrivateMethod(this, "doShortPress", localClass2, arrayOfClass);
			Object[] arrayOfObject = new Object[0];
			localMethod.invoke(this, arrayOfObject);
		} catch (Exception localException) {
			localException.printStackTrace();
			// mSingleTapCompatible = localObject;
		}

	}

	private Method getPrivateMethod(Object paramObject, String paramString, Class paramClass, Class[] paramArrayOfClass) throws Exception {
		if (paramClass == null)
			paramClass = paramObject.getClass();
		Method localMethod = paramClass.getDeclaredMethod(paramString, paramArrayOfClass);
		localMethod.setAccessible(true);
		return localMethod;
	}

	private Object getPrivateObject(Object paramObject, String paramString, Class paramClass) throws Exception {
		if (paramClass == null)
			paramClass = paramObject.getClass();
		Field localField = paramClass.getDeclaredField(paramString);
		localField.setAccessible(true);
		return localField.get(paramObject);
	}
}

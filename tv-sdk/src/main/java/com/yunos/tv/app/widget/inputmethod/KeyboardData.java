package com.yunos.tv.app.widget.inputmethod;

import android.content.Context;
import android.inputmethodservice.Keyboard;

import com.yunos.tv.app.widget.utils.ReflectUtils;

import java.lang.reflect.Method;

/**
 * 键盘数据，主要配置键盘需要显示哪些键及相对大小
 * @author quanqing.hqq
 *
 */
public class KeyboardData extends Keyboard {
	
	private Method resizeMethod = null;
	
	public KeyboardData(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }
	
	public KeyboardData(Context context, int xmlLayoutResId, int modeId, int width, int height) {
		super(context, xmlLayoutResId, modeId, width, height);
	}
	
	public KeyboardData(Context context, int xmlLayoutResId, int modeId) {
		super(context, xmlLayoutResId, modeId);
	}
	
	public KeyboardData(Context context, int layoutTemplateResId, 
            CharSequence characters, int columns, int horizontalPadding) {
		super(context, layoutTemplateResId, characters, columns, horizontalPadding);
	}
	
	@Override
	public int getHorizontalGap() {
		return super.getHorizontalGap();
	}
	
	@Override
	protected void setHorizontalGap(int gap) {
		super.setHorizontalGap(gap);
	}
	
	@Override
	public int getKeyHeight() {
		return super.getKeyHeight();
	}
	
	@Override
	protected void setKeyHeight(int height) {
		super.setKeyHeight(height);
	}
	
	@Override
	protected int getKeyWidth() {
		return super.getKeyWidth();
	}
	
	@Override
	protected void setKeyWidth(int width) {
		super.setKeyWidth(width);
	}
	
	@Override
	protected int getVerticalGap() {
		return super.getVerticalGap();
	}
	
	@Override
	protected void setVerticalGap(int gap) {
		super.setVerticalGap(gap);
	}
	
	public void resizeData(int width, int height){
		if(resizeMethod == null){
			resizeMethod = ReflectUtils.getDeclaredMethod(this, "resize", new Class[] { int.class, int.class });
		}
		
		if(resizeMethod != null){
			try {
				resizeMethod.setAccessible(true);
				resizeMethod.invoke(this, new Object[]{width, height});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

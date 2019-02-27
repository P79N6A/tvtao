package com.yunos.tv.app.widget.inputmethod;

import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 键盘适配器，用于确定键盘每个元素的布局
 * @author quanqing.hqq
 *
 */
public abstract class KeyboardAdapter extends BaseAdapter {

	private int mKeyboardResId = 0;
	private KeyboardData mKeyboardData = null;
	protected int mCount = 0;

	protected Context mContext;

	public KeyboardAdapter(Context context, int resId) {
		mContext = context;
		if (mContext == null) {
			throw new NullPointerException("SpecificLocationAdapter context must not be null ");
		}
		
		mKeyboardResId = resId;
	}

	@Override
	public int getCount() {
		return mCount;
	}

	public int getKeyboardResId() {
		return mKeyboardResId;
	}

	public void setKeyboardResId(int resId) {
		mKeyboardResId = resId;
		clearKeyboardData();
	}

	public void clearKeyboardData() {
		mKeyboardData = null;
	}

	public void resetKeyboard(int width, int height) {
		mKeyboardData = new KeyboardData(mContext, mKeyboardResId, 0, width, height);
		List<Key> list = mKeyboardData.getKeys();
		if (list != null) {
			mCount = list.size();
		}
	}

	public KeyboardData getkeyboardData() {
		return mKeyboardData;
	}

}

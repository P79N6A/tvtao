package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tvlife.app.widget.FocusedBasePositionManager.DecelerateFrameInterpolator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExpandFocusedRelativeLayout extends FocusedRelativeLayout implements Expandable {
	static String TAG = "ExpandFocusedRelativeLayout";
	
	ExpandRunnable mRunnable = new ExpandRunnable();
	int mExpandMargin = 30;
	Map<View, ExpandInfo> mExpandMap = new HashMap<View, ExpandInfo>();
	ExpandChangeListener mExpandChangeListener;
	float mExpandScale = 1.1f;
	boolean mExpanded = true;
	boolean mExpandable = true;
	
	public ExpandFocusedRelativeLayout(Context contxt, AttributeSet attrs, int defStyle) {
		super(contxt, attrs, defStyle);
	}

	public ExpandFocusedRelativeLayout(Context contxt, AttributeSet attrs) {
		super(contxt, attrs);
	}

	public ExpandFocusedRelativeLayout(Context contxt) {
		super(contxt);
	}
	
	private int mExpandType = Expandable.EXPAND_HORIZONTAL;
	
	public interface ExpandChangeListener {
		public static final int EXPAND_START = 1;
		public static final int COLLIPSE_START = 2;

		public static final int EXPAND_COMPLETE = 3;
		public static final int COLLIPSE_COMPLETE = 4;

		public void onExpandChange(ViewGroup v, int state);
	}

	class ExpandInfo {
		int left;
		int collipseLeft;
		int currentLeft;
		
		int top;
		int collipseTop;
		int currentTop;
	}

	/**
	 * 设置缩放比例
	 * @param scale 默认1.1f
	 */
	public void setExpandScale(float scale) {
		this.mExpandScale = scale;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!isExpandable()) {
			return ;
		}
		mExpandMap.clear();

		View v = getChildAt(getChildCount() - 1);
		if (Expandable.EXPAND_HORIZONTAL == mExpandType) {
			int right = v.getRight();
			int width = getWidth();
			if (right > width) {
				throw new IllegalArgumentException("Can't expand from center, width = " + width + ", right = " + right);
			}
		} else {
			int bottom = v.getBottom();
			int height = getHeight();
			if (bottom > height) {
				throw new IllegalArgumentException("Can't expand from center, height = " + height + ", bottom = " + bottom);
			}
		}

		int center = getChildCount() / 2;
		if (getChildCount() % 2 == 1) {
			if (Expandable.EXPAND_HORIZONTAL == mExpandType) {
				for (int index = 0, childCount = getChildCount(); index < childCount; index++) {
					if (index == center) {
						continue;
					}
					View item = getChildAt(index);
					ExpandInfo info = new ExpandInfo();
					info.left = item.getLeft();
					info.currentLeft = item.getLeft();
					info.collipseLeft = item.getLeft() - (index - center) * mExpandMargin;
					mExpandMap.put(item, info);
				}
			} else {
				for (int index = 0, childCount = getChildCount(); index < childCount; index++) {
					if (index == center) {
						continue;
					}
					View item = getChildAt(index);
					ExpandInfo info = new ExpandInfo();
					info.top = item.getTop();
					info.currentTop = item.getTop();
					info.collipseTop = item.getTop() - (index - center) * mExpandMargin;
					mExpandMap.put(item, info);
				}
			}
		} else {
			center--;
			int haflMargin = mExpandMargin / 2;
			if (Expandable.EXPAND_HORIZONTAL == mExpandType) {
				for (int index = 0, childCount = getChildCount(); index < childCount; index++) {
					View item = getChildAt(index);
					ExpandInfo info = new ExpandInfo();
					info.left = item.getLeft();
					info.currentLeft = item.getLeft();

					if (index == center) {
						info.collipseLeft = item.getLeft() + haflMargin;
					} else if (index == center + 1) {
						info.collipseLeft = item.getLeft() - haflMargin;
					} else {
						if (index < center) {
							info.collipseLeft = item.getLeft() - ((index - center) * mExpandMargin - haflMargin);
						} else {
							info.collipseLeft = item.getLeft() - ((index - center - 1) * mExpandMargin + haflMargin);
						}
					}

					mExpandMap.put(item, info);
				}
			} else {
				for (int index = 0, childCount = getChildCount(); index < childCount; index++) {
					View item = getChildAt(index);
					ExpandInfo info = new ExpandInfo();
					info.top = item.getTop();
					info.currentTop = item.getTop();

					if (index == center) {
						info.collipseTop = item.getTop() + haflMargin;
					} else if (index == center + 1) {
						info.collipseTop = item.getTop() - haflMargin;
					} else {
						if (index < center) {
							info.collipseTop = item.getTop() - ((index - center) * mExpandMargin - haflMargin);
						} else {
							info.collipseTop = item.getTop() - ((index - center - 1) * mExpandMargin + haflMargin);
						}
					}

					mExpandMap.put(item, info);
				}
			}
		}

		setScaleX(this.mExpandScale);
		setScaleY(this.mExpandScale);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (!isExpandable()) {
			return;
		}
		if (gainFocus) {
			expand();
		} else {
			collipse();
		}
	}

	public void setExpandMargin(int margin) {
		this.mExpandMargin = margin;
	}
	
	public int getExpandMargin() {
		return this.mExpandMargin;
	}

	public void setExpandChangeListener(ExpandChangeListener l) {
		this.mExpandChangeListener = l;
	}

	void changeState(int state) {
		if (this.mExpandChangeListener != null) {
			this.mExpandChangeListener.onExpandChange(this, state);
		}

		if (state == ExpandChangeListener.EXPAND_START || state == ExpandChangeListener.COLLIPSE_START) {
			setScrolling(true);
		} else {
			setScrolling(false);
		}
	}

	void expand() {
		if (mExpanded) {
			return;
		}
		mExpanded = true;
		mRunnable.expand();
	}

	void collipse() {
		if (!mExpanded) {
			return;
		}
		mExpanded = false;
		mRunnable.collipse();
	}

	void expandItem(float expandScale) {
		int center = getChildCount() / 2;
		if (getChildCount() % 2 == 1) {
			for (int index = 0; index < getChildCount(); index++) {
				if (index == center) {
					continue;
				}
				expandItem(index, expandScale);
			}
		} else {
			for (int index = 0; index < getChildCount(); index++) {
				expandItem(index, expandScale);
			}
		}

		float scale = (1.0f - this.mExpandScale) * expandScale + getScaleX();
		setScaleX(scale);
		setScaleY(scale);
	}

	void expandItem(int index, float expandScale) {
		View item = getChildAt(index);
		ExpandInfo info = mExpandMap.get(item);
		int offset = 0;
		switch (mExpandType) {
		case EXPAND_HORIZONTAL:
			offset = (int) ((info.collipseLeft - info.left) * expandScale);
			info.currentLeft += offset;
			item.offsetLeftAndRight(offset);
			break;
		case EXPAND_VERTICAL:
			offset = (int) ((info.collipseTop - info.top) * expandScale);
			info.currentTop += offset;
			item.offsetTopAndBottom(offset);
			break;
		}
	}

	public void setExpandFrameRate(int rate) {
		this.mRunnable.setExpandFrameRate(rate);
	}

	private class ExpandRunnable implements Runnable {

		DecelerateFrameInterpolator mAccelerateFrameInterpolator = new DecelerateFrameInterpolator(0.5f);
		// DecelerateFrameInterpolator mDecelerateFrameInterpolator = new
		// DecelerateFrameInterpolator();
		int mCurrentFrame = 10;
		int mFrameRate = 10;
		int mLastFrame = 10;
		boolean mIsExpand = true;

		public void setExpandFrameRate(int rate) {
			this.mFrameRate = rate;
			mCurrentFrame = rate;
			mLastFrame = rate;
		}

		public void expand() {
			mIsExpand = true;
			mCurrentFrame = mLastFrame;
			changeState(ExpandChangeListener.EXPAND_START);
			post(this);
		}

		public void collipse() {
			mIsExpand = false;
			mCurrentFrame = mLastFrame;
			changeState(ExpandChangeListener.COLLIPSE_START);
			post(this);
		}

		@Override
		public void run() {
			if (mCurrentFrame <= mFrameRate && mCurrentFrame > 0) {

				float expandScale = 1.0f;
				mLastFrame = mCurrentFrame;
				if (!mIsExpand) {
					expandScale = mAccelerateFrameInterpolator.getInterpolation((float) 1 / mFrameRate);
					mCurrentFrame--;
				} else {
					expandScale = -mAccelerateFrameInterpolator.getInterpolation((float) 1 / mFrameRate);
					mCurrentFrame++;
				}
				expandItem(expandScale);

				invalidate();
				post(this);
			} else {
				if (mIsExpand) {
					changeState(ExpandChangeListener.EXPAND_COMPLETE);
					Iterator<View> ir = mExpandMap.keySet().iterator();
					while (ir.hasNext()) {
						ExpandInfo info = mExpandMap.get(ir.next());
						info.currentLeft = info.left;
					}
				} else {
					changeState(ExpandChangeListener.COLLIPSE_COMPLETE);
					Iterator<View> ir = mExpandMap.keySet().iterator();
					while (ir.hasNext()) {
						ExpandInfo info = mExpandMap.get(ir.next());
						info.currentLeft = info.collipseLeft;
					}
				}
			}
		}

	}
	
	public int getExpandType() {
		return mExpandType;
	}
	
	/**
	 * 设置expand类型，水平还是竖向
	 * @param expandType {@link EXPAND_HORIZONTAL,EXPAND_VERTICAL} 默认前者
	 */
	public void setExpandType(int expandType) {
		this.mExpandType = expandType;
	}
	
	public boolean isExpandable() {
		return mExpandable;
	}
	
	/**
	 * 设置是否可Expandalble
	 * @param expandable
	 */
	public void setExpandable(boolean expandable) {
		this.mExpandable = expandable;
	}
}

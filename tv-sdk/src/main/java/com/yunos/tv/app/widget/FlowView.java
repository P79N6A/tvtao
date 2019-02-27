/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;
import java.util.List;

public class FlowView extends AbsGallery implements FocusListener {

	private static final String TAG = "FlowView";
	private static final boolean DEBUG = true;

	// -----滑动相关参数-------
	private FlingRunnable mFlingRunnable = new FlingRunnable();// 滑动相关操作runnable
	private int mLastPosition = 0;// 可视区域最后一个
	private boolean mIsNext = true;// 往左移还是往右移（按右键，mIsNext为true，整体左移）

	// -----监听器-------
	private ItemSelectedListener mItemSelectedListener;// focus框使用
	private OnScrollListener mOnScrollListener;// 当前是否处于滑动状态的callback
	private FlowViewStateListener mFlowViewStateListener;// 入场动画结束的callback
	private ItemClickListener mItemClickListener;// 响应点击效果的callback

	private int mScrollFlowDuration = 1000;// 滑动的持续时间
	private int mExpandFlowDuration = 1000;// 展开的持续时间
	private int mResetFlowDuration = 1000;// 重置到第一个Item需要的时间
	private int mExpandDelayed = 100;// 滑动结束后进行展开的延迟

	private int mLeftPadding = 50;
	private int mRightpadding = 50;
	private int mHidePadding = 0;

	// -----focus框相关参数-----
	private int mFocusFrameCount = 20;
	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, mFocusFrameCount, new AccelerateDecelerateFrameInterpolator());
	protected FocusRectParams mFocusRectparams = new FocusRectParams();
	private DeepListener mDeep = null;
	private DeepListener mLastDeep = null;
	private int mNextDirection = FOCUS_RIGHT;
	private View mNextFocus = null;

	// -----layout相关参数------
	private boolean mIsRtl = true;// 从左到右布局还是从右到左布局（目前只采用从左到右布局）
	private boolean mIsEnableHideScreen = false;// 是否右负一屏
	private int mlayoutTimes = 0;// 当前已经layout多少次
	private boolean mLayouted = false;// 是否已经layout过
	private int mHideScreenWidth = 0;// 负一屏宽度
	private int mLeftMost;// layout左边界
	private int mRightMost;// layout右边界
	private int mSavedSelectedPosition = 0;// 重新layout前选中Item的index
	private int mSavedLeftMostChildShift = 0;// 重新layout前最左边Item的左边坐标
	private boolean mForceLayout = true;

	private boolean mIsAnimate = true;
	public boolean isFlowing = false;

	boolean mFocusBackground = false;
	private ScrollFlowAction mScrollFlowAction = new ScrollFlowAction();
	private FlowAnimRunnable mFlowAnimRunnable = new FlowAnimRunnable();
	private int mFirstFlowPosition = 0;
	private int mLastFlowPosition = 0;

	public FlowView(Context context) {
		super(context);
		init(context);
	}

	public FlowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FlowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public int getSelectedPosition() {
		return mSelectedPosition;
	}

	public void setFocusBackground(boolean back) {
		mFocusBackground = back;
	}

	public void setLeftAndRightPadding(int left, int right) {
		mLeftPadding = left;
		mRightpadding = right;
	}
	
	public void setFocusFrameCount(int focusCount) {
		mFocusFrameCount = focusCount;
		mParams = new Params(1.1f, 1.1f, 10, null, true, mFocusFrameCount, new AccelerateDecelerateFrameInterpolator());
	}

	/**
	 * 设置滑动周期信息
	 * 
	 * @param scrollFlowDuration
	 * @param expandFlowDuration
	 */
	public void setFlowDuration(int scrollFlowDuration, int expandFlowDuration) {
		mScrollFlowDuration = scrollFlowDuration;
		mExpandFlowDuration = expandFlowDuration;
	}

	/**
	 * 设置展开延时
	 * 
	 * @param delayed
	 */
	public void setExpandDelayed(int delayed) {
		mExpandDelayed = delayed;
	}

	/**
	 * 设置入场开始监听器
	 * 
	 * @param l
	 */
	public void setFlowStartListener(FlowViewStateListener l) {
		mFlowViewStateListener = l;
	}

	/**
	 * 设置item选中监听器
	 * 
	 * @param l
	 */
	public void setOnItemSelectedListener(ItemSelectedListener l) {
		mItemSelectedListener = l;
	}

	/**
	 * 设置滑动监听器
	 * 
	 * @param l
	 */
	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListener = l;
	}

	/**
	 * 设置Item点击监听器，用来处理Item点击效果
	 * 
	 * @param l
	 */
	public void setItemClickListener(ItemClickListener l) {
		mItemClickListener = l;
	}

	/**
	 * 设置是否有负一屏
	 * 
	 * @param enable
	 */
	public void setEnableHideScreen(boolean enable) {
		mIsEnableHideScreen = enable;
		if(enable && mDefaultPosition == 0){
		    mDefaultPosition = 1;
		}
	}

	/**
	 * 设置负一屏padding
	 * 
	 * @param padding
	 */
	public void setHidePadding(int padding) {
		mHidePadding = padding;
	}

	private void init(Context context) {
		mGravity = Gravity.CENTER_VERTICAL;
	}

	@Override
	protected int computeHorizontalScrollExtent() {
		// Only 1 item is considered to be selected
		return 1;
	}

	@Override
	protected int computeHorizontalScrollOffset() {
		// Current scroll position is the same as the selected position
		return mSelectedPosition;
	}

	@Override
	protected int computeHorizontalScrollRange() {
		// Scroll range is the same as the item count
		return mItemCount;
	}

	// //////////////////////////////////////////////////////
	// 布局模块
	// //////////////////////////////////////////////////////

	public void setForceLayout(boolean forceLayout) {
		mForceLayout = true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// if (!mForceLayout && mlayoutTimes >= 2) {
		if (!mForceLayout && getChildCount() > 0) {
			return;
		}
		if (!mScrollFlowAction.isIdle()) {
			mFlingRunnable.forceStop();
		}
		if (DEBUG) {
			Log.d(TAG, "flowLayout");
		}

		/*
		 * Remember that we are in layout to prevent more layout request from
		 * being generated.
		 */
		mInLayout = true;
		layout(0, false);
		mInLayout = false;

		mlayoutTimes++;

		// startFlow();
		Log.d(TAG, "FlowView Layout Finished mlayoutTimes:" + mlayoutTimes);
		// if (mOnFlowViewLayoutFinishedListener != null && mlayoutTimes >= 2) {
		if (mFlowViewStateListener != null && getChildCount() > 0) {
			mFlowViewStateListener.onLayoutFinished();
		}

		mLayouted = true;
		mForceLayout = false;
	}

	@Override
	public void requestLayout() {
		if (DEBUG) {
			Log.d(TAG, "requestLayout");
		}
		
		if (mScrollFlowAction != null && mFlingRunnable != null && !mScrollFlowAction.isIdle() && mForceLayout) {
            mFlingRunnable.forceStop();
        }
		super.requestLayout();
	}

	/**
	 * 存储重新layout前的layout状态信息
	 */
	private void saveCurrentState() {
		if (getChildAt(mSelectedPosition) != null) {
			mSavedSelectedPosition = mSelectedPosition;
			View leftMost;
			if (mIsEnableHideScreen) {
				leftMost = getChildAt(1);
			} else {
				leftMost = getChildAt(0);
			}
			if (leftMost != null) {
				mSavedLeftMostChildShift = leftMost.getLeft() - mLeftPadding;
			}
			if (DEBUG) {
				Log.d(TAG, "flowlayout " + mSavedLeftMostChildShift);
			}
		}
	}

	/**
	 * 主要逻辑是填充部分 Creates and positions all views for this Gallery.
	 * <p>
	 * We layout rarely, most of the time {@link #trackMotionScroll(int)} takes
	 * care of repositioning, adding, and removing children.
	 * 
	 * @param delta
	 *            Change in the selected position. +1 means the selection is
	 *            moving to the right, so views are scrolling to the left. -1
	 *            means the selection is moving to the left.
	 */
	@Override
	protected void layout(int delta, boolean animate) {
		mIsRtl = false;// isLayoutRtl();

		if (mDataChanged) {
			handleDataChanged();
		}
		// Handle an empty gallery by removing all views.
		if (mItemCount == 0) {
			resetList();
			return;
		}

		// if(mLayouted) {
		// saveCurrentState();
		// }

		Log.d(TAG, "flowlayout 1s:" + mSelectedPosition + ";F:" + mFirstPosition + ";L:" + mLastPosition + ";NS:" + mNextSelectedPosition + ";left:" + ";child:" + getChildAt(mSelectedPosition)
				+ ";layoutTime:" + mlayoutTimes + ";mLayout:" + mLayouted);

		// All views go in recycler while we are in layout
		mFirstPosition = mSelectedPosition = 0;
		mLastPosition = mSelectedPosition;
		recycleAllViews();

		// Clear out old views
		// removeAllViewsInLayout();
		detachAllViewsFromParent();

		/*
		 * These will be used to give initial positions to views entering the
		 * gallery as we scroll
		 */
		mRightMost = 0;
		mLeftMost = 0;

		// Make selected view and center it

		// 获取选中位置View
		View sel = makeAndAddView(mSelectedPosition, 0, mLeftPadding, true);

		// 目前只用到了R_L_R
		fillToGalleryRight();
//		fillToGalleryLeft();

		// Flush any cached views that did not get reused above
		mRecycler.clear();
		if (mLayouted) {
			// if(getChildAt(mSavedSelectedPosition) != null) {
			// if(mIsEnableHideScreen && mSavedSelectedPosition == 0 &&
			// mlayoutTimes < 2) {
			// setSelectedPositionInt(mSavedSelectedPosition+1);
			// setNextSelectedPositionInt(mSavedSelectedPosition+1);
			// } else {
			// setSelectedPositionInt(mSavedSelectedPosition);
			// setNextSelectedPositionInt(mSavedSelectedPosition);
			// }
			// int diff =
			// ((FlowListener)getChildAt(mSelectedPosition)).getExpandDistance();
			// for(int i=0;i<getChildCount(); i++) {
			// if(mIsEnableHideScreen && i == 0) {
			// if(mSelectedPosition == 0 && mlayoutTimes >= 2) {
			// getChildAt(mSelectedPosition).offsetLeftAndRight(getChildAt(mSelectedPosition).getMeasuredWidth()+mHidePadding);
			// } else {
			// continue;
			// }
			// } else if(i == mSelectedPosition) {
			// getChildAt(i).offsetLeftAndRight(mSavedLeftMostChildShift+diff);
			// } else if (i > mSelectedPosition) {
			// getChildAt(i).offsetLeftAndRight(mSavedLeftMostChildShift+2*diff);
			// } else {
			// getChildAt(i).offsetLeftAndRight(mSavedLeftMostChildShift);
			// }
			// }
			// } else {
			// if(mIsEnableHideScreen && getChildAt(1) != null) {
			// setNextSelectedPositionInt(1);
			// } else if(getChildAt(0) != null) {
			// setNextSelectedPositionInt(0);
			// }
			// }
		    setSelectedPositionInt(mDefaultPosition);
		    setNextSelectedPositionInt(mDefaultPosition);
//			if (mIsEnableHideScreen) {
//				setSelectedPositionInt(1);
//				setNextSelectedPositionInt(1);
//			} else {
//				setSelectedPositionInt(0);
//				setNextSelectedPositionInt(0);
//			}
			updatePosition();
		} else {
		    setSelectedPositionInt(mDefaultPosition);
//			if (mIsEnableHideScreen) {
//				setSelectedPositionInt(1);
//			}
		}

		sel = getChildAt(mSelectedPosition);
		if (sel != null) {
			positionSelector(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
		}
		checkSelectionChanged();
        mNextFocus = null;

		mDataChanged = false;
		mNeedSync = false;

		updateSelectedItemMetadata();

		// startFlow();
		Log.d(TAG, "flowlayout 2s:" + mSelectedPosition + ";F:" + mFirstPosition + ";L:" + mLastPosition + ";NS:" + mNextSelectedPosition + ";left:" + ";layoutTime:" + mlayoutTimes + ";mLayout:"
				+ mLayouted);

		DeepListener select = (DeepListener) getSelectedView();
		if (select.canDeep()) {
			mDeep = select;
			getSelectedView().setSelected(true);
		}

        if (mItemSelectedListener != null) {
            mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, true, this);
        }
        mHandler.sendEmptyMessageDelayed(MSG_EXPAND, mExpandDelayed);
		reset();
		invalidate();
	}

	/**
	 * 从右至左布局，未用到
	 */
	private void fillToGalleryLeft() {
		int itemSpacing = mSpacing;
		int galleryLeft = getPaddingLeft();

		// Set state for initial iteration
		View prevIterationView = getChildAt(mFirstPosition);
		int curPosition;
		int curRightEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition - 1;
			curRightEdge = prevIterationView.getLeft() - itemSpacing;
		} else {
			// No children available!
			curPosition = 0;
			curRightEdge = getRight() - getLeft() - getPaddingRight();
			mShouldStopFling = true;
		}

		FlowListener prevIterationListener;
		while (/* curRightEdge > galleryLeft && */curPosition >= 0) {
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curRightEdge, false);

			prevIterationListener = (FlowListener) prevIterationView;
			// Remember some state
			// mFirstPosition = curPosition;

			// Set state for next iteration
			// curRightEdge = prevIterationView.getLeft() - itemSpacing;
			if (curRightEdge > galleryLeft) {
				mFirstPosition = curPosition;
			}
			// Set state for next iteration
			prevIterationListener = (FlowListener) prevIterationView;
			curRightEdge = prevIterationView.getLeft() - itemSpacing + prevIterationListener.getExpandDistance();
			curPosition--;
		}
	}

	/**
	 * 从左到右布局
	 */
	private void fillToGalleryRight() {
		int itemSpacing = mSpacing;
		int galleryRight = getRight() - getLeft() - getPaddingRight();
		int numChildren = getChildCount();
		int numItems = mItemCount;

		// Set state for initial iteration
		View prevIterationView = getChildAt(numChildren - 1);
		int curPosition;
		int curLeftEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition + numChildren;
			if (mIsEnableHideScreen && numChildren == 1) {
				curLeftEdge = mLeftPadding;
			} else {
				curLeftEdge = prevIterationView.getRight() + itemSpacing;
			}
		} else {
			mFirstPosition = curPosition = mItemCount - 1;
			curLeftEdge = getPaddingLeft();
			mShouldStopFling = true;
		}

		FlowListener prevIterationListener;
		while (/* curLeftEdge < galleryRight && */curPosition < numItems) {
			prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curLeftEdge, true);

			prevIterationListener = (FlowListener) prevIterationView;
			// curLeftEdge -= prevIterationListener.getExpandDistance();
			if (curLeftEdge < galleryRight) {
				mLastPosition = curPosition;
			}
			// Set state for next iteration
			prevIterationListener = (FlowListener) prevIterationView;
			curLeftEdge = prevIterationView.getRight() + itemSpacing - prevIterationListener.getExpandDistance();
			curPosition++;
		}
	}

	/**
	 * 增加一个子Item到layout View Obtain a view, either by pulling an existing view
	 * from the recycler or by getting a new one from the adapter. If we are
	 * animating, make sure there is enough information in the view's layout
	 * parameters to animate from the old to new positions.
	 * 
	 * @param position
	 *            Position in the gallery for the view to obtain
	 * @param offset
	 *            Offset from the selected position
	 * @param x
	 *            X-coordinate indicating where this view should be placed. This
	 *            will either be the left or right edge of the view, depending
	 *            on the fromLeft parameter
	 * @param fromLeft
	 *            Are we positioning views based on the left edge? (i.e.,
	 *            building from left to right)?
	 * @return A view that has been added to the gallery
	 */
	private View makeAndAddView(int position, int offset, int x, boolean fromLeft) {

		View child;
		if (!mDataChanged) {
			child = mRecycler.get(position);
			if (child != null) {
				// Can reuse an existing view
				int childLeft = child.getLeft();

				// Remember left and right edges of where views have been placed
				mRightMost = Math.max(mRightMost, childLeft + child.getMeasuredWidth());
				mLeftMost = Math.min(mLeftMost, childLeft);

				// Position the view
				if (mIsEnableHideScreen && position == 0) {
					if (mHideScreenWidth == 0) {
						mHideScreenWidth = child.getMeasuredWidth() + mHidePadding;
					}
					setUpChild(child, offset, -mHideScreenWidth, fromLeft);
				} else {
					setUpChild(child, offset, x, fromLeft);
				}

				return child;
			}
		}

		// Nothing found in the recycler -- ask the adapter for a view
		child = mAdapter.getView(position, null, this);

		// Position the view
		setUpChild(child, offset, x, fromLeft);

		return child;
	}

	/**
	 * 放置子View Helper for makeAndAddView to set the position of a view and fill
	 * out its layout parameters.
	 * 
	 * @param child
	 *            The view to position
	 * @param offset
	 *            Offset from the selected position
	 * @param x
	 *            X-coordinate indicating where this view should be placed. This
	 *            will either be the left or right edge of the view, depending
	 *            on the fromLeft parameter
	 * @param fromLeft
	 *            Are we positioning views based on the left edge? (i.e.,
	 *            building from left to right)?
	 */
	private void setUpChild(View child, int offset, int x, boolean fromLeft) {

		// Respect layout params that are already in the view. Otherwise
		// make some up...
		LayoutParams lp = (LayoutParams) child.getLayoutParams();
		if (lp == null) {
			lp = (LayoutParams) generateDefaultLayoutParams();
		}

		addViewInLayout(child, fromLeft != mIsRtl ? -1 : 0, lp);

		// child.setSelected(offset == 0);

		FlowListener l = (FlowListener) child;
		int expandWidth = l.getExpandWidth();
		if (expandWidth <= 0) {
			expandWidth = lp.width;
		}

		x -= l.getExpandDistance();
		// Get measure specs
		int childHeightSpec = getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
		int childWidthSpec = getChildMeasureSpec(mWidthMeasureSpec, mSpinnerPadding.left + mSpinnerPadding.right, expandWidth);

		// Measure child
		child.measure(childWidthSpec, childHeightSpec);

		int childLeft;
		int childRight;

		// Position vertically based on gravity setting
		int childTop = calculateTop(child, true);
		int childBottom = childTop + child.getMeasuredHeight();

		int width = child.getMeasuredWidth();
		if (mIsEnableHideScreen && mSelectedPosition == 0 && offset == 0) {
			if (mHideScreenWidth == 0) {
				mHideScreenWidth = width + mHidePadding;
			}
			x = -mHideScreenWidth;
		}
		if (fromLeft) {
			childLeft = x;
			childRight = childLeft + width;
		} else {
			childLeft = x - width;
			childRight = x;
		}

		child.layout(childLeft, childTop, childRight, childBottom);
		// 初始化位置
		// if (childRight <= 0 || childLeft >= getWidth()) {
		// child.setVisibility(View.INVISIBLE);
		// }
	}

	/**
	 * 根据布局来计算子View的Top位置 Figure out vertical placement based on mGravity
	 * 
	 * @param child
	 *            Child to place
	 * @return Where the top of the child should be
	 */
	private int calculateTop(View child, boolean duringLayout) {
		int myHeight = duringLayout ? getMeasuredHeight() : getHeight();
		int childHeight = duringLayout ? child.getMeasuredHeight() : child.getHeight();

		int childTop = 0;

		switch (mGravity) {
		case Gravity.TOP:
			childTop = mSpinnerPadding.top;
			break;
		case Gravity.CENTER_VERTICAL:
			int availableSpace = myHeight - mSpinnerPadding.bottom - mSpinnerPadding.top - childHeight;
			childTop = mSpinnerPadding.top + (availableSpace / 2);
			break;
		case Gravity.BOTTOM:
			childTop = myHeight - mSpinnerPadding.bottom - childHeight;
			break;
		}
		return childTop;
	}

	@Override
	int getChildHeight(View child) {
		return child.getMeasuredHeight();
	}

	/**
	 * @return The center of this Gallery.
	 */
	private int getCenterOfGallery() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingRight();
	}

	/**
	 * @return The center of the given view.
	 */
	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	// //////////////////////////////////////////////////////
	// 入场动画模块，由flowview统一控制入场改为子view来负责
	// //////////////////////////////////////////////////////

	// float floatCoef = 0.3f;// 入场位置
	// int frameCount = 5;// 入场间距
	// FlowInfo mInfo = null;// 入场信息
	// int maxFlowOffset = 30;// 无效
	// int flowDuration = 1000;// 入场周期
	// FlowRunnable mFlowRunnable = new FlowRunnable();

	public void prepareFlow() {
		if (getChildCount() < 1) {
			return;
		}
		Log.d(TAG, "prepare flow!");
//		mHandler.removeMessages(MSG_EXPAND);
		mFlingRunnable.endFling(false);

		if (mIsEnableHideScreen && mSelectedPosition == 0) {// 如果在负一屏入场需要先平移
			Log.d(TAG, "prepare flow reset position");
			int currentLeft = getChildAt(1).getLeft() + ((FlowListener) getChildAt(1)).getExpandDistance();
			int newLeft = mLeftPadding;
			int rightDistance = currentLeft - newLeft;
			int currentRight = getChildAt(0).getRight() + mHidePadding;// 隐藏层相对偏移位置
			int leftDistance = -currentRight;
			getChildAt(0).offsetLeftAndRight(leftDistance);
			for (int i = 1; i < mItemCount; i++) {
				getChildAt(i).offsetLeftAndRight(-rightDistance);
			}
			if (mItemSelectedListener != null) {
				mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, false, this);
			}

			mSelectedPosition = 1;
			DeepListener select = (DeepListener) getSelectedView();
			if (select.canDeep()) {
				if (mDeep != null) {
					mDeep.onFocusDeeped(false, 0, null);
					mLastDeep = null;
				}
				mDeep = select;
				getSelectedView().setSelected(true);
			}
			reset();
			if (mItemSelectedListener != null) {
				mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, true, this);
			}
		}

		updatePosition();
		mFirstFlowPosition = mFirstPosition;
		mLastFlowPosition = mLastPosition;
		Log.d(TAG, "Prepare start Flow position start from:"+mFirstFlowPosition+";end with:"+mLastFlowPosition + "mSelected Position"+mSelectedPosition);
		for (int i = mFirstFlowPosition; i <= mLastFlowPosition; i++) {
			View child = getChildAt(i);
			if (child instanceof RotateLayout) {
				((RotateLayout) child).mWaitStartFlow = true;
				child.invalidate();
			}
		}
	}
	
	public void hideView() {
		updatePosition();
		for (int i = mFirstPosition; i <= mLastPosition; i++) {
			View child = getChildAt(i);
			if (child instanceof RotateLayout) {
				Log.d(TAG, "child "+i+"need reset flag");
				((RotateLayout) child).mWaitStartFlow = true;
				child.invalidate();
			}
		}
	}
	
	public void showView() {
		for (int i = mFirstPosition; i <= mLastPosition; i++) {
			View child = getChildAt(i);
			if (child instanceof RotateLayout) {
				if(((RotateLayout) child).mWaitStartFlow) {
					Log.d(TAG, "child "+i+"need reset flag");
					((RotateLayout) child).mWaitStartFlow = false;
					child.invalidate();
				}
			}
		}
	}

	public void startFlow() {
		Log.d(TAG, "startFlow");
		// 如果还没有展开，则不进行展开
		if (mFlowViewStateListener != null) {
			mFlowViewStateListener.onFlowStart(mLastFlowPosition - mFirstFlowPosition + 1);
		}
		isFlowing = true;
		Log.d(TAG, "start Flow position start from:"+mFirstFlowPosition+";end with:"+mLastFlowPosition);
		for (int i = mFirstFlowPosition; i <= mLastFlowPosition; i++) {
			View child = getChildAt(i);
			FlowListener l = (FlowListener) child;
			l.setCurrentVisibleIndex(i - mFirstFlowPosition);
			l.setFirstVisiblePositionOnFlow(mFirstFlowPosition);
			l.start();
		}
		mFlowAnimRunnable.start();
		// createAdmitFlowInfo();
		// offsetAdmitFlow();
		// admitFlow();
	}

    
    public void stopFlowView(){
        for (int i = mFirstPosition; i <= mLastPosition; i++) {
            View child = getChildAt(i);
            FlowListener l = (FlowListener) child;
            l.setCurrentVisibleIndex(i - mFirstPosition);
            l.stop();
        }
    }

	int total = 0;

	// /**
	// * 创建入场滑动信息
	// */
	// private void createAdmitFlowInfo() {
	// if (mIsEnableHideScreen && mSelectedPosition == 1) {
	// mFirstPosition = 1;
	// }
	// View prevIterationView = getChildAt(mFirstPosition);
	// if (mInfo != null) {
	// mInfo.map.clear();
	// mInfo = null;
	// }
	//
	// total = 0;
	// mInfo = new FlowInfo();
	// int flowRight = (int) (getWidth() * floatCoef);
	// int flowLeft = prevIterationView.getLeft();
	// int distance = flowRight - flowLeft;
	// mInfo.distance = distance;
	// if (localLOGV) {
	// Log.d(TAG, "createAdmitFlowInfo flow distance = " + distance);
	// }
	// for (int i = mFirstPosition; i <= mLastPosition; i++) {
	// View child = getChildAt(i);
	// mInfo.map.put(i, new Info(child, distance, i * frameCount));
	// }
	// }

	// /**
	// * 根据入场滑动距离重新设置子View
	// */
	// private void offsetAdmitFlow() {
	// for (int i = mFirstPosition; i <= mLastPosition; i++) {
	// View child = getChildAt(i);
	// child.offsetLeftAndRight(mInfo.distance);
	// }
	// }

	// /**
	// * 开始入场滑动
	// */
	// private void admitFlow() {
	// mFlowRunnable.flow();
	// }

	// /**
	// * 触发各个子View进行入场滑动，目前按照帧数来决定不同View的滑动时间
	// *
	// * @return
	// */
	// private boolean trackAdmitFlow() {
	// // if (localLOGV) {
	// // Log.d(TAG, "trackFlow");
	// // }
	// if (!mInfo.start) {
	// if (mInfo.map.size() <= 0) {
	// throw new IllegalArgumentException(
	// "trackFlow error, info size is 0");
	// }
	//
	// if (!mInfo.map.containsKey(mFirstPosition)) {
	// throw new IllegalArgumentException(
	// "trackFlow error, info map does not contain first position, mFirstPosition = "
	// + mFirstPosition);
	// }
	//
	// if (maxFlowOffset > 0) {
	// // mInfo.map.get(mFirstPosition).flow();
	// } else {
	// for (int index = mFirstPosition; index <= mLastPosition; index++) {
	// Info info = mInfo.map.get(index);
	// info.flow();
	// }
	// }
	// mInfo.start = true;
	// return true;
	// }
	//
	// // Info lastInfo = null;
	// boolean hr = false;
	// for (int index = mFirstPosition; index <= mLastPosition; index++) {
	// Info info = mInfo.map.get(index);
	// if (info == null) {
	// continue;
	// }
	//
	// if (info.flowing && !info.scroller.isFinished()) {
	// if (info.scroller.computeScrollOffset()) {
	// int xLeft = info.scroller.getCurrX();
	// int offset = xLeft - info.currX;
	// info.currX = xLeft;
	// total += offset;
	// View child = getChildAt(index);
	// child.offsetLeftAndRight(-offset);
	//
	// hr = true;
	// }
	// } else {
	// if (localLOGV) {
	// Log.d(TAG, "trackAdmitFlow total offset = " + total
	// + ", currX = " + info.currX + ", distance = "
	// + info.distance);
	// }
	// }
	//
	// if (mInfo.frame == info.frame) {
	// info.flow();
	// hr = true;
	// }
	//
	// if (!info.flowing) {
	// hr = true;
	// }
	// }
	//
	// mInfo.frame++;
	//
	// return hr;
	// }

	// /**
	// * 一次入场动画的所有子view的flow信息
	// */
	// class FlowInfo {
	// public int distance = 0;
	// public Map<Integer, Info> map = new HashMap<Integer, Info>();
	// public boolean start = false;
	// public int frame = 0;
	// }
	//
	// /**
	// * 单个子view的入场信息，
	// */
	// class Info {
	// public int distance;
	// public int currX = 0;
	// public Scroller scroller = new Scroller(getContext(),
	// new DecelerateInterpolator(1.5f));
	//
	// boolean flowing = false;
	// View child = null;
	// int frame;
	//
	// public Info(View c, int d, int f) {
	// distance = d;
	// child = c;
	// child.setVisibility(View.INVISIBLE);
	// frame = f;
	// }
	//
	// public void flow() {
	// scroller.startScroll(0, 0, distance, 0, flowDuration);
	// currX = 0;
	// flowing = true;
	// FlowListener l = (FlowListener) child;
	// l.start();
	// child.setVisibility(View.VISIBLE);
	// }
	//
	// public void stop() {
	// flowing = false;
	// }
	// }

	// /**
	// * 入场Runnable
	// *
	// * @author Alfred
	// *
	// */
	// class FlowRunnable implements Runnable {
	//
	// public void flow() {
	// post(this);
	// }
	//
	// public void stop() {
	// mInfo.start = false;
	// mInfo.frame = 0;
	// if (mFlowStopListener != null) {
	// mFlowStopListener.stop();
	// }
	// }
	//
	// @Override
	// public void run() {
	// if (trackAdmitFlow()) {
	// postInvalidate();
	// post(this);
	// } else {
	// stop();
	// }
	//
	// }
	//
	// }

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		super.onFling(e1, e2, velocityX, velocityY);
		// Fling the gallery!
		mFlingRunnable.startUsingVelocity((int) -velocityX);

		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

		if (DEBUG)
			Log.v(TAG, String.valueOf(e2.getX() - e1.getX()));

		super.onScroll(e1, e2, distanceX, distanceY);

		// Track the motion
		trackMotionScroll(-1 * (int) distanceX);

		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {

		super.onDown(e);
		// Kill any existing fling/scroll
		mFlingRunnable.stop(false);

		// Must return true to get matching events for this down event.
		return true;
	}

	/**
	 * Called when a touch event's action is MotionEvent.ACTION_UP.
	 */
	@Override
	protected void onUp() {
		super.onUp();
	}

	// //////////////////////////////////////////////////////
	// 焦点、按键逻辑
	// //////////////////////////////////////////////////////

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (getChildCount() > 0) {
			// getFocusParams();
			if (gainFocus) {
				DeepListener select = (DeepListener) getSelectedView();
				if (select.canDeep()) {
					mDeep = select;
                    Rect rect = new Rect(previouslyFocusedRect);
                    offsetRectIntoDescendantCoords((View)mDeep, rect);
					mDeep.onFocusDeeped(gainFocus, direction, rect);
				}
				if (DEBUG) {
					Log.d(TAG, "onFocusChanged gainFocus=" + gainFocus);
				}
				reset();
			} else {
                Rect rect = new Rect(previouslyFocusedRect);
                offsetRectIntoDescendantCoords((View)mDeep, rect);
				mDeep.onFocusDeeped(gainFocus, direction, rect);
				mLastDeep = null;
			}
		}

		mIsAnimate = true;
	}

	@Override
	public View getSelectedView() {
		if (mItemCount > 0 && mSelectedPosition >= 0) {
			return getChildAt(mSelectedPosition);
		} else {
			return null;
		}
	}

	public View getSelectedView(int position) {
		if (mItemCount > 0 && position >= 0) {
			return getChildAt(position);
		} else {
			return null;
		}
	}

	@Override
	public FocusRectParams getFocusParams() {
//		Log.d(TAG, "FocusParams " + mFocusRectparams.focusRect().toString());
		return mFocusRectparams;
	}

	@Override
	public boolean canDraw() {
		if (mDeep != null) {
			return mDeep.canDraw();
		}
		return getSelectedView() != null;
	}

	@Override
	public boolean isAnimate() {
		if (mDeep != null) {
			return mDeep.isAnimate();
		}
		return mIsAnimate;
	}

	/**
	 * 获取焦点对象
	 * 
	 * @return
	 */
	private ItemListener getDeep() {
		if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {
			return mDeep.getItem();
		} else if (mLastDeep != null && mLastDeep.canDeep()) {
			return mLastDeep.getItem();
		}

		return null;
	}

	private ItemListener getSelectedItemListener() {
		View v = getSelectedView();
		ItemListener item;
		if (v instanceof DeepListener) {
			DeepListener deep = (DeepListener) v;
			item = deep.getItem();
		} else {
			item = (ItemListener) v;
		}
		return item;
	}

	@Override
	public ItemListener getItem() {
		ItemListener item = getDeep();
		if (item != null) {
			return item;
		}
		return getSelectedItemListener();

	}

	@Override
	public Params getParams() {
		if (mParams == null) {
			throw new IllegalArgumentException(TAG + ": The params is null, you must call setScaleParams before it's running");
		}

		return mParams;
	}

	/**
	 * 重置焦点框位置
	 */
	private void reset() {

		ItemListener item = getDeep();
		if (item == null) {
			item = (ItemListener) getSelectedView();
		}
		if (item != null) {
			mFocusRectparams.set(item.getFocusParams());
		}
		offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
		if (DEBUG) {
			Log.d(TAG, "rest rect:" + mFocusRectparams.focusRect());
		}
	}

	/**
	 * 重置焦点框位置到选中位置
	 */
	private void resetSelected() {
		ItemListener item = getSelectedItemListener();
		if (item != null) {
			mFocusRectparams.set(item.getFocusParams());
		}

		offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
		if (DEBUG) {
			Log.d(TAG, "resetSelected rect:" + mFocusRectparams.focusRect());
		}
	}

	/**
	 * 设置焦点矩形
	 */
	@Override
	public void getFocusedRect(Rect r) {
		if (hasFocus()) {
			super.getFocusedRect(r);
			if (mIsEnableHideScreen) {
				r.left += getChildAt(1).getLeft();// 这个以后接入-1屏需要修改
			} else {
				r.left += getChildAt(0).getLeft();
			}
			// r.right += getChildAt(0).getLeft();
			return;
		}

		getDrawingRect(r);
	}

	/**
	 * 获取焦点Rect
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	Rect getFocusedRect(View from, View to) {
		Rect rFrom = new Rect();
		rFrom.set(mFocusRectparams.focusRect());
		if(rFrom.isEmpty()){
		    from.getDrawingRect(rFrom);
		    offsetDescendantRectToMyCoords(from, rFrom);
		}
		
		Rect rTo = new Rect();
		if (to == null) {
			to = getSelectedView();
		}
		to.getDrawingRect(rTo);
		offsetDescendantRectToMyCoords(to, rTo);

		int xDiff = rFrom.left - rTo.left;
		int yDiff = rFrom.top - rTo.top;
		int rWidth = rFrom.width();
		int rheight = rFrom.height();
		rFrom.left = xDiff;
		rFrom.right = rFrom.left + rWidth;
		rFrom.top = yDiff;
		rFrom.bottom = rFrom.top + rheight;

		return rFrom;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mIsReset || mInLayout) {
			return true;
		}
		if (event.dispatch(this, null, this)) {
			return true;
		}
		return false;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// if(isFlowing) {
		// postInvalidateDelayed(30);
		// }
	}

	/**
	 * 处理焦点信息，修改选中位置，触发onItemSelectedListener
	 */
	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		// mLastDeep = null;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			mNextDirection = FOCUS_LEFT;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mNextDirection = FOCUS_RIGHT;
			break;
		}

		if (mDeep != null) {
			if (mDeep.preOnKeyDown(keyCode, event)) {
				return true;
			}
		}

		View nextFocus = null;
		boolean hr = false;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			// mNextDirection = FOCUS_LEFT;
			hr = mSelectedPosition > 0 ? true : false;
			if (hr) {
				if (mItemSelectedListener != null) {
					mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, false, this);
				}
//				mSelectedPosition--;
				nextFocus = getSelectedView(mSelectedPosition - 1);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			// mNextDirection = FOCUS_RIGHT;
			hr = mSelectedPosition < mItemCount - 1 ? true : false;
			if (hr) {
				if (mItemSelectedListener != null) {
					mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, false, this);
				}
//				mSelectedPosition++;
				nextFocus = getSelectedView(mSelectedPosition + 1);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			hr = true;
			break;
		default:
			break;
		}

		if (nextFocus != null) {
			if (mDeep != null) {
				mDeep.onFocusDeeped(false, 0, null);
				mLastDeep = mDeep;
				mDeep = null;
			}

			mNextFocus = nextFocus;
			if (nextFocus instanceof DeepListener) {
				mDeep = (DeepListener) nextFocus;
				if (!mDeep.canDeep()) {
					mDeep = null;
				}
			}
			hr = true;
		} else {
			hr = false;
		}

		return hr;
	}

	/**
	 * Handles left, right, and clicking
	 * 
	 * @see View#onKeyDown
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mItemClickListener != null) {
				mItemClickListener.click(getChildAt(mSelectedPosition));
			}
		}

		if (mDeep != null && mDeep.canDeep()) {
			if (mDeep.hasDeepFocus()) {
				if (mDeep.onKeyDown(keyCode, event)) {
					Log.d(TAG, "onKeyDown mDeep.onKeyDown");
					reset();
					if (mScrollFlowAction.isScrolling()) {// 子控件需要处理位置偏移
						int offset = mScrollFlowAction.getItemLeftDistance(mSelectedPosition);
						mFocusRectparams.focusRect().offset(offset, 0);
					}
				}
			} else {
				if (DEBUG) {
					Log.d(TAG, "mNextDirection" + mNextDirection + "keycode:" + keyCode + "'position" + mSelectedPosition + "mSelectedView" + getSelectedView());
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					changePosition(true);
					mDeep.onFocusDeeped(true, mNextDirection, getFocusedRect(getSelectedView(), mNextFocus));
					movePrevious();
					playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					changePosition(false);
					mDeep.onFocusDeeped(true, mNextDirection, getFocusedRect(getSelectedView(), mNextFocus));
					moveNext();
					playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT);
				}
				// reset();
			}
			return true;
		}

		switch (keyCode) {

		case KeyEvent.KEYCODE_DPAD_LEFT:
			changePosition(true);
			if (movePrevious()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			changePosition(false);
			if (moveNext()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			mReceivedInvokeKeyDown = true;
			// fallthrough to default handling
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {
			return mDeep.onKeyUp(keyCode, event);
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER: {

			if (mReceivedInvokeKeyDown) {
				if (mItemCount > 0) {
					dispatchPress(mSelectedChild);
					postDelayed(new Runnable() {
						@Override
						public void run() {
							dispatchUnpress();
						}
					}, ViewConfiguration.getPressedStateDuration());

					int selectedIndex = mSelectedPosition - mFirstPosition;
					performItemClick(getChildAt(selectedIndex), mSelectedPosition, mAdapter.getItemId(mSelectedPosition));
				}
			}

			// Clear the flag
			mReceivedInvokeKeyDown = false;

			return true;
		}
		}

		return super.onKeyUp(keyCode, event);
	}
	
	void changePosition(boolean left) {
		if (left && mItemCount > 0 && mSelectedPosition > 0) {
			mSelectedPosition --;
		} else if (!left && mItemCount > 0 && mSelectedPosition < mItemCount -1) {
			mSelectedPosition ++;
		}
	}

	/**
	 * 往左移，mIsNext为false，触发itemselected
	 */
	boolean movePrevious() {
		if (mItemCount > 0 && mSelectedPosition >= 0) {
			mIsNext = false;
			if (mItemSelectedListener != null) {
				mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, true, this);
			}
			scrollFlow();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 往右移，mIsNext为true，触发itemselected
	 */
	boolean moveNext() {
		if (mItemCount > 0 && mSelectedPosition < mItemCount) {
			mIsNext = true;
			if (mItemSelectedListener != null) {
				mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, true, this);
			}
			scrollFlow();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 计算View滑动信息 1.负一屏滚动逻辑和其他VIEW的滚动逻辑不一样，负一屏只做2次滚动，0->1和1->0时滚动，滚动参考系为左边界
	 * 非负一屏滚动逻辑以View中心为参考系 2.计算位置都是以卡片未展开的左边界值
	 */
	private void scrollFlow() {
		if (DEBUG) {
			Log.d(TAG, "mCurrent Focus Position" + mSelectedPosition );
		}
		mHandler.removeMessages(MSG_EXPAND);
		View selectedView = getSelectedView();
		int itemCenter = (selectedView.getLeft() + selectedView.getRight()) / 2;
		int center = getWidth() / 2;
		int delta;
		int maxMove;
		int leftMostIndex;
		if (mIsEnableHideScreen) {
			leftMostIndex = 1;
		} else {
			leftMostIndex = 0;
		}

		if (mIsNext) {
			// 按右键，View左移
			if (mIsEnableHideScreen && mSelectedPosition == 1) {
				// 负一屏单独处理
				View hideView = getChildAt(0);
				delta = -(hideView.getRight() + mHidePadding);
				Log.d(TAG, "scrollFlow mIsNext hideView right=" + hideView.getRight() + " mHidePadding=" + mHidePadding + " delta=" + delta);
			} else {
				View rightMostChild = getChildAt(mItemCount - 1);// 最右边的View
				FlowListener rightMostlistener = (FlowListener) rightMostChild;
				int rightBind = rightMostChild.getRight() - rightMostlistener.getExpandDistance();
				int rightPadding = getRightMostPadding();
				maxMove = rightBind - (getWidth() - rightPadding);
				delta = -Math.min(maxMove, itemCenter - center);
				Log.d(TAG, "scrollFlow mIsNext rightBind=" + rightBind + " maxMove=" + maxMove + " itemCenter=" + itemCenter + " center=" + center + " delta=" + delta);
			}
			// if (delta > 0) {
			// delta = 0;
			// }
		} else {
			// 按左键，View右移
			if (mIsEnableHideScreen && mSelectedPosition == 0) {
				// 负一屏单独处理
				View hideView = getChildAt(0);
				int leftPadding = getLeftMostPadding(0);
				delta = -hideView.getLeft() + leftPadding;
				Log.d(TAG, "scrollFlow mIsPre hideView left=" + hideView.getLeft() + " leftPadding=" + leftPadding + " delta=" + delta);
			} else {
				View leftMostChild = getChildAt(leftMostIndex);
				FlowListener leftMostlistener = (FlowListener) leftMostChild;
				int leftBind = leftMostChild.getLeft() + leftMostlistener.getExpandDistance();
				int leftPadding = getLeftMostPadding(leftMostIndex);
				maxMove = leftPadding - leftBind;
				delta = Math.min(center - itemCenter, maxMove);
				Log.d(TAG, "scrollFlow mIsPre leftBind=" + leftBind + " maxMove=" + maxMove + " itemCenter=" + itemCenter + " center=" + center + " delta=" + delta);
			}
			// if (delta < 0) {
			// delta = 0;
			// }
		}

		mScrollFlowAction.resetAction();
		FlowListener selectedItemFlow = (FlowListener) getChildAt(mSelectedPosition);
		int selectedTarget = getChildAt(mSelectedPosition).getLeft() + selectedItemFlow.getExpandDistance() + delta;
		mScrollFlowAction.setItemTargetOffset(mSelectedPosition, selectedTarget);
		// left, 计算选中item左边item的位置
		int leftBorder = selectedTarget;
		for (int i = mSelectedPosition - 1; i >= 0 && i < mSelectedPosition; i--) {
			View currChildView = getChildAt(i);
			FlowListener itemFlow = (FlowListener) currChildView;
			leftBorder = leftBorder - (currChildView.getWidth() - 2 * (itemFlow.getExpandDistance() + mSpacing));

			// 隐藏屏需要加左边界padding值
			if (i == 0 && mIsEnableHideScreen) {
				leftBorder -= getLeftMostPadding(leftMostIndex);
			}
			mScrollFlowAction.setItemTargetOffset(i, leftBorder);
		}

		// 如果出了左边的边界
		int leftPadding = getLeftMostPadding(0);
		if (leftBorder > leftPadding) {
			leftBorder = leftPadding;
			// 以最左边为基准往右重新计算位置
			for (int i = 0; i < mItemCount; i++) {
				mScrollFlowAction.setItemTargetOffset(i, leftBorder);
				View currChildView = getChildAt(i);
				FlowListener itemFlow = (FlowListener) currChildView;

				// 重新设置当前选中移动位置
				if (i == mSelectedPosition) {
					delta = mScrollFlowAction.getItemLeftDistance(i);
				}
				leftBorder = leftBorder + (currChildView.getWidth() - 2 * (itemFlow.getExpandDistance() + mSpacing));

				// 隐藏屏需要加左边界padding值
				if (i == 0 && mIsEnableHideScreen) {
					leftBorder += getLeftMostPadding(leftMostIndex);
				}
			}
		} else {
			// right, 选中item右边item的位置
			int rightBorder = selectedTarget;
			for (int i = mSelectedPosition + 1; i >= 0 && i < mItemCount; i++) {
				View preChildView = getChildAt(i - 1);
				FlowListener itemFlow = (FlowListener) preChildView;
				rightBorder = rightBorder + (preChildView.getWidth() - 2 * (itemFlow.getExpandDistance() + mSpacing));

				// 隐藏屏需要加左边界padding值
				if ((i - 1) == 0 && mIsEnableHideScreen) {
					rightBorder += getLeftMostPadding(leftMostIndex);
				}
				mScrollFlowAction.setItemTargetOffset(i, rightBorder);
			}
			// 如果出了右边的边界
			int rightPadding = getRightMostPadding();
			View rightMostChild = getChildAt(mItemCount - 1);
			FlowListener rightMostlistener = (FlowListener) rightMostChild;
			int rightExpendWidth = rightMostChild.getWidth() - (rightMostlistener.getExpandDistance() * 2);
			if ((rightBorder + rightExpendWidth) < (getWidth() - rightPadding)) {
				rightBorder = getWidth() - rightPadding - rightExpendWidth;
				// 以最右边为基准往左重新计算位置
				for (int i = mItemCount - 1; i >= 0; i--) {
					View currChildView = getChildAt(i);
					FlowListener itemFlow = (FlowListener) currChildView;
					if (i < (mItemCount - 1)) {
						rightBorder = rightBorder - (currChildView.getWidth() - 2 * (itemFlow.getExpandDistance() + mSpacing));
					}
					// 隐藏屏需要加间隔
					if (i == 0 && mIsEnableHideScreen) {
						rightBorder -= getLeftMostPadding(leftMostIndex);
					}
					mScrollFlowAction.setItemTargetOffset(i, rightBorder);

					// 隐藏屏需要加左边界padding值
					if (i == mSelectedPosition) {
						delta = mScrollFlowAction.getItemLeftDistance(i);
					}
				}
			}
		}

		reset();// 重置焦点
		if (delta != 0) {
			mIsAnimate = true;
			mFocusRectparams.focusRect().left += delta;
			mFocusRectparams.focusRect().right += delta;
		} else {
			mIsAnimate = false;
		}

		Log.d(TAG, "scrollFlow delta=" + delta);
		mScrollFlowAction.setDuration(mScrollFlowDuration);
		mFlingRunnable.scrollFlow();
	}

	/**
	 * 取得最左边界值
	 * 
	 * @param leftMostIndex
	 * @return
	 */
	private int getLeftMostPadding(int leftMostIndex) {
		View leftMostChild = getChildAt(leftMostIndex);// 指定最左边的View
		FlowListener leftMostlistener = (FlowListener) leftMostChild;
		return Math.max(leftMostlistener.getExpandDistance(), mLeftPadding);
	}

	/**
	 * 取得最右边界值
	 * 
	 * @return
	 */
	private int getRightMostPadding() {
		View rightMostChild = getChildAt(getChildCount() - 1);// 最右边的View
		FlowListener rightMostlistener = (FlowListener) rightMostChild;
		return Math.max(rightMostlistener.getExpandDistance(), mRightpadding);
	}

	private void expandFlow() {
		FlowListener listener = (FlowListener) getSelectedView();
		mFlingRunnable.expandFlow(listener.getExpandDistance(), mExpandFlowDuration);
	}

	@Override
	protected boolean scrollToChild(int childPosition) {
		View child = getChildAt(childPosition);

		if (child != null) {
			int distance = getCenterOfGallery() - getCenterOfView(child);
			mFlingRunnable.startUsingDistance(distance);
			return true;
		}

		return false;
	}

	private boolean mIsReset = false;
    private int mDefaultPosition = 0;
	
    public boolean isReset() {
        return mIsReset;
    }
	
	public boolean canReset(){
	    return canResetToPosition(mDefaultPosition);
	}
	private boolean canResetToPosition(int pos) {
	    if(pos == 0 && mIsEnableHideScreen){
	        return false;
	    }
		return !mIsReset && mSelectedPosition != pos;
	}
		
	public void setDefaultPosition(int pos){
	    mDefaultPosition = pos;
	}
	
	public void resetToDefaultPosition(){
	    resetToPosition(mDefaultPosition);
	}
	
	/**
	 * 回到第一个Item
	 */
	private void resetToPosition(int pos) {
		if (!canResetToPosition(pos)) {
			Log.e(TAG, "canRset false");
			return;
		} else {
			Log.d(TAG, "reset to first begin!!!");
		}

		if (mItemSelectedListener != null) {
			mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, false, this);
		}

		View lastSelectedView = getSelectedView();
		int lastSelectPosition = mSelectedPosition;
		int preSelected = mSelectedPosition;
		mSelectedPosition = pos;

		if (mDeep.canDeep() && mDeep.hasDeepFocus()) {
			mDeep.onFocusDeeped(false, 0, null);
		}

		View selectView = getSelectedView();
		if (selectView instanceof DeepListener) {
			DeepListener deep = (DeepListener) selectView;
			if (deep.canDeep()) {
				mLastDeep = null;
				mDeep = deep;
				mDeep.onFocusDeeped(true, mSelectedPosition < lastSelectPosition ? FOCUS_LEFT : FOCUS_RIGHT, getFocusedRect(lastSelectedView, getSelectedView()));
			}
		}
		mIsReset = true;

		if (preSelected > mSelectedPosition) {
			mIsNext = false;
		} else {
			mIsNext = true;
		}
		scrollFlow();
	}

	public void smoothScrollBy(int distance) {
		mFlingRunnable.startUsingDistance(distance);
	}

	/**
	 * 分三步滑动 left right other
	 * 
	 * @param offset
	 */
	private void trackLeftCollipseScrollFlow(int offset) {
		if (mIsReset) {
			int index = 0;
			if (mIsEnableHideScreen && mSelectedPosition != 0) {
				index = 1;
			}
			for (; index < mSelectedPosition; index++) {
				getChildAt(index).offsetLeftAndRight(offset);
			}
		} else {
			if (mIsNext) {
				getSelectedView(mSelectedPosition - 1).offsetLeftAndRight(offset);
			} else {
				int index = 0;
				if (mIsEnableHideScreen && mSelectedPosition != 0) {
					index = 1;
				}
				for (; index <= mSelectedPosition; index++) {
					getChildAt(index).offsetLeftAndRight(offset);
				}
			}
		}
	}

	/**
	 * 
	 * @param offset
	 */
	private void trackOtherCollipseScrollFlow(int offset) {
		if (mIsReset) {
			getChildAt(mSelectedPosition).offsetLeftAndRight(offset);
		} else {
			if (mIsNext) {
				int index = 0;
				if (mIsEnableHideScreen && mSelectedPosition != 0) {
					index = 1;
				}
				for (; index < mSelectedPosition - 1; index++) {
					getChildAt(index).offsetLeftAndRight(offset);
				}
			} else {
				for (int index = mSelectedPosition + 2; index < mItemCount; index++) {
					getChildAt(index).offsetLeftAndRight(offset);
				}
			}
		}

	}

	/**
	 * 
	 * @param offset
	 */
	private void trackRightCollipseScrollFlow(int offset) {
		if (mIsReset) {
			for (int index = mSelectedPosition + 1; index < mItemCount; index++) {
				getChildAt(index).offsetLeftAndRight(offset);
			}
		} else {
			if (mIsNext) {
				for (int index = mSelectedPosition; index < mItemCount; index++) {
					getChildAt(index).offsetLeftAndRight(-offset);
				}
			} else {
				getSelectedView(mSelectedPosition + 1).offsetLeftAndRight(offset);
			}
		}

	}

	private void trackExpandFlow(int offset) {
		for (int index = mSelectedPosition + 1; index < mItemCount; index++) {
			getChildAt(index).offsetLeftAndRight(offset);
		}
		int index = 0;
		if (mIsEnableHideScreen) {
			index = 1;
		}
		for (; index < mSelectedPosition; index++) {
			getChildAt(index).offsetLeftAndRight(-offset);
		}
	}

	final int MSG_EXPAND = 1;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_EXPAND) {
				mHandler.removeMessages(MSG_EXPAND);
				expandFlow();
			}
		}
	};

	enum ScrollState {
		IDLE, SCROLL_ING, SCROLL_FINISHED, EXPAND_ING, EXPAND_FINISED
	}

	/**
	 * 存储滑动信息类
	 */
	class ScrollFlowAction {
		private long mStartTime;
		private Scroller mExpendScroller = new Scroller(getContext(), new DecelerateInterpolator());
		private int mExpendCurrDistance = 0;
		ScrollState mCurrentState = ScrollState.IDLE;
		int mDuration = 1000;
		Interpolator mInterpolator = new DecelerateInterpolator();
		List<ScrollFlowItem> mScrollFlowItemList = new ArrayList<ScrollFlowItem>();

		void resetAction() {
			for (int i = 0; i < getCount(); i++) {
				ScrollFlowItem item;
				if (i >= mScrollFlowItemList.size()) {
					// 列表里面不存在
					item = new ScrollFlowItem();
					mScrollFlowItemList.add(item);
				} else {
					// 列表里面已经存在
					item = mScrollFlowItemList.get(i);
				}
				FlowListener itemFlow = (FlowListener) getChildAt(i);
				item.mStartOffset = getChildAt(i).getLeft() + itemFlow.getExpandDistance();
				item.mCurrOffset = item.mStartOffset;
				item.mDelta = 0;
				item.mStopOffset = item.mStartOffset;
			}
		}

		void startScroll() {
			mCurrentState = ScrollState.SCROLL_ING;
			mStartTime = System.currentTimeMillis();
		}

		void stopScroll() {
			mCurrentState = ScrollState.SCROLL_FINISHED;
		}

		boolean isScrolling() {
			return mCurrentState == ScrollState.SCROLL_ING ? true : false;
		}

		/**
		 * 计算中间值
		 * 
		 * @return
		 */
		boolean computeScrollFlowOffset() {
			if (mCurrentState != ScrollState.SCROLL_ING) {
				return false;
			}
			boolean result = false;
			int itemSize = mScrollFlowItemList.size();
			for (int i = 0; i < itemSize; i++) {
				ScrollFlowItem item = mScrollFlowItemList.get(i);
				if ((item.mIsPositive && item.mCurrOffset >= item.mStopOffset) || (!item.mIsPositive && item.mCurrOffset <= item.mStopOffset)) {
					// finished
					item.mDelta = 0;
				} else {
					float ratio = (float) (System.currentTimeMillis() - mStartTime) / (float) mDuration;
					if (ratio >= 1.0f) {
						// finished
						item.mDelta = item.mStopOffset - item.mCurrOffset;
					} else {
						float output = ratio;
						if (mInterpolator != null) {
							output = mInterpolator.getInterpolation(ratio);
						}
						item.mDelta = (int) ((item.mStopOffset - item.mStartOffset) * output) - (item.mCurrOffset - item.mStartOffset);
					}

					if ((item.mIsPositive && (item.mCurrOffset + item.mDelta) > item.mStopOffset) || (!item.mIsPositive && (item.mCurrOffset + item.mDelta) < item.mStopOffset)) {
						item.mDelta = item.mStopOffset - item.mCurrOffset;
						item.mCurrOffset = item.mStopOffset;
					} else {
						item.mCurrOffset += item.mDelta;
					}
					result = true;
				}
			}
			if (!result) {
				stopScroll();
			}
			return result;
		}

		/**
		 * 设置指定item的目标位置
		 * 
		 * @param index
		 * @param target
		 */
		void setItemTargetOffset(int index, int target) {
			if (index >= 0 && index < mScrollFlowItemList.size()) {
				ScrollFlowItem item = mScrollFlowItemList.get(index);
				item.mStopOffset = target;

				if (item.mStopOffset > item.mStartOffset) {
					item.mIsPositive = true;
				} else {
					item.mIsPositive = false;
				}
			} else {
				Log.e(TAG, "setItemTargetOffset out of index total=" + mScrollFlowItemList.size() + " curr=" + index);
			}
		}

		int getItemLeftDistance(int index) {
			if (index >= 0 && index < mScrollFlowItemList.size()) {
				ScrollFlowItem item = mScrollFlowItemList.get(index);
				return item.mStopOffset - item.mCurrOffset;
			}
			return 0;
		}

		boolean computeExpendOffset() {
			boolean result = mExpendScroller.computeScrollOffset();
			if (!result) {
				stopExpend();
			}
			return result;
		}

		int getCurrExpendDistance() {
			int delta = mExpendScroller.getCurrY() - mExpendCurrDistance;
			mExpendCurrDistance = mExpendScroller.getCurrY();
			return delta;
		}

		void startExpend(int distance, int duration) {
			mExpendCurrDistance = 0;
			mCurrentState = ScrollState.EXPAND_ING;
			mExpendScroller.startScroll(0, 0, 0, distance, duration);
		}

		void stopExpend() {
			mExpendCurrDistance = 0;
			mCurrentState = ScrollState.EXPAND_FINISED;
		}

		boolean isIdle() {
			return mCurrentState == ScrollState.IDLE ? true : false;
		}

		void resetState() {
			mCurrentState = ScrollState.IDLE;
		}

		/**
		 * 取得当前移动的距离
		 * 
		 * @param index
		 * @return
		 */
		int getCurrDelta(int index) {
			if (index >= 0 && index < mScrollFlowItemList.size()) {
				return mScrollFlowItemList.get(index).mDelta;
			}
			return 0;
		}

		void setDuration(int duration) {
			mDuration = duration;
		}

		void setInterpolator(Interpolator interpolator) {
			mInterpolator = interpolator;
		}

	}

	class ScrollFlowItem {
		boolean mIsPositive;// move right or down
		int mDelta;
		int mCurrOffset;
		int mStartOffset;
		int mStopOffset;
	}

	/**
	 * Responsible for fling behavior. Use {@link #startUsingVelocity(int)} to
	 * initiate a fling. Each frame of the fling is handled in {@link #run()}. A
	 * FlingRunnable will keep re-posting itself until the fling is done.
	 */
	private class FlingRunnable implements Runnable {
		public final int MODE_FLOW = 1;
		public final int MODE_EXPAND = 2;

		private int mode = -1;
		/**
		 * Tracks the decay of a fling scroll
		 */
		private Scroller mScroller;

		/**
		 * X value reported by mScroller on the previous fling
		 */
		private int mLastFlingX;

		public FlingRunnable() {
			mScroller = new Scroller(getContext());
		}

		private void startCommon() {
			// Remove any pending flings
			removeCallbacks(this);
		}

		public void startUsingVelocity(int initialVelocity) {
			if (initialVelocity == 0)
				return;

			startCommon();

			int initialX = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
			mLastFlingX = initialX;
			mScroller.fling(initialX, 0, initialVelocity, 0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
			post(this);
		}

		public void startUsingDistance(int distance) {
			if (distance == 0)
				return;

			startCommon();

			mLastFlingX = 0;
			mScroller.startScroll(0, 0, distance, 0, 500);
			post(this);
		}

		public void stop(boolean scrollIntoSlots) {
			removeCallbacks(this);
			endFling(scrollIntoSlots);
		}

		public void forceStop() {
			removeCallbacks(this);
			mScroller.forceFinished(true);
			mScrollFlowAction.resetState();
		}

		/**
		 * 结束滑动 重新计算FirstPosition和LastPosition
		 * 
		 * @param scrollIntoSlots
		 */
		public void endFling(boolean scrollIntoSlots) {
			/*
			 * Force the scroller's status to finished (without setting its
			 * position to the end)
			 */
			mScroller.forceFinished(true);
			updatePosition();
		}

		public void scrollFlow() {
			mode = MODE_FLOW;
			if (mOnScrollListener != null) {
				mOnScrollListener.onScrollStateChanged(FlowView.this, OnScrollListener.SCROLL_STATE_FLING);
			}
			mScrollFlowAction.startScroll();
			removeCallbacks(this);
			post(this);
		}

		public void expandFlow(int distance, int dur) {
			mode = MODE_EXPAND;
			if (mFlowViewStateListener != null) {
				mFlowViewStateListener.onFlowStartExpand(getSelectedView(), mSelectedPosition, FlowView.this);
			}
			mScrollFlowAction.startExpend(distance, dur);
			post(this);
		}

		/**
		 * 滑动
		 */
		public void flow() {
			if (mScrollFlowAction.computeScrollFlowOffset()) {
				for (int i = 0; i < getCount(); i++) {
					getChildAt(i).offsetLeftAndRight(mScrollFlowAction.getCurrDelta(i));
				}
				post(this);
				invalidate();
			} else {
				Log.d(TAG, "flow done mIsReset=" + mIsReset);
				if (mIsReset) {
					mIsReset = false;
					resetSelected();
					mIsAnimate = true;
					if (mItemSelectedListener != null) {
						mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, true, FlowView.this);
					}
					if (mFlowViewStateListener != null) {
						mFlowViewStateListener.onResetFlowFinished();
					}
				}
				if (mOnScrollListener != null) {
					mOnScrollListener.onScrollStateChanged(FlowView.this, OnScrollListener.SCROLL_STATE_IDLE);
				}

				mHandler.sendEmptyMessageDelayed(MSG_EXPAND, mExpandDelayed);
				endFling(false);
			}

		}

		/**
		 * 展开
		 */
		public void expand() {
			if (mScrollFlowAction.computeExpendOffset()) {
				trackExpandFlow(mScrollFlowAction.getCurrExpendDistance());
				post(this);
				invalidate();
			} else {
				if (mFlowViewStateListener != null) {
					mFlowViewStateListener.onFlowFinishExpand(getSelectedView(), mSelectedPosition, FlowView.this);
				}
				endFling(false);
			}
		}

		@Override
		public void run() {
			if (mItemCount == 0) {
				endFling(true);
				return;
			}
			if (mode == MODE_FLOW) {
				flow();
			} else if (mode == MODE_EXPAND) {
				expand();
			}
		}

	}

	/**
	 * Tracks a motion scroll. In reality, this is used to do just about any
	 * movement to items (touch scroll, arrow-key scroll, set an item as
	 * selected).
	 * 
	 * @param deltaX
	 *            Change in X from the previous event.
	 */
	void trackMotionScroll(int deltaX) {

		if (getChildCount() == 0) {
			return;
		}
		offsetChildrenLeftAndRight(deltaX);
		invalidate();
	}

	/**
	 * 暂时为用到
	 * 
	 * @param motionToLeft
	 * @param deltaX
	 * @return
	 */
	int getLimitedMotionScrollAmount(boolean motionToLeft, int deltaX) {
		int extremeItemPosition = motionToLeft != mIsRtl ? mItemCount - 1 : 0;
		View extremeChild = getChildAt(extremeItemPosition - mFirstPosition);

		if (extremeChild == null) {
			return deltaX;
		}

		int extremeChildCenter = getCenterOfView(extremeChild);
		int galleryCenter = getCenterOfGallery();

		if (motionToLeft) {
			if (extremeChildCenter <= galleryCenter) {

				// The extreme child is past his boundary point!
				return 0;
			}
		} else {
			if (extremeChildCenter >= galleryCenter) {

				// The extreme child is past his boundary point!
				return 0;
			}
		}

		int centerDifference = galleryCenter - extremeChildCenter;

		return motionToLeft ? Math.max(centerDifference, deltaX) : Math.min(centerDifference, deltaX);
	}

	public interface ItemClickListener {
		public void click(View v);
	}

	public interface FlowViewStateListener {
		/**
		 * 开始入场监听
		 * @param count
		 */
		public void onFlowStart(int count);
		
		/**
		 * 入场结束监听
		 */
		public void onFLowStop();
		
		/**
		 * 重置结束监听
		 */
		public void onResetFlowFinished();
		
		/**
		 * 展开开始监听
		 * @param v
		 * @param position
		 * @param view
		 */
		public void onFlowStartExpand(View v, int position, View view);
		
		/**
		 * 展开结束监听
		 * @param v
		 * @param position
		 * @param view
		 */
		public void onFlowFinishExpand(View v, int position, View view);
		
		/**
		 * layout结束监听器
		 */
		public void onLayoutFinished();
		
		public void onVisiblePositionChange(int first, int end);
	}
	
	/**
	 * flowview 子item需要实现的接口
	 * @author Alfred
	 *
	 */
	public interface FlowListener {

		public int getExpandDistance();

		public int getExpandWidth();

		public void start();
        
        public void stop();

		public void setCurrentVisibleIndex(int i);
		
		public void setFirstVisiblePositionOnFlow(int i);

		public boolean isFlowFinished();

		public int getFlowItemOffset();

	}

	@Override
	public boolean isScrolling() {
	    if(mScrollFlowAction != null){
	        return mScrollFlowAction.isScrolling();
	    }
	    return false;
//		 boolean hr = (mScrollInfo.currentState != ScrollState.IDLE) &&
//		 (mScrollInfo.currentState != ScrollState.EXPAND_FINISED);
//		 Log.e(TAG, "FlowView is scrolling:"+hr);
//		 return hr;
//		return false;
	}

	/**
	 * 设置初始的可见和不可见，onlayout完成后调用
	 */
	private void updatePosition() {
		mFirstPosition = mLastPosition = mSelectedPosition;
		for (int i = mSelectedPosition; i >= 0; i--) {
			View child = getChildAt(i);
			if (child != null && child.getRight() > 0) {
				mFirstPosition = i;
			} else {
				break;
			}
		}

		for (int i = mSelectedPosition; i < mItemCount; i++) {
			View child = getChildAt(i);
			if (child != null && child.getLeft() < getWidth()) {
				mLastPosition = i;
			} else {
				break;
			}
		}
		
		if(mFlowViewStateListener != null) {
			mFlowViewStateListener.onVisiblePositionChange(mFirstPosition, mLastPosition);
		}

		if (mScrollFlowAction != null) {
			Log.d(TAG, "endFling first:" + mFirstPosition + " last:" + mLastPosition +" selected:" + mSelectedPosition +" state:" + mScrollFlowAction.mCurrentState);
		} else {
			Log.d(TAG, "First ENTER endFling first:" + mFirstPosition + " last:" + mLastPosition +" selected:" + mSelectedPosition);
		}
	}

	@Override
	public boolean isFocusBackground() {
		return mFocusBackground;
	}

	private class FlowAnimRunnable implements Runnable {

		private boolean mIsFinished = true;

		public void start() {
			Log.i(TAG, "FlowAnimRunnable start");
			if (!mIsFinished) {
				return;
			}
			mIsFinished = false;
			invalidate();
			post(this);
		}

		@Override
		public void run() {
			if (mIsFinished) {
				Log.i(TAG, "FlowAnimRunnable mIsFinished can not run");
				return;
			}
			boolean isFinished = true;
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View childView = getChildAt(i);
				if (childView instanceof FlowListener) {
					FlowListener flow = (FlowListener) childView;
					boolean isItemFinished = flow.isFlowFinished();
					// Log.d(TAG, "===========isFinished:"+isItemFinished);
					if (!isItemFinished) {
						isFinished = false;
						childView.invalidate();
						int delta = flow.getFlowItemOffset();
						// Log.d(TAG, "==========delta:"+delta);
						if (delta != 0) {
							childView.offsetLeftAndRight(delta);
						}
					}
				}
			}
			if (isFinished) {
				stop();
			} else {
				invalidate();
				post(this);
			}
		}

		public boolean isFinished() {
			return mIsFinished;
		}

		public void stop() {
			Log.i(TAG, "FlowAnimRunnable stop");
			//检查是否出现异常,入场导致坑位不可见行为，尽量规避
			for (int i = mFirstFlowPosition; i <= mLastFlowPosition; i++) {
				View child = getChildAt(i);
				if (child instanceof RotateLayout) {
					if(((RotateLayout) child).mWaitStartFlow) {
						Log.d(TAG, "child "+i+"need reset flag");
						((RotateLayout) child).mWaitStartFlow = false;
						child.invalidate();
					}
				}
			}
			if(mFlowViewStateListener != null ) {
				mFlowViewStateListener.onFLowStop();
			}
			mFirstFlowPosition = mLastFlowPosition = 0;
			mIsFinished = true;
		}
	}

	@Override
	public Rect getClipFocusRect() {
		// TODO Auto-generated method stub
		return null;
	}
}

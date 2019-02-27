package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.yunos.tv.app.widget.AbsBaseListView.RecycleBin;
import com.yunos.tv.app.widget.HListView;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.lib.SystemProUtils;

import java.util.ArrayList;

public class FocusHListView extends HListView implements DeepListener, ItemListener {
	protected static final String TAG = "FocusHListView";
	protected static final boolean DEBUG = false;
	
	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	FocusRectParams mFocusRectparams = new FocusRectParams();
	protected Rect mClipFocusRect = new Rect();
	
	boolean mIsAnimate = true;
	int mDistance = -1;
	boolean mDeepFocus = false;
	boolean mAutoSearch = false;
	ItemSelectedListener mItemSelectedListener;
	boolean mLayouted = false;
	boolean mReset = false;
	boolean mFocusBackground = false;
	boolean mAimateWhenGainFocusFromLeft = true;
	boolean mAimateWhenGainFocusFromRight = true;
	boolean mAimateWhenGainFocusFromUp = true;
	boolean mAimateWhenGainFocusFromDown = true;
	int mItemWidth;
	
	DeepListener mDeep = null;
	boolean mDeepMode = false;
	
	public FocusHListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FocusHListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusHListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setDeepMode(boolean mode) {
		this.mDeepMode = mode;
	}

	public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
		mAimateWhenGainFocusFromLeft = fromleft;
		mAimateWhenGainFocusFromUp = fromUp;
		mAimateWhenGainFocusFromRight = fromRight;
		mAimateWhenGainFocusFromDown = fromDown;
	}

	public void setFocusBackground(boolean back) {
		mFocusBackground = back;
	}

	public void setOnItemSelectedListener(ItemSelectedListener listener) {
		mItemSelectedListener = listener;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.d(TAG, "onFocusChanged");
		if (!mAutoSearch) {
			if (getOnFocusChangeListener() != null) {
				getOnFocusChangeListener().onFocusChange(this, gainFocus);
			}
		}

		if (mAutoSearch) {
			super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		}

		if (gainFocus) {
			if (getChildCount() > 0 && mLayouted) {
				// getFocusParams();
				if (getLeftScrollDistance() == 0) {
					reset();
				}
			} else{
				mReset = true;
			}

			if (mDeepMode) {
				boolean isDeep = false;
				if (getSelectedView() instanceof DeepListener) {
					mDeep = (DeepListener) getSelectedView();
					if (mDeep.canDeep()) {
						isDeep = true;
						Rect focusRect = new Rect(previouslyFocusedRect);
						offsetRectIntoDescendantCoords((View)mDeep, focusRect);
						mDeep.onFocusDeeped(gainFocus, direction, focusRect);
						reset();
					}
				}

				if (!isDeep) {
					if (!mLayouted) {
						mReset = true;
					} else {
						reset();
						performSelect(gainFocus);
					}
				}
			} else {
				performSelect(gainFocus);
			}
		} else {
			if (mDeepMode) {
				if (mDeep != null && mDeep.canDeep()) {
					mDeep.onFocusDeeped(gainFocus, direction, null);
				} else {
					if (mLayouted) {
						performSelect(gainFocus);
					} else {
						mReset = true;
					}
				}
			} else {
				performSelect(gainFocus);
			}
//			mDeep = null;
		}

		mIsAnimate = checkAnimate(direction);
	}

	private boolean checkAnimate(int direction) {
		switch (direction) {
		case FOCUS_LEFT:
			return mAimateWhenGainFocusFromRight ? true : false;
		case FOCUS_UP:
			return mAimateWhenGainFocusFromDown ? true : false;
		case FOCUS_RIGHT:
			return mAimateWhenGainFocusFromLeft ? true : false;
		case FOCUS_DOWN:
			return mAimateWhenGainFocusFromUp ? true : false;
		}

		return true;
	}

	@Override
	public void setSelection(int position) {
		setSelectedPositionInt(position);
		setNextSelectedPositionInt(position);
		if (getChildCount() > 0 && mLayouted) {
			mLayoutMode = LAYOUT_FROM_MIDDLE;
			if (isLayoutRequested()) {
				return;
			}
			layoutChildren();
		}

		reset();
	}

	@Override
	protected void layoutChildren() {
		final boolean blockLayoutRequests = mBlockLayoutRequests;
		if (!blockLayoutRequests) {
			mBlockLayoutRequests = true;
		} else {
			return;
		}

		try {
			// super.layoutChildren();

			invalidate();

			if (getAdapter() == null) {
				resetList();
				// invokeOnItemScrollListener();
				return;
			}

			int childrenLeft = mListPadding.left;
			int childrenRight = getRight() - getLeft() - mListPadding.right;

			int childCount = getChildCount();
			int index = 0;
			int delta = 0;

			View sel = null;
			View oldSel = null;
			View oldFirst = null;
			View newSel = null;
			View focusLayoutRestoreView = null;
			int lastPosition = getLastVisiblePosition();
			boolean dataChanged = mDataChanged;
			if (dataChanged) {
				mLayoutMode = LAYOUT_FROM_MIDDLE;
			}

			// AccessibilityNodeInfo accessibilityFocusLayoutRestoreNode = null;
			// View accessibilityFocusLayoutRestoreView = null;
			// int accessibilityFocusPosition = INVALID_POSITION;

			// Remember stuff we will need down below
			switch (mLayoutMode) {
			case LAYOUT_SET_SELECTION:
				index = mNextSelectedPosition - mFirstPosition;
				if (index >= 0 && index < childCount) {
					newSel = getChildAt(index);
				}
				break;
			case LAYOUT_FORCE_LEFT:
			case LAYOUT_FORCE_RIGHT:
			case LAYOUT_SPECIFIC:
			case LAYOUT_SYNC:
				break;
			default:
				// Remember the previously selected view
				index = mSelectedPosition - mFirstPosition;
				if (index >= 0 && index < childCount) {
					oldSel = getChildAt(index);
				}

				// Remember the previous first child
				oldFirst = getChildAt(0);

				if (mNextSelectedPosition >= 0) {
					delta = mNextSelectedPosition - mSelectedPosition;
				}

				// Caution: newSel might be null
				newSel = getChildAt(index + delta);
				break;
			}

			if (dataChanged) {
				handleDataChanged();
			}

			// Handle the empty set by removing all views that are visible
			// and calling it a day
			if (mItemCount == 0) {
				resetList();
				// invokeOnItemScrollListener();
				return;
			} else if (mItemCount != getAdapter().getCount()) {
				throw new IllegalStateException("The content of the adapter has changed but "
						+ "ListView did not receive a notification. Make sure the content of "
						+ "your adapter is not modified from a background thread, but only " + "from the UI thread. [in ListView("
						+ getId() + ", " + getClass() + ") with Adapter(" + getAdapter().getClass() + ")]");
			}

			setSelectedPositionInt(mNextSelectedPosition);

			// Pull all children into the RecycleBin.
			// These views will be reused if possible
			final int firstPosition = mFirstPosition;
			final RecycleBin recycleBin = mRecycler;

			// reset the focus restoration
			View focusLayoutRestoreDirectChild = null;

			// Don't put header or footer views into the Recycler. Those are
			// already cached in mHeaderViews;
			if (dataChanged) {
				for (int i = 0; i < childCount; i++) {
					recycleBin.addScrapView(getChildAt(i), firstPosition + i);
				}
			} else {
				recycleBin.fillActiveViews(childCount, firstPosition);
			}

			// take focus back to us temporarily to avoid the eventual
			// call to clear focus when removing the focused child below
			// from messing things up when ViewAncestor assigns focus back
			// to someone else
			final View focusedChild = getFocusedChild();
			if (focusedChild != null) {
				// TODO: in some cases focusedChild.getParent() == null

				// we can remember the focused view to restore after relayout if
				// the
				// data hasn't changed, or if the focused position is a header
				// or footer
				if (!dataChanged || isDirectChildHeaderOrFooter(focusedChild)) {
					focusLayoutRestoreDirectChild = focusedChild;
					// remember the specific view that had focus
					focusLayoutRestoreView = findFocus();
					if (focusLayoutRestoreView != null) {
						// tell it we are going to mess with it
						focusLayoutRestoreView.onStartTemporaryDetach();
					}
				}
				requestFocus();
			}

			// Clear out old views
			detachAllViewsFromParent();
			recycleBin.removeSkippedScrap();

			switch (mLayoutMode) {
			case LAYOUT_SET_SELECTION:
				if (newSel != null) {
					sel = fillFromSelection(newSel.getLeft(), childrenLeft, childrenRight);
				} else {
					sel = fillFromMiddle(childrenLeft, childrenRight);
				}
				break;
			case LAYOUT_SYNC:
				sel = fillSpecific(mSyncPosition, mSpecificLeft);
				break;
			case LAYOUT_MOVE_SELECTION:
				sel = moveSelection(oldSel, newSel, delta, childrenLeft, childrenRight);
				break;
			case LAYOUT_FORCE_RIGHT:
				sel = fillLeft(mItemCount - 1, childrenRight);
				adjustViewsLeftOrRight();
				break;
			case LAYOUT_FORCE_LEFT:
				mFirstPosition = 0;
				sel = fillFromLeft(childrenLeft);
				adjustViewsLeftOrRight();
				break;
			case LAYOUT_SPECIFIC:
				sel = fillSpecific(reconcileSelectedPosition(), mSpecificLeft);
				break;
			case LAYOUT_FROM_MIDDLE:
				sel = fillFromMiddle(childrenLeft, childrenRight);
				break;
			default:
				if (childCount == 0) {
					if (!mStackFromBottom) {
						final int position = lookForSelectablePosition(mSelectedPosition, true);
						setSelectedPositionInt(position);
						sel = fillFromLeft(childrenLeft);
					} else {
						final int position = lookForSelectablePosition(mItemCount - 1, false);
						setSelectedPositionInt(position);
						sel = fillLeft(mItemCount - 1, childrenRight);
					}
				} else {
					int selectPsotion = mSelectedPosition;
					if (mSelectedPosition >= firstPosition + delta && mSelectedPosition <= lastPosition + delta) {
						selectPsotion = mSelectedPosition;
					} else {
						oldSel = oldFirst;
						selectPsotion = mFirstPosition + delta;
					}

					if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {
						sel = fillSpecific(selectPsotion, oldSel == null ? childrenLeft : oldSel.getLeft());
					} else if (mFirstPosition < mItemCount) {
						sel = fillSpecific(mFirstPosition, oldFirst == null ? childrenLeft : oldFirst.getLeft());
					} else {
						sel = fillSpecific(0, childrenLeft);
					}
				}
				break;
			}

			// Flush any cached views that did not get reused above
			recycleBin.scrapActiveViews();

			if (sel != null) {
				// the current selected item should get focus if items
				// are focusable
				if (mItemsCanFocus && hasFocus() && !sel.hasFocus()) {
					final boolean focusWasTaken = (sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView
							.requestFocus()) || sel.requestFocus();
					if (!focusWasTaken) {
						// selected item didn't take focus, fine, but still want
						// to make sure something else outside of the selected
						// view
						// has focus
						final View focused = getFocusedChild();
						if (focused != null) {
							focused.clearFocus();
						}
						positionSelector(INVALID_POSITION, sel);
					} else {
						sel.setSelected(false);
						mSelectorRect.setEmpty();
					}
				} else {
					positionSelector(INVALID_POSITION, sel);
				}
				// mSelectedTop = sel.getTop();
			} else {
				if (mTouchMode > TOUCH_MODE_DOWN && mTouchMode < TOUCH_MODE_SCROLL) {
					View child = getChildAt(mMotionPosition - mFirstPosition);
					if (child != null)
						positionSelector(mMotionPosition, child);
				} else {
					// mSelectedTop = 0;
					mSelectorRect.setEmpty();
				}

				// even if there is not selected position, we may need to
				// restore
				// focus (i.e. something focusable in touch mode)
				if (hasFocus() && focusLayoutRestoreView != null) {
					focusLayoutRestoreView.requestFocus();
				}
			}

			// tell focus view we are done mucking with it, if it is still in
			// our view hierarchy.
			if (focusLayoutRestoreView != null && focusLayoutRestoreView.getWindowToken() != null) {
				focusLayoutRestoreView.onFinishTemporaryDetach();
			}

			mLayoutMode = LAYOUT_NORMAL;
			mDataChanged = false;
			// if (mPositionScrollAfterLayout != null) {
			// post(mPositionScrollAfterLayout);
			// mPositionScrollAfterLayout = null;
			// }
			mNeedSync = false;
			setNextSelectedPositionInt(mSelectedPosition);

			// updateScrollIndicators();

			if (mItemCount > 0) {
				checkSelectionChanged();
			}

			// invokeOnItemScrollListener();
		} finally {
			if (!blockLayoutRequests) {
				mBlockLayoutRequests = false;
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (hasFocus() || hasDeepFocus()) {
			if (getLeftScrollDistance() == 0) {
				reset();
			}
		}

		if (getChildCount() > 0) {
			if (mReset) {
				performSelect(hasFocus() || hasDeepFocus());
				mReset = false;
			}
			
			if (hasFocus()) {
				if (mDeepMode) {
					boolean isDeep = false;
					if (getSelectedView() instanceof DeepListener) {
						mDeep = (DeepListener) getSelectedView();
						if (mDeep.canDeep() && !mDeep.hasDeepFocus()) {
							isDeep = true;
							mDeep.onFocusDeeped(true, FOCUS_LEFT, null);
							reset();
						}
					}

					if (!isDeep) {
						reset();
					}
				}
			}
		} else {
			mReset = true;
		}

		mLayouted = true;		
		mClipFocusRect.set(0, 0, getWidth(), getHeight());
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		mLayouted = false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int index = -1;
		if (getAdapter() != null) {
			// 如果没有头部，则item的高度就是第一个元素的高度
			if (getAdapter().getCount() > 0) {
				index = 0;
			}
			
			// 如果有头部，则item的高度就是头部下方第一个元素的高度
			if (getHeaderViewsCount() > 0 && getAdapter().getCount() > getHeaderViewsCount()) {
				index = getHeaderViewsCount();
			}
			
			if(index >= 0){
				measureItemWidth(index);
			}
		}
	}
	
	protected void measureItemWidth(int index) {
        final View child = obtainView(index, mIsScrap);
        int measureWidth = 0;
        if(child != null){
            measureWidth = child.getMeasuredWidth();
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            if (p == null) {
                p = (LayoutParams) generateDefaultLayoutParams();
                child.setLayoutParams(p);
            }
            //在MagicBox中child.getLayoutParams()的值为空
            //在MagicBox2c和MagicBox2中child.getLayoutParams()的值不为空，
            // 为保险起见需要加上child.getLayoutParams() != null的条件
            if(measureWidth == 0 && child.getLayoutParams() != null) {
                // 如果有item的高度为0，则重新测量高度
                int unSpecified = View.MeasureSpec.UNSPECIFIED;
                int w = View.MeasureSpec.makeMeasureSpec(0,unSpecified);
                int h = View.MeasureSpec.makeMeasureSpec(0,unSpecified);
                child.measure(w, h);
                measureWidth = child.getMeasuredWidth();
            }
        }

        mItemWidth = measureWidth;
    }

	@Override
	public Params getParams() {
		if (mParams == null) {
			throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
		}
		
		return mParams;
	}

	@Override
	public void getFocusedRect(Rect r) {
		if (hasFocus() || hasDeepFocus()) {
			super.getFocusedRect(r);
			return;
		}

		getDrawingRect(r);
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
		if (hasFocus()) {
			super.addFocusables(views, direction, focusableMode);
			return;
		}

		if (views == null) {
			return;
		}
		if (!isFocusable()) {
			return;
		}
		if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode() && !isFocusableInTouchMode()) {
			return;
		}
		views.add(this);
	}

	protected void reset() {
		if (getSelectedView() == null) {
			return;
		}

		if (mDeep != null) {
			mFocusRectparams.set(mDeep.getFocusParams());
		} else {
			ItemListener item = (ItemListener) getSelectedView();
			if (item != null) {
				mFocusRectparams.set(item.getFocusParams());
			}
		}

		offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
	}

	private void resetHeader(int nextSelectedPosition) {
		View header = getHeaderView(nextSelectedPosition);
		ItemListener item = (ItemListener) header;

		if (item != null) {
			mFocusRectparams.set(item.getFocusParams());
		}
		// mFocusRectparams.focusRect().left = mListPadding.left;
		// mFocusRectparams.focusRect().right = mListPadding.left +
		// header.getWidth();
		int left = getChildAt(0).getLeft();

		for (int index = getFirstVisiblePosition() - 1; index >= 0; index--) {
			if (index >= getHeaderViewsCount()) {
				if(mItemWidth <= 0){
					Log.e(TAG, "FocusHList mItemWidth <= 0");
				}
				left -= mItemWidth;
			} else {
				left -= getHeaderView(index).getWidth();
			}
		}

		mFocusRectparams.focusRect().left = left;
		mFocusRectparams.focusRect().right = left + header.getWidth();

		// offsetDescendantRectToMyCoords(getSelectedView(),
		// mFocusRectparams.focusRect());
	}

	@Override
	public FocusRectParams getFocusParams() {
		View v = getSelectedView();
		if (isFocusedOrSelected() && v != null) {
			if (mFocusRectparams == null || isScrolling()) {
				reset();
			}
			return mFocusRectparams;
		} else {
			Rect r = new Rect();
			getFocusedRect(r);

			mFocusRectparams.set(r, 0.5f, 0.5f);
			return mFocusRectparams;
		}
	}
	
    private boolean isFocusedOrSelected(){
        return isFocused() || isSelected();
    }

    @Override
    public boolean isFocused() {
        return super.isFocused() || hasFocus() || hasDeepFocus();
    }

	@Override
	public boolean canDraw() {
		if (mDeep != null) {
			return mDeep.canDraw();
		}

		View v = getSelectedView();
		if (v != null && mReset) {
			performSelect(true);
			mReset = false;
		}
		return getSelectedView() != null && mLayouted;
	}

	@Override
	public boolean isAnimate() {
		if (mDeep != null) {
			return mDeep.isAnimate();
		}

		return mIsAnimate;
	}

    public boolean onKeyDownDeep(int keyCode, KeyEvent event){
        if (mDeepMode && mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {// by
            // leiming.yanlm
            if (mDeep.onKeyDown(keyCode, event)) {
                reset();
                offsetFocusRect(-getLeftScrollDistance(), 0);
               // offsetFocusRect(-getLeftScrollDistance(), 0);
                return true;
            }
        }

        return false;
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown keyCode = " + keyCode);

		if(onKeyDownDeep(keyCode, event)){
            return true;
        }

		if (getChildCount() <= 0) {
			return super.onKeyDown(keyCode, event);
		}

		if (checkState(keyCode)) {
			return true;
		}

		if (mDistance < 0) {
			mDistance = getChildAt(0).getWidth();
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (moveLeft()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (moveRight()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT);
				return true;
			}
			break;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {
            return mDeep.onKeyUp(keyCode, event);
        }
	    return super.onKeyUp(keyCode, event);
	}

	private boolean moveLeft() {
		int nextSelectedPosition = getSelectedItemPosition() - 1 >= 0 ? getSelectedItemPosition() - 1 : INVALID_POSITION;
		if (mDeepMode) {
			if (getChildAt(nextSelectedPosition - getFirstPosition()) == null) {
				return true;
			}
		}
		
		if (Math.abs(getLeftScrollDistance()) > getChildAt(0).getWidth() * 3) {
			return true;
		}

		performSelect(false);
		mReset = false;

		if (nextSelectedPosition != INVALID_POSITION) {
			setSelectedPositionInt(nextSelectedPosition);
			setNextSelectedPositionInt(nextSelectedPosition);

			if (mDeepMode) {
				View lastSelectView = (View) mDeep;
				Rect focusRect = new Rect(mDeep.getFocusParams().focusRect());
				mDeep.onFocusDeeped(false, FOCUS_LEFT, null);
				mDeep = null;
				DeepListener deep = (DeepListener) getSelectedView();
				if (deep.canDeep()) {
					mDeep = deep;
					offsetDescendantRectToMyCoords(lastSelectView, focusRect);
					offsetRectIntoDescendantCoords(getSelectedView(), focusRect);
					mDeep.onFocusDeeped(true, FOCUS_LEFT, focusRect);
				}
			}

			if (canDraw()) {
				mReset = false;
				performSelect(true);
			} else {
				mReset = true;
			}

			int amountToScroll = amountToCenterScroll(FOCUS_UP, nextSelectedPosition);

			// if (mIsAnimate) {
			// reset();
			// if (amountToScroll != 0) {
			// smoothScrollBy(amountToScroll);
			// mFocusRectparams.focusRect().top -= amountToScroll;
			// mFocusRectparams.focusRect().bottom -= amountToScroll;
			// }
			// }
			return true;
		}

		return false;
	}

	private void performSelect(boolean select) {
		if (mItemSelectedListener != null) {
			mItemSelectedListener.onItemSelected(getSelectedView(), getSelectedItemPosition(), select, this);
		}
	}

	private boolean moveRight() {
		int nextSelectedPosition = getSelectedItemPosition() + 1 < mItemCount ? getSelectedItemPosition() + 1 : INVALID_POSITION;
		if (mDeepMode) {
			if (getChildAt(nextSelectedPosition - getFirstPosition()) == null) {
				return true;
			}
		}
		if (getLeftScrollDistance() > getChildAt(0).getWidth() * 3) {
			return true;
		}
		performSelect(false);
		mReset = false;

		if (nextSelectedPosition != INVALID_POSITION) {
			setSelectedPositionInt(nextSelectedPosition);
			setNextSelectedPositionInt(nextSelectedPosition);
			if (mDeepMode) {
				View lastSelectView = (View) mDeep;
				Rect focusRect = new Rect(mDeep.getFocusParams().focusRect());
				mDeep.onFocusDeeped(false, FOCUS_RIGHT, null);
				mDeep = null;
				DeepListener deep = (DeepListener) getSelectedView();
				if (deep.canDeep()) {
					mDeep = deep;
					offsetDescendantRectToMyCoords(lastSelectView, focusRect);
					offsetRectIntoDescendantCoords(getSelectedView(), focusRect);
					mDeep.onFocusDeeped(true, FOCUS_RIGHT, focusRect);
				}
			}

			if (canDraw()) {
				mReset = false;
				performSelect(true);
			} else {
				mReset = true;
			}

			int amountToScroll = amountToCenterScroll(FOCUS_DOWN, nextSelectedPosition);

			// if (mIsAnimate) {
			// reset();
			// if (amountToScroll != 0) {
			// smoothScrollBy(amountToScroll);
			// // mFocusRectparams.focusRect().top -= amountToScroll;
			// // mFocusRectparams.focusRect().bottom -= amountToScroll;
			// }
			// }
			return true;
		}

		return false;
	}

	public boolean checkState(int keyCode) {
		if (mLastScrollState == OnScrollListener.SCROLL_STATE_FLING) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return true;
			}
		}

		return false;
	}

	int amountToCenterScroll(int direction, int nextSelectedPosition) {
		int center = (getWidth() - mListPadding.left - mListPadding.right) / 2 + mListPadding.left;
		final int listRight = getWidth() - mListPadding.right;
		final int listLeft = mListPadding.left;
		final int numChildren = getChildCount();
		int amountToScroll = 0;
		int distanceLeft = getLeftScrollDistance();
		if (direction == FOCUS_DOWN) {
			View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
			int nextSelectedCenter = 0;
			boolean reset = false;
			if (nextSelctedView == null) {
				nextSelctedView = getChildAt(getChildCount() - 1);
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				nextSelectedCenter += (nextSelctedView.getWidth() + mSpacing) * (nextSelectedPosition - getLastVisiblePosition());

				reset = false;
			} else {
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				reset = true;
			}

			int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

			if (finalNextSelectedCenter > center) {
				amountToScroll = finalNextSelectedCenter - center;
				int maxDiff = (nextSelctedView.getWidth() + mSpacing) * (mItemCount - getLastVisiblePosition() - 1);
				maxDiff -= distanceLeft;
				View lastVisibleView = getChildAt(numChildren - 1);
				if (lastVisibleView.getRight() > getWidth() - mListPadding.right) {
					maxDiff += (lastVisibleView.getRight() - (getWidth() - mListPadding.right));
				}

				if (amountToScroll > maxDiff) {
					amountToScroll = maxDiff;
				}

				if (reset) {
					reset();
					offsetFocusRect(-distanceLeft, 0);
					if (DEBUG) {
						Log.i(TAG, "amountToCenterScroll: focus rect = " + mFocusRectparams.focusRect() + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = " + nextSelectedPosition);
					}
				}

				if (amountToScroll > 0) {
					if (reset) {
						offsetFocusRect(-amountToScroll, 0);
					} else {
						offsetFocusRect((nextSelctedView.getWidth() + mSpacing - amountToScroll), 0);
					}

					if (DEBUG) {
						Log.d(TAG, "amountToCenterScroll: focus down amountToScroll = " + amountToScroll + ", focus rect = " + mFocusRectparams.focusRect());
					}
					smoothScrollBy(amountToScroll);
					mIsAnimate = true;
				} else {
					if (!reset) {
						offsetFocusRect(nextSelctedView.getWidth() + mSpacing, 0);
					}
					mIsAnimate = true;
				}
			} else {
				reset();
				offsetFocusRect(-distanceLeft, 0);
				mIsAnimate = true;
			}

			return amountToScroll;
		} else if (direction == FOCUS_UP) {
			View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
			int nextSelectedCenter = 0;
			boolean reset = false;
			if (nextSelctedView == null) {
				nextSelctedView = getChildAt(0);
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				if (nextSelectedPosition >= getHeaderViewsCount()) {
					nextSelectedCenter -= (nextSelctedView.getWidth() + mSpacing) * (getFirstVisiblePosition() - nextSelectedPosition);
				} else {
					nextSelectedCenter -= (nextSelctedView.getWidth() + mSpacing) * (getFirstVisiblePosition() - getHeaderViewsCount());
					for (int i = getHeaderViewsCount() - 1; i >= nextSelectedPosition; i--) {
						nextSelectedCenter -= getHeaderView(i).getWidth();
					}
				}

				reset = false;
			} else {
				nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
				reset = true;
			}

			int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

			if (finalNextSelectedCenter < center) {
				amountToScroll = center - finalNextSelectedCenter;
				int maxDiff = 0;

				if (getFirstVisiblePosition() >= getHeaderViewsCount()) {
					maxDiff = (nextSelctedView.getWidth()+ mSpacing) * (getFirstVisiblePosition() - getHeaderViewsCount());
				}

				int start = getHeaderViewsCount() - 1;
				if (start > getFirstVisiblePosition() - 1) {
					start = getFirstVisiblePosition() - 1;
				}
				for (int i = start; i >= 0; i--) {
					maxDiff += getHeaderView(i).getWidth();
				}
				if (maxDiff < 0) {
					maxDiff = 0;
				}

				maxDiff += distanceLeft;
				View firstVisibleView = getChildAt(0);
				if (firstVisibleView.getLeft() < listLeft) {
					maxDiff += (listLeft - firstVisibleView.getLeft());
				}

				if (amountToScroll > maxDiff) {
					amountToScroll = maxDiff;
				}

				if (reset) {
					reset();
					offsetFocusRect(-distanceLeft, 0);
					//offsetFocusRect(-distanceLeft, 0);
					if (DEBUG) {
						Log.i(TAG, "amountToCenterScroll: focus rect = " + mFocusRectparams.focusRect() + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = " + nextSelectedPosition);
					}
				} else if (nextSelectedPosition < getHeaderViewsCount()) {
					reset = true;
					resetHeader(nextSelectedPosition);
					offsetFocusRect(-distanceLeft, 0);
					//offsetFocusRect(-distanceLeft, 0);
				}

				if (amountToScroll > 0) {
					if (reset) {
						offsetFocusRect(amountToScroll, 0);
						//offsetFocusRect(amountToScroll, 0);
					} else {
						offsetFocusRect(-(nextSelctedView.getWidth() + mSpacing - amountToScroll), 0);
						//offsetFocusRect(-(nextSelctedView.getWidth() - amountToScroll), 0);
					}

					if (DEBUG) {
						Log.d(TAG, "amountToCenterScroll: focus down amountToScroll = " + amountToScroll + ", focus rect = " + mFocusRectparams.focusRect());
					}
					smoothScrollBy(-amountToScroll);
					mIsAnimate = true;
				} else {
					if (!reset) {
						offsetFocusRect(-nextSelctedView.getWidth() + mSpacing, 0);
						//offsetFocusRect(-nextSelctedView.getWidth(), 0);
					}
					mIsAnimate = true;
				}
			} else {
				reset();
				offsetFocusRect(-distanceLeft, 0);
				//offsetFocusRect(-distanceLeft, 0);
				mIsAnimate = true;
			}

			return amountToScroll;
		}

		return 0;
	}

	int amountToScroll(int direction, int nextSelectedPosition) {
		final int listRight = getWidth() - mListPadding.right;
		final int listLeft = mListPadding.left;

		final int numChildren = getChildCount();

		if (direction == FOCUS_RIGHT) {
			int amountToScroll = 0;
			int indexToMakeVisible = numChildren - 1;
			final View viewToMakeVisible = getChildAt(indexToMakeVisible);
			final View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);

			int goalRight = listRight;
			if (nextSelctedView != null) {
				if (nextSelectedPosition <= getLastVisiblePosition()) {
					if (nextSelctedView.getRight() <= goalRight) {
						mIsAnimate = true;
					} else {
						amountToScroll = viewToMakeVisible.getWidth();
						mIsAnimate = false;
					}
				} else {
					amountToScroll = viewToMakeVisible.getWidth();
					mIsAnimate = false;
				}
			} else {
				if (nextSelectedPosition < mItemCount - 1) {
					amountToScroll = viewToMakeVisible.getWidth();
					mIsAnimate = false;
				} else {
					int width = mFocusRectparams.focusRect().width();
					mFocusRectparams.focusRect().right = goalRight;
					mFocusRectparams.focusRect().left = goalRight - width;
					amountToScroll = nextSelctedView.getRight() - goalRight;
					mIsAnimate = true;
				}
			}
			if (amountToScroll > 0) {
				Log.d(TAG, "amountToScroll: amountToScroll = " + amountToScroll);
				smoothScrollBy(amountToScroll);
			}

			return amountToScroll;
		} else {
			int amountToScroll = 0;
			int indexToMakeVisible = numChildren - 1;
			final View viewToMakeVisible = getChildAt(indexToMakeVisible);
			final View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
			int goalLeft = listLeft;
			if (nextSelctedView != null) {
				if (nextSelectedPosition <= getLastVisiblePosition()) {
					if (nextSelctedView.getLeft() >= goalLeft) {
						mIsAnimate = true;
					} else {
						amountToScroll = viewToMakeVisible.getWidth();
						mIsAnimate = false;
					}
				} else {
					amountToScroll = viewToMakeVisible.getWidth();
					mIsAnimate = false;
				}
			} else {
				if (nextSelectedPosition < mItemCount - 1) {
					amountToScroll = viewToMakeVisible.getWidth();
					mIsAnimate = false;
				} else {
					int width = mFocusRectparams.focusRect().width();
					mFocusRectparams.focusRect().left = goalLeft;
					mFocusRectparams.focusRect().right = goalLeft + width;
					amountToScroll = goalLeft - nextSelctedView.getLeft();
					mIsAnimate = true;
				}
			}
			if (amountToScroll > 0) {
				Log.d(TAG, "amountToScroll: amountToScroll = " + amountToScroll);
				smoothScrollBy(-amountToScroll);
			}

			return amountToScroll;
		}
	}

	@Override
	public ItemListener getItem() {
		if (mDeep != null) {
			if (mDeep.hasDeepFocus()) {
				return mDeep.getItem();
			}
			// else if (mLastDeep != null) {
			// return mLastDeep.getItem();
			// }
		}
		// else if (mLastDeep != null) {
		// return mLastDeep.getItem();
		// }

		return (ItemListener) getSelectedView();
	}

	@Override
	public boolean isScrolling() {
		boolean isScrolling = (mLastScrollState != OnScrollListener.SCROLL_STATE_IDLE);
		if (mDeep != null) {
			return mDeep.isScrolling() || isScrolling;
		}
		return isScrolling;
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		if (mDeep != null) {
			if (mDeep.preOnKeyDown(keyCode, event)) {
				return true;
			}
		}

		// if (checkState(keyCode)) {
		// return true;
		// }

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_MOVE_HOME: {
			// int nextSelectedPosition = getSelectedItemPosition() - 1 >= 0 ?
			// getSelectedItemPosition() - 1 : INVALID_POSITION;
			// if (nextSelectedPosition != INVALID_POSITION) {
			// final View nextSelctedView = getChildAt(nextSelectedPosition -
			// mFirstPosition);
			// if(nextSelctedView == null){
			// return false;
			// }
			// }
			return getSelectedItemPosition() > 0 ? true : false;
		}
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_MOVE_END: {
			// int nextSelectedPosition = getSelectedItemPosition() + 1 <
			// mItemCount ? getSelectedItemPosition() + 1 : INVALID_POSITION;
			// if (nextSelectedPosition != INVALID_POSITION) {
			// final View nextSelctedView = getChildAt(nextSelectedPosition -
			// mFirstPosition);
			// if(nextSelctedView == null){
			// return false;
			// }
			// }
			return getSelectedItemPosition() < mItemCount - 1 ? true : false;
		}
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return true;

		default:
			break;
		}

		return false;
	}

	@Override
	public boolean hasDeepFocus() {
		return mDeepFocus;
	}

	@Override
	public boolean canDeep() {
		return true;
	}

	@Override
	public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		mDeepFocus = gainFocus;
		onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	@Override
	public boolean isScale() {
		return true;
	}

	@Override
	public int getItemWidth() {
		return getWidth();
	}

	@Override
	public int getItemHeight() {
		return getHeight();
	}

	@Override
	public Rect getManualPadding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onItemSelected(boolean selected) {
		performSelect(selected);
	}

	@Override
	public void onItemClick() {
		if (getSelectedView() != null) {
			performItemClick(getSelectedView(), getSelectedItemPosition(), 0);
		}
	}

	@Override
	public boolean isFocusBackground() {
		if (mDeep != null) {
			return mDeep.isFocusBackground();
		}
		return mFocusBackground;
	}

	@Override
	public void drawBeforeFocus(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawAfterFocus(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public void onFocusStart() {
		if (mDeep != null) {
			mDeep.onFocusStart();
			return;
		}

		super.onFocusStart();
	}

	@Override
	public void onFocusFinished() {
		if (mDeep != null) {
			mDeep.onFocusFinished();
			return;
		}
		super.onFocusFinished();
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		if (mDeep != null) {
			mDeep.onItemClick();
			return true;
		}

		return super.performItemClick(view, position, id);
	}

	public void offsetFocusRect(int offsetX, int offsetY) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			mFocusRectparams.focusRect().offset(offsetX, offsetY);
		}
	}

	@Override
	public Rect getClipFocusRect() {
		return mClipFocusRect;
	}
}

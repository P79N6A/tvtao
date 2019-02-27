package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.lib.SystemProUtils;

public class FocusScrollerLinearLayout extends FocusLinearLayout {

	protected static final String TAG = "FocusScrollerLinearLayout";
	protected static final boolean DEBUG = false;

    public FocusScrollerLinearLayout(Context context) {
        super(context);
        mScroller = new Scroller(context);
    }

    public FocusScrollerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public FocusScrollerLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
    }

	public static final int MIN_VALUE = 0x80000000;
	public static final int MAX_VALUE = 0x7fffffff;

	private static final int SCROLL_DURATION = 100;

	public static final int HORIZONTAL_INVALID = -1;

	public static final int HORIZONTAL_SINGEL = 1;

	public static final int HORIZONTAL_FULL = 2;

	public static final int HORIZONTAL_OUTSIDE_SINGEL = 3;

	public static final int HORIZONTAL_OUTSIDE_FULL = 4;

	private int mScrollMode = HORIZONTAL_SINGEL;

	private OnScrollListener mScrollerListener = null;

    private onNegativeScreenListener mNegativeScreenListener = null;

	private int mMinLeft;
	private int mMinScaledLeft;
	private int mMaxLeft;
	private int mMaxScaledLeft;

	private int mMinRight;
	private int mMinScaledRight;
	private int mMaxRight;
	private int mMaxScaledRight;

	private int mMinTop;
	private int mMinScaledTop;
	private int mMaxTop;
	private int mMaxScaledTop;

	private int mMinBottom;
	private int mMinScaledBottom;
	private int mMaxBottom;
	private int mMaxScaledBottom;

	View mMinLeftView = null;
	View mMaxLeftView = null;

	View mMinRightView = null;
	View mMaxRightView = null;

	View mMinTopView = null;
	View maxTopView = null;

	View minBottomView = null;
	View maxBottomView = null;

	private OutsideScrollListener mOutsideScrollListener = null;

	private Scroller mScroller;

	private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

	private int mManualPaddingRight = 20;
	private int mManualPaddingBottom = 20;

	private int mCenterX = 960;
	private int mCenterY = 360;
	private int mLastHorizontalDirection = 0;
	private int mLastVerticalDirection = 0;
	private int mDuration = 500;

	private int mScrollX = 0;
	private int mScrollY = 0;

    private int mLeftMoveOffset = 0;
    private int mVerticalBaseLine = 0;
    private int mTopMoveOffset = 0;
    private int mHorizontalBaseLine = 0;
    
    private boolean mIsCheckBottom = true;
    private boolean mIsCheckTop = true;
	
	public void setOnScrollListener(OnScrollListener l){
		mScrollerListener = l;
	}

	public void setHorizontalMode(int mode) {
		mScrollMode = mode;
	}

	public void setOutsideScrollListener(OutsideScrollListener l) {
		this.mOutsideScrollListener = l;
	}

	public void setManualPaddingRight(int padding) {
		mManualPaddingRight = padding;
	}

	public void setManualPaddingBottom(int padding) {
		mManualPaddingBottom = padding;
	}

	public void setCenter(int centerX, int centerY) {
		mCenterX = centerX;
		mCenterY = centerY;
	}

	public void setScrollDuration(int duration) {
		mDuration = duration;
	}

    public int getLeftMoveHideOffset() {
        return mLeftMoveOffset;
    }

    public int getVerticalBaseLine() {
        return mVerticalBaseLine;
    }

    public void setVerticalBaseLine(int verticalBaseLine) {
        this.mVerticalBaseLine = verticalBaseLine;
    }

    public int getHorizontalBaseLine() {
        return mHorizontalBaseLine;
    }

    public void setHorizontalBaseLine(int horizontalBaseLine) {
        this.mHorizontalBaseLine = horizontalBaseLine;
    }

    public int getTopMoveHideOffset(){
        return mTopMoveOffset;
    }

	int mLastCode = 0;

	public void setSelectedView(View v, int direction, boolean isSync) {
		setSelectedView(v, direction);
		int code = getKeyCode(direction);
		mLastCode = -1;
		if (isSync) {
			scrollSingel(code, 0);
		}else{
			requestLayout();
			mLastCode = code;
		}
	}

	int getKeyCode(int direction) {
		switch (direction) {
		case View.FOCUS_LEFT:

			return KeyEvent.KEYCODE_DPAD_LEFT;
		case View.FOCUS_UP:

			return KeyEvent.KEYCODE_DPAD_UP;
		case View.FOCUS_RIGHT:

			return KeyEvent.KEYCODE_DPAD_RIGHT;
		case View.FOCUS_DOWN:

			return KeyEvent.KEYCODE_DPAD_DOWN;

		}

		return KeyEvent.KEYCODE_DPAD_LEFT;
	}

	@Override
	protected void initNode() {
        beforeInitNode();
		if (mNeedInitNode) {
			if (getChildCount() <= 0) {
				return;
			}

			this.mMinLeft = MAX_VALUE;
			this.mMaxLeft = MIN_VALUE;

			this.mMinRight = MAX_VALUE;
			this.mMaxRight = MIN_VALUE;

			this.mMinTop = MAX_VALUE;
			this.mMaxTop = MIN_VALUE;

			this.mMinBottom = MAX_VALUE;
			this.mMaxBottom = MIN_VALUE;

            for (int index = 0; index < this.getChildCount(); index++) {
				View child = this.getChildAt(index);
				if (child == null || child.getVisibility() == View.GONE) {
					continue;
				}

                int leftOffset = 0;
                int rightOffset = 0;
                int topOffset = 0;
                int bottomOffset = 0;
                Object obj = child.getLayoutParams();
                if(obj instanceof ViewGroup.MarginLayoutParams){
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)obj;
                    leftOffset = lp.leftMargin;
                    rightOffset = lp.rightMargin;
                    topOffset = lp.topMargin;
                    bottomOffset = lp.bottomMargin;
                }

				// for left
				if (child.getLeft() - leftOffset < this.mMinLeft) {
					mMinLeftView = child;
					this.mMinLeft = child.getLeft() - leftOffset;
				}

				if (child.getLeft() - leftOffset > this.mMaxLeft) {
					mMaxLeftView = child;
					this.mMaxLeft = child.getLeft() - leftOffset;
				}

				// for right
				if (child.getRight() + rightOffset < this.mMinRight) {
					mMinRightView = child;
					this.mMinRight = child.getRight()+rightOffset;
				}

				if (child.getRight() + rightOffset > this.mMaxRight) {
					mMaxRightView = child;
					this.mMaxRight = child.getRight() + rightOffset;
				}

				// for top
				if (child.getTop() - topOffset < this.mMinTop) {
					mMinTopView = child;
					this.mMinTop = child.getTop() - topOffset;
				}

				if (child.getTop() - topOffset > this.mMaxTop) {
					maxTopView = child;
					this.mMaxTop = child.getTop() - topOffset;
				}

				// for bottom
				if (child.getBottom() + bottomOffset < this.mMinBottom) {
					minBottomView = child;
					this.mMinBottom = child.getBottom() + bottomOffset;
				}

				if (child.getBottom() + bottomOffset > this.mMaxBottom) {
					maxBottomView = child;
					this.mMaxBottom = child.getBottom() + bottomOffset;
				}
			}
			
			ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
			if(lp != null){
			    int leftMargin = lp.leftMargin;
			    int topMargin = lp.topMargin;
			    int rightMargin = lp.rightMargin;
			    int bottomMargin = lp.bottomMargin;
			    
    	        this.mMinLeft += leftMargin - rightMargin;
                this.mMaxLeft += leftMargin - rightMargin;
    
                this.mMinRight += leftMargin - rightMargin;
                this.mMaxRight += leftMargin - rightMargin;
    
                this.mMinTop += topMargin - bottomMargin;
                this.mMaxTop += topMargin - bottomMargin;
    
                this.mMinBottom += topMargin - bottomMargin;
                this.mMaxBottom += topMargin - bottomMargin;
			}
			
			if (DEBUG) {
				Log.d(TAG, "init: mMinLeft = " + mMinLeft + ", mMaxLeft = " + mMaxLeft + ", mMinRight = " + mMinRight + ", mMaxRight = " + mMaxRight + ", mMinTop = " + mMinTop + ", mMaxTop = "
						+ mMaxTop + ", mMinBottom = " + mMinBottom + ", mMaxBottom = " + mMaxBottom);

				Log.d(TAG, "init: minLeftView = " + mMinLeftView + ", maxLeftView = " + mMaxLeftView + ", minRightView = " + mMinRightView + ", maxRightView = " + mMaxRightView + ", minTopView = "
						+ mMinTopView + ", maxTopView = " + maxTopView + ", minBottomView = " + minBottomView + ", maxBottomView = " + maxBottomView);
			}

			if (this.getChildCount() > 0) {
				Rect r = new Rect();
                FocusRectParams focusParams = null;
				{
                    if(mMinLeftView != null && mMinLeftView instanceof ItemListener){
                        focusParams = getScaleRectParams((ItemListener) mMinLeftView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMinScaledLeft = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(),
							focusParams.coefX(), focusParams.coefY()).left;
				}

				{
                    if(mMaxLeftView != null && mMaxLeftView instanceof ItemListener){
                        focusParams = getScaleRectParams((ItemListener) mMaxLeftView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMaxScaledLeft = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(),
							focusParams.coefX(), focusParams.coefY()).left;
				}

				{
                    if(mMinRightView != null && mMinRightView instanceof ItemListener){
                        focusParams = getScaleRectParams((ItemListener) mMinRightView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMinScaledRight = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(),
							focusParams.coefX(), focusParams.coefY()).right;
				}

				{
                    if(mMaxRightView != null && mMaxRightView instanceof ItemListener){
                        focusParams = getScaleRectParams((ItemListener) mMaxRightView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMaxScaledRight = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(),
							focusParams.coefX(), focusParams.coefY()).right;
				}

				{
                    if(mMinTopView != null && mMinTopView instanceof ItemListener){
                        focusParams = getScaleRectParams((ItemListener) mMinTopView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMinScaledTop = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(),
							focusParams.coefX(), focusParams.coefY()).top;
				}

				{
                    if(maxTopView != null && maxTopView instanceof ItemListener){
                        focusParams = getScaleRectParams((ItemListener) maxTopView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMaxScaledTop = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(),
							focusParams.coefX(), focusParams.coefY()).top;
				}

				{
                    if(minBottomView != null && minBottomView instanceof ItemListener){
                        focusParams = getScaleRectParams((ItemListener) minBottomView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMinScaledBottom = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(),
							focusParams.coefX(), focusParams.coefY()).bottom;
				}

				{
                    if(maxBottomView != null && maxBottomView instanceof ItemListener){
                        focusParams = getScaleRectParams((ItemListener) maxBottomView, r);
                    }
                    if(focusParams == null){
                        focusParams = getFocusParams();
                        getFocusedRect(r);
                    }
					this.mMaxScaledBottom = ScalePositionManager.instance().getScaledRect(r, mParams.getScaleParams().getScaleX(), mParams.getScaleParams().getScaleY(),
							focusParams.coefX(), focusParams.coefY()).bottom;
				}
			}
		}

		super.initNode();
	}

    private void beforeInitNode() {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)getLayoutParams();
        if(lp != null){
            if(mLeftMoveOffset != lp.leftMargin){
                mLeftMoveOffset = lp.leftMargin;
            }
            if(mTopMoveOffset != lp.topMargin){
                mTopMoveOffset = lp.topMargin;
            }
        }
    }

    private FocusRectParams getScaleRectParams(ItemListener listener, Rect rect){
        FocusRectParams focusParams = null;
        if(listener != null){
            ItemListener item = listener;
            focusParams = item.getFocusParams();
            rect.set(focusParams.focusRect());
            offsetDescendantRectToMyCoords((View)listener, rect);
        }

        return focusParams;
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean hr = super.onKeyDown(keyCode, event);

		if (DEBUG) {
			Log.d(TAG, "onKeyDown hr:" + hr + ", keyCode:" + keyCode);
		}

		if (hr) {
			scrollSingel(keyCode, mDuration);
		}

		return hr;
	}

	@Override
	public void computeScroll() {
		if (this.mScrollMode == HORIZONTAL_FULL || this.mScrollMode == HORIZONTAL_SINGEL) {
			if (mScroller.computeScrollOffset()) {
				int offsetX = mScroller.getCurrX() - mScrollX;
				int offsetY = mScroller.getCurrY() - mScrollY;

				mScrollX = mScroller.getCurrX();
				mScrollY = mScroller.getCurrY();

				offsetFocusRect(offsetX, offsetY);
//				mFocusDstRectparams.focusRect().offset(offsetX, offsetY);
				scrollTo(mScrollX, mScrollY);
				invalidate();
			}

			if (mScroller.isFinished()) {
				reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
			}
		}
		super.computeScroll();
	}
	
	public void offsetFocusRect(int offsetX, int offsetY) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			mFocusRectparams.focusRect().offset(offsetX, offsetY);
		}
	}

	public boolean smoothScrollBy(int dx, int dy, int duration) {
		if (dx == 0 && dy == 0) {
            if(DEBUG){
                Log.w(TAG, "smoothScrollBy dx = " + dx + ", dy = " + dy + ", quit");
            }
            if (!mScroller.isFinished()) {
                int remainScrollDistanceX = mScroller.getFinalX() - mScroller.getCurrX();
                int remainScrollDistanceY = mScroller.getFinalY() - mScroller.getCurrY();
                
                offsetFocusRect(-remainScrollDistanceX, -remainScrollDistanceY);
//                mFocusDstRectparams.focusRect().offset(-remainScrollDistanceX, -remainScrollDistanceY);
            }
			return false;
		}
		
		if (DEBUG) {
			Log.d(TAG, "smoothScrollBy dx = " + dx + ", dy = " + dy + ", duration = " + duration + " top = " + (getScrollY() - getTop()));
		}

		mScrollX = mScroller.getCurrX();
		mScrollY = mScroller.getCurrY();
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
		mScroller.startScroll(mScrollX, mScrollY, dx, dy, duration);
		reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
		invalidate();
        return true;
	}

	protected int getLeftX() {
		return mScroller.getFinalX() - mScroller.getCurrX();
	}

	protected int getLeftY() {
		return mScroller.getFinalY() - mScroller.getCurrY();
	}
	
	void reportScrollStateChange(int newState) {
		if (newState != mLastScrollState) {
			if (mScrollerListener != null) {
				mLastScrollState = newState;
				mScrollerListener.onScrollStateChanged(this, newState);
			}
		}
	}

	void scrollSingel(int keyCode, int duration) {

		int[] diff = getScrollByDiff(keyCode);
		boolean start = smoothScrollBy(diff[0], diff[1], duration);
        if(start){
        	offsetFocusRect(-diff[0], -diff[1]);
//            mFocusDstRectparams.set(mFocusRectparams);
//            mFocusDstRectparams.focusRect().offset(-diff[0], -diff[1]);
        }

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			mLastHorizontalDirection = keyCode;
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			mLastVerticalDirection = keyCode;
		}
	}

	protected int getOffset(int keyCode) {
		View selectedView = getSelectedView();
        Rect visibleRect = new Rect();
        getGlobalVisibleRect(visibleRect);

        int remainScrollX = 0;
        int remainScrollY = 0;
        if(mScroller != null && !mScroller.isFinished()){
            remainScrollX = mScroller.getFinalX() - mScroller.getCurrX();
            remainScrollY = mScroller.getFinalY() - mScroller.getCurrY();
        }

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			int centerX = mCenterX + getScrollX();
			int selectCenterX = getCenterX(selectedView);
			int diff = selectCenterX - centerX;
			int visibleRightBind = visibleRect.right + getScrollX() + mManualPaddingRight;
            int visibleLeftBind = visibleRect.left + getScrollX();

            if(DEBUG){
                Log.d(TAG, "smooth diff = " + diff + " visibleLeftBind = " + visibleLeftBind + " remainScrollX = " + remainScrollX);
            }
            if(visibleLeftBind + remainScrollX < (mMinLeft - mLeftMoveOffset)){
                //基准线的左边
                if(selectCenterX > (mMinLeft - mLeftMoveOffset) + mVerticalBaseLine){
                    if (mNegativeScreenListener != null){
                        mNegativeScreenListener.onNegativeScreenStateChanged(false);
                    }
                    return -visibleLeftBind;
                }
            } else if (diff > 0) {
                //基准线的右边不需要加remainScrollX，否则会提前到达右边界，导致多滚
				if (visibleRightBind /*+ remainScrollX*/ + diff <= mMaxRight + mManualPaddingRight) {
                     //已经是需要移动的距离，未滚动的距离只纳入到基准线左边的计算条件，需要滚动的距离由当前视图自身计算
                     //diff += remainScrollX;
                    if(selectCenterX < mVerticalBaseLine){
                        diff = 0;
                    }
				} else {
					diff = (mMaxRight + mManualPaddingRight) - visibleRightBind;
				}

				if (diff > 0) {
                    if (mNegativeScreenListener != null){
                        mNegativeScreenListener.onNegativeScreenStateChanged(true);
                    }
					return diff;
				}

			}
			return 0;
		}
		case KeyEvent.KEYCODE_DPAD_LEFT: {
			int centerX = mCenterX + getScrollX();
			int selectCenterX = getCenterX(selectedView);
			int diff = centerX - selectCenterX;
			int visibleLeftBind = visibleRect.left + getScrollX();
            int visibleRightBind = visibleRect.right + mManualPaddingRight;
			if (diff > 0) {//视角需要向左移才向左移动
                boolean isShowNegative = true;
                if (visibleLeftBind + remainScrollX - diff >= (mMinLeft - mLeftMoveOffset) + mVerticalBaseLine){
                    //已经是需要移动的距离，未滚动的距离只纳入到计算条件，需要滚动的距离由当前视图自身计算
                    //diff -= remainScrollX;
                } else if (selectCenterX < (mMinLeft - mLeftMoveOffset) + mVerticalBaseLine){
                    if (mNegativeScreenListener != null){
                        mNegativeScreenListener.onNegativeScreenStateChanged(isShowNegative);
                    }
                    return mLeftMoveOffset - getScrollX();
                }
                else {
                    diff = Math.min(visibleLeftBind,diff);
                    if (visibleLeftBind - diff <= (mMinLeft - mLeftMoveOffset)){
                        isShowNegative = false;
                    }
				}

				if (diff > 0) {
                    if (mNegativeScreenListener != null){
                        mNegativeScreenListener.onNegativeScreenStateChanged(isShowNegative);
                    }
					diff = -diff;
                    //未停止滚动或只有一屏中心点小于centerX的情况，目标位置已经是最左边时，不需要滚动
                    if(visibleLeftBind + diff <= mMinLeft){
                        diff = mMinLeft - visibleLeftBind;
                    }
                    return diff;
				}

			}

			return 0;
		}
		case KeyEvent.KEYCODE_DPAD_DOWN: {
			int centerY = mCenterY + getScrollY();
			int selectCenterY = getCenterY(selectedView);
			int diff = selectCenterY - centerY;
			int visibleBottomBind = visibleRect.bottom + getScrollY();
            int visibleTopBind = visibleRect.top + getScrollY();
            if(visibleTopBind + remainScrollY < (mMinTop - mTopMoveOffset)){
                if(selectCenterY > (mMinTop - mTopMoveOffset) + mHorizontalBaseLine){
                    if (mNegativeScreenListener != null){
                        mNegativeScreenListener.onNegativeScreenStateChanged(false);
                    }
                    return -visibleTopBind;
                }
            } if (diff > 0) {
                //基准线的下边不需要加remainScrollY，否则会提前到达下边界，导致多滚
				if (visibleBottomBind/* + remainScrollY*/ + diff <= mMaxBottom + mManualPaddingBottom) {
                    //已经是需要移动的距离，未滚动的距离只纳入到计算条件，需要滚动的距离由当前视图自身计算
                    //diff += remainScrollY;
				} else if (mIsCheckBottom) {
					diff = (mMaxBottom + mManualPaddingBottom) - visibleBottomBind;
				}

				if (diff > 0) {
                    if (mNegativeScreenListener != null){
                        mNegativeScreenListener.onNegativeScreenStateChanged(true);
                    }
					return diff;
				}
			}

			return 0;
		}
		case KeyEvent.KEYCODE_DPAD_UP: {
			int centerY = mCenterY + getScrollY();
			int selectCenterY = getCenterY(selectedView);
			int diff = centerY - selectCenterY;
			int visibleTopBind = visibleRect.top + getScrollY();
            int visibleBottomBind = visibleRect.bottom + mManualPaddingBottom;
			if (diff > 0) {
                boolean isShowNegative = true;
				if (visibleTopBind + remainScrollY - diff >= (mMinTop - mTopMoveOffset) + mHorizontalBaseLine) {
                    //已经是需要移动的距离，未滚动的距离只纳入到计算条件，需要滚动的距离由当前视图自身计算
                    //diff -= remainScrollY;
				} else if(selectCenterY < (mMinTop - mTopMoveOffset) + mHorizontalBaseLine){
                    if (mNegativeScreenListener != null){
                        mNegativeScreenListener.onNegativeScreenStateChanged(isShowNegative);
                    }
                    return mTopMoveOffset - getScrollY();
                }
                else {
                    diff = Math.min(visibleTopBind, diff);
                    if(visibleTopBind - diff < (mMinTop - mTopMoveOffset)){
                        isShowNegative = false;
                    }
				}

				if (diff > 0) {
                    if (mNegativeScreenListener != null){
                        mNegativeScreenListener.onNegativeScreenStateChanged(isShowNegative);
                    }
					diff = -diff;
                    //未停止滚动或只有一屏中心点小于centerY的情况
                    if(visibleTopBind + diff <= mMinTop && mIsCheckTop){
                        diff = mMinTop - visibleTopBind;
                    }
                    return diff;
				}
			}

			return 0;
		}
		}

		// throw new
		// IllegalArgumentException("FocusScrollerRelativeLayout: getOffset: direction must be FOCUS_LEFT, FOCUS_RIGHT, FOCUS_UP, or FOCUS_DOWN, but it is "
		// + direction);
		return 0;
	}

	protected int getCenterX(View v) {
		int selectCenterX = 0;
		if ((mFocusRectparams.centerMode() & FocusRectParams.CENTER_X_FOCUS) == FocusRectParams.CENTER_X_FOCUS) {
			selectCenterX = (mFocusRectparams.focusRect().left + mFocusRectparams.focusRect().right) / 2;
		} else if ((mFocusRectparams.centerMode() & FocusRectParams.CENTER_X) == FocusRectParams.CENTER_X) {
            selectCenterX = (v.getLeft() + v.getRight()) / 2;
		} else {
			throw new IllegalArgumentException("FocusScrollerRelativeLayout: getCenterX: mFocusRectparams.centerMode() = " + mFocusRectparams.centerMode());
		}

		return selectCenterX + mLeftMoveOffset;
	}

	protected int getCenterY(View v) {
		int selectCenterY = 0;
		if ((mFocusRectparams.centerMode() & FocusRectParams.CENTER_Y_FOCUS) == FocusRectParams.CENTER_Y_FOCUS) {
			selectCenterY = (mFocusRectparams.focusRect().top + mFocusRectparams.focusRect().bottom) / 2;
		} else if ((mFocusRectparams.centerMode() & FocusRectParams.CENTER_Y) == FocusRectParams.CENTER_Y) {
            selectCenterY = (v.getTop() + v.getBottom()) / 2;
		} else {
			throw new IllegalArgumentException("FocusScrollerRelativeLayout: getCenterY: mFocusRectparams.centerMode() = " + mFocusRectparams.centerMode());
		}

		return selectCenterY + mTopMoveOffset;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		//如果在滑动的过程中调用了requestLayout方法时，需要重新offset剩下未滑动的距离
        //edit by zhangle:onlayout后焦点内容为空的话也需要从mFocusRectparams里获取焦点位置
		offsetFocusRect(-getLeftX(), -getLeftY());
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		if(gainFocus && previouslyFocusedRect != null){
            previouslyFocusedRect.offset(getScrollX(), getScrollY());
        }
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	public int[] getScrollByDiff(int keyCode) {
		int[] diff = new int[2];
		// diff[0] 为 diffX
		// diff[1] 为 diffY

		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			diff[0] = getOffset(keyCode);
			diff[1] = 0;
			if (mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_DOWN || mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_UP) {
				diff[1] = getOffset(mLastVerticalDirection);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			diff[0] = getOffset(keyCode);
			diff[1] = 0;
			if (mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_DOWN || mLastVerticalDirection == KeyEvent.KEYCODE_DPAD_UP) {
				diff[1] = getOffset(mLastVerticalDirection);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			diff[1] = getOffset(keyCode);
			diff[0] = 0;
			if (mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_LEFT || mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_RIGHT) {
				diff[0] = getOffset(mLastHorizontalDirection);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			diff[1] = getOffset(keyCode);
			diff[0] = 0;
			if (mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_LEFT || mLastHorizontalDirection == KeyEvent.KEYCODE_DPAD_RIGHT) {
				diff[0] = getOffset(mLastHorizontalDirection);
			}
		}

		return diff;
	}
	
	public void isCheckBottom(boolean isCheck){
		mIsCheckBottom = isCheck;
	}
	
	public void isCheckTop(boolean isCheck){
		mIsCheckTop = isCheck;
	}

	public interface OutsideScrollListener {
		public int getCurrX();

		public int getCurrY();

		public void smoothOutsideScrollBy(int distance, int duration);
	}

    public static interface onNegativeScreenListener{
        public void onNegativeScreenStateChanged(boolean isShowNegative);
    }

    public onNegativeScreenListener getNegativeScreenListener() {
        return mNegativeScreenListener;
    }

    public void setNegativeScreenListener(onNegativeScreenListener negativeScreenListener) {
        this.mNegativeScreenListener = negativeScreenListener;
    }
    
	@Override
	public boolean isScrolling() {
		if (mDeep != null){
			return (mDeep.isScrolling()||!mScroller.isFinished());
		}
		
		return !mScroller.isFinished();
	}

}

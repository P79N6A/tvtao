package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.action.IFocusAction;
import com.yunos.tv.app.widget.focus.listener.AnimateWhenGainFocusListener;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class ViewGroup extends android.view.ViewGroup implements IFocusAction, ItemListener {
	public class NodeInfo {
		public int index;
		public View fromLeft;
		public View fromRight;//向右聚焦时的焦点
		public View fromUp;
		public View fromDown;
	}
	
	private final String TAG = this.getClass().getSimpleName();
	protected static final boolean DEBUG = true;

	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	protected FocusRectParams mFocusRectparams = new FocusRectParams();

	private FocusFinder mFocusFinder;
	private boolean mAimateWhenGainFocusFromLeft = true;
	private boolean mAimateWhenGainFocusFromRight = true;
	private boolean mAimateWhenGainFocusFromUp = true;
	private boolean mAimateWhenGainFocusFromDown = true;
	private boolean mIsAnimate = true;

	private boolean mDeepFocus = false;
    private boolean mOnFocused = false;
	private ItemSelectedListener mItemSelectedListener;
	private OnItemClickListener mOnItemClickListener;

	private FocusStateListener mFocusStateListener = null;
	protected View mNextFocus = null;
	protected DeepListener mDeep = null;
	protected DeepListener mLastDeep = null;
	View mLastSelectedView = null;
	
	protected int mNextDirection;
	protected int mIndex = -1;
	private boolean mAutoSearchFocus = true;
	protected Map<View, NodeInfo> mNodeMap = new HashMap<View, NodeInfo>();
	
	protected boolean mNeedInit = true;
	boolean mLayouted = false;
	boolean mNeedReset = false;
	boolean mNeedInitNode = true;

	android.view.ViewGroup mFindRootView;
	boolean mFocusBackground = false;
	boolean mNeedFocused = true;

	boolean mClearDataDetachedFromWindow = true;
	View mFirstSelectedView = null;
    private boolean mUpdateIndexBySelectView = mIndex < 0;

	public ViewGroup(Context context) {
		super(context);
		initFocusFinder();
	}

	public ViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFocusFinder();
	}

	public ViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initFocusFinder();
	}

    public boolean isUpdateIndexBySelectView() {
        return mUpdateIndexBySelectView;
    }

    public void setNeedUpdateIndexBySelectView(boolean needResetIndex) {
        this.mUpdateIndexBySelectView = needResetIndex;
    }

	@Override
	public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
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
	
	protected void afterLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		initNode();
		mLayouted = true;
		reset();
		if (mNeedReset && hasFocusChild()) {
			performItemSelect(getSelectedView(), hasFocus() || hasDeepFocus(), true);
			mNeedReset = false;
		}
	}

	/**
     * 子节点是否有获取焦点的能力
     * @return
     */
    public boolean hasFocusChild(){
        return !(mNodeMap == null || mNodeMap.isEmpty());
    }

	@Override
	public boolean canDeep() {
	    if(!hasFocusChild()){
            return false;
        }
		return true;
	}

	@Override
	public boolean canDraw() {
		if (mDeep != null && mDeep.canDraw())
			return true;
		return getSelectedView() != null || mOnFocused || isSelected();
	}
	
	public boolean checkAnimate(int direction) {
		switch (direction) {
		case View.FOCUS_LEFT:
			return mAimateWhenGainFocusFromRight ? true : false;
		case View.FOCUS_UP:
			return mAimateWhenGainFocusFromDown ? true : false;
		case View.FOCUS_RIGHT:
			return mAimateWhenGainFocusFromLeft ? true : false;
		case View.FOCUS_DOWN:
			return mAimateWhenGainFocusFromUp ? true : false;
		}

		return true;
	}
	
	public void clearFocusedIndex() {
		mIndex = -1;
	}
	
	public void clearSelectedView() {
		// unselected last selected View
		View selectedView = getSelectedView();
		if (selectedView != null) {
			selectedView.setSelected(false);
			performItemSelect(selectedView, false, false);
			OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
			if (listener != null) {
				listener.onFocusChange(selectedView, false);
			}
		}
	}
	
	@Override
	public void drawAfterFocus(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawBeforeFocus(Canvas canvas) {
		// TODO Auto-generated method stub

	}
	
	public void forceInitNode(){
	    mNeedInitNode = true;
	    initNode();
	}

	protected boolean getAnimateByKeyCode(AnimateWhenGainFocusListener animateListener, int keyCode) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			return animateListener.fromRight();
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			return animateListener.fromLeft();
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return animateListener.fromTop();
		case KeyEvent.KEYCODE_DPAD_UP:
			return animateListener.fromBottom();
		default:
			break;
		}

		return false;
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		int selectedIndex = mIndex;
		if (selectedIndex < 0) {
			return i;
		}

		if (i < selectedIndex) {
			return i;
		} else if (i >= selectedIndex) {
			return childCount - 1 - i + selectedIndex;
		} else {
			return i;
		}
	}
	
	public DeepListener getDeep() {
		return mDeep;
	}
	
	/**
	 * 不需要默认值时请在调用该方法前加上isDirectionKeyCode判断
	 * 
	 * @param keyCode
	 *            键值
	 * @return
	 */
	public int getDirectionByKeyCode(int keyCode) {
		int direction = View.FOCUS_DOWN;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			direction = View.FOCUS_LEFT;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			direction = View.FOCUS_RIGHT;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			direction = View.FOCUS_UP;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			direction = View.FOCUS_DOWN;
			break;
		default:
			direction = View.FOCUS_DOWN;
			Log.w(TAG, "direction is default value : View.FOCUS_DOWN");
			break;
		}

		return direction;
	}

	protected int getFocusableItemIndex() {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			if (childView.isFocusable() && childView.getVisibility() == View.VISIBLE && childView instanceof ItemListener) {
				if (childView == mFirstSelectedView) {
					return i;
				}
				return i;
			}
		}
		return -1;
	}

	@Override
	public void getFocusedRect(Rect r) {
		View item = getSelectedView();
		if (hasFocus() || hasDeepFocus()) {
			if (item != null) {
				item.getFocusedRect(r);
				this.offsetDescendantRectToMyCoords(item, r);
				// Log.d(TAG, "getFocusedRect r = " + r);
				return;
			}
		}
		super.getFocusedRect(r);
	}

	protected Rect getFocusedRect(View from, View to) {
		Rect rFrom = new Rect();
		from.getFocusedRect(rFrom);
		Rect rTo = new Rect();
		to.getFocusedRect(rTo);

		offsetDescendantRectToMyCoords(from, rFrom);
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

	public FocusFinder getFocusFinder() {
		return mFocusFinder;
	}

	@Override
	public FocusRectParams getFocusParams() {
		if (mFocusRectparams == null || isScrolling()) {
			reset();
		}
		return mFocusRectparams;
	}

	public FocusRectParams getFocusRectParams() {
		return mFocusRectparams;
	}

	public FocusStateListener getFocusStateListener() {
		return mFocusStateListener;
	}

	@Override
	public ItemListener getItem() {
	    //by zhangle:内部没有可用的焦点的时候返回自己
        if(!hasDeepFocus()){
            return this;
        }
		if (mDeep != null && mDeep.hasDeepFocus()) {
			return mDeep.getItem();
		} else if (mLastDeep != null) {
			return mLastDeep.getItem();
		}

		return (ItemListener) getSelectedView();
	}

	@Override
	public int getItemHeight() {
	    if(hasFocusChild() && getSelectedView() != null){
            return getSelectedView().getHeight();
        }
        return getHeight();
	}

	public ItemSelectedListener getOnItemSelectedListener() {
		return mItemSelectedListener;
	}

	@Override
	public int getItemWidth() {
	    if(hasFocusChild() && getSelectedView() != null){
            return getSelectedView().getWidth();
        }
        return getWidth();
	}

	public DeepListener getLastDeep() {
		return mLastDeep;
	}

	public View getLastSelectedView() {
		return mLastSelectedView;
	}

	@Override
	public Rect getManualPadding() {
		return null;
	}
	
	public View getNextFocus() {
		return mNextFocus;
	}

	public OnItemClickListener getOnItemClickListener() {
		return mOnItemClickListener;
	}

	public Params getParams() {
        if(mDeep != null){
            return mDeep.getParams();
        }

        if (mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }

        return mParams;
	}

	public View getSelectedView() {
	    if(!hasFocusChild()){
            return null;
        }
        if(mIndex < 0){
            mIndex = getFocusableItemIndex();
        }
        return getChildAt(mIndex);
	}

	@Override
	public boolean hasDeepFocus() {
		return mDeepFocus;
	}

	/**
	 * 防止焦点状态先回来，但还没有onLayout好，此时焦点框框住整个视图了
	 * 只在视图初始化完成时调一次
	 * add by quanqing.hqq@alibaba-inc.com
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		forceInitNode();
		setNeedInitNode(true);
	}
	
	private void initFocusFinder() {
		mFocusFinder = new FocusFinder();
		mFocusFinder.clearFocusables();
	}

	@Override
	public void initNode() {
		if (mNeedInitNode) {
			mFocusFinder.clearFocusables();
			mFocusFinder.initFocusables(this);

			this.mNodeMap.clear();
			for (int index = 0; index < this.getChildCount(); index++) {
				View child = this.getChildAt(index);
				if (!child.isFocusable()) {
					continue;
				}

				if (!(child instanceof ItemListener)) {
					continue;
				}
				
				if (mFirstSelectedView == child && isUpdateIndexBySelectView()) {
					mIndex = index;
                    setNeedUpdateIndexBySelectView(false);
				}

				if (!this.mNodeMap.containsKey(child)) {
					NodeInfo info = new NodeInfo();
					info.index = index;
					this.mNodeMap.put(child, info);
				}
			}

			mNeedInitNode = false;
		}

	}
	
	public void setNeedInitNode(boolean initNode){
		mNeedInitNode = initNode;
	}

	public boolean isNeedInitNode(){
		return mNeedInitNode;
	}

	public boolean isAnimate() {
		if (mDeep != null) {
			return mDeep.isAnimate();
		}

		return mIsAnimate;
	}

	boolean isDirectionKeyCode(int keyCode) {
		return isHorizontalKeyCode(keyCode) || isVerticalKeyCode(keyCode);
	}
	
	boolean isEnterKeyCode(int keyCode) {
		return keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER;
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public boolean isFocusBackground() {
		if (mDeep != null) {
			return mDeep.isFocusBackground();
		}
		return mFocusBackground;
	}

	boolean isHorizontalKeyCode(int keyCode) {
		return keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT;
	}
	
	boolean isItemClickSelf() {
		return mDeep == null;
	}

	boolean isItemSelectSelf() {
		return mDeep == null;
	}
	
	public boolean isNeedFocusItem() {
		return mNeedFocused;
	}
	
	public boolean isLayouted(){
        return mLayouted;
    }
	
	boolean isNeedOnFocusSelf() {
		return mDeep == null;
	}
	
	@Override
	public boolean isScale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isScrolling() {
		return false;
	}

	boolean isSelfChildren(View view) {
		return indexOfChild(view) >= 0;
	}

	boolean isVerticalKeyCode(int keyCode) {
		return keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN;
	}

	public void notifyLayoutChanged() {
		Log.d(TAG, "notifyLayoutChanged");
		mNeedInitNode = true;
		requestLayout();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
        mOnFocused = false;
		if (mClearDataDetachedFromWindow) {
			mLayouted = false;
			mNeedInitNode = true;
			if (mNodeMap != null) {
				mNodeMap.clear();
			}
			if (mFocusFinder != null) {
				mFocusFinder.clearFocusables();
			}
		}
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mOnFocused = gainFocus;
		Log.d(TAG, "onFocusChanged");
		if (getOnFocusChangeListener() != null) {
			getOnFocusChangeListener().onFocusChange(this, gainFocus);
		}

		if (gainFocus) {
		    if(!hasFocusChild()){
                mNeedReset = true;
                return;
            }
			mNeedFocused = false;
			if (this.mAutoSearchFocus && previouslyFocusedRect != null) {
				if (mFindRootView == null) {
					mFindRootView = this;
				}
				View v = this.mFocusFinder.findNextFocusFromRect(mFindRootView, previouslyFocusedRect, direction);
				if (this.mNodeMap.containsKey(v)) {
					NodeInfo info = this.mNodeMap.get(v);
					mIndex = info.index;
				} else {
					if (mIndex < 0) {
						mIndex = getFocusableItemIndex();
					}
				}
			} else {
				if (mIndex < 0) {
					mIndex = getFocusableItemIndex();
				}
			}

			boolean isDeep = false;
			if (getSelectedView() instanceof DeepListener) {
				mDeep = (DeepListener) getSelectedView();
				if (mDeep.canDeep()) {
					isDeep = true;
					Rect rect = new Rect(previouslyFocusedRect);
					offsetRectIntoDescendantCoords((View)mDeep, rect);
					mDeep.onFocusDeeped(gainFocus, direction, rect);
					reset();
				}
			}

			if (!isDeep) {
				if (!mLayouted) {
					mNeedReset = true;
				} else {
					reset();
					performItemSelect(getSelectedView(), gainFocus, true);
				}
			}
		} else {
			if (mDeep != null && mDeep.canDeep()) {
				mDeep.onFocusDeeped(gainFocus, direction, null);
			} else {
				if (mLayouted) {
					performItemSelect(getSelectedView(), gainFocus, true);
				} else {
					mNeedReset = true;
				}
			}
		}

		mIsAnimate = checkAnimate(direction);
		invalidate();
	}

	public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect, android.view.ViewGroup findRoot) {
		mFindRootView = findRoot;
		onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		mFindRootView = null;
	}

	@Override
	public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		mDeepFocus = gainFocus;
		onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}
	
	@Override
	public void onFocusFinished() {
		if (mDeep != null) {
			mDeep.onFocusFinished();
			return;
		}
		if (mFocusStateListener != null) {
			mFocusStateListener.onFocusFinished(getSelectedView(), this);
		}
	}
	
	@Override
	public void onFocusStart() {
		if (mDeep != null) {
			mDeep.onFocusStart();
			return;
		}
		if (mFocusStateListener != null) {
			mFocusStateListener.onFocusStart(getSelectedView(), this);
		}
	}
	
	@Override
	public void onItemClick() {
		performClick();
	}

	@Override
	public void onItemSelected(boolean selected) {
		performItemSelect(getSelectedView(), selected, false);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown keyCode = " + keyCode);
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
			this.setPressed(true); //click事件需要onkeydown + onkeyup
			return true;
		}
		
		if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {
			if (mDeep.onKeyDown(keyCode, event)) {
				reset();
				return true;
			}
		}

		int direction = 0;
		if(isDirectionKeyCode(keyCode)){
			direction = getDirectionByKeyCode(keyCode);
		}

		if (mNextFocus != null && this.mNodeMap.containsKey(mNextFocus) && mNextFocus.isFocusable()) {
			mIsAnimate = true;
			if (mDeep != null && mDeep.canDeep()) {
				if (mDeep.hasDeepFocus()) {
					// if (mDeep.onKeyDown(keyCode, event)) {//移到上面
					// reset();
					// }
				} else {
					Rect previouslyFocusedRect = getFocusedRect(getSelectedView(), mNextFocus);
					if (mLastDeep != null && mLastDeep.hasDeepFocus()) {
						mLastDeep.onFocusDeeped(false, direction, null);
						mLastDeep = null;
					}
					mDeep.onFocusDeeped(true, mNextDirection, previouslyFocusedRect);
					NodeInfo info = this.mNodeMap.get(mNextFocus);
					mIndex = info.index;
					reset();
				}
				this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
				return true;
			}

			if (mLastDeep != null && mLastDeep.hasDeepFocus()) {
				mLastDeep.onFocusDeeped(false, direction, null);
			}
			mLastSelectedView = getSelectedView();

			if (mLastSelectedView != null) {
				mLastSelectedView.setSelected(false);
				performItemSelect(mLastSelectedView, false, false);
				OnFocusChangeListener listener = mLastSelectedView.getOnFocusChangeListener();
				if (listener != null) {
					listener.onFocusChange(mLastSelectedView, false);
				}
			}

			NodeInfo info = this.mNodeMap.get(mNextFocus);
			mIndex = info.index;

			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				info.fromRight = mLastSelectedView;
				if (mNextFocus instanceof AnimateWhenGainFocusListener) {
					AnimateWhenGainFocusListener li = (AnimateWhenGainFocusListener) mNextFocus;
					mIsAnimate = li.fromRight();
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				info.fromLeft = mLastSelectedView;
				if (mNextFocus instanceof AnimateWhenGainFocusListener) {
					AnimateWhenGainFocusListener li = (AnimateWhenGainFocusListener) mNextFocus;
					mIsAnimate = li.fromLeft();
				}
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				info.fromUp = mLastSelectedView;
				if (mNextFocus instanceof AnimateWhenGainFocusListener) {
					AnimateWhenGainFocusListener li = (AnimateWhenGainFocusListener) mNextFocus;
					mIsAnimate = li.fromTop();
				}
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				info.fromDown = mLastSelectedView;
				if (mNextFocus instanceof AnimateWhenGainFocusListener) {
					AnimateWhenGainFocusListener li = (AnimateWhenGainFocusListener) mNextFocus;
					mIsAnimate = li.fromBottom();
				}
				break;
			}

			mLastDeep = null;
			View selectedView = getSelectedView();
			if (selectedView != null) {
				selectedView.setSelected(true);
				performItemSelect(selectedView, true, false);
				OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
				if (listener != null) {
					listener.onFocusChange(selectedView, true);
				}
			}

			reset();
			this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(mNextDirection));
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyUp keyCode = " + keyCode);

		if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {
			return mDeep.onKeyUp(keyCode, event);
		}

		if ((KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) && getSelectedView() != null) {
			if (isPressed()) { //click事件需要onkeydown+onkeyup
				setPressed(false);
				performItemClick();
				getSelectedView().performClick();
			}
			return true;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}
	
	protected  void performItemClick() {
		if (mDeep != null) {
			mDeep.onItemClick();
			return;
		}

		if (this.mOnItemClickListener != null) {
			this.mOnItemClickListener.onItemClick(this, getSelectedView());
		}
	}

	void performItemSelect(View v, boolean isSelected, boolean isLocal) {
		if (!isLocal) {
			if (mDeep != null) {
				mDeep.onItemSelected(isSelected);
				return;
			}
		}
		//edit by zhangle:防止没有子节点时抛出空指针
        if(v != null){
    		v.setSelected(isSelected);
    		if (this.mItemSelectedListener != null) {
    			this.mItemSelectedListener.onItemSelected(v, mIndex, isSelected, this);
    		}
        }
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		// mLastDeep = null;
		Log.d(TAG, "preOnKeyDown keyCode = " + keyCode);
		if (mDeep != null) {
			if (mDeep.preOnKeyDown(keyCode, event)) {
				return true;
			}
		}

		View selectedView = getSelectedView();
		NodeInfo nodeInfo = this.mNodeMap.get(selectedView);
		View nextFocus = null;
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			mNextDirection = FOCUS_LEFT;
			if (nodeInfo != null && nodeInfo.fromLeft != null && nodeInfo.fromLeft.isFocusable() && !nodeInfo.fromLeft.equals(selectedView)) {
				nextFocus = nodeInfo.fromLeft;
			} else {
				nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mNextDirection = FOCUS_RIGHT;
			if (nodeInfo != null && nodeInfo.fromRight != null && nodeInfo.fromRight.isFocusable() && !nodeInfo.fromRight.equals(selectedView)) {
				nextFocus = nodeInfo.fromRight;
			} else {
				nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mNextDirection = FOCUS_DOWN;
			if (nodeInfo != null && nodeInfo.fromDown != null && nodeInfo.fromDown.isFocusable() && !nodeInfo.fromDown.equals(selectedView)) {
				nextFocus = nodeInfo.fromDown;
			} else {
				nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
			}

			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			mNextDirection = FOCUS_UP;
			if (nodeInfo != null && nodeInfo.fromUp != null && nodeInfo.fromUp.isFocusable() && !nodeInfo.fromUp.equals(selectedView)) {
				nextFocus = nodeInfo.fromUp;
			} else {
				nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
			}

			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_NUMPAD_ENTER:
			return true;
		default:
			return false;
		}

		mNextFocus = nextFocus;
		if (nextFocus != null) {
			if (mDeep != null) {
				// mDeep.onFocusDeeped(false, 0, null);
				mLastDeep = mDeep;
				mDeep = null;
			}
			if (nextFocus instanceof DeepListener) {
				mDeep = (DeepListener) nextFocus;
				if (!mDeep.canDeep()) {
					mDeep = null;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void release() {
		this.mNodeMap.clear();
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		mLayouted = false;
	}

	public void reset() {
	    if(!hasFocusChild()){
            Rect r = new Rect();
            getFocusedRect(r);
            mFocusRectparams.set(r, 0.5f, 0.5f);
        } else {
    		if (getSelectedView() == null) {
    			return;
    		}

    		if (mDeep != null) {
    			mFocusRectparams.set(mDeep.getFocusParams());
    		} else {
    			if (mIndex == -1 && getChildCount() > 0) {
    				// FocusScrollerRelativeLayout.initNode经常调用本类的getFocusParams出空指针。布局受限。
    				mIndex = 0;
    			}
    			ItemListener item = (ItemListener) getSelectedView();
    			if (item != null) {
    				mFocusRectparams.set(item.getFocusParams());
    			}
    		}

    		offsetDescendantRectToMyCoords(getSelectedView(), mFocusRectparams.focusRect());
        }
	}

	public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
		mAimateWhenGainFocusFromLeft = fromleft;
		mAimateWhenGainFocusFromUp = fromUp;
		mAimateWhenGainFocusFromRight = fromRight;
		mAimateWhenGainFocusFromDown = fromDown;
	}

	public void setAutoSearchFocus(boolean autoSearchFocus) {
		this.mAutoSearchFocus = autoSearchFocus;
	}

	public void setClearDataDetachedFromWindowEnable(boolean enable) {
		mClearDataDetachedFromWindow = enable;
	}

	public void setDeep(DeepListener deep) {
		this.mDeep = deep;
	}

	public void setDeepFocus(boolean deepFocus) {
		mDeepFocus = deepFocus;
	}

	public void setFirstSelectedView(View v) {
		mFirstSelectedView = v;
	}
	
	public void setFocusBackground(boolean focusBg) {
		mFocusBackground = focusBg;
	}
	
	public void setFocusRectParams(FocusRectParams params) {
		if (params == null) {
			Log.w(TAG, "AbstractViewGroupFocus setFocusRectParams 'params' is null.");
		} else {
			mFocusRectparams.set(params);
		}
	}
	
	public void setLastDeep(DeepListener lastDeep) {
		this.mLastDeep = lastDeep;
	}

	public void setLastSelectedView(View lastSelectedView) {
		this.mLastSelectedView = lastSelectedView;
	}

	public void setNextFocus(View nextFocus) {
		this.mNextFocus = nextFocus;
	}

	public void setOnFocusStateListener(FocusStateListener focusStateListener) {
		mFocusStateListener = focusStateListener;
	}

	public void setOnItemClickListener(OnItemClickListener lis) {
		mOnItemClickListener = lis;
	}

	public void setOnItemSelectedListener(ItemSelectedListener lis) {
		mItemSelectedListener = lis;
	}

	public void setParams(Params params) {
		if (params == null) {
			throw new NullPointerException("AbstractViewGroupFocus setParams params is null.");
		}
		mParams = params;
	}

	@Override
	public void setSelected(boolean selected) {
		if (selected == false) {
			mNeedFocused = true;
		}
		super.setSelected(selected);
	}

	public void setSelectedView(View v) {
		if (!this.mNodeMap.containsKey(v)) {
			throw new IllegalArgumentException("Parent does't contain this view");
		}
		// TODO
	}

	public Map<View, NodeInfo> getNodeMap(){
		return mNodeMap;
	}
	
	public static interface OnItemClickListener {

		/**
		 * Callback method to be invoked when an item in this AdapterView has
		 * been clicked.
		 * <p>
		 * Implementers can call getItemAtPosition(position) if they need to
		 * access the data associated with the selected item.
		 * 
		 * @param parent
		 *            The AdapterView where the click happened.
		 * @param view
		 *            The view within the AdapterView that was clicked (this
		 *            will be a view provided by the adapter)
		 */
		void onItemClick(android.view.ViewGroup parent, View view);
	}
}

package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.FocusFinder;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
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

public class FocusRelativeLayout extends RelativeLayout implements DeepListener, ItemListener {
	protected final String TAG = this.getClass().getSimpleName();
	protected static final boolean DEBUG = false;
    private boolean mOnFocused = false;

    public FocusRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initLayout(context);
	}

	public FocusRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initLayout(context);
	}

	public FocusRelativeLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initLayout(context);
	}

	public int mIndex = -1;

	private ItemSelectedListener mOnItemSelectedListener = null;

	private boolean mAutoSearchFocus = true;

	private Map<View, NodeInfo> mNodeMap = new HashMap<View, NodeInfo>();

	private View mLastSelectedView = null;

	private FocusFinder mFocusFinder;

	protected boolean mNeedInit = true;

	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	protected FocusRectParams mFocusRectparams = new FocusRectParams();

	View mNextFocus = null;
	DeepListener mDeep = null;
	DeepListener mLastDeep = null;
	int mNextDirection;

	boolean mLayouted = false;
	boolean mNeedReset = false;
	boolean mNeedInitNode = true;
	boolean mDeepFocus = false;
	boolean mClearDataDetachedFromWindow = true;
	FocusStateListener mFocusStateListener = null;
	OnItemClickListener mOnItemClickListener;
	ViewGroup mFindRootView;
	boolean mNeedFocused = true;
	boolean mFocusBackground = false;
	boolean mIsAnimate = true;
	boolean mAimateWhenGainFocusFromLeft = true;
	boolean mAimateWhenGainFocusFromRight = true;
	boolean mAimateWhenGainFocusFromUp = true;
	boolean mAimateWhenGainFocusFromDown = true;

    private boolean mUpdateIndexBySelectView = mIndex < 0;

	public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
		mAimateWhenGainFocusFromLeft = fromleft;
		mAimateWhenGainFocusFromUp = fromUp;
		mAimateWhenGainFocusFromRight = fromRight;
		mAimateWhenGainFocusFromDown = fromDown;
	}

	public void setClearDataDetachedFromWindowEnable(boolean enable) {
		mClearDataDetachedFromWindow = enable;
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

	public void setFocusBackground(boolean back) {
		mFocusBackground = back;
	}

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public void setOnItemSelectedListener(ItemSelectedListener listener) {
		this.mOnItemSelectedListener = listener;
	}

	private void performItemClick() {
		if (mDeep != null) {
			mDeep.onItemClick();
			return;
		}

		if (this.mOnItemClickListener != null) {
			this.mOnItemClickListener.onItemClick(this, getSelectedView());
		}
	}

	private void performItemSelect(View v, boolean isSelected, boolean isLocal) {
		if (!isLocal) {
			if (mDeep != null) {
				mDeep.onItemSelected(isSelected);
				return;
			}
		}

		if (v != null) {
			v.setSelected(isSelected);
		}
		if (this.mOnItemSelectedListener != null) {
			this.mOnItemSelectedListener.onItemSelected(v, mIndex, isSelected, this);
		}
	}

	public void setAutoSearchFocus(boolean autoSearchFocus) {
		this.mAutoSearchFocus = autoSearchFocus;
	}

	public void setOnFocusStateListener(FocusStateListener l) {
		mFocusStateListener = l;
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
	
	private void initLayout(Context conext) {
		// mScroller = new HotScroller(contxt, new DecelerateInterpolator());
		mFocusFinder = new FocusFinder();
		setChildrenDrawingOrderEnabled(true);
	}

	public boolean isNeedFocusItem() {
		return mNeedFocused;
	}

	public void release() {
		this.mNodeMap.clear();
	}

	public void clearFocusedIndex() {
		mIndex = -1;
	}

    public void setFocusedIndex(int index) {
        mIndex = index;
    }

    public int getFocusedIndex() {
        return mIndex;
    }

    public int getViewNodeIndex(View view) {
        if (mNodeMap != null && mNodeMap.containsKey(view)) {
            NodeInfo info = mNodeMap.get(view);
            return info.index;
        }
        return -1;
    }
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {
			return mDeep.onKeyUp(keyCode, event);
		}

		if ((KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) && getSelectedView() != null) {
			if (isPressed()) { // by zhangle:　click事件需要onkeydown+onkeyup
				setPressed(false);
				performItemClick();
				if(getSelectedView() != null){
					getSelectedView().performClick();
				}
			}
			return true;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}

	@Override
	public void setSelected(boolean selected) {
		if (selected == false) {
			mNeedFocused = true;
		}
		super.setSelected(selected);
	}

    private void performSelected(View selectedView, boolean isSelect, boolean isLocal){
        if (selectedView != null) {
            selectedView.setSelected(isSelect);
            performItemSelect(selectedView, isSelect, isLocal);
            OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
            if (listener != null) {
                listener.onFocusChange(selectedView, isSelect);
            }
        }
    }

    private boolean performDeepAction(DeepListener deep, boolean focus, int direction, Rect previouslyFocusedRect){
        if (deep != null && deep.hasDeepFocus()) {
            deep.onFocusDeeped(focus, direction, previouslyFocusedRect);
            return true;
        }

        return false;
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (mNextFocus != null) {
			if (mDeep != null) {
				// mDeep.onFocusDeeped(false, 0, null);
				mLastDeep = mDeep;
				mDeep = null;
			}
			if (mNextFocus instanceof DeepListener) {
				mDeep = (DeepListener) mNextFocus;
				if (!mDeep.canDeep()) {
					mDeep = null;
				}
			}
		}
		
        if (mDeep != null && mDeep.canDeep() && mDeep.hasDeepFocus()) {// by
            // leiming.yanlm
            if (mDeep.onKeyDown(keyCode, event)) {
                reset();
                return true;
            }
        }
        
		Log.d(TAG, "onKeyDown keyCode = " + keyCode);
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
			this.setPressed(true); // by zhangle:click事件需要onkeydown + onkeyup
			return true;
		}

        int direction = View.FOCUS_DOWN;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                direction = View.FOCUS_LEFT;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                direction = View.FOCUS_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                direction = View.FOCUS_DOWN;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                direction = View.FOCUS_UP;
                break;
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
                    if(performDeepAction(mLastDeep, false, direction, null)){
                        mLastDeep = null;
                    }

                    mDeep.onFocusDeeped(true, mNextDirection, previouslyFocusedRect);

                    mLastSelectedView = getSelectedView();
                    performSelected(mLastSelectedView, false, true);

					NodeInfo info = this.mNodeMap.get(mNextFocus);
					mIndex = info.index;
					
					View selectedView = getSelectedView();//by leiming.yanlm，
		            performSelected(selectedView, true, true);
					reset();
				}

				invalidate();
				this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
				return true;
			}

			/*if (mLastDeep != null && mLastDeep.hasDeepFocus()) {
				mLastDeep.onFocusDeeped(false, direction, null);
			}*/
            performDeepAction(mLastDeep, false, direction, null);

			mLastSelectedView = getSelectedView();

            performSelected(mLastSelectedView, false, false);
			/*if (mLastSelectedView != null) {
				mLastSelectedView.setSelected(false);
				performItemSelect(mLastSelectedView, false, false);
				OnFocusChangeListener listener = mLastSelectedView.getOnFocusChangeListener();
				if (listener != null) {
					listener.onFocusChange(mLastSelectedView, false);
				}
			}*/

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
            performSelected(selectedView, true, false);

			reset();
			invalidate();
			this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(mNextDirection));
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void clearSelectedView() {
		// unselected last selected View
		View selectedView = getSelectedView();
		/*if (selectedView != null) {
			selectedView.setSelected(false);
			performItemSelect(selectedView, false, false);
			OnFocusChangeListener listener = selectedView.getOnFocusChangeListener();
			if (listener != null) {
				listener.onFocusChange(selectedView, false);
			}
		}*/
        performSelected(selectedView, false, false);
	}

	protected void reset() {
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
			if (mIndex == -1 && getChildCount() > 0) {// by leiming.yanlm .
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

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect, ViewGroup findRoot) {
		mFindRootView = findRoot;
		onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		mFindRootView = null;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		Log.d(TAG, "onFocusChanged");
        mOnFocused = gainFocus;
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
            boolean isNeedReset = true;
			if (getSelectedView() instanceof DeepListener) {
				mDeep = (DeepListener) getSelectedView();
				if (mDeep.canDeep()) {
					isDeep = true;
                    Rect rect = null;
                    if (previouslyFocusedRect != null) {
                        rect = new Rect(previouslyFocusedRect);
                        offsetRectIntoDescendantCoords((View) mDeep, rect);
                    }
                    mDeep.onFocusDeeped(gainFocus, direction, rect);
					reset();
                    isNeedReset = false;
				}
			}

            if (!mLayouted) {
                mNeedReset = true;
            } else {
                if(isNeedReset)reset();
                performItemSelect(getSelectedView(), gainFocus, true);
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
            //mDeep = null;
		}

		mIsAnimate = checkAnimate(direction);
		invalidate();
	}

	private boolean checkAnimate(int direction) {
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

	@Override
	public void getFocusedRect(Rect r) {
		View item = getSelectedView();
		if (isFocused() && item != null) {
			if (item != null && item != this) {
				item.getFocusedRect(r);
				this.offsetDescendantRectToMyCoords(item, r);
				// Log.d(TAG, "getFocusedRect r = " + r);
				return;
			}
		}
		super.getFocusedRect(r);
	}

	public void setSelectedView(View v) {
		if (!this.mNodeMap.containsKey(v)) {
			throw new IllegalArgumentException("Parent does't contain this view");
			// return;
		}

		// TODO
		// Set<View> set = mNodeMap.keySet();
		// for (View key : set) {
		// if (key == v) {
		// NodeInfo info = mNodeMap.get(key);
		// mIndex = info.index;
		// break;
		// }
		// }
	}

	public View getSelectedView() {
        if(!hasFocusChild()){
            return null;
        }

		int indexOfView = mIndex;
		View selectedView = getChildAt(indexOfView);
		return selectedView;
	}
	
	public boolean isLayouted(){
        return mLayouted;
    }
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		super.onLayout(changed, l, t, r, b);
		initNode();
		mLayouted = true;
		reset();
		if (mNeedReset && hasFocusChild()) {
			performItemSelect(getSelectedView(), isFocused(), true);
			mNeedReset = false;
		}
	}

	View mFirstSelectedView = null;

	public void setFirstSelectedView(View v) {
		mFirstSelectedView = v;
	}

    protected void initNode() {
        if (DEBUG) {
            Log.d(TAG, "initNode:" + mNeedInitNode);
        }
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

	public void forceInitNode(){
	    mNeedInitNode = true;
	    initNode();
	}

	public void notifyLayoutChanged() {
		Log.d(TAG, "notifyLayoutChanged");
		mNeedInitNode = true;
		requestLayout();
	}

	@Override
	public void requestLayout() {
		super.requestLayout();
		mLayouted = false;
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

    public boolean isUpdateIndexBySelectView() {
        return mUpdateIndexBySelectView;
    }

    public void setNeedUpdateIndexBySelectView(boolean needResetIndex) {
        this.mUpdateIndexBySelectView = needResetIndex;
    }

    class NodeInfo {
		public int index;
		public View fromLeft;
		public View fromRight;// λ�ڵ�ǰview���ұ�
		public View fromUp;
		public View fromDown;
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

	@Override
	public boolean canDraw() {
		if (mDeep != null) {
			return mDeep.canDraw();
		}
		return getSelectedView() != null || mOnFocused || isSelected();
	}

	@Override
	public boolean isAnimate() {
		if (mDeep != null) {
			return mDeep.isAnimate();
		}
		return mIsAnimate;
	}

	@Override
	public ItemListener getItem() {
		View v = getSelectedView();
		if (isFocusedOrSelected() && v != null) {
            ItemListener item = (ItemListener) v;

            DeepListener deep = mDeep;
            DeepListener lastDeep = mLastDeep;

            if (deep != null && deep.hasDeepFocus()) {
                item = deep.getItem();
            } else if (lastDeep != null && lastDeep.hasDeepFocus()) {
                item = lastDeep.getItem();
            }

            return item == null ? this : item;
		} else {
			return this;
		}
	}

	@Override
	public boolean isScrolling() {
		// TODO Auto-generated method stub
		if (mDeep != null){
			return mDeep.isScrolling();
		}
		return false;
	}

	@Override
	public Params getParams() {
        if(mDeep != null){
            return mDeep.getParams();
        }

        if (mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }

        return mParams;
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
			if (nodeInfo != null && nodeInfo.fromLeft != null && nodeInfo.fromLeft.isFocusable()) {
				nextFocus = nodeInfo.fromLeft;
				if (selectedView == nextFocus) {
					Log.w(TAG, "preOnKeyDown the new focus is last quit");
					nodeInfo.fromLeft = null;
					nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
				}
			} else {
				nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mNextDirection = FOCUS_RIGHT;
			if (nodeInfo != null && nodeInfo.fromRight != null && nodeInfo.fromRight.isFocusable()) {
				nextFocus = nodeInfo.fromRight;
				if (selectedView == nextFocus) {
					Log.w(TAG, "preOnKeyDown the new focus is last quit");
					nodeInfo.fromRight = null;
					nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
				}
			} else {
				nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mNextDirection = FOCUS_DOWN;
			if (nodeInfo != null && nodeInfo.fromDown != null && nodeInfo.fromDown.isFocusable()) {
				nextFocus = nodeInfo.fromDown;
				if (selectedView == nextFocus) {
					Log.w(TAG, "preOnKeyDown the new focus is last quit");
					nodeInfo.fromDown = null;
					nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
				}
			} else {
				nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
			}

			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			mNextDirection = FOCUS_UP;
			if (nodeInfo != null && nodeInfo.fromUp != null && nodeInfo.fromUp.isFocusable()) {
				nextFocus = nodeInfo.fromUp;
				if (selectedView == nextFocus) {
					Log.w(TAG, "preOnKeyDown the new focus is last quit");
					nodeInfo.fromUp = null;
					nextFocus = mFocusFinder.findNextFocus(this, selectedView, mNextDirection);
				}
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
			return true;
		} else {
			Log.w(TAG, "preOnKeyDown can not find the new focused");
			return false;
		}
	}

	Rect getFocusedRect(View from, View to) {
        if(from == null || to == null){
            return null;
        }
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

	@Override
	public boolean hasDeepFocus() {
		return mDeepFocus;
	}

	@Override
	public boolean canDeep() {
		if(!hasFocusChild()){
            return false;
        }

		return true;
	}

	@Override
	public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		mDeepFocus = gainFocus;
		onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	@Override
	public boolean isScale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getItemWidth() {
		View v = getSelectedView();
		if (isFocusedOrSelected() && v != null) {
			return v.getWidth();
		}

		return getWidth();
	}

	@Override
	public int getItemHeight() {
		View v = getSelectedView();
		if (isFocusedOrSelected() && v != null) {
			return v.getHeight();
		}

		return getHeight();
	}

	@Override
	public Rect getManualPadding() {
		return null;
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
	public void onFocusFinished() {
		if (mDeep != null) {
			mDeep.onFocusFinished();
			return;
		}
		if (mFocusStateListener != null) {
			mFocusStateListener.onFocusFinished(getSelectedView(), this);
		}
	}

	/**
	 * Interface definition for a callback to be invoked when an item in this
	 * AdapterView has been clicked.
	 */
	public interface OnItemClickListener {

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
		 * @param parent
		 *            The position of the view in the adapter.
		 * @param view
		 *            The row id of the item that was clicked.
		 */
		void onItemClick(ViewGroup parent, View view);
	}

	@Override
	public void onItemSelected(boolean selected) {
		performItemSelect(getSelectedView(), selected, false);
	}

	@Override
	public void onItemClick() {
		performClick();
	}

	private int getFocusableItemIndex() {
		int childCount = getChildCount();
		int result = -1;
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			if (childView.isFocusable() && childView.getVisibility() == View.VISIBLE && childView instanceof ItemListener) {
				if (childView == mFirstSelectedView) {
					return i;
				}
				if (result == -1) {// 记录第一个满足条件的child
					result = i;
				}
			}
		}
		//if (result == -1) {
		//	result = 0;
		//}
		return result;
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

    /**
     * 子节点是否有获取焦点的能力
     * @return
     */
    public boolean hasFocusChild(){
        return !(mNodeMap == null || mNodeMap.isEmpty());
    }

    public void setNeedInitNode(boolean needInitNode) {
        this.mNeedInitNode = needInitNode;
    }

    public boolean isNeedInitNode() {
        return mNeedInitNode;
    }

    public Map<View, NodeInfo> getNodeMap() {
        return mNodeMap;
    }

    private boolean isFocusedOrSelected(){
        return isFocused() || isSelected();
    }

    @Override
    public boolean isFocused() {
        return super.isFocused() || hasFocus() || hasDeepFocus() || mOnFocused;
    }

	@Override
	public Rect getClipFocusRect() {
		if (mDeep != null && isFocusedOrSelected() && isScrolling()){
			Rect clipFocusRect = new Rect();
			Rect deepRect = mDeep.getClipFocusRect();
			if (deepRect != null){
				clipFocusRect.set(deepRect);
				offsetDescendantRectToMyCoords((View)mDeep, clipFocusRect);
			}
			return clipFocusRect;
		}
		return null;
	}
}

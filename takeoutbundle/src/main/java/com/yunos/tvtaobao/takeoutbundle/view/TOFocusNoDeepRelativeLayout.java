package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.RelativeLayout;
import com.yunos.tv.app.widget.focus.FocusScrollerLinearLayout;
import com.yunos.tv.app.widget.focus.listener.FocusDrawStateListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.FocusStateListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tvtaobao.biz.widget.InnerFocusLayout;
import com.yunos.tvtaobao.takeoutbundle.R;

public class TOFocusNoDeepRelativeLayout extends RelativeLayout implements FocusListener, ItemListener {

	protected Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());

	protected FocusRectParams mFocusRectparams = new FocusRectParams();

	boolean mFocusBackground = false;

	boolean mAimateWhenGainFocusFromLeft = true;
	boolean mAimateWhenGainFocusFromRight = true;
	boolean mAimateWhenGainFocusFromUp = true;
	boolean mAimateWhenGainFocusFromDown = true;

	boolean mIsAnimate = true;
	protected FocusStateListener mFocusStateListener = null;
	protected FocusDrawStateListener mFocusDrawStateListener = null;
	private boolean mCanDraw = true;
	private Rect mCustomerPaddingRect;
    private Rect mClipFocusRect = new Rect(); // 默认focus框
	public TOFocusNoDeepRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TOFocusNoDeepRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TOFocusNoDeepRelativeLayout(Context context) {
		super(context);
	}

	public void setOnFocusStateListener(FocusStateListener l) {
		mFocusStateListener = l;
	}

	public void setOnFocusDrawStateListener(FocusDrawStateListener l) {
		mFocusDrawStateListener = l;
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

	public void setFocusCanDraw(boolean can){
	    mCanDraw = can;
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		mIsAnimate = checkAnimate(direction);
	}

	public void setCustomerFocusPaddingRect(Rect rect){
	    mCustomerPaddingRect = rect;
	}

	/**
	 * 处理内部订单滚动信息
	 */
	public boolean actionScroll(int keyCode, KeyEvent event) {
		ScrollView focusScroller = (ScrollView) this.findViewById(R.id.shop_item_detail_host_scroller);
		switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				focusScroller.smoothScrollBy(0, -100);
				if (focusScroller.onKeyDown(keyCode, event)) {
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				//focusScroller.setSelected(true);
				focusScroller.smoothScrollBy(0, 100);
				if (focusScroller.onKeyDown(keyCode, event)) {
					return true;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				focusScroller.smoothScrollTo(0, 0);
				break;
		}
		return false;
	}

	@Override
	public FocusRectParams getFocusParams() {
		Rect r = new Rect();
		getFocusedRect(r);
		if (mCustomerPaddingRect != null) {
		    r.left += mCustomerPaddingRect.left;
		    r.top += mCustomerPaddingRect.top;
		    r.right -=mCustomerPaddingRect.right;
		    r.bottom -= mCustomerPaddingRect.bottom;
		}
		r.top += getResources().getDimensionPixelSize(R.dimen.dp_11);
		r.left += getResources().getDimensionPixelSize(R.dimen.dp_13);
		r.right -= getResources().getDimensionPixelSize(R.dimen.dp_12);
		r.bottom -= getResources().getDimensionPixelSize(R.dimen.dp_11);
		mFocusRectparams.set(r, 0.5f, 0.5f);
		return mFocusRectparams;
	}

	@Override
	public boolean canDraw() {
		return mCanDraw;
	}

	@Override
	public boolean isAnimate() {
		return mIsAnimate;
	}

	@Override
	public boolean isScale() {
		return true;
	}

	@Override
	public Params getParams() {
		if (mParams == null) {
			throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
		}

		return mParams;
	}

	@Override
	public ItemListener getItem() {
		return this;
	}

	@Override
	public boolean isScrolling() {
		return false;
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
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return true;

		default:
			break;
		}
		return false;
	}

	@Override
	public Rect getManualPadding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onFocusStart() {
		if (mFocusStateListener != null) {
			mFocusStateListener.onFocusStart(this, (View) this.getParent());
		}
	}

	@Override
	public void onFocusFinished() {
		if (mFocusStateListener != null) {
			mFocusStateListener.onFocusFinished(this, (View) this.getParent());
		}
	}

	@Override
	public boolean isFocusBackground() {
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
    /*
     * (non-Javadoc)
     * @see com.yunos.tv.app.widget.focus.listener.FocusListener#getClipFocusRect()
     */
    @Override
    public Rect getClipFocusRect() {
        if (mClipFocusRect != null) {
            return mClipFocusRect;
        }
        return new Rect();
    }
}

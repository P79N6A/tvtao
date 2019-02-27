package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Transformation;

import com.yunos.tv.app.widget.focus.PositionManager;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.lib.SystemProUtils;

public abstract class AbsGallery extends AbsSpinner implements GestureDetector.OnGestureListener {

	/**
	 * When set, the drawing method will call
	 * {@link #getChildDrawingOrder(int, int)} to get the index of the child to
	 * draw for that iteration.
	 * 
	 * @hide
	 */
	protected static final int FLAG_USE_CHILD_DRAWING_ORDER = 0x400;

	/**
	 * When set, this ViewGroup supports static transformations on children;
	 * this causes
	 * {@link #getChildStaticTransformation(View, Transformation)}
	 * to be invoked when a child is drawn.
	 *
	 * Any subclass overriding
	 * {@link #getChildStaticTransformation(View, Transformation)}
	 * should set this flags in {@link #mGroupFlags}.
	 * 
	 * {@hide}
	 */
	protected static final int FLAG_SUPPORT_STATIC_TRANSFORMATIONS = 0x800;

	/**
	 * Action to scroll the node content forward.
	 */
	public static final int ACTION_SCROLL_FORWARD = 0x00001000;

	/**
	 * Action to scroll the node content backward.
	 */
	public static final int ACTION_SCROLL_BACKWARD = 0x00002000;

	/**
	 * Duration in milliseconds from the start of a scroll during which we're
	 * unsure whether the user is scrolling or flinging.
	 */
	static final int SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT = 250;

	/**
	 * Horizontal spacing between items.
	 */
	protected int mSpacing = 0;

	int mGravity;

	/**
	 * How long the transition animation should run when a child view changes
	 * position, measured in milliseconds.
	 */
	int mAnimationDuration = 400;

	/**
	 * The alpha of items that are not selected.
	 */
	private float mUnselectedAlpha;

	/**
	 * Helper for detecting touch gestures.
	 */
	GestureDetector mGestureDetector;

	/**
	 * The position of the item that received the user's down touch.
	 */
	int mDownTouchPosition;

	/**
	 * The view of the item that received the user's down touch.
	 */
	View mDownTouchView;

	/**
	 * Sets mSuppressSelectionChanged = false. This is used to set it to false
	 * in the future. It will also trigger a selection changed.
	 */
	Runnable mDisableSuppressSelectionChangedRunnable = new Runnable() {
		@Override
		public void run() {
			mSuppressSelectionChanged = false;
			selectionChanged();
		}
	};

	/**
	 * When fling runnable runs, it resets this to false. Any method along the
	 * path until the end of its run() can set this to true to abort any
	 * remaining fling. For example, if we've reached either the leftmost or
	 * rightmost item, we will set this to true.
	 */
	boolean mShouldStopFling;

	/**
	 * The currently selected item's child.
	 */
	View mSelectedChild;

	/**
	 * Whether to continuously callback on the item selected listener during a
	 * fling.
	 */
	boolean mShouldCallbackDuringFling = true;

	/**
	 * Whether to callback when an item that is not selected is clicked.
	 */
	boolean mShouldCallbackOnUnselectedItemClick = true;

	/**
	 * If true, do not callback to item selected listener.
	 */
	boolean mSuppressSelectionChanged;

	/**
	 * If true, we have received the "invoke" (center or enter buttons) key
	 * down. This is checked before we action on the "invoke" key up, and is
	 * subsequently cleared.
	 */
	boolean mReceivedInvokeKeyDown;

	private AdapterContextMenuInfo mContextMenuInfo;

	/**
	 * If true, this onScroll is the first for this user's drag (remember, a
	 * drag sends many onScrolls).
	 */
	boolean mIsFirstScroll;

	/**
	 * Offset between the center of the selected child view and the center of
	 * the Gallery. Used to reset position correctly during layout.
	 */
	int mSelectedCenterOffset;
	
	private OnScrollListener mOnScrollListener;
	protected int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;

	public AbsGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public AbsGallery(Context context) {
		super(context);
		init(context);
	}

	public AbsGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		int flags = getGroupFlags();
		flags |= FLAG_USE_CHILD_DRAWING_ORDER;
		flags |= FLAG_SUPPORT_STATIC_TRANSFORMATIONS;
	}
	
	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListener = l;
	}
	
	protected void reportScrollStateChange(int newState) {
		if (newState != mLastScrollState) {
			if (mOnScrollListener != null) {
				mLastScrollState = newState;
				mOnScrollListener.onScrollStateChanged(this, newState);
			}
		}
	}

	private void init(Context context) {
		mGestureDetector = new GestureDetector(context, this);
		mGestureDetector.setIsLongpressEnabled(true);
	}

	/**
	 * Whether or not to callback on any {@link #getOnItemSelectedListener()}
	 * while the items are being flinged. If false, only the final selected item
	 * will cause the callback. If true, all items between the first and the
	 * final will cause callbacks.
	 * 
	 * @param shouldCallback
	 *            Whether or not to callback on the listener while the items are
	 *            being flinged.
	 */
	public void setCallbackDuringFling(boolean shouldCallback) {
		mShouldCallbackDuringFling = shouldCallback;
	}

	/**
	 * Whether or not to callback when an item that is not selected is clicked.
	 * If false, the item will become selected (and re-centered). If true, the
	 * {@link #getOnItemClickListener()} will get the callback.
	 * 
	 * @param shouldCallback
	 *            Whether or not to callback on the listener when a item that is
	 *            not selected is clicked.
	 * @hide
	 */
	public void setCallbackOnUnselectedItemClick(boolean shouldCallback) {
		mShouldCallbackOnUnselectedItemClick = shouldCallback;
	}

	/**
	 * Sets how long the transition animation should run when a child view
	 * changes position. Only relevant if animation is turned on.
	 * 
	 * @param animationDurationMillis
	 *            The duration of the transition, in milliseconds.
	 * 
	 * @attr ref android.R.styleable#Gallery_animationDuration
	 */
	public void setAnimationDuration(int animationDurationMillis) {
		mAnimationDuration = animationDurationMillis;
	}

	/**
	 * Sets the spacing between items in a Gallery
	 * 
	 * @param spacing
	 *            The spacing in pixels between items in the Gallery
	 * 
	 * @attr ref android.R.styleable#Gallery_spacing
	 */
	public void setSpacing(int spacing) {
		mSpacing = spacing;
	}

	/**
	 * Describes how the child views are aligned.
	 * 
	 * @param gravity
	 * 
	 * @attr ref android.R.styleable#Gallery_gravity
	 */
	public void setGravity(int gravity) {
		if (mGravity != gravity) {
			mGravity = gravity;
			requestLayout();
		}
	}

	/**
	 * Sets the alpha of items that are not selected in the Gallery.
	 * 
	 * @param unselectedAlpha
	 *            the alpha for the items that are not selected.
	 * 
	 * @attr ref android.R.styleable#Gallery_unselectedAlpha
	 */
	public void setUnselectedAlpha(float unselectedAlpha) {
		mUnselectedAlpha = unselectedAlpha;
	}

	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {

		t.clear();
		t.setAlpha(child == mSelectedChild ? 1.0f : mUnselectedAlpha);

		return true;
	}

	/**
	 * Offset the vertical location of all children of this view by the
	 * specified number of pixels.
	 * 
	 * @param offset
	 *            the number of pixels to offset
	 */
	public void offsetChildrenTopAndBottom(int offset) {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			getChildAt(i).offsetTopAndBottom(offset);
		}
	}
	
	/**
	 * Offset the horizontal location of all children of this view by the
	 * specified number of pixels.
	 * 
	 * @param offset
	 *            the number of pixels to offset
	 */
	void offsetChildrenLeftAndRight(int offset) {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			getChildAt(i).offsetLeftAndRight(offset);
		}
	}

	void dispatchPress(View child) {

		if (child != null) {
			child.setPressed(true);
		}

		setPressed(true);
	}

	void dispatchUnpress() {

		for (int i = getChildCount() - 1; i >= 0; i--) {
			getChildAt(i).setPressed(false);
		}

		setPressed(false);
	}

	@Override
	void selectionChanged() {
		if (!mSuppressSelectionChanged) {
			super.selectionChanged();
		}
	}

	@Override
	protected void dispatchSetPressed(boolean pressed) {

		// Show the pressed state on the selected child
		if (mSelectedChild != null) {
			mSelectedChild.setPressed(pressed);
		}
	}

	void onFinishedMovement() {
		if (mSuppressSelectionChanged) {
			mSuppressSelectionChanged = false;

			// We haven't been callbacking during the fling, so do it now
			super.selectionChanged();
		}
		mSelectedCenterOffset = 0;
		invalidate();
	}

	@Override
	protected ContextMenuInfo getContextMenuInfo() {
		return mContextMenuInfo;
	}

	@Override
	public boolean showContextMenuForChild(View originalView) {

		final int longPressPosition = getPositionForView(originalView);
		if (longPressPosition < 0) {
			return false;
		}

		final long longPressId = mAdapter.getItemId(longPressPosition);
		return dispatchLongPress(originalView, longPressPosition, longPressId);
	}

	@Override
	public boolean showContextMenu() {

		if (isPressed() && mSelectedPosition >= 0) {
			int index = mSelectedPosition - mFirstPosition;
			View v = getChildAt(index);
			return dispatchLongPress(v, mSelectedPosition, mSelectedRowId);
		}

		return false;
	}

	private boolean dispatchLongPress(View view, int position, long id) {
		boolean handled = false;

		if (mOnItemLongClickListener != null) {
			handled = mOnItemLongClickListener.onItemLongClick(this, mDownTouchView, mDownTouchPosition, id);
		}

		if (!handled) {
			mContextMenuInfo = new AdapterContextMenuInfo(view, position, id);
			handled = super.showContextMenuForChild(this);
		}

		if (handled) {
			performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
		}

		return handled;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Gallery steals all key events
		return event.dispatch(this, null, null);
	}

	@Override
	public void dispatchSetSelected(boolean selected) {
		/*
		 * We don't want to pass the selected state given from its parent to its
		 * children since this widget itself has a selected state to give to its
		 * children.
		 */
	}

	@Override
	public void onLongPress(MotionEvent e) {

		if (mDownTouchPosition < 0) {
			return;
		}

		performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
		long id = getItemIdAtPosition(mDownTouchPosition);
		dispatchLongPress(mDownTouchView, mDownTouchPosition, id);
	}

	// Unused methods from GestureDetector.OnGestureListener below

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	protected void setSelectedPositionInt(int position) {
		super.setSelectedPositionInt(position);

		// Updates any metadata we keep about the selected item.
		updateSelectedItemMetadata();
	}

	protected void updateSelectedItemMetadata() {

		View oldSelectedChild = mSelectedChild;

		View child = mSelectedChild = getChildAt(mSelectedPosition - mFirstPosition);
		if (child == null) {
			return;
		}

		child.setSelected(true);
//		child.setFocusable(true);

		if (hasFocus()) {
//			child.requestFocus();
		}

		// We unfocus the old child down here so the above hasFocus check
		// returns true
		if (oldSelectedChild != null && oldSelectedChild != child) {

			// Make sure its drawable state doesn't contain 'selected'
			oldSelectedChild.setSelected(false);

			// Make sure it is not focusable anymore, since otherwise arrow keys
			// can make this one be focused
			oldSelectedChild.setFocusable(false);
		}

	}

	abstract protected boolean scrollToChild(int childPosition);

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		if (mDownTouchPosition >= 0) {

			// An item tap should make it selected, so scroll to this child.
			scrollToChild(mDownTouchPosition - mFirstPosition);

			// Also pass the click so the client knows, if it wants to.
			if (mShouldCallbackOnUnselectedItemClick || mDownTouchPosition == mSelectedPosition) {
				performItemClick(mDownTouchView, mDownTouchPosition, mAdapter.getItemId(mDownTouchPosition));
			}

			return true;
		}

		return false;
	}

	boolean movePrevious() {
		if (mItemCount > 0 && mSelectedPosition > 0) {
			scrollToChild(mSelectedPosition - mFirstPosition - 1);
			return true;
		} else {
			return false;
		}
	}

	boolean moveNext() {
		if (mItemCount > 0 && mSelectedPosition < mItemCount - 1) {
			scrollToChild(mSelectedPosition - mFirstPosition + 1);
			return true;
		} else {
			return false;
		}
	}

	protected void onUp() {
		dispatchUnpress();
	}

	/**
	 * Called when a touch event's action is MotionEvent.ACTION_CANCEL.
	 */
	protected void onCancel() {
		onUp();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// Give everything to the gesture detector
		boolean retValue = mGestureDetector.onTouchEvent(event);

		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP) {
			// Helper method for lifted finger
			onUp();
		} else if (action == MotionEvent.ACTION_CANCEL) {
			onCancel();
		}

		return retValue;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		if (!mShouldCallbackDuringFling) {
			// We want to suppress selection changes

			// Remove any future code to set mSuppressSelectionChanged = false
			removeCallbacks(mDisableSuppressSelectionChangedRunnable);

			// This will get reset once we scroll into slots
			if (!mSuppressSelectionChanged)
				mSuppressSelectionChanged = true;
		}

		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		/*
		 * Now's a good time to tell our parent to stop intercepting our events!
		 * The user has moved more than the slop amount, since GestureDetector
		 * ensures this before calling this method. Also, if a parent is more
		 * interested in this touch's events than we are, it would have
		 * intercepted them by now (for example, we can assume when a Gallery is
		 * in the ListView, a vertical scroll would not end up in this method
		 * since a ListView would have intercepted it by now).
		 */
		getParent().requestDisallowInterceptTouchEvent(true);

		// As the user scrolls, we want to callback selection changes so
		// related-
		// info on the screen is up-to-date with the gallery's selection
		if (!mShouldCallbackDuringFling) {
			if (mIsFirstScroll) {
				/*
				 * We're not notifying the client of selection changes during
				 * the fling, and this scroll could possibly be a fling. Don't
				 * do selection changes until we're sure it is not a fling.
				 */
				if (!mSuppressSelectionChanged)
					mSuppressSelectionChanged = true;
				postDelayed(mDisableSuppressSelectionChangedRunnable, SCROLL_TO_FLING_UNCERTAINTY_TIMEOUT);
			}
		} else {
			if (mSuppressSelectionChanged)
				mSuppressSelectionChanged = false;
		}

		mIsFirstScroll = false;
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {

		// Get the item's view that was touched
		mDownTouchPosition = pointToPosition((int) e.getX(), (int) e.getY());

		if (mDownTouchPosition >= 0) {
			mDownTouchView = getChildAt(mDownTouchPosition - mFirstPosition);
			mDownTouchView.setPressed(true);
		}

		// Reset the multiple-scroll tracking state
		mIsFirstScroll = true;

		// Must return true to get matching events for this down event.
		return true;
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		int selectedIndex = mSelectedPosition - mFirstPosition;

		// Just to be safe
		if (selectedIndex < 0)
			return i;

		if (i == childCount - 1) {
			// Draw the selected child last
			return selectedIndex;
		} else if (i >= selectedIndex) {
			// Move the children after the selected child earlier one
			return i + 1;
		} else {
			// Keep the children before the selected child the same
			return i;
		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		/*
		 * The gallery shows focus by focusing the selected item. So, give focus
		 * to our selected item instead. We steal keys from our selected item
		 * elsewhere.
		 */
		if (gainFocus && mSelectedChild != null) {
//			mSelectedChild.requestFocus(direction);
			mSelectedChild.setSelected(true);
		}

	}

	@Override
	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
		event.setClassName(VGallery.class.getName());
	}

	@Override
	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
		super.onInitializeAccessibilityNodeInfo(info);
		info.setClassName(VGallery.class.getName());
		info.setScrollable(mItemCount > 1);
		if (isEnabled()) {
			if (mItemCount > 0 && mSelectedPosition < mItemCount - 1) {
				info.addAction(ACTION_SCROLL_FORWARD);
			}
			if (isEnabled() && mItemCount > 0 && mSelectedPosition > 0) {
				info.addAction(ACTION_SCROLL_BACKWARD);
			}
		}
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		/*
		 * Gallery expects Gallery.LayoutParams.
		 */
		return new VGallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	/**
	 * Gallery extends LayoutParams to provide a place to hold current
	 * Transformation information along with previous position/transformation
	 * info.
	 */
	public static class LayoutParams extends ViewGroup.LayoutParams {
		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(int w, int h) {
			super(w, h);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}
	}
	
	public void offsetFocusRect(FocusRectParams rectParam, int offsetX, int offsetY) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			rectParam.focusRect().offset(offsetX, offsetY);
		}
	}
	
	public void offsetFocusRect(FocusRectParams rectParam, int left, int right, int top, int bottom) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			rectParam.focusRect().left += left;
			rectParam.focusRect().right += right;
			rectParam.focusRect().top += top;
			rectParam.focusRect().bottom += bottom;
		}
	}
	
	public void offsetFocusRectLeftAndRight(FocusRectParams rectParam, int left, int right) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			rectParam.focusRect().left += left;
			rectParam.focusRect().right += right;
		}
	}
	
	public void offsetFocusRectTopAndBottom(FocusRectParams rectParam, int top, int bottom) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			rectParam.focusRect().top += top;
			rectParam.focusRect().bottom += bottom;
		}
	}
}

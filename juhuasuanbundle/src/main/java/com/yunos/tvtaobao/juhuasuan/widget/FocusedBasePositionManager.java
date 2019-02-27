package com.yunos.tvtaobao.juhuasuan.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView.OnItemClickListener;


import com.yunos.tv.lib.LOG;

import java.util.LinkedList;
import java.util.List;

abstract public class FocusedBasePositionManager {
//	public static final String TAG = "FocusedBasePositionManager";
    public static final int DEFAULT_FRAME_RATE = 6;
    public static final int DEFAULT_FRAME = 1;
	/**
	 * 焦点绘制模式:焦点框边移动到目标item边放大,通过{@link #setFocusMode}设置
	 */
	public static final int FOCUS_SYNC_DRAW = 0;
	/**
	 * 焦点绘制模式:焦点框移动到目标item上后放大,通过{@link #setFocusMode}设置
	 */
	public static final int FOCUS_ASYNC_DRAW = 1;
	/**
	 * 焦点绘制模式:焦点框立马跳到下一个选中item后随着目标item放大,通过{@link #setFocusMode}设置
	 */
	public static final int FOCUS_STATIC_DRAW = 2;
	/**
	 * 绘制状态:空闲
	 */
	public static final int STATE_IDLE = 0;
	/**
	 * 绘制状态:正在绘制中
	 */
	public static final int STATE_DRAWING = 1;

	/**
	 * 放大模式:按固定比例放大
	 */
	public static final int SCALED_FIXED_COEF = 1;
	/**
	 * 放大模式:横向放大固定像素,可以通过{@link #setItemScaleFixedX}设置,纵向自动计算
	 */
	public static final int SCALED_FIXED_X = 2;
	/**
	 * 放大模式:纵向放大固定像素,可以通过{@link #setItemScaleFixedY}设置,横向自动计算
	 */
	public static final int SCALED_FIXED_Y = 3;

	private boolean DEBUG = true;

	protected int mCurrentFrame = DEFAULT_FRAME;

	private int mState = STATE_IDLE;

	protected boolean mIsFirstFrame = true;
	private boolean mConstrantNotDraw = false;
	private ItemInterface mSelectedItem;
	
	protected HorizontalInterface  mHorizontalInterface = null;
	protected ContainInterface mContainer;
	
	private boolean mHasFocus = false;
	protected boolean mTransAnimation = false;
	protected Rect mLastFocusRect;
	protected Rect mFocusRect = new Rect();
	protected Rect mFocusShadowRect = new Rect();
	private Rect mCurrentRect;
	private boolean mScaleCurrentView = true;
	private int mDirection = -1;
	private boolean mIsScrolling = false;
	private FocusParams mFocusParams = null;
	private boolean mIsFocusDrawableVisible = true;
	private boolean mIsFocusShadowDrawableVisible = true;
	private ScaledList mScaledList = new ScaledList();
	protected boolean mIsFocusMove = true;

	private boolean mIsStart = false; 
	
	protected FocusedFrameLayout mFocusedFrameLayout = null;
	
	

	public FocusParams getParams() {
		return mFocusParams;
	}
 
	public FocusedBasePositionManager(FocusParams params, ContainInterface container) {
		this.mContainer = container;
		this.mFocusParams = params;
	}

	/**
	 * 绘制帧
	 * 
	 * @param canvas
	 *            画布
	 */
	public void drawFrame(Canvas canvas) {
		drawUnscale();
	}

	protected void drawUnscale() {
		this.mScaledList.drawUnscale();
	}

	public boolean canDrawNext() {
		return !mIsFirstFrame;
	}

	public void setFocusMove(boolean isMove) {
		this.mIsFocusMove = isMove;
	}

	protected void setStart(boolean isStart) {
		synchronized (this) {
			this.mIsStart = isStart;
		}
	}

	protected boolean isStart() {
		synchronized (this) {
			return this.mIsStart;
		}
	}
	
	
	public void onHorizontalScroll()
	{
//	    this.mContainer.horizontalScroll();
	    
	}
	
	/**
	 * 钱
	 * @param focusedFrameLayout
	 */
	
	public void onSetFocusedFrameLayout(FocusedFrameLayout focusedFrameLayout)
	{
	    mFocusedFrameLayout = focusedFrameLayout;
	}
	

	public void startDraw() {
		setStart(true);
		this.mContainer.invalidate();
	}

	public void stopDraw() {
		setStart(false);

		// if (!isLastFrame()) {
		ItemInterface item = getSelectedItem();
		if (item == null || !getParams().getScale() || !item.getIfScale()) {
			return;
		}

		FrameInterpolator scaleInterpolator = item.getFrameScaleInterpolator();
		if (scaleInterpolator == null) {
			scaleInterpolator = new LinearFrameInterpolator();
		}

		this.mScaledList.add(new ScaledInfo(item, getFrame(), this.mFocusParams.getScaleFrameRate(),
				this.mFocusParams.getItemScaleXValue(), this.mFocusParams.getItemScaleYValue(), scaleInterpolator));
		// }
	}

	int getFrame() {
		int frame = mCurrentFrame - getParams().getFocusFrameRate();
		frame = frame > 0 ? frame : 0;
		if (mCurrentFrame > getParams().getFrameRate()) {
			frame--;
		}
		return frame;
	}

	public void setScrolling(boolean isScrolling) {
		synchronized (this) {
			this.mIsScrolling = isScrolling;
		}
	}

	public boolean isScrolling() {
		synchronized (this) {
			return this.mIsScrolling;
		}
	}

	/**
	 * 设置当前缩放的view
	 * 
	 * @param isScale
	 */
	public void setScaleCurrentView(boolean isScale) {
		this.mScaleCurrentView = isScale;
	}

	/**
	 * 是否最后一帧
	 * 
	 * @return 当前是否为最后一帧
	 */
	public boolean isLastFrame() {
		return (this.mCurrentFrame >= this.mFocusParams.mFrameRate);
	}

	/**
	 * 设置忽略绘制
	 * 
	 * @param notDraw
	 *            是忽略绘制
	 */
	public void setContrantNotDraw(boolean notDraw) {
		this.mConstrantNotDraw = notDraw;
	}

	public boolean getContrantNotDraw() {
		return this.mConstrantNotDraw;
	}

	/**
	 * 设置焦点框显示状态
	 * 
	 * @param visible
	 * @param restart
	 */
	public void setFocusDrawableVisible(boolean visible, boolean restart) {
		// this.mFocusParams.getFocusDrawable().setVisible(visible, restart);
		this.mIsFocusDrawableVisible = visible;
	}

	/**
	 * 设置阴影焦点框显示状态
	 * 
	 * @param visible
	 * @param restart
	 */
	public void setFocusDrawableShadowVisible(boolean visible, boolean restart) {
		// this.mFocusParams.getFocusShadowDrawable().setVisible(visible,
		// restart);
		this.mIsFocusShadowDrawableVisible = visible;
	}

	/**
	 * 设置动画
	 * 
	 * @param transAnimation
	 */
	public void setTransAnimation(boolean transAnimation) {
		this.mTransAnimation = transAnimation;
	}

	public Rect getCurrentRect() {
		return this.mCurrentRect;
	}

	public void setState(int s) {
	    if(mState == s){
	        return;
	    }
		synchronized (this) {
			mState = s;
		}

		this.mContainer.reportState(s);
	}

	public int getState() {
		synchronized (this) {
			return mState;
		}
	}

	public void setFocusDirection(int direction) {
		this.mDirection = direction;
	}

	public int getFocusDirection() {
		return this.mDirection;
	}

	public void setFocus(boolean isFocus) {
		this.mHasFocus = isFocus;
	}

	public boolean hasFocus() {
		return this.mHasFocus;
	}

	public void setSelectedItem(ItemInterface item) {
		LOG.i(getLogTag(), DEBUG, "setSelectedView v = " + item);
		this.mSelectedItem = item;
	}

	public ItemInterface getSelectedItem() {
		return this.mSelectedItem;
	}

	public void reset() {
		this.mCurrentFrame = DEFAULT_FRAME;
		this.mIsFirstFrame = true;
		this.mTransAnimation = false;
	}

	public void computeScaleXY() {
		int scaleMode = getParams().getScaleMode();
		if (SCALED_FIXED_X == scaleMode || SCALED_FIXED_Y == scaleMode) {
			ItemInterface item = getSelectedItem();
			int width = item.getItemWidth();
			int height = item.getItemHeight();
			if (SCALED_FIXED_X == scaleMode) {
				float scaleFixedXValue = (float) (width + getParams().getItemScaleFixedX()) / width;
				getParams().setItemScaleValue(scaleFixedXValue, scaleFixedXValue);
			} else if (SCALED_FIXED_Y == scaleMode) {
				float scaleFixedYValue = (float) (height + getParams().getItemScaleFixedY()) / height;
				getParams().setItemScaleValue(scaleFixedYValue, scaleFixedYValue);
			}
		}
	}

	public float computeScaleXY(ItemInterface item) {
		int scaleMode = getParams().getScaleMode();
		if (SCALED_FIXED_X == scaleMode || SCALED_FIXED_Y == scaleMode) {
			int width = item.getItemWidth();
			int height = item.getItemHeight();
			if (SCALED_FIXED_X == scaleMode) {
				float scaleFixedXValue = (float) (width + getParams().getItemScaleFixedX()) / width;
				return scaleFixedXValue;
			} else if (SCALED_FIXED_Y == scaleMode) {
				float scaleFixedYValue = (float) (height + getParams().getItemScaleFixedY()) / height;
				return scaleFixedYValue;
			}
		}

		return 1.0f;
	}

	void drawScaleAndFocus(Canvas canvas, ItemInterface selectedItem, boolean isDrawFocus, boolean isDrawScale) {
		if (!getParams().getScale()) {
			isDrawScale = false;
		}

		float dstScaleXValue = 1.0f;
		float dstScaleYValue = 1.0f;

		if (isDrawScale) {
			float itemDiffScaleXValue = getParams().getItemScaleXValue() - 1.0f;
			float itemDiffScaleYValue = getParams().getItemScaleYValue() - 1.0f;

			float coef = (float) getFrame() / getParams().getScaleFrameRate();
			FrameInterpolator scaleInterpolator = selectedItem.getFrameScaleInterpolator();

			if (scaleInterpolator == null) {
				scaleInterpolator = new LinearFrameInterpolator();
			}

			coef = scaleInterpolator.getInterpolation(coef);
			dstScaleXValue = 1.0f + itemDiffScaleXValue * coef;
			dstScaleYValue = 1.0f + itemDiffScaleYValue * coef;

			selectedItem.setScaleX(dstScaleXValue);
			selectedItem.setScaleY(dstScaleYValue);
		}

		if (isDrawFocus) {
			mFocusRect.set(getDstRect(selectedItem, dstScaleXValue, dstScaleYValue));
			mFocusShadowRect.set(mFocusRect);
			offsetRect();

			if (DEBUG) {
				Log.d(getLogTag(), "drawScaleAndFocus: mFocusRect = " + mFocusRect + ", mCurrentFrame:" + mCurrentFrame + ", dstScaleXValue = "
						+ dstScaleXValue + ", dstScaleYValue = " + dstScaleYValue);
			}
			drawDrawable(canvas);
		}
	}

	void drawDrawable(Canvas canvas) {
		if (isLastFrame()) {
			if (mIsFocusShadowDrawableVisible) {
				getParams().getFocusShadowDrawable().setBounds(mFocusShadowRect);
				getParams().getFocusShadowDrawable().draw(canvas);
			}
		} else {
			if (mIsFocusDrawableVisible) {
				getParams().getFocusDrawable().setBounds(mFocusRect);
				getParams().getFocusDrawable().draw(canvas);
			}
		}
	}

	void offsetRect() {
		if (isLastFrame()) {
			offsetRect(mFocusShadowRect);
		} else {
			offsetRect(mFocusRect);
		}
	}

	void offsetRect(Rect rect) {
		performScrolloffset(rect);

		performManualOffset(rect);

		if (isLastFrame()) {
			performSelectedShadowDrawableOffset(rect);
		} else {
			performSelectedDrawableOffset(rect);
		}
	}
	
	void offsetRect(Rect rect, boolean forceNotLast) {
		performScrolloffset(rect);

		performManualOffset(rect);

		if (isLastFrame() && !forceNotLast) {
			performSelectedShadowDrawableOffset(rect);
		} else {
			performSelectedDrawableOffset(rect);
		}
	}

	void performScrolloffset(Rect rect) {
		rect.left += this.mContainer.getViewScrollX();
		rect.right += this.mContainer.getViewScrollX();
		rect.top += this.mContainer.getViewScrollY();
		rect.bottom += this.mContainer.getViewScrollY();
	}

	void performManualOffset(Rect rect) {
		rect.left += getParams().getManualPaddingLeft();
		rect.right += getParams().getManualPaddingRight();
		rect.top += getParams().getManualPaddingTop();
		rect.bottom += getParams().getManualPaddingBottom();
	}

	void performSelectedDrawableOffset(Rect rect) {
		Rect selectedPadding = getParams().getSelectedPadding();
		rect.top -= selectedPadding.top;
		rect.left -= selectedPadding.left;
		rect.right += selectedPadding.right;
		rect.bottom += selectedPadding.bottom;
	}

	void performSelectedShadowDrawableOffset(Rect rect) {
		// rect.top -= getParams().getSelectedShadowPaddingTop();
		// rect.left -= getParams().getSelectedShadowPaddingLeft();
		// rect.right += getParams().getSelectedShadowPaddingRight();
		// rect.bottom += getParams().getSelectedShadowPaddingBottom();

		Rect selectedshadowPadding = getParams().getSelectedShadowPadding();
		rect.top -= selectedshadowPadding.top;
		rect.left -= selectedshadowPadding.left;
		rect.right += selectedshadowPadding.right;
		rect.bottom += selectedshadowPadding.bottom;
	}

	public Rect getDstRect() {
		return getDstRect(getSelectedItem());
	}

	public Rect getDstRect(ItemInterface selectedItem) {

		Rect rect = selectedItem.getItemScaledRect(getParams().getItemScaleXValue(), getParams().getItemScaleYValue());

		// int[] location = new int[2];
		// this.mContainer.getLocationOnScreen(location);
		// rect.left -= location[0];
		// rect.top -= location[1];
		// rect.right -= location[0];
		// rect.bottom -= location[1];

		return rect;
	}

	public Rect getDstRect(ItemInterface selectedItem, float scaleX, float scaleY) {
		Rect rect = selectedItem.getItemScaledRect(scaleX, scaleY);

		// int[] location = new int[2];
		// this.mContainer.getLocationOnScreen(location);
		// rect.left -= location[0];
		// rect.top -= location[1];
		// rect.right -= location[0];
		// rect.bottom -= location[1];

		return rect;
	}

	public Rect getOriginalRect(ItemInterface selectedItem) {
		Rect rect = selectedItem.getOriginalRect();

		// int[] location = new int[2];
		// this.mContainer.getLocationOnScreen(location);
		// rect.left -= location[0];
		// rect.top -= location[1];
		// rect.right -= location[0];
		// rect.bottom -= location[1];

		return rect;
	}

	public static FocusedBasePositionManager createPositionManager(FocusParams params, ContainInterface container) {
//		if (params.getFocusMode() == FocusedBasePositionManager.FOCUS_STATIC_DRAW) {
//			//return new StaticPositionManager(params, container);
//		} else if (params.getFocusMode() == FocusedBasePositionManager.FOCUS_ASYNC_DRAW) {
//
//			return new AsyncPositionManager(params, container);
//
//		} else if (params.getFocusMode() == FocusedBasePositionManager.FOCUS_SYNC_DRAW) {
//			//return new SyncPositionManager(params, container);
//		}
//
//		return null;
		

		return new AsyncPositionManager(params, container);
	}

	// public abstract void drawChild(Canvas canvas);

	public interface PositionInterface {
		public void createPositionManager(FocusParams params);

		public void setOnItemSelectedListener(FocusItemSelectedListener listener);

		public void setOnItemClickListener(OnItemClickListener listener);
	}

	/**
	 * 焦点选中状态监听器
	 * 
	 */
	public interface FocusItemSelectedListener {
		/**
		 * 选中状态变化回调
		 * 
		 * @param v
		 *            选中的元素
		 * @param position
		 *            元素对应的位置
		 * @param isSelected
		 *            元素是否选中
		 * @param view
		 *            父容器
		 */
		public void onItemSelected(View v, int position, boolean isSelected, View view);
	}

	public interface OnScrollListener {

		/**
		 * The view is not scrolling. Note navigating the list using the
		 * trackball counts as being in the idle state since these transitions
		 * are not animated.
		 */
		public static int SCROLL_STATE_IDLE = 0;

		/**
		 * The user is scrolling using touch, and their finger is still on the
		 * screen
		 */
		public static int SCROLL_STATE_TOUCH_SCROLL = 1;

		/**
		 * The user had previously been scrolling using touch and had performed
		 * a fling. The animation is now coasting to a stop
		 */
		public static int SCROLL_STATE_FLING = 2;

		/**
		 * Callback method to be invoked while the list view or grid view is
		 * being scrolled. If the view is being scrolled, this method will be
		 * called before the next frame of the scroll is rendered. In
		 * particular, it will be called before any calls to
		 * {@link Adapter#getView(int, View, ViewGroup)}.
		 * 
		 * @param view
		 *            The view whose scroll state is being reported
		 * 
		 * @param scrollState
		 *            The current scroll state. One of
		 *            {@link #SCROLL_STATE_IDLE},
		 *            {@link #SCROLL_STATE_TOUCH_SCROLL} or
		 *            {@link #SCROLL_STATE_IDLE}.
		 */
		public void onScrollStateChanged(ViewGroup view, int scrollState);

		/**
		 * Callback method to be invoked when the list or grid has been
		 * scrolled. This will be called after the scroll has completed
		 * 
		 * @param view
		 *            The view whose scroll state is being reported
		 * @param firstVisibleItem
		 *            the index of the first visible cell (ignore if
		 *            visibleItemCount == 0)
		 * @param visibleItemCount
		 *            the number of visible cells
		 * @param totalItemCount
		 *            the number of items in the list adaptor
		 */
		public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}

	public interface ItemInterface {
		public void setScaleX(float scaleX);

		public void setScaleY(float scaleY);

		public int getItemWidth();

		public int getItemHeight();

		public Rect getOriginalRect();

		public Rect getItemScaledRect(float scaledX, float scaledY);

		public boolean getIfScale();

		public FrameInterpolator getFrameScaleInterpolator();

		public FrameInterpolator getFrameFocusInterpolator();

		public Rect getFocusPadding(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate);
	}

	public interface ContainInterface {
		public void invalidate();

		public void postInvalidate();

		public void getLocationOnScreen(int[] location);

		public int getViewScrollX();

		public int getViewScrollY();

		public void reportState(int state); 
	}
	
	
	

	public interface HorizontalInterface
	{ 
//	    public void onHorizontalScroll();
	    
	}
	
	

	class ScaledList {
		List<ScaledInfo> mList = new LinkedList<ScaledInfo>();
		Object lock = new Object();
		boolean mIsBreak = false;

		public void add(ScaledInfo info) {
			setBreak(true);
			synchronized (this) {
				this.mList.add(info);
			}
		}

		public void drawUnscale() {
			setBreak(false);
			synchronized (this) {
				while (this.mList.size() > 0 && !isBreak()) {
					ScaledInfo info = this.mList.get(0);
					if (info.getFrame() < 0) {
						this.mList.remove(0);
					} else {
						break;
					}
				}

				for (int index = 0; index < this.mList.size() && !isBreak(); index++) {
					drawUnscale(this.mList.get(index));
				}

				if (this.mList.size() > 0) {
					mContainer.invalidate();
				}
			}
		}

		void setBreak(boolean isBreak) {
			synchronized (lock) {
				this.mIsBreak = isBreak;
			}
		}

		boolean isBreak() {
			synchronized (lock) {
				return this.mIsBreak;
			}
		}

		private void drawUnscale(ScaledInfo info) {
			if (info.getFrame() < 0) {
				return;
			}

			float itemDiffScaleXValue = info.getScaleX() - 1.0f;
			float itemDiffScaleYValue = info.getScaleY() - 1.0f;
			float coef = (float) info.getFrame() / info.getFrameRate();
			coef = info.getScaleFrameInterpolator().getInterpolation(coef);
			float dstScaleX = 1.0f + itemDiffScaleXValue * coef;
			float dstScaleY = 1.0f + itemDiffScaleYValue * coef;
			info.getItem().setScaleX(dstScaleX);
			info.getItem().setScaleY(dstScaleY);

			if(DEBUG){
				Log.d(getLogTag(), "drawUnscale: dstScaleX = " + dstScaleX + ", dstScaleY = " + dstScaleY);
			}
			info.subFrame();
		}
	}

	class ScaledInfo {
		ItemInterface mItem;
		int mFrame;
		int mFrameRate;
		float mScaleX;
		float mScaleY;
		FrameInterpolator mScaleInterpolator;

		public ScaledInfo() {

		}

		public ScaledInfo(ItemInterface item, int frame, int frameRate, float scaleX, float scaleY, FrameInterpolator scaleInterpolator) {
			this.mItem = item;
			this.mFrame = frame;
			this.mFrameRate = frameRate;
			this.mScaleX = scaleX;
			this.mScaleY = scaleY;
			this.mScaleInterpolator = scaleInterpolator;
		}

		public ItemInterface getItem() {
			return this.mItem;
		}

		public void subFrame() {
			this.mFrame--;
		}

		public int getFrame() {
			return this.mFrame;
		}

		public int getFrameRate() {
			return this.mFrameRate;
		}

		public float getScaleX() {
			return this.mScaleX;
		}

		public float getScaleY() {
			return this.mScaleY;
		}

		public FrameInterpolator getScaleFrameInterpolator() {
			return this.mScaleInterpolator;
		}
	}

	public interface FrameInterpolator {
		float getInterpolation(float input);
	}

	public static class LinearFrameInterpolator implements FrameInterpolator {

		@Override
		public float getInterpolation(float input) {
			return input;
		}

	}

	/**
	 * An interpolator where the rate of change starts out quickly and and then
	 * decelerates.
	 * 
	 */
	public static class DecelerateFrameInterpolator implements FrameInterpolator {
		public DecelerateFrameInterpolator() {
		}

		/**
		 * Constructor
		 * 
		 * @param factor
		 *            Degree to which the animation should be eased. Setting
		 *            factor to 1.0f produces an upside-down y=x^2 parabola.
		 *            Increasing factor above 1.0f makes exaggerates the
		 *            ease-out effect (i.e., it starts even faster and ends
		 *            evens slower)
		 */
		public DecelerateFrameInterpolator(float factor) {
			mFactor = factor;
		}

		public float getInterpolation(float input) {
			float result;
			if (mFactor == 1.0f) {
				result = (float) (1.0f - (1.0f - input) * (1.0f - input));
			} else {
				result = (float) (1.0f - Math.pow((1.0f - input), 2 * mFactor));
			}
			return result;
		}

		private float mFactor = 1.0f;
	}

	/**
	 * An interpolator where the rate of change starts out slowly and and then
	 * accelerates.
	 * 
	 */
	public static class AccelerateFrameInterpolator implements FrameInterpolator {
		private final float mFactor;
		private final double mDoubleFactor;

		public AccelerateFrameInterpolator() {
			mFactor = 1.0f;
			mDoubleFactor = 2.0;
		}

		/**
		 * Constructor
		 * 
		 * @param factor
		 *            Degree to which the animation should be eased. Seting
		 *            factor to 1.0f produces a y=x^2 parabola. Increasing
		 *            factor above 1.0f exaggerates the ease-in effect (i.e., it
		 *            starts even slower and ends evens faster)
		 */
		public AccelerateFrameInterpolator(float factor) {
			mFactor = factor;
			mDoubleFactor = 2 * mFactor;
		}

		public float getInterpolation(float input) {
			if (mFactor == 1.0f) {
				return input * input;
			} else {
				return (float) Math.pow(input, mDoubleFactor);
			}
		}
	}
	
	abstract public String getLogTag();
}

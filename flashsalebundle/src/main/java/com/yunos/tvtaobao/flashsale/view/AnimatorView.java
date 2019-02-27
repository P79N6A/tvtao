/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-4-19       lizhi.ywp
*
*/

package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

/**
 * Base class for a {@link FrameLayout} container that will perform animations
 * when switching between its views.
 * 
 * @attr ref android.R.styleable#ViewAnimator_inAnimation
 * @attr ref android.R.styleable#ViewAnimator_outAnimation
 * @attr ref android.R.styleable#ViewAnimator_animateFirstView
 */
public class AnimatorView extends FrameLayout {
	/** 定义切换方向 */
	public final static byte ANIM_DIR_NORMAL = 0; // 直接跳转
	public final static byte ANIM_DIR_FORWORD = 1; // 切换下一个
	public final static byte ANIM_DIR_BACKGROUD = 2; // 切换上一个
	public final static long VIEW_CHANGE_CHECK_TIMEOUT = 20;
	
	/**动画标志位*/
	private final static int ANIM_SYTLE_NO = 0;
	private final static int ANIM_SYTLE_IN = 0X1;
	private final static int ANIM_SYTLE_OUT = (0x1 << 1);
	private final static int ANIM_MASK = ANIM_SYTLE_IN | ANIM_SYTLE_OUT;

	
	/** 普通切换 */
	public final static byte SWITCH_MODE_NORMAL = 0;

	/** 循环切换 */
	public final static byte SWITCH_MODE_HALFAUTO_CYCLE = 1;

	/** 当前显示的child索引 */
	protected int mWhichChild = 0;

	/** 记录当前显示的View，用来判断切换过程中是否需要进行动画切换 */
	protected View mCurChild = null;

	/** 第一次显示,即没有切换过 */
	protected boolean mFirstTime = true;

	/***/
	protected boolean mAnimateFirstTime = true;

	/** 进入动画 */
	protected Animation mInAnimation;
	/** 出场动画 */
	protected Animation mOutAnimation;

	/** 切换模式，判断是否支持循环切换 */
	protected byte mSwitchMode = SWITCH_MODE_NORMAL;

	public AnimatorView(Context context) {
		super(context);
		initViewAnimator(context, null);
	}

	public AnimatorView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initViewAnimator(context, attrs);
	}

	public AnimatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initViewAnimator(context, attrs);
	}

	/**
	 * Initialize this {@link AnimatorView}, possibly setting
	 * {@link #setMeasureAllChildren(boolean)} based on {@link FrameLayout}
	 * flags.
	 */
	protected void initViewAnimator(Context context, AttributeSet attrs) {
		if (attrs == null) {
			// For compatibility, always measure children when undefined.
			setMeasureAllChildren(true);
		} else {
			// 加载自定义的属性，暂时不支持
		}
		initView();
	}

	private void resetWhenNoView() {
		mWhichChild = 0;
		mFirstTime = true;
		mCurChild = null;
	}

	@Override
	public void removeAllViews() {
		super.removeAllViews();
		resetWhenNoView();
	}

	@Override
	public void removeView(View view) {
		final int index = indexOfChild(view);
		if (index >= 0) {
			removeViewAt(index);
		}
	}

	@Override
	public void removeViewAt(int index) {
		super.removeViewAt(index);
		final int childCount = getChildCount();

		if (childCount == 0) {
			resetWhenNoView();
		} else if (mWhichChild == index) {
			// Displayed was removed, so show the new child living in its place
			mCurChild = null;
			setDisplayedChild(mWhichChild);
		} else if (mWhichChild > index) {
			// the removing view is in the front of the current displaying view,
			// only update index and current display view.
			setDisplayedChild(mWhichChild - 1);
		} else {
			// do nothing when the removing view is in the background of the
			// current displaying view.
		}
		OnViewChange();
	}

	@Override
	public void removeViewInLayout(View view) {
		removeView(view);
	}

	@Override
	public void removeViews(int start, int count) {
		super.removeViews(start, count);
		int childCount = getChildCount();

		if (0 == childCount) {
			resetWhenNoView();
		} else if (mWhichChild >= start && mWhichChild < start + count) {
			// Try showing new displayed child, wrapping if needed
			mCurChild = null;
			setDisplayedChild(mWhichChild);
		} else if (mWhichChild >= start + count) {
			// the removing view is in the front of the current displaying view,
			// only update index and current display view.
			setDisplayedChild(mWhichChild - count);
		}
		OnViewChange();
	}

	@Override
	public void removeViewsInLayout(int start, int count) {
		removeViews(start, count);
	}

	@Override
	public int getBaseline() {
		View v = getCurrentView();

		return (null != v) ? v.getBaseline() : super.getBaseline();
	}

	private boolean checkAvalible(int whichChild) {
		if (SWITCH_MODE_NORMAL == mSwitchMode) {
			return (whichChild >= 0) && (whichChild < getChildCount());
		}
		return true;
	}

	/**
	 * init view relative parameter.
	 */
	protected void initView() {

	}

	/**
	 * Shows only the specified child. The other displays Views exit the screen,
	 * optionally with the with the {@link #getOutAnimation() out animation} and
	 * the specified child enters the screen, optionally with the
	 * {@link #getInAnimation() in animation}.
	 * 
	 * @param childIndex
	 *            The index of the child to be shown.
	 * @param animate
	 *            Whether or not to use the in and out animations, defaults to
	 *            true.
	 */
	protected void showOnly(int childIndex, boolean animate) {
		/** 检测是左移动或者右移动，还是没有移动 */
		View selectView = null;
		View unselectView = null;
		int unselectIndex = -1;
		View child;

		mCurChild = getChildAt(childIndex);
		if (mCurChild.getVisibility() == View.VISIBLE) {
			/** nothing */
			return;
		}
		final int count = getChildCount();

		for (int i = 0; i < count; i++) {
			child = getChildAt(i);

			if (i == childIndex) {
				/** 查找到当前的需要显示的 */
				selectView = child;
				if (mOutAnimation != null
						&& child.getAnimation() == mOutAnimation) {
					child.clearAnimation();
				}
			} else {
				if (child.getVisibility() == View.VISIBLE) {
					/** 隐藏先前的 */
					if (unselectIndex >= 0) {
						/** 如果出现多个，则不做任何动画 */
						if (null != unselectView) {
							unselectView.setVisibility(View.GONE);
							unselectView = null;
						}
						child.setVisibility(View.GONE);
					} else {
						unselectView = child;
						unselectIndex = i;
					}
				}
				if (null != mInAnimation
						&& child.getAnimation() == mInAnimation) {
					child.clearAnimation();
				}
			}
		}
		if (animate && null != unselectView) {
			/** 出场动画 */
			if (null != mOutAnimation) {
				// onPageWillUnselected(unselectView, unselectIndex);
				AnimCallback callback = new AnimCallback(selectView,
						childIndex, unselectView, unselectIndex, true);
				onPageWillUnselected(selectView, childIndex, unselectView, unselectIndex);
				mOutAnimation.setAnimationListener(callback);
				unselectView.startAnimation(mOutAnimation);
			} else {
				onPageUnselected(unselectView, unselectIndex);
			}
			unselectView.setVisibility(View.GONE);

			if (null != mInAnimation) {
				AnimCallback callback = new AnimCallback(selectView,
						childIndex, unselectView, unselectIndex, false);
				onPageWillSelected(selectView, childIndex, unselectView, unselectIndex);
				mInAnimation.setAnimationListener(callback);
				selectView.startAnimation(mInAnimation);
			} else {
				onPageSelected(selectView, childIndex);
			}
			selectView.setVisibility(View.VISIBLE);

		} else {
			selectView.setVisibility(View.VISIBLE);
			onPageSelected(selectView, childIndex);
			if (null != unselectView) {
				unselectView.setVisibility(View.GONE);
				onPageUnselected(unselectView, unselectIndex);
			}
		}
		mFirstTime = false;
	}
	private int mAnimFlag = ANIM_SYTLE_NO;
	
	public boolean isAnim(){
		return 0 != (mAnimFlag & ANIM_MASK); 
	}
	class AnimCallback implements AnimationListener {
		private View mSelectView;
		private int mSelectIndex;
		private View mUnselectView;
		private int mUnselectIndex;
		private boolean mIsAnimOut;

		public AnimCallback() {

		}

		public AnimCallback(View selectView, int selectIndex,
                            View unselectView, int unselectIndex, boolean isAnimOut) {
			set(selectView, selectIndex, unselectView, unselectIndex, isAnimOut);
		}

		public void set(View selectView, int selectIndex, View unselectView,
                        int unselectIndex, boolean isAnimOut) {
			mSelectView = selectView;
			mSelectIndex = selectIndex;
			mUnselectView = unselectView;
			mUnselectIndex = unselectIndex;
			mIsAnimOut = isAnimOut;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			if (mIsAnimOut) {
				
//				onPageWillUnselected(mSelectView, mSelectIndex, mUnselectView,
//						mUnselectIndex);
				mAnimFlag |= ANIM_SYTLE_OUT;
			} else {
//				onPageWillSelected(mSelectView, mSelectIndex, mUnselectView,
//						mUnselectIndex);
				mAnimFlag |= ANIM_SYTLE_IN;
			}
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if (mIsAnimOut) {
				onPageUnselected(mUnselectView, mUnselectIndex);
				mAnimFlag &= ~ANIM_SYTLE_OUT;
			} else {
				onPageSelected(mSelectView, mSelectIndex);
				mAnimFlag &= ~ANIM_SYTLE_IN;
			}
			// ViewAnimator.this.setLayerType(View.LAYER_TYPE_NONE, null);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Shows only the specified child. The other displays Views exit the screen
	 * with the {@link #getOutAnimation() out animation} and the specified child
	 * enters the screen with the {@link #getInAnimation() in animation}.
	 * 
	 * @param childIndex
	 *            The index of the child to be shown.
	 */
	protected void showOnly(int childIndex) {
		final boolean animate = (!mFirstTime || mAnimateFirstTime);
		showOnly(childIndex, animate);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		child.setVisibility(View.GONE);
		
		if (index >= 0 && mWhichChild >= index) {
			// Added item above current one, increment the index of the
			setDisplayedChild(mWhichChild + 1);
		}
		OnViewChange();
	}

	/**
	 * Returns whether the current View should be animated the first time the
	 * ViewAnimator is displayed.
	 * 
	 * @return true if the current View will be animated the first time it is
	 *         displayed, false otherwise.
	 * 
	 * @see #setAnimateFirstView(boolean)
	 */
	public boolean getAnimateFirstView() {
		return mAnimateFirstTime;
	}

	/**
	 * Indicates whether the current View should be animated the first time the
	 * ViewAnimator is displayed.
	 * 
	 * @param animate
	 *            True to animate the current View the first time it is
	 *            displayed, false otherwise.
	 */
	public void setAnimateFirstView(boolean animate) {
		mAnimateFirstTime = animate;
	}

	/**
	 * Returns the current animation used to animate a View that enters the
	 * screen.
	 * 
	 * @return An Animation or null if none is set.
	 * 
	 * @see #setInAnimation(android.view.animation.Animation)
	 * @see #setInAnimation(android.content.Context, int)
	 */
	public Animation getInAnimation() {
		return mInAnimation;
	}

	/**
	 * Specifies the animation used to animate a View that enters the screen.
	 *
	 * @param inAnimation
	 *            The animation started when a View enters the screen.
	 *
	 * @see #getInAnimation()
	 * @see #setInAnimation(android.content.Context, int)
	 */
	public void setInAnimation(Animation inAnimation) {
		mInAnimation = inAnimation;
	}

	/**
	 * Returns the current animation used to animate a View that exits the
	 * screen.
	 *
	 * @return An Animation or null if none is set.
	 *
	 * @see #setOutAnimation(android.view.animation.Animation)
	 * @see #setOutAnimation(android.content.Context, int)
	 */
	public Animation getOutAnimation() {
		return mOutAnimation;
	}

	/**
	 * Specifies the animation used to animate a View that exit the screen.
	 *
	 * @param outAnimation
	 *            The animation started when a View exit the screen.
	 *
	 * @see #getOutAnimation()
	 * @see #setOutAnimation(android.content.Context, int)
	 */
	public void setOutAnimation(Animation outAnimation) {
		mOutAnimation = outAnimation;
	}

	/**
	 * Specifies the animation used to animate a View that enters the screen.
	 *
	 * @param context
	 *            The application's environment.
	 * @param resourceID
	 *            The resource id of the animation.
	 *
	 * @see #getInAnimation()
	 * @see #setInAnimation(android.view.animation.Animation)
	 */
	public void setInAnimation(Context context, int resourceID) {
		setInAnimation(AnimationUtils.loadAnimation(context, resourceID));
	}

	/**
	 * Specifies the animation used to animate a View that exit the screen.
	 *
	 * @param context
	 *            The application's environment.
	 * @param resourceID
	 *            The resource id of the animation.
	 *
	 * @see #getOutAnimation()
	 * @see #setOutAnimation(android.view.animation.Animation)
	 */
	public void setOutAnimation(Context context, int resourceID) {
		setOutAnimation(AnimationUtils.loadAnimation(context, resourceID));
	}

	/**
	 * Returns the View corresponding to the currently displayed child.
	 * 
	 * @return The View currently displayed.
	 * 
	 * @see #getDisplayedChild()
	 */
	public View getCurrentView() {
		return getChildAt(mWhichChild);
	}

	/**
	 * set the currently displayed child.
	 * 
	 * @param v
	 *            the currently displayed child.
	 * @return true if exist, else false.
	 * 
	 * @see #getCurrentView()
	 */
	public boolean setCurrentView(View v) {
		final int index = indexOfChild(v);
		if (index >= 0) {
			setDisplayedChild(index);
			return true;
		}
		return false;
	}

	/**
	 * Returns the index of the currently displayed child view.
	 */
	public int getDisplayedChild() {
		return mWhichChild;
	}

	/**
	 * set the currently switch mode.
	 * 
	 * @param mode
	 *            the currently switch mode.
	 * @return
	 * 
	 * @see #getCurrentView()
	 */
	public void setSwitchMode(byte mode) {
		mSwitchMode = mode;
	}

	/**
	 * get the currently switch mode.
	 * 
	 * @return the currently switch mode
	 * 
	 * @see #getCurrentView()
	 */
	public byte getSwitchMode() {
		return mSwitchMode;
	}

	/**
	 * Sets which child view will be displayed.
	 * 
	 * @param whichChild
	 *            the index of the child view to display
	 */
	public void setDisplayedChild(int whichChild) {
		setDisplayedChild(whichChild, false);
	}

	public void setDisplayedChild(int whichChild, boolean notNeedAnim) {
		mWhichChild = whichChild;
		if (whichChild >= getChildCount()) {
			mWhichChild = 0;
		} else if (whichChild < 0) {
			mWhichChild = getChildCount() - 1;
		}
		boolean hasFocus = getFocusedChild() != null;
		// This will clear old focus if we had it
		if (notNeedAnim) {
			showOnly(mWhichChild, false);
		} else {
			showOnly(mWhichChild);
		}

		if (hasFocus) {
			// Try to retake focus if we had it
			requestFocus(FOCUS_FORWARD);
		}
	}

	/**
	 * has next View.
	 */
	public boolean hasNext() {
		int childSize = getChildCount();

		return (mWhichChild + 1) < childSize;
	}

	/**
	 * has previous View.
	 */
	public boolean hasPrevious() {
		return (mWhichChild - 1) >= 0;
	}

	/**
	 * Manually shows the next child.
	 */
	public void showNext() {
		int newIndex = mWhichChild + 1;

		if (checkAvalible(newIndex)) {
			setDisplayedChild(newIndex);
		}
	}

	/**
	 * Manually shows the previous child.
	 */
	public void showPrevious() {
		int newIndex = mWhichChild - 1;

		if (checkAvalible(newIndex)) {
			setDisplayedChild(newIndex);
		}
	}

	/**
	 * Callback interface for responding to changing state of the selected page.
	 */
	public interface OnPageSelectListener {
		/**
		 * This method will be invoked when a new page becomes selected. if it
		 * has Animation, Animation is necessarily complete, if not, directly
		 * callback.
		 * 
		 * @param selectView
		 *            instance of the new selected view.
		 * @param selectPos
		 *            Position index of the new selected view.
		 * 
		 */
		public void onPageSelected(View selectView, int selectPos);

		/**
		 * This method will be invoked when a new page will be selected
		 * 
		 * 
		 * @param selectView
		 *            instance of the new selected view.
		 * @param selectPos
		 *            Position index of the new unselected view.
		 * @param unselectView
		 *            instance of the previous unselected view.
		 * @param unselectPos
		 *            Position index of the previous unselected view.
		 * 
		 */
		public void onPageWillSelected(View selectView, int selectPos,
                                       View unselectView, int unselectPos);

	}

	private OnPageSelectListener mOnPageSelectListener;

	/**
	 * set the selected Callback interface for responding to changing state of
	 * the selected page.
	 */
	public void setOnPageSelectListener(OnPageSelectListener l) {
		mOnPageSelectListener = l;
	}

	/**
	 * get the selected Callback interface for responding to changing state of
	 * the selected page.
	 */
	public OnPageSelectListener getOnPageSelectListener() {
		return mOnPageSelectListener;
	}

	protected void onPageSelected(View selectView, int selectPos) {
		OnPageSelectListener l = getOnPageSelectListener();

		if (null != l) {
			l.onPageSelected(selectView, selectPos);
		}
	}

	protected void onPageWillSelected(View selectView, int selectPos,
                                      View unselectView, int unselectPos) {
		OnPageSelectListener l = getOnPageSelectListener();
		if (null != l) {
			l.onPageWillSelected(selectView, selectPos, unselectView,
					unselectPos);
		}
	}

	/**
	 * Callback interface for add view or remove view
	 * 
	 */
	public interface OnViewChangeListener {
		public void OnViewChange(View selectView, int selectPos);
	}

	private OnViewChangeListener mOnViewChangeListener;
	private Runnable mCheckViewChangeListener;
	final private Handler mMainHandler = new Handler(Looper.getMainLooper());
	
	public Handler getMainHandler(){
		return mMainHandler;
	}
	public void OnViewChange(){
		if( null == mOnViewChangeListener){
			return;
		}
		if( null == mCheckViewChangeListener){
			
			mCheckViewChangeListener = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					OnViewChangeListener l = mOnViewChangeListener;
					View v = getCurrentView();
					
					if( null != l && null != v){
						l.OnViewChange(v, mWhichChild);
					}
				}
			};
		}else{
			mMainHandler.removeCallbacks(mCheckViewChangeListener);
		}
		mMainHandler.postDelayed(mCheckViewChangeListener, VIEW_CHANGE_CHECK_TIMEOUT);
	}
	/**
	 * set the view change Callback interface for remove or add view
	 * 
	 */
	public void setOnViewChangeListener(OnViewChangeListener l) {
		mOnViewChangeListener = l;
	}
	/**
	 * get the view change Callback interface for remove or add view
	 * 
	 */
	public OnViewChangeListener getOnViewChangeListener() {
		return mOnViewChangeListener;
	}
	
	
	
	/**
	 * Callback interface for responding to changing state of the unselected
	 * page.
	 */
	public interface OnPageUnselectListener {
		/**
		 * This method will be invoked when the page becomes unselected. if it
		 * has Animation, Animation is necessarily complete, if not, directly
		 * callback.
		 * 
		 * @param unselectView
		 *            instance of the unselected view.
		 * @param unselectPos
		 *            Position index of the unselected view.
		 * 
		 */
		public void onPageUnselected(View unselectView, int unselectPos);

		/**
		 * This method will be invoked when the page will be gone
		 * 
		 * 
		 * @param selectView
		 *            instance of the new selected view.
		 * @param selectPos
		 *            Position index of the new unselected view.
		 * @param unselectView
		 *            instance of the previous unselected view.
		 * @param unselectPos
		 *            Position index of the previous unselected view.
		 * 
		 */
		public void onPageWillUnselected(View selectView, int selectPos,
                                         View unselectView, int unselectPos);

	}

	private OnPageUnselectListener mOnPageUnselectListener;

	/**
	 * set the selected Callback interface for responding to changing state of
	 * the unselected page.
	 */
	public void setOnPageUnselectListener(OnPageUnselectListener l) {
		mOnPageUnselectListener = l;
	}

	/**
	 * get the selected Callback interface for responding to changing state of
	 * the unselected page.
	 */
	public OnPageUnselectListener getOnPageUnselectListener() {
		return mOnPageUnselectListener;
	}

	protected void onPageUnselected(View unselectView, int unselectPos) {
		OnPageUnselectListener l = getOnPageUnselectListener();

		if (null != l) {
			l.onPageUnselected(unselectView, unselectPos);
		}
	}
	
	protected void onPageWillUnselected(View selectView, int selectPos,
                                        View unselectView, int unselectPos) {
		OnPageUnselectListener l = getOnPageUnselectListener();

		if (null != l) {
			l.onPageWillUnselected(selectView, selectPos, unselectView,
					unselectPos);
		}
	}
}

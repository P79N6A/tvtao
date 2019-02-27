package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.FlipHGridView;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.lib.SystemProUtils;

import java.util.ArrayList;

/**
 * 可进行focus的弹性间距GridView的控件
 * 1.是针对本身居中滚动
 * 2.针对按键在特点的情况下做了一些限制（只能单个方向上滚动，也就是如果正在向下滚动不能向上按键需要在preOnKey里面做限制）
 * 注：目前只支持单个headerView，不支持多个跟footerView
 *     在滚动的过程中重新layout是无效的
 * 
 * @author tim
 *
 */
public class FocusFlipHGridView extends FlipHGridView implements FocusListener, DeepListener, ItemListener {
	private final boolean DEBUG = true;
	private final String TAG = ((Object)this).getClass().getSimpleName();
	private final boolean DYNAMIC_ADD_CHILD_VIEW = true;//是否在滚动的时候添加列表的childView,
											//优点：提升速度，缺点：因为view的位置都是通过计算取得的所以会有使用上限制
	protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
	private FocusRectParams mFocusRectparams = new FocusRectParams();
	protected Rect mClipFocusRect = new Rect();
	
	
	private boolean mIsAnimate = true;
	private int mDistance = -1;
	boolean mDeepFocus = false;
	private boolean mIsFirstLayout = true;//是否为首次布局
	private boolean mFirstAnimDone = true;//是否正在首次动画
	private OnFocusFlipGridViewListener mOnFocusFlipGridViewListener;//监听器
	private OutAnimationRunnable mOutAnimationRunnable;//出场的动画
	private boolean mNeedResetParam = false;//是否需要对focus区域相关参数的重置，因为reset()被放在layout完成的回调里面
	private int mOnKeyDirection = FOCUS_RIGHT;//此时的按键的键值，为了当进入headerView的时候支持findFocus接口
	private Rect mPreFocusRect = new Rect();//上次focus的区域，为了当进入headerView的时候支持findFocus接口
	private boolean mCenterFocus = true;//是否需要居中显示focus框
	private ItemSelectedListener mItemSelectedListener;
	private boolean mNeedAutoSearchFocused = true;//只有第一次需要自动查找，后续focus的时候只需要使用上次选中的item就可以
	private boolean mAnimAlpha = true;//是否需要在动画的时候设置alpha值
	private RectF mAlphaRectF;
	private int mAnimAlphaValue = 255;
	private boolean mAimateWhenGainFocusFromLeft = true;
	private boolean mAimateWhenGainFocusFromRight = true;
	private boolean mAimateWhenGainFocusFromUp = true;
	private boolean mAimateWhenGainFocusFromDown = true;
	public FocusFlipHGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FocusFlipHGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FocusFlipHGridView(Context context) {
		super(context);
		init();
	}


	public void setOnItemSelectedListener(ItemSelectedListener listener) {
		mItemSelectedListener = listener;
	}
	
	public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown){
		mAimateWhenGainFocusFromLeft = fromleft;
		mAimateWhenGainFocusFromUp = fromUp;
		mAimateWhenGainFocusFromRight = fromRight;
		mAimateWhenGainFocusFromDown = fromDown;
	}
	
	/**
	 * 初始化focus相关操作
	 */
	public void initFocused(){
		setNeedAutoSearchFocused(true);
		//当焦点离开的时候清理之前headerView的focus信息
		if(getHeaderViewsCount() > 0){
			for(int i = 0; i < getHeaderViewsCount(); i++){
				View view = mHeaderViewInfos.get(i).view;
				if(view instanceof FocusRelativeLayout){
					FocusRelativeLayout headerView = (FocusRelativeLayout)view;
					headerView.notifyLayoutChanged();
					headerView.clearFocusedIndex();
				}
			}
		}
	}
	
	
	/**
	 * 设置在onFocused的时候是否需要自己查找childView
	 */
	public void setNeedAutoSearchFocused(boolean b){
		mNeedAutoSearchFocused = b;
	}
	
	@Override
	public void setSelection(int position) {
		if(isFlipFinished()){
			if (getChildCount() > 0 && !mIsFirstLayout) {
				View preSelectedView = getSelectedView();
				int preSelectedPos = getSelectedItemPosition();
				setSelectedPositionInt(position);
				setNextSelectedPositionInt(position);
				mLayoutMode = LAYOUT_FROM_MIDDLE;
				lockFlipAnimOnceLayout();
				mNeedLayout = true;
				mNeedAutoSearchFocused = false;
				mNeedResetParam = true;
				layoutChildren(); 
				if(isFocused()){
					checkSelected(preSelectedView, preSelectedPos);					
				}
				else{
					//如果没有focused就不需要设置false了，因为之前unfocused的时候已经设置false
					//视图状态的变化与是否有监听器无关，状态改变后，如果有监听器则需要通知。  quanqing.hqq
					View currSelectedView = getSelectedView();
					int currSelectedPos = getSelectedItemPosition();
					if(preSelectedPos != currSelectedPos){
						onItemSelectedChanged(currSelectedView, currSelectedPos, true);
					}
				}
			}
		}
	}
	
	void onItemSelectedChanged(View item, int position, boolean selected){
		if(item != null){
			item.setSelected(selected);
		}
		if (mItemSelectedListener != null) {
			mItemSelectedListener.onItemSelected(item, position, selected, this);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		View preSelectedView = getSelectedView();
		int preSelectedPos = getSelectedItemPosition();
		mIsAnimate = true;
		boolean hasFocused = hasFocus();
		if(!hasFocused){
			Log.i(TAG, "requestFocus for touch event to onKeyUp");
			requestFocus();
		}
		//保存键值
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			mOnKeyDirection = FOCUS_LEFT;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			mOnKeyDirection = FOCUS_RIGHT;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			mOnKeyDirection = FOCUS_UP;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			mOnKeyDirection = FOCUS_DOWN;
			break;
		default:
			mOnKeyDirection = FOCUS_RIGHT;
			break;
		}
		int selectedPos = getSelectedItemPosition();
		//判断选中的是否是headerView，如果是的话就进行深入
		if(selectedPos < getHeaderViewsCount()){
			//headerView
			View view = getSelectedView();
			if(view instanceof FocusRelativeLayout){
				FocusRelativeLayout headerView = (FocusRelativeLayout)view;
				boolean headerViewRet = headerView.onKeyDown(keyCode, event);
				if(DEBUG){
					Log.i(TAG, "onKeyDown headerViewRet="+headerViewRet);
				}
				if(headerViewRet == true){
					mNeedResetParam = true;
					layoutResetParam();//因为在headerView内部不走onLayoutChildrenDone的方法所以这里需要手动运行
					checkSelected(preSelectedView, preSelectedPos);
					return headerViewRet;
				}
				else{
					headerView.clearSelectedView();
				}
			}
		}
		
		//动画未完成
		if(isFlipFinished() == false){
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				{
					//在滚动过程中按左右键只改变选中的顺序
					int nextSelectedPos = getSelectedItemPosition() - 1;
					setSelectedPositionInt(nextSelectedPos);
					checkSelectionChanged();
					amountToCenterScroll(FOCUS_UP, nextSelectedPos);
					checkSelected(preSelectedView, preSelectedPos);
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				{
					//在滚动过程中按左右键只改变选中的顺序
					int nextSelectedPos = getSelectedItemPosition() + 1;
					setSelectedPositionInt(nextSelectedPos);
					checkSelectionChanged();
					amountToCenterScroll(FOCUS_DOWN, nextSelectedPos);
					checkSelected(preSelectedView, preSelectedPos);
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if(DYNAMIC_ADD_CHILD_VIEW == false){
					break;
				}
				if(!lockKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT))
				{
					//在滚动中时不进入原来的addView逻辑，由动画在滚动的过程中动态添加，这个只计算需要滚动的距离跟focus框
					int currselectedPos = getSelectedItemPosition();
					int nextSelectedPosition;
					int headerCount = mHeaderViewInfos.size();
					if(headerCount > 0){
						if(currselectedPos >= headerCount){
							nextSelectedPosition = currselectedPos - getNumLines();
							if(nextSelectedPosition < headerCount){
								nextSelectedPosition = headerCount - 1;
							}
						}
						else{
							nextSelectedPosition = currselectedPos - 1;
							if(nextSelectedPosition < 0){
								nextSelectedPosition = INVALID_POSITION;
							}
						}
					}
					else{
						nextSelectedPosition = currselectedPos - getNumLines();
						if(nextSelectedPosition < 0){
							nextSelectedPosition = INVALID_POSITION;
						}
					}
					if(DEBUG){
						Log.i(TAG, "KEYCODE_DPAD_UP nextSelectedPosition="+nextSelectedPosition);
					}
					if (nextSelectedPosition != INVALID_POSITION) {
						setSelectedPositionInt(nextSelectedPosition);
						checkSelectionChanged();
						//设置选中的当前行的第一个View，因为在下次layoutChild的时候需要，
						//如果为空的话不进行设置，因为在添加新的View的时候会自动加入
						int columnEnd = getColumnEnd(nextSelectedPosition);
						View selectedView = getChildAt(columnEnd - getFirstVisiblePosition());
						if(selectedView != null){
							setReferenceViewInSelectedRow(selectedView);
						}
						//进入headerView的focus
						if(nextSelectedPosition < headerCount){
							View view = mHeaderViewInfos.get(nextSelectedPosition).view;
							//快速滚动的时候当选中headerView并且还未加入到列表里面的时候重新做一下布局，
							//因为滚动未完成时其childView还没有还原在原始的位置就离开的界面
							if(view.getParent() == null){
							    int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY );
						        int heightSpec = View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.EXACTLY );
						        view.measure(widthSpec, heightSpec);
						        view.layout(0, 0, view.getWidth(), view.getHeight());
							}
							if(view instanceof FocusRelativeLayout && view instanceof FlipGridViewHeaderOrFooterInterface){
								FocusRelativeLayout headerView = (FocusRelativeLayout)view;
								//如果当前选中的HeaderView还没有findFocus，就进行查找，目的是为了focus到离上次选中的item最近的位置
								if(headerView.isNeedFocusItem()){
									//将上次选中的item的坐标拉回到跟headerView下面的位置
									if(DEBUG){
										Log.i(TAG, "mFocusRectparams.focusRect() = "  + mPreFocusRect);
										Log.i(TAG, "mPreFocusRect = "  + mPreFocusRect);
									}
									
									int remainScrollDistance = getRemainScrollLeftDistance(nextSelectedPosition);
									mPreFocusRect.left += remainScrollDistance;
									mPreFocusRect.right += remainScrollDistance;
									//由目标焦点位置查找焦点 quanqing.hqq
									headerView.onFocusChanged(true, mOnKeyDirection, mPreFocusRect, null);
									
									if(DEBUG){
										Log.i(TAG, "remainScrollDistance offset, mPreFocusRect = "  + mPreFocusRect);
									}
								}
								headerView.reset();
							}
						}
						amountToCenterScroll(FOCUS_LEFT, nextSelectedPosition);
						checkSelected(preSelectedView, preSelectedPos);
						return true;
					}
				}
				return true;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if(DYNAMIC_ADD_CHILD_VIEW == false){
					break;
				}
				if(!lockKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT))
				{
					//在滚动中时不进入原来的addView逻辑，由动画在滚动的过程中动态添加，这个只计算需要滚动的距离跟focus框
					int nextSelectedPosition = getSelectedItemPosition() + getNumLines() < mItemCount ? getSelectedItemPosition() + getNumLines() : INVALID_POSITION;
					//条件1：当倒数第二排向下的时候其正对的下面没有item，但是最后一排其实还剩余几个，这种情况下选中最后一排的最后一个item
					boolean isLastMovePos = false;//是否为上述说的情况
					int selectedColumnStart = getColumnStart(getSelectedItemPosition());
					if(nextSelectedPosition == INVALID_POSITION && (selectedColumnStart + getNumLines()) < mItemCount){
						nextSelectedPosition = mItemCount -1;
						isLastMovePos = true;
					}
					if (nextSelectedPosition != INVALID_POSITION) {
						setSelectedPositionInt(nextSelectedPosition);
						checkSelectionChanged();
						//setFlipSelectedPosition(nextSelectedPosition);
						//设置选中的当前行的第一个View，因为在下次layoutChild的时候需要，
						//如果为空的话不进行设置，因为在添加新的View的时候会自动加入
						int columnEnd = getColumnEnd(nextSelectedPosition);
						View selectedView = getChildAt(columnEnd - getFirstVisiblePosition());
						if(selectedView != null){
							setReferenceViewInSelectedRow(selectedView);
						}
						amountToCenterScroll(FOCUS_RIGHT, nextSelectedPosition);
						//条件1：成立的时候处理focus框左右位置需要单独处理，取得存在列表里面的同一列位置取当其左右位置
						if(isLastMovePos){
							int existItemPos = getColumnStart(mFirstPosition);
							//如果当前选中的是headerView，那么选中adapterView的第一个。
							if(existItemPos < mHeaderViewInfos.size()){
								existItemPos = mHeaderViewInfos.size();
							}
							if(existItemPos < mFirstPosition){
								existItemPos += getNumLines();
							}
							int nextSelectedColumnStart = getColumnStart(nextSelectedPosition);
							int columnDelta = nextSelectedPosition - nextSelectedColumnStart;
							View existItem = getChildAt(existItemPos + columnDelta - mFirstPosition);
							if(existItem != null && existItem instanceof ItemListener){
								ItemListener item = (ItemListener)existItem;
								FocusRectParams rectParms = item.getFocusParams();
								if(rectParms != null){
									Rect rect = rectParms.focusRect();
									offsetDescendantRectToMyCoords(existItem, rect);
									offsetFocusRectTopAndBottom(rect.top, rect.bottom);
								}
							}
						}
						checkSelected(preSelectedView, preSelectedPos);
						return true;
					}
				}
				return true;
			default:
				break;
			}
		}
		else{
			//向下按键从headerView到adapterView，满足以下条件：
			//1.headerView无法再下向focusedView，2.从headerView到adapterView
			if(selectedPos < getHeaderViewsCount() && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
				int selectedIndex = selectedPos;
				View currSelectedView = getSelectedView();
				FocusRelativeLayout headerView = null;
				
				if(checkIsCanRight()){
					if(selectedIndex == (getHeaderViewsCount() - 1)){
						if(currSelectedView != null && currSelectedView instanceof FocusRelativeLayout){
							headerView = (FocusRelativeLayout)currSelectedView;
							View focusedView = focusSearch(headerView.getSelectedView(), FOCUS_RIGHT);
							if(focusedView != null){
								int nextSelectedIndex = -1;
								for(int i = 0; i < getChildCount(); i++){
									if(getChildAt(i).equals(focusedView)){
										nextSelectedIndex = i + getFirstVisiblePosition();
										break;
									}
								}
								if(nextSelectedIndex >= 0){
									mNeedResetParam = true;
									setAdapterSelection(nextSelectedIndex);
									checkSelected(preSelectedView, preSelectedPos);
									return true;
								}
							}
						}
					} else {
						int nextSelectedIndex = (selectedPos++);
						if(nextSelectedIndex >= 0 && nextSelectedIndex < getHeaderViewsCount()){
							mNeedResetParam = true;
							setAdapterSelection(nextSelectedIndex);
							checkSelected(preSelectedView, preSelectedPos);
							return true;
						}
					}
				}
				
			} else if(selectedPos <= getHeaderViewsCount() && keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
				
				//向上按键从adapterView到headerView，满足以下条件：
				//1.adapterView无法再向上focusedView，2.headerView不为空
				
				int nextSelectedIndex = (selectedPos--);
				if(nextSelectedIndex >= 0 && nextSelectedIndex < getHeaderViewsCount()){
					mNeedResetParam = true;
					setAdapterSelection(nextSelectedIndex);
					checkSelected(preSelectedView, preSelectedPos);
					return true;
				}
			}
		}
		
		mNeedResetParam = true;
		boolean ret = super.onKeyDown(keyCode, event);
		checkSelected(preSelectedView, preSelectedPos);
		return ret;
	}
	
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean hasFocused = hasFocus();
		if(!hasFocused){
			Log.i(TAG, "requestFocus for touch event to onKeyUp");
			requestFocus();
		}
		
		int selectedPos = getSelectedItemPosition();
		//判断选中的是否是headerView，如果是的话就进行深入
		if(selectedPos < getHeaderViewsCount()){
			//headerView
			View view = getSelectedView();
			if(view instanceof FocusRelativeLayout){
				FocusRelativeLayout headerView = (FocusRelativeLayout)view;
				boolean headerViewRet = headerView.onKeyUp(keyCode, event);
				if(DEBUG){
					Log.i(TAG, "onKeyUp headerViewRet="+headerViewRet);
				}
				return headerViewRet;
			}
		}		
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		if(mNeedAutoSearchFocused){
			super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
			if(gainFocus){
				mNeedAutoSearchFocused = false;
			}
		}
		else{
			if (getOnFocusChangeListener() != null) {
				getOnFocusChangeListener().onFocusChange(this, gainFocus);
			}
			if(gainFocus){
				setSelection(getSelectedItemPosition());
			}
		}
		//当该控件获得焦点的时候就需要设置焦点的位置
		if (gainFocus && getChildCount() > 0) {
			int selectedPos = getSelectedItemPosition();
			if(selectedPos < getHeaderViewsCount()){
				//headerView
				View view = getSelectedView();
				if(view instanceof FocusRelativeLayout){
					FocusRelativeLayout headerView = (FocusRelativeLayout)view;
					headerView.onFocusChanged(true, direction, previouslyFocusedRect, this);
				}
			}
			mNeedResetParam = true;
			layoutResetParam();
			performSelect(true);
		}
		else{
			performSelect(false);
		}
		mIsAnimate = checkAnimate(direction);
	}


	private boolean checkAnimate(int direction){
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
	
	/**
	 * 保持在居中滚动，计算出滚动的距离跟focus框的位置
	 * @param direction
	 * @param nextSelectedPosition
	 */
	protected int amountToCenterScroll(int direction, int nextSelectedPosition) {
		int horizontalSpacing = getHorizontalSpacing();
//		int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
		int center = getWidth() / 2;
		int amountToScroll = 0;
		int distanceLeft = getFlipRowFirstItemLeftMoveDistance(nextSelectedPosition);
		switch (direction) {
			case FOCUS_RIGHT:
			{
				View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
				int nextSelectedCenter = 0;
				boolean reset = false;
				int lastRight = getChildAt(getChildCount() - 1).getRight();
				if (nextSelctedView == null) {
					//还未添加的时候通过计算来取得当前选中view的中心位置
					nextSelctedView = getChildAt(getChildCount() - 1);
					nextSelectedCenter = nextSelctedView.getRight() - nextSelctedView.getWidth() / 2;
					int oldColumnStart = getColumnStart(getLastVisiblePosition());
					int columnStart = getColumnStart(nextSelectedPosition);
					int delta = (columnStart - oldColumnStart) / getNumLines();
					nextSelectedCenter += (nextSelctedView.getWidth() + horizontalSpacing) * delta;
					reset = false;
				} else {
					nextSelectedCenter = nextSelctedView.getRight() - nextSelctedView.getWidth() / 2;
					reset = true;
				}
	
				int finalNextSelectedCenter = nextSelectedCenter + distanceLeft;
	
				if (finalNextSelectedCenter > center) {
					amountToScroll = finalNextSelectedCenter - center;
					int maxDiff = getRightLeftDistance(getLastVisiblePosition());
					//lastBottom到达底点的距离
					maxDiff = lastRight + maxDiff + mListPadding.right - getWidth();
					if(maxDiff < 0){
						maxDiff = 0;
					}
					int leftDistance = getFlipRowFirstItemLeftMoveDistance(getLastVisiblePosition());
					maxDiff += leftDistance;
					if (amountToScroll > maxDiff) {
						amountToScroll = maxDiff;
					}
	
					if (reset) {
						resetParam(nextSelctedView, 0);
                        offsetFocusRectLeftAndRight(distanceLeft, distanceLeft);
					}
	
					if (amountToScroll > 0) {
						if (reset) {
							offsetFocusRectLeftAndRight(-amountToScroll, -amountToScroll);
						}else{
							//因为down是要求向下移一个宫格再加上滚动的距离
							offsetFocusRectLeftAndRight(((nextSelctedView.getWidth() + horizontalSpacing) - amountToScroll),
									((nextSelctedView.getWidth() + horizontalSpacing) - amountToScroll));
						}
	
						if (DEBUG) {
							Log.i(TAG, "amountToCenterScroll down: focus rect = " + mFocusRectparams.focusRect() + 
									", distanceLeft = " + distanceLeft + ", nextSelectedPosition = " + nextSelectedPosition +
									" amountToScroll="+amountToScroll+" nextSelectedCenter="+nextSelectedCenter);
						}
						startRealScroll(-amountToScroll);
						mIsAnimate = true;
					} else {
						if (!reset) {
							offsetFocusRectLeftAndRight(nextSelctedView.getWidth() + horizontalSpacing, 
									nextSelctedView.getWidth() + horizontalSpacing);
						}
						mIsAnimate = true;
					}
				} else {
					resetParam(getSelectedView(), 0);
					mIsAnimate = true;
				}
	
				return amountToScroll;
			}
			case FOCUS_LEFT:
			{
				View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
				int nextSelectedCenter = 0;
				View firstView = getChildAt(0);
				int firstLeft = firstView.getLeft();
				if(firstView instanceof GridViewHeaderViewExpandDistance){
					firstLeft += ((GridViewHeaderViewExpandDistance)firstView).getLeftExpandDistance();
				}
				boolean reset = false;
				boolean notResetParam = false;
				if (nextSelctedView == null) {
					//还未添加的时候通过计算来取得当前选中view的中心位置，取最上一个item的位置来计算
					nextSelctedView = getChildAt(0);
					int oldColumnStart = getColumnStart(getFirstVisiblePosition());
					int columnStart = getColumnStart(nextSelectedPosition);
					int headerCount = mHeaderViewInfos.size();
					int delta;
					if(headerCount > 0){
						if(columnStart < headerCount){
							delta = (oldColumnStart - headerCount) / getNumLines();
							//先计算除item以外的每行的高度
							nextSelectedCenter = nextSelctedView.getLeft() -(nextSelctedView.getWidth() + horizontalSpacing) * delta;
							//再加上headerView的中心点的高度
							for(int i = columnStart; i < headerCount; i++){
								View headerView = mHeaderViewInfos.get(i).view;
								int headerViewWidth = headerView.getWidth();
								if(headerView instanceof GridViewHeaderViewExpandDistance){
									headerViewWidth -= ((GridViewHeaderViewExpandDistance)headerView).getLeftExpandDistance();
									headerViewWidth -= ((GridViewHeaderViewExpandDistance)headerView).getRightExpandDistance();
								}
								nextSelectedCenter -= horizontalSpacing + headerViewWidth / 2;
								if(DEBUG){
									Log.i(TAG, "amountToCenterScroll up rowStart="+columnStart+" oldRowStart="+oldColumnStart+" delta="+delta+
											" headerViewHeight="+headerViewWidth+" nextSelectedCenter="+nextSelectedCenter);
								}
							}
							//设置headerView的focus位置
							View headerView = mHeaderViewInfos.get(0).view;

							//取得item相对于整体headerView的位置
							if(headerView != null && headerView instanceof ItemListener){
								ItemListener item = (ItemListener)headerView;
								if(mFocusRectparams == null){
									mFocusRectparams = new FocusRectParams();
								}
								mFocusRectparams.set(item.getFocusParams());
							}
							
							int topOffset = getHeaderViewTop(0);
							int leftOffset = mListPadding.left;
							//去掉填充距离
							if(headerView instanceof GridViewHeaderViewExpandDistance){
								leftOffset -= ((GridViewHeaderViewExpandDistance)headerView).getLeftExpandDistance();
							}
							//考虑滚动的过程中，子view跟整体的滚动距离偏移值
							int secondIndex = getHeaderViewSecondIndex(headerView);
							if(nextSelectedPosition >= getFirstVisiblePosition() && nextSelectedPosition <= getLastVisiblePosition()){
								int childLeftDistance = getFlipItemLeftMoveDistance(nextSelectedPosition, secondIndex);
								leftOffset += (distanceLeft - childLeftDistance);
							}
                            offsetFocusRect(leftOffset, leftOffset, topOffset, topOffset);
							notResetParam = true;

						}
						else{
							//rowStart >= headerCount
							delta = (oldColumnStart - columnStart) / getNumLines();
							nextSelectedCenter = nextSelctedView.getLeft() + nextSelctedView.getWidth() / 2;
							nextSelectedCenter -= (nextSelctedView.getWidth() + horizontalSpacing) * delta;
						}
					}
					else{
						//headerCount <= 0
						delta = (oldColumnStart - columnStart) / getNumLines();
						nextSelectedCenter = nextSelctedView.getLeft() + nextSelctedView.getWidth() / 2;
						nextSelectedCenter -= (nextSelctedView.getWidth() + horizontalSpacing) * delta;				
					}
					reset = false;
				} else {
					//nextSelctedView != null
					if(nextSelctedView instanceof GridViewHeaderViewExpandDistance){
						int leftDistance = ((GridViewHeaderViewExpandDistance)nextSelctedView).getLeftExpandDistance();
						int rightDistance = ((GridViewHeaderViewExpandDistance)nextSelctedView).getRightExpandDistance();
						nextSelectedCenter = nextSelctedView.getLeft() + leftDistance + 
								(nextSelctedView.getWidth() - leftDistance - rightDistance) / 2;
					}
					else{
						nextSelectedCenter = nextSelctedView.getLeft() + nextSelctedView.getWidth() / 2;
					}
					reset = true;
				}
	
				int finalNextSelectedCenter = nextSelectedCenter + distanceLeft;
	
				if (finalNextSelectedCenter < center) {
					amountToScroll = center - finalNextSelectedCenter;
					int maxDiff = getLeftLeftDistance(getFirstVisiblePosition());
					//firstTop到达顶点的距离
					maxDiff = mListPadding.left - (firstLeft - maxDiff);
					if(maxDiff < 0){
						maxDiff = 0;
					}
					int left = getFlipRowFirstItemLeftMoveDistance(getFirstVisiblePosition());
					maxDiff -= left;
					if(maxDiff < 0){
						maxDiff = 0;
					}
					if (amountToScroll > maxDiff) {
						amountToScroll = maxDiff;
					}
	
					if (reset) {
						//使用headerView里面选中view进行剩余位置的计算，但是滚动的计算应该是针对整体的headerView
						int headerCount = mHeaderViewInfos.size();
						if(headerCount > 0){
							if(nextSelectedPosition < headerCount){
								int secondIndex = getHeaderViewSecondIndex(nextSelctedView);
								if(secondIndex >= 0){
									int childLeftDistance = getFlipItemLeftMoveDistance(nextSelectedPosition, secondIndex);
									resetParam(nextSelctedView, childLeftDistance);
								}
							}
							else{
								resetParam(nextSelctedView, 0);
								offsetFocusRectLeftAndRight(distanceLeft, distanceLeft);
							}
						}
						else{
							resetParam(nextSelctedView, 0);
							offsetFocusRectLeftAndRight(distanceLeft, distanceLeft);
						}
					}
	
					if (amountToScroll > 0) {
						if(notResetParam == false){
							if (reset) {
								offsetFocusRectLeftAndRight(amountToScroll, amountToScroll);
							}else{
								offsetFocusRectLeftAndRight(-((nextSelctedView.getWidth() + horizontalSpacing) - amountToScroll),
										-((nextSelctedView.getWidth() + horizontalSpacing) - amountToScroll));
							}
						}
						if (DEBUG) {
							Log.i(TAG, "amountToCenterScroll up: focus rect = " + mFocusRectparams.focusRect() + 
									", distanceLeft = " + distanceLeft + ", nextSelectedPosition = " + nextSelectedPosition +
									" amountToScroll="+amountToScroll+" nextSelectedCenter="+nextSelectedCenter+
									" finalNextSelectedCenter="+finalNextSelectedCenter+" center="+center);
						}
						startRealScroll(amountToScroll);
						mIsAnimate = true;
					} else {
						if (!reset && notResetParam == false) {
							offsetFocusRectLeftAndRight(-(nextSelctedView.getWidth() + horizontalSpacing), 
									-(nextSelctedView.getWidth() + horizontalSpacing));
						}
						mIsAnimate = true;
					}
				} else {
					resetParam(getSelectedView(), 0);
					mIsAnimate = true;
				}
				//保存位置
				mPreFocusRect.set(mFocusRectparams.focusRect());
				return amountToScroll;
			}
			case FOCUS_UP:
			case FOCUS_DOWN:
				{
					//取得有效的参考View的序号
					int lastVisiblePos = getLastVisiblePosition();
					int firstVisiblePos = getFirstVisiblePosition();
					//向下
					if(nextSelectedPosition > lastVisiblePos){
						int visibleColumnStart = getColumnStart(lastVisiblePos);
						//如果参考不足一行,最取上一行的
						if((lastVisiblePos - visibleColumnStart) < (getNumLines() - 1)){
							visibleColumnStart = getColumnStart(lastVisiblePos - getNumLines());
						}
						int selectedColumnStart = getColumnStart(nextSelectedPosition);
						int rowDelta = nextSelectedPosition - selectedColumnStart;
						View visibleView = getChildAt(visibleColumnStart + rowDelta - firstVisiblePos);
						int delta = (selectedColumnStart - visibleColumnStart) / getNumLines();
						int offset = (visibleView.getWidth() + horizontalSpacing) * delta;
						offset += getFlipItemLeftMoveDistance(visibleColumnStart + rowDelta, 0);
						if(DEBUG){
							Log.i(TAG, "amountToCenterScroll left right down visibleRowStart="+visibleColumnStart+
									" offset="+offset+" selectedRowStart="+selectedColumnStart);
						}
						resetParam(visibleView, offset);
					}
					//向上
					else if(nextSelectedPosition < firstVisiblePos){
						int visibleColumnStart = getColumnStart(firstVisiblePos);
						//如果参考不足一行,最取下一行的
						if(visibleColumnStart != firstVisiblePos){
							visibleColumnStart = getColumnStart(firstVisiblePos + getNumLines());
						}
						int selectedColumnStart = getColumnStart(nextSelectedPosition);
						int rowDelta = nextSelectedPosition - selectedColumnStart;
						View visibleView = getChildAt(visibleColumnStart + rowDelta - firstVisiblePos);
						int delta = (visibleColumnStart - selectedColumnStart) / getNumLines();
						int offset = -((visibleView.getWidth() + horizontalSpacing) * delta);
						offset += getFlipItemLeftMoveDistance(visibleColumnStart + rowDelta, 0);
						if(DEBUG){
							Log.i(TAG, "amountToCenterScroll left right up visibleRowStart="+visibleColumnStart+
									" offset="+offset+" selectedRowStart="+selectedColumnStart);
						}
						resetParam(visibleView, offset);
					}
					else{
						int offset = getFlipItemLeftMoveDistance(nextSelectedPosition, 0);
						resetParam(getSelectedView(), offset);
					}
				}				
		}
		return 0;
	}
	
	/**
	 * 计算出下边界的距离（选中的item居中）
	 */
	@Override
	protected void adjustForRightFadingEdge(View childInSelectedColumn, int leftSelectionPixel, int rightSelectionPixel) {
		// Some of the newly selected item extends below the bottom of the
		// list
		//int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
		//不需要考虑padding，因为是针对整个GridView的长度
		int left = childInSelectedColumn.getLeft();
		if(childInSelectedColumn instanceof GridViewHeaderViewExpandDistance){
			left += ((GridViewHeaderViewExpandDistance)childInSelectedColumn).getLeftExpandDistance();
		}
		
		int right = childInSelectedColumn.getRight();
		if(childInSelectedColumn instanceof GridViewHeaderViewExpandDistance){
			right -= ((GridViewHeaderViewExpandDistance)childInSelectedColumn).getRightExpandDistance();
		}
		
		//int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
		//不需要考虑padding，因为是针对整个GridView的长度
		int tempLeftSelectionPixel;
		int tempRightSelectionPixel;
		if(mCenterFocus){
			int center = getWidth() / 2;
			int childWidth = childInSelectedColumn.getWidth();
			if(childInSelectedColumn instanceof GridViewHeaderViewExpandDistance){
				childWidth -= ((GridViewHeaderViewExpandDistance)childInSelectedColumn).getLeftExpandDistance();
				childWidth -= ((GridViewHeaderViewExpandDistance)childInSelectedColumn).getRightExpandDistance();
			}
			tempLeftSelectionPixel = center - childWidth / 2;
			tempRightSelectionPixel = center + childWidth / 2;
		}
		else{
			tempLeftSelectionPixel = leftSelectionPixel;
			tempRightSelectionPixel = rightSelectionPixel;
		}
		if (right > tempRightSelectionPixel) {

			// Find space available above the selection into which we can
			// scroll upwards
			int spaceLeft = left - tempLeftSelectionPixel;

			// Find space required to bring the bottom of the selected item
			// fully into view
			int spaceRight = right - tempRightSelectionPixel;
			//需要做上下均衡取最小值，因为会有小数点误差
			int offset = Math.min(spaceLeft, spaceRight);
			if(mCenterFocus){
				int maxDiff = getRightLeftDistance(getSelectedItemPosition());
				//bottom到达底点的距离
				maxDiff = right + maxDiff + mListPadding.right - getWidth();
				if(maxDiff < 0){
					maxDiff = 0;
				}
				if(offset > maxDiff){
					offset = maxDiff;
				}
			}
			// Now offset the selected item to get it into view
			offsetChildrenLeftAndRight(-offset);
		}
	}


	/**
	 * 计算移动到上边界的距离（选中的item居中）
	 */
	@Override
	protected void adjustForLeftFadingEdge(View childInSelectedColumn, int leftSelectionPixel, int rightSelectionPixel) {
		// Some of the newly selected item extends above the top of the list
		int left = childInSelectedColumn.getLeft();
		if(childInSelectedColumn instanceof GridViewHeaderViewExpandDistance){
			left += ((GridViewHeaderViewExpandDistance)childInSelectedColumn).getLeftExpandDistance();
		}
		
		int right = childInSelectedColumn.getRight();
		if(childInSelectedColumn instanceof GridViewHeaderViewExpandDistance){
			right -= ((GridViewHeaderViewExpandDistance)childInSelectedColumn).getRightExpandDistance();
		}
		
		//int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
		//不需要考虑padding，因为是针对整个GridView的长度
		int tempLeftSelectionPixel;
		int tempRightSelectionPixel;
		if(mCenterFocus){
			int center = getWidth() / 2;
			int childWidth = childInSelectedColumn.getWidth();
			if(childInSelectedColumn instanceof GridViewHeaderViewExpandDistance){
				childWidth -= ((GridViewHeaderViewExpandDistance)childInSelectedColumn).getLeftExpandDistance();
				childWidth -= ((GridViewHeaderViewExpandDistance)childInSelectedColumn).getRightExpandDistance();
			}
			tempLeftSelectionPixel = center - childWidth / 2;
			tempRightSelectionPixel = center + childWidth / 2;
		}
		else{
			tempLeftSelectionPixel = leftSelectionPixel;
			tempRightSelectionPixel = rightSelectionPixel;
		}
		if (left < tempLeftSelectionPixel) {
			// Find space required to bring the top of the selected item
			// fully into view
			int spaceLeft = tempLeftSelectionPixel - left;

			// Find space available below the selection into which we can
			// scroll downwards
			int spaceRight = tempRightSelectionPixel - right;
			//需要做上下均衡取最小值，因为会有小数点误差
			int offset = Math.min(spaceLeft, spaceRight);
			if(mCenterFocus){
				int maxDiff = getLeftLeftDistance(getSelectedItemPosition());
				//top到达顶点的距离
				maxDiff = mListPadding.left - (left - maxDiff);
				if(maxDiff < 0){
					maxDiff = 0;
				}
				if (offset > maxDiff) {
					offset = maxDiff;
				}
			}
			// Now offset the selected item to get it into view
			offsetChildrenLeftAndRight(offset);
		}
	}
	
	
	@Override
	protected void layoutChildren() {
		if(DYNAMIC_ADD_CHILD_VIEW && isFlipFinished() == false){
			Log.i(TAG, "layoutChildren flip is running can not layout");
			return;
		}
		super.layoutChildren();
		mClipFocusRect.set(0, 0, getWidth(), getHeight());
	}
	
	
	@Override
	protected void onLayoutChildrenDone(){
		//如果为首次布局完成启动入场动画
		boolean isFirst = false;//使用临时变量为防止在onLayout的时候layoutChildren递归调用导致栈溢出
		if(mIsFirstLayout == true){
			mIsFirstLayout = false;
			isFirst = true;
		}
		if(mOnFocusFlipGridViewListener != null){
			mOnFocusFlipGridViewListener.onLayoutDone(isFirst);
		}

		resetFocusParam();
	}
	
	@Override
	protected void offsetChildrenLeftAndRight(int offset) {
		super.offsetChildrenLeftAndRight(offset);
	}

	
	/**
	 * 弹性间距的运动过程的回调
	 */
	@Override
	protected void onFlipItemRunnableRunning(float moveRatio, View itemView, int index){
		if(mAnimAlpha){
			if(mFirstAnimDone == false && itemView != null){
				mAnimAlphaValue = (int)(moveRatio * 255);
				setAlpha(moveRatio);
			}
		}
		super.onFlipItemRunnableRunning(moveRatio, itemView, index);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if(mAnimAlpha){
			if(mAlphaRectF == null){
				mAlphaRectF = new RectF(0, 0, getWidth(), getHeight());
			}
//			canvas.saveLayerAlpha(mAlphaRectF, mAnimAlphaValue,Canvas.ALL_SAVE_FLAG);
			super.dispatchDraw(canvas);
//			canvas.restore();
		}
		else{
			super.dispatchDraw(canvas);
		}
	}
	/**
	 * 弹性间距的动画完成回调
	 */
	@Override
	protected void onFlipItemRunnableFinished(){
		if(mFirstAnimDone == false){
			//入场动画完成后使可聚焦
			setFocusable(true);
			mFirstAnimDone = true;
		}
		super.onFlipItemRunnableFinished();
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
		if (hasFocus()) {
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

	
	@Override
	public FocusRectParams getFocusParams() {
		View v = getSelectedView();
		if (v != null) {
			if (mFocusRectparams == null || isScrolling()) {
				resetFocusParam();
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
		return getSelectedView() != null;
	}

	@Override
	public boolean isAnimate() {
		return mIsAnimate;
	}
	

	@Override
	public ItemListener getItem() {
		View view = getSelectedView();
		if(view instanceof FocusRelativeLayout){
			FocusRelativeLayout headerView = (FocusRelativeLayout)view;
			return headerView.getItem();
		}
		else{
			View v = getSelectedView();
			if (v == null) {
				Log.e(TAG, "getItem: getSelectedView is null! this:" + this.toString());
			}
			return (ItemListener) v;
		}
	}

	@Override
	public boolean isScrolling() {
		if (this.DEBUG){
			   Log.d(TAG, "isFliping =" + isFliping());
			}
			return isFliping();
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		int selectedPos = getSelectedItemPosition();
		if(selectedPos < getHeaderViewsCount()){
			//headerView
			View view = getSelectedView();
			if(view instanceof FocusRelativeLayout){
				//动画过程中，长按按钮的事件不宜传到headerView, 否则会使headerView中的nextFocus在执行onKeyDown时控件不一致
				//add by quanqing.hqq
				if(isFlipFinished() == false){
					return false;
				} else {
					FocusRelativeLayout headerView = (FocusRelativeLayout)view;
					boolean header = headerView.preOnKeyDown(keyCode, event);
					if(DEBUG){
						Log.i(TAG, "preOnKeyDown header="+header);
					}
					if(header == true){
						return true;
					}
				}
				//add end
			}
			
		}

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_MOVE_HOME:
			int headerCount = mHeaderViewInfos.size();
			if(headerCount > 0){
				//向上滚动的时候如果选中是headerView并且向上不能focus的时候返回false
				if(selectedPos > 0){
					int columnStart = getColumnStart(selectedPos);
					if(columnStart == headerCount){
						View lastHeaderView = mHeaderViewInfos.get(headerCount - 1).view;
						if(!lastHeaderView.isFocusable()){
							return false;
						}

					}
					return true;
				}
				else{
					//此行到这里说明向上方法上已经没有item可以选择了
					if(mOnFocusFlipGridViewListener != null){
						mOnFocusFlipGridViewListener.onReachGridViewLeft();
					}
					return false;
				}
			}
			else{
				//第一排
				if(getSelectedItemPosition() < getNumLines()){
					//此行到这里说明向上方法上已经没有item可以选择了
					if(mOnFocusFlipGridViewListener != null){
						mOnFocusFlipGridViewListener.onReachGridViewLeft();
					}
					return false;
				}
				else{
					return true;
				}
			}
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_MOVE_END:
			boolean isCan = checkIsCanRight();
			if(!isCan){
				return false;
			}
			
			return getSelectedItemPosition() < mItemCount - 1 ? true : false;
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_PAGE_UP:
			//当选中的item为一行的头部的时候返回false，为了避免选中的item不断的放大缩小的问题
			{
				int selected = getSelectedItemPosition();
				if(selected < getHeaderViewsCount()){
					return false;
				}
				else{
					int adapterIndex = selected - getHeaderViewsCount();
					if(DEBUG){
						Log.i(TAG, "preOnKeyDown left selected="+selected+" headerCount="+getHeaderViewsCount()+
								" columnNum="+getNumLines());
					}
					return (adapterIndex % getNumLines()) == 0 ? false : true;
				}
			}
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_PAGE_DOWN:
			//当选中的item为一行的尾部的时候返回false，为了避免选中的item不断的放大缩小的问题
			{
				int selected = getSelectedItemPosition();
				if(selected < getHeaderViewsCount()){
					return false;
				}
				else{
				    //最后一个
					if(selected >= mItemCount - 1){
						return false;
					}
					else{
						int adapterIndex = selected - getHeaderViewsCount() + 1;
						if(DEBUG){
							Log.i(TAG, "preOnKeyDown right selected="+selected+" headerCount="+getHeaderViewsCount()+
									" columnNum="+getNumLines());
						}
						return (adapterIndex % getNumLines()) == 0 ? false : true;
					}
				}
			}
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
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
	public Rect getManualPadding() {
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
	
	
	/**
	 * 设置focusFlipGridView的监听器
	 * @param listener
	 */
	public void setOnFocusFlipGridViewListener(OnFocusFlipGridViewListener listener){
		mOnFocusFlipGridViewListener = listener;
	}
	
	
	/**
	 * 停止出场动画
	 */
	public void stopOutAnimation(){
		Log.i(TAG, "stopOutAnimation");
		mOutAnimationRunnable.stop();
	}
	
	
	/**
	 * 开始出场动画
	 * 注：可能需要延时启动，因为由layout需要一定的时间，由业务层来做处理
	 */
	public void startOutAnimation(){
		mOutAnimationRunnable.start();
	}
	
	
	/**
	 * 开始入场动画
	 */
	public void startInAnimation(){
		if(mFirstAnimDone){
			mFirstAnimDone = false;
			//在动画的过程中不让聚焦
			setFocusable(false);
			int count = getChildCount();
			int delta = getWidth() / 2;
			if(mAnimAlpha){
				setAlpha(0);
				mAnimAlphaValue = 0;
			}
			for(int i = 0; i < count; i++){
				View childView = getChildAt(i);
				childView.offsetLeftAndRight(delta);
			}
			startFlip(-delta);
		}
	}
	
	
	/**
	 * 初始化
	 */
	private void init(){
		mOutAnimationRunnable = new OutAnimationRunnable();
	}
	
	//只修改了方法名
	private int getHeaderViewTop(int index){
		int headerCount = mHeaderViewInfos.size();
		if(index < headerCount){
			View headerView = mHeaderViewInfos.get(index).view;
			int childTop = mListPadding.top;
			int height = getHeight() - mListPadding.top - mListPadding.bottom;
			final int absoluteGravity = Gravity.CENTER_VERTICAL;// Gravity.getAbsoluteGravity(mGravity,
												// layoutDirection);
			switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
				case Gravity.TOP:
					break;
				case Gravity.CENTER_VERTICAL:
					childTop += ((height - headerView.getHeight()) / 2);
					break;
				case Gravity.BOTTOM:
					childTop += height - headerView.getHeight();
					break;
				default:
					break;
			}
			return childTop;
		}
		return 0;
	}
	/**
	 * 取得列最后一个index
	 * @param position
	 * @return
	 */
	private int getColumnEnd(int position){
		int columnEnd = position;
		int headerCount = getHeaderViewsCount();
		if (position >= headerCount) {
			if (position < mItemCount - getFooterViewsCount()) {
				int newPosition = position - headerCount;
				int left = getNumLines() - ((newPosition % getNumLines()) + 1);
				columnEnd = newPosition + left + headerCount;
				if(columnEnd >= mItemCount){
					columnEnd = mItemCount - 1;
				}
			} else {
				columnEnd = position;
			}
		}

		return columnEnd;
	}
	
	
	/**
	 * 重新检查是否选中,非系统的onSelectedListener（应用到onKeyDown处理后）
	 * @param preSelectedView
	 * @param preSelectedPos
	 */
	private void checkSelected(View preSelectedView, int preSelectedPos) {
		if(DEBUG){
			Log.i(TAG, "checkSelected prePos="+preSelectedPos);
		}
		
		//视图状态的变化与是否有监听器无关，状态改变后，如果有监听器则需要通知。  quanqing.hqq
		View currSelectedView = getSelectedView();
		int currSelectedPos = getSelectedItemPosition();
		if(preSelectedPos != currSelectedPos){
			if(preSelectedPos >= 0 && preSelectedView != null){
				onItemSelectedChanged(preSelectedView, preSelectedPos, false);
			}
			onItemSelectedChanged(currSelectedView, currSelectedPos, true);
		}
	}
	
	
	/**
	 * 设置选中的方法
	 * @param select
	 */
	private void performSelect(boolean select){
		//视图状态的变化与是否有监听器无关，状态改变后，如果有监听器则需要通知。   quanqing.hqq
		View selectedView = getSelectedView();
		onItemSelectedChanged(selectedView, getSelectedItemPosition(), select);
	}
	
	
	protected void resetFocusParam() {
		mNeedResetParam = true;
		layoutResetParam();
	}
	
	/**
	 * 因为布局引起的重新设置focus参数，需要mNeedResetParam支持
	 */
	private void layoutResetParam() {
		if(mNeedResetParam == false){
			return;
		}
		mNeedResetParam = false;
		int scrollOffset = mScrollOffset;
		int selectedPos = getSelectedItemPosition();
		if(selectedPos < getHeaderViewsCount()){
			//headerView
			View view = getSelectedView();
			if(view instanceof FocusRelativeLayout && view instanceof FlipGridViewHeaderOrFooterInterface){
				FocusRelativeLayout headerView = (FocusRelativeLayout)view;
				//如果当前选中的HeaderView还没有findFocus，就进行查找，目的是为了focus到离上次选中的item最近的位置
				if(headerView.isNeedFocusItem()){
					headerView.onFocusChanged(true, mOnKeyDirection, mPreFocusRect, this);
				}
				FlipGridViewHeaderOrFooterInterface headerInt = (FlipGridViewHeaderOrFooterInterface)view;
				int validChildCount = headerInt.getHorCount() * headerInt.getVerticalCount();
				int secondIndex = -1;
				for(int i = 0; i < validChildCount; i ++){
					View childView = headerInt.getView(i);
					if(childView != null && childView.equals(headerView.getSelectedView())){
						secondIndex = i;
						break;
					}
				}
				if(secondIndex >= 0){
					//如果在滚动中焦点的位置应该在滚动目标位置 
					int leftMove = getFlipItemLeftMoveDistance(selectedPos, secondIndex);
					if(DEBUG){
						Log.i(TAG, "reset header index="+selectedPos+" secondIndex="+secondIndex+
								" scrollOffset="+scrollOffset+" leftMove="+leftMove);
					}
					if(scrollOffset == 0){
						scrollOffset = leftMove;
					}
					headerView.reset();
				}
			}
		}
		else{
			//如果在滚动中焦点的位置应该在滚动目标位置
			int leftMove = getFlipItemLeftMoveDistance(selectedPos, 0);
			if(DEBUG){
				Log.i(TAG, "reset header index="+selectedPos+" scrollOffset="+scrollOffset+" leftMove="+leftMove);
			}
			if(scrollOffset == 0){
				scrollOffset = leftMove;
			}
		}
		resetParam(getSelectedView(), scrollOffset);
		// offsetDescendantRectToMyCoords(getSelectedView(),
		// mFocusRectparams.focusRect());
	}


	/**
	 * 重新设置focus框相关信息
	 * @param view
	 * @param offset
	 */
	private void resetParam(View view, int offset){
		if(DEBUG){
			Log.i(TAG, "View="+view+" offset="+offset+" position="+getSelectedItemPosition());
		}
		if(view != null && view instanceof ItemListener){
			ItemListener item = (ItemListener)view;
			if(mFocusRectparams == null){
				mFocusRectparams = new FocusRectParams();
			}
			mFocusRectparams.set(item.getFocusParams());

		}
		else if(view != null && view instanceof FocusListener){
			FocusListener item = (FocusListener)view;
			if(mFocusRectparams == null){
				mFocusRectparams = new FocusRectParams();
			}
			mFocusRectparams.set(item.getFocusParams());
		}
		else{
			Log.w(TAG, "resetParam error view="+view+" mItemCount="+mItemCount+" mFirstIndex="+mFirstPosition+" mSelectedIndex="+mSelectedPosition);
			return;
			//mFocusRectparams = null;
		}
		if(mFocusRectparams != null){
			Rect rect = mFocusRectparams.focusRect();
			if(rect != null){
                offsetFocusRectLeftAndRight(offset, offset);
			}
			offsetDescendantRectToMyCoords(view, mFocusRectparams.focusRect());
			mPreFocusRect.set(mFocusRectparams.focusRect());
		}
	}
	
	
	/**
	 * 计算出到最顶上的剩余距离 只修改了方法名
	 * @return
	 */
	private int getLeftLeftDistance(int itemIndex){
		View itemView = getChildAt(itemIndex - getFirstVisiblePosition());
		if(itemView == null){
			return Integer.MAX_VALUE;
		}
		int rightDistance = 0;
		int numLines = getNumLines();
		int headerCount = mHeaderViewInfos.size();
		int footerCount = mFooterViewInfos.size();
		if(itemIndex < headerCount){
			//header
			for(int i = itemIndex - 1; i >= 0; i--){
				View headerView = mHeaderViewInfos.get(i).view;
				rightDistance += headerView.getWidth() + getHorizontalSpacing();
				if(headerView instanceof GridViewHeaderViewExpandDistance){
					rightDistance -= ((GridViewHeaderViewExpandDistance)headerView).getLeftExpandDistance();
					rightDistance -= ((GridViewHeaderViewExpandDistance)headerView).getRightExpandDistance();
				}
			}
		}
		else if(itemIndex >= headerCount && itemIndex < (mItemCount - footerCount)){
			//adatper
			int preRowIndex = itemIndex - ((itemIndex - mHeaderViewInfos.size()) % numLines) - 1;
			int firstAdapterIndex = mHeaderViewInfos.size();
			if(preRowIndex >= firstAdapterIndex){
				int rightColumnCount = (preRowIndex - firstAdapterIndex + 1) / numLines;
				int rowLeft = (preRowIndex - firstAdapterIndex + 1) % numLines;
				if(rowLeft > 0){
					rightColumnCount ++;
				}
				if(rightColumnCount > 0){
					rightDistance += (itemView.getWidth() + getHorizontalSpacing()) * rightColumnCount;
				}
			}
			for(int i = headerCount -1 ; i >= 0; i--){
				View headerView = mHeaderViewInfos.get(i).view;
				rightDistance += headerView.getWidth() + getHorizontalSpacing();
				if(headerView instanceof GridViewHeaderViewExpandDistance){
					rightDistance -= ((GridViewHeaderViewExpandDistance)headerView).getLeftExpandDistance();
					rightDistance -= ((GridViewHeaderViewExpandDistance)headerView).getRightExpandDistance();
				}
			}
		}
		else{
			//footer
			int footerStart = itemIndex - (mItemCount - footerCount);
			if(footerStart > 0){
				for(int i = footerStart - 1; i >= 0; i--){
					View footerView = mFooterViewInfos.get(i).view;
					rightDistance += footerView.getWidth() + getHorizontalSpacing();
					if(footerView instanceof GridViewHeaderViewExpandDistance){
						rightDistance -= ((GridViewHeaderViewExpandDistance)footerView).getLeftExpandDistance();
						rightDistance -= ((GridViewHeaderViewExpandDistance)footerView).getRightExpandDistance();
					}
				}
			}
			
			int preColumnIndex = mItemCount - footerCount - 1;
			int firstAdapterIndex = headerCount;
			if(preColumnIndex >= firstAdapterIndex){
				int rightColumnCount = (preColumnIndex - firstAdapterIndex + 1) / numLines;
				int rowLeft = (preColumnIndex - firstAdapterIndex + 1) % numLines;
				if(rowLeft > 0){
					rightColumnCount ++;
				}
				if(rightColumnCount > 0){
					View adatperView = getChildAt(preColumnIndex - mFirstPosition);
					if(adatperView != null){
						rightDistance += (adatperView.getWidth() + getHorizontalSpacing()) * rightColumnCount;
					}
					else{
						return Integer.MAX_VALUE;
					}
				}
			}
			for(int i = headerCount - 1; i >= 0; i--){
				View headerView = mHeaderViewInfos.get(i).view;
				rightDistance += headerView.getWidth() + getHorizontalSpacing();
				if(headerView instanceof GridViewHeaderViewExpandDistance){
					rightDistance -= ((GridViewHeaderViewExpandDistance)headerView).getLeftExpandDistance();
					rightDistance -= ((GridViewHeaderViewExpandDistance)headerView).getRightExpandDistance();
				}
			}
		}
		return rightDistance;
	}
	
	
	
	
	/**
	 * 计算出到最底下的剩余距离 只修改了方法名
	 * @param itemIndex
	 * @return
	 */
	private int getRightLeftDistance(int itemIndex){
		View itemView = getChildAt(itemIndex - getFirstVisiblePosition());
		if(itemView == null){
			return Integer.MAX_VALUE;
		}
		int rightDistance = 0;
		int rowCount = getNumLines();
		int headerCount = mHeaderViewInfos.size();
		int footerCount = mFooterViewInfos.size();
		if(itemIndex < headerCount){
			//header
			for(int i = itemIndex + 1; i < headerCount; i++){
				View headerView = mHeaderViewInfos.get(i).view;
				rightDistance += headerView.getWidth() + getHorizontalSpacing();
				if(headerView instanceof GridViewHeaderViewExpandDistance){
					rightDistance -= ((GridViewHeaderViewExpandDistance)headerView).getLeftExpandDistance();
					rightDistance -= ((GridViewHeaderViewExpandDistance)headerView).getRightExpandDistance();
				}
			}
			
			int nextColumnIndex = headerCount;
			int lastAdapterIndex = mItemCount - mFooterViewInfos.size() - 1;
			if(nextColumnIndex <= lastAdapterIndex){
				int rightColumnCount = (lastAdapterIndex - nextColumnIndex + 1) / rowCount;
				int rowLeft = (lastAdapterIndex - nextColumnIndex + 1) % rowCount;
				if(rowLeft > 0){
					rightColumnCount ++;
				}
				if(rightColumnCount > 0){
					View adatperView = getChildAt(nextColumnIndex - mFirstPosition);
					if(adatperView != null){
						rightDistance += (adatperView.getWidth() + getHorizontalSpacing()) * rightColumnCount;
					}
					else{
						return Integer.MAX_VALUE;
					}
				}
			}
			for(int i = 0; i < footerCount; i++){
				View footerView = mFooterViewInfos.get(i).view;
				rightDistance += footerView.getWidth() + getHorizontalSpacing();
				if(footerView instanceof GridViewHeaderViewExpandDistance){
					rightDistance -= ((GridViewHeaderViewExpandDistance)footerView).getLeftExpandDistance();
					rightDistance -= ((GridViewHeaderViewExpandDistance)footerView).getRightExpandDistance();
				}
			}
		}
		else if(itemIndex >= headerCount && itemIndex < (mItemCount - footerCount)){
			//adatper
			int nextColumnIndex = itemIndex + rowCount - ((itemIndex - mHeaderViewInfos.size()) % rowCount);
			int lastAdapterIndex = mItemCount - mFooterViewInfos.size() - 1;
			if(nextColumnIndex <= lastAdapterIndex){
				int rightColumnCount = (lastAdapterIndex - nextColumnIndex + 1) / rowCount;
				int rowLeft = (lastAdapterIndex - nextColumnIndex + 1) % rowCount;
				if(rowLeft > 0){
					rightColumnCount ++;
				}
				if(rightColumnCount > 0){
					rightDistance += (itemView.getWidth() + getHorizontalSpacing()) * rightColumnCount;
				}
			}
			for(int i = 0; i < footerCount; i++){
				View footerView = mFooterViewInfos.get(i).view;
				rightDistance += footerView.getWidth() + getHorizontalSpacing();
				if(footerView instanceof GridViewHeaderViewExpandDistance){
					rightDistance -= ((GridViewHeaderViewExpandDistance)footerView).getLeftExpandDistance();
					rightDistance -= ((GridViewHeaderViewExpandDistance)footerView).getRightExpandDistance();
				}
			}
		}
		else{
			//footer
			int footerStart = footerCount - (mItemCount - 1 - itemIndex);
			if(footerStart > 0){
				for(int i = footerStart; i < footerCount; i++){
					View footerView = mFooterViewInfos.get(i).view;
					rightDistance += footerView.getWidth() + getHorizontalSpacing();
					if(footerView instanceof GridViewHeaderViewExpandDistance){
						rightDistance -= ((GridViewHeaderViewExpandDistance)footerView).getLeftExpandDistance();
						rightDistance -= ((GridViewHeaderViewExpandDistance)footerView).getRightExpandDistance();
					}
				}
			}
		}
		return rightDistance;
	}
	
	
	/**
	 * 是否可以向下聚焦（如果一行的下面没有item的时候不让向下） 只修改了方法名
	 * @return
	 */
	private boolean checkIsCanRight(){
		int selectedIndex = getSelectedItemPosition();
		int headerCount = mHeaderViewInfos.size();
		int footerCount = mFooterViewInfos.size();
		if(selectedIndex < headerCount){
			if(selectedIndex < (mItemCount - 1)){
				return true;
			}
			else{
				if(mOnFocusFlipGridViewListener != null){
					mOnFocusFlipGridViewListener.onReachGridViewRight();
				}
				return false;
			}
		}
		else{
			if(footerCount > 0){
				if(selectedIndex < (mItemCount - 1)){
					return true;
				}
				else{
					if(mOnFocusFlipGridViewListener != null){
						mOnFocusFlipGridViewListener.onReachGridViewRight();
					}
					return false;
				}	
			}
			else{
				int rowCount = getNumLines();
//				int nextDownIndex = selectedIndex + columnCount;
//				if(nextDownIndex > (mItemCount - 1)){
//					//下下一行的第一个位置是否大于列表总数
//					//int nextColumnIndex = selectedIndex + columnCount - ((selectedIndex - headerCount + 1) % columnCount) + 1;
//					//下一行的第一个位置是否大于列表总数
//					int nextColumnIndex = getRowStart(nextDownIndex);
//					Log.i("test", "nextColumnIndex="+nextColumnIndex);
//					if(nextColumnIndex >= mItemCount){
//						if(mOnFocusFlipGridViewListener != null){
//							mOnFocusFlipGridViewListener.onReachGridViewBottom();
//						}
//					}
//					return false;
//				}
				int nextFirstIndex = getColumnStart(selectedIndex) + rowCount;
				if(nextFirstIndex >= mItemCount){
					if(mOnFocusFlipGridViewListener != null){
						mOnFocusFlipGridViewListener.onReachGridViewRight();
					}
					return false;
				}
				else{
					return true;
				}
			}
		}
	}
	
	
	/**
	 * 取得headerView的选中childView的序号
	 * @param headerView
	 * @return
	 */
	private int getHeaderViewSecondIndex(View headerView){
		int secondIndex = -1;
		if(headerView instanceof FocusRelativeLayout){
			View selectedChildView = ((FocusRelativeLayout)headerView).getSelectedView();
			if(selectedChildView != null){
				if(headerView instanceof FlipGridViewHeaderOrFooterInterface){
					FlipGridViewHeaderOrFooterInterface headerInterface = (FlipGridViewHeaderOrFooterInterface)headerView;
					secondIndex = headerInterface.getViewIndex(selectedChildView);
				}
			}
		}
		return secondIndex;
	}
	
	
	/**
	 * 设置selection(专供带有headerView使用的，因为setSelection()已经被重写)
	 * @param index
	 */
	private void setAdapterSelection(int index){
		super.setSelection(index);
	}
	
	/**
	 * 设置出场动画帧率
	 * @param outAnimFrameCount
	 */
	public void setOutAnimFrameCount(int outAnimFrameCount) {
		if (mOutAnimationRunnable != null) {
			mOutAnimationRunnable.setOutAnimFrameCount(outAnimFrameCount);
		}
	}
	
	/**
	 * 出场动画
	 * @author tim
	 *
	 */
	private class OutAnimationRunnable implements Runnable {
		private int outAnimFrameCount = 15;
		public void setOutAnimFrameCount(int outAnimFrameCount) {
			this.outAnimFrameCount = outAnimFrameCount;
		}

		private int mCurrFrameCount;
		private boolean mIsFinished = true;
		public void start(){
			if(mIsFinished){
				setFocusable(false);
				mIsFinished = false;
				mCurrFrameCount = 0;
				post(this);
			}
		}
		
		public void stop(){
			if(mIsFinished == false){
				Log.i(TAG, "OutAnimationRunnable stop");
				setFocusable(true);
				mIsFinished = true;
				setChild(1.0f);
				if(mOnFocusFlipGridViewListener != null){
					mOnFocusFlipGridViewListener.onOutAnimationDone();
				}
			}
		}
		
		@Override
		public void run() {
			if(mIsFinished){
				return;
			}
			if(mCurrFrameCount > outAnimFrameCount){
				stop();
				return;
			}
			mCurrFrameCount ++;
			float scale = 1.0f - (float)mCurrFrameCount / outAnimFrameCount;
			setChild(scale);
			post(this);
		}
		
		
		/**
		 * 设置子view的参数
		 * @param scale
		 */
		private void setChild(float scale){
			int itemCount = getChildCount();
			if(mAnimAlpha){
				setAlpha(scale);
				mAnimAlphaValue = (int)(scale * 255);
			}
			for(int i = 0; i < itemCount; i++){
				View itemView = getChildAt(i);
				if(itemView instanceof FlipGridViewHeaderOrFooterInterface){
					FlipGridViewHeaderOrFooterInterface headerOrFooterView = (FlipGridViewHeaderOrFooterInterface)itemView;
					int childCount = headerOrFooterView.getHorCount() * headerOrFooterView.getVerticalCount();
					for(int j = 0; j < childCount; j++){
						View childView = headerOrFooterView.getView(j);
						if(childView != null){
							childView.setScaleX(scale);
							childView.setScaleY(scale);							
						}
					}
				}
				else{
					itemView.setScaleX(scale);
					itemView.setScaleY(scale);
				}
			}
		}
		
	}
	
	
	/**
	 * FocusFlipView的监听器
	 * @author tim
	 */
	public interface OnFocusFlipGridViewListener{
		/**
		 * 每次完成新的布局的回调（几乎每次按键都会重新布局）
		 * @param isFirst 是否为首次布局
		 */
		public void onLayoutDone(boolean isFirst);
		/**
		 * 出场动画完成
		 */
		public void onOutAnimationDone();
		/**
		 * 已经滚动到最顶上
		 */
		public void onReachGridViewLeft();
		/**
		 * 已经滚动到最底下
		 */
		public void onReachGridViewRight();
	}


	@Override
	public boolean isFocusBackground() {
		// TODO Auto-generated method stub
		return false;
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
	
	//向上滚动时，仍然需要滚动的距离，用于计算目标焦点位置 quanqing.hqq
	//只修改了方法名
	private int getRemainScrollLeftDistance(int nextSelectedPosition){
		
		int remainAmountToScroll = 0;
		
		int horizontalSpacing = getHorizontalSpacing();
		int center = getWidth() / 2;
		int distanceLeft = getFlipRowFirstItemLeftMoveDistance(nextSelectedPosition);
		
		View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
		int nextSelectedCenter = 0;
		View firstView = getChildAt(0);
		int firstLeft = firstView.getLeft();
		if(firstView instanceof GridViewHeaderViewExpandDistance){
			firstLeft += ((GridViewHeaderViewExpandDistance)firstView).getLeftExpandDistance();
		}
		if (nextSelctedView == null) {
			//还未添加的时候通过计算来取得当前选中view的中心位置，取最上一个item的位置来计算
			nextSelctedView = getChildAt(0);
			int oldColumnStart = getColumnStart(getFirstVisiblePosition());
			int columnStart = getColumnStart(nextSelectedPosition);
			int headerCount = mHeaderViewInfos.size();
			int delta;
			if(headerCount > 0){
				if(columnStart < headerCount){
					delta = (oldColumnStart - headerCount) / getNumLines();
					//先计算除item以外的每行的高度
					nextSelectedCenter = nextSelctedView.getLeft() -(nextSelctedView.getWidth() + horizontalSpacing) * delta;
					//再加上headerView的中心点的高度
					for(int i = columnStart; i < headerCount; i++){
						View headerView = mHeaderViewInfos.get(i).view;
						int headerViewWidth = headerView.getWidth();
						if(headerView instanceof GridViewHeaderViewExpandDistance){
							headerViewWidth -= ((GridViewHeaderViewExpandDistance)headerView).getLeftExpandDistance();
							headerViewWidth -= ((GridViewHeaderViewExpandDistance)headerView).getRightExpandDistance();
						}
						nextSelectedCenter -= horizontalSpacing + headerViewWidth / 2;
						if(DEBUG){
							Log.i(TAG, "getUpRect rowStart="+columnStart+" oldRowStart="+oldColumnStart+" delta="+delta+
									" headerViewHeight="+headerViewWidth+" nextSelectedCenter="+nextSelectedCenter);
						}
					}
				}
				else{
					//rowStart >= headerCount
					delta = (oldColumnStart - columnStart) / getNumLines();
					nextSelectedCenter = nextSelctedView.getLeft() + nextSelctedView.getWidth() / 2;
					nextSelectedCenter -= (nextSelctedView.getWidth() + horizontalSpacing) * delta;
				}
			}
			else{
				//headerCount <= 0
				delta = (oldColumnStart - columnStart) / getNumLines();
				nextSelectedCenter = nextSelctedView.getLeft() + nextSelctedView.getWidth() / 2;
				nextSelectedCenter -= (nextSelctedView.getWidth() + horizontalSpacing) * delta;				
			}
		} else {
			//nextSelctedView != null
			if(nextSelctedView instanceof GridViewHeaderViewExpandDistance){
				int leftDistance = ((GridViewHeaderViewExpandDistance)nextSelctedView).getLeftExpandDistance();
				int rightDistance = ((GridViewHeaderViewExpandDistance)nextSelctedView).getRightExpandDistance();
				nextSelectedCenter = nextSelctedView.getLeft() + leftDistance + 
						(nextSelctedView.getWidth() - leftDistance - rightDistance) / 2;
			}
			else{
				nextSelectedCenter = nextSelctedView.getLeft() + nextSelctedView.getWidth() / 2;
			}
		}

		int finalNextSelectedCenter = nextSelectedCenter + distanceLeft;

		if (finalNextSelectedCenter < center) {
			remainAmountToScroll = center - finalNextSelectedCenter;
			int maxDiff = getLeftLeftDistance(getFirstVisiblePosition());
			//firstTop到达顶点的距离
			maxDiff = mListPadding.left - (firstLeft - maxDiff);
			if(maxDiff < 0){
				maxDiff = 0;
			}
			int left = getFlipRowFirstItemLeftMoveDistance(getFirstVisiblePosition());
			maxDiff -= left;
			if(maxDiff < 0){
				maxDiff = 0;
			}
			if (remainAmountToScroll > maxDiff) {
				remainAmountToScroll = maxDiff;
			}
		}
		
		return remainAmountToScroll;
	}
	
	
	public void offsetFocusRect(int offsetX, int offsetY) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			mFocusRectparams.focusRect().offset(offsetX, offsetY);
		}
	}
	
	public void offsetFocusRect(int left, int right, int top, int bottom) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			mFocusRectparams.focusRect().left += left;
			mFocusRectparams.focusRect().right += right;
			mFocusRectparams.focusRect().top += top;
			mFocusRectparams.focusRect().bottom += bottom;
		}
	}
	
	public void offsetFocusRectLeftAndRight(int left, int right) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			mFocusRectparams.focusRect().left += left;
			mFocusRectparams.focusRect().right += right;
		}
	}
	
	public void offsetFocusRectTopAndBottom(int top, int bottom) {
		if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
			mFocusRectparams.focusRect().top += top;
			mFocusRectparams.focusRect().bottom += bottom;
		}
	}

	@Override
	public Rect getClipFocusRect() {
		// TODO Auto-generated method stub
		return mClipFocusRect;
	}
}

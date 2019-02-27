package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.ItemFlipScroller.ItemFlipScrollerListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class FlipGridView extends GridView {
	private boolean DEBUG = false;
	private final String TAG = "FlipGridView";
	private FlipRunnable mFlipRunnable;//动画算法的管理类
	private List<Integer> mHeaderViewList;//headerView的列表
	private List<Integer> mFooterViewList;//footerView的列表
	private OnFlipRunnableListener mFlipGridViewListener;//动画监听方法
	private int mLayoutPreFirstPos;//上次布局的显示在可视区域内的开始序号
	private int mLayoutPreLastPos;//上次布局的显示在可视区域内的结束序号
	private boolean mLockOffset;//是否锁住移动的动画
	private boolean mLockFlipAnim;//是否需要锁住弹性间距的动画
	private long mTestSpeedStartTime;
	private int mTestSpeedFrame;
	protected int mScrollOffset;//滚动的offset值
	public FlipGridView(Context context) {
		super(context);
		init();
	}

	public FlipGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FlipGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	
	/**
	 * 只锁一次布局时的弹性间距的动画
	 */
	public void lockFlipAnimOnceLayout(){
		mLockFlipAnim = true;
	}
	
	/**
	 * 判断是否有必要锁住按键事件
	 * @param keyCode
	 * @return
	 */
	public boolean lockKeyEvent(int keyCode){
		return mFlipRunnable.lockKeyEvent(keyCode);
	}
	
	
	/**
	 * 弹性间距的动画是否为下向，如果已经停止返回false
	 * @return
	 */
	public boolean isFlipDown(){
		if(mFlipRunnable.isFinished() == false){
			return mFlipRunnable.isDown();
		}
		return false;
	}
	
	
	/**
	 * 是否动画已经结束
	 * @return
	 */
	public boolean isFlipFinished(){
		return mFlipRunnable.isFinished();
	}
	
	
	/**
	 * 设置是否需要弹性间距动画的开关（默认为开）
	 * @param delay
	 */
	public void setDelayAnim(boolean delay){
		mFlipRunnable.setDelayAnim(delay);
	}
	
	
	/**
	 * 设置弹性间距选中的位置
	 * @param pos
	 */
	public void setFlipSelectedPosition(int pos){
		mFlipRunnable.setSelectedPosition(pos);
	}
	
	
	/**
	 * 取得快速滚动的时候单步的步长
	 * @return
	 */
	public int getFastFlipStep(){
		return mFlipRunnable.getFastFlipStep();
	}
	
	
	/**
	 * 手动结束动画
	 */
	public void stopFlip(){
		mFlipRunnable.stop();
	}
	
	
	/**
	 * 开始滚动，参数位置不做处理，是多少就滚动多少
	 * @param delta
	 */
	public void startRealScroll(int delta){
		mFlipRunnable.setSelectedPosition(mSelectedPosition);
		mFlipRunnable.startRealScroll(delta);
	}
	
	
	/**
	 * 取得指定index的行的第一个item的还剩余的距离
	 * @param index
	 * @return
	 */
	public int getFlipColumnFirstItemLeftMoveDistance(int index){
		return mFlipRunnable.getFlipColumnFirstItemLeftMoveDistance(index);
	}
	
	protected void correctTooHigh(int numColumns, int verticalSpacing, int childCount) {
		//锁住动画
		mLockFlipAnim = true;
		super.correctTooHigh(numColumns, verticalSpacing, childCount);
		mLockFlipAnim = false;
	}
	
	protected void correctTooLow(int numColumns, int verticalSpacing, int childCount) {
		//锁住动画
		mLockFlipAnim = true;
		super.correctTooLow(numColumns, verticalSpacing, childCount);
		mLockFlipAnim = false;
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(DEBUG){
			if (System.currentTimeMillis() - mTestSpeedStartTime >= 1000) {
				Log.d(TAG, "dispatchDraw frame = " + mTestSpeedFrame);
				mTestSpeedStartTime = System.currentTimeMillis();
				mTestSpeedFrame = 0;
			}
			mTestSpeedFrame++;
		}
	}
	
	
	/**
	 * 手动开始动画（不建议在入场动画之外的场景下使用）
	 * @param offset
	 */
	protected void startFlip(final int offset){
		mLockOffset = true;
		flipScrollBy(offset);
	}
	
	@Override
	public void offsetChildrenTopAndBottom(final int offset) {
		if(mTouchMode < 0 && !mLockFlipAnim){
			if(mLockOffset){
				return;
			}
			mScrollOffset = offset;
		}
		else{
			mScrollOffset = 0;
			super.offsetChildrenTopAndBottom(offset);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(mTouchMode < 0 && mFlipRunnable.lockKeyEvent(keyCode)){
			return true;
		}
		else{
			return super.onKeyDown(keyCode, event);
		}
		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(!mFlipRunnable.isFinished() && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP)){
			mFlipRunnable.startComeDown(); 
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	protected void layoutChildren() {
		if(mFlipRunnable.isFinished() == false){
			mLayoutPreFirstPos = getFirstVisiblePosition();
			mLayoutPreLastPos = getLastVisiblePosition();
			//为了保证在向下滚动的时候如果要重新布局的话lastPos必须不小于现在的lastPos,
			//防止在动画过程中重新布局后lastPos变小。因为原生的GridView是水平整行布局的
			if(mFlipRunnable.isDown()){
				setMinLastPos(mLayoutPreLastPos);
			}
			else{
				setMinFirstPos(mLayoutPreFirstPos);
			}
		}
		gridViewLayoutChildren();
		setMinLastPos(-1);
		setMinFirstPos(Integer.MAX_VALUE);
		if(mTouchMode < 0){
			onLayoutChildrenDone();
			if(mScrollOffset != 0){
				mFlipRunnable.setStartingFlipScroll();
				//如果还在滚动的过程中回调重新布局的引起的位置问题
				mFlipRunnable.checkFlipFastScroll();
				mFlipRunnable.addView(getFirstVisiblePosition(), getLastVisiblePosition());
				mFlipRunnable.setSelectedPosition(mSelectedPosition);
				flipScrollBy(mScrollOffset);
				mScrollOffset = 0;
			}
			else{
				mFlipRunnable.checkFlipFastScroll();
			}
		}
		mLockFlipAnim = false;
	}
	
	private void gridViewLayoutChildren(){
		if (!mNeedLayout) {
			return;
		}

		final boolean blockLayoutRequests = mBlockLayoutRequests;
		if (!blockLayoutRequests) {
			mBlockLayoutRequests = true;
		}

		try {
			// super.layoutChildren();

			invalidate();

			if (mAdapter == null) {
				resetList();
				// TODO
				// invokeOnItemScrollListener();
				return;
			}

			final int childrenTop = mListPadding.top;
			final int childrenBottom = getBottom() - getTop() - mListPadding.bottom;

			int childCount = getChildCount();
			int index = 0;
			int delta = 0;

			View sel;
			View oldSel = null;
			View oldFirst = null;
			View newSel = null;

			// Remember stuff we will need down below
			switch (mLayoutMode) {
			case LAYOUT_SET_SELECTION:
			case LAYOUT_FROM_MIDDLE:
				index = mNextSelectedPosition - mFirstPosition;
				if (index >= 0 && index < childCount) {
					newSel = getChildAt(index);
				}
				break;
			case LAYOUT_FORCE_TOP:
			case LAYOUT_FORCE_BOTTOM:
			case LAYOUT_SPECIFIC:
			case LAYOUT_SYNC:
				break;
			case LAYOUT_MOVE_SELECTION:
				if (mNextSelectedPosition >= 0) {
					delta = mNextSelectedPosition - mSelectedPosition;
				}
				break;				
			default:
				// Remember the previously selected view
				index = mSelectedPosition - mFirstPosition;
				if (index >= 0 && index < childCount) {
					oldSel = getChildAt(index);
				}

				// Remember the previous first child
				oldFirst = getChildAt(getHeaderViewsCount());
			}

			boolean dataChanged = mDataChanged;
			if (dataChanged) {
				handleDataChanged();
			}

			// Handle the empty set by removing all views that are visible
			// and calling it a day
			if (mItemCount == 0) {
				resetList();
				// TODO
				// invokeOnItemScrollListener();
				return;
			}

			setSelectedPositionInt(mNextSelectedPosition);
			if(oldSel != null && (mSelectedPosition - mFirstPosition) != index){
				//grid view 的count由多到少时，可能只有不足一屏的情况，此时需要重新查找oldSel
				index = mSelectedPosition - mFirstPosition;
				oldSel = getChildAt(index);
			}

			// Pull all children into the RecycleBin.
			// These views will be reused if possible
			final int firstPosition = mFirstPosition;
			final RecycleBin recycleBin = mRecycler;

			if (dataChanged) {
				for (int i = 0; i < childCount; i++) {
					recycleBin.addScrapView(getChildAt(i), firstPosition + i);
				}
			} else {
				recycleBin.fillActiveViews(childCount, firstPosition);
			}

			// Clear out old views
			// removeAllViewsInLayout();
			detachAllViewsFromParent();
			recycleBin.removeSkippedScrap();

			switch (mLayoutMode) {
			case LAYOUT_FROM_MIDDLE:
				if (newSel != null) {
					sel = fillFromSelection(newSel.getTop(), childrenTop, childrenBottom);
				} else {
					sel = fillSelectionMiddle(childrenTop, childrenBottom);
				}
				break;
			case LAYOUT_SET_SELECTION:
				if (newSel != null) {
					sel = fillFromSelection(newSel.getTop(), childrenTop, childrenBottom);
				} else {
					sel = fillSelection(childrenTop, childrenBottom);
				}
				break;
			case LAYOUT_FORCE_TOP:
				mFirstPosition = 0;
				sel = fillFromTop(childrenTop);
				adjustViewsUpOrDown();
				break;
			case LAYOUT_FORCE_BOTTOM:
				sel = fillUp(mItemCount - 1, childrenBottom);
				adjustViewsUpOrDown();
				break;
			case LAYOUT_SPECIFIC:
				sel = fillSpecific(mSelectedPosition, mSpecificTop);
				break;
			case LAYOUT_SYNC:
				sel = fillSpecific(mSyncPosition, mSpecificTop);
				break;
			case LAYOUT_MOVE_SELECTION:
				// Move the selection relative to its old position
				sel = moveSelection(delta, childrenTop, childrenBottom);
				break;
			default:
				if (childCount == 0) {
					if (!mStackFromBottom) {
						setSelectedPositionInt(mAdapter == null || isInTouchMode() ? INVALID_POSITION : 0);
						sel = fillFromTop(childrenTop);
					} else {
						final int last = mItemCount - 1;
						setSelectedPositionInt(mAdapter == null || isInTouchMode() ? INVALID_POSITION : last);
						sel = fillFromBottom(last, childrenBottom);
					}
				} else {
					if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {
						sel = fillSpecific(mSelectedPosition, oldSel == null ? childrenTop : oldSel.getTop());
					} else if (mFirstPosition < mItemCount) {
						sel = fillSpecific(mFirstPosition, oldFirst == null ? childrenTop : oldFirst.getTop());
					} else {
						sel = fillSpecific(0, childrenTop);
					}
				}
				break;
			}

			// Flush any cached views that did not get reused above
			recycleBin.scrapActiveViews();

			if (sel != null) {
				positionSelector(INVALID_POSITION, sel);
				// mSelectedTop = sel.getTop();
			} else if (mTouchMode > TOUCH_MODE_DOWN && mTouchMode < TOUCH_MODE_SCROLL) {
				View child = getChildAt(mMotionPosition - mFirstPosition);
				if (child != null)
					positionSelector(mMotionPosition, child);
			} else {
				// mSelectedTop = 0;
				mSelectorRect.setEmpty();
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

			// TODO
			// invokeOnItemScrollListener();
		} finally {
			if (!blockLayoutRequests) {
				mBlockLayoutRequests = false;
			}
		}
		mNeedLayout = false;
	}
	
	
	
	
	/**
	 * GridView完成新的布局(需重载)
	 */
	protected void onLayoutChildrenDone(){
	}
	
	
	
	/**
	 * 向下：取得计算新增行的参照item，现在为最后一行的第一个
	 * 向上：取第一行的最后一个
	 * （现在修改成新增列参考的位置为最后一排的第一列的item，
	 * 	  因为这个item是整行里面的参照物，而后面一行是以前面一行为参照物的）
	 */
	@Override
	protected int getFillGapNextChildIndex(boolean isDown){
		if(mTouchMode < 0){
			if(isDown){
				int first = getFirstVisiblePosition();
				int last = getLastVisiblePosition();
				if(first >= last){
					return 0;
				}
				int left = (last - mHeaderViewList.size() + 1)% getColumnNum();
				if(left <= 0){
					left = getColumnNum();
				}
				int childIndex = (last - (left - 1)) - first;
				if(childIndex >= getChildCount()){
					childIndex = getChildCount() -1;
				}
				return childIndex;
			}
			else{
				int first = getFirstVisiblePosition();
				//当屏幕内没有HeaderView的时候就取列表最上一行的最后一列
				if(first >= mHeaderViewList.size()){
					int numColumn = getColumnNum();
					int childIndex = numColumn - 1;
					int count = getChildCount();
					if(count < numColumn){
						childIndex = count - 1;
					}
					return childIndex;
				}
				else{
					//当屏幕内有HeaderView的时候就取第0个HeaderView
					return 0;
				}
			}
			
		}
		else{
			return super.getFillGapNextChildIndex(isDown);
		}
	}
	

	/**
	 * 弹性间距的开始的回调
	 */
	protected void onFlipItemRunnableStart(){
		if(mFlipGridViewListener != null){
			mFlipGridViewListener.onStart();
		}
	}
	
	/**
	 * 弹性间距的运动过程的回调
	 */
	protected void onFlipItemRunnableRunning(float moveRatio, View itemView, int index){
		if(mFlipGridViewListener != null){
			mFlipGridViewListener.onFlipItemRunnable(moveRatio, itemView, index);
		}
	}
	
	
	/**
	 * 弹性间距的动画完成回调
	 */
	protected void onFlipItemRunnableFinished(){
		mLockOffset = false;
		if(mFlipGridViewListener != null){
			mFlipGridViewListener.onFinished();
		}
	}
	
	
	/**
	 * 设置弹性间距的监听方法
	 * @param l
	 */
	public void setOnFlipGridViewRunnableListener(OnFlipRunnableListener l){
		mFlipGridViewListener = l;
	}
	
	
	/**
	 * 增加Flip的HeaderView，需要配置行跟列的个数
	 * @param v
	 */
	@Override
	public void addHeaderView(View v) {
		if(v != null && v instanceof FlipGridViewHeaderOrFooterInterface){
			FlipGridViewHeaderOrFooterInterface headView = (FlipGridViewHeaderOrFooterInterface)v;
			int columnCount = headView.getHorCount();
			int verticalCount = headView.getVerticalCount();
			mHeaderViewList.add(mFlipRunnable.makeHeaderAndFooterViewInfo(columnCount, verticalCount));
			super.addHeaderView(v);
		}
		else{
			throw new IllegalStateException("Cannot add FlipGridView header view to list header view maybe not FlipGridViewHeaderOrFooterInterface");
		}
	}
	
	
	/**
	 * 手动加入headerView相关信息
	 * @param v
	 */
	 public void addHeaderViewInfo(View v) {
        if (v != null && v instanceof FlipGridViewHeaderOrFooterInterface) {
            FlipGridViewHeaderOrFooterInterface headView = (FlipGridViewHeaderOrFooterInterface) v;
            int columnCount = headView.getHorCount();
            int verticalCount = headView.getVerticalCount();
            mHeaderViewList.add(mFlipRunnable.makeHeaderAndFooterViewInfo(columnCount,
                    verticalCount));
        } else {
            throw new IllegalStateException(
                    "Cannot add FlipGridView header view to list header view maybe not FlipGridViewHeaderOrFooterInterface");
        }
    }
	
	 
	/**
	 * 删除headerView
	 * @param headerView
	 */
	 @Override
	public boolean removeHeaderView(View headerView){
		if(isFlipFinished()){
			int headerViewIndex = -1;
			for(int i = 0; i < mHeaderViewInfos.size(); i++){
				FixedViewInfo headerChildView = mHeaderViewInfos.get(i);
				if(headerChildView != null && headerChildView.view.equals(headerView)){
					headerViewIndex = i;
					break;
				}
			}
			if(headerViewIndex >= 0){
				//删除headerView不让其进行动画
				lockFlipAnimOnceLayout();
				mHeaderViewList.remove(headerViewIndex);
				return super.removeHeaderView(headerView);
			}
		}
		else{
			Log.e(TAG, "flip running can not remove view");
		}
		return false;
	}
	
	
	/**
	 * 手动清除headerView相关信息
	 */
    public void clearHeaderViewInfo() {
        mHeaderViewList.clear();
    }

	    
	/**
	 * 增加Flip的FooterView，需要配置行跟列的个数
	 * @param v
	 */
	@Override
	public void addFooterView(View v){
		if(v != null && v instanceof FlipGridViewHeaderOrFooterInterface){
			FlipGridViewHeaderOrFooterInterface headView = (FlipGridViewHeaderOrFooterInterface)v;
			int columnCount = headView.getHorCount();
			int verticalCount = headView.getVerticalCount();
			mFooterViewList.add(mFlipRunnable.makeHeaderAndFooterViewInfo(columnCount, verticalCount));
			super.addFooterView(v);
		}
		else{
			throw new IllegalStateException("Cannot add FlipGridView footer view to list header view maybe not FlipGridViewHeaderOrFooterInterface");
		}
	}
	
	
	/**
	 * 手动加入footerView相关信息
	 * @param v
	 */
	 public void addFooterViewInfo(View v) {
			if(v != null && v instanceof FlipGridViewHeaderOrFooterInterface){
				FlipGridViewHeaderOrFooterInterface footerView = (FlipGridViewHeaderOrFooterInterface)v;
				int columnCount = footerView.getHorCount();
				int verticalCount = footerView.getVerticalCount();
				mFooterViewList.add(mFlipRunnable.makeHeaderAndFooterViewInfo(columnCount, verticalCount));
			}
			else{
				throw new IllegalStateException("Cannot add FlipGridView footer view to list header view maybe not FlipGridViewHeaderOrFooterInterface");
			}
    }
	   

	/**
	 * 删除footerView
	 * @param headerView
	 */
	 @Override
	public boolean removeFooterView(View footerView){
		if(isFlipFinished()){
			int footerViewIndex = -1;
			for(int i = 0; i < mFooterViewInfos.size(); i++){
				FixedViewInfo footerChildView = mFooterViewInfos.get(i);
				if(footerChildView != null && footerChildView.view.equals(footerView)){
					footerViewIndex = i;
					break;
				}
			}
			if(footerViewIndex >= 0){
				//删除footerView不让其进行动画
				lockFlipAnimOnceLayout();
				mFooterViewInfos.remove(footerViewIndex);
				return super.removeFooterView(footerView);
			}
		}
		else{
			Log.e(TAG, "flip running can not remove view");
		}
		return false;
	}
	 
	 
	/**
	 * 手动清除footerView相关信息
	 */
    public void clearFooterViewInfo() {
    	mFooterViewList.clear();
    }
    
    
	/**
	 * 取得当前item剩余的移动距离
	 * @param index
	 * @param secondIndex
	 * @return
	 */
	public int getFlipItemLeftMoveDistance(int index ,int secondIndex){
		if(!mFlipRunnable.isFinished()){
			return mFlipRunnable.getFlipItemLeftMoveDistance(index, secondIndex);
		}
		return 0;
	}
	
	protected boolean isFliping(){
		return !mFlipRunnable.isFinished();
	}
	
	/**
	 * 开始滚动
	 * @param distance
	 */
	private void flipScrollBy(int distance) {		
		// No sense starting to scroll if we're not going anywhere
		final int firstPos = mFirstPosition;
		final int childCount = getChildCount();
		final int lastPos = firstPos + childCount;
		final int topLimit = getPaddingTop();
		final int bottomLimit = getHeight() - getPaddingBottom();

		if (distance == 0 || mItemCount == 0 || childCount == 0 || (firstPos == 0 && getChildAt(0).getTop() == topLimit && distance > 0)
				|| (lastPos == mItemCount && getChildAt(childCount - 1).getBottom() == bottomLimit && distance < 0)) {
			mFlipRunnable.stop();
		} else {
			reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
			mFlipRunnable.startScroll(distance);
		}
	}
	
	
	/**
	 * 取得Header参数列表
	 * @return
	 */
	private List<Integer> getHeaderViewInfo(){
		return mHeaderViewList;
	}
	
	
	/**
	 * 取得Footer参数列表
	 * @return
	 */
	private List<Integer> getFooterViewInfo(){
		return mFooterViewList;
	}
	
	
	/**
	 * 初始化
	 */
	private void init(){
		mFlipRunnable = new FlipRunnable();
		mFlipRunnable.setOnFlipRunnableListener(new OnFlipRunnableListener() {
			@Override
			public void onFlipItemRunnable(float moveRatio, View itemView, int index) {
				onFlipItemRunnableRunning(moveRatio, itemView, index);
			}
			
			@Override
			public void onFinished() {
				onFlipItemRunnableFinished();
			}

			@Override
			public void onStart() {
				onFlipItemRunnableStart();
			}
		});
		mHeaderViewList = new ArrayList<Integer>();
		mFooterViewList = new ArrayList<Integer>();
	}
	
	
	/**
	 * 不考虑padding，对于回收View的影响，因为现在将padding理解为是对整个GridView的padding
	 */
	protected void detachOffScreenChildren(boolean isDown) {
		int numChildren = getChildCount();
		int firstPosition = mFirstPosition;
		int start = 0;
		int count = 0;

		if (isDown) {
			final int top = 0;//getPaddingTop();
			for (int i = 0; i < numChildren; i++) {
				int n = i;
				final View child = getChildAt(n);
				if (child.getBottom() >= top) {
					break;
				} else {
					start = n;
					count++;
					mRecycler.addScrapView(child, firstPosition + n);
				}
			}

			start = 0;
		} else {
			final int bottom = getHeight();// - getPaddingBottom();
			for (int i = numChildren - 1; i >= 0; i--) {
				int n = i;
				final View child = getChildAt(n);
				if (child.getTop() <= bottom) {
					break;
				} else {
					start = n;
					count++;
					mRecycler.addScrapView(child, firstPosition + n);
				}
			}
		}

		detachViewsFromParent(start, count);

		if (isDown) {
			mFirstPosition += count;
		}
	}
	
	
	/**
	 * 取得当前选中位置的偏移跟当前行的差值距离（现在没有用到这段代码，后续可以会使用
	 * 作用是：当快速滚动的时候如果这个时候要layout,使用一次加入很多个新的view，但此次布局的参考会有问题，
	 * 因为在快速滚动的时候mReferenceViewInSelectedRow值是在onkeyDown里设置的，不是在layout的时候，
	 * 如果onkeyDown还没有加入到列表里面的时候会在动画的过程中加入，但是如果这个时候layout，
	 * mReferenceViewInSelectedRow值就不对了）
	 * @param rowStart
	 * @param selectedRowView
	 * @return
	 */
	protected int getSelectedRowItemOffset(int rowStart, View selectedRowView){
		int offset = 0;
		int tag = (Integer)selectedRowView.getTag();
		int columnCount = getColumnNum();
		int tagRowStart = getRowStart(tag);
		int upRowDelta = 0;
		if(tagRowStart > (rowStart + columnCount)){
			upRowDelta = ((tagRowStart - rowStart) / columnCount) - 1;
			offset -= ((selectedRowView.getHeight() + getVerticalSpacing())) * upRowDelta;
			if(rowStart < mHeaderViewInfos.size()){
				offset -= (selectedRowView.getHeight() + getVerticalSpacing());
			}
			int rowStartLeft = getFlipColumnFirstItemLeftMoveDistance(rowStart);
			int tagRowLeft = getFlipColumnFirstItemLeftMoveDistance(tagRowStart);
			int tagLeft = getFlipItemLeftMoveDistance(tag, 0);
//			offset += tagLeft - tagRowLeft;
//			offset -= rowStartLeft - tagRowLeft;
			if(DEBUG){
				Log.i(TAG, "getSelectedRowItemOffset mReferenceViewInSelectedRow="+selectedRowView.getTag()+
						" tagRowStart="+tagRowStart+" rowStart="+rowStart+" offset="+offset+
						" height="+selectedRowView.getHeight()+" upRowDelta="+upRowDelta+
						" rowStartLeft="+rowStartLeft+" tagLeft="+tagLeft+" tagRowLeft="+tagRowLeft);
			}
		}

		return offset;
	}
	
	
	/**
	 * 拉绳效果Runnable
	 * 有三个地方影响列表item的位置
	 * 1.checkFlipFastScroll();这个地方只有连续按的时候才会调到，
	 * 		目的是因为每次按键GridView会被重新布局，需要重新摆正动画时候的位置
	 * 2.onOffsetNewChild();是因为新增的item，
	 * 		使用getFillGapNextChildIndex();取得index做为参照默认布局是水平一排，因为动画是有错开距离，所以需要再次调整位置
	 * 3.run();动画运动的时候每个item的位移
	 * @author tim
	 *
	 */
	private class FlipRunnable implements Runnable {
		private ItemFlipScroller mItemFlipScroller;
		private SparseArray<Integer> mFlipItemBottomList;
		private int mHeaderViewDelta = 0;
		private OnFlipRunnableListener mOnFlipRunnableListener;
		FlipRunnable() {
			mFlipItemBottomList = new SparseArray<Integer>();
			mItemFlipScroller = new ItemFlipScroller();
			mItemFlipScroller.setItemFlipScrollerListener(new ItemFlipScrollerListener() {
				@Override
				public void onOffsetNewChild(int childIndex, int secondIndex, int delta) {
					int child = childIndex - getFirstVisiblePosition();
					View childView = getChildAt(child);
					if(DEBUG){
						Log.i(TAG, "onOffsetNewChild childIndex="+childIndex+" secondIndex="+secondIndex+" delta="+delta+" before bottom="+childView.getBottom());
					}
					if(childView != null && delta != 0){
						if(childIndex < getHeaderViewsCount()){
							//header
							//如果头部有子view的话需要处理每个子View
							if(childView != null && childView instanceof FlipGridViewHeaderOrFooterInterface){
								FlipGridViewHeaderOrFooterInterface headerView = (FlipGridViewHeaderOrFooterInterface)childView;
								int headerChildCount = headerView.getHorCount() * headerView.getVerticalCount();
								if(secondIndex < headerChildCount){
									if(secondIndex == (headerChildCount -1)){
										mHeaderViewDelta = delta;
										if(DEBUG){
											Log.i(TAG, "onOffsetNewChild mHeaderViewDelta="+mHeaderViewDelta+" secondIndex="+secondIndex+" headerChildCount="+headerChildCount);
										}
										childView.offsetTopAndBottom(delta);
										mFlipItemBottomList.put(getFlipItemMapKey(childIndex, -1), childView.getBottom());
									}
									View headerChildView = headerView.getView(secondIndex);
									if(headerChildView != null){
										headerChildView.offsetTopAndBottom(delta - mHeaderViewDelta);
										mFlipItemBottomList.put(getFlipItemMapKey(childIndex, secondIndex), headerChildView.getBottom());
									}
								}
							}
							else{
								childView.offsetTopAndBottom(delta);
								mFlipItemBottomList.put(getFlipItemMapKey(childIndex, 0), childView.getBottom());
							}
						}
						else{
							//adapter
							childView.offsetTopAndBottom(delta);
							mFlipItemBottomList.put(getFlipItemMapKey(childIndex, 0), childView.getBottom());
						}
					}
					
					
				}

				@Override
				public void onFinished() {
					if(mOnFlipRunnableListener != null){
						mOnFlipRunnableListener.onFinished();
					}
				}
			});
		}
		
		void setHor_delay_distance(int hor_delay_distance) {
			mItemFlipScroller.setHor_delay_distance(hor_delay_distance);
		}

		void setVer_delay_distance(int ver_delay_distance) {
			mItemFlipScroller.setVer_delay_distance(ver_delay_distance);
		}

		void setMin_fast_setp_discance(int min_fast_setp_discance) {
			mItemFlipScroller.setMin_fast_setp_discance(min_fast_setp_discance);
		}

		void setFlip_scroll_frame_count(int flip_scroll_frame_count) {
			mItemFlipScroller.setFlip_scroll_frame_count(flip_scroll_frame_count);
		}

		void setHor_delay_frame_count(int hor_delay_frame_count) {
			mItemFlipScroller.setHor_delay_frame_count(hor_delay_frame_count);
		}

		void setVer_delay_frame_count(int ver_delay_frame_count) {
			mItemFlipScroller.setVer_delay_frame_count(ver_delay_frame_count);
		}
		
		
		/**
		 * 是否有必要锁住按键使按键无效
		 * @param keyCode
		 * @return
		 */
		public boolean lockKeyEvent(int keyCode){
			boolean isFinished = mItemFlipScroller.isFinished();
			if(DEBUG){
				Log.i(TAG, "lockKeyEvent isFinished="+isFinished+" keyCode="+keyCode+" isDown="+mItemFlipScroller.isDown());
			}
			if(!isFinished){
				//在向下滚动的时候按上方向键无效
				if(keyCode == KeyEvent.KEYCODE_DPAD_UP && mItemFlipScroller.isDown()){
					return true;
				}
				//在向上滚动的时候按下方向键无效
				else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !mItemFlipScroller.isDown()){
					return true;
				}
				//使左右按键无效
				else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
					return true;
				}
			}
			return false;
		}
		
		
		/**
		 * 设置当前选中的位置
		 */
		public void setSelectedPosition(int selectedPosition){
			mItemFlipScroller.setSelectedPosition(selectedPosition);
		}
		
		
		/**
		 * 取得快速滚动的时候单步的步长
		 * @return
		 */
		public int getFastFlipStep(){
			return mItemFlipScroller.getFastFlipStep();
		}
		
		public void startComeDown(){
			mItemFlipScroller.startComeDown(); 
		}
		/**
		 * 设置是否需要弹性间距动画的开关（默认为开）
		 * @param delay
		 */
		public void setDelayAnim(boolean delay){
			mItemFlipScroller.setDelayAnim(delay);
		}
		
		
		/**
		 * 是否已经结束
		 * @return
		 */
		public boolean isFinished(){
			return mItemFlipScroller.isFinished();
		}
		
		/**
		 * 增加需要增加的item
		 * @param start
		 * @param end
		 */
		public void addView(int start, int end){
			mItemFlipScroller.checkAddView(start, end);
		}
		
		
		/**
		 * 设置监听方法
		 * @param listener
		 */
		public void setOnFlipRunnableListener(OnFlipRunnableListener listener){
			mOnFlipRunnableListener = listener;
		}
		
		
		
		/**
		 * 取得当前item剩余的移动距离
		 * @param index
		 * @param secondIndex
		 * @return
		 */
		public int getFlipItemLeftMoveDistance(int index, int secondIndex){
			if(DEBUG){
				Log.i(TAG, "getFlipItemLeftMoveDistance index="+index+" secondIndex="+secondIndex);
			}
			return mItemFlipScroller.getFlipItemLeftMoveDistance(index, secondIndex);
		}
		
		
		/**
		 * 设置正在启动
		 */
		public void setStartingFlipScroll(){
			mItemFlipScroller.setStartingFlipScroll();
		}
		
		
		public boolean isDown(){
			return mItemFlipScroller.isDown();
		}
		
		/**
		 * 判断是否进入快速滚动模式，并将item进入动画的位置（因为之前重新进行了布局）
		 */
		public void checkFlipFastScroll(){
			if(!mItemFlipScroller.isFinished()){
				int childCount = getChildCount();
				for (int i = childCount - 1; i >= 0; i--) {
					int index = i + getFirstVisiblePosition();
					View childView = getChildAt(i);
					if(index < getHeaderViewsCount()){
						//header
						if(childView != null && childView instanceof FlipGridViewHeaderOrFooterInterface){
							FlipGridViewHeaderOrFooterInterface headerView = (FlipGridViewHeaderOrFooterInterface)childView;
							int headerChildCount = headerView.getHorCount() * headerView.getVerticalCount();
							//取得HeaderView上次的问题
							Integer childBottom = mFlipItemBottomList.get(getFlipItemMapKey(index, -1));
							if(childBottom != null){
								//先处理HeaderView
								int delta = childBottom - childView.getBottom();
								if(DEBUG){
									Log.i(TAG, "checkFlipFastScroll headerView childBottom="+childBottom+" headerView bottom="+childView.getBottom()+" delta="+delta);
								}
								if(delta != 0){
									//这里无需要加入到setFastScrollOffset里面因为HeaderView不参加item的计算，headerChildView才会参加
									//mItemFlipScroller.setFastScrollOffset(index, 0, delta);
									childView.offsetTopAndBottom(delta);
								}
								//再处理HeaderChildView
								for(int headerChildIndex = headerChildCount - 1; headerChildIndex >= 0; headerChildIndex--){
									childBottom = mFlipItemBottomList.get(getFlipItemMapKey(index, headerChildIndex));
									if(childBottom != null){
										View headerChildView = headerView.getView(headerChildIndex);
										if(headerChildView != null){
											delta = childBottom - headerChildView.getBottom();
											if(DEBUG){
												Log.i(TAG, "checkFlipFastScroll headerChild headerChildIndex="+headerChildIndex+
													" childBottom="+childBottom+" headerChildView bottom="+headerChildView.getBottom());
											}
											if(delta != 0){
												mItemFlipScroller.setFastScrollOffset(index, headerChildIndex, delta);
												headerChildView.offsetTopAndBottom(delta);
											}
										}
									}
								}
							}
						}
						else{
							Integer childBottom = mFlipItemBottomList.get(getFlipItemMapKey(index, 0));
							if(childBottom != null){
								int delta = childBottom - childView.getBottom();
								if(delta != 0){
									mItemFlipScroller.setFastScrollOffset(index, 0, delta);
									childView.offsetTopAndBottom(delta);
								}
							}
						}
					}
					else{
						//adapter
						Integer childBottom = mFlipItemBottomList.get(getFlipItemMapKey(index, 0));
						if(DEBUG){
							Log.i(TAG, "checkFlipFastScroll adapter index="+index+
									" childBottom="+childBottom+" currBottom="+childView.getBottom());
						}
						if(childBottom != null){
							int delta = childBottom - childView.getBottom();
							if(delta != 0){
								mItemFlipScroller.setFastScrollOffset(index, 0, delta);
								childView.offsetTopAndBottom(delta);
							}
						}
					}
					}
			}
			else{
				mFlipItemBottomList.clear();
			}
		}
		
		/**
		 * 开始滚动
		 * @param distance
		 */
		public void startScroll(int distance){
			if(mItemFlipScroller.isFinished()){
				mItemFlipScroller.clearChild();
				mItemFlipScroller.setColumnCount(getColumnNum());
				mItemFlipScroller.setHeaderViewInfo(getHeaderViewInfo());
				mItemFlipScroller.setFooterViewInfo(getFooterViewInfo());
				mItemFlipScroller.setTotalItemCount(getCount());
				mItemFlipScroller.addGridView(getFirstVisiblePosition(), getLastVisiblePosition(), distance < 0 ? true : false);
				mItemFlipScroller.startComputDistanceScroll(distance);
				postOnAnimation(this);
				if(mOnFlipRunnableListener != null){
					mOnFlipRunnableListener.onStart();
				}
			}
			else{
				mItemFlipScroller.startComputDistanceScroll(distance);
				if(mOnFlipRunnableListener != null){
					mOnFlipRunnableListener.onStart();
				}
			}
		} 
		
		
		/**
		 * 开始滚动，参数为正式滚动的距离
		 * @param distance
		 */
		public void startRealScroll(int distance){
			if(mItemFlipScroller.isFinished() == false){
				mItemFlipScroller.startRealScroll(distance);
				if(mOnFlipRunnableListener != null){
					mOnFlipRunnableListener.onStart();
				}
			}
		}
		
		
		/**
		 * 当前行的第一个item还剩余的距离
		 * @param index
		 * @return
		 */
		public int getFlipColumnFirstItemLeftMoveDistance(int index){
			if(mItemFlipScroller.isFinished() == false){
				return mItemFlipScroller.getFlipColumnFirstItemLeftMoveDistance(index);
			}
			return 0;
			
		}
		
		@Override
		public void run() {
			setEachFrame(false);
		}
		
		
		/**
		 * 停止
		 */
		public void stop(){
			if(mItemFlipScroller.isFinished() == false){
				setEachFrame(true);
				mItemFlipScroller.finish();
			}
		}
		
		/**
		 * 将列表的header跟footer里面的布局信息合成
		 * @param columnCount
		 * @param verticalCount
		 * @return int
		 */
		public int makeHeaderAndFooterViewInfo(int columnCount, int verticalCount){
			return (columnCount << 16) | verticalCount;
		}
		
		/**
		 * 取得合成的map 键值
		 * @param index
		 * @param secondIndex
		 * @return
		 */
	    private int getFlipItemMapKey(int index, int secondIndex){
	    	//secondIndex max 2^8
	    	//secondIndex -1代表headerView或footerView的父级
	    	return (index << 8) | secondIndex;
	    }
	    
	    
	    /**
	     * 设置每帧动画
	     * @param finishImmediately （是否要马上停止）
	     */
	    private void setEachFrame(boolean finishImmediately){
	    	int preFirst = getFirstVisiblePosition();
			int preLast = getLastVisiblePosition();
			if (mDataChanged) {
				layoutChildren();
			}

			if (mItemCount == 0 || getChildCount() == 0) {
				return;
			}
			
			boolean more;
			if(mItemFlipScroller.isFinished() == false && finishImmediately == true){
				more = true;
			}
			else{
				more = mItemFlipScroller.computeScrollOffset();
			}
			if(more){
				//暂时只取第0个，只能在入场时候才有效
				float moveRatio;
				if(finishImmediately){
					moveRatio = 1.0f;
				}
				else{
					moveRatio = mItemFlipScroller.getFlipItemMoveRatio(0, 0);
				}
				
				int childCount = getChildCount();
				for (int i = childCount - 1; i >= 0; i--) {
					int index = i + getFirstVisiblePosition();
					int delta = 0;
					if(index < getHeaderViewsCount()){
						//header
						View view = getChildAt(i);
						if(mOnFlipRunnableListener != null){
							mOnFlipRunnableListener.onFlipItemRunnable(moveRatio, view, index);
						}
						if(view != null && view instanceof FlipGridViewHeaderOrFooterInterface){
							FlipGridViewHeaderOrFooterInterface headerView = (FlipGridViewHeaderOrFooterInterface)view;
							int headerChildCount = headerView.getHorCount() * headerView.getVerticalCount();
							//HeaderView里面的childView的动画
							int headerViewDelta = 0;//HeaderView整体的移动距离，整体的距离对里面单个的移动有影响
							for(int headerChildIndex = headerChildCount - 1; headerChildIndex >= 0; headerChildIndex--){
								if(finishImmediately){
									delta = mItemFlipScroller.getFlipItemLeftMoveDistance(index, headerChildIndex);
								}
								else{
									delta = mItemFlipScroller.getCurrDelta(index, headerChildIndex);
								}
								if(headerChildIndex >= (headerChildCount - 1)){
									headerViewDelta = delta;
									view.offsetTopAndBottom(headerViewDelta);
									//保存HeaderView的位置
									mFlipItemBottomList.put(getFlipItemMapKey(index, -1), view.getBottom());
								}
								View headerChildView = headerView.getView(headerChildIndex);
								if(headerChildView != null){
									headerChildView.offsetTopAndBottom(delta - headerViewDelta);
									mFlipItemBottomList.put(getFlipItemMapKey(index, headerChildIndex), headerChildView.getBottom());
								}
							}
						}
						else{
							if(finishImmediately){
								delta = mItemFlipScroller.getFlipItemLeftMoveDistance(index, 0);
							}
							else{
								delta = mItemFlipScroller.getCurrDelta(index, 0);
							}
							view.offsetTopAndBottom(delta);
							mFlipItemBottomList.put(getFlipItemMapKey(index, 0), view.getBottom());
						}
					}
					else{
						//adapter
						if(finishImmediately){
							delta = mItemFlipScroller.getFlipItemLeftMoveDistance(index, 0);;
						}
						else{
							delta = mItemFlipScroller.getCurrDelta(index, 0);
						}
						View view = getChildAt(i);
						if(mOnFlipRunnableListener != null){
							mOnFlipRunnableListener.onFlipItemRunnable(moveRatio, view, index);
						}
						if(delta != 0){
							if(DEBUG){
								if(index == 19 ||
										index == 13 ||
										index == 24){
									int before = view.getBottom();
									view.offsetTopAndBottom(delta);
									Log.i(TAG, "run index="+index+" before="+before+
											" delta="+delta+" after="+view.getBottom());
								}
								else{
									view.offsetTopAndBottom(delta);
								}
							}
							else{
								view.offsetTopAndBottom(delta);
							}
						}
						mFlipItemBottomList.put(getFlipItemMapKey(index, 0), view.getBottom());
					}
				}
				
				detachOffScreenChildren(mItemFlipScroller.isDown());
				fillGap(mItemFlipScroller.isDown());
				onScrollChanged(0, 0, 0, 0);
				invalidate();
				postOnAnimation(this);
				mItemFlipScroller.addGridView(getFirstVisiblePosition(), getLastVisiblePosition(), mItemFlipScroller.isDown());
				//取得当前新增的item位置
				int currFirst = getFirstVisiblePosition();
				int currLast = getLastVisiblePosition();
				int headerViewCount = getHeaderViewsCount();
				//在前头增加
				if(currFirst < preFirst){
					if(preFirst <= (headerViewCount - 1)){
						//all header view
						for(int i = preFirst; i <= (headerViewCount - 1); i++){
							int index = i - currFirst;
							if(index >= 0){
								View childView = getChildAt(index);
								if(childView != null && childView instanceof FlipGridViewHeaderOrFooterInterface){
									FlipGridViewHeaderOrFooterInterface headerView = (FlipGridViewHeaderOrFooterInterface)childView;
									mFlipItemBottomList.put(getFlipItemMapKey(i, -1), childView.getBottom());
									int headerChildCount = headerView.getHorCount() * headerView.getVerticalCount();
									for(int headerChildIndex = headerChildCount - 1; headerChildIndex >= 0; headerChildIndex--){
										View headerChildView = headerView.getView(headerChildIndex);
										if(headerChildView != null){
											mFlipItemBottomList.put(getFlipItemMapKey(i, headerChildIndex), headerChildView.getBottom());
										}
									}
								}
								else{
									mFlipItemBottomList.put(getFlipItemMapKey(i, 0), childView.getBottom());
								}
							}
						}
					}
					else if(preFirst > (headerViewCount - 1) && currFirst <= (headerViewCount - 1)){
						//add header view
						for(int i = currFirst; i <= (headerViewCount - 1); i++){
							int index = i - currFirst;
							if(index >= 0){
								View childView = getChildAt(index);
								if(childView != null && childView instanceof FlipGridViewHeaderOrFooterInterface){
									FlipGridViewHeaderOrFooterInterface headerView = (FlipGridViewHeaderOrFooterInterface)childView;
									mFlipItemBottomList.put(getFlipItemMapKey(i, -1), childView.getBottom());
									int headerChildCount = headerView.getHorCount() * headerView.getVerticalCount();
									for(int headerChildIndex = headerChildCount - 1; headerChildIndex >= 0; headerChildIndex--){
										View headerChildView = headerView.getView(headerChildIndex);
										if(headerChildView != null){
											mFlipItemBottomList.put(getFlipItemMapKey(i, headerChildIndex), headerChildView.getBottom());
										}
									}
								}
								else{
									mFlipItemBottomList.put(getFlipItemMapKey(i, 0), childView.getBottom());
								}
							}
						}
						//add adapter view
						for(int i = (headerViewCount - 1) + 1; i < preFirst; i++){
							int index = i - currFirst;
							if(index >= 0){
								mFlipItemBottomList.put(getFlipItemMapKey(i, 0), getChildAt(index).getBottom());
							}
						}
					}
					else{
						//all adapter
						for(int i = currFirst; i < preFirst; i++){
							int index = i - currFirst;
							if(index >= 0){
								mFlipItemBottomList.put(getFlipItemMapKey(i, 0), getChildAt(index).getBottom());
							}
						}
					}
				}
				
				//在末尾增加
				if(currLast > preLast){
					for(int i = preLast + 1; i <= currLast; i++){
						int index = i - currFirst;
						if(index >= 0){
							mFlipItemBottomList.put(getFlipItemMapKey(i, 0), getChildAt(index).getBottom());
						}
					}
				}
				
			}
	    }
	    
	}
	
	
	/**
	 * headerView或者footerView加入到弹性距离里面需要实现的接口
	 * @author tim
	 *
	 */
	public static interface FlipGridViewHeaderOrFooterInterface{
		/**
		 * 行数
		 * @return
		 */
		public int getHorCount();
		/**
		 * 列数
		 * @return
		 */
		public int getVerticalCount();
		/**
		 * 取得指定的需要参加到弹性间距的view
		 * @param index
		 * @return
		 */
		public View getView(int index);
		/**
		 * 取得指定view的序号
		 * @param view
		 * @return
		 */
		public int getViewIndex(View view);
	}

	
	/**
	 * 弹性间距动画的回调监听器
	 * @author tim
	 *
	 */
	public static interface OnFlipRunnableListener{
		/**
		 * 开始
		 */
    	public void onStart();
    	/**
    	 * 动画过程（每帧的动画都有会调用，不要在这个方法里做太多的处理）
    	 * @param moveRatio
    	 * @param itemView
    	 * @param index
    	 */
    	public void onFlipItemRunnable(float moveRatio, View itemView, int index);
    	/**
    	 * 动画结束
    	 */
    	public void onFinished();
    }

	public void setHor_delay_distance(int hor_delay_distance) {
		mFlipRunnable.setHor_delay_distance(hor_delay_distance);
	}

	public void setVer_delay_distance(int ver_delay_distance) {
		mFlipRunnable.setVer_delay_distance(ver_delay_distance);
	}

	public void setMin_fast_setp_discance(int min_fast_setp_discance) {
		mFlipRunnable.setMin_fast_setp_discance(min_fast_setp_discance);
	}

	public void setFlip_scroll_frame_count(int flip_scroll_frame_count) {
		mFlipRunnable.setFlip_scroll_frame_count(flip_scroll_frame_count);
	}

	public void setHor_delay_frame_count(int hor_delay_frame_count) {
		mFlipRunnable.setHor_delay_frame_count(hor_delay_frame_count);
	}

	public void setVer_delay_frame_count(int ver_delay_frame_count) {
		mFlipRunnable.setVer_delay_frame_count(ver_delay_frame_count);
	}
}

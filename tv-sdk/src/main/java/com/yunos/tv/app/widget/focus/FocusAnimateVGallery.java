package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Scroller;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;

import java.util.ArrayList;
import java.util.List;

public class FocusAnimateVGallery extends FocusVGallery implements ItemSelectedListener, DeepListener{
	protected final String TAG = this.getClass().getSimpleName();
	protected static final boolean DEBUG = false;
	
	private static final int EXPAND = 2001;
	private static final int COLLIPSE = 2002;
	
	AnimateRunnable mAnimateRunnable = new AnimateRunnable();
	int mFlingDuration = 300;
	protected boolean mCollipseWhenLostFocus = true;
	protected boolean mIsCurrentExpand = false;
	List<FlingItem> mItems;
	protected boolean mGainFocus = false;
	
	protected boolean mExpandMode = true;
	protected long mDelayTime = 200;
	protected boolean mPrepareExpand;
	
	private void clearItems() {
		if (isEmptyList(mItems))
			return;

		FlingItem item = null;
		int count = mItems.size();
		for (int i = 0; i < count; i++) {
			item = mItems.get(i);
			if (item != null) {
				item.clearItem();
			}
		}
		mItems.clear();
	}
	
	protected boolean initItems() {
		if (mItems == null) {
			mItems = new ArrayList<FlingItem>();
		} else {
			clearItems();
		}
		
		int childCount = getChildCount();
		if (childCount <= 0)
			return false;

		FlingItem item = null;
		View child = null;
		int childCenterY = 0;
		int selectCenterY = getHeight() / 2;
		for (int i = 0; i < childCount; i++) {
			child = getChildAt(i);
			if (child != null) {
				item = new FlingItem(child);
				childCenterY = getItemCenterY(child);
				item.resetItem(0, childCenterY - selectCenterY);
				if(selectCenterY != 0){
					float alpha = 1-1.0f * Math.abs(childCenterY-selectCenterY) / selectCenterY;
					if(alpha < 0) alpha = 0;
					item.setMaxAlpha(alpha);
				}
				mItems.add(item);
			}
		}

		return true;
	}
	
	@Override
	protected void reportScrollStateChange(int newState) {
		super.reportScrollStateChange(newState);
		if (newState == OnScrollListener.SCROLL_STATE_IDLE) {
			adjustViewsTopAndBottom();
			initItems();
			if(mAnimateRunnable.getMode() == EXPAND){
				setExpand();
			} else {
				//setCollipse();
			}
		}
	}
	
	private void adjustViewsTopAndBottom() {
		int selectCenterY = getHeight() / 2;
		int childCenterY = getSelectedItemCenterY();
		
		int delta = selectCenterY - childCenterY;
		offsetChildrenTopAndBottom(delta);
	}

	protected void setExpand(){
		int childCount = mItems == null ? 0 : mItems.size();
		if (childCount <= 0)
			return ;
		
		FlingItem item = null;
		FlingItem itemFling = null;
		int selectCenterY = getHeight() / 2;
		ArrayList<FlingItem> list = new ArrayList<FlingItem>();
		for (int i = 0; i < childCount; i++) {
			itemFling = mItems.get(i);
			item = new FlingItem(itemFling.mChild);
			list.add(item);
			item.setMode(EXPAND);
			item.setMaxAlpha(itemFling.getMaxAlpha());
			item.setCurrentY(getItemCenterY(itemFling.mChild) - selectCenterY);
			item.setDuration(mFlingDuration);
		}
		
		if (!isEmptyList(list)) {
			mAnimateRunnable.clearFlingItems();
			mAnimateRunnable.setFlingItems(list);
		}
	}
	
	private boolean initFlingItems(int mode) {
		int childCount = mItems == null ? 0 : mItems.size();
		if (childCount <= 0)
			return false;

		FlingItem item = null;
		FlingItem itemFling = null;
		int selectCenterY = getHeight() / 2;
		ArrayList<FlingItem> list = new ArrayList<FlingItem>();
		for (int i = 0; i < childCount; i++) {
			itemFling = mItems.get(i);
			if (mode == EXPAND) {
				item = new FlingItem(itemFling.mChild);
				list.add(item);
				item.setMaxAlpha(itemFling.getMaxAlpha());
				item.resetItem(0, itemFling.getEndY());
				item.setCurrentY(getItemCenterY(itemFling.mChild) - selectCenterY);
				item.setDuration(mFlingDuration);
			} else {
				/*item = mAnimateRunnable.getItem(i);
				if(item != null){
					item.setMaxAlpha(1 - 1.0f * Math.abs(getItemCenterY(itemFling.mChild) - selectCenterY) / selectCenterY);
				}*/
			}
		}

		if (!isEmptyList(list)) {
			mAnimateRunnable.clearFlingItems();
			mAnimateRunnable.setFlingItems(list);
			return true;
		}

		return false;
	}
	
	protected void setCollipse() {
		
		if(mAnimateRunnable != null){
			mAnimateRunnable.setMode(COLLIPSE);
		}
		
		int childCount = mItems == null ? 0 : mItems.size();
		if (childCount <= 0)
			return;

		FlingItem itemFling = null;
		int selectIndex = getSelectedItemPosition() - getFirstVisiblePosition();
		for (int i = 0; i < childCount; i++) {
			if (i == selectIndex) {
				continue;
			}
			itemFling = mItems.get(i);
			if (itemFling != null && itemFling.mChild != null) {
				itemFling.offsetTopAndBottom(-itemFling.getEndY());
				itemFling.mChild.setAlpha(0);
			}
		}
	}
	
	public void setExpandMode(boolean expand){
		mExpandMode = expand;
	}
	
	public void setDelayTime(long time){
		mDelayTime = time;
	}
	
	public void expand() {
		if(!mLayouted || !mExpandMode){
			return ;
		}
		
		if(mIsCurrentExpand)
			return;

		if(!mAnimateRunnable.isFinished()){
			mAnimateRunnable.forceFinished();
		}
		
		if(getFlingRunnable() != null && !getFlingRunnable().isFinished()){
			getFlingRunnable().stop(false);
		}
		
		mIsCurrentExpand = true;
		
		int distance = getChildDistance();
		initFlingItems(EXPAND);
		mAnimateRunnable.setDuratioin(mFlingDuration);
		mAnimateRunnable.expand(distance);
	}
	
	int getSelectedItemCenterY(){
		return getItemCenterY(getSelectedItemPosition() - getFirstVisiblePosition());
	}
	
	private int getItemCenterY(int itemPosition){
		View child = null; 
		int position = itemPosition;
		if(position >= 0 && position < getChildCount()) {
			child = getChildAt(position);
		}
		return getItemCenterY(child);
	}
	
	private int getItemCenterY(View child){
		int centerY = 0;
		if(child != null){
			centerY = (child.getTop() + child.getBottom()) / 2;
		}
		return centerY;
	}
	
	private int getChildDistance(){
		int selectedCenterY = getHeight() / 2;
		int firstItemCenterY = selectedCenterY;
		int lastItemCenterY = selectedCenterY;
		if(!isEmptyList(mItems)){
			firstItemCenterY = Math.abs(mItems.get(0).getEndY());
			lastItemCenterY = Math.abs(mItems.get(mItems.size() - 1).getEndY());
		}
		int distanceUp = Math.abs(selectedCenterY - firstItemCenterY);
		int distanceDown = Math.abs(selectedCenterY - lastItemCenterY);
		return Math.max(distanceUp, distanceDown);
	}
	
	public void collipse() {
		if (!(mLayouted && mCollipseWhenLostFocus) || !mExpandMode)
			return ;
		
		if(!mIsCurrentExpand)
			return ;
		if(!mAnimateRunnable.isFinished()){
			mAnimateRunnable.forceFinished();
		}
		if(getFlingRunnable() != null && !getFlingRunnable().isFinished()){
			getFlingRunnable().stop(false);
		}
		
		mIsCurrentExpand = false;
		//initFlingItems(COLLIPSE);
		mAnimateRunnable.setDuratioin(mFlingDuration);
		mAnimateRunnable.collipse();
	}

	public void setCollipseWhenLostFocus(boolean collipse) {
		mCollipseWhenLostFocus = collipse;
	}
	
	public FocusAnimateVGallery(Context context) {
		super(context);
	}

	public FocusAnimateVGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FocusAnimateVGallery(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setStaticTransformationsEnabledInGallery() {
		setStaticTransformationsEnabled(true);
	}

	/**
	 * Draw one child of this V Gallery. This method is responsible for getting
	 * the canvas in the right state. This includes clipping, translating so
	 * that the child's scrolled origin is at 0, 0, and applying any animation
	 * transformations.
	 *
	 * @param canvas
	 *            The canvas on which to draw the child
	 * @param child
	 *            Who to draw
	 * @param drawingTime
	 *            The time at which draw is occuring
	 * @return True if an invalidate() was issued
	 */
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

		if(isMove()){
			return super.drawChild(canvas, child, drawingTime);
		}
		
		int SelectedIndex = this.getSelectedItemPosition();
		int CurrentIndex = this.getPositionForView(child);
		
		int center = getHeight() / 2;
		if(center != 0){
			int childCenter = (child.getTop() + child.getBottom()) / 2;
			int distance = Math.abs(childCenter - center);
			if(distance == 0){
				if(CurrentIndex == SelectedIndex){
					child.setAlpha(1 - 1.0f * distance / center);
				} else {
					child.setAlpha(0);
				}
			} else {
				child.setAlpha(1 - 1.0f * distance / center);
			}
		}
		
		return super.drawChild(canvas, child, drawingTime);
	}

	@Override
	public boolean isFocusBackground(){
		return true;
	}
	
	@Override
	public void onItemSelected(View v, int position, boolean isSelected,
			View view) {

	}
	
	/**
	 * 是否在做expand 或 collipse;
	 * 
	 * @return
	 */
	public boolean isMove() {
		return !mAnimateRunnable.isFinished();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		initItems();
	}
	
	public boolean checkState(int keyCode) {
		if(super.checkState(keyCode)){
			return true;
		}
		
		if(isMove() || mPrepareExpand){
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				return true;
			}
		}
		
		return false;
	}
	
	class FlingItem implements Runnable{
		int mCurrentY = 0;
		int mStartFlingY = 0;
		int mEndFlingY = 0;
		View mChild = null;
		int mMode = COLLIPSE;
		Scroller mScroller;
		int mDuration = 0;
		int mDeltaY = 0;
		float mMaxAlpha = 1;
		
		public FlingItem(View child){
			setChild(child);
			mScroller = new Scroller(getContext(), new AccelerateDecelerateFrameInterpolator());
		}
		
		public void setChild(View child) {
			mChild = child;
			if(mChild == null){
				throw new NullPointerException("FlingItem child is must not be null.");
			}
		}
		
		public void setDuration(int duration){
			mDuration = duration;
		}
		
		public int getStartY(){
			return mStartFlingY;
		}
		
		public void setStartY(int y){
			mStartFlingY = y;
		}
		
		public int getEndY(){
			return mEndFlingY;
		}
		
		public void setEndY(int y){
			mEndFlingY = y;
		}
		
		public int getCurrentY(){
			return mCurrentY;
		}
		
		public void setCurrentY(int y){
			mCurrentY = y;
		}
		
		public float getMaxAlpha(){
			return mMaxAlpha;
		}
		
		public void setMaxAlpha(float alpha){
			mMaxAlpha = alpha;
		}
		
		public void offsetTopAndBottom(int offset){
			
			int distanceY = mScroller.getCurrY();
			if(Math.abs(mDeltaY) == 0){
				if(mMaxAlpha >= 1){
					mChild.setAlpha(1);
				} else {
					mChild.setAlpha(0);
				}
			} else {
				mChild.setAlpha(mMaxAlpha * Math.abs(1.0f * distanceY / mDeltaY));
			}
			
			mChild.offsetTopAndBottom(offset);
		}
		
		public void setMode(int mode){
			mMode = mode;
		}
		public void resetItem(int startY, int endY){
			mStartFlingY = startY;
			mEndFlingY = endY;
		}
		
		public void clearItem(){
			mChild = null;
			mScroller = null;
		}
		
		public void expand(){
			if(mMode == EXPAND) return;
			mMode = EXPAND;
			mDeltaY = mEndFlingY - (mCurrentY + mStartFlingY);
			mScroller.startScroll(0, mStartFlingY + mCurrentY, 0, mEndFlingY - (mCurrentY + mStartFlingY), mDuration);
		}
		
		public void collipse(){
			if(mMode == COLLIPSE) return;
			mMode = COLLIPSE;
			mDeltaY = mStartFlingY - mCurrentY;
			mScroller.startScroll(0, mCurrentY, 0, mStartFlingY - mCurrentY, mDuration);
			if(DEBUG){
				Log.d(TAG, "mMode 11== COLLIPSE start Y = " + mCurrentY + " mStartFlingY = " + mStartFlingY + " hascode = " + this.hashCode());
			}
		}
		
		public void onFling(int y){
			offsetTopAndBottom( y - mCurrentY );
			if(DEBUG){
				Log.d(TAG, "mMode 11== COLLIPSE " + " current = " + y + "  offset = " + ( y - mCurrentY ) + " hascode = " + this.hashCode());
			}
			mCurrentY = y;
		}
		
		@Override
		public void run(){
			boolean more = mScroller.computeScrollOffset();
			if(more){
				onFling(mScroller.getCurrY());
			}
			if(DEBUG){
				Log.d(TAG, "Fling item Y = " + mScroller.getCurrY() + " hascode = " + this.hashCode() + " mode = " + mMode);
			}
		}

		public void forceFinished() {
			mScroller.abortAnimation();
		}
	}
	
	class AnimateRunnable implements Runnable {

		private Scroller mScroller;
		private int mMode = EXPAND;

		private int mStartFlingY = 0;
		private int mEndFlingY = 100;
		private int mCurrentY = mStartFlingY;
		private int mDuration = 5000;
		
		private ArrayList<FlingItem> mFlingItems;

		public AnimateRunnable() {
			mScroller = new Scroller(getContext(), new AccelerateDecelerateFrameInterpolator());
			mFlingItems = new ArrayList<FlingItem>();
		}
		
		public int getMode(){
			return mMode;
		}
		
		public void setMode(int mode){
			mMode = mode;
		}
		
		public void reset(){
			clearFlingItems();
		}
		
		public void clearFlingItems(){
			if(!isEmptyList(mFlingItems)){
				mFlingItems.clear();
			}
		}
		
		public FlingItem getItem(int index){
			if(index < 0 || index >= mFlingItems.size()) return null;
			return mFlingItems.get(index);
		}
		
		public void setFlingItems(List<FlingItem> list){
			if(!isEmptyList(list)){
				mFlingItems.addAll(list);
			}
		}
		
		public void setDuratioin(int duration) {
			mDuration = duration;
		}
		
		public void setEndFlingY(int value){
			mEndFlingY = value;
		}

		public void expand(int value) {
			removeCallbacks(this);
			if (mMode == EXPAND) {
				Log.d("test", "drop expand"+" mode:" + mMode + ";finish:"+isFinished()+"X:"+mScroller.getCurrX()+";id:"+getId());
				return;
			}
			mMode = EXPAND;
			mEndFlingY = value;
			mScroller.startScroll(0, mCurrentY + mStartFlingY, 0, mEndFlingY - (mCurrentY + mStartFlingY), mDuration);
			onExpand();
			post(this);
		}

		public void collipse() {
			removeCallbacks(this);
			if (mMode == COLLIPSE) {
				Log.d("test", "drop collipse"+" mode:" + mMode + ";finish:"+isFinished()+"X:"+mScroller.getCurrX()+";id:"+getId());
				return;
			}
			mMode = COLLIPSE;
			mScroller.startScroll(0, mCurrentY, 0, mCurrentY - mStartFlingY, mDuration);
			onCollipse();
			post(this);
		}
		
		private void onExpand(){
			int flingItemCount = mFlingItems.size();
			FlingItem item = null;
			for(int i = 0; i < flingItemCount; i++){
				item = mFlingItems.get(i);
				item.expand();
			}
		}
		
		private void onCollipse(){
			int flingItemCount = mFlingItems.size();
			FlingItem item = null;
			for(int i = 0; i < flingItemCount; i++){
				item = mFlingItems.get(i);
				item.collipse();
			}
		}
		
		private void onFinished(){
			int flingItemCount = mFlingItems.size();
			FlingItem item = null;
			for(int i = 0; i < flingItemCount; i++){
				item = mFlingItems.get(i);
				item.forceFinished();
			}
		}

		private void onFling() {
			int flingItemCount = mFlingItems.size();
			FlingItem item = null;
			for(int i = 0; i < flingItemCount; i++){
				item = mFlingItems.get(i);
				item.run();
			}
		}

		public boolean isFinished() {
			return mScroller.isFinished();
		}

		@Override
		public void run() {
			boolean more = mScroller.computeScrollOffset();
			onFling();
			final int y = mScroller.getCurrY();
			mCurrentY = y;
			if (more) {
				invalidate();
				post(this);
			} else {
				mPrepareExpand = false;
			}
		}
		
		public void forceFinished(){
			if(mScroller != null){
				mScroller.abortAnimation();
				onFinished();
			}
			post(this);
		}

	}

	public boolean isEmptyList(List<FlingItem> list) {
		return list == null || list.size() == 0;
	}

}

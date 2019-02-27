/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

public class CircleVGallery extends AbsGallery implements DeepListener, ItemListener {

    protected Params mParams = new Params(1.0f, 1.0f, 5, null, true, 5, null);
    private FocusRectParams mFocusRectparams;

    private ItemSelectedListener mItemSelectedListener;

    private static final String TAG = "CircleVGallery";

    private static final boolean localLOGV = false;

    /**
     * Left most edge of a child seen so far during layout.
     */
    private int mTopMost;

    /**
     * Right most edge of a child seen so far during layout.
     */
    private int mBottomMost;

    /**
     * Executes the delta scrolls from a fling or scroll movement.
     */
    private FlingRunnable mFlingRunnable = new FlingRunnable();

    /**
     * If true, mFirstPosition is the position of the rightmost child, and the
     * children are ordered right to left.
     */
    private boolean mIsRtl = true;
    
    /**
     * 最小的scale
     */
    private float mMinScale = 0.2f;
    /**
     * 最透明的值
     */
    private float mMinAlpha = 0.5f;
    
    /**
     * 显示的个数
     */
    private int mVisibleCount = 7;
    
    private Rect mRect = new Rect();
    
    public float getMinScale(){
        return mMinScale;
    }
    
    public void setMinScale(float scale){
        mMinScale = scale;
        requestLayout();
    }
    
    public float getMinAlpha(){
        return mMinAlpha;
    }
    
    public void setMinAlpha(float alpha){
        mMinAlpha = alpha;
        requestLayout();
    }
    
    public int getVisibleCount(){
        return mVisibleCount;
    }
    
    public void setVisibleCount(int count){
        mVisibleCount = count;
        requestLayout();
    }
    
    
    /**
     * 是否循环显示
     */
    private boolean mIsLoop = false;
    
    public CircleVGallery(Context context) {
        super(context);
        init(context);
    }

    public CircleVGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleVGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    private void init(Context context) {
        mGravity = Gravity.CENTER_HORIZONTAL;
        setChildrenDrawingOrderEnabled(true);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
//        Log.d(TAG,"dispatchDraw1");
        super.dispatchDraw(canvas);
//        Log.d(TAG,"dispatchDraw2");
    }
    
//    @Override
//    protected void onDraw(Canvas canvas) {
////        Log.d(TAG,"onDraw");
//        super.onDraw(canvas);
//    }
    
//    @Override
//    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
//        Log.d(TAG,"drawChild");
//        return super.drawChild(canvas, child, drawingTime);
//    }
//    
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int selected = getSelectedItemPosition() - getFirstVisiblePosition();
        if (selected < 0) {
            return i;
        }
        return i < selected ? i : childCount - 1 - i + selected;
    }

    @Override
    protected int computeVerticalScrollExtent() {
        // Only 1 item is considered to be selected
        return 1;
    }

    @Override
    protected int computeVerticalScrollOffset() {
        // Current scroll position is the same as the selected position
        return mSelectedPosition;
    }

    @Override
    protected int computeVerticalScrollRange() {
        // Scroll range is the same as the item count
        return mItemCount;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        /*
         * Remember that we are in layout to prevent more layout request from
         * being generated.
         */
        mInLayout = true;
        layout(0, false);
        mInLayout = false;
    }

    @Override
    int getChildWidth(View child) {
        return child.getMeasuredWidth();
    }
    
    
    private int mNextSelectedOffseted = 0;
    private int mSelectedOffseted = 0;
    
    private void offsetChildrenTopAndBottomByStep(int offset,boolean toUp){
        Log.d(TAG,"offsetChildrenTopAndBottomByStep:" + offset);
        if(offset == 0){
            return;
        }
        int itemSpacing = mSpacing;
        View sel = getSelectedView();
        if(sel != null){
            sel.offsetTopAndBottom(offset);
            setChildScale(sel,0,getSelectedItemPosition());
            setChildAlpha(sel,0);
            mSelectedOffseted += offset;
            
            final int childTop = sel.getTop();
            final int childCenter = sel.getHeight() / 2;
            final int galleryCenter = getHeight() / 2;
            mSelectedCenterOffset = childTop + childCenter - galleryCenter;
            
            View prevView = sel;
            for(int i = mSelectedPosition + 1; i < getCount(); i++){ //填充下面
                View curView = getChildAt(i - mFirstPosition);
                if(curView != null){
                    int posOffset = i - mSelectedPosition;
//                    int itemTop = prevView.getTop() + (int)(itemSpacing * scale);
                    int spacing = getItemSpacing(prevView, posOffset);
                    int itemTop = prevView.getTop() + spacing;
                    int maxDownCount = mVisibleCount / 2;
//                    if(toUp){   //下面新出来
//                        if(i - mNextScrollSelectedPosition == maxDownCount){
//                            int prevBottom = prevView.getBottom();
//                            int itemBottom = 2 * mMaxBottom - spacing - prevBottom;
//                            itemTop = itemBottom + curView.getHeight();
////                            spacing *= -1;
//                            Log.d(TAG,"filldown last item1 index:" + i + " spacing:" + spacing);
//                        }
//                    } else {
//                        if(i - mNextScrollSelectedPosition == maxDownCount + 1){
//                            spacing *= -1;
//                            Log.d(TAG,"filldown last item2 index:" + i + " spacing:" + spacing);
//                        }
//                    }
                    
                    if(mFlingRunnable.isRunning() 
                            && ((offset < 0 && i - mNextScrollSelectedPosition == maxDownCount) 
                                    || (offset > 0 && i - mNextScrollSelectedPosition == maxDownCount + 1))){
                        int prevBottom = prevView.getBottom();
                        int itemBottom = 2 * mMaxBottom - spacing - prevBottom;
                        itemTop = itemBottom - curView.getHeight();
                        Log.d(TAG,"last item index:" + i + ",mNextScrollSelectedPosition=" + mNextScrollSelectedPosition + ",target:(" + itemTop + "," + itemBottom + "),prevBottom:" + prevBottom + ",offset:" + (itemTop - curView.getTop()));
                    }

                    int itemOffset = itemTop - curView.getTop();
                    Log.d(TAG,"offset1 index:" + i + ",selOffset:" + offset 
                            + ",prevTop:" + prevView.getTop() + ",itemTargetTop:" + itemTop + ",ItemCurrentTop:" + curView.getTop()
                            + ",itemOffset:" + itemOffset
                            + ",getItemSpacing:" + getItemSpacing(prevView, posOffset));
//                    itemOffset = offset;
//                    if( (toUp && i - mNextScrollSelectedPosition == maxDownCount) || (i - mNextScrollSelectedPosition == maxDownCount + 1) ){
//                        itemOffset *= -1;
//                    }
//                    Log.d(TAG,"filldown last item1 index:" + i + " offset:" + itemOffset);
                    curView.offsetTopAndBottom(itemOffset);
                    setChildScale(curView,posOffset,i);
                    setChildAlpha(curView,posOffset);

                    if(mNextScrollSelectedPosition == i){
                        mNextSelectedOffseted += itemOffset;
                    }
                }
                prevView = curView;
            }
            
            prevView = sel;
            for(int i = mSelectedPosition -1; i >= 0; i--){   //填充上面
                View curView = getChildAt(i - mFirstPosition);
                if(curView != null){
                    int posOffset = i - mSelectedPosition;
//                    int itemBottom = prevView.getBottom() - (int)(itemSpacing * scale);
                    int spacing = getItemSpacing(prevView, posOffset);
                    int itemBottom = prevView.getBottom() - spacing;
                    int maxUpCount =  (mVisibleCount - 1) / 2;
//                    if(toUp){
//                        if(mNextScrollSelectedPosition - i == maxUpCount + 1){
//                            spacing *= -1;
//                            Log.d(TAG,"fillup last item1 index:" + i + " spacing:" + spacing);
//                        }
//                    } else {
//                        if(mNextScrollSelectedPosition - i == maxUpCount){
//                            spacing *= -1;
//                            Log.d(TAG,"fillup last item2 index:" + i + " spacing:" + spacing);
//                        }
//                    }
//                    
                    if(mFlingRunnable.isRunning() && 
                            ((offset < 0 && mNextScrollSelectedPosition - i == maxUpCount + 1) 
                                    || (offset > 0 && mNextScrollSelectedPosition - i == maxUpCount))){
                        int prevTop = prevView.getTop();
                        int itemTop = spacing + 2 * mMaxTop  - prevTop;
                        itemBottom = itemTop + curView.getHeight();

                        Log.d(TAG,"bbccaa index:" + i + ",next=" + mNextScrollSelectedPosition 
                                + ",target:(" + itemTop + "," + itemBottom + "),prevTop:" + prevTop 
                                + ",offset:" + (itemBottom - curView.getBottom()) + ",toUp:" + toUp + ",scroll:" + mFlingRunnable.isRunning());
                    }
                    
                    int itemOffset = itemBottom - curView.getBottom();
//                    int itemOffset = (int)(offset * scale);
//                    Log.d(TAG,"offset2 index:" + i + ",mNextScrollSelectedPosition=" + mNextScrollSelectedPosition + ",offset:" + itemOffset + ",itemBottom:" + itemBottom + ",currentBottom:" + curView.getBottom() + ",prevBottom:" + prevView.getBottom());
                    Log.d(TAG,"bbccaa index:" + i+ ",itemOffset:" + itemOffset);
                    curView.offsetTopAndBottom(itemOffset);
                    setChildScale(curView,posOffset,i);
                    setChildAlpha(curView,posOffset);
                    
                    if(mNextScrollSelectedPosition == i){
                        mNextSelectedOffseted += itemOffset;
                    }
                }
                prevView = curView;
            }
//            for (int i = getChildCount() - 1; i >= 0; i--) {
//                if(i != mSelectedPosition){
//                }
//                getChildAt(i).offsetTopAndBottom(offset);
//            }
        } else {
            Log.w(TAG,"sel is null!");
        }
    }
    
    /**
     * Tracks a motion scroll. In reality, this is used to do just about any
     * movement to items (touch scroll, arrow-key scroll, set an item as
     * selected).
     * 
     * @param deltaX
     *            Change in X from the previous event.
     */
    void trackMotionScroll(int deltaY) {

        if (getChildCount() == 0) {
            return;
        }

        boolean toUp = deltaY < 0;

        int limitedDeltaY = getLimitedMotionScrollAmount(toUp, deltaY);
        if (limitedDeltaY != deltaY) {
            // The above call returned a limited amount, so stop any
            // scrolls/flings
            mFlingRunnable.endFling(false);
            onFinishedMovement();
        }

        Log.d(TAG,"trackMotionScroll:deltaY:" + deltaY + ",limitedDeltaY:" + limitedDeltaY);
//        offsetChildrenTopAndBottom(limitedDeltaY);
        offsetChildrenTopAndBottomByStep(limitedDeltaY,toUp);

        detachOffScreenChildren(toUp);

        if (toUp) {
            // If moved left, there will be empty space on the right
            fillToGalleryDown();
        } else {
            // Similarly, empty space on the left
            fillToGalleryUp();
        }
        
        // Clear unused views
        mRecycler.clear();

        setSelectionToCenterChild();

//        final View selChild = mSelectedChild;
//        if (selChild != null) {
//            final int childTop = selChild.getTop();
//            final int childCenter = selChild.getHeight() / 2;
//            final int galleryCenter = getHeight() / 2;
//            mSelectedCenterOffset = childTop + childCenter - galleryCenter;
//        }

        onScrollChanged(0, 0, 0, 0); // dummy values, View's implementation does
                                        // not use these.

        invalidate();
    }

    int getLimitedMotionScrollAmount(boolean motionToUp, int deltaY) {
        int extremeItemPosition = motionToUp != mIsRtl ? mItemCount - 1 : 0;
        View extremeChild = getChildAt(extremeItemPosition - mFirstPosition);

        if (extremeChild == null) {
            return deltaY;
        }

        int extremeChildCenter = getCenterOfView(extremeChild);
        int galleryCenter = getCenterOfGallery();

        if (motionToUp) {
            if (extremeChildCenter <= galleryCenter) {

                // The extreme child is past his boundary point!
                return 0;
            }
        } else {
            if (extremeChildCenter >= galleryCenter) {

                // The extreme child is past his boundary point!
                return 0;
            }
        }

        int centerDifference = galleryCenter - extremeChildCenter;

        return motionToUp ? Math.max(centerDifference, deltaY) : Math.min(centerDifference, deltaY);
    }

    /**
     * @return The center of this Gallery.
     */
    private int getCenterOfGallery() {
        return (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingBottom();
    }

    /**
     * @return The center of the given view.
     */
    private static int getCenterOfView(View view) {
        return view.getTop() + view.getHeight() / 2;
    }

    /**
     * Detaches children that are off the screen (i.e.: Gallery bounds).
     * 
     * @param toUp
     *            Whether to detach children to the left of the Gallery, or to
     *            the right.
     */
    private void detachOffScreenChildren(boolean toUp) {
        if(mFlingRunnable.isRunning()){
            return;
        }
        int numChildren = getChildCount();
        int firstPosition = mFirstPosition;
        int start = 0;
        int count = 0;
        int maxDownCount = (mVisibleCount - 1) / 2;
        int maxUpCount = mVisibleCount / 2;
        
        if (mDirection == DIRECTION_DOWN) { //清理上方的内容
//            final int galleryTop = getPaddingTop();
            for (int i = 0; i < numChildren; i++) {
//                getSelectedItemPosition() - curPosition <= maxDownCount
//                int n = mIsRtl ? (numChildren - 1 - i) : i;
//                int n = i;
//                final View child = getChildAt(i);
//                
//                if (child.getBottom() >= galleryTop && mNextScrollSelectedPosition - getFirstVisiblePosition() - n < maxDownCount) {
//                    break;
//                } else {
//                    child.setVisibility(View.INVISIBLE);
////                    start = n;
////                    count++;
////                    mRecycler.put(firstPosition + n, child);
//                }
                
                final View child = getChildAt(i);
                if(mNextScrollSelectedPosition - (getFirstVisiblePosition() + i ) > maxDownCount){
                    start = i;
                    count++;
                    mRecycler.put(firstPosition + i, child);
                    Log.d(TAG,"detach start1:" + start);
                } else {
                    break;
                }
            }
            
        } else {//清理下方的内容
//            final int galleryBottom = getHeight() - getPaddingBottom();
            for (int i = numChildren - 1; i >= 0; i--) {
//                int n = mIsRtl ? numChildren - 1 - i : i;
//                final View child = getChildAt(n);
//                if (child.getTop() <= galleryBottom && n + getFirstVisiblePosition() - mNextScrollSelectedPosition < maxDownCount) {
//                    break;
//                } else {
//                    child.setVisibility(View.INVISIBLE);
////                    start = n;
////                    count++;
////                    mRecycler.put(firstPosition + n, child);
//                }
                
                final View child = getChildAt(i);
                if((getFirstVisiblePosition() + i ) - mNextScrollSelectedPosition > maxDownCount){
                    start = i;
                    count++;
                    mRecycler.put(firstPosition + i, child);
                    Log.d(TAG,"detach start2:" + start);
                } else {
                    break;
                }
            }
        }

        Log.d(TAG,"detach start:" + start + ",count:" + count + ",scrolling:" + mFlingRunnable.isRunning());
        detachViewsFromParent(start, count);
        

        if (mDirection == DIRECTION_DOWN) {
            mFirstPosition += count;
        }
    }

    /**
     * Scrolls the items so that the selected item is in its 'slot' (its center
     * is the gallery's center).
     */
    private void scrollIntoSlots() {

        if (getChildCount() == 0 || mSelectedChild == null)
            return;

        int selectedCenter = getCenterOfView(mSelectedChild);
        int targetCenter = getCenterOfGallery();

        int scrollAmount = targetCenter - selectedCenter;
        if (scrollAmount != 0) {
            Log.d(TAG,"scrollIntoSlots:" + scrollAmount);
            mFlingRunnable.startUsingDistance(scrollAmount);
        } else {
            onFinishedMovement();
        }
    }

    /**
     * Looks for the child that is closest to the center and sets it as the
     * selected child.
     */
    private void setSelectionToCenterChild() {

        View selView = mSelectedChild;
        if (mSelectedChild == null)
            return;
        if(mIsSetSelectionToCenterChild){
            return;
        }

        int galleryCenter = getCenterOfGallery();

        // Common case where the current selected position is correct
        int selViewCenter = (selView.getTop() + selView.getBottom()) / 2;

//        Log.d(TAG,"selectedViewDetla:" + Math.abs(selViewCenter - galleryCenter));
        if(Math.abs(selViewCenter - galleryCenter) <= mSpacing / 2){
//            Log.d(TAG,"not set new selection!");
            return;
        }

//        Log.d(TAG,"selView(" + selView.getTop() + "," + selView.getBottom() + ") galleryCenter:" + galleryCenter);
//        if (selView.getTop() <= galleryCenter && selView.getBottom() >= galleryCenter) {
//            return;
//        }
        
        // TODO better search
        int closestEdgeDistance = Integer.MAX_VALUE;
        int newSelectedChildIndex = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {

            View child = getChildAt(i);
            int childCenter = (child.getTop() + child.getBottom()) / 2;
//            Log.d(TAG,"index:" + i + ",detla:" + Math.abs(childCenter - galleryCenter));
            if(Math.abs(childCenter - galleryCenter) <= mSpacing / 2){
                newSelectedChildIndex = i;
                break;
            }
//            if (child.getTop() <= galleryCenter && child.getBottom() >= galleryCenter) {
//                // This child is in the center
//                newSelectedChildIndex = i;
//                break;
//            }

//            int childClosestEdgeDistance = Math.min(Math.abs(child.getTop() - galleryCenter), Math.abs(child.getBottom() - galleryCenter));
            int childClosestEdgeDistance = Math.abs(childCenter - galleryCenter);
            if (childClosestEdgeDistance < closestEdgeDistance) {
                closestEdgeDistance = childClosestEdgeDistance;
                newSelectedChildIndex = i;
            }
        }

        int newPos = mFirstPosition + newSelectedChildIndex;

//        Log.d(TAG,"set new Selection:" + newPos);
        if (newPos != mSelectedPosition) {
            setSelectedPositionInt(newPos);
            setNextSelectedPositionInt(newPos);
            checkSelectionChanged();
            mIsSetSelectionToCenterChild = true;
        }
        mFlingRunnable.offsetSelectedDistance(mSelectedOffseted - mNextSelectedOffseted);
        Log.d(TAG,"offsetnextselected: mSelectedOffseted:" + mSelectedOffseted + ",mNextSelectedOffseted:" + mNextSelectedOffseted);
        mSelectedOffseted = 0;
        mNextSelectedOffseted = 0;
    }
    
    @Override
    protected void setSelectedPositionInt(int position) {
        mNextScrollSelectedPosition = position;
        super.setSelectedPositionInt(position);
    }
    
    @Override
    public void setSelection(int position) {
        mNextScrollSelectedPosition = position;
        super.setSelection(position);
    }

    
    private int mMaxTop = -1;
    private int mMaxBottom = -1;
    
    //计算最高和最低的位置
    private void calcTopAndBottomMost(final View sel){
        int count = 0;
        final Rect selectedRect = new Rect(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        mMaxTop = selectedRect.top;
        mMaxBottom = selectedRect.bottom;

        //计算最底部的位置
        Rect prevRect = new Rect(selectedRect);
        int maxDownCount = (mVisibleCount - 1) / 2;
        while(count < maxDownCount){
            count++;
            prevRect.offset(0, getItemSpacing(prevRect, count));
            mMaxBottom = prevRect.bottom;
        }
        
        count = 0;
        prevRect = new Rect(selectedRect);
        int maxUpCount = mVisibleCount / 2;
        while(count < maxUpCount){
            count++;
            prevRect.offset(0, -1 * getItemSpacing(prevRect, count * -1));
            mMaxTop = prevRect.top;
        }
        
        Log.d(TAG,"maxedge top " + mMaxTop + ",bottom:" + mMaxBottom);
    }
    
    /**
     * Creates and positions all views for this Gallery.
     * <p>
     * We layout rarely, most of the time {@link #trackMotionScroll(int)} takes
     * care of repositioning, adding, and removing children.
     * 
     * @param delta
     *            Change in the selected position. +1 means the selection is
     *            moving to the right, so views are scrolling to the left. -1
     *            means the selection is moving to the left.
     */
    @Override
    protected void layout(int delta, boolean animate) {
        Log.d(TAG,"layout, mSpinnerPadding:" + mSpinnerPadding);
        mIsRtl = false;// isLayoutRtl();

        int childrenTop = mSpinnerPadding.top;
        int childrenHeight = getBottom() - getTop() - mSpinnerPadding.top - mSpinnerPadding.bottom;

        if (mDataChanged) {
            handleDataChanged();
        }

        // Handle an empty gallery by removing all views.
        if (mItemCount == 0) {
            resetList();
            return;
        }

        // Update to the new selected position.
        if (mNextScrollSelectedPosition >= 0) {
            setSelectedPositionInt(mNextScrollSelectedPosition);
        }

        // All views go in recycler while we are in layout
        recycleAllViews();

        // Clear out old views
        // removeAllViewsInLayout();
        detachAllViewsFromParent();

        /*
         * These will be used to give initial positions to views entering the
         * gallery as we scroll
         */
        mBottomMost = 0;
        mTopMost = 0;

        // Make selected view and center it

        /*
         * mFirstPosition will be decreased as we add views to the left later
         * on. The 0 for x will be offset in a couple lines down.
         */
        mFirstPosition = mSelectedPosition;
        View sel = makeAndAddView(mSelectedPosition, 0, 0, true,mSelectedPosition);

        // Put the selected child in the center
        int selectedOffset = childrenTop + (childrenHeight / 2) - (sel.getHeight() / 2) + mSelectedCenterOffset;
        Log.d(TAG,"selectedOffset:" + selectedOffset + " = " + childrenTop + " + (" + childrenHeight + " / 2) - ( " + sel.getHeight()  + " / 2) + " + mSelectedCenterOffset);
        sel.offsetTopAndBottom(selectedOffset);
        setChildScale(sel,0,mSelectedPosition);
        setChildAlpha(sel,0);

        
        calcTopAndBottomMost(sel);
        
        mRect.set(sel.getLeft(),sel.getTop(),sel.getRight(),sel.getBottom());
        
        if (sel != null) {
            positionSelector(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        }
        fillToGalleryDown();
        fillToGalleryUp();

        // Flush any cached views that did not get reused above
        mRecycler.clear();

        invalidate();
        checkSelectionChanged();

        mDataChanged = false;
        mNeedSync = false;
        setNextSelectedPositionInt(mSelectedPosition);

        updateSelectedItemMetadata();
        
        Log.d(TAG,"maxedge:mTopMost:" + mTopMost + ",mBottomMost:" + mBottomMost);
    }
    
    @Override
    public View getRootView() {
        View parent = this;

        while (parent.getParent() != null && parent.getParent() instanceof View) {
            parent = (View) parent.getParent();
            if (parent instanceof FocusPositionManager) {
                break;
            }
        }

        return parent;
    }
    
    private int getItemSpacing(Rect prevRect,int posOffset){
        int prevTop = prevRect.top;
        int prevCenter = prevRect.height() / 2;
        int galleryCenter = getHeight() / 2;
        int offset = prevTop + prevCenter - galleryCenter;// + (posOffset > 0 ? 1 : -1) * mSelectedCenterOffset * -1;
        int offsetAbs = Math.abs(offset);
        int retSpace = (int)((1 - (float)offsetAbs / (float)galleryCenter) * mSpacing);
//        int retSpace = (int)((1 - (float)mSpacing *  / (float)galleryCenter) * mSpacing);
        Log.d(TAG,"getItemSpacing posOffset:" + posOffset + ",offset:" + (prevTop + prevCenter - galleryCenter) + ",spacing:" + retSpace);
        return retSpace;
    }
    
    private int getItemSpacing(View prevView, int posOffset){
        Rect r = new Rect();
        r.left = prevView.getLeft();
        r.top = prevView.getTop();
        r.right = prevView.getRight();
        r.bottom = prevView.getBottom();
        return getItemSpacing(r,posOffset);
        
//        int prevTop = prevView.getTop();
//        int prevCenter = prevView.getHeight() / 2;
//        int galleryCenter = getHeight() / 2;
//        int offset = prevTop + prevCenter - galleryCenter;// + (posOffset > 0 ? 1 : -1) * mSelectedCenterOffset * -1;
//        int offsetAbs = Math.abs(offset);
//        int retSpace = (int)((1 - (float)offsetAbs / (float)galleryCenter) * mSpacing);
//        Log.d(TAG,"getItemSpacing posOffset:" + posOffset + ",offset:" + (prevTop + prevCenter - galleryCenter) + ",spacing:" + retSpace);
//        return retSpace;
    }
    
    private void setChildScale(View child,int posOffset,int pos){
        int childTop = child.getTop();
        int maxDownCount = mVisibleCount / 2;
        int maxUpCount =  (mVisibleCount - 1) / 2;
        if(mFlingRunnable.isRunning()){
            if(mDirection == DIRECTION_DOWN){
                if(mNextScrollSelectedPosition - pos == maxUpCount + 1){
                    childTop = 2 * mMaxTop - child.getTop();
                    Log.d(TAG,"setChildScale index:" + pos + ",1");
                }
                if(pos - mNextScrollSelectedPosition == maxDownCount){
                    childTop = 2 * mMaxBottom - child.getBottom() - child.getHeight();
                    Log.d(TAG,"setChildScale index:" + pos + ",2");
                }
            } else if(mDirection == DIRECTION_UP){
                if(mNextScrollSelectedPosition - pos == maxUpCount){
                    childTop = 2 * mMaxTop - child.getTop();
                    Log.d(TAG,"setChildScale index:" + pos + ",3");
                }
                
                if(pos - mNextScrollSelectedPosition == maxDownCount + 1){
                    childTop = 2 * mMaxBottom - child.getBottom() - child.getHeight();
                    Log.d(TAG,"setChildScale index:" + pos + ",4");
                }
            }
        }
        
        int childCenter = child.getHeight() / 2;
        int galleryCenter = getHeight() / 2;
        int offset = Math.abs(childTop + childCenter - galleryCenter);

//        float scale = 1 - (float)offset / (float)galleryCenter * mMaxScale;
        float scale = 1 - (float)offset / (float)galleryCenter * (1 - mMinScale);
        
//        Log.d(TAG,"setChildScale posOffset:" + posOffset + ", offset no abs:" + (childTop + childCenter - galleryCenter) + ",scale:" + scale + ",galleryCenter:" + galleryCenter);
        Log.d(TAG,"setChildScale index:" + pos + ",scale:" + scale);
        child.setScaleX(scale);
        child.setScaleY(scale);
    }
    
//    private float quarteaseOut(float t, float b, float c, float d){
//        return -c * ((t=t/d-1)*t*t*t - 1) + b;
//    }
//    
//    private float quarteaseIn(float t, float b, float c, float d){
//        return c*(t/=d)*t*t*t + b;
//    }
    
    private void setChildAlpha(View child,int posOffset){
        int childTop = child.getTop();
        int childCenter = child.getHeight() / 2;
        int galleryCenter = getHeight() / 2;
        int offset = childTop + childCenter - galleryCenter;
        if(Math.abs(offset) >= mSpacing){
            child.setAlpha(mMinAlpha);
        } else { 
            float fOffset = Math.abs(offset);
//            if((mDirection == DIRECTION_DOWN && offset > 0) || (mDirection == DIRECTION_DOWN && offset < 0)){
//                fOffset = quarteaseIn(offset,mSpacing,0,mSpacing);
//                Log.d(TAG,"setChildAlpha 1origin:" + Math.abs(offset) + ",result:" + fOffset);
//            } else {
//                fOffset = quarteaseOut(offset,0,mSpacing,mSpacing);
//                Log.d(TAG,"setChildAlpha 2origin:" + Math.abs(offset) + ",result:" + fOffset);
//            }
            float alpha = 1 - fOffset / (float)mSpacing * (1 - mMinAlpha);
            Log.d(TAG,"setChildAlpha offset:" + offset + ",alpha:" + alpha);
            child.setAlpha(alpha);
        }
    }
    
    private void fillToGalleryUp() {
        int itemSpacing = mSpacing;
        int galleryTop = getPaddingTop();

        // Set state for initial iteration
        View prevIterationView = getChildAt(0);
        int curPosition;
        int curBottomEdge;
        int prevTopEdge;

        if (prevIterationView != null) {
            curPosition = mFirstPosition - 1;
            
            if(mFlingRunnable.isRunning()){
                curBottomEdge = prevIterationView.getBottom() + getItemSpacing(prevIterationView, curPosition - mSelectedPosition);
            } else {
                curBottomEdge = prevIterationView.getBottom() - getItemSpacing(prevIterationView, curPosition - mSelectedPosition);
            }
            prevTopEdge = prevIterationView.getTop();
        } else {
            // No children available!
            curPosition = 0;
            curBottomEdge = getBottom() - getTop() - getPaddingBottom();
            prevTopEdge = getBottom() - getTop() - getPaddingBottom();
            mShouldStopFling = true;
        }

        int maxUpCount = mVisibleCount / 2;
//        while (curBottomEdge > galleryTop && curPosition >= 0) {
        //prevTopEdge > galleryTop && 
        while (curPosition >= 0 && mNextScrollSelectedPosition - curPosition <= maxUpCount) {
//            if(mFlingRunnable.isRunning() &&mNextScrollSelectedPosition - curPosition == maxUpCount){
//                Log.d(TAG,"fillup index:" + curPosition + ",topBottom:(" + (curBottomEdge - prevIterationView.getHeight()) + "," + curBottomEdge + ")");
//            }
            prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curBottomEdge, false,curPosition);

            // Remember some state
            mFirstPosition = curPosition;
            int itemSpace = getItemSpacing(prevIterationView, curPosition - mSelectedPosition);
            
//            Log.d(TAG,"isrunning:" + mFlingRunnable.isRunning() + ",mNextScrollSelectedPosition:" + mNextScrollSelectedPosition + ",curPosition:" + curPosition + ",maxUpCount:" + maxUpCount);
//            if(mFlingRunnable.isRunning() && mNextScrollSelectedPosition - curPosition == maxUpCount - 1){
//                itemSpace *= -1;
//            }
            
            // Set state for next iteration
            curBottomEdge = prevIterationView.getBottom() - itemSpace;
            curPosition--;
        }
    }

    private void fillToGalleryDown() {
        Log.d(TAG,"fillToGalleryDown1");
        int itemSpacing = mSpacing;
        int galleryBottom = getBottom() - getTop() - getPaddingBottom();
        int numChildren = getChildCount();
        int numItems = mItemCount;

        // Set state for initial iteration
        View prevIterationView = getChildAt(numChildren - 1);
        int curPosition;
        int curTopEdge;
        int prevBottomEdge;

        if (prevIterationView != null) {
            curPosition = mFirstPosition + numChildren;
            if(mFlingRunnable.isRunning()){
                curTopEdge = prevIterationView.getTop() - getItemSpacing(prevIterationView, curPosition - mSelectedPosition);
            } else {
                curTopEdge = prevIterationView.getTop() + getItemSpacing(prevIterationView, curPosition - mSelectedPosition);
            }
            prevBottomEdge = prevIterationView.getBottom();
        } else {
            mFirstPosition = curPosition = mItemCount - 1;
            curTopEdge = getPaddingTop();
            prevBottomEdge = getPaddingTop();
            mShouldStopFling = true;
        }

        int maxDownCount = (mVisibleCount - 1) / 2;

        Log.d(TAG,"fillToGalleryDown2:" + prevBottomEdge + ","+ galleryBottom  + ","+  curPosition  + ","+  numItems  + ","+  curPosition  + ","+  mNextScrollSelectedPosition  + ","+  maxDownCount);
//        while (curTopEdge < galleryBottom && curPosition < numItems) {
        //prevBottomEdge < galleryBottom && 
        while (curPosition < numItems && curPosition - mNextScrollSelectedPosition <= maxDownCount) {
            Log.d(TAG,"fillToGalleryDown3 curTopEdge:" + curTopEdge + ",spacing:" + getItemSpacing(prevIterationView, curPosition - mSelectedPosition));
            prevIterationView = makeAndAddView(curPosition, curPosition - mSelectedPosition, curTopEdge, true,curPosition);

            int itemSpace = getItemSpacing(prevIterationView, curPosition - mSelectedPosition);
//            if(mFlingRunnable.isRunning() && curPosition - mNextScrollSelectedPosition == maxDownCount - 1){
//                itemSpace *= -1;
//            }
            
            // Set state for next iteration
            curTopEdge = prevIterationView.getTop() + itemSpace;
            
//            Log.d(TAG,"fillToGalleryDown:" + (int)(itemSpacing * Math.pow(mScale, Math.abs(curPosition - mSelectedPosition))));
            
            curPosition++;
        }
    }

    /**
     * Obtain a view, either by pulling an existing view from the recycler or by
     * getting a new one from the adapter. If we are animating, make sure there
     * is enough information in the view's layout parameters to animate from the
     * old to new positions.
     * 
     * @param position
     *            Position in the gallery for the view to obtain
     * @param offset
     *            Offset from the selected position
     * @param x
     *            X-coordinate indicating where this view should be placed. This
     *            will either be the left or right edge of the view, depending
     *            on the fromLeft parameter
     * @param fromLeft
     *            Are we positioning views based on the left edge? (i.e.,
     *            building from left to right)?
     * @return A view that has been added to the gallery
     */
    private View makeAndAddView(int position, int offset, int y, boolean fromTop,int pos) {
//        Log.d(TAG,"makeAndAddView:" + position + "," + offset + "," + y + "," + fromTop);
        View child;
        if (!mDataChanged) {
            child = mRecycler.get(position);
            if (child != null) {
                // Can reuse an existing view
                int childTop = child.getTop();

                // Remember left and right edges of where views have been placed
                mBottomMost = Math.max(mBottomMost, childTop + child.getMeasuredHeight());
                mTopMost = Math.min(mTopMost, childTop);

                // Position the view
                setUpChild(child, offset, y, fromTop,pos);

                return child;
            }
        }

        // Nothing found in the recycler -- ask the adapter for a view
        child = mAdapter.getView(position, null, this);

        // Position the view
        setUpChild(child, offset, y, fromTop,pos);

        return child;
    }

    /**
     * Helper for makeAndAddView to set the position of a view and fill out its
     * layout parameters.
     * 
     * @param child
     *            The view to position
     * @param offset
     *            Offset from the selected position
     * @param x
     *            X-coordinate indicating where this view should be placed. This
     *            will either be the left or right edge of the view, depending
     *            on the fromLeft parameter
     * @param fromLeft
     *            Are we positioning views based on the left edge? (i.e.,
     *            building from left to right)?
     */
    private void setUpChild(View child, int offset, int y, boolean fromTop,int pos) {

        // Respect layout params that are already in the view. Otherwise
        // make some up...
        VGallery.LayoutParams lp = (VGallery.LayoutParams) child.getLayoutParams();
        if (lp == null) {
            lp = (VGallery.LayoutParams) generateDefaultLayoutParams();
        }

        addViewInLayout(child, fromTop != mIsRtl ? -1 : 0, lp);

        child.setSelected(offset == 0);
        // Get measure specs
        int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, mSpinnerPadding.left + mSpinnerPadding.right, lp.width);
        
        
        // Measure child
        child.measure(childWidthSpec, childHeightSpec);
        Log.d(TAG,"childSizeSpec(" + childWidthSpec + childHeightSpec  + "),childSize:(" + child.getWidth() + "," + child.getHeight() + ")");

        int childTop;
        int childBottom;

        // Position vertically based on gravity setting
        int childLeft = calculateLeft(child, true);
        int childRight = childLeft + child.getMeasuredWidth();

        int height = child.getMeasuredHeight();
        if (fromTop) {
            childTop = y;
            childBottom = childTop + height;
        } else {
            childTop = y - height;
            childBottom = y;
        }
        Log.d(TAG,"index: " + (offset + mSelectedPosition) + "  layoutChild(" + childLeft + "," + childTop + "," + childRight + "," + childBottom + ")");


        child.layout(childLeft, childTop, childRight, childBottom);
        setChildScale(child,offset,pos);
        setChildAlpha(child,offset);
//        int distance = getCenterOfGallery() - getCenterOfView(child);
//        Log.d(TAG,"setUpChild distance:" + distance);
    }

    /**
     * Figure out vertical placement based on mGravity
     * 
     * @param child
     *            Child to place
     * @return Where the top of the child should be
     */
    private int calculateLeft(View child, boolean duringLayout) {
        int myWidth = duringLayout ? getMeasuredWidth() : getWidth();
        int childWidth = duringLayout ? child.getMeasuredWidth() : child.getWidth();

        int childLeft = 0;

        switch (mGravity) {
        case Gravity.LEFT:
            childLeft = mSpinnerPadding.left;
            break;
        case Gravity.CENTER_HORIZONTAL:
            int availableSpace = myWidth - mSpinnerPadding.right - mSpinnerPadding.left - childWidth;
            childLeft = mSpinnerPadding.left + (availableSpace / 2);
            break;
        case Gravity.RIGHT:
            childLeft = myWidth - mSpinnerPadding.right - childWidth;
            break;
        }
        return childLeft;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        super.onFling(e1, e2, velocityX, velocityY);
        // Fling the gallery!
        mFlingRunnable.startUsingVelocity((int) -velocityX);

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        if (localLOGV)
            Log.v(TAG, String.valueOf(e2.getX() - e1.getX()));

        super.onScroll(e1, e2, distanceX, distanceY);
        // Track the motion
        trackMotionScroll(-1 * (int) distanceY);

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {

        super.onDown(e);
        // Kill any existing fling/scroll
        mFlingRunnable.stop(false);

        // Must return true to get matching events for this down event.
        return true;
    }

    /**
     * Called when a touch event's action is MotionEvent.ACTION_UP.
     */
    @Override
    protected void onUp() {
        super.onUp();

        if (mFlingRunnable.mScroller.isFinished()) {
            scrollIntoSlots();
        }
    }

    // Unused methods from GestureDetector.OnGestureListener above

    private static final int DIRECTION_UP = 0;
    private static final int DIRECTION_DOWN = 1;
    private int mDirection = -1;
    
    /**
     * Handles left, right, and clicking
     * 
     * @see View#onKeyDown
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mFlingRunnable.isRunning() && (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)){
            return true;
        }
        
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_UP:
            mDirection = DIRECTION_UP;
            if (movePrevious()) {
                playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
                return true;
            }
            break;
        case KeyEvent.KEYCODE_DPAD_DOWN:
            mDirection = DIRECTION_DOWN;
            if (moveNext()) {
                playSoundEffect(SoundEffectConstants.NAVIGATION_DOWN);
                return true;
            }
            break;
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            mReceivedInvokeKeyDown = true;
            // fallthrough to default handling
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER: {

            if (mReceivedInvokeKeyDown) {
                if (mItemCount > 0) {

                    dispatchPress(mSelectedChild);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dispatchUnpress();
                        }
                    }, ViewConfiguration.getPressedStateDuration());

                    int selectedIndex = mSelectedPosition - mFirstPosition;
                    performItemClick(getChildAt(selectedIndex), mSelectedPosition, mAdapter.getItemId(mSelectedPosition));
                }
            }

            // Clear the flag
            mReceivedInvokeKeyDown = false;

            return true;
        }
        }

        return super.onKeyUp(keyCode, event);
    }
    
    private boolean mIsSetSelectionToCenterChild = false;
    private int mNextScrollSelectedPosition = INVALID_POSITION;
    
    @Override
    protected boolean scrollToChild(int childPosition) {
        mNextSelectedOffseted = 0;
        mSelectedOffseted = 0;
        mIsSetSelectionToCenterChild = false;
        View child = getChildAt(childPosition);
        mNextScrollSelectedPosition = mFirstPosition + childPosition;
        if (child != null) {
            int distance = getCenterOfGallery() - getCenterOfView(child);
//            Log.d(TAG,"scrollToChild top " + child.getTop() + ",bottom " + child.getBottom());
            Log.d(TAG,"startScroll:" + -distance + ",childPosition:" + childPosition + ",spacing:" + getItemSpacing(getChildAt(childPosition), childPosition - mSelectedPosition) + ",child:(" + child.getWidth() + "," + child.getHeight() + ") scale:" + child.getScaleX());
            mFlingRunnable.startUsingDistance(distance);
            return true;
        }

        return false;
    }

    /**
     * Responsible for fling behavior. Use {@link #startUsingVelocity(int)} to
     * initiate a fling. Each frame of the fling is handled in {@link #run()}. A
     * FlingRunnable will keep re-posting itself until the fling is done.
     */
    private class FlingRunnable implements Runnable {
        /**
         * Tracks the decay of a fling scroll
         */
        private Scroller mScroller;

        /**
         * X value reported by mScroller on the previous fling
         */
        private int mLastFlingY;

        private ListLoopScroller mListLoopScroller;
        private int mFrameCount;
        private float mDefatultScrollStep = 10.0f;
        private int mDistance = 0;
        
        public FlingRunnable() {
            mScroller = new Scroller(getContext(), new DecelerateInterpolator());
            mListLoopScroller = new ListLoopScroller();
        }

        private void startCommon() {
            // Remove any pending flings
            removeCallbacks(this);
        }

        public void startUsingVelocity(int initialVelocity) {
//            Log.d(TAG,"startUsingVelocity");
            if (initialVelocity == 0)
                return;

            startCommon();

            int initialY = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingY = initialY;
            mScroller.fling(0, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            post(this);
        }
        
        public void offsetSelectedDistance(int distance){
            if(distance == 0){
                return;
            }
            Log.d(TAG,"offsetSelectedDistance:" + distance);
            if(!mListLoopScroller.isFinished()){
                mDistance += distance;
                mLastFlingY = 0;
                int frameCount = Math.abs((int)(mDistance / mDefatultScrollStep));
                mListLoopScroller.startScroll(mListLoopScroller.getCurr(), -distance, frameCount);
            }
        }
        
        public boolean isRunning(){
            return !mListLoopScroller.isFinished();
        }

        public void startUsingDistance(int distance) {
            if (distance == 0)
                return;
            mDistance = distance;
            mLastFlingY = 0;
            int frameCount;
            if(mFrameCount <= 0){
                //use default sroll step
                frameCount = (int)(distance / mDefatultScrollStep);
                if(frameCount < 0){
                    frameCount = -frameCount;
                }
				else if(frameCount == 0){
					frameCount = 1;
				}
            }
            else{
                frameCount = mFrameCount;
            }
            if(mListLoopScroller.isFinished()){
                startCommon();
                mListLoopScroller.startScroll(0, -distance, frameCount);
                post(this);
            }
            else{
                mListLoopScroller.startScroll(0, -distance, frameCount);
            }
//          mScroller.startScroll(0, 0, 0, -distance, mAnimationDuration);
//          post(this);
        }

        public void stop(boolean scrollIntoSlots) {
            removeCallbacks(this);
            endFling(scrollIntoSlots);
        }

        private void endFling(boolean scrollIntoSlots) {
            /*
             * Force the scroller's status to finished (without setting its
             * position to the end)
             */
            mDirection = -1;
            mScroller.forceFinished(true);
            mListLoopScroller.finish();
            if (scrollIntoSlots)
                scrollIntoSlots();
        }
        
//        int totalDistance = 0;
        
        @Override
        public void run() {
            if (mItemCount == 0) {
                endFling(true);
                return;
            }

            mShouldStopFling = false;

//          final Scroller scroller = mScroller;
            boolean more = mListLoopScroller.computeScrollOffset();
            final int y = mListLoopScroller.getCurr();

            // Flip sign to convert finger direction to list items direction
            // (e.g. finger moving down means list is moving towards the top)
            int delta = mLastFlingY - y;
//            Log.d(TAG,"run:mLastFlingY=" + mLastFlingY + ",y=" + y + ",delta:" + delta);
            // Pretend that each frame of a fling scroll is a touch scroll
            if (delta > 0) {
                // Moving towards the left. Use leftmost view as
                // mDownTouchPosition
                mDownTouchPosition = mIsRtl ? (mFirstPosition + getChildCount() - 1) : mFirstPosition;

                // Don't fling more than 1 screen
                delta = Math.min(getHeight() - getPaddingTop() - getPaddingBottom() - 1, delta);
            } else {
                // Moving towards the right. Use rightmost view as
                // mDownTouchPosition
                int offsetToLast = getChildCount() - 1;
                mDownTouchPosition = mIsRtl ? mFirstPosition : (mFirstPosition + getChildCount() - 1);

                // Don't fling more than 1 screen
                delta = Math.max(-(getHeight() - getPaddingTop() - getPaddingBottom() - 1), delta);
            }

            Log.d(TAG,"mmaa trackMotionScroll:" + delta + ",mOffseted:" + mNextSelectedOffseted + ",mDistance:" + mDistance + ",mLastFlingY - y:" + mLastFlingY + "-" + y);
            if(Math.abs(mNextSelectedOffseted - mDistance) < mDefatultScrollStep){
                endFling(true);
                return;
            }
            trackMotionScroll(delta);
//            totalDistance += delta;
            
            
            if (more && !mShouldStopFling) {
                mLastFlingY = y;
                post(this);
            } else {
//                Log.d(TAG,"mmaa totalDistance:" + totalDistance);
                endFling(true);
            }
        }

    }
    

    public void setOnItemSelectedListener(ItemSelectedListener l) {
        mItemSelectedListener = l;
    }
    
//    public ItemSelectedListener getOnItemSelectedListener(){
//        return mItemSelectedListener;
//    }
    
    Rect mFocusRect = new Rect();
    @Override
    public FocusRectParams getFocusParams() {
    	ViewGroup root = (ViewGroup) getRootView();
    	mFocusRect.set(mRect);
    	root.offsetDescendantRectToMyCoords(this, mFocusRect);
    	mFocusRectparams = new FocusRectParams(mFocusRect, 0.5f, 0.5f);
        return mFocusRectparams;
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public boolean isAnimate() {
        return false;
    }

    @Override
    public ItemListener getItem() {
        return (ItemListener)getSelectedView();
    }

    @Override
    public boolean isScrolling() {
        return false;
    }

    @Override
    public Params getParams() {
        return mParams;
    }
    
    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
//        boolean hr = false;
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_UP:
            if (mItemCount > 0 && mSelectedPosition > 0){
                onKeyDown(keyCode, event);
                return true;
            }
//            hr = mSelectedPosition > 0 ? true : false;
//            if (hr) {
//                if (mItemSelectedListener != null) {
//                    mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, false, this);
//                }
//                mSelectedPosition--;
//            }
//            break;
        case KeyEvent.KEYCODE_DPAD_DOWN:
            if (mItemCount > 0 && mSelectedPosition < mItemCount - 1) {
                onKeyDown(keyCode, event);
                return true;
            }
            /*
            hr = mSelectedPosition < mItemCount - 1 ? true : false;
            if (hr) {
                if (mItemSelectedListener != null) {
                    mItemSelectedListener.onItemSelected(getSelectedView(), mSelectedPosition, false, this);
                }
                mSelectedPosition++;
            }
            */
            break;
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            onKeyDown(keyCode, event);
            return true;
        default:
            break;
        }
        return false;
    }

    @Override
    public boolean canDeep() {
        return true;
    }

    @Override
    public boolean hasDeepFocus() {
        return mDeepFocus;
    }
    
    private boolean mDeepFocus = false;
    
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
        Log.d(TAG,"getItemWidth:CircleVGallery:" + getSelectedView().getWidth() ); 
        return getSelectedView().getWidth();
    }

    @Override
    public int getItemHeight() {
        Log.d(TAG,"getItemHeight:CircleVGallery:" + getSelectedView().getHeight()); 
        return getSelectedView().getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }
    
    
    public class ListLoopScroller {
        private final String TAG = "ListLoopScroller";
        private final boolean DEBUG = true;
        private final float DEFALUT_MAX_STEP = 20.0f;//默认单步最大的步长
        private final float SLOW_DOWN_RATIO = 1.0f;//减速距离的放大系统
        private int mStart;//开始位置
        private int mCurr;//当前位置
        private int mFinal;//本次动画的结束位置
        private boolean mFinished = true;//是否已经完成
        private float mMaxStep = DEFALUT_MAX_STEP;//单步最大的步长，即为滚动速度的峰值
        private int mCurrFrameIndex;//当前匀速的帧，减速不计入在内
        private int mTotalFrameCount;//总共匀速帧数，因为最后有减速的过程所以该值会小于实现行动的帧数
        private float mStep;//单步的步长，可以理解为速度
        private int mSlowDownIndex;//当前减速的帧数
        private float mSlowDownFrameCount;//减速的总帧数
        private int mSlowDownStart;//减速的开始位置
        private float mSlowDownDistance;//减速的距离
        private float mSlowDownStep;//减速的初始步长
        
        /**
         * 构造方法
         */
        public ListLoopScroller(){
        }

        
        /**
         * 当前的位置
         * @return
         */
        public int getCurr(){
            return mCurr;
        }
        
        
        /**
         * 取得最终的位置
         * @return
         */
        public int getFinal(){
            return mFinal;
        }
        
        
        /**
         * 取得开始的值
         * @return
         */
        public int getStart(){
            return mStart;
        }
        
        
        /**
         * 是否已经结束
         * @return
         */
        public boolean isFinished(){
            return mFinished;
        }
        
        /**
         * 设置峰值的步长
         */
        public void setMaxStep(float maxStep){
            mMaxStep = maxStep;
        }
        
        
        /**
         * 开始滚动，如果动画还在运行中会将滚动的距离叠加，当速度达到峰值作匀速
         * @param start
         * @param distance
         * @param frameCount
         */
        public void startScroll(int start, int distance, int frameCount){
            distance = mFinal - mCurr + distance;
            mTotalFrameCount = frameCount;
            //计算单步的步长
            mStep = (float)distance / (float)mTotalFrameCount;
            //向下
            //如果步长大于最大值，就取步长的最大值
            if(mStep > mMaxStep){
                mStep = mMaxStep;
                //通过步长反算需要的帧数
                mTotalFrameCount = (int)(distance / mStep);
                Log.i(TAG, "startScroll change mTotalFrameCount="+mTotalFrameCount);
            }
            //向上
            else if(mStep < -mMaxStep){
                mStep = -mMaxStep;
                //通过步长反算需要的帧数
                mTotalFrameCount = (int)(distance / mStep);
                Log.i(TAG, "startScroll change mTotalFrameCount="+mTotalFrameCount);
            }
            //匀速的参数初始化
            mCurr = 0;
            mStart = start;
            mFinal = mStart + distance;
            mFinished = false;
            mCurrFrameIndex = 0;
            //减速的参数初始化
            mSlowDownFrameCount = 0;
            mSlowDownStep = mStep / SLOW_DOWN_RATIO;
            computeSlowDownDistance();
            Log.i(TAG, "startScroll mStep="+mStep+" mStart="+mStart+" mFinalY="+mFinal+" mTotalFrameCount="+mTotalFrameCount);
        }

        
        /**
         * 计算过程的值（先进行匀速再进行匀减速的过程）
         * @return true动画未完成，否则完成
         */
        public boolean computeScrollOffset(){
            //完成时直接返回
            if(mFinished){
                return false;
            }
            
            //当前的数帧大于总数
            if(mCurrFrameIndex >= mTotalFrameCount){
                finish();
                return false;
            }
            
            //减速计算过程
            if(mSlowDownFrameCount > 0){
                if(mSlowDownIndex > mSlowDownFrameCount){
                    finish();
                    return false;
                }
                mSlowDownIndex ++;
                if(mSlowDownIndex >= mSlowDownFrameCount){
                    //最后一帧补齐,因为有误差所以最后一帧不进入计算
                    mCurr = mFinal;
                }
                else{
                    //匀减速直线运动 S=vt-（at^2）/2; v:初始速度 a: v/t(初始速度/总时间);
                    mCurr =  mSlowDownStart + (int)(mSlowDownStep * mSlowDownIndex - (mSlowDownIndex * mSlowDownIndex * mSlowDownStep) / (2 * mSlowDownFrameCount));
                }
                if(DEBUG){
                Log.i(TAG, "computeScrollOffset slow down mTotalFrameCount="+mTotalFrameCount+
                        " mCurrFrameIndex="+mCurrFrameIndex+" mCurr="+mCurr+" mSlowDownIndex="+mSlowDownIndex+ " mStep="+mStep);
                }
            }
            //匀速计算过程
            else{
                float p = (float)(mCurrFrameIndex + 1) / (float)mTotalFrameCount;
                //当前帧的动画位置
                mCurr = (int)(mStart + (mFinal - mStart) * p);
                mCurrFrameIndex ++;
                
                //判断是否进入减速的动画
                int leftDistance = mFinal - mCurr;
                if(leftDistance < 0){
                    leftDistance = -leftDistance;
                }
                if(leftDistance < mSlowDownDistance){
                    setSlowDown(leftDistance);
                }
                else{
                    resetSlowDown();
                }
                if(DEBUG){
                Log.i(TAG, "computeScrollOffset mCurrFrameIndex="+mCurrFrameIndex+
                        " mTotalFrameCount="+mTotalFrameCount+" currY="+mCurr+" mFinalY="+mFinal+ " mSlowDownFrameCount="+mSlowDownFrameCount);
                }
            }
            return true;
        }
        
        
        /**
         * 完成动画
         */
        public void finish(){
            mCurrFrameIndex = mTotalFrameCount;
            mSlowDownFrameCount = 0;
            mFinished = true;
        }
        
        /**
         * 重置减速相关的参数
         */
        private void resetSlowDown(){
            mSlowDownStart = 0;
            mSlowDownFrameCount = 0;
            mSlowDownIndex = 0;
        }
        
        
        /**
         * 设置进入减速状态的相关参数
         * @param distance 减速的距离
         */
        private void setSlowDown(int distance){
            mSlowDownStart = mCurr;
            mSlowDownIndex = 0;
            //取得减速动画的帧数，因为是以之前匀速的速度做匀减速运算，所以帧数要比匀速的要多
            mSlowDownFrameCount = 2 * distance / mSlowDownStep;
            //保证为正数
            if(mSlowDownFrameCount < 0){
                mSlowDownFrameCount = -mSlowDownFrameCount;
            }
        }
        
        
        /**
         * 计算减速的距离
         */
        private void computeSlowDownDistance(){
            int distance = (mFinal - mStart) / 2;
            if(distance < 0){
                distance = -distance;
            }
            mSlowDownDistance = mStep * mStep;
            if(mSlowDownDistance > distance){
                Log.w(TAG, "computeSlowDownDistance mSlowDownDistance too big="+mSlowDownDistance+" distance="+distance+" mStep="+ mStep);
                mSlowDownDistance = distance;
            }
            Log.i(TAG, "computeSlowDownDistance mSlowDownDistance="+mSlowDownDistance);
        }
        
        
    }


	@Override
	public void onItemSelected(boolean selected) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFocusBackground() {
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

	@Override
	public Rect getClipFocusRect() {
		// TODO Auto-generated method stub
		return null;
	}
}

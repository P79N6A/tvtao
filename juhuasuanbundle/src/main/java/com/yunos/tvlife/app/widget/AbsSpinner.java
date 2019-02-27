/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AbsListView.SelectionBoundsAdjuster;
import android.widget.Adapter;
import android.widget.SpinnerAdapter;

import java.lang.reflect.Method;

/**
 * An abstract base class for spinner widgets. SDK users will probably not
 * need to use this class.
 * 
 * @attr ref android.R.styleable#AbsSpinner_entries
 */
public abstract class AbsSpinner extends AdapterView<SpinnerAdapter> {
	
	private final static String TAG = "AbsSpinner";
	private final static boolean DEBUG = true;
	
    SpinnerAdapter mAdapter;

    int mHeightMeasureSpec;
    int mWidthMeasureSpec;
    int mItemWidth;
    int mItemHeight;
    boolean mBlockLayoutRequests;

    int mSelectionLeftPadding = 0;
    int mSelectionTopPadding = 0;
    int mSelectionRightPadding = 0;
    int mSelectionBottomPadding = 0;
    final Rect mSpinnerPadding = new Rect();

    final RecycleBin mRecycler = new RecycleBin();
    private DataSetObserver mDataSetObserver;

    /** Temporary frame to hold a child View's frame rectangle */
    private Rect mTouchFrame;
    
    /**
     * If mAdapter != null, whenever this is true the adapter has stable IDs.
     */
    boolean mAdapterHasStableIds;
    
    final boolean[] mIsScrap = new boolean[1];
    
    Drawable mDivider;
    int mDividerWidth;
    
    /**
     * The offset in pixels form the left of the AdapterView to the left
     * of the currently selected view. Used to save and restore state.
     */
    int mSelectedLeft = 0;
    
    int mSelectorPosition;
    
    /**
     * adapter is used to show scalable content
     * it is not add to view, but layout by view 
     */
    Adapter mScalableAdapter;
    
    /**
     * the view is showed scalable content
     * draw in dispatchDraw.
     */
    View mScalableView;
    
    /**
     * spacing of mScalableView & selected view
     * used in vertical spacing.
     */
    int mScalableViewSpacing;
    
    final RecycleBin mScalableRecycler = new RecycleBin();
    
    /**
     * Indicates whether this view is in a state where the selector should be drawn. This will
     * happen if we have focus but are not in touch mode, or we are in the middle of displaying
     * the pressed state for an item.
     *
     * @return True if the selector should be shown
     * 
     */
    boolean shouldShowSelector() {
        return (hasFocus() && !isInTouchMode()) || touchModeDrawsInPressedState();
    }
    
    /**
     * Defines the selector's location and dimension at drawing time
     */
    Rect mSelectorRect = new Rect();
    
    /**
     * Track if we are currently attached to a window.
     */
    boolean mIsAttached;
    
    /**
     * The position to resurrect the selected position to.
     */
    int mResurrectToPosition = 0;
    
    /**
     * Controls how the next layout will happen
     */
    int mLayoutMode = LAYOUT_NORMAL;
    
    /**
     * Regular layout - usually an unsolicited layout from the view system
     */
    static final int LAYOUT_NORMAL = 0;

    /**
     * Show the first item
     */
    static final int LAYOUT_FORCE_TOP = 1;

    /**
     * Force the selected item to be on somewhere on the screen
     */
    static final int LAYOUT_SET_SELECTION = 2;

    /**
     * Show the last item
     */
    static final int LAYOUT_FORCE_BOTTOM = 3;

    /**
     * Make a mSelectedItem appear in a specific location and build the rest of
     * the views from there. The top is specified by mSpecificTop.
     */
    static final int LAYOUT_SPECIFIC = 4;

    /**
     * Layout to sync as a result of a data change. Restore mSyncPosition to have its top
     * at mSpecificTop
     */
    static final int LAYOUT_SYNC = 5;
    
    /**
     * @return True if the current touch mode requires that we draw the selector in the pressed
     *         state.
     */
    boolean touchModeDrawsInPressedState() {
    	//TODO
        // FIXME use isPressed for this
//        switch (mTouchMode) {
//        case TOUCH_MODE_TAP:
//        case TOUCH_MODE_DONE_WAITING:
//            return true;
//        default:
//            return false;
//        }
    	return false;
    }
    
    protected boolean needMeasureSelectedView = true;

    public AbsSpinner(Context context) {
        super(context);
        initAbsSpinner();
    }

    public AbsSpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAbsSpinner();
//
//        TypedArray a = context.obtainStyledAttributes(attrs,
//                com.android.internal.R.styleable.AbsSpinner, defStyle, 0);
//
//        CharSequence[] entries = a.getTextArray(com.android.internal.R.styleable.AbsSpinner_entries);
//        if (entries != null) {
//            ArrayAdapter<CharSequence> adapter =
//                    new ArrayAdapter<CharSequence>(context,
//                    		com.android.internal.R.layout.simple_spinner_item, entries);
//            adapter.setDropDownViewResource(com.android.internal.R.layout.simple_spinner_dropdown_item);
//            setAdapter(adapter);
//        }
//
//        a.recycle();
    }

    /**
     * Common code for different constructor flavors
     */
    private void initAbsSpinner() {
        setFocusable(true);
        setWillNotDraw(false);
    }
    
    @Override
    public void setAdapter(Adapter adapter) {
        if (null != mAdapter) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            resetList();
        }
        
        mAdapter = (SpinnerAdapter)adapter;
        
        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        mRecycler.clear();
        
        if (mAdapter != null) {
            mOldItemCount = mItemCount;
            mAdapterHasStableIds = mAdapter.hasStableIds();
            mItemCount = mAdapter.getCount();
            checkFocus();

            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
            
            mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());

            int position = initPosition();

            setSelectedPositionInt(position);
            setNextSelectedPositionInt(position);
            
            if (mItemCount == 0) {
                // Nothing selected
                checkSelectionChanged();
            }
            
        } else {
            checkFocus();            
            resetList();
            // Nothing selected
            checkSelectionChanged();
        }

        requestLayout();
    }
    
    int initPosition(){
    	return mItemCount > 0 ? 0 : INVALID_POSITION;
    }
    
    void positionSelector(int position, View sel) {
        if (position != INVALID_POSITION) {
            mSelectorPosition = position;
        	setSelectedPositionInt(position);
        }

        getFocusedRect(mSelectorRect);
//        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        if (sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster)sel).adjustListItemSelectionBounds(mSelectorRect);
        }
        positionSelector(mSelectorRect.left, mSelectorRect.top, mSelectorRect.right, mSelectorRect.bottom);

        //TODO by lawin
//        final boolean isChildViewEnabled = mIsChildViewEnabled;
//        if (sel.isEnabled() != isChildViewEnabled) {
//            mIsChildViewEnabled = !isChildViewEnabled;
//            if (getSelectedItemPosition() != INVALID_POSITION) {
//                refreshDrawableState();
//            }
//        }
    }
    
    private void positionSelector(int l, int t, int r, int b) {
    	if(!mSelectorRect.isEmpty())
	        mSelectorRect.set(l - mSelectionLeftPadding, t - mSelectionTopPadding, r
	                + mSelectionRightPadding, b + mSelectionBottomPadding);
    }
    
    /**
     * set selector rect padding, used for layout selector.
     * @param leftPadding
     * @param topPadding
     * @param rightPadding
     * @param bottomPadding
     */
    public void setSelectorPadding(int leftPadding, int topPadding, int rightPadding, int bottomPadding){
    	mSelectionLeftPadding = leftPadding;
    	mSelectionTopPadding = topPadding;
    	mSelectionRightPadding = rightPadding;
    	mSelectionBottomPadding = bottomPadding;
    }
    
    private void setupScalableView(View scalableView, View child) {
    	AbsSpinner.LayoutParams lp = (AbsSpinner.LayoutParams) child.getLayoutParams();
        if (lp == null) {
            lp = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
        }
        // Get measure specs
        int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec,
                mSpinnerPadding.top + mSpinnerPadding.bottom, lp.height);
        int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
                mSpinnerPadding.left + mSpinnerPadding.right, lp.width);

        // Measure child
        scalableView.measure(childWidthSpec, childHeightSpec);
        int height = scalableView.getMeasuredHeight();
        int l,r,t,b;
		l = child.getLeft();
		r = child.getRight();
		t = child.getBottom() + mScalableViewSpacing;
		b = t + height;
//		Log.d(TAG, "setUpScalableView l = " + l + ", r = " + r + ", t = " + t
//				+ ", b = " + b);
		scalableView.layout(l, t, r, b);
	}

    /**
     * set scalable view for this
     * @param position
     * @param child
     */
    void setScalableView(int position, View child){
    	if(mScalableAdapter != null){
	    	View scalView;
	    	scalView = mScalableAdapter.getView(position, null, this);
	    	Log.d(TAG, " getScalableView position = " + position+" child = " +getChildAt(position));
	        if(scalView!=null && child!=null){
	        	setupScalableView(scalView, child);
	        	mScalableView = scalView;
	        }else{
	        	mScalableView = null;
	        }
    	}
    }

    int getItemWidth(){
    	return mItemWidth;
    }

    int getItemHeight(){
    	return mItemHeight;
    }

    void clearScalableView(){
    	mScalableView = null;
    }

    /**
     * set scalable adapter, used for show scalable content
     * @param adapter
     */
    public void setScalableAdapter(Adapter adapter){
    	mScalableAdapter = adapter;
    	mScalableRecycler.clear();
    }

    /**
     * set vertical spacing of mScalableView & selected view
     * @param spacing
     */
    public void setScalableViewSpacing(int spacing){
    	mScalableViewSpacing = spacing;
    }


    /**
     * Clear out all children from the list
     */
    void resetList() {
        mDataChanged = false;
        mNeedSync = false;

        removeAllViewsInLayout();
        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        mOldItemCount = mItemCount;
        mItemCount = 0;

        setSelectedPositionInt(INVALID_POSITION);
        setNextSelectedPositionInt(INVALID_POSITION);
        invalidate();
    }

    /**
     * @see android.view.View#measure(int, int)
     *
     * Figure out the dimensions of this Spinner. The width comes from
     * the widthMeasureSpec as Spinnners can't have their width set to
     * UNSPECIFIED. The height is based on the height of the selected item
     * plus padding.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize;
        int heightSize;

        mSpinnerPadding.left = getPaddingLeft()/* > mSelectionLeftPadding ? getPaddingLeft()
                : mSelectionLeftPadding*/;
        mSpinnerPadding.top = getPaddingTop() /*> mSelectionTopPadding ? getPaddingTop()
                : mSelectionTopPadding*/;
        mSpinnerPadding.right = getPaddingRight()/* > mSelectionRightPadding ? getPaddingRight()
                : mSelectionRightPadding*/;
        mSpinnerPadding.bottom = getPaddingBottom()/* > mSelectionBottomPadding ? getPaddingBottom()
                : mSelectionBottomPadding*/;

        if (mDataChanged) {
            handleDataChanged();
        }

        int preferredHeight = 0;
        int preferredWidth = 0;
        boolean needsMeasuring = true;

        if(needMeasureSelectedView){
	        int selectedPosition = getSelectedItemPosition();
	        if (selectedPosition >= 0 && mAdapter != null && selectedPosition < mAdapter.getCount()) {
	            // Try looking in the recycler. (Maybe we were measured once already)
	            View view = mRecycler.getScrapView(selectedPosition);
	            if (view == null) {
	                // Make a new one
	                view = mAdapter.getView(selectedPosition, null, this);
	                if (view != null) {
	                	// Put in recycler for re-measuring and/or layout
	                	mRecycler.addScrapView(selectedPosition, view);
	                }
	            }


	            if (view != null) {
	                if (view.getLayoutParams() == null) {
	                    mBlockLayoutRequests = true;
	                    view.setLayoutParams(generateDefaultLayoutParams());
	                    mBlockLayoutRequests = false;
	                }
	                measureChild(view, widthMeasureSpec, heightMeasureSpec);

	                preferredHeight = getChildHeight(view) + mSpinnerPadding.top + mSpinnerPadding.bottom;
	                preferredWidth = getChildWidth(view) + mSpinnerPadding.left + mSpinnerPadding.right;

	                needsMeasuring = false;
	            }
	        }
        }

        if (needsMeasuring) {
            // No views -- just use padding
            preferredHeight = mSpinnerPadding.top + mSpinnerPadding.bottom;
            if (widthMode == View.MeasureSpec.UNSPECIFIED) {
                preferredWidth = mSpinnerPadding.left + mSpinnerPadding.right;
            }
        }

        preferredHeight = Math.max(preferredHeight, getSuggestedMinimumHeight());
        preferredWidth = Math.max(preferredWidth, getSuggestedMinimumWidth());

        heightSize = resolveSizeAndState(preferredHeight, heightMeasureSpec, 0);
        widthSize = resolveSizeAndState(preferredWidth, widthMeasureSpec, 0);

        setMeasuredDimension(widthSize, heightSize);
        mHeightMeasureSpec = heightMeasureSpec;
        mWidthMeasureSpec = widthMeasureSpec;
    }

    int getChildHeight(View child) {
        return child.getMeasuredHeight();
    }

    int getChildWidth(View child) {
        return child.getMeasuredWidth();
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    void recycleAllViews() {
        final int childCount = getChildCount();
        final AbsSpinner.RecycleBin recycleBin = mRecycler;
        final int position = mFirstPosition;

        // All views go in recycler
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            int index = position + i;
            recycleBin.addScrapView(index, v);
        }
    }

    /**
     * Jump directly to a specific item in the adapter data.
     */
    public void setSelection(int position, boolean animate) {
        // Animate only if requested position is already on screen somewhere
        boolean shouldAnimate = animate && mFirstPosition <= position &&
                position <= mFirstPosition + getChildCount() - 1;
        setSelectionInt(position, shouldAnimate);
    }

    @Override
    public void setSelection(int position) {
        setNextSelectedPositionInt(position);
        requestLayout();
        invalidate();
    }


    /**
     * Makes the item at the supplied position selected.
     *
     * @param position Position to select
     * @param animate Should the transition be animated
     *
     */
    void setSelectionInt(int position, boolean animate) {
        if (position != mOldSelectedPosition) {
            mBlockLayoutRequests = true;
            int delta  = position - mSelectedPosition;
            setNextSelectedPositionInt(position);
            layout(delta, animate);
            mBlockLayoutRequests = false;
        }
    }

    abstract void layout(int delta, boolean animate);

    @Override
    public View getSelectedView() {
        if (mItemCount > 0 && mSelectedPosition >= 0) {
            return getChildAt(mSelectedPosition - mFirstPosition);
        } else {
            return null;
        }
    }

    /**
     * Override to prevent spamming ourselves with layout requests
     * as we place views
     *
     * @see android.view.View#requestLayout()
     */
    @Override
    public void requestLayout() {
        if (!mBlockLayoutRequests) {
            super.requestLayout();
        }
    }

    @Override
    public SpinnerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public int getCount() {
        return mItemCount;
    }

    void hideSelector() {
        if (mSelectedPosition != INVALID_POSITION) {
        	//TODO by lawin
            if (mLayoutMode != LAYOUT_SPECIFIC) {
                mResurrectToPosition = mSelectedPosition;
            }
            if (mNextSelectedPosition >= 0 && mNextSelectedPosition != mSelectedPosition) {
                mResurrectToPosition = mNextSelectedPosition;
            }
            setSelectedPositionInt(INVALID_POSITION);
            setNextSelectedPositionInt(INVALID_POSITION);
            mSelectedLeft = 0;
        }
    }

    /**
     * Maps a point to a position in the list.
     *
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The position of the item which contains the specified point, or
     *         {@link #INVALID_POSITION} if the point does not intersect an item.
     */
    public int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return mFirstPosition + i;
                }
            }
        }
        return INVALID_POSITION;
    }

    static class SavedState extends View.BaseSavedState {
        long selectedId;
        int position;

        /**
         * Constructor called from {@link AbsSpinner#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            selectedId = in.readLong();
            position = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(selectedId);
            out.writeInt(position);
        }

        @Override
        public String toString() {
            return "AbsSpinner.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " selectedId=" + selectedId
                    + " position=" + position + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.selectedId = getSelectedItemId();
        if (ss.selectedId >= 0) {
            ss.position = getSelectedItemPosition();
        } else {
            ss.position = INVALID_POSITION;
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.selectedId >= 0) {
            mDataChanged = true;
            mNeedSync = true;
            mSyncRowId = ss.selectedId;
            mSyncPosition = ss.position;
            mSyncMode = SYNC_SELECTED_POSITION;
            requestLayout();
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams{
        /**
         * View type for this view, as returned by
         * {@link android.widget.Adapter#getItemViewType(int) }
         */
        int viewType;

        /**
         * The position the view was removed from when pulled out of the
         * scrap heap.
         * @hide
         */
        int scrappedFromPosition;

        /**
         * The ID the view represents
         */
        long itemId = -1;

        /**
         * When an AbsSpinner is measured with an AT_MOST measure spec, it needs
         * to obtain children views to measure itself. When doing so, the children
         * are not attached to the window, but put in the recycler which assumes
         * they've been attached before. Setting this flag will force the reused
         * view to be attached to the window rather than just attached to the
         * parent.
         */
        @ViewDebug.ExportedProperty(category = "list")
        boolean forceAdd;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.viewType = viewType;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    static View retrieveFromScrap(SparseArray<View> scrapViews, int position) {
        int size = scrapViews.size();
        if (size > 0) {
            // See if we still have a view for this position.
            for (int i = 0; i < size; i++) {
            	 int key = scrapViews.keyAt(i);
                View view = scrapViews.get(key);
                if (((AbsSpinner.LayoutParams)view.getLayoutParams()).scrappedFromPosition == position) {
                    scrapViews.remove(key);
                    return view;
                }
            }

            View view = scrapViews.get(position);
            scrapViews.remove(position);
            return view;
        } else {
            return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
    	super.onAttachedToWindow();
    	mIsAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
    	super.onDetachedFromWindow();
    	mRecycler.clear();
    	mIsAttached = false;
    }

    class RecycleBin {

        private SparseArray<View> mCurrentScrap;
        private SparseArray<View>[] mScrapViews;

        private SparseArray<View> mTransientStateViews;

        private int mViewTypeCount;

        public RecycleBin() {
        	initReflectMethod();
		}

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0;
        }

        View getTransientStateView(int position) {
            if (mTransientStateViews == null) {
                return null;
            }
            final int index = mTransientStateViews.indexOfKey(position);
            if (index < 0) {
                return null;
            }
            final View result = mTransientStateViews.valueAt(index);
            mTransientStateViews.remove(position);
            return result;
        }

        public void addScrapView(int position, View scrap) {
			AbsSpinner.LayoutParams lp = (AbsSpinner.LayoutParams) scrap.getLayoutParams();
			if (lp == null) {
				return;
			}

			lp.scrappedFromPosition = position;
			int viewType = lp.viewType;
//			final boolean scrapHasTransientState = reflectViewHasTransientState(scrap);
//			if (!shouldRecycleViewType(viewType) || scrapHasTransientState) {
//				// if (viewType != ITEM_VIEW_TYPE_HEADER_OR_FOOTER ||
//				// scrapHasTransientState) {
//				// if (mSkippedScrap == null) {
//				// mSkippedScrap = new ArrayList<View>();
//				// }
//				// mSkippedScrap.add(scrap);
//				// }
//				if (scrapHasTransientState) {
//					if (mTransientStateViews == null) {
//						mTransientStateViews = new SparseArray<View>();
//					}
//					 scrap.dispatchStartTemporaryDetach();
//					mTransientStateViews.put(position, scrap);
//				}
//				return;
//			}
            
            
            if(mViewTypeCount == 1){
            	mCurrentScrap.put(position, scrap);
            } else {
            	mScrapViews[viewType].put(position, scrap);
            }
        }
        
        Method reflectViewHasTransientStateMethod;
        
        private void initReflectMethod(){
        	if(Build.VERSION.SDK_INT >= 16){
	        	try {
					reflectViewHasTransientStateMethod = View.class.getDeclaredMethod("hasTransientState");
					reflectViewHasTransientStateMethod.setAccessible(true);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        
        public boolean reflectViewHasTransientState(View view){
			if(Build.VERSION.SDK_INT >= 16 && reflectViewHasTransientStateMethod != null){
				try {
					Boolean state = (Boolean)reflectViewHasTransientStateMethod.invoke(view);
					return state.booleanValue();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
        	return false;
        }
        
        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            //noinspection unchecked
            SparseArray<View>[] scrapViews = new SparseArray[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new SparseArray<View>();
            }
            mViewTypeCount = viewTypeCount;
            mCurrentScrap = scrapViews[0];
            mScrapViews = scrapViews;
        }
        
        View getScrapView(int position) {
            // System.out.print("Looking for " + position);
			View result = null;
			if (mViewTypeCount == 1) {
				return retrieveFromScrap(mCurrentScrap, position);
			} else {
				int whichScrap = mAdapter.getItemViewType(position);
				if (whichScrap >= 0 && whichScrap < mScrapViews.length) {
					return retrieveFromScrap(mScrapViews[whichScrap], position);
				}
			}
			return result;
        }
        
        /**
         * Dump any currently saved views with transient state.
         */
        void clearTransientStateViews() {
            if (mTransientStateViews != null) {
                mTransientStateViews.clear();
            }
        }

        void clear() {
			if (mViewTypeCount == 1) {
				final SparseArray<View> currentScrap = mCurrentScrap;
				final int count = currentScrap.size();
				for (int i = 0; i < count; i++) {
					final View view = currentScrap.valueAt(i);
					if (view != null) {
						removeDetachedView(view, true);
					}
				}
				currentScrap.clear();
			} else {
				final SparseArray<View>[] scrapViews = mScrapViews;
				int count = 0;
				for (int i = 0; i < mViewTypeCount; i++) {
					final SparseArray<View> currentScrap = scrapViews[i];
					count = currentScrap.size();
					for (int j = 0; j < count; j++) {
						final View view = currentScrap.valueAt(j);
						if (view != null) {
							removeDetachedView(view, true);
						}
					}
					currentScrap.clear();
				}
			}

			clearTransientStateViews();
        }
    }
}

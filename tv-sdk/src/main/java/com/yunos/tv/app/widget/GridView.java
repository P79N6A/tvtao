package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Checkable;

public class GridView extends AbsListView {
	private static final String TAG = "GridView";
	private static final boolean DEBUG = false;

	/**
	 * Creates as many columns as can fit on screen.
	 * 
	 * @see #setNumColumns(int)
	 */
	public static final int AUTO_FIT = -1;

	/**
	 * Disables stretching.
	 * 
	 * @see #setStretchMode(int)
	 */
	public static final int NO_STRETCH = 0;
	/**
	 * Stretches the spacing between columns.
	 * 
	 * @see #setStretchMode(int)
	 */
	public static final int STRETCH_SPACING = 1;
	/**
	 * Stretches columns.
	 * 
	 * @see #setStretchMode(int)
	 */
	public static final int STRETCH_COLUMN_WIDTH = 2;
	/**
	 * Stretches the spacing between columns. The spacing is uniform.
	 * 
	 * @see #setStretchMode(int)
	 */
	public static final int STRETCH_SPACING_UNIFORM = 3;

	private int mNumColumns = AUTO_FIT;
	private int mVerticalSpacing = 0;

	private View mReferenceView = null;
	private View mReferenceViewInSelectedRow = null;

	private int mColumnWidth;
	private int mRequestedColumnWidth = -1;
	private int mRequestedNumColumns;

	private int mHorizontalSpacing = 0;
	private int mRequestedHorizontalSpacing;

	private int mStretchMode = STRETCH_COLUMN_WIDTH;

	private int mMinLastPos = -1;
	private int mMinFirstPos = Integer.MAX_VALUE;
	public GridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setMinLastPos(int pos){
		mMinLastPos = pos;
	}
	
	public void setMinFirstPos(int pos){
		mMinFirstPos = pos;
	}
	
	public void setReferenceViewInSelectedRow(View selectedRowView){
		mReferenceViewInSelectedRow = selectedRowView;
	}
	
	
	/**
	 * Set the number of columns in the grid
	 * 
	 * @param numColumns
	 *            The desired number of columns.
	 * 
	 * @attr ref android.R.styleable#GridView_numColumns
	 */
	public void setNumColumns(int numColumns) {
		if (numColumns != mRequestedNumColumns) {
			mRequestedNumColumns = numColumns;
			requestLayoutIfNecessary();
		}
	}

	/**
	 * Set the width of columns in the grid.
	 * 
	 * @param columnWidth
	 *            The column width, in pixels.
	 * 
	 * @attr ref android.R.styleable#GridView_columnWidth
	 */
	public void setColumnWidth(int columnWidth) {
		if (columnWidth != mRequestedColumnWidth) {
			mRequestedColumnWidth = columnWidth;
			requestLayoutIfNecessary();
		}
	}

	/**
	 * Set the amount of vertical (y) spacing to place between each item in the
	 * grid.
	 * 
	 * @param verticalSpacing
	 *            The amount of vertical space between items, in pixels.
	 * 
	 * @see #getVerticalSpacing()
	 * 
	 * @attr ref android.R.styleable#GridView_verticalSpacing
	 */
	public void setVerticalSpacing(int verticalSpacing) {
		if (verticalSpacing != mVerticalSpacing) {
			mVerticalSpacing = verticalSpacing;
			requestLayoutIfNecessary();
		}
	}

    /**
     * Set the amount of horizontal (x) spacing to place between each item
     * in the grid.
     *
     * @param horizontalSpacing The amount of horizontal space between items,
     * in pixels.
     *
     * @attr ref android.R.styleable#GridView_horizontalSpacing
     */
    public void setHorizontalSpacing(int horizontalSpacing) {
        if (horizontalSpacing != mRequestedHorizontalSpacing) {
            mRequestedHorizontalSpacing = horizontalSpacing;
            requestLayoutIfNecessary();
        }
    }

    /**
     * Returns the amount of horizontal spacing currently used between each item in the grid.
     *
     * <p>This is only accurate for the current layout. If {@link #setHorizontalSpacing(int)}
     * has been called but layout is not yet complete, this method may return a stale value.
     * To get the horizontal spacing that was explicitly requested use
     * {@link #getRequestedHorizontalSpacing()}.</p>
     *
     * @return Current horizontal spacing between each item in pixels
     *
     * @see #setHorizontalSpacing(int)
     * @see #getRequestedHorizontalSpacing()
     *
     * @attr ref android.R.styleable#GridView_horizontalSpacing
     */
    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    /**
     * Returns the requested amount of horizontal spacing between each item in the grid.
     *
     * <p>The value returned may have been supplied during inflation as part of a style,
     * the default GridView style, or by a call to {@link #setHorizontalSpacing(int)}.
     * If layout is not yet complete or if GridView calculated a different horizontal spacing
     * from what was requested, this may return a different value from
     * {@link #getHorizontalSpacing()}.</p>
     *
     * @return The currently requested horizontal spacing between items, in pixels
     *
     * @see #setHorizontalSpacing(int)
     * @see #getHorizontalSpacing()
     *
     * @attr ref android.R.styleable#GridView_horizontalSpacing
     */
    public int getRequestedHorizontalSpacing() {
        return mRequestedHorizontalSpacing;
    }
	
	public int getVerticalSpacing(){
		return mVerticalSpacing;
	}
	
	
	public int getColumnNum(){
		return mNumColumns;
	}
	
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		int closestChildIndex = -1;
		if (gainFocus && previouslyFocusedRect != null) {
			previouslyFocusedRect.offset(getScrollX(), getScrollY());

			// figure out which item should be selected based on previously
			// focused rect
			Rect otherRect = mTempRect;
			int minDistance = Integer.MAX_VALUE;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				// only consider view's on appropriate edge of grid
				if (!isCandidateSelection(i, direction)) {
					continue;
				}

				final View other = getChildAt(i);
				boolean isHeaderOrFooter = false;
				if(mHeaderViewInfos != null && mHeaderViewInfos.size() > 0){
					for(int h = 0; h < mHeaderViewInfos.size(); h++){
						FixedViewInfo info = mHeaderViewInfos.get(h);
						if(info != null && info.view.equals(other)){
							isHeaderOrFooter = true;
						}
					}
				}
				if(!isHeaderOrFooter && mFooterViewInfos != null && mFooterViewInfos.size() > 0){
					for(int f = 0; f < mFooterViewInfos.size(); f++){
						FixedViewInfo info = mFooterViewInfos.get(f);
						if(info != null && info.view.equals(other)){
							isHeaderOrFooter = true;
						}
					}			
				}
				
				if(isHeaderOrFooter && other instanceof ViewGroup){
					ViewGroup headerorFooterView = (ViewGroup)other;
					int headerorFooterCount = (headerorFooterView == null ? 0 : headerorFooterView.getChildCount());
					for(int childIndex = 0; childIndex < headerorFooterCount; childIndex ++){
						View childView = headerorFooterView.getChildAt(childIndex);
						childView.getDrawingRect(otherRect);
						offsetDescendantRectToMyCoords(childView, otherRect);
						int distance = getDistance(previouslyFocusedRect, otherRect, direction);
						if (distance < minDistance) {
							minDistance = distance;
							closestChildIndex = i;
						}
					}
				}
				else{
					other.getDrawingRect(otherRect);
					offsetDescendantRectToMyCoords(other, otherRect);
					int distance = getDistance(previouslyFocusedRect, otherRect, direction);
	
					if (distance < minDistance) {
						minDistance = distance;
						closestChildIndex = i;
					}
				}
			}
		}

		if (closestChildIndex >= 0) {
			setHeaderSelection(closestChildIndex + mFirstPosition);
		} else {
			if(getChildCount() > 0 && gainFocus == true){
				setHeaderSelection(mFirstPosition);
			}
			//requestLayout();
		}
	}

	
	
	private void setHeaderSelection(int selectedPos){
		int headerCount = getHeaderViewsCount();
		if(selectedPos < headerCount){
			for (int i = selectedPos; i < headerCount; i++) {
				View headerView = mHeaderViewInfos.get(i).view;
				if(headerView.isFocusable()){
					setSelection(i);
					return;
				}
			}
			//if all headerView focus disable then selected first adapterView
			setSelection(headerCount);
		}
		else{
			setSelection(selectedPos);
		}
	}
	
	
	/**
	 * Is childIndex a candidate for next focus given the direction the focus
	 * change is coming from?
	 * 
	 * @param childIndex
	 *            The index to check.
	 * @param direction
	 *            The direction, one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT,
	 *            FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}
	 * @return Whether childIndex is a candidate.
	 */
	private boolean isCandidateSelection(int childIndex, int direction) {
		final int count = getChildCount();
		final int invertedIndex = count - 1 - childIndex;

		int rowStart;
		int rowEnd;

		if (!mStackFromBottom) {
			rowStart = childIndex - (childIndex % mNumColumns);
			rowEnd = Math.max(rowStart + mNumColumns - 1, count);
		} else {
			rowEnd = count - 1 - (invertedIndex - (invertedIndex % mNumColumns));
			rowStart = Math.max(0, rowEnd - mNumColumns + 1);
		}

		switch (direction) {
		case FOCUS_RIGHT:
			// coming from left, selection is only valid if it is on left
			// edge
			return childIndex == rowStart;
		case FOCUS_DOWN:
			// coming from top; only valid if in top row
			return rowStart == 0;
		case FOCUS_LEFT:
			// coming from right, must be on right edge
			return childIndex == rowEnd;
		case FOCUS_UP:
			// coming from bottom, need to be in last row
			return rowEnd == count - 1;
		case FOCUS_FORWARD:
			// coming from top-left, need to be first in top row
			return childIndex == rowStart && rowStart == 0;
		case FOCUS_BACKWARD:
			// coming from bottom-right, need to be last in bottom row
			return childIndex == rowEnd && rowEnd == count - 1;
		default:
			throw new IllegalArgumentException("direction must be one of " + "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, "
					+ "FOCUS_FORWARD, FOCUS_BACKWARD}.");
		}
	}

	protected int getFillGapNextChildIndex(boolean isDown){
		if(isDown){
			return getChildCount() - 1;
		}
		else{
			return 0;
		}
	}
	
	@Override
	void fillGap(boolean isDown) {
		if (DEBUG) {
			Log.d(TAG, "fillGap: mFirstPosition = " + mFirstPosition);
		}
		final int numColumns = mNumColumns;
		final int verticalSpacing = mVerticalSpacing;

		final int count = getChildCount();

		if (isDown) {
			int paddingTop = 0;
			if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
				paddingTop = getListPaddingTop();
			}
			// final int startOffset = count > 0 ? getChildAt(count -
			// 1).getBottom() : paddingTop;
			int position = mFirstPosition + count;
			if (mStackFromBottom) {
				position += numColumns - 1;
			}

			// fillDownWithHeaderOrFooter(position, getNextTop(position,
			// getChildAt(count - 1)));
			fillDown(position, getNextTop(position, getChildAt(getFillGapNextChildIndex(isDown))));
			correctTooHigh(numColumns, verticalSpacing, getChildCount());
		} else {
			int paddingBottom = 0;
			if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
				paddingBottom = getListPaddingBottom();
			}
			// final int startOffset = count > 0 ? getChildAt(0).getTop() :
			// getHeight() - paddingBottom;
			int position = mFirstPosition;
			if (!mStackFromBottom) {
				// if (position < getHeaderViewsCount()) {
				// position--;
				// } else if (position - numColumns < getHeaderViewsCount()) {
				// position = getHeaderViewsCount();
				// } else {
				// position -= numColumns;
				// }
			} else {
				position--;
			}

			fillUpWithHeaderOrFooter(position, getNextBottom(position, getChildAt(getFillGapNextChildIndex(isDown))));
			// fillUp(position, getNextBottom(position, getChildAt(0)));
			correctTooLow(numColumns, verticalSpacing, getChildCount());
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Sets up mListPadding
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode == MeasureSpec.UNSPECIFIED) {
			if (mColumnWidth > 0) {
				widthSize = mColumnWidth + mListPadding.left + mListPadding.right;
			} else {
				widthSize = mListPadding.left + mListPadding.right;
			}
			widthSize += getVerticalScrollbarWidth();
		}

		int childWidth = widthSize - mListPadding.left - mListPadding.right;

		int childHeight = 0;
		int childState = 0;

		mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
		final int count = mItemCount;
		if (count > getHeaderViewsCount() + getFooterViewsCount()) {
			final View child = obtainView(getHeaderViewsCount(), mIsScrap);

			AbsListView.LayoutParams p = (AbsListView.LayoutParams) child.getLayoutParams();
			if (p == null) {
				p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
				child.setLayoutParams(p);
			}
			p.viewType = mAdapter.getItemViewType(0);
			p.forceAdd = true;

			int childHeightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.height);
			int childWidthSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.width);
			child.measure(childWidthSpec, childHeightSpec);

			childHeight = child.getMeasuredHeight();
			if (mNumColumns == AUTO_FIT && mRequestedColumnWidth < 0) {
				setColumnWidth(child.getMeasuredWidth());
			}
			childState = combineMeasuredStates(childState, child.getMeasuredState());

			if (mRecycler.shouldRecycleViewType(p.viewType)) {
				mRecycler.addScrapView(child, -1);
			}
		}

		if (heightMode == MeasureSpec.UNSPECIFIED) {
			heightSize = mListPadding.top + mListPadding.bottom + childHeight + getVerticalFadingEdgeLength() * 2;
		}

		if (heightMode == MeasureSpec.AT_MOST) {
			int ourSize = mListPadding.top + mListPadding.bottom;

			final int numColumns = mNumColumns;
			for (int i = 0; i < count; i += numColumns) {
				ourSize += childHeight;
				if (i + numColumns < count) {
					ourSize += mVerticalSpacing;
				}
				if (ourSize >= heightSize) {
					ourSize = heightSize;
					break;
				}
			}
			heightSize = ourSize;
		}
		boolean didNotInitiallyFit = determineColumns(childWidth);
		if (widthMode == MeasureSpec.AT_MOST && mRequestedNumColumns != AUTO_FIT) {
			int ourSize = (mRequestedNumColumns * mColumnWidth) + ((mRequestedNumColumns - 1) * mHorizontalSpacing) + mListPadding.left
					+ mListPadding.right;
			if (ourSize > widthSize || didNotInitiallyFit) {
				widthSize |= MEASURED_STATE_TOO_SMALL;
			}
		}

		setMeasuredDimension(widthSize, heightSize);
		mWidthMeasureSpec = widthMeasureSpec;
	}

	private boolean determineColumns(int availableSpace) {
		final int requestedHorizontalSpacing = mRequestedHorizontalSpacing;
		final int stretchMode = mStretchMode;
		final int requestedColumnWidth = mRequestedColumnWidth;
		boolean didNotInitiallyFit = false;

		if (mRequestedNumColumns == AUTO_FIT) {
			if (requestedColumnWidth > 0) {
				// Client told us to pick the number of columns
				mNumColumns = (availableSpace + requestedHorizontalSpacing) / (requestedColumnWidth + requestedHorizontalSpacing);
			} else {
				// Just make up a number if we don't have enough info
				mNumColumns = 2;
			}
		} else {
			// We picked the columns
			mNumColumns = mRequestedNumColumns;
		}

		if (mNumColumns <= 0) {
			mNumColumns = 1;
		}

		switch (stretchMode) {
		case NO_STRETCH:
			// Nobody stretches
			mColumnWidth = requestedColumnWidth;
			mHorizontalSpacing = requestedHorizontalSpacing;
			break;

		default:
			int spaceLeftOver = availableSpace - (mNumColumns * requestedColumnWidth) - ((mNumColumns - 1) * requestedHorizontalSpacing);

			if (spaceLeftOver < 0) {
				didNotInitiallyFit = true;
			}

			switch (stretchMode) {
			case STRETCH_COLUMN_WIDTH:
				// Stretch the columns
				mColumnWidth = requestedColumnWidth + spaceLeftOver / mNumColumns;
				mHorizontalSpacing = requestedHorizontalSpacing;
				break;

			case STRETCH_SPACING:
				// Stretch the spacing between columns
				mColumnWidth = requestedColumnWidth;
				if (mNumColumns > 1) {
					mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver / (mNumColumns - 1);
				} else {
					mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
				}
				break;

			case STRETCH_SPACING_UNIFORM:
				// Stretch the spacing between columns
				mColumnWidth = requestedColumnWidth;
				if (mNumColumns > 1) {
					mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver / (mNumColumns + 1);
				} else {
					mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
				}
				break;
			}

			break;
		}
		return didNotInitiallyFit;
	}

	@Override
    protected void attachLayoutAnimationParameters(View child,
            ViewGroup.LayoutParams params, int index, int count) {

        GridLayoutAnimationController.AnimationParameters animationParams =
                (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

        if (animationParams == null) {
            animationParams = new GridLayoutAnimationController.AnimationParameters();
            params.layoutAnimationParameters = animationParams;
        }

        animationParams.count = count;
        animationParams.index = index;
        animationParams.columnsCount = mNumColumns;
        animationParams.rowsCount = count / mNumColumns;

        if (!mStackFromBottom) {
            animationParams.column = index % mNumColumns;
            animationParams.row = index / mNumColumns;
        } else {
            final int invertedIndex = count - 1 - index;

            animationParams.column = mNumColumns - 1 - (invertedIndex % mNumColumns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / mNumColumns;
        }
    }
	
	@Override
	protected void layoutChildren() {
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
				if (index >= 0 && index < childCount && mAdapter != null && index < mAdapter.getCount()) {
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
	 * Make sure views are touching the top or bottom edge, as appropriate for
	 * our gravity
	 */
	protected void adjustViewsUpOrDown() {
		final int childCount = getChildCount();

		if (childCount > 0) {
			int delta;
			View child;

			if (!mStackFromBottom) {
				// Uh-oh -- we came up short. Slide all views up to make them
				// align with the top
				child = getChildAt(0);
				delta = child.getTop() - mListPadding.top;
				if (mFirstPosition != 0) {
					// It's OK to have some space above the first item if it is
					// part of the vertical spacing
					delta -= mVerticalSpacing;
				}
				if (delta < 0) {
					// We only are looking to see if we are too low, not too
					// high
					delta = 0;
				}
			} else {
				// we are too high, slide all views down to align with bottom
				child = getChildAt(childCount - 1);
				delta = child.getBottom() - (getHeight() - mListPadding.bottom);

				if (mFirstPosition + childCount < mItemCount) {
					// It's OK to have some space below the last item if it is
					// part of the vertical spacing
					delta += mVerticalSpacing;
				}

				if (delta > 0) {
					// We only are looking to see if we are too high, not too
					// low
					delta = 0;
				}
			}

			if (delta != 0) {
				offsetChildrenTopAndBottom(-delta);
			}
		}
	}

	/**
	 * Fills the grid based on positioning the new selection at a specific
	 * location. The selection may be moved so that it does not intersect the
	 * faded edges. The grid is then filled upwards and downwards from there.
	 * 
	 * @param selectedTop
	 *            Where the selected item should be
	 * @param childrenTop
	 *            Where to start drawing children
	 * @param childrenBottom
	 *            Last pixel where children can be drawn
	 * @return The view that currently has selection
	 */
	protected View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
		final int fadingEdgeLength = getVerticalFadingEdgeLength();
		int selectedPosition = mSelectedPosition;
		int numColumns = mNumColumns;
		final int verticalSpacing = mVerticalSpacing;

		int rowStart = mSelectedPosition;
		int rowEnd = -1;

		if (!mStackFromBottom) {
			if (selectedPosition >= getHeaderViewsCount()) {
				selectedPosition -= getHeaderViewsCount();
				rowStart = selectedPosition - (selectedPosition % numColumns);
				rowStart += getHeaderViewsCount();
			}
		} else {
			int invertedSelection = mItemCount - 1 - selectedPosition;

			rowEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
			rowStart = Math.max(0, rowEnd - numColumns + 1);
		}

		View sel;
		View referenceView;

		int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
		int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);

		if (rowStart < getHeaderViewsCount() || rowStart > mItemCount - getFooterViewsCount() - 1) {
			sel = makeHeaderOrFooter(mStackFromBottom ? rowEnd : rowStart, selectedTop, true);
			numColumns = 1;
		} else {
			sel = makeRow(mStackFromBottom ? rowEnd : rowStart, selectedTop, true);
		}
		// Possibly changed again in fillUp if we add rows above this one.
		mFirstPosition = rowStart;

		referenceView = mReferenceView;
		adjustForTopFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
		adjustForBottomFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);

		if (!mStackFromBottom) {
			fillUpWithHeaderOrFooter(rowStart, getNextBottom(rowStart, referenceView));

			adjustViewsUpOrDown();

			fillDownWithHeaderOrFooter(rowStart, getNextTop(rowStart, referenceView));
			// fillDown(rowStart + numColumns, referenceView.getBottom() +
			// verticalSpacing);
		} else {
			fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
			adjustViewsUpOrDown();
			fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
		}

		return sel;
	}

	/**
	 * Move all views upwards so the selected row does not interesect the bottom
	 * fading edge (if necessary).
	 * 
	 * @param childInSelectedRow
	 *            A child in the row that contains the selection
	 * @param topSelectionPixel
	 *            The topmost pixel we can draw the selection into
	 * @param bottomSelectionPixel
	 *            The bottommost pixel we can draw the selection into
	 */
	protected void adjustForBottomFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
		// Some of the newly selected item extends below the bottom of the
		// list
		if (childInSelectedRow.getBottom() > bottomSelectionPixel) {

			// Find space available above the selection into which we can
			// scroll upwards
			int spaceAbove = childInSelectedRow.getTop() - topSelectionPixel;

			// Find space required to bring the bottom of the selected item
			// fully into view
			int spaceBelow = childInSelectedRow.getBottom() - bottomSelectionPixel;
			int offset = Math.min(spaceAbove, spaceBelow);

			// Now offset the selected item to get it into view
			offsetChildrenTopAndBottom(-offset);
		}
	}

	/**
	 * Move all views upwards so the selected row does not interesect the top
	 * fading edge (if necessary).
	 * 
	 * @param childInSelectedRow
	 *            A child in the row that contains the selection
	 * @param topSelectionPixel
	 *            The topmost pixel we can draw the selection into
	 * @param bottomSelectionPixel
	 *            The bottommost pixel we can draw the selection into
	 */
	protected void adjustForTopFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
		// Some of the newly selected item extends above the top of the list
		int top = childInSelectedRow.getTop();
		if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance){
			top += ((GridViewHeaderViewExpandDistance)childInSelectedRow).getUpExpandDistance();
		}
		
		int bottom = childInSelectedRow.getBottom();
		if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance){
			bottom -= ((GridViewHeaderViewExpandDistance)childInSelectedRow).getDownExpandDistance();
		}
		if (top < topSelectionPixel) {
			// Find space required to bring the top of the selected item
			// fully into view
			int spaceAbove = topSelectionPixel - top;

			// Find space available below the selection into which we can
			// scroll downwards
			int spaceBelow = bottomSelectionPixel - bottom;
			int offset = Math.min(spaceAbove, spaceBelow);

			// Now offset the selected item to get it into view
			offsetChildrenTopAndBottom(offset);
		}
	}

	/**
	 * Calculate the bottom-most pixel we can draw the selection into
	 * 
	 * @param childrenBottom
	 *            Bottom pixel were children can be drawn
	 * @param fadingEdgeLength
	 *            Length of the fading edge in pixels, if present
	 * @param numColumns
	 *            Number of columns in the grid
	 * @param rowStart
	 *            The start of the row that will contain the selection
	 * @return The bottom-most pixel we can draw the selection into
	 */
	private int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength, int numColumns, int rowStart) {
		// Last pixel we can draw the selection into
		int bottomSelectionPixel = childrenBottom;
		if (rowStart + numColumns - 1 < mItemCount - 1) {
			bottomSelectionPixel -= fadingEdgeLength;
		}
		return bottomSelectionPixel;
	}

	/**
	 * Calculate the top-most pixel we can draw the selection into
	 * 
	 * @param childrenTop
	 *            Top pixel were children can be drawn
	 * @param fadingEdgeLength
	 *            Length of the fading edge in pixels, if present
	 * @param rowStart
	 *            The start of the row that will contain the selection
	 * @return The top-most pixel we can draw the selection into
	 */
	private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int rowStart) {
		// first pixel we can draw the selection into
		int topSelectionPixel = childrenTop;
		if (rowStart > 0) {
			topSelectionPixel += fadingEdgeLength;
		}
		return topSelectionPixel;
	}

	protected View fillSelection(int childrenTop, int childrenBottom) {
		final int selectedPosition = reconcileSelectedPosition();
		final int numColumns = mNumColumns;
		final int verticalSpacing = mVerticalSpacing;

		int rowStart;
		int rowEnd = -1;

		if (!mStackFromBottom) {
			rowStart = getRowStart(selectedPosition);//selectedPosition - (selectedPosition % numColumns);
		} else {
			final int invertedSelection = mItemCount - 1 - selectedPosition;

			rowEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
			rowStart = Math.max(0, rowEnd - numColumns + 1);
		}

		final int fadingEdgeLength = getVerticalFadingEdgeLength();
		final int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);

		final View sel = makeRow(mStackFromBottom ? rowEnd : rowStart, topSelectionPixel, true);
		mFirstPosition = rowStart;

		final View referenceView = mReferenceView;

		if (!mStackFromBottom) {
			fillDownWithHeaderOrFooter(rowStart, getNextTop(rowStart, referenceView));
			// fillDown(rowStart + numColumns, referenceView.getBottom() +
			// verticalSpacing);
			pinToBottom(childrenBottom);
			fillUpWithHeaderOrFooter(rowStart, getNextBottom(rowStart, referenceView));
			// fillUp(rowStart - numColumns, referenceView.getTop() -
			// verticalSpacing);
			adjustViewsUpOrDown();
		} else {
			final int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
			final int offset = bottomSelectionPixel - referenceView.getBottom();
			offsetChildrenTopAndBottom(offset);
			fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
			pinToTop(childrenTop);
			fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
			adjustViewsUpOrDown();
		}

		return sel;
	}

	protected View fillSelectionMiddle(int childrenTop, int childrenBottom) {
		int height = getHeight();
		final int selectedPosition = reconcileSelectedPosition();
		final int numColumns = mNumColumns;
		final int verticalSpacing = mVerticalSpacing;

		int rowStart;
		int rowEnd = -1;

		if (!mStackFromBottom) {
			rowStart = getRowStart(selectedPosition);//selectedPosition - (selectedPosition % numColumns);
		} else {
			final int invertedSelection = mItemCount - 1 - selectedPosition;

			rowEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
			rowStart = Math.max(0, rowEnd - numColumns + 1);
		}

		final int fadingEdgeLength = getVerticalFadingEdgeLength();
		final int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
		final int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);
		
		final View sel = makeRow(mStackFromBottom ? rowEnd : rowStart, topSelectionPixel, true);
		for(int i = 0; i < getChildCount(); i++){
			View childView = getChildAt(i);
			adjustForTopFadingEdge(childView, topSelectionPixel, bottomSelectionPixel);
			adjustForBottomFadingEdge(childView, topSelectionPixel, bottomSelectionPixel);
		}

		mFirstPosition = rowStart;

		final View referenceView = mReferenceView;

		if (!mStackFromBottom) {
			fillDownWithHeaderOrFooter(rowStart, getNextTop(rowStart, referenceView));
			// fillDown(rowStart + numColumns, referenceView.getBottom() +
			// verticalSpacing);
			pinToBottom(childrenBottom);
			fillUpWithHeaderOrFooter(rowStart, getNextBottom(rowStart, referenceView));
			// fillUp(rowStart - numColumns, referenceView.getTop() -
			// verticalSpacing);
			adjustViewsUpOrDown();
		} else {
			final int offset = bottomSelectionPixel - referenceView.getBottom();
			offsetChildrenTopAndBottom(offset);
			fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
			pinToTop(childrenTop);
			fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
			adjustViewsUpOrDown();
		}

		return sel;
	}
	
	private void pinToTop(int childrenTop) {
		if (mFirstPosition == 0) {
			final int top = getChildAt(0).getTop();
			final int offset = childrenTop - top;
			if (offset < 0) {
				offsetChildrenTopAndBottom(offset);
			}
		}
	}

	private void pinToBottom(int childrenBottom) {
		final int count = getChildCount();
		if (mFirstPosition + count == mItemCount) {
			final int bottom = getChildAt(count - 1).getBottom();
			final int offset = childrenBottom - bottom;
			if (offset > 0) {
				offsetChildrenTopAndBottom(offset);
			}
		}
	}

	@Override
	int findMotionRow(int y) {
		final int childCount = getChildCount();
		if (childCount > 0) {

			final int numColumns = mNumColumns;
			if (!mStackFromBottom) {
				for (int i = 0; i < childCount; i += numColumns) {
					if (y <= getChildAt(i).getBottom()) {
						return mFirstPosition + i;
					}
				}
			} else {
				for (int i = childCount - 1; i >= 0; i -= numColumns) {
					if (y >= getChildAt(i).getTop()) {
						return mFirstPosition + i;
					}
				}
			}
		}
		return INVALID_POSITION;
	}

	/**
	 * Layout during a scroll that results from tracking motion events. Places
	 * the mMotionPosition view at the offset specified by mMotionViewTop, and
	 * then build surrounding views from there.
	 * 
	 * @param position
	 *            the position at which to start filling
	 * @param top
	 *            the top of the view at that position
	 * @return The selected view, or null if the selected view is outside the
	 *         visible area.
	 */
	protected View fillSpecific(int position, int top) {
		int numColumns = mNumColumns;

		int motionRowStart = position;
		int motionRowEnd = -1;

		if (!mStackFromBottom) {
			if (position >= getHeaderViewsCount()) {
				position -= getHeaderViewsCount();
				motionRowStart = position - (position % numColumns);
				motionRowStart += getHeaderViewsCount();
			}
		} else {
			final int invertedSelection = mItemCount - 1 - position;

			motionRowEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
			motionRowStart = Math.max(0, motionRowEnd - numColumns + 1);
		}

		// View temp = makeRow(mStackFromBottom ? motionRowEnd : motionRowStart,
		// top, true);
		View temp = null;
		if (motionRowStart < getHeaderViewsCount() || motionRowStart > mItemCount - getFooterViewsCount() - 1) {
			temp = makeHeaderOrFooter(motionRowStart, top, true);
			numColumns = 1;
		} else {
			temp = makeRow(mStackFromBottom ? motionRowEnd : motionRowStart, top, true);
		}

		// Possibly changed again in fillUp if we add rows above this one.
		mFirstPosition = motionRowStart;

		final View referenceView = mReferenceView;
		// We didn't have anything to layout, bail out
		if (referenceView == null) {
			return null;
		}

		final int verticalSpacing = mVerticalSpacing;

		View above;
		View below;

		if (!mStackFromBottom) {
			above = fillUpWithHeaderOrFooter(motionRowStart, getNextBottom(motionRowStart, referenceView));
			// above = fillUp(motionRowStart - numColumns,
			// referenceView.getTop() - verticalSpacing);
			adjustViewsUpOrDown();

			below = fillDownWithHeaderOrFooter(motionRowStart, getNextTop(motionRowStart, referenceView));
			// below = fillDown(motionRowStart + numColumns,
			// referenceView.getBottom() + verticalSpacing);
			// Check if we have dragged the bottom of the grid too high
			final int childCount = getChildCount();
			if (childCount > 0) {
				correctTooHigh(numColumns, verticalSpacing, childCount);
			}
		} else {
			below = fillDown(motionRowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
			adjustViewsUpOrDown();
			above = fillUp(motionRowStart - 1, referenceView.getTop() - verticalSpacing);
			// Check if we have dragged the bottom of the grid too high
			final int childCount = getChildCount();
			if (childCount > 0) {
				correctTooLow(numColumns, verticalSpacing, childCount);
			}
		}

		if (temp != null) {
			return temp;
		} else if (above != null) {
			return above;
		} else {
			return below;
		}
	}

	protected void correctTooHigh(int numColumns, int verticalSpacing, int childCount) {
		// First see if the last item is visible
		final int lastPosition = mFirstPosition + childCount - 1;
		if (lastPosition == mItemCount - 1 && childCount > 0) {
			// Get the last child ...
			final View lastChild = getChildAt(childCount - 1);

			// ... and its bottom edge
			int lastBottom = lastChild.getBottom();
			if(lastChild instanceof GridViewHeaderViewExpandDistance){
				lastBottom -= ((GridViewHeaderViewExpandDistance)lastChild).getDownExpandDistance();
			}
			// This is bottom of our drawable area
			final int end = (getBottom() - getTop()) - mListPadding.bottom;

			// This is how far the bottom edge of the last view is from the
			// bottom of the
			// drawable area
			int bottomOffset = end - lastBottom;

			final View firstChild = getChildAt(0);
			int firstTop = firstChild.getTop();
			if(firstChild instanceof GridViewHeaderViewExpandDistance){
				firstTop += ((GridViewHeaderViewExpandDistance)firstChild).getUpExpandDistance();
			}
			
			// Make sure we are 1) Too high, and 2) Either there are more rows
			// above the
			// first row or the first row is scrolled off the top of the
			// drawable area
			if (bottomOffset > 0 && (mFirstPosition > 0 || firstTop < mListPadding.top)) {
				if (mFirstPosition == 0) {
					// Don't pull the top too far down
					bottomOffset = Math.min(bottomOffset, mListPadding.top - firstTop);
				}

				// Move everything down
				offsetChildrenTopAndBottom(bottomOffset);
				if (mFirstPosition > 0) {
					// Fill the gap that was opened above mFirstPosition with
					// more rows, if
					// possible
					// fillUpWithHeaderOrFooter(mFirstPosition - 1,
					// getNextBottom(mFirstPosition - 1, firstChild));
					fillUp(mFirstPosition - mNumColumns, getNextBottom(mFirstPosition - 1, firstChild));
					// Close up the remaining gap
					adjustViewsUpOrDown();
				}
			}
		}
	}

	protected void correctTooLow(int numColumns, int verticalSpacing, int childCount) {
		if (mFirstPosition == 0 && childCount > 0) {
			// Get the first child ...
			final View firstChild = getChildAt(0);

			// ... and its top edge
			int firstTop = firstChild.getTop();
			if(firstChild instanceof GridViewHeaderViewExpandDistance){
				firstTop += ((GridViewHeaderViewExpandDistance)firstChild).getUpExpandDistance();
			}
			// This is top of our drawable area
			final int start = mListPadding.top;

			// This is bottom of our drawable area
			final int end = (getBottom() - getTop()) - mListPadding.bottom;

			// This is how far the top edge of the first view is from the top of
			// the
			// drawable area
			int topOffset = firstTop - start;
			final View lastChild = getChildAt(childCount - 1);
			int lastBottom = lastChild.getBottom();
			if(lastChild instanceof GridViewHeaderViewExpandDistance){
				lastBottom -= ((GridViewHeaderViewExpandDistance)lastChild).getDownExpandDistance();
			}
			final int lastPosition = mFirstPosition + childCount - 1;

			// Make sure we are 1) Too low, and 2) Either there are more rows
			// below the
			// last row or the last row is scrolled off the bottom of the
			// drawable area
			if (topOffset > 0 && (lastPosition < mItemCount - 1 || lastBottom > end)) {
				if (lastPosition == mItemCount - 1) {
					// Don't pull the bottom too far up
					topOffset = Math.min(topOffset, lastBottom - end);
				}

				// Move everything up
				offsetChildrenTopAndBottom(-topOffset);
				if (lastPosition < mItemCount - 1) {
					// Fill the gap that was opened below the last position with
					// more rows, if
					// possible
					// fillDownWithHeaderOrFooter(lastPosition + 1,
					// getNextTop(lastPosition + 1, lastChild));
					fillDown(lastPosition + mNumColumns, getNextTop(lastPosition + 1, lastChild));
					// Close up the remaining gap
					adjustViewsUpOrDown();
				}
			}
		}
	}

	/**
	 * Fills the list from top to bottom, starting with mFirstPosition
	 * 
	 * @param nextTop
	 *            The location where the top of the first item should be drawn
	 * 
	 * @return The view that is currently selected
	 */
	protected View fillFromTop(int nextTop) {
		mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}
		mFirstPosition -= mFirstPosition % mNumColumns;
		return fillDown(mFirstPosition, nextTop);
	}

	protected View fillFromBottom(int lastPosition, int nextBottom) {
		lastPosition = Math.max(lastPosition, mSelectedPosition);
		lastPosition = Math.min(lastPosition, mItemCount - 1);

		final int invertedPosition = mItemCount - 1 - lastPosition;
		lastPosition = mItemCount - 1 - (invertedPosition - (invertedPosition % mNumColumns));

		return fillUp(lastPosition, nextBottom);
	}

	private View makeHeaderOrFooter(int pos, int y, boolean flow) {
		int nextLeft;
		final int columnWidth = mColumnWidth;
		final boolean isLayoutRtl = false;// isLayoutRtl();
		final int horizontalSpacing = mHorizontalSpacing;

		// if (isLayoutRtl) {
		// nextLeft = getWidth() - mListPadding.right - columnWidth -
		// ((mStretchMode == STRETCH_SPACING_UNIFORM) ? horizontalSpacing : 0);
		// } else {
		// nextLeft = mListPadding.left + ((mStretchMode ==
		// STRETCH_SPACING_UNIFORM) ? horizontalSpacing : 0);
		// }

		nextLeft = mListPadding.left;
		boolean selected = pos == mSelectedPosition;
		final int where = flow ? -1 : 0;

		mReferenceView = makeAndAddView(pos, y, flow, nextLeft, selected, where, getWidth() - mListPadding.left - mListPadding.right);

		final boolean hasFocus = shouldShowSelector();
		final boolean inClick = touchModeDrawsInPressedState();
		View selectedView = null;

		if (selected && (hasFocus || inClick)) {
			selectedView = mReferenceView;
		}

		if (selectedView != null) {
			mReferenceViewInSelectedRow = mReferenceView;
		}

		return mReferenceView;
	}
	
	int firstColumnMarginleft = 0;
	
	/**
	 * 每行第一列与左边的距离
	 * @param firstColumnMarginleft
	 */
	public void setFirstColumnMarginleft(int firstColumnMarginleft) {
		this.firstColumnMarginleft = firstColumnMarginleft;
	}

	private View makeRow(int startPos, int y, boolean flow) {
		final int columnWidth = mColumnWidth;
		final int horizontalSpacing = mHorizontalSpacing;

		final boolean isLayoutRtl = false;// isLayoutRtl();

		int last;
		int nextLeft;

		if (isLayoutRtl) {
			nextLeft = getWidth() - mListPadding.right - columnWidth - ((mStretchMode == STRETCH_SPACING_UNIFORM) ? horizontalSpacing : 0);
		} else {
			nextLeft = firstColumnMarginleft + mListPadding.left + ((mStretchMode == STRETCH_SPACING_UNIFORM) ? horizontalSpacing : 0);
		}

		if (!mStackFromBottom) {
			last = Math.min(startPos + mNumColumns, mItemCount - getFooterViewsCount());
		} else {
			last = startPos + 1;
			startPos = Math.max(0, startPos - mNumColumns + 1);

			if (last - startPos < mNumColumns) {
				final int deltaLeft = (mNumColumns - (last - startPos)) * (columnWidth + horizontalSpacing);
				nextLeft += (isLayoutRtl ? -1 : +1) * deltaLeft;
			}
		}

		View selectedView = null;

		final boolean hasFocus = shouldShowSelector();
		final boolean inClick = touchModeDrawsInPressedState();
		final int selectedPosition = mSelectedPosition;

		View child = null;
		for (int pos = startPos; pos < last; pos++) {
			// is this the selected item?
			boolean selected = pos == selectedPosition;
			// does the list view have focus or contain focus

			final int where = flow ? -1 : pos - startPos;
			child = makeAndAddView(pos, y, flow, nextLeft, selected, where, mColumnWidth);

			nextLeft += (isLayoutRtl ? -1 : +1) * columnWidth;
			if (pos < last - 1) {
				nextLeft += horizontalSpacing;
			}

			if (selected && (hasFocus || inClick)) {
				selectedView = child;
			}
		}

		mReferenceView = child;

		if (selectedView != null) {
			mReferenceViewInSelectedRow = mReferenceView;
		}

		return selectedView;
	}

	private View fillUpWithHeaderOrFooter(int rowStart, int nextBottom) {
		if (DEBUG) {
			Log.d(TAG, "fillUpWithHeaderOrFooter: rowStart = " + rowStart + ", nextBottom = " + nextBottom);
		}

		if (rowStart > 0) {
			if (isHeader(rowStart - 1) || isFooter(rowStart - 1)) {
				return fillUp(rowStart - 1, nextBottom);
			} else if (isFooter(rowStart) && !isFooter(rowStart - 1)) {
				return fillUp(getRowStart(rowStart - 1), nextBottom);
			} else if (!isHeader(rowStart) && isHeader(rowStart - 1)) {
				return fillUp(rowStart - 1, nextBottom);
			} else {
				return fillUp(rowStart - mNumColumns, nextBottom);
			}
		}

		return null;
	}

	/**
	 * Fills the list from pos up to the top of the list view.
	 * 
	 * @param pos
	 *            The first position to put in the list
	 * 
	 * @param nextBottom
	 *            The location where the bottom of the item associated with pos
	 *            should be drawn
	 * 
	 * @return The view that is currently selected
	 */
	protected View fillUp(int pos, int nextBottom) {
		View selectedView = null;

		int end = 0;
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end = mListPadding.top;
		}

		while ((nextBottom > end || pos >= mMinFirstPos) && pos >= 0) {
			if (isHeader(pos)) {
				View header = makeHeaderOrFooter(pos, nextBottom, false);
				if (header != null) {
					selectedView = header;
				}

				nextBottom = getNextBottom(pos, mReferenceView);

				mFirstPosition = pos;
				pos--;
			} else if (isFooter(pos)) {
				View header = makeHeaderOrFooter(pos, nextBottom, false);
				if (header != null) {
					selectedView = header;
				}

				nextBottom = getNextBottom(pos, mReferenceView);

				mFirstPosition = pos;
				pos--;
				pos = getRowStart(pos);
			} else {
				View temp = makeRow(pos, nextBottom, false);
				if (temp != null) {
					selectedView = temp;
				}

				nextBottom = getNextBottom(pos, mReferenceView);

				mFirstPosition = pos;

				if (pos - mNumColumns > getHeaderViewsCount() - 1) {
					pos -= mNumColumns;
				} else {
					pos = getHeaderViewsCount() - 1;
				}
			}
		}

		if (mStackFromBottom) {
			mFirstPosition = Math.max(0, pos + 1);
		}

		// setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
		// - 1);
		return selectedView;
	}

	private int getNextBottom(int pos, View referenceView) {
		int nextBottom;
		if (pos == 0) {
			nextBottom = referenceView.getTop();
		} else {
			nextBottom = referenceView.getTop() - mVerticalSpacing;
		}
		if(referenceView instanceof GridViewHeaderViewExpandDistance){
			nextBottom -= ((GridViewHeaderViewExpandDistance)referenceView).getDownExpandDistance();
		}
		return nextBottom;
	}

	private View fillDownWithHeaderOrFooter(int rowStart, int nextTop) {
		if (DEBUG) {
			Log.d(TAG, "fillDownWithHeaderOrFooter: rowStart = " + rowStart + ", nextTop = " + nextTop);
		}

		if (isHeader(rowStart + 1) || isFooter(rowStart + 1)) {
			return fillDown(rowStart + 1, nextTop);
		} else if (isHeader(rowStart) && !isHeader(rowStart + 1)) {
			return fillDown(rowStart + 1, nextTop);
		} else if (!isFooter(rowStart) && isFooter(rowStart + 1)) {
			return fillDown(rowStart + 1, nextTop);
		} else {
			return fillDown(Math.min(rowStart + mNumColumns, mItemCount - getFooterViewsCount()), nextTop);
		}
	}

	/**
	 * Fills the list from pos down to the end of the list view.
	 * 
	 * @param pos
	 *            The first position to put in the list
	 * 
	 * @param nextTop
	 *            The location where the top of the item associated with pos
	 *            should be drawn
	 * 
	 * @return The view that is currently selected, if it happens to be in the
	 *         range that we draw.
	 */
	private View fillDown(int pos, int nextTop) {
		View selectedView = null;

		int end = (getBottom() - getTop());
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end -= mListPadding.bottom;
		}

		while ((nextTop < end || pos <= mMinLastPos) && pos < mItemCount) {
			if (pos < getHeaderViewsCount() || pos > mItemCount - getFooterViewsCount() - 1) {
				if(pos < getHeaderViewsCount()){
					View headerView = mHeaderViewInfos.get(pos).view;
					if(headerView != null && headerView instanceof GridViewHeaderViewExpandDistance){
						nextTop -= ((GridViewHeaderViewExpandDistance)headerView).getUpExpandDistance();
					}
				}
				View header = makeHeaderOrFooter(pos, nextTop, true);
				if (header != null) {
					selectedView = header;
				}

				nextTop = getNextTop(pos, mReferenceView);

				pos++;
			} else {
				View temp = makeRow(pos, nextTop, true);
				if (temp != null) {
					selectedView = temp;
				}

				// mReferenceView will change with each call to makeRow()
				// do not cache in a local variable outside of this loop
				nextTop = getNextTop(pos, mReferenceView);

				if (pos + mNumColumns < mItemCount - getFooterViewsCount()) {
					pos += mNumColumns;
				} else {
					pos = mItemCount - getFooterViewsCount();
				}
			}
		}

		// setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
		// - 1);
		return selectedView;
	}

	private int getNextTop(int pos, View referenceView) {
		if(referenceView==null){
			return 0;
		}
		int nextTop = referenceView.getBottom() + mVerticalSpacing;
		if(referenceView instanceof GridViewHeaderViewExpandDistance){
			nextTop -= ((GridViewHeaderViewExpandDistance)referenceView).getDownExpandDistance();
		}
		return nextTop;
	}

	protected int getRowStart(int position) {
		int rowStart = position;

		if (position < getHeaderViewsCount()) {
			rowStart = position;
		} else {
			if (position < mItemCount - getFooterViewsCount()) {
				int newPosition = position - getHeaderViewsCount();
				rowStart = newPosition - (newPosition % mNumColumns) + getHeaderViewsCount();
			} else {
				rowStart = position;
			}
		}

		return rowStart;
	}

	/**
	 * Fills the grid based on positioning the new selection relative to the old
	 * selection. The new selection will be placed at, above, or below the
	 * location of the new selection depending on how the selection is moving.
	 * The selection will then be pinned to the visible part of the screen,
	 * excluding the edges that are faded. The grid is then filled upwards and
	 * downwards from there.
	 * 
	 * @param delta
	 *            Which way we are moving
	 * @param childrenTop
	 *            Where to start drawing children
	 * @param childrenBottom
	 *            Last pixel where children can be drawn
	 * @return The view that currently has selection
	 */
	protected View moveSelection(int delta, int childrenTop, int childrenBottom) {
		final int fadingEdgeLength = getVerticalFadingEdgeLength();
		final int selectedPosition = mSelectedPosition;
		final int numColumns = mNumColumns;
		final int verticalSpacing = mVerticalSpacing;

		int oldRowStart;
		int rowStart;
		int rowEnd = -1;

		if (!mStackFromBottom) {
			// if (selectedPosition - delta < getHeaderViewsCount()) {
			// oldRowStart = selectedPosition - delta;
			// } else {
			// int newPosition = selectedPosition - delta -
			// getHeaderViewsCount();
			// oldRowStart = newPosition - (newPosition % numColumns) +
			// getHeaderViewsCount();
			// }

			oldRowStart = getRowStart(selectedPosition - delta);

			// if (selectedPosition < getHeaderViewsCount()) {
			// rowStart = selectedPosition;
			// } else {
			// int newPosition = selectedPosition - getHeaderViewsCount();
			// rowStart = newPosition - (newPosition % numColumns) +
			// getHeaderViewsCount();
			// }

			rowStart = getRowStart(selectedPosition);
		} else {
			int invertedSelection = mItemCount - 1 - selectedPosition;

			rowEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
			rowStart = Math.max(0, rowEnd - numColumns + 1);

			invertedSelection = mItemCount - 1 - (selectedPosition - delta);
			oldRowStart = mItemCount - 1 - (invertedSelection - (invertedSelection % numColumns));
			oldRowStart = Math.max(0, oldRowStart - numColumns + 1);
		}

		final int rowDelta = rowStart - oldRowStart;

		final int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
		final int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, numColumns, rowStart);

		// Possibly changed again in fillUp if we add rows above this one.
		mFirstPosition = rowStart;

		View sel;
		View referenceView;

		if (rowDelta > 0) {
			/*
			 * Case 1: Scrolling down.
			 */

			int oldBottom = mReferenceViewInSelectedRow == null ? 0 : mReferenceViewInSelectedRow.getBottom();
			if(mReferenceViewInSelectedRow != null && mReferenceViewInSelectedRow instanceof GridViewHeaderViewExpandDistance){
				oldBottom -= ((GridViewHeaderViewExpandDistance)mReferenceViewInSelectedRow).getDownExpandDistance();
			}
			if (rowStart < getHeaderViewsCount() || rowStart > mItemCount - 1 - getFooterViewsCount()) {
				sel = makeHeaderOrFooter(rowStart, oldBottom + verticalSpacing, true);
			} else {
				sel = makeRow(mStackFromBottom ? rowEnd : rowStart, oldBottom + verticalSpacing, true);
			}
			// sel = makeRow(mStackFromBottom ? rowEnd : rowStart, oldBottom +
			// verticalSpacing, true);
			referenceView = mReferenceView;

			adjustForBottomFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
		} else if (rowDelta < 0) {
			/*
			 * Case 2: Scrolling up.
			 */
			final int oldTop = mReferenceViewInSelectedRow == null ? 0 : mReferenceViewInSelectedRow.getTop();

			if (rowStart < getHeaderViewsCount() || rowStart > mItemCount - 1 - getFooterViewsCount()) {
				sel = makeHeaderOrFooter(rowStart, oldTop - verticalSpacing, false);
			} else {
				sel = makeRow(mStackFromBottom ? rowEnd : rowStart, oldTop - verticalSpacing, false);
			}
			
			// sel = makeRow(mStackFromBottom ? rowEnd : rowStart, oldTop -
			// verticalSpacing, false);
			referenceView = mReferenceView;

			adjustForTopFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
		} else {
			/*
			 * Keep selection where it was
			 */
			final int oldTop = mReferenceViewInSelectedRow == null ? 0 : mReferenceViewInSelectedRow.getTop();

			if (rowStart < getHeaderViewsCount() || rowStart > mItemCount - 1 - getFooterViewsCount()) {
				sel = makeHeaderOrFooter(rowStart, oldTop, true);
			} else {
				sel = makeRow(mStackFromBottom ? rowEnd : rowStart, oldTop, true);
			}
			referenceView = mReferenceView;
		}

		if (!mStackFromBottom) {
			fillUpWithHeaderOrFooter(rowStart, getNextBottom(rowStart, referenceView));
			// fillUp(rowStart - numColumns, referenceView.getTop() -
			// verticalSpacing);
			adjustViewsUpOrDown();

			fillDownWithHeaderOrFooter(rowStart, getNextTop(rowStart, referenceView));
		} else {
			fillDown(rowEnd + numColumns, referenceView.getBottom() + verticalSpacing);
			adjustViewsUpOrDown();
			fillUp(rowStart - 1, referenceView.getTop() - verticalSpacing);
		}
		
		return sel;
	}
	
	
	/**
	 * Obtain the view and add it to our list of children. The view can be made
	 * fresh, converted from an unused view, or used as is if it was in the
	 * recycle bin.
	 * 
	 * @param position
	 *            Logical position in the list
	 * @param y
	 *            Top or bottom edge of the view to add
	 * @param flow
	 *            if true, align top edge to y. If false, align bottom edge to
	 *            y.
	 * @param childrenLeft
	 *            Left edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @param where
	 *            to add new item in the list
	 * @return View that was added
	 */
	private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected, int where, int width) {
		View child;

		if (!mDataChanged) {
			// Try to use an existing view for this position
			child = mRecycler.getActiveView(position);
			if (child != null) {
				// Found it -- we're using an existing child
				// This just needs to be positioned
				setupChild(child, position, y, flow, childrenLeft, selected, true, where, width);
				return child;
			}
		}

		// Make a new view for this position, or convert an unused view if
		// possible
		child = obtainView(position, mIsScrap);

		// This needs to be positioned and measured
		setupChild(child, position, y, flow, childrenLeft, selected, mIsScrap[0], where, width);

		return child;
	}

	/**
	 * Add a view as a child and make sure it is measured (if necessary) and
	 * positioned properly.
	 * 
	 * @param child
	 *            The view to add
	 * @param position
	 *            The position of the view
	 * @param y
	 *            The y position relative to which this view will be positioned
	 * @param flow
	 *            if true, align top edge to y. If false, align bottom edge to
	 *            y.
	 * @param childrenLeft
	 *            Left edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @param recycled
	 *            Has this view been pulled from the recycle bin? If so it does
	 *            not need to be remeasured.
	 * @param where
	 *            Where to add the item in the list
	 * 
	 */
	private void setupChild(View child, int position, int y, boolean flow, int childrenLeft, boolean selected, boolean recycled, int where,
			int width) {
		boolean isSelected = selected && shouldShowSelector();
		final boolean updateChildSelected = isSelected != child.isSelected();
		final int mode = mTouchMode;
		final boolean isPressed = mode > TOUCH_MODE_DOWN && mode < TOUCH_MODE_SCROLL && mMotionPosition == position;
		final boolean updateChildPressed = isPressed != child.isPressed();

		boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

		// Respect layout params that are already in the view. Otherwise make
		// some up...
		AbsListView.LayoutParams p = (AbsListView.LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
		}
		p.viewType = mAdapter.getItemViewType(position);

		if (recycled && !p.forceAdd) {
			attachViewToParent(child, where, p);
		} else {
			p.forceAdd = false;
			addViewInLayout(child, where, p, true);
		}

		if (updateChildSelected) {
			child.setSelected(isSelected);
			if (isSelected) {
				//modify by quanqing.hqq 此处不宜调用，否则会清空FocusRelativeLayout和FocusLinearLayout的deep
				//导致焦点传入不进来
				//requestFocus();
			}
		}

		if (updateChildPressed) {
			child.setPressed(isPressed);
		}

		if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
			if (child instanceof Checkable) {
				((Checkable) child).setChecked(mCheckStates.get(position));
			} else if (getContext().getApplicationInfo().targetSdkVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				child.setActivated(mCheckStates.get(position));
			}
		}

		if (needToMeasure) {
			int childHeightSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.height);

			int childWidthSpec = getChildMeasureSpec(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), 0, p.width);
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();

		int childLeft;
		int childTop = flow ? y : y - h;
		if(flow == false && child instanceof GridViewHeaderViewExpandDistance){
			childTop += ((GridViewHeaderViewExpandDistance)child).getDownExpandDistance();
		}
		// final int layoutDirection = getLayoutDirection();
		final int absoluteGravity = Gravity.CENTER_HORIZONTAL;// Gravity.getAbsoluteGravity(mGravity,
																// layoutDirection);
		switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
		case Gravity.LEFT:
			childLeft = childrenLeft;
			break;
		case Gravity.CENTER_HORIZONTAL:
			childLeft = childrenLeft + ((width - w) / 2);
			break;
		case Gravity.RIGHT:
			childLeft = childrenLeft + width - w;
			break;
		default:
			childLeft = childrenLeft;
			break;
		}

		if (needToMeasure) {
			final int childRight = childLeft + w;
			final int childBottom = childTop + h;
			child.layout(childLeft, childTop, childRight, childBottom);
		} else {
			child.offsetLeftAndRight(childLeft - child.getLeft());
			child.offsetTopAndBottom(childTop - child.getTop());
		}

		if (mCachingStarted) {
			child.setDrawingCacheEnabled(true);
		}

		if (recycled && (((AbsListView.LayoutParams) child.getLayoutParams()).scrappedFromPosition) != position) {
			child.jumpDrawablesToCurrentState();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return commonKey(keyCode, 1, event);
	}

	private boolean commonKey(int keyCode, int count, KeyEvent event) {
		if (mAdapter == null) {
			return false;
		}

		if (mDataChanged) {
			layoutChildren();
		}

		boolean handled = false;
		int action = event.getAction();
		int navigation = SoundEffectConstants.NAVIGATION_LEFT;
		if (action != KeyEvent.ACTION_UP) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_LEFT);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_RIGHT);
				}
				navigation = SoundEffectConstants.NAVIGATION_RIGHT;
				break;

			case KeyEvent.KEYCODE_DPAD_UP:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_UP);
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_UP);
				}
				navigation = SoundEffectConstants.NAVIGATION_UP;
				break;

			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_DOWN);
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_DOWN);
				}
				navigation = SoundEffectConstants.NAVIGATION_DOWN;
				break;

			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded();
					if (!handled && event.getRepeatCount() == 0 && getChildCount() > 0) {
						keyPressed();
						handled = true;
					}
				}
				break;

			case KeyEvent.KEYCODE_SPACE:
				// if (mPopup == null || !mPopup.isShowing()) {
				// if (event.hasNoModifiers()) {
				// handled = resurrectSelectionIfNeeded() ||
				// pageScroll(FOCUS_DOWN);
				// } else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
				// handled = resurrectSelectionIfNeeded() ||
				// pageScroll(FOCUS_UP);
				// }
				// }
				break;

			case KeyEvent.KEYCODE_PAGE_UP:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || pageScroll(FOCUS_UP);
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_UP);
				}
				navigation = SoundEffectConstants.NAVIGATION_UP;
				break;

			case KeyEvent.KEYCODE_PAGE_DOWN:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || pageScroll(FOCUS_DOWN);
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_DOWN);
				}
				navigation = SoundEffectConstants.NAVIGATION_DOWN;
				break;

			case KeyEvent.KEYCODE_MOVE_HOME:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_UP);
				}
				break;

			case KeyEvent.KEYCODE_MOVE_END:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_DOWN);
				}
				break;

			case KeyEvent.KEYCODE_TAB:
				// XXX Sometimes it is useful to be able to TAB through the
				// items in
				// a GridView sequentially. Unfortunately this can create an
				// asymmetry in TAB navigation order unless the list selection
				// always reverts to the top or bottom when receiving TAB focus
				// from
				// another widget. Leaving this behavior disabled for now but
				// perhaps it should be configurable (and more comprehensive).
				if (false) {
					if (event.hasNoModifiers()) {
						handled = resurrectSelectionIfNeeded() || sequenceScroll(FOCUS_FORWARD);
					} else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
						handled = resurrectSelectionIfNeeded() || sequenceScroll(FOCUS_BACKWARD);
					}
				}
				break;
			}
		}

		if (handled) {
			this.playSoundEffect(navigation);
			return true;
		}

		// if (sendToTextFilter(keyCode, count, event)) {
		// return true;
		// }

		switch (action) {
		case KeyEvent.ACTION_DOWN:
			return super.onKeyDown(keyCode, event);
		case KeyEvent.ACTION_UP:
			return super.onKeyUp(keyCode, event);
		case KeyEvent.ACTION_MULTIPLE:
			return super.onKeyMultiple(keyCode, count, event);
		default:
			return false;
		}
	}

	/**
	 * Scrolls to the next or previous item, horizontally or vertically.
	 * 
	 * @param direction
	 *            either {@link View#FOCUS_LEFT}, {@link View#FOCUS_RIGHT},
	 *            {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
	 * 
	 * @return whether selection was moved
	 */
	boolean arrowScroll(int direction) {
		final int selectedPosition = mSelectedPosition;
		final int numColumns = mNumColumns;

		int startOfRowPos;
		int endOfRowPos;

		boolean moved = false;

		if (!mStackFromBottom) {
			if (isHeader(selectedPosition) || isFooter(selectedPosition)) {
				startOfRowPos = selectedPosition;
				endOfRowPos = selectedPosition;
			} else {
				startOfRowPos = getRowStart(selectedPosition);
				endOfRowPos = Math.min(startOfRowPos + numColumns - 1, mItemCount - 1 - getFooterViewsCount());
			}
		} else {
			final int invertedSelection = mItemCount - 1 - selectedPosition;
			endOfRowPos = mItemCount - 1 - (invertedSelection / numColumns) * numColumns;
			startOfRowPos = Math.max(0, endOfRowPos - numColumns + 1);
		}

		switch (direction) {
		case FOCUS_UP:
			if (startOfRowPos > 0 && selectedPosition > 0) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				// int lastItemPosition = mItemCount - 1 -
				// getFooterViewsCount();
				// if (selectedPosition - 1 > lastItemPosition) {
				// setSelectionInt(Math.max(0, selectedPosition - 1));
				// } else if (selectedPosition < getHeaderViewsCount()) {
				// setSelectionInt(Math.max(0, selectedPosition - 1));
				// } else if (selectedPosition < getHeaderViewsCount() +
				// mNumColumns) {
				// setSelectionInt(Math.max(0, getHeaderViewsCount()));
				// } else if (selectedPosition - mNumColumns <=
				// lastItemPosition) {
				// // TODO search focus
				// setSelectionInt(Math.max(0, lastItemPosition));
				// }

				if (isHeader(selectedPosition - 1) || isFooter(selectedPosition - 1)) {
					setSelectionInt(Math.max(0, selectedPosition - 1));
				} else if (isFooter(selectedPosition) && !isFooter(selectedPosition - 1)) {
					// TODO search focus
					setSelectionInt(Math.max(selectedPosition - 1, getHeaderViewsCount()));
				} else if (!isHeader(selectedPosition) && isHeader(selectedPosition - mNumColumns)) {
					setSelectionInt(Math.max(getHeaderViewsCount() - 1, 0));
				} else {
					setSelectionInt(Math.max(0, selectedPosition - mNumColumns));
				}
				moved = true;
			}
			break;
		case FOCUS_DOWN:
			if (endOfRowPos < mItemCount - 1 && selectedPosition < mItemCount) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				// int lastItemPosition = mItemCount - 1 -
				// getFooterViewsCount();
				// int lastRowStartWithoutFooter = lastItemPosition -
				// lastItemPosition % mNumColumns + getHeaderViewsCount();
				//
				// if (selectedPosition + 1 < getHeaderViewsCount()) {
				// setSelectionInt(Math.min(selectedPosition + 1, mItemCount - 1
				// - getFooterViewsCount()));
				// } else if (selectedPosition == getHeaderViewsCount() - 1) {
				// setSelectionInt(Math.min(getHeaderViewsCount(), mItemCount -
				// 1 - getFooterViewsCount()));
				// } else if (selectedPosition < lastRowStartWithoutFooter) {
				// setSelectionInt(Math.min(selectedPosition + mNumColumns,
				// mItemCount - 1));
				// } else if (selectedPosition < mItemCount -
				// getFooterViewsCount())
				// setSelectionInt(Math.min(mItemCount - getFooterViewsCount(),
				// mItemCount - 1));
				// else {
				// setSelectionInt(Math.min(selectedPosition + 1, mItemCount -
				// 1));
				// }

				if (isHeader(selectedPosition + 1) || isFooter(selectedPosition + 1)) {
					setSelectionInt(Math.min(selectedPosition + 1, mItemCount - 1));
				} else if (isHeader(selectedPosition) && !isHeader(selectedPosition + 1)) {
					// TODO search focus
					setSelectionInt(Math.min(selectedPosition + 1, mItemCount - 1 - getFooterViewsCount()));
				} else if (!isFooter(selectedPosition) && isFooter(selectedPosition + mNumColumns)) {
					setSelectionInt(Math.min(mItemCount - getFooterViewsCount(), mItemCount - 1));
				} else {
					setSelectionInt(Math.min(selectedPosition + mNumColumns, mItemCount - 1 - getFooterViewsCount()));
				}
				moved = true;
			}
			break;
		case FOCUS_LEFT:
			if (selectedPosition > startOfRowPos) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(Math.max(0, selectedPosition - 1));
				moved = true;
			}
			break;
		case FOCUS_RIGHT:
			if (selectedPosition < endOfRowPos) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(Math.min(selectedPosition + 1, mItemCount - 1));
				moved = true;
			}
			break;
		}

		if (moved) {
			//playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
			// TODO
			// invokeOnItemScrollListener();
		}

		if (moved) {
			awakenScrollBars();
		}

		return moved;
	}

	boolean isHeader(int position) {
		return position < getHeaderViewsCount() ? true : false;
	}

	boolean isFooter(int position) {
		return position > mItemCount - getFooterViewsCount() - 1 ? true : false;
	}
	
	/**
	 * Sets the currently selected item
	 * 
	 * @param position
	 *            Index (starting at 0) of the data item to be selected.
	 * 
	 *            If in touch mode, the item will not be selected but it will
	 *            still be positioned appropriately.
	 */
	@Override
	public void setSelection(int position) {
		if (!isInTouchMode()) {
			setNextSelectedPositionInt(position);
		} else {
			mResurrectToPosition = position;
		}
		mLayoutMode = LAYOUT_SET_SELECTION;
		// if (mPositionScroller != null) {
		// mPositionScroller.stop();
		// }
		//requestLayout();
		mNeedLayout = true;
		layoutChildren();
	}

	/**
	 * Makes the item at the supplied position selected.
	 * 
	 * @param position
	 *            the position of the new selection
	 */
	@Override
	void setSelectionInt(int position) {
		int previousSelectedPosition = mNextSelectedPosition;

		// if (mPositionScroller != null) {
		// mPositionScroller.stop();
		// }

		setNextSelectedPositionInt(position);
		mNeedLayout = true;
		layoutChildren();

		final int next = mStackFromBottom ? mItemCount - 1 - mNextSelectedPosition : mNextSelectedPosition;
		final int previous = mStackFromBottom ? mItemCount - 1 - previousSelectedPosition : previousSelectedPosition;

		final int nextRow = next / mNumColumns;
		final int previousRow = previous / mNumColumns;

		if (nextRow != previousRow) {
			awakenScrollBars();
		}

	}

	/**
	 * Go to the last or first item if possible.
	 * 
	 * @param direction
	 *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}.
	 * 
	 * @return Whether selection was moved.
	 */
	boolean fullScroll(int direction) {
		boolean moved = false;
		if (direction == FOCUS_UP) {
			mLayoutMode = LAYOUT_SET_SELECTION;
			setSelectionInt(0);
			// TODO
			// invokeOnItemScrollListener();
			moved = true;
		} else if (direction == FOCUS_DOWN) {
			mLayoutMode = LAYOUT_SET_SELECTION;
			setSelectionInt(mItemCount - 1);
			// TODO
			// invokeOnItemScrollListener();
			moved = true;
		}

		if (moved) {
			awakenScrollBars();
		}

		return moved;
	}

	/**
	 * Scrolls up or down by the number of items currently present on screen.
	 * 
	 * @param direction
	 *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
	 * @return whether selection was moved
	 */
	boolean pageScroll(int direction) {
		int nextPage = -1;

		if (direction == FOCUS_UP) {
			nextPage = Math.max(0, mSelectedPosition - getChildCount());
		} else if (direction == FOCUS_DOWN) {
			nextPage = Math.min(mItemCount - 1, mSelectedPosition + getChildCount());
		}

		if (nextPage >= 0) {
			setSelectionInt(nextPage);
			// TODO
			// invokeOnItemScrollListener();
			awakenScrollBars();
			return true;
		}

		return false;
	}

	/**
	 * Goes to the next or previous item according to the order set by the
	 * adapter.
	 */
	boolean sequenceScroll(int direction) {
		int selectedPosition = mSelectedPosition;
		int numColumns = mNumColumns;
		int count = mItemCount;

		int startOfRow;
		int endOfRow;
		if (!mStackFromBottom) {
			startOfRow = (selectedPosition / numColumns) * numColumns;
			endOfRow = Math.min(startOfRow + numColumns - 1, count - 1);
		} else {
			int invertedSelection = count - 1 - selectedPosition;
			endOfRow = count - 1 - (invertedSelection / numColumns) * numColumns;
			startOfRow = Math.max(0, endOfRow - numColumns + 1);
		}

		boolean moved = false;
		boolean showScroll = false;
		switch (direction) {
		case FOCUS_FORWARD:
			if (selectedPosition < count - 1) {
				// Move to the next item.
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(selectedPosition + 1);
				moved = true;
				// Show the scrollbar only if changing rows.
				showScroll = selectedPosition == endOfRow;
			}
			break;

		case FOCUS_BACKWARD:
			if (selectedPosition > 0) {
				// Move to the previous item.
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(selectedPosition - 1);
				moved = true;
				// Show the scrollbar only if changing rows.
				showScroll = selectedPosition == startOfRow;
			}
			break;
		}

		if (moved) {
			//playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
			// TODO
			// invokeOnItemScrollListener();
		}

		if (showScroll) {
			awakenScrollBars();
		}

		return moved;
	}
	
	
	public static interface GridViewHeaderViewExpandDistance{
		public int getUpExpandDistance();
		public int getDownExpandDistance();
	}
	
    public void setStretchMode(int stretchMode) {
        if (stretchMode != mStretchMode) {
            mStretchMode = stretchMode;
            requestLayoutIfNecessary();
        }
    }
}

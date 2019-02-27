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

public class HGridView extends AbsHListView {
	private static final String TAG = "GridView";
	private static final boolean DEBUG = true;

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

	private int mNumLines = AUTO_FIT;
	private int mVerticalSpacing = 0;

	private View mReferenceView = null;
	private View mReferenceViewInSelectedRow = null;

	private int mLineHeight;
	private int mRequestedLineHeight = -1;
	private int mRequestedNumLines;

	private int mHorizontalSpacing = 0;
	private int mRequestedVerticalSpacing;

	private int mStretchMode = STRETCH_COLUMN_WIDTH;
	
	private int mMinLastPos = -1;
	private int mMinFirstPos = Integer.MAX_VALUE;

	public HGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public HGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HGridView(Context context) {
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
	public void setNumLines(int numLines) {
		if (numLines != mRequestedNumLines) {
			mRequestedNumLines = numLines;
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
	public void setLineHeight(int lineHeight) {
		if (lineHeight != mRequestedLineHeight) {
			mRequestedLineHeight = lineHeight;
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
		if (verticalSpacing != mRequestedVerticalSpacing) {
			mRequestedVerticalSpacing = verticalSpacing;
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
        if (horizontalSpacing != mHorizontalSpacing) {
            mHorizontalSpacing = horizontalSpacing;
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
    public int getRequestedVerticalSpacing() {
        return mRequestedVerticalSpacing;
    }
	
	public int getVerticalSpacing(){
		return mVerticalSpacing;
	}
	
	
	public int getNumLines(){
		return mNumLines;
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

		int columnStart;
		int columnEnd;

		if (!mStackFromBottom) {
			columnStart = childIndex - (childIndex % mNumLines);
			columnEnd = Math.max(columnStart + mNumLines - 1, count);
		} else {
			columnEnd = count - 1 - (invertedIndex - (invertedIndex % mNumLines));
			columnStart = Math.max(0, columnEnd - mNumLines + 1);
		}

		switch (direction) {
		case View.FOCUS_DOWN:
			// coming from left, selection is only valid if it is on left
			// edge
			return childIndex == columnStart;
		case View.FOCUS_RIGHT:
			// coming from top; only valid if in top row
			return columnStart == 0;
		case View.FOCUS_UP:
			// coming from right, must be on right edge
			return childIndex == columnEnd;
		case View.FOCUS_LEFT:
			// coming from bottom, need to be in last row
			return columnEnd == count - 1;
		case View.FOCUS_FORWARD:
			// coming from top-left, need to be first in top row
			return childIndex == columnStart && columnStart == 0;
		case View.FOCUS_BACKWARD:
			// coming from bottom-right, need to be last in bottom row
			return childIndex == columnEnd && columnEnd == count - 1;
		default:
			throw new IllegalArgumentException("direction must be one of " + "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, "
					+ "FOCUS_FORWARD, FOCUS_BACKWARD}.");
		}
	}

	protected int getFillGapNextChildIndex(boolean isRight){
		if(isRight){
			return getChildCount() - 1;
		}
		else{
			return 0;
		}
	}
	
	@Override
	void fillGap(boolean isRight) {
		if (DEBUG) {
			Log.d(TAG, "fillGap: mFirstPosition = " + mFirstPosition);
		}
		final int numLines = mNumLines;
		final int horizontalSpacing = mHorizontalSpacing;

		final int count = getChildCount();

		if (isRight) {
			int paddingLeft = 0;
			if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
				paddingLeft = getListPaddingLeft();
			}
			// final int startOffset = count > 0 ? getChildAt(count -
			// 1).getBottom() : paddingTop;
			int position = mFirstPosition + count;
			if (mStackFromBottom) {
				position += numLines - 1;
			}

			// fillDownWithHeaderOrFooter(position, getNextTop(position,
			// getChildAt(count - 1)));
			fillRight(position, getNextLeft(position, getChildAt(getFillGapNextChildIndex(isRight))));
			correctTooWide(numLines, horizontalSpacing, getChildCount());
		} else {
			int paddingRight = 0;
			if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
				paddingRight = getListPaddingRight();
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

			fillLeftWithHeaderOrFooter(position, getNextRight(position, getChildAt(getFillGapNextChildIndex(isRight))));
			// fillUp(position, getNextBottom(position, getChildAt(0)));
			correctTooNarrow(numLines, horizontalSpacing, getChildCount());
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

		if (heightMode == MeasureSpec.UNSPECIFIED) {
			if (mLineHeight > 0) {
				heightSize = mLineHeight + mListPadding.top + mListPadding.bottom;
			} else {
				heightSize = mListPadding.top + mListPadding.bottom;
			}
			heightSize += getHorizontalScrollbarHeight();
		}

		int childHeight = heightSize - mListPadding.top - mListPadding.bottom;

		int childWidth = 0;
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

			childWidth = child.getMeasuredWidth();
			if (mNumLines == AUTO_FIT && mRequestedLineHeight < 0) {
				setLineHeight(child.getMeasuredHeight());
			}
			childState = combineMeasuredStates(childState, child.getMeasuredState());

			if (mRecycler.shouldRecycleViewType(p.viewType)) {
				mRecycler.addScrapView(child, -1);
			}
		}

		if (widthMode == MeasureSpec.UNSPECIFIED) {
			widthSize = mListPadding.left + mListPadding.right + childWidth + getHorizontalFadingEdgeLength() * 2;
		}

		if (widthMode == MeasureSpec.AT_MOST) {
			int ourSize = mListPadding.left + mListPadding.right;

			final int numLines = mNumLines;
			for (int i = 0; i < count; i += numLines) {
				ourSize += childWidth;
				if (i + numLines < count) {
					ourSize += mHorizontalSpacing;
				}
				if (ourSize >= widthSize) {
					ourSize = widthSize;
					break;
				}
			}
			widthSize = ourSize;
		}
		boolean didNotInitiallyFit = determineLines(childHeight);
		if (heightMode == MeasureSpec.AT_MOST && mRequestedNumLines != AUTO_FIT) {
			int ourSize = (mRequestedNumLines * mLineHeight) + ((mRequestedNumLines - 1) * mVerticalSpacing) + mListPadding.top
					+ mListPadding.bottom;
			if (ourSize > heightSize || didNotInitiallyFit) {
				heightSize |= MEASURED_STATE_TOO_SMALL;
			}
		}

		setMeasuredDimension(widthSize, heightSize);
		mHeightMeasureSpec = heightMeasureSpec;
	}

	private boolean determineLines(int availableSpace) {
		final int requestedVerticalSpacing = mRequestedVerticalSpacing;
		final int stretchMode = mStretchMode;
		final int requestedLineHeight = mRequestedLineHeight;
		boolean didNotInitiallyFit = false;

		if (mRequestedNumLines == AUTO_FIT) {
			if (requestedLineHeight > 0) {
				// Client told us to pick the number of columns
				mNumLines = (availableSpace + requestedVerticalSpacing) / (requestedLineHeight + requestedVerticalSpacing);
			} else {
				// Just make up a number if we don't have enough info
				mNumLines = 2;
			}
		} else {
			// We picked the columns
			mNumLines = mRequestedNumLines;
		}

		if (mNumLines <= 0) {
			mNumLines = 1;
		}

		switch (stretchMode) {
		case NO_STRETCH:
			// Nobody stretches
			mLineHeight = requestedLineHeight;
			mVerticalSpacing = requestedVerticalSpacing;
			break;

		default:
			int spaceTopOver = availableSpace - (mNumLines * requestedLineHeight) - ((mNumLines - 1) * requestedVerticalSpacing);

			if (spaceTopOver < 0) {
				didNotInitiallyFit = true;
			}

			switch (stretchMode) {
			case STRETCH_COLUMN_WIDTH:
				// Stretch the columns
				mLineHeight = requestedLineHeight + spaceTopOver / mNumLines;
				mVerticalSpacing = requestedVerticalSpacing;
				break;

			case STRETCH_SPACING:
				// Stretch the spacing between columns
				mLineHeight = requestedLineHeight;
				if (mNumLines > 1) {
					mVerticalSpacing = requestedVerticalSpacing + spaceTopOver / (mNumLines - 1);
				} else {
					mVerticalSpacing = requestedVerticalSpacing + spaceTopOver;
				}
				break;

			case STRETCH_SPACING_UNIFORM:
				// Stretch the spacing between columns
				mLineHeight = requestedLineHeight;
				if (mNumLines > 1) {
					mVerticalSpacing = requestedVerticalSpacing + spaceTopOver / (mNumLines + 1);
				} else {
					mVerticalSpacing = requestedVerticalSpacing + spaceTopOver;
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
        animationParams.columnsCount = count / mNumLines;
        animationParams.rowsCount = mNumLines;

        if (!mStackFromBottom) {
            animationParams.column = index / mNumLines;
            animationParams.row = index % mNumLines;
        } else {
            final int invertedIndex = count - 1 - index;

            animationParams.column = animationParams.rowsCount - 1 - invertedIndex / mNumLines;
            animationParams.row = mNumLines - 1 - (invertedIndex % mNumLines);
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

			final int childrenLeft = mListPadding.left;
			final int childrenRight = getRight() - getLeft() - mListPadding.right;

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
			case LAYOUT_FORCE_LEFT:
			case LAYOUT_FORCE_RIGHT:
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
					sel = fillFromSelection(newSel.getLeft(), childrenLeft, childrenRight);
				} else {
					sel = fillSelection(childrenLeft, childrenRight);
				}
				break;
			case LAYOUT_FORCE_LEFT:
				mFirstPosition = 0;
				sel = fillFromLeft(childrenLeft);
				adjustViewsLeftOrRight();
				break;
			case LAYOUT_FORCE_RIGHT:
				sel = fillLeft(mItemCount - 1, childrenRight);
				adjustViewsLeftOrRight();
				break;
			case LAYOUT_SPECIFIC:
				sel = fillSpecific(mSelectedPosition, mSpecificLeft);
				break;
			case LAYOUT_SYNC:
				sel = fillSpecific(mSyncPosition, mSpecificLeft);
				break;
			case LAYOUT_MOVE_SELECTION:
				// Move the selection relative to its old position
				sel = moveSelection(delta, childrenLeft, childrenRight);
				break;
			default:
				if (childCount == 0) {
					if (!mStackFromBottom) {
						setSelectedPositionInt(mAdapter == null || isInTouchMode() ? INVALID_POSITION : 0);
						sel = fillFromLeft(childrenLeft);
					} else {
						final int last = mItemCount - 1;
						setSelectedPositionInt(mAdapter == null || isInTouchMode() ? INVALID_POSITION : last);
						sel = fillFromRight(last, childrenRight);
					}
				} else {
					if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {
						sel = fillSpecific(mSelectedPosition, oldSel == null ? childrenLeft : oldSel.getLeft());
					} else if (mFirstPosition < mItemCount) {
						sel = fillSpecific(mFirstPosition, oldFirst == null ? childrenLeft : oldFirst.getLeft());
					} else {
						sel = fillSpecific(0, childrenLeft);
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
	protected void adjustViewsLeftOrRight() {
		final int childCount = getChildCount();

		if (childCount > 0) {
			int delta;
			View child;

			if (!mStackFromBottom) {
				// Uh-oh -- we came up short. Slide all views up to make them
				// align with the top
				child = getChildAt(0);
				delta = child.getLeft() - mListPadding.left;
				if (mFirstPosition != 0) {
					// It's OK to have some space above the first item if it is
					// part of the vertical spacing
					delta -= mHorizontalSpacing;
				}
				if (delta < 0) {
					// We only are looking to see if we are too low, not too
					// high
					delta = 0;
				}
			} else {
				// we are too high, slide all views down to align with bottom
				child = getChildAt(childCount - 1);
				delta = child.getRight() - (getWidth() - mListPadding.right);

				if (mFirstPosition + childCount < mItemCount) {
					// It's OK to have some space below the last item if it is
					// part of the vertical spacing
					delta += mHorizontalSpacing;
				}

				if (delta > 0) {
					// We only are looking to see if we are too high, not too
					// low
					delta = 0;
				}
			}

			if (delta != 0) {
				offsetChildrenLeftAndRight(-delta);
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
	protected View fillFromSelection(int selectedLeft, int childrenLeft, int childrenRight) {
		final int fadingEdgeLength = getHorizontalFadingEdgeLength();
		int selectedPosition = mSelectedPosition;
		int numLines = mNumLines;
		final int horizontalSpacing = mHorizontalSpacing;

		int columnStart = mSelectedPosition;
		int columnEnd = -1;

		if (!mStackFromBottom) {
			if (selectedPosition >= getHeaderViewsCount()) {
				selectedPosition -= getHeaderViewsCount();
				columnStart = selectedPosition - (selectedPosition % mNumLines);
				columnStart += getHeaderViewsCount();
			}
		} else {
			int invertedSelection = mItemCount - 1 - selectedPosition;

			columnEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % mNumLines));
			columnStart = Math.max(0, columnEnd - mNumLines + 1);
		}

		View sel;
		View referenceView;

		int leftSelectionPixel = getLeftSelectionPixel(childrenLeft, fadingEdgeLength, columnStart);
		int rightSelectionPixel = getRightSelectionPixel(childrenRight, fadingEdgeLength, numLines, columnStart);

		if (columnStart < getHeaderViewsCount() || columnStart > mItemCount - getFooterViewsCount() - 1) {
			sel = makeHeaderOrFooter(mStackFromBottom ? columnEnd : columnStart, selectedLeft, true);
			numLines = 1;
		} else {
			sel = makeColumn(mStackFromBottom ? columnEnd : columnStart, selectedLeft, true);
		}
		// Possibly changed again in fillUp if we add rows above this one.
		mFirstPosition = columnStart;

		referenceView = mReferenceView;
		adjustForLeftFadingEdge(referenceView, leftSelectionPixel, rightSelectionPixel);
		adjustForRightFadingEdge(referenceView, leftSelectionPixel, rightSelectionPixel);

		if (!mStackFromBottom) {
			fillLeftWithHeaderOrFooter(columnStart, getNextRight(columnStart, referenceView));

			adjustViewsLeftOrRight();

			fillRightWithHeaderOrFooter(columnStart, getNextLeft(columnStart, referenceView));
			// fillDown(rowStart + numColumns, referenceView.getBottom() +
			// verticalSpacing);
		} else {
			fillRight(columnEnd + numLines, referenceView.getRight() + horizontalSpacing);
			adjustViewsLeftOrRight();
			fillLeft(columnStart - 1, referenceView.getLeft() - horizontalSpacing);
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
	protected void adjustForRightFadingEdge(View childInSelectedRow, int leftSelectionPixel, int rightSelectionPixel) {
		// Some of the newly selected item extends below the bottom of the
		// list
		if (childInSelectedRow.getRight() > rightSelectionPixel) {

			// Find space available above the selection into which we can
			// scroll upwards
			int spaceLeft = childInSelectedRow.getLeft() - leftSelectionPixel;

			// Find space required to bring the bottom of the selected item
			// fully into view
			int spaceRight = childInSelectedRow.getRight() - rightSelectionPixel;
			int offset = Math.min(spaceLeft, spaceRight);

			// Now offset the selected item to get it into view
			offsetChildrenLeftAndRight(-offset);
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
	protected void adjustForLeftFadingEdge(View childInSelectedRow, int leftSelectionPixel, int rightSelectionPixel) {
		// Some of the newly selected item extends above the top of the list
		int left = childInSelectedRow.getLeft();
		if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance){
			left += ((GridViewHeaderViewExpandDistance)childInSelectedRow).getLeftExpandDistance();
		}
		
		int right = childInSelectedRow.getRight();
		if(childInSelectedRow instanceof GridViewHeaderViewExpandDistance){
			right -= ((GridViewHeaderViewExpandDistance)childInSelectedRow).getRightExpandDistance();
		}
		if (left < leftSelectionPixel) {
			// Find space required to bring the top of the selected item
			// fully into view
			int spaceLeft = leftSelectionPixel - left;

			// Find space available below the selection into which we can
			// scroll downwards
			int spaceRight = rightSelectionPixel - right;
			int offset = Math.min(spaceLeft, spaceRight);

			// Now offset the selected item to get it into view
			offsetChildrenLeftAndRight(offset);
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
	private int getRightSelectionPixel(int childrenRight, int fadingEdgeLength, int numLines, int columnStart) {
		// Last pixel we can draw the selection into
		int rightSelectionPixel = childrenRight;
		if (columnStart + numLines - 1 < mItemCount - 1) {
			rightSelectionPixel -= fadingEdgeLength;
		}
		return rightSelectionPixel;
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
	private int getLeftSelectionPixel(int childrenLeft, int fadingEdgeLength, int columnStart) {
		// first pixel we can draw the selection into
		int leftSelectionPixel = childrenLeft;
		if (columnStart > 0) {
			leftSelectionPixel += fadingEdgeLength;
		}
		return leftSelectionPixel;
	}

	protected View fillSelection(int childrenLeft, int childrenRight) {
		final int selectedPosition = reconcileSelectedPosition();
		final int numLines = mNumLines;
		final int horizontalSpacing = mHorizontalSpacing;

		int columsStart;
		int columnEnd = -1;

		if (!mStackFromBottom) {
			columsStart = getColumnStart(selectedPosition);//selectedPosition - (selectedPosition % numLines);
		} else {
			final int invertedSelection = mItemCount - 1 - selectedPosition;

			columnEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numLines));
			columsStart = Math.max(0, columnEnd - numLines + 1);
		}

		final int fadingEdgeLength = getHorizontalFadingEdgeLength();
		final int leftSelectionPixel = getLeftSelectionPixel(childrenLeft, fadingEdgeLength, columsStart);

		final View sel = makeColumn(mStackFromBottom ? columnEnd : columsStart, leftSelectionPixel, true);
		mFirstPosition = columsStart;

		final View referenceView = mReferenceView;

		if (!mStackFromBottom) {
			fillRightWithHeaderOrFooter(columsStart, getNextLeft(columsStart, referenceView));
			// fillDown(rowStart + numColumns, referenceView.getBottom() +
			// verticalSpacing);
			pinToRight(childrenRight);
			fillLeftWithHeaderOrFooter(columsStart, getNextRight(columsStart, referenceView));
			// fillUp(rowStart - numColumns, referenceView.getTop() -
			// verticalSpacing);
			adjustViewsLeftOrRight();
		} else {
			final int rightSelectionPixel = getRightSelectionPixel(childrenRight, fadingEdgeLength, numLines, columsStart);
			final int offset = rightSelectionPixel - referenceView.getRight();
			offsetChildrenLeftAndRight(offset);
			fillLeft(columsStart - 1, referenceView.getLeft() - horizontalSpacing);
			pinToLeft(childrenLeft);
			fillRight(columnEnd + numLines, referenceView.getRight() + horizontalSpacing);
			adjustViewsLeftOrRight();
		}

		return sel;
	}
	
	protected View fillSelectionMiddle(int childrenLeft, int childrenRight) {
		int width = getWidth();
		final int selectedPosition = reconcileSelectedPosition();
		final int numLines = mNumLines;
		final int horizontalSpacing = mHorizontalSpacing;

		int columnStart;
		int columnEnd = -1;

		if (!mStackFromBottom) {
			columnStart = getColumnStart(selectedPosition);//selectedPosition - (selectedPosition % numColumns);
		} else {
			final int invertedSelection = mItemCount - 1 - selectedPosition;

			columnEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numLines));
			columnStart = Math.max(0, columnEnd - numLines + 1);
		}

		final int fadingEdgeLength = getHorizontalFadingEdgeLength();
		final int leftSelectionPixel = getLeftSelectionPixel(childrenLeft, fadingEdgeLength, columnStart);
		final int rightSelectionPixel = getRightSelectionPixel(childrenRight, fadingEdgeLength, numLines, columnStart);
		
		final View sel = makeColumn(mStackFromBottom ? columnEnd : columnStart, leftSelectionPixel, true);
		for(int i = 0; i < getChildCount(); i++){
			View childView = getChildAt(i);
			adjustForLeftFadingEdge(childView, leftSelectionPixel, rightSelectionPixel);
			adjustForRightFadingEdge(childView, leftSelectionPixel, rightSelectionPixel);
		}

		mFirstPosition = columnStart;

		final View referenceView = mReferenceView;

		if (!mStackFromBottom) {
			fillRightWithHeaderOrFooter(columnStart, getNextLeft(columnStart, referenceView));
			// fillDown(rowStart + numColumns, referenceView.getBottom() +
			// verticalSpacing);
			pinToRight(childrenRight);
			fillLeftWithHeaderOrFooter(columnStart, getNextRight(columnStart, referenceView));
			// fillUp(rowStart - numColumns, referenceView.getTop() -
			// verticalSpacing);
			adjustViewsLeftOrRight();
		} else {
			final int offset = rightSelectionPixel - referenceView.getRight();
			offsetChildrenLeftAndRight(offset);
			fillLeft(columnStart - 1, referenceView.getLeft() - horizontalSpacing);
			pinToLeft(childrenLeft);
			fillRight(columnEnd + numLines, referenceView.getRight() + horizontalSpacing);
			adjustViewsLeftOrRight();
		}

		return sel;
	}

	private void pinToLeft(int childrenLeft) {
		if (mFirstPosition == 0) {
			final int left = getChildAt(0).getLeft();
			final int offset = childrenLeft - left;
			if (offset < 0) {
				offsetChildrenLeftAndRight(offset);
			}
		}
	}

	private void pinToRight(int childrenRight) {
		final int count = getChildCount();
		if (mFirstPosition + count == mItemCount) {
			final int right = getChildAt(count - 1).getRight();
			final int offset = childrenRight - right;
			if (offset > 0) {
				offsetChildrenLeftAndRight(offset);
			}
		}
	}

	@Override
	int findMotionRow(int x) {
		final int childCount = getChildCount();
		if (childCount > 0) {

			final int numLines = mNumLines;
			if (!mStackFromBottom) {
				for (int i = 0; i < childCount; i += numLines) {
					if (x <= getChildAt(i).getRight()) {
						return mFirstPosition + i;
					}
				}
			} else {
				for (int i = childCount - 1; i >= 0; i -= numLines) {
					if (x >= getChildAt(i).getLeft()) {
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
	protected View fillSpecific(int position, int left) {
		int numLines = mNumLines;

		int motionColumnStart = position;
		int motionColumnEnd = -1;

		if (!mStackFromBottom) {
			if (position >= getHeaderViewsCount()) {
				position -= getHeaderViewsCount();
				motionColumnStart = position - (position % numLines);
				motionColumnStart += getHeaderViewsCount();
			}
		} else {
			final int invertedSelection = mItemCount - 1 - position;

			motionColumnEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numLines));
			motionColumnStart = Math.max(0, motionColumnEnd - numLines + 1);
		}

		// View temp = makeRow(mStackFromBottom ? motionRowEnd : motionRowStart,
		// top, true);
		View temp = null;
		if (motionColumnStart < getHeaderViewsCount() || motionColumnStart > mItemCount - getFooterViewsCount() - 1) {
			temp = makeHeaderOrFooter(motionColumnStart, left, true);
			numLines = 1;
		} else {
			temp = makeColumn(mStackFromBottom ? motionColumnEnd : motionColumnStart, left, true);
		}

		// Possibly changed again in fillUp if we add rows above this one.
		mFirstPosition = motionColumnStart;

		final View referenceView = mReferenceView;
		// We didn't have anything to layout, bail out
		if (referenceView == null) {
			return null;
		}

		final int horizontalSpacing = mHorizontalSpacing;

		View leftWards;
		View rightWards;

		if (!mStackFromBottom) {
			leftWards = fillLeftWithHeaderOrFooter(motionColumnStart, getNextRight(motionColumnStart, referenceView));
			// above = fillUp(motionRowStart - numColumns,
			// referenceView.getTop() - verticalSpacing);
			adjustViewsLeftOrRight();

			rightWards = fillRightWithHeaderOrFooter(motionColumnStart, getNextLeft(motionColumnStart, referenceView));
			// below = fillDown(motionRowStart + numColumns,
			// referenceView.getBottom() + verticalSpacing);
			// Check if we have dragged the bottom of the grid too high
			final int childCount = getChildCount();
			if (childCount > 0) {
				correctTooWide(numLines, horizontalSpacing, childCount);
			}
		} else {
			rightWards = fillRight(motionColumnEnd + numLines, referenceView.getRight() + horizontalSpacing);
			adjustViewsLeftOrRight();
			leftWards = fillLeft(motionColumnStart - 1, referenceView.getLeft() - horizontalSpacing);
			// Check if we have dragged the bottom of the grid too high
			final int childCount = getChildCount();
			if (childCount > 0) {
				correctTooNarrow(numLines, horizontalSpacing, childCount);
			}
		}

		if (temp != null) {
			return temp;
		} else if (leftWards != null) {
			return leftWards;
		} else {
			return rightWards;
		}
	}

	protected void correctTooWide(int numLines, int horizontalSpacing, int childCount) {
		// First see if the last item is visible
		final int lastPosition = mFirstPosition + childCount - 1;
		if (lastPosition == mItemCount - 1 && childCount > 0) {
			// Get the last child ...
			final View lastChild = getChildAt(childCount - 1);

			// ... and its bottom edge
			int lastRight = lastChild.getRight();
			if(lastChild instanceof GridViewHeaderViewExpandDistance){
				lastRight -= ((GridViewHeaderViewExpandDistance)lastChild).getRightExpandDistance();
			}
			// This is bottom of our drawable area
			final int end = (getRight() - getLeft()) - mListPadding.right;

			// This is how far the bottom edge of the last view is from the
			// bottom of the
			// drawable area
			int rightOffset = end - lastRight;

			final View firstChild = getChildAt(0);
			int firstLeft = firstChild.getLeft();
			if(firstChild instanceof GridViewHeaderViewExpandDistance){
				firstLeft += ((GridViewHeaderViewExpandDistance)firstChild).getLeftExpandDistance();
			}

			// Make sure we are 1) Too high, and 2) Either there are more rows
			// above the
			// first row or the first row is scrolled off the top of the
			// drawable area
			if (rightOffset > 0 && (mFirstPosition > 0 || firstLeft < mListPadding.left)) {
				if (mFirstPosition == 0) {
					// Don't pull the top too far down
					rightOffset = Math.min(rightOffset, mListPadding.left - firstLeft);
				}

				// Move everything down
				offsetChildrenLeftAndRight(rightOffset);
				if (mFirstPosition > 0) {
					// Fill the gap that was opened above mFirstPosition with
					// more rows, if
					// possible
					// fillUpWithHeaderOrFooter(mFirstPosition - 1,
					// getNextBottom(mFirstPosition - 1, firstChild));
					fillLeft(mFirstPosition - mNumLines, getNextRight(mFirstPosition - 1, firstChild));
					// Close up the remaining gap
					adjustViewsLeftOrRight();
				}
			}
		}
	}

	protected void correctTooNarrow(int numLines, int horizontalSpacing, int childCount) {
		if (mFirstPosition == 0 && childCount > 0) {
			// Get the first child ...
			final View firstChild = getChildAt(0);

			// ... and its top edge
			int firstLeft = firstChild.getLeft();
			if(firstChild instanceof GridViewHeaderViewExpandDistance){
				firstLeft += ((GridViewHeaderViewExpandDistance)firstChild).getLeftExpandDistance();
			}

			// This is top of our drawable area
			final int start = mListPadding.left;

			// This is bottom of our drawable area
			final int end = (getRight() - getLeft()) - mListPadding.right;

			// This is how far the top edge of the first view is from the top of
			// the
			// drawable area
			int leftOffset = firstLeft - start;
			final View lastChild = getChildAt(childCount - 1);
			int lastRight = lastChild.getRight();
			if(lastChild instanceof GridViewHeaderViewExpandDistance){
				lastRight -= ((GridViewHeaderViewExpandDistance)lastChild).getRightExpandDistance();
			}
			final int lastPosition = mFirstPosition + childCount - 1;

			// Make sure we are 1) Too low, and 2) Either there are more rows
			// below the
			// last row or the last row is scrolled off the bottom of the
			// drawable area
			if (leftOffset > 0 && (lastPosition < mItemCount - 1 || lastRight > end)) {
				if (lastPosition == mItemCount - 1) {
					// Don't pull the bottom too far up
					leftOffset = Math.min(leftOffset, lastRight - end);
				}

				// Move everything up
				offsetChildrenLeftAndRight(-leftOffset);
				if (lastPosition < mItemCount - 1) {
					// Fill the gap that was opened below the last position with
					// more rows, if
					// possible
					// fillDownWithHeaderOrFooter(lastPosition + 1,
					// getNextTop(lastPosition + 1, lastChild));
					fillRight(lastPosition + mNumLines, getNextLeft(lastPosition + 1, lastChild));
					// Close up the remaining gap
					adjustViewsLeftOrRight();
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
	protected View fillFromLeft(int nextLeft) {
		mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}
		mFirstPosition -= mFirstPosition % mNumLines;
		return fillRight(mFirstPosition, nextLeft);
	}

	protected View fillFromRight(int lastPosition, int nextRight) {
		lastPosition = Math.max(lastPosition, mSelectedPosition);
		lastPosition = Math.min(lastPosition, mItemCount - 1);

		final int invertedPosition = mItemCount - 1 - lastPosition;
		lastPosition = mItemCount - 1 - (invertedPosition - (invertedPosition % mNumLines));

		return fillLeft(lastPosition, nextRight);
	}

	private View makeHeaderOrFooter(int pos, int x, boolean flow) {
		int nextTop;
		final int lineHeight = mLineHeight;
		final boolean isLayoutRtl = false;// isLayoutRtl();
		final int verticalSpacing = mVerticalSpacing;

		// if (isLayoutRtl) {
		// nextLeft = getWidth() - mListPadding.right - columnWidth -
		// ((mStretchMode == STRETCH_SPACING_UNIFORM) ? horizontalSpacing : 0);
		// } else {
		// nextLeft = mListPadding.left + ((mStretchMode ==
		// STRETCH_SPACING_UNIFORM) ? horizontalSpacing : 0);
		// }

		nextTop = mListPadding.top;
		boolean selected = pos == mSelectedPosition;
		final int where = flow ? -1 : 0;

		mReferenceView = makeAndAddView(pos, x, flow, nextTop, selected, where, getHeight() - mListPadding.top - mListPadding.bottom);

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
	
	int firstRowMarginTop = 0;
	
	/**
	 * 每行第一列与左边的距离
	 * @param firstColumnMarginleft
	 */
	public void setFirstRowMarginTop(int firstRowMarginTop) {
		this.firstRowMarginTop = firstRowMarginTop;
	}

	private View makeColumn(int startPos, int x, boolean flow) {
		final int lineHeight = mLineHeight;
		final int verticalSpacing = mVerticalSpacing;

		final boolean isLayoutRtl = false;// isLayoutRtl();

		int last;
		int nextTop;

		if (isLayoutRtl) {
			nextTop = getHeight() - mListPadding.bottom - lineHeight - ((mStretchMode == STRETCH_SPACING_UNIFORM) ? verticalSpacing : 0);
		} else {
			nextTop = firstRowMarginTop + mListPadding.top + ((mStretchMode == STRETCH_SPACING_UNIFORM) ? verticalSpacing : 0);
		}

		if (!mStackFromBottom) {
			last = Math.min(startPos + mNumLines, mItemCount - getFooterViewsCount());
		} else {
			last = startPos + 1;
			startPos = Math.max(0, startPos - mNumLines + 1);

			if (last - startPos < mNumLines) {
				final int deltaTop = (mNumLines - (last - startPos)) * (lineHeight + verticalSpacing);
				nextTop += (isLayoutRtl ? -1 : +1) * deltaTop;
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
			child = makeAndAddView(pos, x, flow, nextTop, selected, where, lineHeight);

			nextTop += (isLayoutRtl ? -1 : +1) * lineHeight;
			if (pos < last - 1) {
				nextTop += verticalSpacing;
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

	private View fillLeftWithHeaderOrFooter(int columnStart, int nextRight) {
		if (DEBUG) {
			Log.d(TAG, "fillLeftWithHeaderOrFooter: columnStart = " + columnStart + ", nextBottom = " + nextRight);
		}

		if (columnStart > 0) {
			if (isHeader(columnStart - 1) || isFooter(columnStart - 1)) {
				return fillLeft(columnStart - 1, nextRight);
			} else if (isFooter(columnStart) && !isFooter(columnStart - 1)) {
				return fillLeft(getColumnStart(columnStart - 1), nextRight);
			} else if (!isHeader(columnStart) && isHeader(columnStart - 1)) {
				return fillLeft(columnStart - 1, nextRight);
			} else {
				return fillLeft(columnStart - mNumLines, nextRight);
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
	protected View fillLeft(int pos, int nextRight) {
		View selectedView = null;

		int end = 0;
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end = mListPadding.left;
		}

		while ((nextRight > end || pos >= mMinFirstPos) && pos >= 0) {
			if (isHeader(pos)) {
				View header = makeHeaderOrFooter(pos, nextRight, false);
				if (header != null) {
					selectedView = header;
				}

				nextRight = getNextRight(pos, mReferenceView);

				mFirstPosition = pos;
				pos--;
			} else if (isFooter(pos)) {
				View header = makeHeaderOrFooter(pos, nextRight, false);
				if (header != null) {
					selectedView = header;
				}

				nextRight = getNextRight(pos, mReferenceView);

				mFirstPosition = pos;
				pos--;
				pos = getColumnStart(pos);
			} else {
				View temp = makeColumn(pos, nextRight, false);
				if (temp != null) {
					selectedView = temp;
				}

				nextRight = getNextRight(pos, mReferenceView);

				mFirstPosition = pos;

				if (pos - mNumLines > getHeaderViewsCount() - 1) {
					pos -= mNumLines;
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

	private int getNextRight(int pos, View referenceView) {
		int nextRight;
		if (pos == 0) {
			nextRight = referenceView.getLeft();
		} else {
			nextRight = referenceView.getLeft() - mHorizontalSpacing;
		}
		if(referenceView instanceof GridViewHeaderViewExpandDistance){
			nextRight -= ((GridViewHeaderViewExpandDistance)referenceView).getRightExpandDistance();
		}
		
		return nextRight;
	}

	private View fillRightWithHeaderOrFooter(int columnStart, int nextLeft) {
		if (DEBUG) {
			Log.d(TAG, "fillRightWithHeaderOrFooter: columnStart = " + columnStart + ", nextLeft = " + nextLeft);
		}

		if (isHeader(columnStart + 1) || isFooter(columnStart + 1)) {
			return fillRight(columnStart + 1, nextLeft);
		} else if (isHeader(columnStart) && !isHeader(columnStart + 1)) {
			return fillRight(columnStart + 1, nextLeft);
		} else if (!isFooter(columnStart) && isFooter(columnStart + 1)) {
			return fillRight(columnStart + 1, nextLeft);
		} else {
			return fillRight(Math.min(columnStart + mNumLines, mItemCount - getFooterViewsCount()), nextLeft);
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
	private View fillRight(int pos, int nextLeft) {
		View selectedView = null;

		int end = (getRight() - getLeft());
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end -= mListPadding.right;
		}

		while ((nextLeft < end || pos <= mMinLastPos)&& pos < mItemCount) {
			if (pos < getHeaderViewsCount() || pos > mItemCount - getFooterViewsCount() - 1) {
				if(pos < getHeaderViewsCount()){
					View headerView = mHeaderViewInfos.get(pos).view;
					if(headerView != null && headerView instanceof GridViewHeaderViewExpandDistance){
						nextLeft -= ((GridViewHeaderViewExpandDistance)headerView).getLeftExpandDistance();
					}
				}
				
				View header = makeHeaderOrFooter(pos, nextLeft, true);
				if (header != null) {
					selectedView = header;
				}

				nextLeft = getNextLeft(pos, mReferenceView);

				pos++;
			} else {
				View temp = makeColumn(pos, nextLeft, true);
				if (temp != null) {
					selectedView = temp;
				}

				// mReferenceView will change with each call to makeRow()
				// do not cache in a local variable outside of this loop
				nextLeft = getNextLeft(pos, mReferenceView);

				if (pos + mNumLines < mItemCount - getFooterViewsCount()) {
					pos += mNumLines;
				} else {
					pos = mItemCount - getFooterViewsCount();
				}
			}
		}

		// setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
		// - 1);
		return selectedView;
	}

	private int getNextLeft(int pos, View referenceView) {
		int nextLeft = referenceView.getRight() + mHorizontalSpacing;
		if(referenceView instanceof GridViewHeaderViewExpandDistance){
			nextLeft -= ((GridViewHeaderViewExpandDistance)referenceView).getRightExpandDistance();
		}
		return nextLeft;
	}
	
	protected int getColumnStart(int position) {
		int columnStart = position;

		if (position < getHeaderViewsCount()) {
			columnStart = position;
		} else {
			if (position < mItemCount - getFooterViewsCount()) {
				int newPosition = position - getHeaderViewsCount();
				columnStart = newPosition - (newPosition % mNumLines) + getHeaderViewsCount();
			} else {
				columnStart = position;
			}
		}

		return columnStart;
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
	protected View moveSelection(int delta, int childrenleft, int childrenRight) {
		final int fadingEdgeLength = getVerticalFadingEdgeLength();
		final int selectedPosition = mSelectedPosition;
		final int numLines = mNumLines;
		final int horizontalSpacing = mHorizontalSpacing;

		int oldColumnStart;
		int columnStart;
		int columnEnd = -1;

		if (!mStackFromBottom) {
			// if (selectedPosition - delta < getHeaderViewsCount()) {
			// oldRowStart = selectedPosition - delta;
			// } else {
			// int newPosition = selectedPosition - delta -
			// getHeaderViewsCount();
			// oldRowStart = newPosition - (newPosition % numColumns) +
			// getHeaderViewsCount();
			// }

			oldColumnStart = getColumnStart(selectedPosition - delta);

			// if (selectedPosition < getHeaderViewsCount()) {
			// rowStart = selectedPosition;
			// } else {
			// int newPosition = selectedPosition - getHeaderViewsCount();
			// rowStart = newPosition - (newPosition % numColumns) +
			// getHeaderViewsCount();
			// }

			columnStart = getColumnStart(selectedPosition);
		} else {
			int invertedSelection = mItemCount - 1 - selectedPosition;

			columnEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numLines));
			columnStart = Math.max(0, columnEnd - numLines + 1);

			invertedSelection = mItemCount - 1 - (selectedPosition - delta);
			oldColumnStart = mItemCount - 1 - (invertedSelection - (invertedSelection % numLines));
			oldColumnStart = Math.max(0, oldColumnStart - numLines + 1);
		}

		final int rowDelta = columnStart - oldColumnStart;

		final int leftSelectionPixel = getLeftSelectionPixel(childrenleft, fadingEdgeLength, columnStart);
		final int rightSelectionPixel = getRightSelectionPixel(childrenRight, fadingEdgeLength, numLines, columnStart);

		// Possibly changed again in fillUp if we add rows above this one.
		mFirstPosition = columnStart;

		View sel;
		View referenceView;

		if (rowDelta > 0) {
			/*
			 * Case 1: Scrolling down.
			 */

			int oldRight = mReferenceViewInSelectedRow == null ? 0 : mReferenceViewInSelectedRow.getRight();
			if(mReferenceViewInSelectedRow != null && mReferenceViewInSelectedRow instanceof GridViewHeaderViewExpandDistance){
				oldRight -= ((GridViewHeaderViewExpandDistance)mReferenceViewInSelectedRow).getRightExpandDistance();
			}
			
			if (columnStart < getHeaderViewsCount() || columnStart > mItemCount - 1 - getFooterViewsCount()) {
				sel = makeHeaderOrFooter(columnStart, oldRight + horizontalSpacing, true);
			} else {
				sel = makeColumn(mStackFromBottom ? columnEnd : columnStart, oldRight + horizontalSpacing, true);
			}
			// sel = makeRow(mStackFromBottom ? rowEnd : rowStart, oldBottom +
			// verticalSpacing, true);
			referenceView = mReferenceView;

			adjustForRightFadingEdge(referenceView, leftSelectionPixel, rightSelectionPixel);
		} else if (rowDelta < 0) {
			/*
			 * Case 2: Scrolling up.
			 */
			final int oldLeft = mReferenceViewInSelectedRow == null ? 0 : mReferenceViewInSelectedRow.getLeft();

			if (columnStart < getHeaderViewsCount() || columnStart > mItemCount - 1 - getFooterViewsCount()) {
				sel = makeHeaderOrFooter(columnStart, oldLeft - horizontalSpacing, false);
			} else {
				sel = makeColumn(mStackFromBottom ? columnEnd : columnStart, oldLeft - horizontalSpacing, false);
			}
			// sel = makeRow(mStackFromBottom ? rowEnd : rowStart, oldTop -
			// verticalSpacing, false);
			referenceView = mReferenceView;

			adjustForLeftFadingEdge(referenceView, leftSelectionPixel, rightSelectionPixel);
		} else {
			/*
			 * Keep selection where it was
			 */
			final int oldLeft = mReferenceViewInSelectedRow == null ? 0 : mReferenceViewInSelectedRow.getLeft();

			if (columnStart < getHeaderViewsCount() || columnStart > mItemCount - 1 - getFooterViewsCount()) {
				sel = makeHeaderOrFooter(columnStart, oldLeft, true);
			} else {
				sel = makeColumn(mStackFromBottom ? columnEnd : columnStart, oldLeft, true);
			}
			referenceView = mReferenceView;
		}

		if (!mStackFromBottom) {
			fillLeftWithHeaderOrFooter(columnStart, getNextRight(columnStart, referenceView));
			// fillUp(rowStart - numColumns, referenceView.getTop() -
			// verticalSpacing);
			adjustViewsLeftOrRight();

			fillRightWithHeaderOrFooter(columnStart, getNextLeft(columnStart, referenceView));
		} else {
			fillRight(columnEnd + numLines, referenceView.getRight() + horizontalSpacing);
			adjustViewsLeftOrRight();
			fillLeft(columnStart - 1, referenceView.getLeft() - horizontalSpacing);
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
	private View makeAndAddView(int position, int x, boolean flow, int childrenTop, boolean selected, int where, int height) {
		View child;

		if (!mDataChanged) {
			// Try to use an existing view for this position
			child = mRecycler.getActiveView(position);
			if (child != null) {
				// Found it -- we're using an existing child
				// This just needs to be positioned
				setupChild(child, position, x, flow, childrenTop, selected, true, where, height);
				return child;
			}
		}

		// Make a new view for this position, or convert an unused view if
		// possible
		child = obtainView(position, mIsScrap);

		// This needs to be positioned and measured
		setupChild(child, position, x, flow, childrenTop, selected, mIsScrap[0], where, height);

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
	private void setupChild(View child, int position, int x, boolean flow, int childrenTop, boolean selected, boolean recycled, int where,
			int height) {
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
			int childHeightSpec = ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), 0, p.height);

			int childWidthSpec = ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.width);
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();

		int childTop;
		int childLeft = flow ? x : x - w;
		if(flow == false && child instanceof GridViewHeaderViewExpandDistance){
			childLeft += ((GridViewHeaderViewExpandDistance)child).getRightExpandDistance();
		}

		// final int layoutDirection = getLayoutDirection();
		final int absoluteGravity = Gravity.CENTER_HORIZONTAL;// Gravity.getAbsoluteGravity(mGravity,
																// layoutDirection);
		switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
		case Gravity.TOP:
			childTop = childrenTop;
			break;
		case Gravity.CENTER_HORIZONTAL:
			childTop = childrenTop + ((height - h) / 2);
			break;
		case Gravity.BOTTOM:
			childTop = childrenTop + height - h;
			break;
		default:
			childTop = childrenTop;
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

		if (action != KeyEvent.ACTION_UP) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_UP);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_DOWN);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_LEFT);
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_LEFT);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_RIGHT);
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_RIGHT);
				}
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
					handled = resurrectSelectionIfNeeded() || pageScroll(FOCUS_LEFT);
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_LEFT);
				}
				break;

			case KeyEvent.KEYCODE_PAGE_DOWN:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || pageScroll(FOCUS_RIGHT);
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_RIGHT);
				}
				break;

			case KeyEvent.KEYCODE_MOVE_HOME:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_LEFT);
				}
				break;

			case KeyEvent.KEYCODE_MOVE_END:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_RIGHT);
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
		final int numLines = mNumLines;

		int startOfColumnPos;
		int endOfColumnPos;

		boolean moved = false;

		if (!mStackFromBottom) {
			if (isHeader(selectedPosition) || isFooter(selectedPosition)) {
				startOfColumnPos = selectedPosition;
				endOfColumnPos = selectedPosition;
			} else {
				startOfColumnPos = getColumnStart(selectedPosition);
				endOfColumnPos = Math.min(startOfColumnPos + numLines - 1, mItemCount - 1 - getFooterViewsCount());
			}
		} else {
			final int invertedSelection = mItemCount - 1 - selectedPosition;
			endOfColumnPos = mItemCount - 1 - (invertedSelection / numLines) * numLines;
			startOfColumnPos = Math.max(0, endOfColumnPos - numLines + 1);
		}

		switch (direction) {
		case FOCUS_LEFT:
			if (startOfColumnPos > 0 && selectedPosition > 0) {
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
				} else if (!isHeader(selectedPosition) && isHeader(selectedPosition - numLines)) {
					setSelectionInt(Math.max(getHeaderViewsCount() - 1, 0));
				} else {
					setSelectionInt(Math.max(0, selectedPosition - numLines));
				}
				moved = true;
			}
			break;
		case FOCUS_RIGHT:
			if (endOfColumnPos < mItemCount - 1 && selectedPosition < mItemCount) {
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
				} else if (!isFooter(selectedPosition) && isFooter(selectedPosition + numLines)) {
					setSelectionInt(Math.min(mItemCount - getFooterViewsCount(), mItemCount - 1));
				} else {
					setSelectionInt(Math.min(selectedPosition + numLines, mItemCount - 1 - getFooterViewsCount()));
				}
				moved = true;
			}
			break;
		case FOCUS_UP:
			if (selectedPosition > startOfColumnPos) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(Math.max(0, selectedPosition - 1));
				moved = true;
			}
			break;
		case FOCUS_DOWN:
			if (selectedPosition < endOfColumnPos) {
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(Math.min(selectedPosition + 1, mItemCount - 1));
				moved = true;
			}
			break;
		}

		if (moved) {
			playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
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

		final int nextRow = next / mNumLines;
		final int previousRow = previous / mNumLines;

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
		if (direction == FOCUS_LEFT) {
			mLayoutMode = LAYOUT_SET_SELECTION;
			setSelectionInt(0);
			// TODO
			// invokeOnItemScrollListener();
			moved = true;
		} else if (direction == FOCUS_RIGHT) {
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

		if (direction == FOCUS_LEFT) {
			nextPage = Math.max(0, mSelectedPosition - getChildCount());
		} else if (direction == FOCUS_RIGHT) {
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
		int numLines = mNumLines;
		int count = mItemCount;

		int startOfColumn;
		int endOfColumn;
		if (!mStackFromBottom) {
			startOfColumn = (selectedPosition / numLines) * numLines;
			endOfColumn = Math.min(startOfColumn + numLines - 1, count - 1);
		} else {
			int invertedSelection = count - 1 - selectedPosition;
			endOfColumn = count - 1 - (invertedSelection / numLines) * numLines;
			startOfColumn = Math.max(0, endOfColumn - numLines + 1);
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
				showScroll = selectedPosition == endOfColumn;
			}
			break;

		case FOCUS_BACKWARD:
			if (selectedPosition > 0) {
				// Move to the previous item.
				mLayoutMode = LAYOUT_MOVE_SELECTION;
				setSelectionInt(selectedPosition - 1);
				moved = true;
				// Show the scrollbar only if changing rows.
				showScroll = selectedPosition == startOfColumn;
			}
			break;
		}

		if (moved) {
			playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
			// TODO
			// invokeOnItemScrollListener();
		}

		if (showScroll) {
			awakenScrollBars();
		}

		return moved;
	}
	
	public static interface GridViewHeaderViewExpandDistance{
		public int getLeftExpandDistance();
		public int getRightExpandDistance();
	}
	
    public void setStretchMode(int stretchMode) {
        if (stretchMode != mStretchMode) {
            mStretchMode = stretchMode;
            requestLayoutIfNecessary();
        }
    }
}

package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.SpinnerAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * A view that shows items in a horizontal scrolling list. The items come from
 * the {@link Adapter} associated with this view.
 * 
 */
public class HorizontalGridView extends AbsHorizontalListView {

	int mDividerWidth;
	Drawable mDivider;
	int mNumLines;
	int mTotalHeight = 0;
	List<Integer> mHeightList = new LinkedList<Integer>();
	protected int mHorizontalSpacing = 0;
	protected int mVerticalSpacing = 0;
	final static int NO_POSITION = -1;
	private final static String TAG = "HorizontalListView";

	public HorizontalGridView(Context context) {
		this(context, null);

	}

	public HorizontalGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HorizontalGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setSpacing(0);
	}

	public void setNumLines(int lines) {
		this.mNumLines = lines;
	}

	public int getNumLines() {
		return this.mNumLines;
	}

	public void setHorizontalSpacing(int spacing) {
		this.mHorizontalSpacing = spacing;
	}

	public void setVerticalSpacing(int spacing) {
		this.mVerticalSpacing = spacing;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (widthMode == MeasureSpec.AT_MOST) {
			int widthSize = MeasureSpec.getSize(widthMeasureSpec);
			int heightSize = getMeasuredHeight();
			int measureAllChildWidthSize = measureWidthOfChildren(heightMeasureSpec, 0, NO_POSITION, widthSize, -1);
			widthSize = measureAllChildWidthSize > widthSize ? widthSize : measureAllChildWidthSize;
			setMeasuredDimension(widthSize, heightSize);
		}

		int heightSize = getMeasuredHeight();
		switch (mGravity) {
		case Gravity.CENTER_VERTICAL:
			mGravityHeightAnchor = heightSize >> 1;
			break;
		}

		mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
		final int count = mItemCount;
		if (count > 0) {
			if (heightMode == MeasureSpec.UNSPECIFIED) {
				final View child = obtainView(0, mIsScrap);
				AbsSpinner.LayoutParams p = (AbsSpinner.LayoutParams) child.getLayoutParams();
				if (p == null) {
					p = new AbsSpinner.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
					child.setLayoutParams(p);
				}
				p.viewType = mAdapter.getItemViewType(0);
				p.forceAdd = true;

				int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.bottom,
						p.height);
				int lpWidth = p.width;
				int childWidthSpec;
				if (lpWidth > 0) {
					childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth, MeasureSpec.EXACTLY);
				} else {
					childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				}

				child.measure(childWidthSpec, childHeightSpec);

				final int w = child.getMeasuredWidth();
				final int h = child.getMeasuredHeight();
				setMeasuredDimension(mWidthMeasureSpec, h * this.mNumLines + mSpinnerPadding.top + mSpinnerPadding.bottom);

				if (mRecycler.shouldRecycleViewType(p.viewType)) {
					mRecycler.addScrapView(-1, child);
				}
			} else {
				android.view.ViewGroup.LayoutParams params = getLayoutParams();
				int widthSize = params.width;
				heightSize = params.height;

				setMeasuredDimension(mWidthMeasureSpec, heightSize);
			}
		}
	}

	/**
	 * Measures the height of the given range of children (inclusive) and
	 * returns the height with this ListView's padding and divider heights
	 * included. If maxHeight is provided, the measuring will stop when the
	 * current height reaches maxHeight.
	 *
	 * @param heightMeasureSpec
	 *            The height measure spec to be given to a child's
	 *            {@link View#measure(int, int)}.
	 * @param startPosition
	 *            The position of the first child to be shown.
	 * @param endPosition
	 *            The (inclusive) position of the last child to be shown.
	 *            Specify {@link #NO_POSITION} if the last child should be the
	 *            last available child from the adapter.
	 * @param maxWidth
	 *            The maximum maxWidth that will be returned (if all the
	 *            children don't fit in this value, this value will be
	 *            returned).
	 * @param disallowPartialChildPosition
	 *            In general, whether the returned width should only contain
	 *            entire children. This is more powerful--it is the first
	 *            inclusive position at which partial children will not be
	 *            allowed. Example: it looks nice to have at least 3 completely
	 *            visible children, and in portrait this will most likely fit;
	 *            but in landscape there could be times when even 2 children can
	 *            not be completely shown, so a value of 2 (remember, inclusive)
	 *            would be good (assuming startPosition is 0).
	 * @return The width of this ListView with the given children.
	 */
	final int measureWidthOfChildren(int heightMeasureSpec, int startPosition, int endPosition, final int maxWidth,
			int disallowPartialChildPosition) {

		final SpinnerAdapter adapter = mAdapter;
		if (adapter == null) {
			return mSpinnerPadding.left + mSpinnerPadding.right;
		}

		// Include the padding of the list
		int returnedWidth = mSpinnerPadding.left + mSpinnerPadding.right;
		final int dividerWidth = ((mDividerWidth > 0) && mDivider != null) ? mDividerWidth : 0;
		// The previous height value that was less than maxHeight and contained
		// no partial children
		int prevWidthWithoutPartialChild = 0;
		int i;
		View child;

		// mItemCount - 1 since endPosition parameter is inclusive
		endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1 : endPosition;
		final AbsSpinner.RecycleBin recycleBin = mRecycler;
		final boolean recyle = recycleOnMeasure();
		final boolean[] isScrap = mIsScrap;

		for (i = startPosition; i <= endPosition; ++i) {
			child = obtainView(i, isScrap);

			measureScrapChild(child, i, heightMeasureSpec);

			if (i > 0) {
				// Count the divider for all but one child
				returnedWidth += dividerWidth;
			}

			// Recycle the view before we possibly return from the method
			int viewType = ((LayoutParams) child.getLayoutParams()).viewType;
			if (recyle && recycleBin.shouldRecycleViewType(viewType)) {
				recycleBin.addScrapView(-1, child);
			}

			returnedWidth += child.getMeasuredWidth();

			if (returnedWidth >= maxWidth) {
				// We went over, figure out which height to return. If
				// returnedHeight > maxHeight,
				// then the i'th position did not fit completely.
				return (disallowPartialChildPosition >= 0) // Disallowing is
															// enabled (> -1)
						&& (i > disallowPartialChildPosition) // We've past the
																// min pos
						&& (prevWidthWithoutPartialChild > 0) // We have a prev
																// height
						&& (returnedWidth != maxWidth) // i'th child did not fit
														// completely
				? prevWidthWithoutPartialChild : returnedWidth;
			}

			if ((disallowPartialChildPosition >= 0) && (i >= disallowPartialChildPosition)) {
				prevWidthWithoutPartialChild = returnedWidth;
			}
		}

		// At this point, we went through the range of children, and they each
		// completely fit, so return the returnedHeight
		return returnedWidth;
	}

	public void setSelection(int position) {
//		int numColums = (this.getLastVisiblePosition() - this.getFirstVisiblePosition()) / this.mNumLines;
		int currentSelectedPosition = position - mSelectedPosition;
//		int currentSelectedColumPosition = position - mSelectedPosition - numColums;

		if (Math.abs(currentSelectedPosition) >= this.mNumLines /*|| Math.abs(currentSelectedColumPosition) == 1*/) {
			if (currentSelectedPosition >= this.mNumLines /*|| currentSelectedColumPosition == 1*/) {
				arrowScrollImpl(View.FOCUS_RIGHT);
			} else if (currentSelectedPosition <= -this.mNumLines /*|| currentSelectedColumPosition == -1*/) {
				arrowScrollImpl(View.FOCUS_LEFT);
			}
			setNextSelectedPositionInt(position);
		} else {
			super.setSelection(position);
		}
	}

	void fillGap(boolean left) {
		if (left) {
			// If moved left, there will be empty space on the right
			fillToGalleryRight();
		} else {
			// Similarly, empty space on the left
			fillToGalleryLeft();
		}
		// final int count = getChildCount();
		// if (left) {
		// int paddingLeft = 0;
		// // if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK)
		// // {
		// // paddingTop = getListPaddingTop();
		// // }
		// final int startOffset = count > 0 ? getChildAt(count - 1).getRight()
		// + mDividerWidth : paddingLeft;
		// fillRight(mFirstPosition + count, startOffset);
		// correctTooRight(getChildCount());
		// } else {
		// int paddingRight = 0;
		// // if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK)
		// // {
		// // paddingBottom = getListPaddingBottom();
		// // }
		// final int startOffset = count > 0 ? getChildAt(0).getLeft() -
		// mDividerWidth : getWidth() - paddingRight;
		// fillLeft(mFirstPosition - 1, startOffset);
		// correctTooLeft(getChildCount());
		// }
	}

	/**
	 * Determine how much we need to scroll in order to get the next selected
	 * view visible, with a fading edge showing below as applicable. The amount
	 * is capped at {@link #getMaxScrollAmount()} .
	 *
	 * @param direction
	 *            either {@link android.view.View#FOCUS_LEFT} or
	 *            {@link android.view.View#FOCUS_RIGHT}.
	 * @param nextSelectedPosition
	 *            The position of the next selection, or
	 *            {@link #INVALID_POSITION} if there is no next selectable
	 *            position
	 * @return The amount to scroll. Note: this is always positive! Direction
	 *         needs to be taken into account when actually scrolling.
	 */
	int amountToScroll(int direction, int nextSelectedPosition) {
		// TODO
		final int listRight = getListRight();
		final int listLeft = getListLeft();

		final int numChildren = getChildCount();

		if (direction == View.FOCUS_RIGHT) {
			int indexToMakeVisible = numChildren - 1;
			if (nextSelectedPosition != INVALID_POSITION) {
				indexToMakeVisible = nextSelectedPosition - mFirstPosition;
			}

			final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
			final View viewToMakeVisible = getChildAt(indexToMakeVisible);

			int goalRight = listRight;
			if (positionToMakeVisible < mItemCount - 1) {
				goalRight -= getArrowScrollPreviewLength();
			}

			if (!unhandleFullVisible) {
				if (viewToMakeVisible.getRight() <= goalRight) {
					// item is fully visible.
					return 0;
				}

				if (nextSelectedPosition != INVALID_POSITION && (goalRight - viewToMakeVisible.getLeft()) >= getMaxScrollAmount()) {
					// item already has enough of it visible, changing selection
					// is
					// good enough
					return 0;
				}
			}

			int amountToScroll = (viewToMakeVisible.getRight() - goalRight);

			if ((mFirstPosition + numChildren) == mItemCount) {
				// last is last in list -> make sure we don't scroll past it
				final int max = getChildAt(numChildren - 1).getRight() - goalRight;
				amountToScroll = Math.min(amountToScroll, max);
			}

			return Math.min(amountToScroll, getMaxScrollAmount());
		} else if (direction == View.FOCUS_LEFT) {
			int indexToMakeVisible = 0;
			if (nextSelectedPosition != INVALID_POSITION) {
				indexToMakeVisible = nextSelectedPosition - mFirstPosition;
			}
			final int positionToMakeVisible = mFirstPosition + indexToMakeVisible;
			final View viewToMakeVisible = getChildAt(indexToMakeVisible);
			int goalLeft = listLeft;
			if (positionToMakeVisible > 0) {
				goalLeft += getArrowScrollPreviewLength();
			}

			if (!unhandleFullVisible) {
				if (viewToMakeVisible.getLeft() >= goalLeft) {
					// item is fully visible.
					return 0;
				}

				if (nextSelectedPosition != INVALID_POSITION && (viewToMakeVisible.getLeft() - goalLeft) >= getMaxScrollAmount()) {
					// item already has enough of it visible, changing selection
					// is good enough
					return 0;
				}
			}

			int amountToScroll = (goalLeft - viewToMakeVisible.getLeft());
			if (mFirstPosition == 0) {
				// first is first in list -> make sure we don't scroll past it
				final int max = goalLeft - getChildAt(0).getLeft();
				amountToScroll = Math.min(amountToScroll, max);
			}
			return Math.min(amountToScroll, getMaxScrollAmount());
		} else {
			return 0;
		}
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
	void layout(int delta, boolean animate) {
		// TODO
		// mStackFromRight = /*(getLayoutDirection() == LAYOUT_DIRECTION_RTL)*/
		// false;

		int childrenLeft = mSpinnerPadding.left;
		int childrenWidth = getRight() - getLeft() - mSpinnerPadding.left - mSpinnerPadding.right;

		if (mDataChanged) {
			handleDataChanged();
		}

		// Handle an empty gallery by removing all views.
		if (mItemCount == 0) {
			resetList();
			return;
		}

		int childCount = getChildCount();
		int index = 0;

		View oldSel = null;
		View oldFirst = null;
		// View newSel = null;

		// Remember the previously selected view
		index = mSelectedPosition - mFirstPosition;
		if (index >= 0 && index < childCount) {
			oldSel = getChildAt(index);
		}

		// Remember the previous first child
		oldFirst = getChildAt(0);

		if (mNextSelectedPosition >= 0) {
			delta = mNextSelectedPosition - mSelectedPosition;
		}

		// // Caution: newSel might be null
		// newSel = getChildAt(index + delta);

		// Update to the new selected position.
		if (mNextSelectedPosition >= 0) {
			setSelectedPositionInt(mNextSelectedPosition);
		}

		View focusLayoutRestoreView = null;
		// reset the focus restoration
		View focusLayoutRestoreDirectChild = null;

		// take focus back to us temporarily to avoid the eventual
		// call to clear focus when removing the focused child below
		// from messing things up when ViewAncestor assigns focus back
		// to someone else
		final View focusedChild = getFocusedChild();
		if (focusedChild != null) {
			// we can remember the focused view to restore after relayout if the
			// data hasn't changed, or if the focused position is a header or
			// footer
			if (!mDataChanged /* || isDirectChildHeaderOrFooter(focusedChild) */) {
				focusLayoutRestoreDirectChild = focusedChild;
				// remember the specific view that had focus
				focusLayoutRestoreView = findFocus();
				if (focusLayoutRestoreView != null) {
					// tell it we are going to mess with it
					focusLayoutRestoreView.onStartTemporaryDetach();
				}
			}

			requestFocus();
		}

		// All views go in recycler while we are in layout
		recycleAllViews();

		// Clear out old views
		// removeAllViewsInLayout();
		detachAllViewsFromParent();

		mTotalHeight = 0;
		mHeightList.clear();
		/*
		 * These will be used to give initial positions to views entering the
		 * gallery as we scroll
		 */
		mRightMost = 0;
		mLeftMost = 0;
		View sel = null;
		switch (mLayoutMode) {
		case LAYOUT_SPECIFIC:
			sel = fillSpecific(reconcileSelectedPosition(), mSpecificTop);
			break;
		default:
			if (mStackFromRight) {
				// TODO:fix stackFromRight
			} else {
				int firstViewLeft = oldFirst != null ? oldFirst.getLeft() : childrenLeft;
				sel = fillFromLeft(firstViewLeft);
			}
		}

		// Make selected view and center it

		/*
		 * mFirstPosition will be decreased as we add views to the left later
		 * on. The 0 for x will be offset in a couple lines down.
		 */

		// mFirstPosition = mSelectedPosition;
		// TODO need be fix

		// View sel = makeAndAddView(mSelectedPosition, 0, true, 0, true);

		// Put the selected child in the center
		// int selectedOffset = childrenLeft + (childrenWidth / 2) -
		// (sel.getWidth() / 2);
		// sel.offsetLeftAndRight(selectedOffset);

		// fillToGalleryRight();
		// fillToGalleryLeft();

		if (sel != null && gainFocus) {
			// the current selected item should get focus if items
			// are focusable
			if (mItemsCanFocus && hasFocus() && !sel.hasFocus()) {
				final boolean focusWasTaken = (sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView
						.requestFocus()) || sel.requestFocus();
				if (!focusWasTaken) {
					// selected item didn't take focus, fine, but still want
					// to make sure something else outside of the selected view
					// has focus
					final View focused = getFocusedChild();
					if (focused != null) {
						focused.clearFocus();
					}
					positionSelector(INVALID_POSITION, sel);
				} else {
					sel.setSelected(false);
					mSelectorRect.setEmpty();
				}
			} else {
				positionSelector(INVALID_POSITION, sel);
			}
			mSelectedLeft = sel.getLeft();
		} else {
			mSelectorRect.setEmpty();
		}

		// tell focus view we are done mucking with it, if it is still in
		// our view hierarchy.
		if (focusLayoutRestoreView != null && focusLayoutRestoreView.getWindowToken() != null) {
			focusLayoutRestoreView.onFinishTemporaryDetach();
		}

		// // Flush any cached views that did not get reused above
		// mRecycler.clear();

		invalidate();
		checkSelectionChanged();

		mDataChanged = false;
		if (mPositionScrollAfterLayout != null) {
			post(mPositionScrollAfterLayout);
			mPositionScrollAfterLayout = null;
		}
		mNeedSync = false;
		setNextSelectedPositionInt(mSelectedPosition);
		// updateSelectedItemMetadata(mSelectedPosition);
	}

	View addViewFoward(View theView, int position) {
		int rightPosition = position - 1;
		int edgeOfNewChild = theView.getRight() + mDividerWidth + mSpacing + mHorizontalSpacing;
		View view = null;
		int linesHeight = 0;
		for (int line = 0; line < this.mNumLines && rightPosition < mItemCount; line++) {
			if (view != null) {
				linesHeight += view.getHeight();
			}
			view = obtainView(rightPosition, mIsScrap);
			setupChild(view, rightPosition, edgeOfNewChild, false, mSpinnerPadding.top + linesHeight + mVerticalSpacing * line + mSpacing
					* line, false, mIsScrap[0]);
			rightPosition++;
		}
		return view;
	}

	View addViewBackward(View theView, int position) {
		int leftPosition = position + 1;
		int edgeOfNewChild = theView.getLeft() - mDividerWidth - mSpacing - mHorizontalSpacing;
		View view = null;
		int linesHeight = 0;
		for (int line = 0; line < this.mNumLines && leftPosition < mItemCount; line++) {
			if (view != null) {
				linesHeight += view.getHeight();
			}
			view = obtainView(leftPosition, mIsScrap);
			setupChild(view, leftPosition, edgeOfNewChild, true, mSpinnerPadding.left + linesHeight + mVerticalSpacing * line + mSpacing
					* line, false, mIsScrap[0]);
			leftPosition++;
		}
		return view;
	}

	View addViewFoward(View theView, int position, int count) {
		int rightPosition = position;
		View nextView = theView;
		for (int i = 0; i < count && rightPosition < mItemCount; i++) {
			nextView = addViewFoward(nextView, rightPosition);
			// belowPosition++;
			rightPosition += this.mNumLines;
		}
		return nextView;
	}

	View addViewBackward(View theView, int position, int count) {
		int leftPosition = position;
		View nextView = theView;
		for (int i = 0; i < count && leftPosition >= 0; i++) {
			nextView = addViewBackward(nextView, leftPosition);
			// belowPosition--;
			leftPosition -= this.mNumLines;
		}
		return nextView;
	}

	/**
	 * Scroll the children by amount, adding a view at the end and removing
	 * views that fall off as necessary.
	 *
	 * @param amount
	 *            The amount (positive or negative) to scroll.
	 */
	void scrollListItemsBy(int amount) {
		// offsetChildrenLeftAndRight(amount);
		mFlingRunnable.endFling(false);
		mFlingRunnable.startScroll(amount, 300);
		// final int listRight = getWidth() - mSpinnerPadding.right;
		// final int listLeft = mSpinnerPadding.left;
		// final AbsSpinner.RecycleBin recycleBin = mRecycler;
		//
		// if (amount > 0) {
		// // shifted items up
		//
		// // may need to pan views into the bottom space
		// int numChildren = getChildCount();
		// View last = getChildAt(numChildren - 1);
		// while (last.getRight() < listRight) {
		// final int lastVisiblePosition = mFirstPosition + numChildren - 1;
		// if (lastVisiblePosition < mItemCount - 1) {
		// last = addViewFoward(last, lastVisiblePosition);
		// numChildren++;
		// } else {
		// break;
		// }
		// }
		//
		// // may have brought in the last child of the list that is skinnier
		// // than the fading edge, thereby leaving space at the end. need
		// // to shift back
		// if (last.getRight() < listRight) {
		// offsetChildrenLeftAndRight(listRight - last.getRight());
		// }
		//
		// // left views may be panned off screen
		// View first = getChildAt(0);
		// while (first.getRight() < listLeft) {
		// AbsSpinner.LayoutParams layoutParams = (LayoutParams)
		// first.getLayoutParams();
		// if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
		// detachViewFromParent(first);
		// recycleBin.addScrapView(mFirstPosition, first);
		// } else {
		// removeViewInLayout(first);
		// }
		// first = getChildAt(0);
		// mFirstPosition++;
		// }
		// } else {
		// // shifted items down
		// View first = getChildAt(0);
		//
		// // may need to pan views into top
		// while ((first.getLeft() > listLeft) && (mFirstPosition > 0)) {
		// first = addViewBackward(first, mFirstPosition);
		// mFirstPosition--;
		// }
		//
		// // may have brought the very first child of the list in too far and
		// // need to shift it back
		// if (first.getLeft() > listLeft) {
		// offsetChildrenLeftAndRight(listLeft - first.getLeft());
		// }
		//
		// int lastIndex = getChildCount() - 1;
		// View last = getChildAt(lastIndex);
		//
		// // bottom view may be panned off screen
		// while (last.getLeft() > listRight) {
		// AbsSpinner.LayoutParams layoutParams = (LayoutParams)
		// last.getLayoutParams();
		// if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
		// detachViewFromParent(last);
		// recycleBin.addScrapView(mFirstPosition + lastIndex, last);
		// } else {
		// removeViewInLayout(last);
		// }
		// last = getChildAt(--lastIndex);
		// }
		// }
	}

	/**
	 * Fills the list from left to right, starting with mFirstPosition
	 *
	 * @param nextLeft
	 *            The location where the left of the first item should be drawn
	 *
	 * @return The view that is currently selected
	 */
	private View fillFromLeft(int nextLeft) {
		mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}
		return fillRight(mFirstPosition, nextLeft);
	}

	/**
	 * Check if we have dragged the bottom of the list too left (we have pushed
	 * the bottom element off the bottom of the screen when we did not need to).
	 * Correct by sliding everything back up.
	 *
	 * @param childCount
	 *            Number of children
	 */
	private void correctTooLeft(int childCount) {
		// First see if the first item is visible. If it is not, it is OK for
		// the
		// bottom of the list to be pushed down.
		if (mFirstPosition == 0 && childCount > 0) {

			// Get the first child ...
			final View firstChild = getChildAt(0);

			// ... and its top edge
			final int firstLeft = firstChild.getLeft();

			// This is top of our drawable area
			final int start = mSpinnerPadding.left;

			// This is bottom of our drawable area
			final int end = (getRight() - getLeft()) - mSpinnerPadding.right;

			// This is how far the top edge of the first view is from the top of
			// the
			// drawable area
			int leftOffset = firstLeft - start;
			View lastChild = getChildAt(childCount - 1);
			final int lastBottom = lastChild.getBottom();
			int lastPosition = mFirstPosition + childCount - 1;

			// Make sure we are 1) Too low, and 2) Either there are more rows
			// below the
			// last row or the last row is scrolled off the bottom of the
			// drawable area
			if (leftOffset > 0) {
				if (lastPosition < mItemCount - 1 || lastBottom > end) {
					if (lastPosition == mItemCount - 1) {
						// Don't pull the bottom too far up
						leftOffset = Math.min(leftOffset, lastBottom - end);
					}
					// Move everything up
					offsetChildrenLeftAndRight(-leftOffset);
					if (lastPosition < mItemCount - 1) {
						// Fill the gap that was opened below the last position
						// with more rows, if
						// possible
						fillRight(lastPosition + 1, lastChild.getBottom() + mDividerWidth);
						// Close up the remaining gap
						adjustViewsRightOrLeft();
					}
				} else if (lastPosition == mItemCount - 1) {
					adjustViewsRightOrLeft();
				}
			}
		}
	}

	/**
	 * Put a specific item at a specific location on the screen and then build
	 * up and down from there.
	 *
	 * @param position
	 *            The reference view to use as the starting point
	 * @param Left
	 *            Pixel offset from the top of this view to the top of the
	 *            reference view.
	 *
	 * @return The selected view, or null if the selected view is outside the
	 *         visible area.
	 */
	private View fillSpecific(int position, int Left) {

		boolean tempIsSelected = position == mSelectedPosition;
		View temp = makeAndAddView(position, Left, true, mSpinnerPadding.top, tempIsSelected);
		// Possibly changed again in fillUp if we add rows above this one.
		mFirstPosition = position;

		View above;
		View below;

		final int dividerWidth = mDividerWidth;
		if (!mStackFromRight) {
			above = fillRight(position - 1, temp.getLeft() - dividerWidth - mSpacing - mHorizontalSpacing);
			// This will correct for the top of the first view not touching the
			// top of the list
			adjustViewsRightOrLeft();
			below = fillLeft(position + 1, temp.getRight() + dividerWidth + mSpacing + mHorizontalSpacing);
			int childCount = getChildCount();
			if (childCount > 0) {
				correctTooRight(childCount);
			}
		} else {
			// TODO
			below = fillLeft(position + 1, temp.getRight() + dividerWidth + mSpacing + mHorizontalSpacing);
			// This will correct for the bottom of the last view not touching
			// the bottom of the list
			adjustViewsRightOrLeft();

			above = fillRight(position - 1, temp.getLeft() - dividerWidth - mSpacing - mHorizontalSpacing);

			int childCount = getChildCount();
			if (childCount > 0) {
				// correctTooLow(childCount);
			}
		}

		if (tempIsSelected) {
			return temp;
		} else if (above != null) {
			return above;
		} else {
			return below;
		}
	}

	/**
	 * Check if we have dragged the bottom of the list too right (we have pushed
	 * the top element off the top of the screen when we did not need to).
	 * Correct by sliding everything back down.
	 *
	 * @param childCount
	 *            Number of children
	 */
	private void correctTooRight(int childCount) {
		// First see if the last item is visible. If it is not, it is OK for the
		// top of the list to be pushed up.
		int lastPosition = mFirstPosition + childCount - 1;
		if (lastPosition == mItemCount - 1 && childCount > 0) {

			// Get the last child ...
			final View lastChild = getChildAt(childCount - 1);

			// ... and its bottom edge
			final int lastRight = lastChild.getRight();

			// This is bottom of our drawable area
			final int end = (getRight() - getLeft()) - mSpinnerPadding.right;

			// This is how far the bottom edge of the last view is from the
			// bottom of the
			// drawable area
			int rightOffset = end - lastRight;
			View firstChild = getChildAt(0);
			final int firstLeft = firstChild.getLeft();

			// Make sure we are 1) Too high, and 2) Either there are more rows
			// above the
			// first row or the first row is scrolled off the top of the
			// drawable area
			if (rightOffset > 0 && (mFirstPosition > 0 || firstLeft < mSpinnerPadding.left)) {
				if (mFirstPosition == 0) {
					// Don't pull the top too far down
					rightOffset = Math.min(rightOffset, mSpinnerPadding.left - firstLeft);
				}
				// Move everything down
				// offsetChildrenTopAndBottom(rightOffset);
				offsetChildrenLeftAndRight(rightOffset);
				if (mFirstPosition > 0) {
					// Fill the gap that was opened above mFirstPosition with
					// more rows, if
					// possible
					fillLeft(mFirstPosition - 1, firstChild.getTop() - mDividerWidth);
					// Close up the remaining gap
					adjustViewsRightOrLeft();
				}

			}
		}
	}

	/**
	 * Obtain the view and add it to our list of children. The view can be made
	 * fresh, converted from an unused view, or used as is if it was in the
	 * recycle bin.
	 *
	 * @param position
	 *            Logical position in the list
	 * @param x
	 *            left or right edge of the view to add
	 * @param flow
	 *            If flow is true, align left edge to x. If false, align right
	 *            edge to x.
	 * @param childrenTop
	 *            Top edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @return View that was added
	 */
	private View makeAndAddView(int position, int x, boolean flow, int childrenTop, boolean selected) {
		View child;

		if (!mDataChanged) {
			// Try to use an existing view for this position
			// TODO
			// child = mRecycler.getActiveView(position);
			child = mRecycler.getScrapView(position);
			if (child != null) {
				// Found it -- we're using an existing child
				// This just needs to be positioned
				child = mAdapter.getView(position, child, this);
				setupChild(child, position, x, flow, childrenTop, selected, true);

				return child;
			}
		}

		// Make a new view for this position, or convert an unused view if
		// possible
		child = obtainView(position, mIsScrap);

		// This needs to be positioned and measured
		setupChild(child, position, x, flow, childrenTop, selected, mIsScrap[0]);

		return child;
	}

	/**
	 * Add a view as a child and make sure it is measured (if necessary) and
	 * positioned properly.
	 *
	 * @param child
	 *            The view to add
	 * @param position
	 *            The position of this child
	 * @param x
	 *            The x position relative to which this view will be positioned
	 * @param flowRight
	 *            If true, align left edge to x. If false, align right edge to
	 *            x.
	 * @param childrenTop
	 *            top edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @param recycled
	 *            Has this view been pulled from the recycle bin? If so it does
	 *            not need to be remeasured.
	 */
	private void setupChild(View child, int position, int x, boolean flowRight, int childrenTop, boolean selected, boolean recycled) {
		final boolean isSelected = selected && shouldShowSelector();
		final boolean updateChildSelected = isSelected != child.isSelected();
		// TODO by lawin
		// final int mode = mTouchMode;
		// final boolean isPressed = mode > TOUCH_MODE_DOWN && mode <
		// TOUCH_MODE_SCROLL &&
		// mMotionPosition == position;
		// final boolean updateChildPressed = isPressed != child.isPressed();
		final boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

		// Respect layout params that are already in the view. Otherwise make
		// some up...
		// noinspection unchecked
		AbsSpinner.LayoutParams p = (AbsSpinner.LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = (AbsSpinner.LayoutParams) generateDefaultLayoutParams();
		}
		p.viewType = mAdapter.getItemViewType(position);

		if ((recycled && !p.forceAdd) /*
									 * || (p.recycledHeaderFooter && p.viewType
									 * ==
									 * AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER
									 * )
									 */) {
			attachViewToParent(child, flowRight ? -1 : 0, p);
		} else {
			p.forceAdd = false;
			// if (p.viewType == AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
			// p.recycledHeaderFooter = true;
			// }
			addViewInLayout(child, flowRight ? -1 : 0, p, true);
		}

		if (updateChildSelected) {
			child.setSelected(isSelected);
		}

		// if (updateChildPressed) {
		// child.setPressed(isPressed);
		// }

		// TODO by lawin
		// if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null) {
		// if (child instanceof Checkable) {
		// ((Checkable) child).setChecked(mCheckStates.get(position));
		// } else if (getContext().getApplicationInfo().targetSdkVersion
		// >= android.os.Build.VERSION_CODES.HONEYCOMB) {
		// child.setActivated(mCheckStates.get(position));
		// }
		// }
		if (needToMeasure) {
			int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.bottom, p.height);
			int lpWidth = p.width;
			int childWidthSpec;
			if (lpWidth > 0) {
				childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth, MeasureSpec.EXACTLY);
				;
			} else {
				childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();

		final int childLeft = flowRight ? x : x - w;
		childrenTop += getGravityHeightAnchor(child);

		if (needToMeasure) {
			final int childRight = childLeft + w;
			final int childBottom = childrenTop + h;
			child.layout(childLeft, childrenTop, childRight, childBottom);
		} else {
			child.offsetLeftAndRight(childLeft - child.getLeft());
			child.offsetTopAndBottom(childrenTop - child.getTop());
		}

		// TODO
		// if (mCachingStarted && !child.isDrawingCacheEnabled()) {
		// child.setDrawingCacheEnabled(true);
		// }

		if (recycled && (((AbsSpinner.LayoutParams) child.getLayoutParams()).scrappedFromPosition) != position) {
			child.jumpDrawablesToCurrentState();
		}

	}

	private void fillToGalleryLeft() {
		if (mStackFromRight) {
			fillToGalleryLeftRtl();
		} else {
			fillToGalleryLeftLtr();
		}
	}

	private void fillToGalleryLeftRtl() {
		int itemSpacing = mSpacing;
		int galleryLeft = getPaddingLeft();
		int numChildren = getChildCount();
		int numItems = mItemCount;

		// Set state for initial iteration
		View prevIterationView = getChildAt(numChildren - 1);
		int curPosition;
		int curRightEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition + numChildren;
			curRightEdge = prevIterationView.getLeft() - itemSpacing - mHorizontalSpacing;
		} else {
			// No children available!
			mFirstPosition = curPosition = mItemCount - 1;
			curRightEdge = getRight() - getLeft() - getPaddingRight();
			mShouldStopFling = true;
		}

		while (curRightEdge > galleryLeft && curPosition < mItemCount) {
			// Set state for next iteration
			curRightEdge = prevIterationView.getLeft() - itemSpacing - mHorizontalSpacing;
			int linesHeight = 0;
			for (int line = this.mNumLines - 1; line >= 0 && curPosition + line < mItemCount; line--) {
				linesHeight += mHeightList.get(line);
				int actualTop = mTotalHeight - linesHeight;

				prevIterationView = makeAndAddView(curPosition + line, curRightEdge, false, mSpinnerPadding.top + actualTop
						+ mVerticalSpacing * line + itemSpacing * line, mSelectedPosition == curPosition);
			}
			curPosition -= this.mNumLines;
		}
	}

	private void fillToGalleryLeftLtr() {
		int itemSpacing = mSpacing;
		int galleryLeft = getPaddingLeft();

		// Set state for initial iteration
		View prevIterationView = getChildAt(0);
		int curPosition;
		int curRightEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition - this.mNumLines;
			curRightEdge = prevIterationView.getLeft() - itemSpacing - mHorizontalSpacing;
		} else {
			// No children available!
			curPosition = 0;
			curRightEdge = getRight() - getLeft() - getPaddingRight();
			mShouldStopFling = true;
		}

		while (curRightEdge > galleryLeft && curPosition >= 0) {
			// Remember some state
			mFirstPosition = curPosition;

			// Set state for next iteration
			curRightEdge = prevIterationView.getLeft() - itemSpacing - mHorizontalSpacing;
			int linesHeight = 0;
			for (int line = this.mNumLines - 1; line >= 0 && curPosition + line < mItemCount; line--) {
				linesHeight += mHeightList.get(line);
				int actualTop = mTotalHeight - linesHeight;
				prevIterationView = makeAndAddView(curPosition + line, curRightEdge, false, mSpinnerPadding.top + actualTop
						+ mVerticalSpacing * line + itemSpacing * line, mSelectedPosition == curPosition);
			}
			curPosition -= this.mNumLines;
		}
	}

	private void fillToGalleryRight() {
		if (mStackFromRight) {
			fillToGalleryRightRtl();
		} else {
			fillToGalleryRightLtr();
		}
	}

	private void fillToGalleryRightRtl() {
		int itemSpacing = mSpacing;
		int galleryRight = getRight() - getLeft() - getPaddingRight();

		// Set state for initial iteration
		View prevIterationView = getChildAt(0);
		int curPosition;
		int curLeftEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition - this.mNumLines;
			curLeftEdge = prevIterationView.getRight() + itemSpacing + mHorizontalSpacing;
		} else {
			curPosition = 0;
			curLeftEdge = getPaddingLeft();
			mShouldStopFling = true;
		}

		while (curLeftEdge < galleryRight && curPosition >= 0) {
			// Remember some state
			mFirstPosition = curPosition;
			// Set state for next iteration
			curLeftEdge = prevIterationView.getRight() + itemSpacing + mHorizontalSpacing;

			int linesHeight = 0;
			View child = null;
			for (int line = 0; line < this.mNumLines && curPosition + line <= mItemCount; line++) {
				if (child != null) {
					linesHeight += child.getHeight();
				}
				prevIterationView = makeAndAddView(curPosition + line, curLeftEdge, true, mSpinnerPadding.top + linesHeight
						+ mVerticalSpacing * line + itemSpacing * line, mSelectedPosition == curPosition);

				child = prevIterationView;
			}
			curPosition -= this.mNumLines;
		}
	}

	private void fillToGalleryRightLtr() {
		int itemSpacing = mSpacing;
		int galleryRight = getRight() - getLeft() - getPaddingRight();
		int numChildren = getChildCount();
		int numItems = mItemCount;

		// Set state for initial iteration
		View prevIterationView = getChildAt(numChildren - 1);
		int curPosition;
		int curLeftEdge;

		if (prevIterationView != null) {
			curPosition = mFirstPosition + numChildren;
			curLeftEdge = prevIterationView.getRight() + itemSpacing + mHorizontalSpacing;
		} else {
			mFirstPosition = curPosition = mItemCount - 1;
			curLeftEdge = getPaddingLeft();
			mShouldStopFling = true;
		}

		while (curLeftEdge < galleryRight && curPosition < numItems) {
			curLeftEdge = prevIterationView.getRight() + itemSpacing + mHorizontalSpacing;
			int linesHeight = 0;
			View child = null;
			for (int line = 0; line < this.mNumLines && curPosition + line < numItems; line++) {
				if (child != null) {
					linesHeight += child.getHeight();
				}
				prevIterationView = makeAndAddView(curPosition + line, curLeftEdge, true, mSpinnerPadding.top + linesHeight
						+ mVerticalSpacing * line + itemSpacing * line, mSelectedPosition == curPosition);

				child = prevIterationView;
//				curPosition++;
			}
			 curPosition += this.mNumLines;
		}
	}

	/**
	 * Fills the list from pos up to the left of the list view.
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
	private View fillLeft(int pos, int nextRight) {
		View selectedView = null;

		int end = 0;
		// if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
		// end = mListPadding.top;
		// }
		boolean selected;
		View child = null;
		int linesHeight = 0;
		while (nextRight > end && pos >= 0) {
			// is this the selected item?
			for (int line = 0; line < this.mNumLines && pos > 0; line++) {
				selected = pos == mSelectedPosition;
				if (child != null) {
					linesHeight += child.getHeight();
				}
				child = makeAndAddView(pos, nextRight, false,
						mSpinnerPadding.top + linesHeight + mVerticalSpacing * line + mSpacing * line, selected);

				if (line > mHeightList.size() - 1) {
					mTotalHeight += child.getHeight();
					mHeightList.add(child.getHeight());
				}
				if (selected) {
					selectedView = child;
				}
				pos--;

			}
			nextRight = child.getLeft() - mDividerWidth;
			nextRight -= mHorizontalSpacing;
			nextRight -= mSpacing;
			child = null;
			linesHeight = 0;
		}

		mFirstPosition = pos + this.mNumLines;
		// setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
		// - 1);
		return selectedView;
	}

	/**
	 * Fills the list from pos left to the end of the list view.
	 *
	 * @param pos
	 *            The first position to put in the list
	 *
	 * @param nextRight
	 *            The location where the left of the item associated with pos
	 *            should be drawn
	 *
	 * @return The view that is currently selected, if it happens to be in the
	 *         range that we draw.
	 */
	private View fillRight(int pos, int nextLeft) {
		View selectedView = null;

		int end = getRight() - getLeft();

		// TODO by lawin
		// if ((mGroupFlags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
		// end -= mSpinnerPadding.right;
		// }
		boolean selected;
		View child = null;
		int linesHeight = 0;
		while (nextLeft < end && pos < mItemCount) {
			// is this the selected item?
			for (int line = 0; line < this.mNumLines && pos < mItemCount; line++) {
				selected = pos == mSelectedPosition;
				if (child != null) {
					linesHeight += child.getHeight();
				}
				child = makeAndAddView(pos, nextLeft, true, mSpinnerPadding.top + linesHeight + mVerticalSpacing * line + mSpacing * line,
						selected);
				if (line > mHeightList.size() - 1) {
					mTotalHeight += child.getHeight();
					mHeightList.add(child.getHeight());
				}
				if (child != null) {
					end = getMaxWidth(child, end);
				}

				// specialize for mSelectedPosition == -1
				if (selected || (mSelectedPosition == -1 && pos == 0)) {
					selectedView = child;
					if (selectedView != null)
						resetItemLayout(selectedView);
				}

				pos++;
			}
			nextLeft = child.getRight() + mDividerWidth;
			nextLeft += mHorizontalSpacing;
			nextLeft += mSpacing;
			child = null;
			linesHeight = 0;
		}

		return selectedView;
	}

	/**
	 * @return True to recycle the views used to measure this ListView in
	 *         UNSPECIFIED/AT_MOST modes, false otherwise.
	 * @hide
	 */
	@ViewDebug.ExportedProperty(category = "list")
	protected boolean recycleOnMeasure() {
		return true;
	}

	protected void measureScrapChild(View child, int position, int heightMeasureSpec) {
		AbsSpinner.LayoutParams p = (AbsSpinner.LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = new AbsSpinner.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
			child.setLayoutParams(p);
		}

		p.viewType = mAdapter.getItemViewType(position);

		// int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
		// mListPadding.left + mListPadding.right, p.width);
		int childHeightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, mSpinnerPadding.top + mSpinnerPadding.right, p.height);
		int lpWidth = p.width;
		int childWidthSpec;
		if (lpWidth > 0) {
			childWidthSpec = MeasureSpec.makeMeasureSpec(lpWidth, MeasureSpec.EXACTLY);
		} else {
			childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		return super.getChildStaticTransformation(child, t);
	}

	/**
	 * Indicates that the views created by the ListAdapter can contain focusable
	 * items.
	 *
	 * @param itemsCanFocus
	 *            true if items can get focus, false otherwise
	 */
	@Deprecated
	public void setItemsCanFocus(boolean canFocus) {

	}

	/**
	 * @param direction
	 *            either {@link android.view.View#FOCUS_UP} or
	 *            {@link android.view.View#FOCUS_DOWN}.
	 * @return The position of the next selectable position of the views that
	 *         are currently visible, taking into account the fact that there
	 *         might be no selection. Returns {@link #INVALID_POSITION} if there
	 *         is no selectable view on screen in the given direction.
	 */
	int lookForSelectablePositionOnScreen(int direction) {
		final int firstPosition = mFirstPosition;
		if (direction == View.FOCUS_RIGHT) {
			int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition + this.mNumLines : firstPosition;
			if (startPos >= mAdapter.getCount()) {
				return INVALID_POSITION;
			}
			if (startPos < firstPosition) {
				startPos = firstPosition;
			}

			final int lastVisiblePos = getLastVisiblePosition();
			final SpinnerAdapter adapter = getAdapter();
			for (int pos = startPos; pos <= lastVisiblePos; pos++) {
				if (/*
					 * adapter.isEnabled(pos) &&
					 */getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE) {
					return pos;
				}
			}
		} else if (direction == View.FOCUS_LEFT) {
			int last = firstPosition + getChildCount() - 1;
			int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition - this.mNumLines : firstPosition + getChildCount()
					- 1;
			if (startPos < 0 || startPos < 0) {
				return INVALID_POSITION;
			}
			if (startPos > last) {
				startPos = last;
			}

			final SpinnerAdapter adapter = getAdapter();
			for (int pos = startPos; pos >= firstPosition; pos--) {
				if (/*
					 * adapter.isEnabled(pos) &&
					 */getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE) {
					return pos;
				}
			}
		} else if (direction == View.FOCUS_UP) {
			if (mSelectedPosition % this.mNumLines == 0) {
				return INVALID_POSITION;
			}
			int last = firstPosition + getChildCount() - 1;
			int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition - 1 : firstPosition + getChildCount() - 1;
			if (startPos < 0 || startPos < 0) {
				return INVALID_POSITION;
			}
			if (startPos > last) {
				startPos = last;
			}

			final SpinnerAdapter adapter = getAdapter();
			for (int pos = startPos; pos >= firstPosition; pos--) {
				if (/*
					 * adapter.isEnabled(pos) &&
					 */getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE) {
					return pos;
				}
			}
		} else if (direction == View.FOCUS_DOWN) {
			if (mSelectedPosition % this.mNumLines == this.mNumLines - 1) {
				return INVALID_POSITION;
			}

			int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition + 1 : firstPosition;
			if (startPos >= mAdapter.getCount()) {
				return INVALID_POSITION;
			}
			if (startPos < firstPosition) {
				startPos = firstPosition;
			}

			final int lastVisiblePos = getLastVisiblePosition();
			final SpinnerAdapter adapter = getAdapter();
			for (int pos = startPos; pos <= lastVisiblePos; pos++) {
				if (/*
					 * adapter.isEnabled(pos) &&
					 */getChildAt(pos - firstPosition).getVisibility() == View.VISIBLE) {
					return pos;
				}
			}
		}

		return INVALID_POSITION;
	}

	/**
	 * Handle an arrow scroll going up or down. Take into account whether items
	 * are selectable, whether there are focusable items etc.
	 *
	 * @param direction
	 *            Either {@link android.view.View#FOCUS_LEFT} or
	 *            {@link android.view.View#FOCUS_RIGHT}.
	 * @return Whether any scrolling, selection or focus change occured.
	 */
	boolean arrowScrollImpl(int direction) {
		if (getChildCount() <= 0) {
			return false;
		}

		View selectedView = getSelectedView();
		int selectedPos = mSelectedPosition;

		int nextSelectedPosition = lookForSelectablePositionOnScreen(direction);
		if (nextSelectedPosition == INVALID_POSITION) {
			return false;
		}
		int amountToScroll = amountToScroll(direction, nextSelectedPosition);

		// if we are moving focus, we may OVERRIDE the default behavior
		final ArrowScrollFocusResult focusResult = mItemsCanFocus ? arrowScrollFocused(direction) : null;
		if (focusResult != null) {
			nextSelectedPosition = focusResult.getSelectedPosition();
			amountToScroll = focusResult.getAmountToScroll();
		}

		boolean needToRedraw = focusResult != null;
		if (nextSelectedPosition != INVALID_POSITION) {
			handleNewSelectionChange(selectedView, direction, nextSelectedPosition, focusResult != null);
			setSelectedPositionInt(nextSelectedPosition);
			setNextSelectedPositionInt(nextSelectedPosition);
			selectedView = getSelectedView();
			selectedPos = nextSelectedPosition;
			if (mItemsCanFocus && focusResult == null) {
				// there was no new view found to take focus, make sure we
				// don't leave focus with the old selection
				final View focused = getFocusedChild();
				if (focused != null) {
					focused.clearFocus();
				}
			}
			needToRedraw = true;
			checkSelectionChanged();
		}

		if (amountToScroll > 0) {
			scrollListItemsBy((direction == View.FOCUS_LEFT) ? -amountToScroll : amountToScroll);
			needToRedraw = true;
		}

		// if we didn't find a new focusable, make sure any existing focused
		// item that was panned off screen gives up focus.
		if (mItemsCanFocus && (focusResult == null) && selectedView != null && selectedView.hasFocus()) {
			final View focused = selectedView.findFocus();
			if (!isViewAncestorOf(focused, this) || distanceToView(focused) > 0) {
				focused.clearFocus();
			}
		}

		// if the current selection is panned off, we need to remove the
		// selection
		if (nextSelectedPosition == INVALID_POSITION && selectedView != null && !isViewAncestorOf(selectedView, this)) {
			selectedView = null;
			hideSelector();

			// but we don't want to set the ressurect position (that would make
			// subsequent
			// unhandled key events bring back the item we just scrolled off!)
			// mResurrectToPosition = INVALID_POSITION;
		}

		if (needToRedraw) {
			//
			if (selectedView != null && amountToScroll <= 0) {
				positionSelector(selectedPos, selectedView);
				mSelectedLeft = selectedView.getLeft();
			} else {
				// selector rect changed in flingRunable
			}
			if (!awakenScrollBars()) {
				invalidate();
			}
			// invokeOnItemScrollListener();
			return true;
		}

		return false;
	}

	boolean moveLeft() {
		if (mItemCount > 0 && mSelectedPosition - this.mNumLines >= 0 && arrowScrollImpl(View.FOCUS_LEFT)) {
			return true;
		} else {
			return false;
		}
	}

	boolean moveRight() {
		if (mItemCount > 0 && mSelectedPosition + this.mNumLines < mItemCount - 1 && arrowScrollImpl(View.FOCUS_RIGHT)) {
			return true;
		} else {
			return false;
		}
	}

	boolean moveUp() {
		if (mItemCount > 0 && mSelectedPosition > 0 && arrowScrollImpl(View.FOCUS_UP)) {
			return true;
		}

		return false;
	}

	boolean moveDown() {
		if (mItemCount > 0 && mSelectedPosition + 1 < mItemCount - 1 && arrowScrollImpl(View.FOCUS_DOWN)) {
			return true;
		}

		return false;
	}

	/**
	 * Handles left, right, and clicking
	 *
	 * @see android.view.View#onKeyDown
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mAdapter == null || !mIsAttached) {
			return false;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (moveLeft()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
				return true;
			}
			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (moveRight()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_RIGHT);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if (moveUp()) {
				playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (moveDown()) {
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

}

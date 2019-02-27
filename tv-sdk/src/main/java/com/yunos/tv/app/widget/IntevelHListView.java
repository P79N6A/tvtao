package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Checkable;
import android.widget.ListAdapter;

import java.util.LinkedList;
import java.util.List;

public class IntevelHListView extends AbsHListView {
	final protected static String TAG = "IntevelHListView";
	final protected static boolean DEBUG = true;
	/**
	 * When arrow scrolling, need a certain amount of pixels to preview next
	 * items. This is usually the fading edge, but if that is small enough, we
	 * want to make sure we preview at least this many pixels.
	 */
	private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;

	int mDividerWidth;
	Drawable mDivider;
	private boolean mDividerIsOpaque;
	int mSpecificLeft;
	protected int mIntevel = 50;
	String mItemRight = "";
	String mItemLeft = "";
	protected List<PositionInfo> list = new LinkedList<PositionInfo>();

	private final ArrowScrollFocusResult mArrowScrollFocusResult = new ArrowScrollFocusResult();

	public IntevelHListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public IntevelHListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public IntevelHListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setIntevel(int intevel) {
		mIntevel = intevel;
	}

	@Override
	void fillGap(boolean isRight) {
		final int count = getChildCount();
		if (isRight) {
			int paddingLeft = 0;
			if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
				paddingLeft = getListPaddingLeft();
			}
			final int startOffset = count > 0 ? getChildAt(count - 1).getRight() + mDividerWidth + mSpacing : paddingLeft;
			fillRight(mFirstPosition + count, startOffset);
			// correctTooWide(getChildCount());
			checkItemLeftIntevel(getFirstVisiblePosition(), false);
		} else {
			int paddingRight = 0;
			if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
				paddingRight = getListPaddingRight();
			}
			final int startOffset = count > 0 ? getChildAt(0).getLeft() - mDividerWidth - mSpacing : getWidth() - paddingRight;
			fillLeft(mFirstPosition - 1, startOffset);
			// correctTooNarrow(getChildCount());
			checkItemRightIntevel(getLastVisiblePosition(), false);
		}

		if (DEBUG) {
			Log.d(TAG, "fillGap: list = " + convertListToString());
		}
	}

	@Override
	protected boolean detachOffScreenChildren(boolean isRight) {
		boolean hr = super.detachOffScreenChildren(isRight);
		if (hr) {
			if (isRight) {
				if (list.size() > 0 && (list.get(0).position() <= getFirstVisiblePosition())) {
					list.remove(0);
				}
			} else {
				if (list.size() > 0 && (list.get(list.size() - 1).position() > getLastVisiblePosition())) {
					list.remove(list.size() - 1);
				}
			}
		}
		return hr;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		final ListAdapter adapter = mAdapter;
		int closetChildIndex = -1;
		int closestChildLeft = 0;
		if (adapter != null && gainFocus && previouslyFocusedRect != null) {
			previouslyFocusedRect.offset(getScrollX(), getScrollY());

			// Don't cache the result of getChildCount or mFirstPosition here,
			// it could change in layoutChildren.
			if (adapter.getCount() < getChildCount() + mFirstPosition) {
				mLayoutMode = LAYOUT_NORMAL;
				layoutChildren();
			}

			// figure out which item should be selected based on previously
			// focused rect
			Rect otherRect = mTempRect;
			int minDistance = Integer.MAX_VALUE;
			final int childCount = getChildCount();
			final int firstPosition = mFirstPosition;

			for (int i = 0; i < childCount; i++) {
				// only consider selectable views
				if (!adapter.isEnabled(firstPosition + i)) {
					continue;
				}

				View other = getChildAt(i);
				other.getDrawingRect(otherRect);
				offsetDescendantRectToMyCoords(other, otherRect);
				int distance = getDistance(previouslyFocusedRect, otherRect, direction);

				if (distance < minDistance) {
					minDistance = distance;
					closetChildIndex = i;
					closestChildLeft = other.getLeft();
				}
			}
		}

		if (closetChildIndex >= 0) {
			setSelectionFromLeft(closetChildIndex + mFirstPosition, closestChildLeft - mListPadding.left);
		} else {
			// requestLayout();
		}
	}

	/**
	 * Sets the drawable that will be drawn between each item in the list. If
	 * the drawable does not have an intrinsic height, you should also call
	 * {@link #setDividerHeight(int)}
	 * 
	 * @param divider
	 *            The drawable to use.
	 */
	public void setDivider(Drawable divider) {
		if (divider != null) {
			mDividerWidth = divider.getIntrinsicWidth();
		} else {
			mDividerWidth = 0;
		}
		mDivider = divider;
		mDividerIsOpaque = divider == null || divider.getOpacity() == PixelFormat.OPAQUE;
		requestLayout();
		invalidate();
	}

	@Override
	protected void layoutChildren() {
		final boolean blockLayoutRequests = mBlockLayoutRequests;
		if (!blockLayoutRequests) {
			mBlockLayoutRequests = true;
		} else {
			return;
		}

		mItemRight = "";
		mItemLeft = "";
		list.clear();
		try {
			// super.layoutChildren();

			invalidate();

			if (mAdapter == null) {
				resetList();
				// invokeOnItemScrollListener();
				return;
			}

			int childrenLeft = mListPadding.left;
			int childrenRight = getRight() - getLeft() - mListPadding.right;

			int childCount = getChildCount();
			int index = 0;
			int delta = 0;

			View sel;
			View oldSel = null;
			View oldFirst = null;
			View newSel = null;

			View focusLayoutRestoreView = null;

			// AccessibilityNodeInfo accessibilityFocusLayoutRestoreNode = null;
			// View accessibilityFocusLayoutRestoreView = null;
			// int accessibilityFocusPosition = INVALID_POSITION;

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
			default:
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

				// Caution: newSel might be null
				newSel = getChildAt(index + delta);
			}

			boolean dataChanged = mDataChanged;
			if (dataChanged) {
				handleDataChanged();
			}

			// Handle the empty set by removing all views that are visible
			// and calling it a day
			if (mItemCount == 0) {
				resetList();
				// invokeOnItemScrollListener();
				return;
			} else if (mItemCount != mAdapter.getCount()) {
				throw new IllegalStateException("The content of the adapter has changed but " + "ListView did not receive a notification. Make sure the content of "
						+ "your adapter is not modified from a background thread, but only " + "from the UI thread. [in ListView(" + getId() + ", " + getClass() + ") with Adapter("
						+ mAdapter.getClass() + ")]");
			}

			setSelectedPositionInt(mNextSelectedPosition);

			// Pull all children into the RecycleBin.
			// These views will be reused if possible
			final int firstPosition = mFirstPosition;
			final RecycleBin recycleBin = mRecycler;

			// reset the focus restoration
			View focusLayoutRestoreDirectChild = null;

			// Don't put header or footer views into the Recycler. Those are
			// already cached in mHeaderViews;
			if (dataChanged) {
				for (int i = 0; i < childCount; i++) {
					recycleBin.addScrapView(getChildAt(i), firstPosition + i);
				}
			} else {
				recycleBin.fillActiveViews(childCount, firstPosition);
			}

			// take focus back to us temporarily to avoid the eventual
			// call to clear focus when removing the focused child below
			// from messing things up when ViewAncestor assigns focus back
			// to someone else
			final View focusedChild = getFocusedChild();
			if (focusedChild != null) {
				// TODO: in some cases focusedChild.getParent() == null

				// we can remember the focused view to restore after relayout if
				// the
				// data hasn't changed, or if the focused position is a header
				// or footer
				if (!dataChanged || isDirectChildHeaderOrFooter(focusedChild)) {
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

			// // Remember which child, if any, had accessibility focus.
			// final ViewRootImpl viewRootImpl = getViewRootImpl();
			// if (viewRootImpl != null) {
			// final View accessFocusedView =
			// viewRootImpl.getAccessibilityFocusedHost();
			// if (accessFocusedView != null) {
			// final View accessFocusedChild = findAccessibilityFocusedChild(
			// accessFocusedView);
			// if (accessFocusedChild != null) {
			// if (!dataChanged ||
			// isDirectChildHeaderOrFooter(accessFocusedChild)) {
			// // If the views won't be changing, try to maintain
			// // focus on the current view host and (if
			// // applicable) its virtual view.
			// accessibilityFocusLayoutRestoreView = accessFocusedView;
			// accessibilityFocusLayoutRestoreNode = viewRootImpl
			// .getAccessibilityFocusedVirtualView();
			// } else {
			// // Otherwise, try to maintain focus at the same
			// // position.
			// accessibilityFocusPosition =
			// getPositionForView(accessFocusedChild);
			// }
			// }
			// }
			// }

			// Clear out old views
			detachAllViewsFromParent();
			recycleBin.removeSkippedScrap();

			switch (mLayoutMode) {
			case LAYOUT_SET_SELECTION:
				if (newSel != null) {
					sel = fillFromSelection(newSel.getLeft(), childrenLeft, childrenRight);
				} else {
					sel = fillFromMiddle(childrenLeft, childrenRight);
				}
				break;
			case LAYOUT_SYNC:
				sel = fillSpecific(mSyncPosition, mSpecificLeft);
				break;
			case LAYOUT_FORCE_RIGHT:
				sel = fillLeft(mItemCount - 1, childrenRight);
				adjustViewsLeftOrRight();
				break;
			case LAYOUT_FORCE_LEFT:
				mFirstPosition = 0;
				sel = fillFromLeft(childrenLeft);
				adjustViewsLeftOrRight();
				break;
			case LAYOUT_SPECIFIC:
				sel = fillSpecific(reconcileSelectedPosition(), mSpecificLeft);
				break;
			case LAYOUT_MOVE_SELECTION:
				sel = moveSelection(oldSel, newSel, delta, childrenLeft, childrenRight);
				break;
			default:
				if (childCount == 0) {
					if (!mStackFromBottom) {
						final int position = lookForSelectablePosition(0, true);
						setSelectedPositionInt(position);
						sel = fillFromLeft(childrenLeft);
					} else {
						final int position = lookForSelectablePosition(mItemCount - 1, false);
						setSelectedPositionInt(position);
						sel = fillLeft(mItemCount - 1, childrenRight);
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
				// the current selected item should get focus if items
				// are focusable
				if (mItemsCanFocus && hasFocus() && !sel.hasFocus()) {
					final boolean focusWasTaken = (sel == focusLayoutRestoreDirectChild && focusLayoutRestoreView != null && focusLayoutRestoreView.requestFocus()) || sel.requestFocus();
					if (!focusWasTaken) {
						// selected item didn't take focus, fine, but still want
						// to make sure something else outside of the selected
						// view
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
				// mSelectedTop = sel.getTop();
			} else {
				if (mTouchMode > TOUCH_MODE_DOWN && mTouchMode < TOUCH_MODE_SCROLL) {
					View child = getChildAt(mMotionPosition - mFirstPosition);
					if (child != null)
						positionSelector(mMotionPosition, child);
				} else {
					// mSelectedTop = 0;
					mSelectorRect.setEmpty();
				}

				// even if there is not selected position, we may need to
				// restore
				// focus (i.e. something focusable in touch mode)
				if (hasFocus() && focusLayoutRestoreView != null) {
					focusLayoutRestoreView.requestFocus();
				}
			}

			// // Attempt to restore accessibility focus.
			// if (accessibilityFocusLayoutRestoreNode != null) {
			// accessibilityFocusLayoutRestoreNode.performAction(
			// AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
			// } else if (accessibilityFocusLayoutRestoreView != null) {
			// accessibilityFocusLayoutRestoreView.requestAccessibilityFocus();
			// } else if (accessibilityFocusPosition != INVALID_POSITION) {
			// // Bound the position within the visible children.
			// final int position = MathUtils.constrain(
			// (accessibilityFocusPosition - mFirstPosition), 0,
			// (getChildCount() - 1));
			// final View restoreView = getChildAt(position);
			// if (restoreView != null) {
			// restoreView.requestAccessibilityFocus();
			// }
			// }

			// tell focus view we are done mucking with it, if it is still in
			// our view hierarchy.
			if (focusLayoutRestoreView != null && focusLayoutRestoreView.getWindowToken() != null) {
				focusLayoutRestoreView.onFinishTemporaryDetach();
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

			// invokeOnItemScrollListener();
		} finally {
			if (!blockLayoutRequests) {
				mBlockLayoutRequests = false;
			}
		}

		// checkItemLeftIntevel(getFirstVisiblePosition(), true);
		// checkItemRightIntevel(getLastVisiblePosition(), true);

		if (DEBUG) {
			Log.d(TAG, "layoutChildren: list = " + convertListToString());
		}
	}

	protected String convertListToString() {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			s += list.get(i).toString();
		}

		return s;
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
	private View fillFromSelection(int selectedLeft, int childrenLeft, int childrenRight) {
		int fadingEdgeLength = getHorizontalFadingEdgeLength();
		final int selectedPosition = mSelectedPosition;

		View sel;

		final int leftSelectionPixel = getLeftSelectionPixel(childrenLeft, fadingEdgeLength, selectedPosition);
		final int rightSelectionPixel = getRightSelectionPixel(childrenRight, fadingEdgeLength, selectedPosition);

		sel = makeAndAddView(selectedPosition, selectedLeft, true, mListPadding.top, true);

		// Some of the newly selected item extends below the bottom of the list
		if (sel.getRight() > rightSelectionPixel) {
			// Find space available above the selection into which we can scroll
			// upwards
			final int spaceLeft = sel.getLeft() - leftSelectionPixel;

			// Find space required to bring the bottom of the selected item
			// fully into view
			final int spaceRight = sel.getRight() - rightSelectionPixel;
			final int offset = Math.min(spaceLeft, spaceRight);

			// Now offset the selected item to get it into view
			sel.offsetLeftAndRight(-offset);
		} else if (sel.getLeft() < leftSelectionPixel) {
			// Find space required to bring the top of the selected item fully
			// into view
			final int spaceLeft = leftSelectionPixel - sel.getLeft();

			// Find space available below the selection into which we can scroll
			// downwards
			final int spaceRight = rightSelectionPixel - sel.getRight();
			final int offset = Math.min(spaceLeft, spaceRight);

			// Offset the selected item to get it into view
			sel.offsetLeftAndRight(offset);
		}

		// Fill in views above and below
		fillLeftAndRight(sel, selectedPosition);

		if (!mStackFromBottom) {
			correctTooWide(getChildCount());
		} else {
			correctTooNarrow(getChildCount());
		}

		return sel;
	}

	/**
	 * Put mSelectedPosition in the middle of the screen and then build up and
	 * down from there. This method forces mSelectedPosition to the center.
	 * 
	 * @param childrenTop
	 *            Top of the area in which children can be drawn, as measured in
	 *            pixels
	 * @param childrenBottom
	 *            Bottom of the area in which children can be drawn, as measured
	 *            in pixels
	 * @return Currently selected view
	 */
	private View fillFromMiddle(int childrenLeft, int childrenRight) {
		int width = childrenRight - childrenLeft;

		int position = reconcileSelectedPosition();

		View sel = makeAndAddView(position, childrenLeft, true, mListPadding.top, true);
		mFirstPosition = position;

		int selWidth = sel.getMeasuredWidth();
		if (selWidth <= width) {
			sel.offsetLeftAndRight((width - selWidth) / 2);
		}

		fillLeftAndRight(sel, position);

		if (!mStackFromBottom) {
			correctTooWide(getChildCount());
		} else {
			correctTooNarrow(getChildCount());
		}

		return sel;
	}

	/**
	 * Put a specific item at a specific location on the screen and then build
	 * up and down from there.
	 * 
	 * @param position
	 *            The reference view to use as the starting point
	 * @param top
	 *            Pixel offset from the top of this view to the top of the
	 *            reference view.
	 * 
	 * @return The selected view, or null if the selected view is outside the
	 *         visible area.
	 */
	private View fillSpecific(int position, int left) {
		boolean tempIsSelected = position == mSelectedPosition;
		View temp = makeAndAddView(position, left, true, mListPadding.top, tempIsSelected);
		String item = mAdapter.getItem(position).toString();
		mItemLeft = item;
		mItemRight = item;
		// if (position != 0) {
		int tempPosition = position;
		String tempItem = item;
		tempPosition--;
		while (tempItem.equals(mAdapter.getItem(tempPosition).toString()) && tempPosition >= 0) {
			tempPosition--;
		}

		tempPosition++;
		if (tempPosition >= getFirstVisiblePosition() && tempPosition <= getLastVisiblePosition()) {
			list.add(new PositionInfo(tempPosition, item));
		}
		// }
		// Possibly changed again in fillLeft if we add rows above this one.
		mFirstPosition = position;

		View leftTowards;
		View rightTowards;

		final int dividerWidth = mDividerWidth;
		if (!mStackFromBottom) {
			leftTowards = fillLeft(position - 1, temp.getLeft() - dividerWidth - mSpacing);
			// This will correct for the top of the first view not touching the
			// top of the list
			adjustViewsLeftOrRight();
			rightTowards = fillRight(position + 1, temp.getRight() + dividerWidth + mSpacing);
			int childCount = getChildCount();
			if (childCount > 0) {
				correctTooWide(childCount);
			}
		} else {
			rightTowards = fillRight(position + 1, temp.getRight() + dividerWidth);
			// This will correct for the bottom of the last view not touching
			// the bottom of the list
			adjustViewsLeftOrRight();
			leftTowards = fillLeft(position - 1, temp.getLeft() - dividerWidth);
			int childCount = getChildCount();
			if (childCount > 0) {
				correctTooNarrow(childCount);
			}
		}

		if (tempIsSelected) {
			return temp;
		} else if (leftTowards != null) {
			return leftTowards;
		} else {
			return rightTowards;
		}
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
	private View fillLeft(int pos, int nextRight) {
		View selectedView = null;

		int end = 0;
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end = mListPadding.top;
		}

		while (nextRight > end && pos >= 0) {
			// is this the selected item?
			boolean selected = pos == mSelectedPosition;

			// change start
			String item = mAdapter.getItem(pos).toString();
			if (!mItemLeft.equals(item)) {
				nextRight -= mIntevel;
				if (nextRight <= end) {
					break;
				}
				list.add(0, new PositionInfo(pos + 1, mAdapter.getItem(pos + 1).toString()));
				mItemLeft = item.toString();
			}

			if (pos == 0) {
				list.add(0, new PositionInfo(pos, mAdapter.getItem(pos).toString()));
			}

			// nextRight -= checkItemLeftIntevel(pos, true);
			// change end
			View child = makeAndAddView(pos, nextRight, false, mListPadding.top, selected);
			nextRight = child.getLeft() - mDividerWidth - mSpacing;
			if (selected) {
				selectedView = child;
			}
			pos--;
		}

		mFirstPosition = pos + 1;
		// setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
		// - 1);
		return selectedView;
	}

	/**
	 * Make sure views are touching the top or bottom edge, as appropriate for
	 * our gravity
	 */
	private void adjustViewsLeftOrRight() {
		final int childCount = getChildCount();
		int delta;

		if (childCount > 0) {
			View child;

			if (!mStackFromBottom) {
				// Uh-oh -- we came up short. Slide all views up to make them
				// align with the top
				child = getChildAt(0);
				delta = child.getLeft() - mListPadding.left;
				if (mFirstPosition != 0) {
					// It's OK to have some space above the first item if it is
					// part of the vertical spacing
					delta -= mDividerWidth;
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
					delta += mDividerWidth;
				}

				if (delta > 0) {
					delta = 0;
				}
			}

			if (delta != 0) {
				offsetChildrenLeftAndRight(-delta);
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
	private View fillFromLeft(int nextLeft) {
		mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}

		mItemLeft = mAdapter.getItem(mFirstPosition).toString();
		return fillRight(mFirstPosition, nextLeft);
	}

	/**
	 * Fills the list based on positioning the new selection relative to the old
	 * selection. The new selection will be placed at, above, or below the
	 * location of the new selection depending on how the selection is moving.
	 * The selection will then be pinned to the visible part of the screen,
	 * excluding the edges that are faded. The list is then filled upwards and
	 * downwards from there.
	 * 
	 * @param oldSel
	 *            The old selected view. Useful for trying to put the new
	 *            selection in the same place
	 * @param newSel
	 *            The view that is to become selected. Useful for trying to put
	 *            the new selection in the same place
	 * @param delta
	 *            Which way we are moving
	 * @param childrenTop
	 *            Where to start drawing children
	 * @param childrenBottom
	 *            Last pixel where children can be drawn
	 * @return The view that currently has selection
	 */
	private View moveSelection(View oldSel, View newSel, int delta, int childrenLeft, int childrenRight) {
		int fadingEdgeLength = getVerticalFadingEdgeLength();
		final int selectedPosition = mSelectedPosition;

		View sel;

		final int leftSelectionPixel = getLeftSelectionPixel(childrenLeft, fadingEdgeLength, selectedPosition);
		final int rightSelectionPixel = getRightSelectionPixel(childrenLeft, fadingEdgeLength, selectedPosition);

		if (delta > 0) {
			/*
			 * Case 1: Scrolling down.
			 */

			/*
			 * Before After | | | | +-------+ +-------+ | A | | A | | 1 | =>
			 * +-------+ +-------+ | B | | B | | 2 | +-------+ +-------+ | | | |
			 * 
			 * Try to keep the top of the previously selected item where it was.
			 * oldSel = A sel = B
			 */

			// Put oldSel (A) where it belongs
			oldSel = makeAndAddView(selectedPosition - 1, oldSel.getLeft(), true, mListPadding.top, false);

			final int dividerWidth = mDividerWidth;

			// Now put the new selection (B) below that
			sel = makeAndAddView(selectedPosition, oldSel.getRight() + dividerWidth, true, mListPadding.top, true);

			// Some of the newly selected item extends below the bottom of the
			// list
			if (sel.getRight() > rightSelectionPixel) {

				// Find space available above the selection into which we can
				// scroll upwards
				int spaceLeft = sel.getLeft() - leftSelectionPixel;

				// Find space required to bring the bottom of the selected item
				// fully into view
				int spaceRight = sel.getRight() - rightSelectionPixel;

				// Don't scroll more than half the height of the list
				int halfHorizontalSpace = (childrenRight - childrenLeft) / 2;
				int offset = Math.min(spaceLeft, spaceRight);
				offset = Math.min(offset, halfHorizontalSpace);

				// We placed oldSel, so offset that item
				oldSel.offsetLeftAndRight(-offset);
				// Now offset the selected item to get it into view
				sel.offsetLeftAndRight(-offset);
			}

			// Fill in views above and below
			if (!mStackFromBottom) {
				fillLeft(mSelectedPosition - 2, sel.getLeft() - dividerWidth);
				adjustViewsLeftOrRight();
				fillRight(mSelectedPosition + 1, sel.getRight() + dividerWidth);
			} else {
				fillRight(mSelectedPosition + 1, sel.getRight() + dividerWidth);
				adjustViewsLeftOrRight();
				fillLeft(mSelectedPosition - 2, sel.getLeft() - dividerWidth);
			}
		} else if (delta < 0) {
			/*
			 * Case 2: Scrolling up.
			 */

			/*
			 * Before After | | | | +-------+ +-------+ | A | | A | +-------+ =>
			 * | 1 | | B | +-------+ | 2 | | B | +-------+ +-------+ | | | |
			 * 
			 * Try to keep the top of the item about to become selected where it
			 * was. newSel = A olSel = B
			 */

			if (newSel != null) {
				// Try to position the top of newSel (A) where it was before it
				// was selected
				sel = makeAndAddView(selectedPosition, newSel.getLeft(), true, mListPadding.top, true);
			} else {
				// If (A) was not on screen and so did not have a view, position
				// it above the oldSel (B)
				sel = makeAndAddView(selectedPosition, oldSel.getLeft(), false, mListPadding.top, true);
			}

			// Some of the newly selected item extends above the top of the list
			if (sel.getLeft() < leftSelectionPixel) {
				// Find space required to bring the top of the selected item
				// fully into view
				int spaceLeft = leftSelectionPixel - sel.getLeft();

				// Find space available below the selection into which we can
				// scroll downwards
				int spaceRight = rightSelectionPixel - sel.getRight();

				// Don't scroll more than half the height of the list
				int halfHorizontalSpace = (childrenRight - childrenLeft) / 2;
				int offset = Math.min(spaceLeft, spaceRight);
				offset = Math.min(offset, halfHorizontalSpace);

				// Offset the selected item to get it into view
				sel.offsetLeftAndRight(offset);
			}

			// Fill in views above and below
			fillLeftAndRight(sel, selectedPosition);
		} else {

			int oldLeft = oldSel.getLeft();

			/*
			 * Case 3: Staying still
			 */
			sel = makeAndAddView(selectedPosition, oldLeft, true, mListPadding.top, true);

			// We're staying still...
			if (oldLeft < childrenLeft) {
				// ... but the top of the old selection was off screen.
				// (This can happen if the data changes size out from under us)
				int newRight = sel.getRight();
				if (newRight < childrenLeft + 20) {
					// Not enough visible -- bring it onscreen
					sel.offsetLeftAndRight(childrenLeft - sel.getLeft());
				}
			}

			// Fill in views above and below
			fillLeftAndRight(sel, selectedPosition);
		}

		return sel;
	}

	/**
	 * Calculate the top-most pixel we can draw the selection into
	 * 
	 * @param childrenTop
	 *            Top pixel were children can be drawn
	 * @param fadingEdgeLength
	 *            Length of the fading edge in pixels, if present
	 * @param selectedPosition
	 *            The position that will be selected
	 * @return The top-most pixel we can draw the selection into
	 */
	private int getLeftSelectionPixel(int childrenLeft, int fadingEdgeLength, int selectedPosition) {
		// first pixel we can draw the selection into
		int leftSelectionPixel = childrenLeft;
		if (selectedPosition > 0) {
			leftSelectionPixel += fadingEdgeLength;
		}
		return leftSelectionPixel;
	}

	/**
	 * Calculate the bottom-most pixel we can draw the selection into
	 * 
	 * @param childrenBottom
	 *            Bottom pixel were children can be drawn
	 * @param fadingEdgeLength
	 *            Length of the fading edge in pixels, if present
	 * @param selectedPosition
	 *            The position that will be selected
	 * @return The bottom-most pixel we can draw the selection into
	 */
	private int getRightSelectionPixel(int childrenRight, int fadingEdgeLength, int selectedPosition) {
		int rightSelectionPixel = childrenRight;
		if (selectedPosition != mItemCount - 1) {
			rightSelectionPixel -= fadingEdgeLength;
		}
		return rightSelectionPixel;
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

		while (nextLeft < end && pos < mItemCount) {
			// is this the selected item?
			boolean selected = pos == mSelectedPosition;

			// change start
			String item = mAdapter.getItem(pos).toString();
			if (!mItemRight.equals(item)) {
				if (pos != 0) {
					nextLeft += mIntevel;
				}
				if (nextLeft >= end) {
					break;
				}
				list.add(new PositionInfo(pos, item));
				mItemRight = item.toString();
			}

			// nextLeft += checkItemRightIntevel(pos, true);
			// change end
			View child = makeAndAddView(pos, nextLeft, true, mListPadding.top, selected);

			nextLeft = child.getRight() + mDividerWidth + mSpacing;
			if (selected) {
				selectedView = child;
			}
			pos++;
		}

		// setVisibleRangeHint(mFirstPosition, mFirstPosition + getChildCount()
		// - 1);
		return selectedView;
	}

	int checkItemRightIntevel(int position, boolean add) {
		String item = mAdapter.getItem(position).toString();
		int intevel = 0;
		if (!mItemRight.equals(item)) {
			if (add) {
				list.add(new PositionInfo(position, item));
			}
			mItemRight = item.toString();
			intevel = mIntevel;
		}

		if (position == 0) {
			intevel = 0;
		}

		return intevel;
	}

	int checkItemLeftIntevel(int position, boolean add) {
		String item = mAdapter.getItem(position).toString();
		int intevel = 0;
		if (!mItemLeft.equals(item)) {
			if (add) {
				list.add(0, new PositionInfo(position + 1, mAdapter.getItem(position + 1).toString()));
			}
			mItemLeft = item.toString();
			intevel = mIntevel;
		}

		if (position == 0) {
			if (add) {
				list.add(0, new PositionInfo(position, mAdapter.getItem(position).toString()));
			}
			intevel = 0;
		}

		return intevel;
	}

	/**
	 * Once the selected view as been placed, fill up the visible area above and
	 * below it.
	 * 
	 * @param sel
	 *            The selected view
	 * @param position
	 *            The position corresponding to sel
	 */
	private void fillLeftAndRight(View sel, int position) {
		final int dividerWidth = mDividerWidth;
		if (!mStackFromBottom) {
			fillLeft(position - 1, sel.getLeft() - dividerWidth);
			adjustViewsLeftOrRight();
			fillRight(position + 1, sel.getRight() + dividerWidth);
		} else {
			fillRight(position + 1, sel.getRight() + dividerWidth);
			adjustViewsLeftOrRight();
			fillLeft(position - 1, sel.getLeft() - dividerWidth);
		}
	}

	/**
	 * Check if we have dragged the bottom of the list too high (we have pushed
	 * the top element off the top of the screen when we did not need to).
	 * Correct by sliding everything back down.
	 * 
	 * @param childCount
	 *            Number of children
	 */
	private void correctTooWide(int childCount) {
		// First see if the last item is visible. If it is not, it is OK for the
		// top of the list to be pushed up.
		int lastPosition = mFirstPosition + childCount - 1;
		if (lastPosition == mItemCount - 1 && childCount > 0) {

			// Get the last child ...
			final View lastChild = getChildAt(childCount - 1);

			// ... and its bottom edge
			final int lastRight = lastChild.getRight();

			// This is bottom of our drawable area
			final int end = (getRight() - getLeft()) - mListPadding.right;

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
					fillLeft(mFirstPosition - 1, firstChild.getLeft() - mDividerWidth);
					// Close up the remaining gap
					adjustViewsLeftOrRight();
				}

			}
		}
	}

	/**
	 * Check if we have dragged the bottom of the list too low (we have pushed
	 * the bottom element off the bottom of the screen when we did not need to).
	 * Correct by sliding everything back up.
	 * 
	 * @param childCount
	 *            Number of children
	 */
	private void correctTooNarrow(int childCount) {
		// First see if the first item is visible. If it is not, it is OK for
		// the
		// bottom of the list to be pushed down.
		if (mFirstPosition == 0 && childCount > 0) {

			// Get the first child ...
			final View firstChild = getChildAt(0);

			// ... and its top edge
			final int firstLeft = firstChild.getLeft();

			// This is top of our drawable area
			final int start = mListPadding.left;

			// This is bottom of our drawable area
			final int end = (getRight() - getLeft()) - mListPadding.right;

			// This is how far the top edge of the first view is from the top of
			// the
			// drawable area
			int leftOffset = firstLeft - start;
			View lastChild = getChildAt(childCount - 1);
			final int lastRight = lastChild.getRight();
			int lastPosition = mFirstPosition + childCount - 1;

			// Make sure we are 1) Too low, and 2) Either there are more rows
			// below the
			// last row or the last row is scrolled off the bottom of the
			// drawable area
			if (leftOffset > 0) {
				if (lastPosition < mItemCount - 1 || lastRight > end) {
					if (lastPosition == mItemCount - 1) {
						// Don't pull the bottom too far up
						leftOffset = Math.min(leftOffset, lastRight - end);
					}
					// Move everything up
					offsetChildrenLeftAndRight(-leftOffset);
					if (lastPosition < mItemCount - 1) {
						// Fill the gap that was opened below the last position
						// with more rows, if
						// possible
						fillRight(lastPosition + 1, lastChild.getRight() + mDividerWidth);
						// Close up the remaining gap
						adjustViewsLeftOrRight();
					}
				} else if (lastPosition == mItemCount - 1) {
					adjustViewsLeftOrRight();
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Sets up mListPadding
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

		int childWidth = 0;
		int childHeight = 0;
		int childState = 0;

		mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
		if (mItemCount > 0 && (widthMode == View.MeasureSpec.UNSPECIFIED || heightMode == View.MeasureSpec.UNSPECIFIED)) {
			final View child = obtainView(0, mIsScrap);

			measureScrapChild(child, 0, heightMeasureSpec);

			childWidth = child.getMeasuredWidth();
			childHeight = child.getMeasuredHeight();
			childState = combineMeasuredStates(childState, child.getMeasuredState());

			if (recycleOnMeasure() && mRecycler.shouldRecycleViewType(((LayoutParams) child.getLayoutParams()).viewType)) {
				mRecycler.addScrapView(child, -1);
			}
		}

		if (heightMode == View.MeasureSpec.UNSPECIFIED) {
			heightSize = mListPadding.top + mListPadding.bottom + childHeight + getHorizontalFadingEdgeLength();
		} else {
			heightSize |= (childState & MEASURED_STATE_MASK);
		}

		if (widthMode == View.MeasureSpec.UNSPECIFIED) {
			widthSize = mListPadding.left + mListPadding.right + childWidth + getHorizontalFadingEdgeLength() * 2;
		}

		if (heightMode == View.MeasureSpec.AT_MOST) {
			// TODO: after first layout we should maybe start at the first
			// visible position, not 0
			heightSize = measureWidthOfChildren(heightMeasureSpec, 0, NO_POSITION, widthSize, -1);
		}

		setMeasuredDimension(widthSize, heightSize);
		mHeightMeasureSpec = heightMeasureSpec;
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
	 *            If flow is true, align top edge to y. If false, align bottom
	 *            edge to y.
	 * @param childrenLeft
	 *            Left edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @return View that was added
	 */
	private View makeAndAddView(int position, int x, boolean flow, int childrenLeft, boolean selected) {
		View child;

		if (!mDataChanged) {
			// Try to use an existing view for this position
			child = mRecycler.getActiveView(position);
			if (child != null) {
				// Found it -- we're using an existing child
				// This just needs to be positioned
				setupChild(child, position, x, flow, childrenLeft, selected, true);

				return child;
			}
		}

		// Make a new view for this position, or convert an unused view if
		// possible
		child = obtainView(position, mIsScrap);

		// This needs to be positioned and measured
		setupChild(child, position, x, flow, childrenLeft, selected, mIsScrap[0]);

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
	 * @param y
	 *            The y position relative to which this view will be positioned
	 * @param flowDown
	 *            If true, align top edge to y. If false, align bottom edge to
	 *            y.
	 * @param childrenLeft
	 *            Left edge where children should be positioned
	 * @param selected
	 *            Is this position selected?
	 * @param recycled
	 *            Has this view been pulled from the recycle bin? If so it does
	 *            not need to be remeasured.
	 */
	private void setupChild(View child, int position, int x, boolean flowLeft, int childrenTop, boolean selected, boolean recycled) {
		final boolean isSelected = selected && shouldShowSelector();
		final boolean updateChildSelected = isSelected != child.isSelected();
		final int mode = mTouchMode;
		final boolean isPressed = mode > TOUCH_MODE_DOWN && mode < TOUCH_MODE_SCROLL && mMotionPosition == position;
		final boolean updateChildPressed = isPressed != child.isPressed();
		final boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

		// Respect layout params that are already in the view. Otherwise make
		// some up...
		// noinspection unchecked
		AbsListView.LayoutParams p = (AbsListView.LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
		}
		p.viewType = mAdapter.getItemViewType(position);

		if ((recycled && !p.forceAdd) || (p.recycledHeaderFooter && p.viewType == ITEM_VIEW_TYPE_HEADER_OR_FOOTER)) {
			attachViewToParent(child, flowLeft ? -1 : 0, p);
		} else {
			p.forceAdd = false;
			if (p.viewType == ITEM_VIEW_TYPE_HEADER_OR_FOOTER) {
				p.recycledHeaderFooter = true;
			}
			addViewInLayout(child, flowLeft ? -1 : 0, p, true);
		}

		if (updateChildSelected) {
			child.setSelected(isSelected);
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
			int childHeightSpec = getChildMeasureSpec(mHeightMeasureSpec, mListPadding.top + mListPadding.bottom, p.width);
			int lpWidth = p.width;
			int childWidthSpec;
			if (lpWidth > 0) {
				childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, View.MeasureSpec.EXACTLY);
			} else {
				childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();
		final int childLeft = flowLeft ? x : x - w;

		if (needToMeasure) {
			final int childRight = childLeft + w;
			final int childBottom = childrenTop + h;
			child.layout(childLeft, childrenTop, childRight, childBottom);
		} else {
			child.offsetTopAndBottom(childrenTop - child.getTop());
			child.offsetLeftAndRight(childLeft - child.getLeft());
		}

		if (mCachingStarted && !child.isDrawingCacheEnabled()) {
			child.setDrawingCacheEnabled(true);
		}

		if (recycled && (((AbsListView.LayoutParams) child.getLayoutParams()).scrappedFromPosition) != position) {
			child.jumpDrawablesToCurrentState();
		}
	}

	private void measureScrapChild(View child, int position, int heightMeasureSpec) {
		LayoutParams p = (LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = (AbsListView.LayoutParams) generateDefaultLayoutParams();
			child.setLayoutParams(p);
		}
		p.viewType = mAdapter.getItemViewType(position);
		p.forceAdd = true;

		int childHeightSpec = getChildMeasureSpec(heightMeasureSpec, mListPadding.top + mListPadding.bottom, p.height);
		int lpWidth = p.width;
		int childWidthSpec;
		if (lpWidth > 0) {
			childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, View.MeasureSpec.EXACTLY);
		} else {
			childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * @return True to recycle the views used to measure this ListView in
	 *         UNSPECIFIED/AT_MOST modes, false otherwise.
	 */
	protected boolean recycleOnMeasure() {
		return true;
	}

	/**
	 * Measures the height of the given range of children (inclusive) and
	 * returns the height with this ListView's padding and divider heights
	 * included. If maxHeight is provided, the measuring will stop when the
	 * current height reaches maxHeight.
	 * 
	 * @param widthMeasureSpec
	 *            The width measure spec to be given to a child's
	 *            {@link View#measure(int, int)}.
	 * @param startPosition
	 *            The position of the first child to be shown.
	 * @param endPosition
	 *            The (inclusive) position of the last child to be shown.
	 *            Specify {@link #NO_POSITION} if the last child should be the
	 *            last available child from the adapter.
	 * @param maxHeight
	 *            The maximum height that will be returned (if all the children
	 *            don't fit in this value, this value will be returned).
	 * @param disallowPartialChildPosition
	 *            In general, whether the returned height should only contain
	 *            entire children. This is more powerful--it is the first
	 *            inclusive position at which partial children will not be
	 *            allowed. Example: it looks nice to have at least 3 completely
	 *            visible children, and in portrait this will most likely fit;
	 *            but in landscape there could be times when even 2 children can
	 *            not be completely shown, so a value of 2 (remember, inclusive)
	 *            would be good (assuming startPosition is 0).
	 * @return The height of this ListView with the given children.
	 */
	final int measureWidthOfChildren(int heightMeasureSpec, int startPosition, int endPosition, final int maxWidth, int disallowPartialChildPosition) {

		final ListAdapter adapter = mAdapter;
		if (adapter == null) {
			return mListPadding.left + mListPadding.right;
		}

		// Include the padding of the list
		int returnedWidth = mListPadding.left + mListPadding.right;
		final int dividerHeight = 0;// ((mDividerHeight > 0) && mDivider !=
									// null) ? mDividerHeight : 0;
		// The previous height value that was less than maxHeight and contained
		// no partial children
		int prevWidthWithoutPartialChild = 0;
		int i;
		View child;

		// mItemCount - 1 since endPosition parameter is inclusive
		endPosition = (endPosition == NO_POSITION) ? adapter.getCount() - 1 : endPosition;
		final AbsListView.RecycleBin recycleBin = mRecycler;
		final boolean recyle = recycleOnMeasure();
		final boolean[] isScrap = mIsScrap;

		for (i = startPosition; i <= endPosition; ++i) {
			child = obtainView(i, isScrap);

			measureScrapChild(child, i, heightMeasureSpec);

			if (i > 0) {
				// Count the divider for all but one child
				returnedWidth += dividerHeight;
			}

			// Recycle the view before we possibly return from the method
			if (recyle && recycleBin.shouldRecycleViewType(((LayoutParams) child.getLayoutParams()).viewType)) {
				recycleBin.addScrapView(child, -1);
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
						&& (returnedWidth != maxWidth) // i'th child did not
														// fit completely
				? prevWidthWithoutPartialChild : maxWidth;
			}

			if ((disallowPartialChildPosition >= 0) && (i >= disallowPartialChildPosition)) {
				prevWidthWithoutPartialChild = returnedWidth;
			}
		}

		// At this point, we went through the range of children, and they each
		// completely fit, so return the returnedHeight
		return returnedWidth;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Dispatch in the normal way
		boolean handled = super.dispatchKeyEvent(event);
		if (!handled) {
			// If we didn't handle it...
			View focused = getFocusedChild();
			if (focused != null && event.getAction() == KeyEvent.ACTION_DOWN) {
				// ... and our focused child didn't handle it
				// ... give it to ourselves so we can scroll if necessary
				handled = onKeyDown(event.getKeyCode(), event);
			}
		}
		return handled;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return commonKey(keyCode, 1, event);
	}

	private boolean commonKey(int keyCode, int count, KeyEvent event) {
		if (mAdapter == null || !mIsAttached) {
			return false;
		}

		if (mDataChanged) {
			layoutChildren();
		}

		boolean handled = false;
		int action = event.getAction();

		if (action != KeyEvent.ACTION_UP) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded();
					if (!handled) {
						while (count-- > 0) {
							if (arrowScroll(FOCUS_LEFT)) {
								checkItemRightIntevel(getLastVisiblePosition(), false);
								handled = true;
							} else {
								break;
							}
						}
					}
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_LEFT);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if (event.hasNoModifiers()) {
					handled = resurrectSelectionIfNeeded();
					if (!handled) {
						while (count-- > 0) {
							if (arrowScroll(FOCUS_RIGHT)) {
								checkItemLeftIntevel(getFirstVisiblePosition(), false);
								handled = true;
							} else {
								break;
							}
						}
					}
				} else if (event.hasModifiers(KeyEvent.META_ALT_ON)) {
					handled = resurrectSelectionIfNeeded() || fullScroll(FOCUS_RIGHT);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_UP:
				if (event.hasNoModifiers()) {
					handled = handleVerticalFocusWithinListItem(FOCUS_UP);
				}
				break;

			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (event.hasNoModifiers()) {
					handled = handleVerticalFocusWithinListItem(FOCUS_DOWN);
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
				// handled = true;
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
				// a ListView sequentially. Unfortunately this can create an
				// asymmetry in TAB navigation order unless the list selection
				// always reverts to the top or bottom when receiving TAB focus
				// from
				// another widget. Leaving this behavior disabled for now but
				// perhaps it should be configurable (and more comprehensive).
				if (false) {
					if (event.hasNoModifiers()) {
						handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_RIGHT);
					} else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
						handled = resurrectSelectionIfNeeded() || arrowScroll(FOCUS_LEFT);
					}
				}
				break;
			}
		}

		if (DEBUG) {
			Log.d(TAG, "commonKey: list = " + convertListToString());
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

		default: // shouldn't happen
			return false;
		}
	}

	/**
	 * Scrolls to the next or previous item if possible.
	 * 
	 * @param direction
	 *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
	 * 
	 * @return whether selection was moved
	 */
	boolean arrowScroll(int direction) {
		try {
			mInLayout = true;
			final boolean handled = arrowScrollImpl(direction);
			if (handled) {
				playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
			}
			return handled;
		} finally {
			mInLayout = false;
		}
	}

	/**
	 * Holds results of focus aware arrow scrolling.
	 */
	static private class ArrowScrollFocusResult {
		private int mSelectedPosition;
		private int mAmountToScroll;

		/**
		 * How {@link android.widget.ListView#arrowScrollFocused} returns its
		 * values.
		 */
		void populate(int selectedPosition, int amountToScroll) {
			mSelectedPosition = selectedPosition;
			mAmountToScroll = amountToScroll;
		}

		public int getSelectedPosition() {
			return mSelectedPosition;
		}

		public int getAmountToScroll() {
			return mAmountToScroll;
		}
	}

	/**
	 * Handle an arrow scroll going up or down. Take into account whether items
	 * are selectable, whether there are focusable items etc.
	 * 
	 * @param direction
	 *            Either {@link View#FOCUS_UP} or
	 *            {@link View#FOCUS_DOWN}.
	 * @return Whether any scrolling, selection or focus change occured.
	 */
	private boolean arrowScrollImpl(int direction) {
		if (getChildCount() <= 0) {
			return false;
		}

		View selectedView = getSelectedView();
		int selectedPos = mSelectedPosition;

		int nextSelectedPosition = lookForSelectablePositionOnScreen(direction);
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
			scrollListItemsBy((direction == FOCUS_LEFT) ? amountToScroll : -amountToScroll);
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
			mResurrectToPosition = INVALID_POSITION;
		}

		if (needToRedraw) {
			if (selectedView != null) {
				positionSelector(selectedPos, selectedView);
				// mSelectedTop = selectedView.getTop();
			}
			if (!awakenScrollBars()) {
				invalidate();
			}
			// invokeOnItemScrollListener();
			return true;
		}

		return false;
	}

	/**
	 * Scroll the children by amount, adding a view at the end and removing
	 * views that fall off as necessary.
	 *
	 * @param amount
	 *            The amount (positive or negative) to scroll.
	 */
	private void scrollListItemsBy(int amount) {
		offsetChildrenLeftAndRight(amount);

		final int listRight = getWidth() - mListPadding.right;
		final int listLeft = mListPadding.left;
		final AbsListView.RecycleBin recycleBin = mRecycler;

		if (amount < 0) {
			// shifted items up

			// may need to pan views into the bottom space
			int numChildren = getChildCount();
			View last = getChildAt(numChildren - 1);
			while (last.getRight() < listRight) {
				final int lastVisiblePosition = mFirstPosition + numChildren - 1;
				if (lastVisiblePosition < mItemCount - 1) {
					last = addViewRight(last, lastVisiblePosition);
					numChildren++;
				} else {
					break;
				}
			}

			// may have brought in the last child of the list that is skinnier
			// than the fading edge, thereby leaving space at the end. need
			// to shift back
			if (last.getRight() < listRight) {
				offsetChildrenLeftAndRight(listRight - last.getRight());
			}

			// top views may be panned off screen
			View first = getChildAt(0);
			while (first.getRight() < listLeft) {
				if (list.size() > 0 && (list.get(0).position() == getFirstVisiblePosition() || list.get(0).position() < getFirstVisiblePosition() || list.get(0).position() > getLastVisiblePosition())) {
					list.remove(0);
				}
				AbsListView.LayoutParams layoutParams = (LayoutParams) first.getLayoutParams();
				if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
					detachViewFromParent(first);
					recycleBin.addScrapView(first, mFirstPosition);
					// String item =
					// mAdapter.getItem(mFirstPosition).toString();

				} else {
					removeViewInLayout(first);
				}
				first = getChildAt(0);
				mFirstPosition++;
			}
		} else {
			// shifted items down
			View first = getChildAt(0);

			// may need to pan views into top
			while ((first.getLeft() > listLeft) && (mFirstPosition > 0)) {
				first = addViewLeft(first, mFirstPosition);
				mFirstPosition--;
			}

			// may have brought the very first child of the list in too far and
			// need to shift it back
			if (first.getLeft() > listLeft) {
				offsetChildrenLeftAndRight(listLeft - first.getLeft());
			}

			int lastIndex = getChildCount() - 1;
			View last = getChildAt(lastIndex);

			// bottom view may be panned off screen
			while (last.getLeft() > listRight) {
				if (list.size() > 0
						&& (list.get(list.size() - 1).position() == getLastVisiblePosition() || list.get(list.size() - 1).position() < getFirstVisiblePosition() || list.get(list.size() - 1)
								.position() > getLastVisiblePosition())) {
					list.remove(list.size() - 1);
				}
				AbsListView.LayoutParams layoutParams = (LayoutParams) last.getLayoutParams();
				if (recycleBin.shouldRecycleViewType(layoutParams.viewType)) {
					detachViewFromParent(last);
					recycleBin.addScrapView(last, mFirstPosition + lastIndex);
				} else {
					removeViewInLayout(last);
				}
				last = getChildAt(--lastIndex);
			}
		}
	}

	private View addViewLeft(View theView, int position) {
		int leftPosition = position - 1;
		View view = obtainView(leftPosition, mIsScrap);
		int edgeOfNewChild = theView.getLeft() - mDividerWidth - mSpacing;
		edgeOfNewChild -= checkItemLeftIntevel(leftPosition, true);
		setupChild(view, leftPosition, edgeOfNewChild, false, mListPadding.top, false, mIsScrap[0]);
		return view;
	}

	private View addViewRight(View theView, int position) {
		int rightPosition = position + 1;
		View view = obtainView(rightPosition, mIsScrap);
		int edgeOfNewChild = theView.getRight() + mDividerWidth + mSpacing;

		edgeOfNewChild += checkItemRightIntevel(rightPosition, true);
		setupChild(view, rightPosition, edgeOfNewChild, true, mListPadding.top, false, mIsScrap[0]);
		return view;
	}

	/**
	 * Determine the distance to the nearest edge of a view in a particular
	 * direction.
	 *
	 * @param descendant
	 *            A descendant of this list.
	 * @return The distance, or 0 if the nearest edge is already on screen.
	 */
	private int distanceToView(View descendant) {
		int distance = 0;
		descendant.getDrawingRect(mTempRect);
		offsetDescendantRectToMyCoords(descendant, mTempRect);
		final int listRight = getRight() - getLeft() - mListPadding.right;
		if (mTempRect.right < mListPadding.left) {
			distance = mListPadding.left - mTempRect.right;
		} else if (mTempRect.left > listRight) {
			distance = mTempRect.left - listRight;
		}
		return distance;
	}

	/**
	 * Return true if child is an ancestor of parent, (or equal to the parent).
	 */
	private boolean isViewAncestorOf(View child, View parent) {
		if (child == parent) {
			return true;
		}

		final ViewParent theParent = child.getParent();
		return (theParent instanceof ViewGroup) && isViewAncestorOf((View) theParent, parent);
	}

	/**
	 * When selection changes, it is possible that the previously selected or
	 * the next selected item will change its size. If so, we need to offset
	 * some folks, and re-layout the items as appropriate.
	 *
	 * @param selectedView
	 *            The currently selected view (before changing selection).
	 *            should be <code>null</code> if there was no previous
	 *            selection.
	 * @param direction
	 *            Either {@link View#FOCUS_UP} or
	 *            {@link View#FOCUS_DOWN}.
	 * @param newSelectedPosition
	 *            The position of the next selection.
	 * @param newFocusAssigned
	 *            whether new focus was assigned. This matters because when
	 *            something has focus, we don't want to show selection (ugh).
	 */
	private void handleNewSelectionChange(View selectedView, int direction, int newSelectedPosition, boolean newFocusAssigned) {
		if (newSelectedPosition == INVALID_POSITION) {
			throw new IllegalArgumentException("newSelectedPosition needs to be valid");
		}

		// whether or not we are moving down or up, we want to preserve the
		// top of whatever view is on top:
		// - moving down: the view that had selection
		// - moving up: the view that is getting selection
		View leftView;
		View rightView;
		int leftViewIndex, rightViewIndex;
		boolean leftSelected = false;
		final int selectedIndex = mSelectedPosition - mFirstPosition;
		final int nextSelectedIndex = newSelectedPosition - mFirstPosition;
		if (direction == FOCUS_LEFT) {
			leftViewIndex = nextSelectedIndex;
			rightViewIndex = selectedIndex;
			leftView = getChildAt(leftViewIndex);
			rightView = selectedView;
			leftSelected = true;
		} else {
			leftViewIndex = selectedIndex;
			rightViewIndex = nextSelectedIndex;
			leftView = selectedView;
			rightView = getChildAt(rightViewIndex);
		}

		final int numChildren = getChildCount();

		// start with top view: is it changing size?
		if (leftView != null) {
			leftView.setSelected(!newFocusAssigned && leftSelected);
			measureAndAdjustRight(leftView, leftViewIndex, numChildren);
		}

		// is the bottom view changing size?
		if (rightView != null) {
			rightView.setSelected(!newFocusAssigned && !leftSelected);
			measureAndAdjustRight(rightView, rightViewIndex, numChildren);
		}
	}

	/**
	 * Re-measure a child, and if its height changes, lay it out preserving its
	 * top, and adjust the children below it appropriately.
	 *
	 * @param child
	 *            The child
	 * @param childIndex
	 *            The view group index of the child.
	 * @param numChildren
	 *            The number of children in the view group.
	 */
	private void measureAndAdjustRight(View child, int childIndex, int numChildren) {
		int oldWidth = child.getWidth();
		measureItem(child);
		if (child.getMeasuredWidth() != oldWidth) {
			// lay out the view, preserving its top
			relayoutMeasuredItem(child);

			// adjust views below appropriately
			final int widthDelta = child.getMeasuredWidth() - oldWidth;
			for (int i = childIndex + 1; i < numChildren; i++) {
				getChildAt(i).offsetLeftAndRight(widthDelta);
			}
		}
	}

	/**
	 * Measure a particular list child. TODO: unify with setUpChild.
	 *
	 * @param child
	 *            The child.
	 */
	private void measureItem(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childHeightSpec = getChildMeasureSpec(mHeightMeasureSpec, mListPadding.top + mListPadding.bottom, p.height);
		int lpWidth = p.width;
		int childWidthSpec;
		if (lpWidth > 0) {
			childWidthSpec = View.MeasureSpec.makeMeasureSpec(lpWidth, View.MeasureSpec.EXACTLY);
		} else {
			childWidthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * Layout a child that has been measured, preserving its top position. TODO:
	 * unify with setUpChild.
	 *
	 * @param child
	 *            The child.
	 */
	private void relayoutMeasuredItem(View child) {
		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();
		final int childLeft = child.getLeft();
		final int childRight = childLeft + w;
		final int childTop = mListPadding.top;
		final int childBottom = childTop + h;
		child.layout(childLeft, childTop, childRight, childBottom);
	}

	/**
	 * Do an arrow scroll based on focus searching. If a new view is given
	 * focus, return the selection delta and amount to scroll via an
	 * {@link ArrowScrollFocusResult}, otherwise, return null.
	 *
	 * @param direction
	 *            either {@link View#FOCUS_UP} or
	 *            {@link View#FOCUS_DOWN}.
	 * @return The result if focus has changed, or <code>null</code>.
	 */
	private ArrowScrollFocusResult arrowScrollFocused(final int direction) {
		final View selectedView = getSelectedView();
		View newFocus;
		if (selectedView != null && selectedView.hasFocus()) {
			View oldFocus = selectedView.findFocus();
			newFocus = FocusFinder.getInstance().findNextFocus(this, oldFocus, direction);
		} else {
			if (direction == FOCUS_RIGHT) {
				final boolean leftFadingEdgeShowing = (mFirstPosition > 0);
				final int listLeft = mListPadding.left + (leftFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
				final int xSearchPoint = (selectedView != null && selectedView.getLeft() > listLeft) ? selectedView.getLeft() : listLeft;
				mTempRect.set(xSearchPoint, 0, xSearchPoint, 0);
			} else {
				final boolean rightFadingEdgeShowing = (mFirstPosition + getChildCount() - 1) < mItemCount;
				final int listRight = getWidth() - mListPadding.right - (rightFadingEdgeShowing ? getArrowScrollPreviewLength() : 0);
				final int xSearchPoint = (selectedView != null && selectedView.getRight() < listRight) ? selectedView.getRight() : listRight;
				mTempRect.set(xSearchPoint, 0, xSearchPoint, 0);
			}
			newFocus = FocusFinder.getInstance().findNextFocusFromRect(this, mTempRect, direction);
		}

		if (newFocus != null) {
			final int positionOfNewFocus = positionOfNewFocus(newFocus);

			// if the focus change is in a different new position, make sure
			// we aren't jumping over another selectable position
			if (mSelectedPosition != INVALID_POSITION && positionOfNewFocus != mSelectedPosition) {
				final int selectablePosition = lookForSelectablePositionOnScreen(direction);
				if (selectablePosition != INVALID_POSITION
						&& ((direction == FOCUS_RIGHT && selectablePosition < positionOfNewFocus) || (direction == FOCUS_LEFT && selectablePosition > positionOfNewFocus))) {
					return null;
				}
			}

			int focusScroll = amountToScrollToNewFocus(direction, newFocus, positionOfNewFocus);

			final int maxScrollAmount = getMaxScrollAmount();
			if (focusScroll < maxScrollAmount) {
				// not moving too far, safe to give next view focus
				newFocus.requestFocus(direction);
				mArrowScrollFocusResult.populate(positionOfNewFocus, focusScroll);
				return mArrowScrollFocusResult;
			} else if (distanceToView(newFocus) < maxScrollAmount) {
				// Case to consider:
				// too far to get entire next focusable on screen, but by going
				// max scroll amount, we are getting it at least partially in
				// view,
				// so give it focus and scroll the max ammount.
				newFocus.requestFocus(direction);
				mArrowScrollFocusResult.populate(positionOfNewFocus, maxScrollAmount);
				return mArrowScrollFocusResult;
			}
		}
		return null;
	}

	/**
	 * Determine how much we need to scroll in order to get newFocus in view.
	 *
	 * @param direction
	 *            either {@link View#FOCUS_UP} or
	 *            {@link View#FOCUS_DOWN}.
	 * @param newFocus
	 *            The view that would take focus.
	 * @param positionOfNewFocus
	 *            The position of the list item containing newFocus
	 * @return The amount to scroll. Note: this is always positive! Direction
	 *         needs to be taken into account when actually scrolling.
	 */
	private int amountToScrollToNewFocus(int direction, View newFocus, int positionOfNewFocus) {
		int amountToScroll = 0;
		newFocus.getDrawingRect(mTempRect);
		offsetDescendantRectToMyCoords(newFocus, mTempRect);
		if (direction == FOCUS_LEFT) {
			if (mTempRect.left < mListPadding.left) {
				amountToScroll = mListPadding.left - mTempRect.left;
				if (positionOfNewFocus > 0) {
					amountToScroll += getArrowScrollPreviewLength();
				}
			}
		} else {
			final int listRight = getWidth() - mListPadding.right;
			if (mTempRect.right > listRight) {
				amountToScroll = mTempRect.right - listRight;
				if (positionOfNewFocus < mItemCount - 1) {
					amountToScroll += getArrowScrollPreviewLength();
				}
			}
		}
		return amountToScroll;
	}

	/**
	 * @param newFocus
	 *            The view that would have focus.
	 * @return the position that contains newFocus
	 */
	private int positionOfNewFocus(View newFocus) {
		final int numChildren = getChildCount();
		for (int i = 0; i < numChildren; i++) {
			final View child = getChildAt(i);
			if (isViewAncestorOf(newFocus, child)) {
				return mFirstPosition + i;
			}
		}
		throw new IllegalArgumentException("newFocus is not a child of any of the" + " children of the list!");
	}

	/**
	 * @param direction
	 *            either {@link View#FOCUS_UP} or
	 *            {@link View#FOCUS_DOWN}.
	 * @return The position of the next selectable position of the views that
	 *         are currently visible, taking into account the fact that there
	 *         might be no selection. Returns {@link #INVALID_POSITION} if there
	 *         is no selectable view on screen in the given direction.
	 */
	private int lookForSelectablePositionOnScreen(int direction) {
		final int firstPosition = mFirstPosition;
		if (direction == FOCUS_RIGHT) {
			int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition + 1 : firstPosition;
			if (startPos >= mAdapter.getCount()) {
				return INVALID_POSITION;
			}
			if (startPos < firstPosition) {
				startPos = firstPosition;
			}

			final int lastVisiblePos = getLastVisiblePosition();
			final ListAdapter adapter = getAdapter();
			for (int pos = startPos; pos <= lastVisiblePos; pos++) {
				if (adapter.isEnabled(pos) && getChildAt(pos - firstPosition).getVisibility() == VISIBLE) {
					return pos;
				}
			}
		} else {
			int last = firstPosition + getChildCount() - 1;
			int startPos = (mSelectedPosition != INVALID_POSITION) ? mSelectedPosition - 1 : firstPosition + getChildCount() - 1;
			if (startPos < 0 || startPos >= mAdapter.getCount()) {
				return INVALID_POSITION;
			}
			if (startPos > last) {
				startPos = last;
			}

			final ListAdapter adapter = getAdapter();
			for (int pos = startPos; pos >= firstPosition; pos--) {
				if (adapter.isEnabled(pos) && getChildAt(pos - firstPosition).getVisibility() == VISIBLE) {
					return pos;
				}
			}
		}
		return INVALID_POSITION;
	}

	/**
	 * Determine how much we need to scroll in order to get the next selected
	 * view visible, with a fading edge showing below as applicable. The amount
	 * is capped at {@link #getMaxScrollAmount()} .
	 *
	 * @param direction
	 *            either {@link View#FOCUS_UP} or
	 *            {@link View#FOCUS_DOWN}.
	 * @param nextSelectedPosition
	 *            The position of the next selection, or
	 *            {@link #INVALID_POSITION} if there is no next selectable
	 *            position
	 * @return The amount to scroll. Note: this is always positive! Direction
	 *         needs to be taken into account when actually scrolling.
	 */
	private int amountToScroll(int direction, int nextSelectedPosition) {
		final int listRight = getWidth() - mListPadding.right;
		final int listLeft = mListPadding.left;

		final int numChildren = getChildCount();

		if (direction == FOCUS_RIGHT) {
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

			if (viewToMakeVisible.getRight() <= goalRight) {
				// item is fully visible.
				return 0;
			}

			if (nextSelectedPosition != INVALID_POSITION && (goalRight - viewToMakeVisible.getLeft()) >= getMaxScrollAmount()) {
				// item already has enough of it visible, changing selection is
				// good enough
				return 0;
			}

			int amountToScroll = (viewToMakeVisible.getRight() - goalRight);

			if ((mFirstPosition + numChildren) == mItemCount) {
				// last is last in list -> make sure we don't scroll past it
				final int max = getChildAt(numChildren - 1).getRight() - listRight;
				amountToScroll = Math.min(amountToScroll, max);
			}

			return Math.min(amountToScroll, getMaxScrollAmount());
		} else {
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
			if (viewToMakeVisible.getLeft() >= goalLeft) {
				// item is fully visible.
				return 0;
			}

			if (nextSelectedPosition != INVALID_POSITION && (viewToMakeVisible.getRight() - goalLeft) >= getMaxScrollAmount()) {
				// item already has enough of it visible, changing selection is
				// good enough
				return 0;
			}

			int amountToScroll = (goalLeft - viewToMakeVisible.getLeft());
			if (mFirstPosition == 0) {
				// first is first in list -> make sure we don't scroll past it
				final int max = listLeft - getChildAt(0).getLeft();
				amountToScroll = Math.min(amountToScroll, max);
			}
			return Math.min(amountToScroll, getMaxScrollAmount());
		}
	}

	/**
	 * @return The amount to preview next items when arrow srolling.
	 */
	private int getArrowScrollPreviewLength() {
		return Math.max(MIN_SCROLL_PREVIEW_PIXELS, getHorizontalFadingEdgeLength());
	}

	/**
	 * @return The maximum amount a list view will scroll in response to an
	 *         arrow event.
	 */
	public int getMaxScrollAmount() {
		return (int) (MAX_SCROLL_FACTOR * (getRight() - getLeft()));
	}

	/**
	 * Go to the last or first item if possible (not worrying about panning
	 * across or navigating within the internal focus of the currently selected
	 * item.)
	 * 
	 * @param direction
	 *            either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
	 * 
	 * @return whether selection was moved
	 */
	boolean fullScroll(int direction) {
		boolean moved = false;
		if (direction == FOCUS_LEFT) {
			if (mSelectedPosition != 0) {
				int position = lookForSelectablePosition(0, true);
				if (position >= 0) {
					mLayoutMode = LAYOUT_FORCE_LEFT;
					setSelectionInt(position);
					// TODO
					// invokeOnItemScrollListener();
				}
				moved = true;
			}
		} else if (direction == FOCUS_RIGHT) {
			if (mSelectedPosition < mItemCount - 1) {
				int position = lookForSelectablePosition(mItemCount - 1, true);
				if (position >= 0) {
					mLayoutMode = LAYOUT_FORCE_RIGHT;
					setSelectionInt(position);
					// TODO
					// invokeOnItemScrollListener();
				}
				moved = true;
			}
		}

		if (moved && !awakenScrollBars()) {
			awakenScrollBars();
			invalidate();
		}

		return moved;
	}

	/**
	 * To avoid horizontal focus searches changing the selected item, we
	 * manually focus search within the selected item (as applicable), and
	 * prevent focus from jumping to something within another item.
	 * 
	 * @param direction
	 *            one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}
	 * @return Whether this consumes the key event.
	 */
	private boolean handleVerticalFocusWithinListItem(int direction) {
		if (direction != FOCUS_UP && direction != FOCUS_DOWN) {
			throw new IllegalArgumentException("direction must be one of" + " {View.FOCUS_UP, View.FOCUS_DOWN}");
		}

		final int numChildren = getChildCount();
		if (mItemsCanFocus && numChildren > 0 && mSelectedPosition != INVALID_POSITION) {
			final View selectedView = getSelectedView();
			if (selectedView != null && selectedView.hasFocus() && selectedView instanceof ViewGroup) {

				final View currentFocus = selectedView.findFocus();
				final View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) selectedView, currentFocus, direction);
				if (nextFocus != null) {
					// do the math to get interesting rect in next focus'
					// coordinates
					currentFocus.getFocusedRect(mTempRect);
					offsetDescendantRectToMyCoords(currentFocus, mTempRect);
					offsetRectIntoDescendantCoords(nextFocus, mTempRect);
					if (nextFocus.requestFocus(direction, mTempRect)) {
						return true;
					}
				}
				// we are blocking the key from being handled (by returning
				// true)
				// if the global result is going to be some other view within
				// this
				// list. this is to acheive the overall goal of having
				// horizontal d-pad navigation remain in the current item.
				final View globalNextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) getRootView(), currentFocus, direction);
				if (globalNextFocus != null) {
					return isViewAncestorOf(globalNextFocus, this);
				}
			}
		}
		return false;
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
		boolean down = false;

		if (direction == FOCUS_LEFT) {
			nextPage = Math.max(0, mSelectedPosition - getChildCount() - 1);
		} else if (direction == FOCUS_RIGHT) {
			nextPage = Math.min(mItemCount - 1, mSelectedPosition + getChildCount() - 1);
			down = true;
		}

		if (nextPage >= 0) {
			int position = lookForSelectablePosition(nextPage, down);
			if (position >= 0) {
				mLayoutMode = LAYOUT_SPECIFIC;
				mSpecificLeft = getPaddingLeft() + getHorizontalFadingEdgeLength();

				if (down && position > mItemCount - getChildCount()) {
					mLayoutMode = LAYOUT_FORCE_RIGHT;
				}

				if (!down && position < getChildCount()) {
					mLayoutMode = LAYOUT_FORCE_LEFT;
				}

				setSelectionInt(position);
				// TODO
				// invokeOnItemScrollListener();
				if (!awakenScrollBars()) {
					invalidate();
				}

				return true;
			}
		}

		return false;
	}

	@Override
	void rememberSyncState() {
		if (getChildCount() > 0) {
			mNeedSync = true;
			mSyncHeight = mLayoutHeight;
			if (mSelectedPosition >= 0) {
				// Sync the selection state
				View v = getChildAt(mSelectedPosition - mFirstPosition);
				mSyncRowId = mNextSelectedRowId;
				mSyncPosition = mNextSelectedPosition;
				if (v != null) {
					mSpecificLeft = v.getLeft();
				}
				mSyncMode = SYNC_SELECTED_POSITION;
			} else {
				// Sync the based on the offset of the first view
				View v = getChildAt(0);
				ListAdapter adapter = getAdapter();
				if (mFirstPosition >= 0 && mFirstPosition < adapter.getCount()) {
					mSyncRowId = adapter.getItemId(mFirstPosition);
				} else {
					mSyncRowId = NO_ID;
				}
				mSyncPosition = mFirstPosition;
				if (v != null) {
					mSpecificLeft = v.getLeft();
				}
				mSyncMode = SYNC_FIRST_POSITION;
			}
		}
	}

	/**
	 * Sets the currently selected item. If in touch mode, the item will not be
	 * selected but it will still be positioned appropriately. If the specified
	 * selection position is less than 0, then the item at position 0 will be
	 * selected.
	 * 
	 * @param position
	 *            Index (starting at 0) of the data item to be selected.
	 */
	@Override
	public void setSelection(int position) {
		setSelectionFromLeft(position, 0);
	}

	/**
	 * Sets the selected item and positions the selection y pixels from the top
	 * edge of the ListView. (If in touch mode, the item will not be selected
	 * but it will still be positioned appropriately.)
	 * 
	 * @param position
	 *            Index (starting at 0) of the data item to be selected.
	 * @param y
	 *            The distance from the top edge of the ListView (plus padding)
	 *            that the item will be positioned.
	 */
	public void setSelectionFromLeft(int position, int x) {
		if (mAdapter == null) {
			return;
		}

		if (!isInTouchMode()) {
			position = lookForSelectablePosition(position, true);
			if (position >= 0) {
				setNextSelectedPositionInt(position);
			}
		} else {
			mResurrectToPosition = position;
		}

		if (position >= 0) {
			mLayoutMode = LAYOUT_SPECIFIC;
			mSpecificLeft = mListPadding.left + x;

			if (mNeedSync) {
				mSyncPosition = position;
				mSyncRowId = mAdapter.getItemId(position);
			}

			// if (mPositionScroller != null) {
			// mPositionScroller.stop();
			// }
			// requestLayout();
			layoutChildren();
		}
	}

	/**
	 * Find a position that can be selected (i.e., is not a separator).
	 * 
	 * @param position
	 *            The starting position to look at.
	 * @param lookDown
	 *            Whether to look down for other positions.
	 * @return The next selectable position starting at position and then
	 *         searching either up or down. Returns {@link #INVALID_POSITION} if
	 *         nothing can be found.
	 */
	@Override
	int lookForSelectablePosition(int position, boolean lookRight) {
		final ListAdapter adapter = mAdapter;
		if (adapter == null || isInTouchMode()) {
			return INVALID_POSITION;
		}

		final int count = adapter.getCount();
		if (!mAreAllItemsSelectable) {
			if (lookRight) {
				position = Math.max(0, position);
				while (position < count && !adapter.isEnabled(position)) {
					position++;
				}
			} else {
				position = Math.min(position, count - 1);
				while (position >= 0 && !adapter.isEnabled(position)) {
					position--;
				}
			}

			if (position < 0 || position >= count) {
				return INVALID_POSITION;
			}
			return position;
		} else {
			if (position < 0 || position >= count) {
				return INVALID_POSITION;
			}
			return position;
		}
	}

	@Override
	int findMotionRow(int x) {
		int childCount = getChildCount();
		if (childCount > 0) {
			if (!mStackFromBottom) {
				for (int i = 0; i < childCount; i++) {
					View v = getChildAt(i);
					if (x <= v.getRight()) {
						return mFirstPosition + i;
					}
				}
			} else {
				for (int i = childCount - 1; i >= 0; i--) {
					View v = getChildAt(i);
					if (x >= v.getLeft()) {
						return mFirstPosition + i;
					}
				}
			}
		}
		return INVALID_POSITION;
	}

	protected class PositionInfo {
		int position;
		String item;

		public PositionInfo(int p, String i) {
			position = p;
			item = i;
		}

		public int position() {
			return position;
		}

		public String item() {
			return item;
		}

		public String toString() {
			return "[position = " + position + ", item = " + item + "]";
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (o == this) {
				return true;
			}
			if (o instanceof PositionInfo) {
				PositionInfo other = (PositionInfo) o;
				if (item == null) {
					return position == other.position && other.item == null;
				} else {
					return position == other.position && item.equals(other.item);
				}
			}
			return false;
		}
	}
}

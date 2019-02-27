package com.yunos.tvtaobao.biz.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;

/**
 * 进入到每个item内部的Focus
 * Focus框不会发生变化只是对内部子View的selected状态做出改变
 *
 * @author tingmeng.ytm
 */
public class InnerFocusGroupListView extends FocusListView {
    //默认页面进入时聚焦
    private boolean mItemInnerFocusState = true;
    private OnKeyListener mOnKeyListener;

    public InnerFocusGroupListView(Context context) {
        super(context);
    }

    public InnerFocusGroupListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerFocusGroupListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void clearInnerFocusState() {
        mItemInnerFocusState = false;
    }

    public void resetInnerFocusState(){
        mItemInnerFocusState = true;
    }

    @Override
    protected void performSelect(boolean select) {
        if (!select) {
            View selectedView = getSelectedView();
            // 清除内部相关内容
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout) selectedView;
                focusView.clearItemSelected();
            }
            mItemInnerFocusState = false;
        }
        super.performSelect(select);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout) selectedView;
                return focusView.onKeyUp(keyCode, event);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public int getDescendantFocusability() {
        return ViewGroup.FOCUS_BLOCK_DESCENDANTS;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getChildCount() <= 0) {
            return super.onKeyDown(keyCode, event);
        }

//        mOnKeyListener.onKeyEvent(keyCode);
        if (checkState(keyCode)) {
            return true;
        }
        // 在item的内部处理
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout) selectedView;
                if (focusView.onKeyDown(keyCode, event)) {
                    return true;
                }
            } else {
                mItemInnerFocusState = false;
            }
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && getNextSelectedPosition(FOCUS_DOWN) == INVALID_POSITION) {
            if (mOnKeyListener != null && mLastScrollState != OnScrollListener.SCROLL_STATE_FLING)
                mOnKeyListener.onExceedingBottom();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface OnKeyListener {

        void onExceedingBottom();
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    public void setOnKeyListener(OnKeyListener onItemListener) {
        mOnKeyListener = onItemListener;
    }

    protected int amountToCenterScroll(int direction, int nextSelectedPosition) {
        int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
        final int listBottom = getHeight() - mListPadding.bottom;
        final int listTop = mListPadding.top;
        final int numChildren = getVisibleChildCount();
        int amountToScroll = 0;
//        View selectedView = getSelectedView();
//        if (selectedView == null) {
//            selectedView = getFirstVisibleChild();
//        }
//        int distanceToCenter = getHeight() / 2 - selectedView.getHeight() / 2 - selectedView.getTop() / 2;
        int distanceLeft = getLeftScrollDistance();
//        int distance = getItemDistance(getSelectedItemPosition(), nextSelectedPosition, direction);
//        int finalNextSelectedCenter = distance - distanceLeft + distanceToCenter;
        if (direction == FOCUS_DOWN) {

            View nextSelctedView = getChildAt(nextSelectedPosition - getFirstPosition());
            int nextSelectedCenter = 0;

            boolean reset = false;
            if (DEBUG) Log.d(TAG, "down -> nextSelectedView: " + nextSelctedView);
            if (nextSelctedView == null) {
                nextSelctedView = getLastChild();

                nextSelectedCenter = nextSelctedView.getBottom();
                if (DEBUG)
                    Log.d(TAG, "down -> nextSelectedCenter:" + nextSelectedCenter + ",center:" + center + ",spacing:" + mSpacing);
//                nextSelectedCenter += (nextSelctedView.getHeight() + mSpacing)//todo
//                        * (nextSelectedPosition - getLastPosition());
                nextSelectedCenter += getItemDistance(getLastPosition(), nextSelectedPosition, FOCUS_DOWN);
                if (DEBUG)
                    Log.d(TAG, "down -> nextSelectedCenter:" + nextSelectedCenter + ",center:" + center + ",spacing:" + mSpacing);
//                Log.d(TAG, "down next distance:" + distance);
                reset = false;
            } else {
                nextSelectedCenter = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                reset = true;
            }

            int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;
            if (finalNextSelectedCenter > center) {
                amountToScroll = finalNextSelectedCenter - center;

                int maxDiff = (nextSelctedView.getHeight() + mSpacing) * (mItemCount - getLastVisiblePosition() - 1);
                maxDiff -= distanceLeft;
                View lastVisibleView = getLastVisibleChild();
                if (lastVisibleView.getBottom() > getHeight() - mListPadding.bottom) {
                    maxDiff += (lastVisibleView.getBottom() - (getHeight() - mListPadding.bottom));
                }

                if (amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if (reset) {
                    reset();
                    offsetFocusRect(0, -distanceLeft);
                    if (DEBUG) {
                        Log.i(TAG, "amountToCenterScroll: focus rect = " + mFocusRectparams.focusRect()
                                + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = "
                                + nextSelectedPosition);
                    }
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        offsetFocusRect(0, -amountToScroll);
                    } else {
                        offsetFocusRect(0, (nextSelctedView.getHeight() + mSpacing - amountToScroll));
                    }

                    if (DEBUG) {
                        Log.d(TAG, "mListLoopScroller amountToCenterScroll: focus down amountToScroll = " + amountToScroll
                                + ", focus rect = " + mFocusRectparams.focusRect());
                    }
                    smoothScrollBy(amountToScroll);
                    mIsAnimate = true;
                } else {
                    if (!reset) {
                        offsetFocusRect(0, nextSelctedView.getHeight() + mSpacing);
                    }
                    mIsAnimate = true;
                }
            } else {
                reset();
                offsetFocusRect(0, -distanceLeft);
                mIsAnimate = true;
            }
            return amountToScroll;
        } else if (direction == FOCUS_UP)

        {
            View nextSelctedView = getChildAt(nextSelectedPosition - getFirstPosition());
            int nextSelectedCenter = 0;
            boolean reset = false;
            if (DEBUG) Log.d(TAG, "up -> nextSelectedView: " + nextSelctedView);
            if (nextSelctedView == null) {
                nextSelctedView = getFirstVisibleChild();
                nextSelectedCenter = nextSelctedView.getTop();

                int distance = getItemDistance(getFirstVisiblePosition(), nextSelectedPosition, FOCUS_UP);
                Log.d(TAG, "up -> nextSelectedCenter:" + nextSelectedCenter + ",center:" + center + ",distance:" + distance);
                if (nextSelectedPosition >= getHeaderViewsCount()) {
//                    nextSelectedCenter -= (nextSelctedView.getHeight() + mSpacing)
//                            * (getFirstVisiblePosition() - nextSelectedPosition);
                    nextSelectedCenter += distance;
                    if (DEBUG)
                        Log.d(TAG, "up -> nextSelectedCenter, getHeaderViewsCount:" + getHeaderViewsCount() + ",nextSelectedPosition:" + nextSelectedPosition);
                } else {
                    if (DEBUG)
                        Log.d(TAG, "up -> nextSelectedCenter:" + nextSelectedCenter + ",center:" + center);
                    nextSelectedCenter += getItemDistance(getFirstVisiblePosition(), getHeaderViewsCount(), FOCUS_UP);
                    for (int i = getHeaderViewsCount() - 1; i >= nextSelectedPosition; i--) {
                        nextSelectedCenter -= getHeaderView(i).getHeight();
                    }
                }
                if (DEBUG)
                    Log.d(TAG, "up next distance:" + distance);
                reset = false;
            } else {
                nextSelectedCenter = (nextSelctedView.getTop() + nextSelctedView.getBottom()) / 2;
                reset = true;
            }

            int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

            if (finalNextSelectedCenter < center) {
                amountToScroll = center - finalNextSelectedCenter;
                int maxDiff = 0;
                int start = getHeaderViewsCount() - 1;
                if (getFirstVisiblePosition() >= getHeaderViewsCount()) {
                    maxDiff = (nextSelctedView.getHeight() + mSpacing)
                            * (getFirstVisiblePosition() - getHeaderViewsCount());
                } else {
                    start = getFirstVisiblePosition() - 1;
                }
                for (int i = start; i >= 0; i--) {
                    maxDiff += getHeaderView(i).getHeight();
                }
                if (maxDiff < 0) {
                    maxDiff = 0;
                }

                maxDiff += distanceLeft;
                View firstVisibleView = getFirstVisibleChild();
                if (firstVisibleView.getTop() < listTop) {
                    int firstOffset = getFirsVisibletChildIndex() - nextSelectedPosition;
                    if (firstOffset > 0) {
                        maxDiff += (listTop - firstVisibleView.getTop()) - firstOffset * listTop;
                    } else {
                        maxDiff += (listTop - firstVisibleView.getTop());
                    }
                }

                if (amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if (reset) {
                    reset();
                    offsetFocusRect(0, -distanceLeft);
                    if (DEBUG) {
                        Log.i(TAG, "amountToCenterScroll: focus rect = " + mFocusRectparams.focusRect()
                                + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = "
                                + nextSelectedPosition);
                    }
                } else if (nextSelectedPosition < getHeaderViewsCount()) {
                    reset = true;
                    resetHeader(nextSelectedPosition);
                    offsetFocusRect(0, -distanceLeft);
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        offsetFocusRect(0, amountToScroll);
                    } else {
                        offsetFocusRect(0, -(nextSelctedView.getHeight() + mSpacing - amountToScroll));
                    }

                    if (DEBUG) {
                        Log.d(TAG, "amountToCenterScroll: focus down amountToScroll = " + amountToScroll
                                + ", focus rect = " + mFocusRectparams.focusRect());
                    }
                    smoothScrollBy(-amountToScroll);
                    mIsAnimate = true;
                } else {
                    if (!reset) {
                        offsetFocusRect(0, -(nextSelctedView.getHeight() + mSpacing));
                    }
                    mIsAnimate = true;
                }
            } else {
                reset();
                offsetFocusRect(0, -distanceLeft);
                mIsAnimate = true;
            }

            return amountToScroll;
        }

        return 0;
    }

    /**
     * 手动查看内部的focus对象
     *
     * @param keyCode 按键值
     */
    public void manualFindFocusInner(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_MOVE_HOME:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MOVE_END:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // 从外部focus切换到内部focus
                if (!mItemInnerFocusState) {
                    View selectedView = getSelectedView();
                    if (selectedView instanceof InnerFocusLayout) {
                        InnerFocusLayout focusView = (InnerFocusLayout) selectedView;
                        // 如果能找到focus就进入内部选中模式
                        if (focusView.isChangedInnerKey(keyCode) && focusView.findFirstFocus(keyCode)) {
                            mItemInnerFocusState = true;
                            focusView.setNextFocusSelected();
                        }
                    }
                }
            default:
                break;
        }
    }

    /**
     * 处理内部Focus的动作
     *
     * @param keyCode
     * @param event
     * @return boolean true 成功处理完成
     */
    public boolean actionInnerFocus(int keyCode, KeyEvent event) {
        if (mItemInnerFocusState && innerFocus(keyCode, event)) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout) selectedView;
                focusView.setNextFocusSelected();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkState(int keyCode) {
        if (mLastScrollState == OnScrollListener.SCROLL_STATE_FLING) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                return true;
            }
        }

        return false;
    }

    private boolean retainFocus = true;

    public void setRetainFocus(boolean retainFocus) {
        this.retainFocus = retainFocus;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        if (checkState(keyCode)) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_MOVE_HOME:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MOVE_END: {
                if (innerFocus(keyCode, event)) {
                    return true;
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN && getNextSelectedPosition(FOCUS_DOWN) == INVALID_POSITION) {
//                    if (mLastScrollState != OnScrollListener.SCROLL_STATE_FLING)
                    return true;
                }
//                if (!retainFocus)
//                    return false;
                break;
            }
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // 从外部focus切换到内部focus
                if (!mItemInnerFocusState) {
                    View selectedView = getSelectedView();
                    if (selectedView instanceof InnerFocusLayout) {
                        InnerFocusLayout focusView = (InnerFocusLayout) selectedView;
                        // 如果能找到focus就进入内部选中模式
                        if (focusView.isChangedInnerKey(keyCode) && focusView.findFirstFocus(keyCode)) {
                            mItemInnerFocusState = true;
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    // 如果已经在进入到内部就在内部进行focus
                    if (innerFocus(keyCode, event)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            default:
                break;
        }


        return super.preOnKeyDown(keyCode, event);
    }

    /**
     * 进入到内部进行focus
     *
     * @param keyCode
     * @param event
     * @return
     */
    private boolean innerFocus(int keyCode, KeyEvent event) {
        if (mItemInnerFocusState) {
            View selectedView = getSelectedView();
            if (selectedView instanceof InnerFocusLayout) {
                InnerFocusLayout focusView = (InnerFocusLayout) selectedView;
                // 如果能找到focus就进入内部选中模式
                if (focusView.findNextFocus(keyCode, event)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getItemDistance(int startPos, int endPos, int direction) {
        if (DEBUG)
            Log.d(TAG, "before next distance, start pos:" + startPos + ",endPos:" + endPos + ",direction:" + direction);
        ListAdapter adapter = null;
        if (getAdapter() instanceof HeaderViewListAdapter) {
            adapter = ((HeaderViewListAdapter) getAdapter()).getWrappedAdapter();
        } else {
            adapter = getAdapter();
        }

        if (adapter == null || !(adapter instanceof GroupBaseAdapter))
            return 0;
        GroupBaseAdapter groupAdapter = (GroupBaseAdapter) adapter;
        // 有效性
        if (startPos < 0 || endPos < 0) {
            throw new IllegalArgumentException();
        }
        // 相同为0
        if (startPos == endPos) {
            return 0;
        }
        int startGroupPos = groupAdapter.getGroupPos(startPos);
        int startItemPos = groupAdapter.getGroupItemPos(startPos);
        int endGroupPos = groupAdapter.getGroupPos(endPos);
        int endItemPos = groupAdapter.getGroupItemPos(endPos);
        int hintHeight = getGroupHintHeight(groupAdapter);
        int itemHeight = getGroupItemHeight(groupAdapter);
        int totalHeight = 0;
        if (direction == View.FOCUS_DOWN) {
            if (startGroupPos != endGroupPos) {
                // 在不同的组内
                if (startItemPos == Integer.MAX_VALUE) {
                    // 组标识+后续的item总量
                    totalHeight += groupAdapter.getItemCount(startGroupPos) * itemHeight;
                } else {
                    // 后续的总量，本身不记入
                    totalHeight += (groupAdapter.getItemCount(startGroupPos) - 1 - startItemPos) * itemHeight;
                }
                if (endItemPos == Integer.MAX_VALUE) {
                    // 只用添加组标识
                    totalHeight += hintHeight;
                } else {
                    // 前面的item总量 + 组标识
                    totalHeight += endItemPos * itemHeight + hintHeight + itemHeight;//center
                }
                for (int i = startGroupPos + 1; i < endGroupPos; i++) {
                    // 所有中间组的距离 所有item + 组标识
                    totalHeight += groupAdapter.getItemCount(i) * itemHeight;
                    totalHeight += hintHeight;
                }
            } else {
                // 在相同的组内
                if (startItemPos == Integer.MAX_VALUE) {
                    totalHeight += (endItemPos + 1) * itemHeight;
                } else {
                    totalHeight += (endItemPos - startItemPos) * itemHeight + itemHeight;
                }
            }
        } else if (direction == View.FOCUS_UP) {
            if (startGroupPos != endGroupPos) {
                // 在不同的组内
                if (startItemPos == Integer.MAX_VALUE) {
                    // 之前组标识，本身不记入

//                    totalHeight -= hintHeight;
                } else {
                    // 组标识+后续的item总量
                    totalHeight -= startItemPos * itemHeight + hintHeight;
                }
                if (endItemPos == Integer.MAX_VALUE) {
                    // 只用添加组标识
                    //totalHeight += hintWidth;
                    totalHeight -= groupAdapter.getItemCount(endGroupPos) * itemHeight + hintHeight;
                } else {
                    // 前面的item总量 + 组标识
                    totalHeight -= (groupAdapter.getItemCount(endGroupPos) - endItemPos) * itemHeight;
                }
                for (int i = startGroupPos - 1; i > endGroupPos; i--) {
                    // 所有中间组的距离 所有item + 组标识
                    totalHeight -= groupAdapter.getItemCount(i) * itemHeight;
                    totalHeight -= hintHeight;
                }
            } else {
                // 在相同的组内
//                if (startItemPos == Integer.MAX_VALUE) {
//                    totalHeight -= hintHeight;
//                    totalHeight -= endItemPos * itemHeight;
//                } else {
                totalHeight = (endItemPos - startItemPos) * itemHeight;
//                }
            }
        }
        if (DEBUG)
            Log.i(TAG, "getItemDistance startPos=" + startPos + " endPos=" + endPos + " startGroupPos=" + startGroupPos + " startItemPos=" + startItemPos +
                    " endGroupPos=" + endGroupPos + " endItemPos=" + endItemPos + " hintWidth=" + hintHeight + " itemWidth=" + itemHeight + " totalHeight=" + totalHeight);
        return totalHeight;
    }

    /**
     * 取得组标识的宽度
     *
     * @param groupAdapter
     * @return
     */
    private int getGroupHintHeight(GroupBaseAdapter groupAdapter) {
        Rect hintRect = groupAdapter.getGroupHintRect();
        if (hintRect != null) {
            return hintRect.height();
        }
        return 0;
    }

    /**
     * 取得组内item的宽度
     *
     * @param groupAdapter
     * @return
     */
    private int getGroupItemHeight(GroupBaseAdapter groupAdapter) {
        Rect itemRect = groupAdapter.getGroupItemRect();
        if (itemRect != null) {
            return itemRect.height();
        }
        return 0;
    }
}

package com.yunos.tvtaobao.search.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.widget.TvRecyclerView;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2018/12/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CenterRecyclerView extends RecyclerView {
    private static final String TAG = "CenterRecyclerView";
    private View curFocusChildView;
    private int mLoadMoreBeforehandCount = 20;

    public CenterRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public CenterRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CenterRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        curFocusChildView = null;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        AppDebug.i(TAG, TAG + ".computeScroll ScrollState : " + getScrollState());
        //TODO 修改没有居中的情况下，重新滚动到最中间
        if (getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            AppDebug.i(TAG, TAG + ".computeScroll curFocusChildView : " + curFocusChildView);
            if (curFocusChildView != null) {
                if (!isCenter(curFocusChildView)) {
                    refreshFocusItemToCenter(curFocusChildView);
                }
            }
        }
    }

    private boolean isCenter(View view) {
        AppDebug.i(TAG, TAG + ".isCenter Height : " + getHeight());
        AppDebug.i(TAG, TAG + ".isCenter view.Height : " + view.getHeight() + " ,view.Y : " + view.getY());
        return (this.getHeight() / 2 == view.getY() + view.getHeight() / 2);
    }

    public void refreshFocusItemToCenter(View child) {
//        View tView = getLayoutManager().getFocusedChild();
        AppDebug.i(TAG, TAG + ".refreshFocusItemToCenter view : " + child);
        if (child == null) {
            return;
        }
        Rect mTempRect = new Rect();
        mTempRect.set(0, 0, child.getWidth(), child.getHeight());
        AppDebug.i(TAG, "fixed position: " + mTempRect);

        // todo 获取父容器居中显示位置
        int parentLeft = 0;
        int parentRight;
        int parentTop = 0;
        int parentBottom;
        int childWidth = child.getWidth();
        int childHeight = child.getHeight();
        parentLeft = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 - childWidth / 2;
        parentTop = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 - childHeight / 2;
        parentRight = parentLeft + childWidth;
        parentBottom = parentTop + childHeight;

        AppDebug.i(TAG, "onRequestChildFocus child : " + child);
        final int childLeft = child.getLeft() + mTempRect.left;
        final int childTop = child.getTop() + mTempRect.top;
        final int childRight = childLeft + mTempRect.width();
        final int childBottom = childTop + mTempRect.height();

        final int offScreenLeft = Math.min(0, childLeft - parentLeft);
        final int offScreenTop = Math.min(0, childTop - parentTop);
        final int offScreenRight = Math.max(0, childRight - parentRight);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom);

        // todo 计算需要的偏移量
        final int dx;
        dx = offScreenLeft != 0 ? offScreenLeft
                : Math.min(childLeft - parentLeft, offScreenRight);
        final int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);

        if (dx != 0 || dy != 0) {
            smoothScrollBy(dx, dy);
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        AppDebug.i(TAG, TAG + ".requestChildFocus child : " + child + " " + focused);
        curFocusChildView = focused;
        refreshFocusItemToCenter(focused);
    }

    /***********
     * 按键加载更多 start
     **********/

    private OnLoadMoreListener mOnLoadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            // 加载更多回调
            if (null != mOnLoadMoreListener) {
                if (getLastVisiblePosition() >= getAdapter().getItemCount() - (1 + mLoadMoreBeforehandCount)) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        }
        super.onScrollStateChanged(state);
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        } else {
            return getChildAdapterPosition(getChildAt(childCount - 1));
        }
    }

    public void setCurrentItemToCenter(int position) {
        if (getLayoutManager().getChildCount() <= 0)
            return;

        View itemView = getLayoutManager().getChildAt(0);
        int itemHeight = itemView.getHeight();
        int scrollY = itemHeight * position;
        int distanceY = scrollY - getHeight() / 2 + itemHeight / 2;

        scrollBy(0, distanceY);
    }

    public View findCurFocused() {
        AppDebug.e(TAG, TAG + ".findCurFocused curFocusChildView : " + curFocusChildView);
        if (curFocusChildView != null) {
            return curFocusChildView;
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                View firstView = getChildAt(i);
                if (firstView != null && firstView.hasFocusable()) {
                    return firstView;
                }
            }
            return null;
        }
    }

    public View findNextFocused(View focused, int direction) {
        if (direction == FOCUS_RIGHT) {
            return findCurFocused();
        }
        return null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                || event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
            return super.dispatchKeyEvent(event);
        }

        boolean result = super.dispatchKeyEvent(event);
        View focusView = this.getFocusedChild();
        if (focusView == null) {
            return result;
        } else {

            int dy = 0;
            int dx = 0;
            if (getChildCount() > 0) {
                View firstView = this.getChildAt(0);
                dy = firstView.getHeight();
                dx = firstView.getWidth();
            }
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return true;
            } else {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        View rightView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_RIGHT);
                        Log.i(TAG, "rightView is null:" + (rightView == null));
                        if (rightView != null) {
                            rightView.requestFocus();
                            return true;
                        } else {
                            return super.dispatchKeyEvent(event);
                        }
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        View leftView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_LEFT);
                        Log.i(TAG, "leftView is null:" + (leftView == null));
                        if (leftView != null) {
                            leftView.requestFocus();
                            return true;
                        } else {
                            return super.dispatchKeyEvent(event);
                        }
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                        Log.i(TAG, " downView is null:" + (downView == null));
                        if (downView != null) {
                            downView.requestFocus();
                            return true;
                        } else {
                            this.smoothScrollBy(0, dy);
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_UP:
                        View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                        Log.i(TAG, "upView is null:" + (upView == null));
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            return true;
                        } else {
                            if (upView != null) {
                                upView.requestFocus();
                                return true;
                            } else {
                                this.smoothScrollBy(0, -dy);
                                return true;
                            }

                        }
                }

            }

        }
        return result;
    }
}

/**
 * $
 * PROJECT NAME: Qian
 * PACKAGE NAME: com.qian
 * FILE NAME: TvShopFocusHListView.java
 * CREATED TIME: 2015-6-30
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.tvshoppingbundle.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusHListView;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.core.common.AppDebug;

public class TvShopFocusHListView extends FocusHListView {

    private int mOldSelectionPosition = INVALID_POSITION;
    private int mCurrentSelectionPosition = INVALID_POSITION;
    FocusRectParams mFocusRectparams = null;

    public TvShopFocusHListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public TvShopFocusHListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TvShopFocusHListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mFocusRectparams = getFocusParams();
    }

    @Override
    protected void layoutChildren() {
        AppDebug.i("test", "test.layoutChildren");
        postInvalidate();
        super.layoutChildren();
    }

    /**
     * 第一次选中选项时，调用此方法
     * @param position
     */
    public void fristSelection(int position) {
        mLayoutMode = LAYOUT_FROM_MIDDLE;
        mOldSelectionPosition = getSelectedItemPosition();
        super.setSelection(position);
        mCurrentSelectionPosition = position;
    }

    protected void amountToScroll() {
        int nextSelectedPosition = mCurrentSelectionPosition;
        setSelectedPositionInt(nextSelectedPosition);
        setNextSelectedPositionInt(nextSelectedPosition);
        int direction = View.FOCUS_RIGHT;
        if (nextSelectedPosition < mOldSelectionPosition) {
            direction = View.FOCUS_LEFT;
        }

        int center = (getWidth() - mListPadding.left - mListPadding.right) / 2 + mListPadding.left;

        int listLeft = mListPadding.left;
        int numChildren = getChildCount();
        int amountToScroll = 0;
        int distanceLeft = getLeftScrollDistance();
        if (direction == View.FOCUS_RIGHT) {
            View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
            int nextSelectedCenter = 0;
            boolean reset = false;
            if (nextSelctedView == null) {
                nextSelctedView = getChildAt(getChildCount() - 1);
                nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                nextSelectedCenter += nextSelctedView.getWidth() * (nextSelectedPosition - getLastVisiblePosition());

                reset = false;
            } else {
                nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                reset = true;
            }

            reset = true;

            int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

            if (finalNextSelectedCenter > center) {
                amountToScroll = finalNextSelectedCenter - center;
                int maxDiff = nextSelctedView.getWidth() * (mItemCount - getLastVisiblePosition() - 1);
                maxDiff -= distanceLeft;
                View lastVisibleView = getChildAt(numChildren - 1);
                if (lastVisibleView.getRight() > getWidth() - mListPadding.right) {
                    maxDiff += (lastVisibleView.getRight() - (getWidth() - mListPadding.right));
                }

                if (amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if (reset) {
                    reset();
                    mFocusRectparams.focusRect().offset(-distanceLeft, 0);
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        mFocusRectparams.focusRect().offset(-amountToScroll, 0);
                    } else {
                        mFocusRectparams.focusRect().offset((nextSelctedView.getWidth() - amountToScroll), 0);
                    }

                    smoothScrollBy(amountToScroll);

                } else {
                    if (!reset) {
                        mFocusRectparams.focusRect().offset(nextSelctedView.getWidth(), 0);
                    }
                }
            } else {
                reset();
                mFocusRectparams.focusRect().offset(-distanceLeft, 0);
            }
        } else if (direction == View.FOCUS_LEFT) {
            View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
            int nextSelectedCenter = 0;
            boolean reset = false;
            if (nextSelctedView == null) {
                nextSelctedView = getChildAt(0);
                nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                if (nextSelectedPosition >= getHeaderViewsCount()) {
                    nextSelectedCenter -= nextSelctedView.getWidth()
                            * (getFirstVisiblePosition() - nextSelectedPosition);
                } else {
                    nextSelectedCenter -= nextSelctedView.getWidth()
                            * (getFirstVisiblePosition() - getHeaderViewsCount());
                    for (int i = getHeaderViewsCount() - 1; i >= nextSelectedPosition; i--) {
                        nextSelectedCenter -= getHeaderView(i).getWidth();
                    }
                }

                reset = false;
            } else {
                nextSelectedCenter = (nextSelctedView.getLeft() + nextSelctedView.getRight()) / 2;
                reset = true;
            }

            int finalNextSelectedCenter = nextSelectedCenter - distanceLeft;

            if (finalNextSelectedCenter < center) {
                amountToScroll = center - finalNextSelectedCenter;
                int maxDiff = 0;

                if (getFirstVisiblePosition() >= getHeaderViewsCount()) {
                    maxDiff = nextSelctedView.getWidth() * (getFirstVisiblePosition() - getHeaderViewsCount());
                }

                int start = getHeaderViewsCount() - 1;
                if (start > getFirstVisiblePosition() - 1) {
                    start = getFirstVisiblePosition() - 1;
                }
                for (int i = start; i >= 0; i--) {
                    maxDiff += getHeaderView(i).getWidth();
                }
                if (maxDiff < 0) {
                    maxDiff = 0;
                }

                maxDiff += distanceLeft;
                View firstVisibleView = getChildAt(0);
                if (firstVisibleView.getLeft() < listLeft) {
                    maxDiff += (listLeft - firstVisibleView.getLeft());
                }

                if (amountToScroll > maxDiff) {
                    amountToScroll = maxDiff;
                }

                if (reset) {
                    reset();
                    mFocusRectparams.focusRect().offset(-distanceLeft, 0);
                }

                if (amountToScroll > 0) {
                    if (reset) {
                        mFocusRectparams.focusRect().offset(amountToScroll, 0);
                    } else {
                        mFocusRectparams.focusRect().offset(-(nextSelctedView.getWidth() - amountToScroll), 0);
                    }
                    smoothScrollBy(-amountToScroll);
                } else {
                    if (!reset) {
                        mFocusRectparams.focusRect().offset(-nextSelctedView.getWidth(), 0);
                    }
                }
            } else {
                reset();
                mFocusRectparams.focusRect().offset(-distanceLeft, 0);
            }
        }
    }
}

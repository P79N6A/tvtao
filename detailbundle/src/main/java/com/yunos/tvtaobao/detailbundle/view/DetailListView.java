package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.ListView;
import com.yunos.tv.core.common.AppDebug;

public class DetailListView extends ListView {

    private final String TAG = "DetailListView";
    private int mDirection;
    private int nextSelectedPosition;

    public DetailListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDetailListView(context);
    }

    public DetailListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDetailListView(context);
    }

    public DetailListView(Context context) {
        super(context);
        initDetailListView(context);
    }

    private void initDetailListView(Context context) {
        mDirection = 0;
    }

    @Override
    protected int lookForSelectablePositionOnScreen(int direction) {
        mDirection = direction;
        nextSelectedPosition = super.lookForSelectablePositionOnScreen(direction);
        AppDebug.i(TAG, "lookForSelectablePositionOnScreen --> mDirection = " + mDirection
                + "; nextSelectedPosition = " + nextSelectedPosition);
        return nextSelectedPosition;
    }

    @Override
    protected int getArrowScrollPreviewLength() {

        int Length = super.getArrowScrollPreviewLength();

        if (nextSelectedPosition != INVALID_POSITION) {
            int numChildren = getVisibleChildCount();
            View viewToMakeVisible = null;
            int indexToMakeVisible = numChildren - 1;
            if (mDirection == View.FOCUS_DOWN) {
                indexToMakeVisible = numChildren - 1;
                indexToMakeVisible = nextSelectedPosition - getFirstVisiblePosition();
                viewToMakeVisible = getChildAt(indexToMakeVisible);
            } else if (mDirection == View.FOCUS_UP) {
                indexToMakeVisible = 0;
                indexToMakeVisible = nextSelectedPosition - getFirstVisiblePosition();
                viewToMakeVisible = getChildAt(indexToMakeVisible);
            }

            if (viewToMakeVisible != null) {
                Length = viewToMakeVisible.getHeight();
            }
            AppDebug.i(TAG, "getArrowScrollPreviewLength --> viewToMakeVisible = " + viewToMakeVisible
                    + "; numChildren = " + numChildren + "; mDirection = " + mDirection + "; indexToMakeVisible = "
                    + indexToMakeVisible + "; Length = " + Length);
        }
        return Length;
    }

    @Override
    public int getMaxScrollAmount() { 
        return (int) (0.53 * (getBottom() - getTop()));
    }

}

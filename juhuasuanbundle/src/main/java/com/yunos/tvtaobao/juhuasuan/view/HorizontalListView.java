package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.animation.Transformation;
import android.widget.SpinnerAdapter;

import com.yunos.tv.core.common.AppDebug;

public class HorizontalListView extends AbsHorizontalListView {

    int mDividerWidth;
    Drawable mDivider;

    final static int NO_POSITION = -1;
    private final static String TAG = "HorizontalListView";

    //    private Handler mHandler = new Handler();

    public HorizontalListView(Context context) {
        this(context, null);

    }

    public HorizontalListView(Context context, AttributeSet attrs) {
        //this(context, attrs, yunos.demo.R.attr.horizontalListViewStyle);
        super(context, attrs);
    }

    public HorizontalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSpacing(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //    	if(mHandler.hasMessages(1)){
        //    		return true;
        //    	}
        //    	else{
        //        	mHandler.sendEmptyMessageDelayed(1, 200);
        //    	}
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        AppDebug.i(TAG, TAG + ".onMeasure widthMeasureSpec=" + widthMeasureSpec + ", heightMeasureSpec="
                + heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == View.MeasureSpec.AT_MOST) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
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
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        return super.focusSearch(focused, direction);
    }

    @Override
    public View focusSearch(int direction) {
        return super.focusSearch(direction);
    }

    /**
     * Measures the height of the given range of children (inclusive) and
     * returns the height with this ListView's padding and divider heights
     * included. If maxHeight is provided, the measuring will stop when the
     * current height reaches maxHeight.
     * @param heightMeasureSpec
     *            The height measure spec to be given to a child's {@link View#measure(int, int)}.
     * @param startPosition
     *            The position of the first child to be shown.
     * @param endPosition
     *            The (inclusive) position of the last child to be
     *            shown. Specify {@link #NO_POSITION} if the last child should be
     *            the last available child from the adapter.
     * @param maxWidth
     *            The maximum maxWidth that will be returned (if all the
     *            children don't fit in this value, this value will be
     *            returned).
     * @param disallowPartialChildPosition
     *            In general, whether the returned
     *            width should only contain entire children. This is more
     *            powerful--it is the first inclusive position at which partial
     *            children will not be allowed. Example: it looks nice to have
     *            at least 3 completely visible children, and in portrait this
     *            will most likely fit; but in landscape there could be times
     *            when even 2 children can not be completely shown, so a value
     *            of 2 (remember, inclusive) would be good (assuming
     *            startPosition is 0).
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
                // We went over, figure out which height to return.  If returnedHeight > maxHeight,
                // then the i'th position did not fit completely.
                return (disallowPartialChildPosition >= 0) // Disallowing is enabled (> -1)
                        && (i > disallowPartialChildPosition) // We've past the min pos
                        && (prevWidthWithoutPartialChild > 0) // We have a prev height
                        && (returnedWidth != maxWidth) // i'th child did not fit completely
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

    /**
     * @return True to recycle the views used to measure this ListView in
     *         UNSPECIFIED/AT_MOST modes, false otherwise.
     * @hide
     */
    @ViewDebug.ExportedProperty(category = "list")
    protected boolean recycleOnMeasure() {
        return true;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        return super.getChildStaticTransformation(child, t);
    }

    public void setItemsCanFocus(boolean canFocus) {

    }

}

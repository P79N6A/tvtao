package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.yunos.tv.core.common.AppDebug;

public class ViewScroller extends Scroller {

    private int mDuration = 500;

    public ViewScroller(Context context) {
        super(context);
    }

    public ViewScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setmDuration(int time) {
        mDuration = time;
    }

    public int getmDuration() {
        return mDuration;
    }

    @Override
    public boolean computeScrollOffset() {
        boolean hr = super.computeScrollOffset();
        AppDebug.i("ViewScroller", "computeScrollOffset isFinished = " + isFinished() + ", mScrollListener = "
                + mScrollListener);
        if (mScrollListener != null) {
            mScrollListener.scrolling();
        }

        if (isFinished()) {
            if (mScrollListener != null) {
                mScrollListener.endScroll();
            }
        }

        return hr;
    }

    ScrollListener mScrollListener;

    public void setScrollListener(ScrollListener l) {
        mScrollListener = l;
    }

    public interface ScrollListener {

        public void startScroll();

        public void scrolling();

        public void endScroll();
    }
}

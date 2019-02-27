package com.yunos.tvtaobao.juhuasuan.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

import com.yunos.tv.core.common.AppDebug;

public class ScrollHorizontalScrollView extends HorizontalScrollView {

    private String TAG = "ScrollHorizontalScrollView";

    private Context mContext = null;

    private HotScroller mScrollerer = null;

    private int currentX = 0;
    private int currentY = 0;

    private int mDuration = 600;

    public ScrollHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        OnInitWorkShopScrollView(context);
    }

    public ScrollHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        OnInitWorkShopScrollView(context);
    }

    public ScrollHorizontalScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        OnInitWorkShopScrollView(context);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        AppDebug.i(TAG, TAG + ".onRestoreInstanceState state=" + state);
        //        super.onRestoreInstanceState(state);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        AppDebug.i(TAG, TAG + ".onSaveInstanceState ");
        return super.onSaveInstanceState();
    }

    private void OnInitWorkShopScrollView(Context context) {
        mContext = context;
        mScrollerer = new HotScroller(mContext, new DecelerateInterpolator());
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // TODO Auto-generated method stub
        super.onScrollChanged(l, t, oldl, oldt);

    }

    public void startScroll(int dx, int dy) {
        startScroll(dx, dy, mDuration);
    }

    public void startScroll(int dx, int dy, int duration) {
        startScroll(currentX, currentY, dx, dy, duration);
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        if (mScrollerer.isFinished()) {
            mScrollerer.forceFinished(true);
        }

        if (startX < 0) {
            startX = 0;
        }
        AppDebug.i(TAG, TAG + ".startScroll startX=" + startX + ", startY=" + startY + ", dx=" + dx + ", dy=" + dy);
        mScrollerer.startScroll(startX, startY, dx, dy, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScrollerer.computeScrollOffset()) {
            currentX = mScrollerer.getCurrX();
            currentY = mScrollerer.getCurrY();
            AppDebug.i(TAG, TAG + ".computeScroll currentX=" + currentX + ", currentY=" + currentY);

            this.scrollTo(currentX, currentY);

            this.postInvalidate();
        }
    }

    public void resetScroll() {
        currentX = 0;
        currentY = 0;
        AppDebug.i(TAG, TAG + ".resetScroll currentX=" + currentX + ", currentY=" + currentY);
    }

    class HotScroller extends Scroller {

        public HotScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
            // TODO Auto-generated constructor stub
        }

        public HotScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
            // TODO Auto-generated constructor stub
        }

        public HotScroller(Context context) {
            super(context, new AccelerateDecelerateInterpolator());
            // TODO Auto-generated constructor stub
        }

        @Override
        public boolean computeScrollOffset() {
            if (!isFinished()) {
                invalidate();
            }
            return super.computeScrollOffset();
        }
    }
}

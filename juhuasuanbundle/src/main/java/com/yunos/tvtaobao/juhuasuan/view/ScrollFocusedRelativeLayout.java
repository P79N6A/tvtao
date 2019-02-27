package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;


import com.yunos.tvtaobao.juhuasuan.R;

import java.util.HashMap;
import java.util.Map;

public class ScrollFocusedRelativeLayout extends RelativeLayout {

    public static final String TAG                        = "FocusedRelativeLayout";
    private static final int    DEFAULT_FRAME_RATE         = 6;
    private static final int    DEFAULT_FRAME              = 1;
    private static final int    SCROLL_DURATION            = 120;
    public static final int     FOCUS_SYNC_DRAW            = 0;
    public static final int     FOCUS_ASYNC_DRAW           = 1;
    public static final int     FOCUS_STATIC_DRAW          = 2;
    private int                 mode                       = 1;
    private Rect mMySelectedPaddingRect     = new Rect();
    private Rect mManualSelectedPaddingRect = new Rect();
    protected Drawable mMySelectedDrawable        = null;

    int                         textHeight                 = 0;
    int                         mItemImagedWidthDiff       = 0;
    int                         mItemTopDiff               = 0;
    int                         mItemBottomDiff            = 0;

    private boolean             isTransAnimation           = false;

    private float               mScaleXValue               = 1.0F;
    private float               mScaleYValue               = 1.0F;

    private View mLastSelectedView          = null;

    private long                KEY_INTERVEL               = 20L;
    private long                mKeyTime                   = 0L;

    private final int           STATE_IDLE                 = 0;
    private final int           STATE_DRAWING              = 1;
    private int                 mState                     = 0;
    private boolean             mNeedDraw                  = false;

    private int                 mCurrentFrame              = 1;
    private int                 mFrameRate                 = 6;
    private Rect mLastFocusRect;
    private Rect mFocusRect;
    private Rect mCurrentrect;
    private boolean             mIsFirstFrame              = true;
    private boolean             mConstrantNotDraw          = false;
    public int                  mIndex                     = -1;

    private boolean             mOutsieScroll              = false;
    private boolean             mInit                      = false;
    private HotScroller         mScroller;
    private int                 mScreenWidth;
    private int                 mViewRight                 = 20;
    private int                 mStartX;
    private long                mScrollTime                = 0L;

    private Map<View, NodeInfo> mNodeMap                   = new HashMap();

    public void setManualPadding(int left, int top, int right, int bottom) {
        this.mManualSelectedPaddingRect.left = left;
        this.mManualSelectedPaddingRect.right = right;
        this.mManualSelectedPaddingRect.top = top;
        this.mManualSelectedPaddingRect.bottom = bottom;
    }

    private void setInit(boolean isInit) {
        synchronized (this) {
            this.mInit = isInit;
        }
    }

    private boolean isInit() {
        synchronized (this) {
            return this.mInit;
        }
    }

    public void setFocusMode(int mode) {
        this.mode = mode;
    }

    public void setViewRight(int right) {
        this.mViewRight = right;
    }

    public void setOutsideSroll(boolean scroll) {
        Log.d("FocusedRelativeLayout", "setOutsideSroll scroll = " + scroll + ", this = " + this);
        this.mScrollTime = System.currentTimeMillis();
        this.mOutsieScroll = scroll;
    }

    public ScrollFocusedRelativeLayout(Context contxt) {
        super(contxt);
        setChildrenDrawingOrderEnabled(true);
        this.mScroller = new HotScroller(contxt);
        this.mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
        initData();
    }

    private void initData() {
        this.setItemScaleValue(1.1f, 1.1f);
        // 设置焦点框图片
        this.setFocusResId(R.drawable.ju_home_focus);
        // 设置动画类型
        this.setFocusMode(ScrollFocusedRelativeLayout.FOCUS_ASYNC_DRAW);
        // 设置帧率
        this.setFrameRate(6);
        // 设置页面横向滚动后右边的padding
        //		this.setViewRight(20);
        // 设置padding
        //        this.setManualPadding(-10, -10, 10, 10);
    }

    public ScrollFocusedRelativeLayout(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        setChildrenDrawingOrderEnabled(true);
        this.mScroller = new HotScroller(contxt);
        this.mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
        initData();
    }

    public ScrollFocusedRelativeLayout(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        setChildrenDrawingOrderEnabled(true);
        this.mScroller = new HotScroller(contxt);
        this.mScreenWidth = contxt.getResources().getDisplayMetrics().widthPixels;
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        int selectedIndex = this.mIndex;
        if (selectedIndex < 0) {
            return i;
        }

        if (i < selectedIndex)
            return i;
        if (i >= selectedIndex) {
            return (childCount - 1 - i + selectedIndex);
        }
        return i;
    }

    private synchronized void init() {
        if ((hasFocus()) && (!(this.mOutsieScroll)) && (!(isInit()))) {
            int[] location = new int[2];
            int minLeft = 65536;
            for (int index = 0; index < getChildCount(); ++index) {
                View child = getChildAt(index);
                if (!(this.mNodeMap.containsKey(child))) {
                    NodeInfo info = new NodeInfo();
                    info.index = index;
                    this.mNodeMap.put(child, info);
                }

                child.getLocationOnScreen(location);
                if (location[0] < minLeft) {
                    minLeft = location[0];
                }
            }

            this.mStartX = minLeft;
            Log.d("FocusedRelativeLayout", "init mStartX = " + this.mStartX);
            setInit(true);
        }
    }

    public void release() {
        this.mNodeMap.clear();
    }

    public void setFrameRate(int rate) {
        this.mFrameRate = rate;
    }

    public void dispatchDraw(Canvas canvas) {
        Log.i("FocusedRelativeLayout", "dispatchDraw");
        super.dispatchDraw(canvas);
        drawFrame(canvas);
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Log.d("FocusedRelativeLayout", "onFocusChanged this = " + this + ", mScreenWidth = " + this.mScreenWidth);
        synchronized (this) {
            this.mKeyTime = System.currentTimeMillis();
        }

        this.isTransAnimation = false;
        this.mNeedDraw = true;
        setState(1);
        if (!(gainFocus)) {
            drawFrame(null);
            this.mMySelectedDrawable.setVisible(false, true);
        } else {
            if (-1 == this.mIndex) {
                this.mIndex = 0;
            }
            this.mLastSelectedView = null;
            invalidate();
        }
    }

    private void drawFrame(Canvas canvas) {
        Log.w("FocusedRelativeLayout", "drawFrame: mCurrentFrame = " + this.mCurrentFrame + ", needDraw = " + this.mNeedDraw + ", child count = " + getChildCount() + ", this = " + this + ", has focus = " + hasFocus() + ", mOutsieScroll = " + this.mOutsieScroll + ", mIndex = " + this.mIndex
                + ", mFrameRate = " + this.mFrameRate);

        if (this.mode == 0)
            drawSyncFrame(canvas);
        else if (1 == this.mode)
            drawAsyncFrame(canvas);
        else if (2 == this.mode)
            drawStaticFrame(canvas);
    }

    private void drawSyncFrame(Canvas canvas) {
        if (getSelectedView() != null) {
            if ((this.mCurrentFrame < this.mFrameRate) && (this.mNeedDraw)) {
                if (this.mIsFirstFrame) {
                    drawFirstFrame(canvas, true, true);
                    return;
                }
                drawOtherFrame(canvas, true, true);
                return;
            }
            if (this.mCurrentFrame == this.mFrameRate) {
                drawLastFrame(canvas, true, true);
                return;
            }
            if (this.mConstrantNotDraw)
                return;
            if (hasFocus()) {
                drawFocus(canvas, false);
                this.mLastFocusRect = this.mFocusRect;
            }
            return;
        }

        Log.w("FocusedRelativeLayout", "drawFrame select view is null");
    }

    private void drawAsyncFrame(Canvas canvas) {
        if (getSelectedView() != null) {
            boolean isScale = this.mCurrentFrame > this.mFrameRate / 2;
            if ((this.mCurrentFrame < this.mFrameRate) && (this.mNeedDraw)) {
                if (this.mIsFirstFrame) {
                    drawFirstFrame(canvas, isScale, !(isScale));
                    return;
                }
                drawOtherFrame(canvas, isScale, !(isScale));
                return;
            }
            if (this.mCurrentFrame == this.mFrameRate) {
                drawLastFrame(canvas, isScale, !(isScale));
                return;
            }
            if (this.mConstrantNotDraw)
                return;
            if (hasFocus()) {
                drawFocus(canvas, false);
                this.mLastFocusRect = this.mFocusRect;
            }
            return;
        }

        Log.w("FocusedRelativeLayout", "drawFrame select view is null");
    }

    private void drawStaticFrame(Canvas canvas) {
        if (getSelectedView() != null) {
            if ((this.mCurrentFrame < this.mFrameRate) && (this.mNeedDraw)) {
                if (this.mIsFirstFrame) {
                    drawFirstFrame(canvas, true, false);
                    return;
                }
                drawOtherFrame(canvas, true, false);
                return;
            }
            if (this.mCurrentFrame == this.mFrameRate) {
                drawLastFrame(canvas, true, false);
                return;
            }
            if (this.mConstrantNotDraw)
                return;
            if (hasFocus()) {
                drawFocus(canvas, false);
                this.mLastFocusRect = this.mFocusRect;
            }
            return;
        }

        Log.w("FocusedRelativeLayout", "drawFrame select view is null");
    }

    private void drawFirstFrame(Canvas canvas, boolean isScale, boolean isDynamic) {
        boolean dynamic = isDynamic;
        float scaleXValue = this.mScaleXValue;
        float scaleYValue = this.mScaleXValue;
        if (1 == this.mode) {
            dynamic = false;
            scaleXValue = 1.0F;
            scaleYValue = 1.0F;
        }
        this.mFocusRect = getImageRect(scaleXValue, scaleYValue, dynamic);
        this.mCurrentrect = this.mFocusRect;
        Log.d("FocusedRelativeLayout", "drawFirstFrame: mFocusRect = " + this.mFocusRect + ", this = " + this);

        drawScale(isScale);

        if (hasFocus()) {
            drawFocus(canvas, isDynamic);
        }
        this.mIsFirstFrame = false;
        this.mCurrentFrame += 1;
        invalidate();
    }

    private void drawOtherFrame(Canvas canvas, boolean isScale, boolean isDynamic) {
        Log.i("FocusedRelativeLayout", "drawOtherFrame, this = " + this);

        drawScale(isScale);

        if (hasFocus()) {
            drawFocus(canvas, isDynamic);
        }
        this.mCurrentFrame += 1;
        invalidate();
    }

    private void drawLastFrame(Canvas canvas, boolean isScale, boolean isDynamic) {
        Log.d("FocusedRelativeLayout", "drawLastFrame, this = " + this);

        drawScale(isScale);

        if (hasFocus()) {
            drawFocus(canvas, isDynamic);
        }

        this.mCurrentFrame = 1;
        this.mLastFocusRect = this.mFocusRect;
        this.mLastSelectedView = getSelectedView();
        this.mNeedDraw = false;
        this.mIsFirstFrame = true;

        setState(0);
    }

    private void scaleSelectedView() {
        View selectedView = getSelectedView();
        if (selectedView != null) {
            float diffX = this.mScaleXValue - 1.0F;
            float diffY = this.mScaleYValue - 1.0F;

            int frameRate = this.mFrameRate;
            if (1 == this.mode) {
                frameRate /= 2;
            }

            int currentFrame = this.mCurrentFrame;
            if (1 == this.mode) {
                currentFrame -= this.mFrameRate / 2;
            }
            float dstScaleX = 1.0F + diffX * currentFrame / frameRate;
            float dstScaleY = 1.0F + diffY * currentFrame / frameRate;
            Log.i("FocusedRelativeLayout", "scaleSelectedView: dstScaleX = " + dstScaleX + ", dstScaleY = " + dstScaleY + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", Selected View = " + selectedView + ", this = " + this);

            selectedView.setScaleX(dstScaleX);
            selectedView.setScaleY(dstScaleY);
        }
    }

    private void scaleLastSelectedView() {
        if (this.mLastSelectedView != null) {
            float diffX = this.mScaleXValue - 1.0F;
            float diffY = this.mScaleYValue - 1.0F;

            int frameRate = this.mFrameRate;
            if (1 == this.mode) {
                if (this.mCurrentFrame > this.mFrameRate / 2) {
                    return;
                }
                frameRate /= 2;
            }

            int currentFrame = this.mCurrentFrame;
            if (1 == this.mode) {
                currentFrame = this.mFrameRate / 2 - currentFrame;
            } else
                currentFrame = this.mFrameRate - currentFrame;

            float dstScaleX = 1.0F + diffX * currentFrame / frameRate;
            float dstScaleY = 1.0F + diffY * currentFrame / frameRate;
            Log.i("FocusedRelativeLayout", "scaleLastSelectedView: dstScaleX = " + dstScaleX + ", dstScaleY = " + dstScaleY + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", mLastSelectedView = " + this.mLastSelectedView + ", this = " + this
                    + ", currentFrame = " + currentFrame);

            this.mLastSelectedView.setScaleX(dstScaleX);
            this.mLastSelectedView.setScaleY(dstScaleY);
        }
    }

    private void drawScale(boolean isScale) {
        Log.i("FocusedRelativeLayout", "drawScale: mCurrentFrame = " + this.mCurrentFrame + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this);

        if ((hasFocus()) && (isScale)) {
            scaleSelectedView();
        }
        scaleLastSelectedView();
    }

    private void drawFocus(Canvas canvas, boolean isDynamic) {
        Log.i("FocusedRelativeLayout", "drawFocus: mCurrentFrame = " + this.mCurrentFrame + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this);
        if ((isDynamic) && (this.isTransAnimation) && (this.mLastFocusRect != null) && (getState() != 0))
            drawDynamicFocus(canvas);
        else
            drawStaticFocus(canvas);
    }

    private void drawStaticFocus(Canvas canvas) {
        float diffX = this.mScaleXValue - 1.0F;
        float diffY = this.mScaleYValue - 1.0F;

        int frameRate = this.mFrameRate;
        int currentFrame = this.mCurrentFrame;
        if (1 == this.mode) {
            frameRate /= 2;
            currentFrame -= frameRate;
        }

        float dstScaleX = 1.0F + diffX * currentFrame / frameRate;
        float dstScaleY = 1.0F + diffY * currentFrame / frameRate;
        Log.i("FocusedRelativeLayout", "drawStaticFocus: mCurrentFrame = " + this.mCurrentFrame + ", dstScaleX = " + dstScaleX + ", dstScaleY = " + dstScaleY + ", mScaleXValue = " + this.mScaleXValue + ", mScaleYValue = " + this.mScaleYValue + ", this = " + this);

        Rect dstRect = getImageRect(dstScaleX, dstScaleY, false);
        if (dstRect == null) {
            return;
        }

        this.mFocusRect = dstRect;
        this.mCurrentrect = dstRect;
        this.mMySelectedDrawable.setBounds(dstRect);
        this.mMySelectedDrawable.draw(canvas);
        this.mMySelectedDrawable.setVisible(true, true);
    }

    private void drawDynamicFocus(Canvas canvas) {
        Rect dstRect = new Rect();

        int frameRate = this.mFrameRate;
        if (1 == this.mode) {
            frameRate /= 2;
        }

        int diffLeft = this.mFocusRect.left - this.mLastFocusRect.left;
        int diffRight = this.mFocusRect.right - this.mLastFocusRect.right;
        int diffTop = this.mFocusRect.top - this.mLastFocusRect.top;
        int diffBottom = this.mFocusRect.bottom - this.mLastFocusRect.bottom;

        dstRect.left = (this.mLastFocusRect.left + diffLeft * this.mCurrentFrame / frameRate);
        dstRect.right = (this.mLastFocusRect.right + diffRight * this.mCurrentFrame / frameRate);
        dstRect.top = (this.mLastFocusRect.top + diffTop * this.mCurrentFrame / frameRate);
        dstRect.bottom = (this.mLastFocusRect.bottom + diffBottom * this.mCurrentFrame / frameRate);
        Log.i("FocusedRelativeLayout", "drawDynamicFocus: mCurrentFrame = " + this.mCurrentFrame + ", dstRect.left = " + dstRect.left + ", dstRect.right = " + dstRect.right + ", dstRect.top = " + dstRect.top + ", dstRect.bottom = " + dstRect.bottom + ", this = " + this);

        this.mCurrentrect = dstRect;
        this.mMySelectedDrawable.setBounds(dstRect);
        this.mMySelectedDrawable.draw(canvas);
        this.mMySelectedDrawable.setVisible(true, true);
    }

    public void setItemScaleValue(float scaleXValue, float scaleYValue) {
        this.mScaleXValue = scaleXValue;
        this.mScaleYValue = scaleYValue;
    }

    public void setFocusResId(int focusResId) {
        this.mMySelectedDrawable = getResources().getDrawable(focusResId);
        this.mMySelectedPaddingRect = new Rect();
        this.mMySelectedDrawable.getPadding(this.mMySelectedPaddingRect);
    }

    public View getSelectedView() {
        int indexOfView = this.mIndex;
        View selectedView = getChildAt(indexOfView);
        return selectedView;
    }

    public void setSelectedView(int index) {
        this.mIndex = index;
    }

    private Rect getImageRect(float scaleXValue, float scaleYValue, boolean isDynamic) {
        View selectedView = getSelectedView();
        if (selectedView == null) {
            return null;
        }
        Rect imgRect = new Rect();
        Rect gridViewRect = new Rect();

        if (selectedView instanceof ScalePostionInterface) {
            ScalePostionInterface face = (ScalePostionInterface) selectedView;
            imgRect = face.getScaledRect(scaleXValue, scaleYValue);
        } else {
            selectedView.getGlobalVisibleRect(imgRect);
            int imgW = imgRect.right - imgRect.left;
            int imgH = imgRect.bottom - imgRect.top;
            if (isDynamic) {
                imgRect.left = (int) (imgRect.left + (1.0D - scaleXValue) * imgW / 2.0D);
                imgRect.top = (int) (imgRect.top + (1.0D - scaleYValue) * imgH / 2.0D);
                imgRect.right = (int) (imgRect.left + imgW * scaleXValue);
                imgRect.bottom = (int) (imgRect.top + imgH * scaleYValue);
            }
        }
        Log.d("FocusedRelativeLayout", "getImageRect imgRect = " + imgRect);

        getGlobalVisibleRect(gridViewRect);

        imgRect.left -= gridViewRect.left;
        imgRect.right -= gridViewRect.left;
        imgRect.top -= gridViewRect.top;
        imgRect.bottom -= gridViewRect.top;

        imgRect.left += this.mScroller.getCurrX();
        imgRect.right += this.mScroller.getCurrX();

        imgRect.top -= this.mMySelectedPaddingRect.top;
        imgRect.left -= this.mMySelectedPaddingRect.left;
        imgRect.right += this.mMySelectedPaddingRect.right;
        imgRect.bottom += this.mMySelectedPaddingRect.bottom;

        imgRect.left += this.mManualSelectedPaddingRect.left;
        imgRect.right += this.mManualSelectedPaddingRect.right;
        imgRect.top += this.mManualSelectedPaddingRect.top;
        imgRect.bottom += this.mManualSelectedPaddingRect.bottom;

        return imgRect;
    }

    private void setState(int s) {
        synchronized (this) {
            this.mState = s;
        }
    }

    private int getState() {
        synchronized (this) {
            return this.mState;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((23 == keyCode) && (getSelectedView() != null)) {
            getSelectedView().performClick();
        }

        return super.onKeyUp(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int direction;
        synchronized (this) {
            if ((System.currentTimeMillis() - this.mKeyTime <= this.KEY_INTERVEL) || (getState() == 1) || (System.currentTimeMillis() - this.mScrollTime < 100L)) {
                Log.d("FocusedRelativeLayout", "onKeyDown mAnimationTime = " + this.mKeyTime + " -- current time = " + System.currentTimeMillis());
                return true;
            }
            this.mKeyTime = System.currentTimeMillis();
        }

        if (!(isInit())) {
            init();
            return true;
        }

        View lastSelectedView = getSelectedView();
        NodeInfo nodeInfo = (NodeInfo) this.mNodeMap.get(lastSelectedView);
        View v = null;

        switch (keyCode) {
            case 21:
                if (nodeInfo.fromLeft != null)
                    v = nodeInfo.fromLeft;
                else {
                    v = lastSelectedView.focusSearch(17);
                }
                direction = 17;
                break;
            case 22:
                if (nodeInfo.fromRight != null)
                    v = nodeInfo.fromRight;
                else {
                    v = lastSelectedView.focusSearch(66);
                }
                direction = 66;
                break;
            case 20:
                if (nodeInfo.fromDown != null)
                    v = nodeInfo.fromDown;
                else {
                    v = lastSelectedView.focusSearch(130);
                }
                direction = 130;
                break;
            case 19:
                if (nodeInfo.fromUp != null)
                    v = nodeInfo.fromUp;
                else {
                    v = lastSelectedView.focusSearch(33);
                }
                direction = 33;
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }

        Log.d("FocusedRelativeLayout", "onKeyDown v = " + v);
        if ((v != null)) {
            View.OnFocusChangeListener listener;
            NodeInfo info = (NodeInfo) this.mNodeMap.get(v);
            View selectedView = getSelectedView();
            if (selectedView != null) {
                selectedView.setSelected(false);
                listener = selectedView.getOnFocusChangeListener();
                if (listener != null) {
                    listener.onFocusChange(selectedView, false);
                }
            }
            if (info == null) {
                return false;
            }
            this.mIndex = info.index;

            selectedView = getSelectedView();
            if (selectedView != null) {
                selectedView.setSelected(true);
                listener = selectedView.getOnFocusChangeListener();
                if (listener != null) {
                    listener.onFocusChange(selectedView, true);
                }
            }

            switch (keyCode) {
                case 21:
                    info.fromRight = lastSelectedView;
                    break;
                case 22:
                    info.fromLeft = lastSelectedView;
                    break;
                case 20:
                    info.fromUp = lastSelectedView;
                    break;
                case 19:
                    info.fromDown = lastSelectedView;
            }

            scroll();
            
            this.isTransAnimation = true;
            this.mNeedDraw = true;
            setState(1);
            invalidate();
        } else {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            return false;
        }

        playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
        return true;
    }

    private void scroll() {
        int dx;
        int[] location = new int[2];
        getSelectedView().getLocationOnScreen(location);
        int left = location[0];
        int right = location[0] + getSelectedView().getWidth();
        int imgW = getSelectedView().getWidth();
        left = (int) (left + (1.0D - this.mScaleXValue) * imgW / 2.0D);
        right = (int) (left + imgW * this.mScaleXValue);

        Log.d("FocusedRelativeLayout", "scroll left = " + location[0] + ", right = " + right);
        if ((right >= this.mScreenWidth) && (!(this.mOutsieScroll))) {
            dx = right - this.mScreenWidth + this.mViewRight;

            smoothScrollBy(dx, 80);
            return;
        }
        getLocationOnScreen(location);
        Log.d("FocusedRelativeLayout", "scroll conrtainer left = " + this.mStartX);
        if ((left < this.mStartX) && (!(this.mOutsieScroll))) {
            dx = left - this.mStartX;
            if (this.mScroller.getCurrX() > Math.abs(dx))
                smoothScrollBy(dx, 80);
            else
                smoothScrollBy(-this.mScroller.getCurrX(), 80);
        }
    }

    private boolean containView(View v) {
        Rect containerRect = new Rect();
        Rect viewRect = new Rect();

        getGlobalVisibleRect(containerRect);
        v.getGlobalVisibleRect(viewRect);

        return ((containerRect.left > viewRect.left) || (containerRect.right < viewRect.right) || (containerRect.top > viewRect.top) || (containerRect.bottom < viewRect.bottom));
    }

    public void smoothScrollTo(int fx, int duration) {
        int dx = fx - this.mScroller.getFinalX();
        smoothScrollBy(dx, duration);
    }

    public void smoothScrollBy(int dx, int duration) {
        Log.w("FocusedRelativeLayout", "smoothScrollBy dx = " + dx);
        this.mScroller.startScroll(this.mScroller.getFinalX(), this.mScroller.getFinalY(), dx, this.mScroller.getFinalY(), duration);
        invalidate();
    }

    private boolean checkFocusPosition() {
        if ((this.mCurrentrect == null) || (!(hasFocus()))) {
            return false;
        }

        Rect dstRect = getImageRect(this.mScaleXValue, this.mScaleYValue, false);

        return ((Math.abs(dstRect.left - this.mCurrentrect.left) <= 5) && (Math.abs(dstRect.right - this.mCurrentrect.right) <= 5) && (Math.abs(dstRect.top - this.mCurrentrect.top) <= 5) && (Math.abs(dstRect.bottom - this.mCurrentrect.bottom) <= 5));
    }

    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            Log.d("FocusedRelativeLayout", "computeScroll mScroller.getCurrX() = " + this.mScroller.getCurrX());
        }

        super.computeScroll();
    }

    class HotScroller extends Scroller {

        public HotScroller(Context paramContext, Interpolator paramInterpolator, boolean paramBoolean) {
            super(paramContext, paramInterpolator, paramBoolean);
        }

        public HotScroller(Context paramContext, Interpolator paramInterpolator) {
            super(paramContext, paramInterpolator);
        }

        public HotScroller(Context paramContext) {
            super(paramContext);
        }

        public boolean computeScrollOffset() {
            boolean isFinished = isFinished();
            boolean needInvalidate = ScrollFocusedRelativeLayout.this.checkFocusPosition();
            Log.d("FocusedRelativeLayout", "computeScrollOffset isFinished = " + isFinished + ", mOutsieScroll = " + ScrollFocusedRelativeLayout.this.mOutsieScroll + ", needInvalidate = " + needInvalidate + ", this = " + this);
            if ((ScrollFocusedRelativeLayout.this.mOutsieScroll) || (!(isFinished)) || (needInvalidate)) {
                ScrollFocusedRelativeLayout.this.invalidate();
            }
            ScrollFocusedRelativeLayout.this.init();
            return super.computeScrollOffset();
        }
    }

    class NodeInfo {

        public int  index;
        public View fromLeft;
        public View fromRight;
        public View fromUp;
        public View fromDown;
    }

    public static abstract interface ScalePostionInterface {

        public abstract Rect getScaledRect(float paramFloat1, float paramFloat2);
    }
}
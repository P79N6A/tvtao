package com.yunos.tv.app.widget.focus;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.yunos.tv.app.widget.focus.listener.DrawListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.PositionListener;
import com.yunos.tv.app.widget.focus.params.AlphaParams;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.ScaleParams;
import com.yunos.tv.lib.SystemProUtils;

import java.util.LinkedList;
import java.util.List;

abstract public class PositionManager {

    protected static final String TAG = "PositionManager";
    protected static final boolean DEBUG = false;

    public static final int FOCUS_SYNC_DRAW = 0;
    public static final int FOCUS_ASYNC_DRAW = 1;
    public static final int FOCUS_STATIC_DRAW = 2;

    public static final int STATE_IDLE = 0;
    public static final int STATE_DRAWING = 1;

    PositionListener mListener;

    FocusListener mFocus = null;
    FocusListener mLastFocus = null;

    int mFrame = 1;
    int mScaleFrame = 1;
    int mFocusFrame = 1;
    int mAlphaFrame = 1;
    int mFocusFrameRate = 50;
    int mScaleFrameRate = 50;
    int mAlphaFrameRate = 50;

    DrawListener mSelector;
    DrawListener mConvertSelector;

    Rect mFocusRect = new Rect();
    Rect mClipFocusRect = new Rect();

    int mFocusMode = FOCUS_STATIC_DRAW;

    private ScaledList mScaledList = new ScaledList();
    private AlphaList mAlphaList = new AlphaList();
    private NodeManager mNodeManager = new NodeManager();

    private NodeManager mCurNodeManager = new NodeManager();// 当前focus的节点

    boolean mStart = true;
    boolean mPause = false;

    protected Interpolator mSelectorPolator = null;
    protected ItemListener mLastItem;
    protected boolean mForceDrawFocus = false;

    public static PositionManager createPositionManager(int focusMode, PositionListener l) {
        Log.i(TAG, "createPositionManager focusMode = " + focusMode);
        if (focusMode == FOCUS_SYNC_DRAW) {
            SystemProUtils.setGlobalFocusMode(focusMode);
            return new SyncPositionManager(focusMode, l);
        } else if (focusMode == FOCUS_STATIC_DRAW) {
            SystemProUtils.setGlobalFocusMode(focusMode);
            return new StaticPositionManager(focusMode, l);
        }

        Log.e(TAG, "createPositionManager focusMode not support");
        return null;
    }

    public boolean isFocusBackground() {
        return mFocus.isFocusBackground();
    }

    public PositionManager(int focusMode, PositionListener l) {
        this.mListener = l;
        this.mFocusMode = focusMode;
    }

    /**
     * 是否可以绘制focus框
     * @return
     */
    public boolean isFocusStarted() {
        return mStart;
    }

    public void focusStart() {
        if (mStart && !mPause) {
            return;
        }

        mStart = true;
        mPause = false;
        //        Log.i(TAG, TAG + ".focusStart.mFrame = " + mFrame + ".mScaleFrame = " + mScaleFrame + ".mFocusFrame = "
        //                + mFocusFrame + ".mAlphaFrame = " + mAlphaFrame + ".mFocusFrameRate = " + mFocusFrameRate
        //                + ".mScaleFrameRate = " + mScaleFrameRate + ".mAlphaFrameRate = " + mAlphaFrameRate);
        reset();
        mListener.postInvalidate();
    }

    public void focusPause() {
        if (mPause) {
            return;
        }
        mPause = true;
    }

    public void focusStop() {
        Log.i(TAG, TAG + ".focusStop");
        if (!mStart) {
            return;
        }

        mStart = false;
    }

    public void draw(Canvas canvas) {
        // drawFocus(canvas);
        // drawUnscale();
        //drawUnscale(canvas);
        drawOut(canvas);
        if (!mStart) {// 停止时，取消focus框以及还原大小
            drawCurOut(canvas);
        }
    }

    public void drawBeforeFocus(ItemListener item, Canvas canvas) {
        item.drawBeforeFocus(canvas);
    }

    public void drawAfterFocus(ItemListener item, Canvas canvas) {
        item.drawAfterFocus(canvas);
    }

    public void release() {
        Log.d(TAG, "release");
        mScaledList.clear();
        mAlphaList.clear();
        mNodeManager.clear();
        mCurNodeManager.clear();
    }

    protected void drawCurOut(Canvas canvas) {
        mCurNodeManager.drawOut(canvas);
    }

    protected void drawOut(Canvas canvas) {
        this.mNodeManager.drawOut(canvas);
    }

    protected void preDrawOut(Canvas canvas) {
        this.mNodeManager.preDrawOut(canvas);
    }

    protected void postDrawOut(Canvas canvas) {
        this.mNodeManager.postDrawOut(canvas);
    }

    protected void drawUnscale(Canvas canvas) {
        this.mScaledList.drawOut(canvas);
    }

    protected void preDrawUnscale(Canvas canvas) {
        this.mScaledList.preDrawOut(canvas);
    }

    public void forceDrawFocus() {
        mForceDrawFocus = true;
        if (mListener != null) {
            mListener.invalidate();
        }
    }

    protected void postDrawUnscale(Canvas canvas) {
        this.mScaledList.postDrawOut(canvas);
    }

    void drawFocus(Canvas canvas) {
        if (this.mFocus != null && !mFocusRect.isEmpty()&&mSelector!=null) {
            mSelector.setRect(mFocusRect);
            if (!mClipFocusRect.isEmpty() && clipCanvasIsNeeded(mFocusRect, mClipFocusRect)) {
                canvas.save();
                canvas.clipRect(mClipFocusRect);
                mSelector.draw(canvas);
                canvas.restore();
            } else {
                mSelector.draw(canvas);
            }
            if (mConvertSelector != null) {
                mConvertSelector.setRect(mFocusRect);
                mConvertSelector.draw(canvas);
            }
        } else {
            Log.w(TAG, "drawFocus mFocus = " + mFocus);
        }
    }

    void drawFocus(Canvas canvas, Rect rect, float alpha, DrawListener selector, Rect clipRect) {
        if (rect == null || selector == null) {
            return;
        }
        if (this.mFocus != null && !rect.isEmpty()) {
            selector.setRect(rect);
            selector.setAlpha(alpha);
            if (!clipRect.isEmpty() && clipCanvasIsNeeded(rect, clipRect)) {
                canvas.save(Canvas.CLIP_SAVE_FLAG);
                canvas.clipRect(clipRect);
                selector.draw(canvas);
                canvas.restore();
            } else {
                selector.draw(canvas);
            }
        } else {
            Log.w(TAG, "drawFocus mFocus = " + mFocus);
        }
    }

    void drawStaticFocus(Canvas canvas, ItemListener item) {
        drawStaticFocus(canvas, item, mFocus.getParams().getScaleParams().getScaleX(), mFocus.getParams()
                .getScaleParams().getScaleY());
    }

    void drawStaticFocus(Canvas canvas, ItemListener item, float scaleX, float scaleY) {
        mFocusRect.set(getDstRect(scaleX, scaleY, item.isScale()));
        if (DEBUG) {
            Log.i(TAG, TAG + ".drawStaticFocus.scaleX = " + scaleX + ".scaleY = " + scaleY + ".mFocusRect = "
                    + mFocusRect.toShortString() + ", item = " + item);
        }

        mClipFocusRect.set(getClipFocusRect(mFocus));
        // mListener.offsetDescendantRectToItsCoords((View) mFocus, mFocusRect);
        // offsetFocusRect(mFocusRect);
        offsetManualPadding(mFocusRect, item);
        drawFocus(canvas);
    }

    protected Rect getFinalRect(ItemListener item) {
        Rect r = new Rect();
        ScaleParams scaleParams = mFocus.getParams().getScaleParams();
        r.set(getDstRect(scaleParams.getScaleX(), scaleParams.getScaleY(), item.isScale()));
        // offsetFocusRect(r);
        offsetManualPadding(r, item);
        return r;
    }

    public Rect getDstRect(float scaleX, float scaleY, boolean isScale) {
        FocusRectParams params = mFocus.getFocusParams();
        Rect r = new Rect();
        r.set(params.focusRect());
        mListener.offsetDescendantRectToItsCoords((View) mFocus, r);
        if (isScale) {
            ScalePositionManager.instance().getScaledRect(r, scaleX, scaleY, params.coefX(), params.coefY());
        }
        return r;
    }

    public Rect getClipFocusRect(FocusListener focus) {
        Rect r = new Rect();
        if (focus != null) {
            Rect clipRect = focus.getClipFocusRect();
            if (clipRect != null) {
                r.set(clipRect);
                mListener.offsetDescendantRectToItsCoords((View) focus, r);
            }
        }
        return r;
    }

    /**
     * Get item's Rectangle and transfer to this coordinates
     * (FocusPositionManager).
     * @param ItemListener
     *            , the item.
     * @param isScale
     *            , whether needs scale the item.
     * @return the final rect of this item(View)
     */
    public Rect getAlphaListItemRect(ItemListener item, boolean isScale) {
        Rect r = new Rect();
        try{
            FocusRectParams params = item.getFocusParams();
            r = item.getFocusParams().focusRect();
            if (item instanceof View) {
                View thisView = (View) item;
                while (!(thisView instanceof FocusPositionManager) && thisView != null) {
                    ViewParent parent = thisView.getParent();
                    if (parent instanceof ViewGroup) {
                        ViewGroup group = (ViewGroup) parent;
                        group.offsetDescendantRectToMyCoords((View) thisView, r);
                        thisView = (View) group;
                    } else if (parent == null) {
                        return null;
                    } else {
                        return r;
                    }
                }
            } else {
                return null;
            }

            if (isScale) {
                ScalePositionManager.instance().getScaledRect(r, item.getScaleX(), item.getScaleY(), params.coefX(),
                        params.coefY());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return r;
    }

    public void setSelector(DrawListener selector) {
        mSelector = selector;
    }

    public void setConvertSelector(DrawListener convertSelector) {
        mConvertSelector = convertSelector;
    }

    protected void removeScaleNode(ItemListener item) {
        this.mScaledList.remove(item);
    }

    private void addNode(ItemListener item) {
        if (DEBUG) {
            Log.i(TAG, TAG + ".addNode.item = " + item);
        }

        if (item == null) {
            Log.w(TAG, "addNode: item is null");
            return;
        }
        this.mNodeManager.release();

        if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_STATIC_DRAW) {
            AlphaParams alphaParams = mFocus.getParams().getAlphaParams();
            Interpolator alphaInterpolator = alphaParams.getAlphaInteroplator();

            if (alphaInterpolator == null) {
                alphaInterpolator = new LinearInterpolator();
            }
            if (DEBUG) {
                Log.i(TAG, TAG + ".addNode.mAlphaFrame = " + mAlphaFrame + ", mAlphaFrameRate = " + mAlphaFrameRate);
            }

            AlphaInfo alphaInfo = new AlphaInfo(item, mAlphaFrame, mAlphaFrameRate, alphaParams.getToAlpha(),
                    alphaInterpolator, mSelector, mFocus);

            this.mNodeManager.init(mAlphaList, alphaInfo);
        }

        ScaleParams scaledParams = mFocus.getParams().getScaleParams();
        Interpolator scaleInterpolator = scaledParams.getScaleInterpolator();
        if (scaleInterpolator == null) {
            scaleInterpolator = new LinearInterpolator();
        }
        if (DEBUG) {
            Log.i(TAG, TAG + ".addNode.mScaleFrame = " + mScaleFrame + ", mScaleFrameRate = " + mScaleFrameRate);
        }

        ScaledInfo scaledInfo = new ScaledInfo(item, mScaleFrame, mScaleFrameRate, scaledParams.getScaleX(),
                scaledParams.getScaleY(), scaleInterpolator);

        this.mNodeManager.init(mScaledList, scaledInfo);
    }

    boolean mCurNodeAdded = false;

    protected void addCurNode(ItemListener item) {
        if (DEBUG) {
            Log.i(TAG, TAG + ".addCurNode.item = " + item);
        }

        if (item == null) {
            Log.w(TAG, "addCurNode: item is null");
            return;
        }
        if (mCurNodeManager.mList.size() > 0 && mCurNodeAdded) {// 如果当前节点已经增加了
            return;
        }
        mCurNodeAdded = true;
        this.mCurNodeManager.release();

        ScaledList scaledList = new ScaledList();
        AlphaList alphaList = new AlphaList();

        if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_STATIC_DRAW) {
            AlphaParams alphaParams = mFocus.getParams().getAlphaParams();
            Interpolator alphaInterpolator = alphaParams.getAlphaInteroplator();

            if (alphaInterpolator == null) {
                alphaInterpolator = new LinearInterpolator();
            }

            if (DEBUG) {
                Log.i(TAG, TAG + ".addCurNode.mAlphaFrame = " + mAlphaFrame + ", mAlphaFrameRate = " + mAlphaFrameRate
                        + ".item = " + item);
            }
            AlphaInfo alphaInfo = new AlphaInfo(item, mAlphaFrame, mAlphaFrameRate, alphaParams.getToAlpha(),
                    alphaInterpolator, mSelector, mFocus);

            this.mCurNodeManager.init(alphaList, alphaInfo);
        }

        ScaleParams scaledParams = mFocus.getParams().getScaleParams();
        Interpolator scaleInterpolator = scaledParams.getScaleInterpolator();
        if (scaleInterpolator == null) {
            scaleInterpolator = new LinearInterpolator();
        }
        if (DEBUG) {
            Log.i(TAG, TAG + ".addCurNode.mScaleFrame = " + mScaleFrame + ", mScaleFrameRate = " + mScaleFrameRate
                    + ".item = " + item);
        }
        ScaledInfo scaledInfo = new ScaledInfo(item, mScaleFrame, mScaleFrameRate, scaledParams.getScaleX(),
                scaledParams.getScaleY(), scaleInterpolator);

        this.mCurNodeManager.init(scaledList, scaledInfo);
    }

    protected void removeNode(ItemListener item) {
        this.mNodeManager.remove(item);
    }

    private void addScaledNode(ItemListener item) {
        if (item == null) {
            Log.w(TAG, "addScaledNode: item is null");
            return;
        }

        ScaleParams params = mFocus.getParams().getScaleParams();
        Interpolator scaleInterpolator = params.getScaleInterpolator();
        if (scaleInterpolator == null) {
            scaleInterpolator = new LinearInterpolator();
        }

        // if (item.getScaleX() > 1.0f || item.getScaleY() > 1.0f) {

        this.mScaledList.add(new ScaledInfo(item, mScaleFrame, mScaleFrameRate, params.getScaleX(), params.getScaleY(),
                scaleInterpolator));
        // }
    }

    public void stop() {
        if (mFocus == null) {
            return;
        }
        resetSelector();
        ItemListener item = mFocus.getItem();
        addNode(item);
        // removeScaleNode(item);
        // addScaledNode(item);
    }

    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        if (DEBUG) {
            Log.i(TAG, TAG + ".preOnKeyDown.mFocus = " + mFocus);
        }

        if (mFocus == null) {
            return false;
        }
        return mFocus.preOnKeyDown(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mFocus.onKeyDown(keyCode, event);
    }

    public void reset() {
        this.mFrame = 1;
        this.mScaleFrame = 1;
        this.mFocusFrame = 1;
        mAlphaFrame = 1;
        mListener.invalidate();
    }

    public void reset(FocusListener focus) {
        Log.d(TAG, "reset focus = " + focus);
        if (focus != null) {
            this.mLastFocus = mFocus;
            this.mFocus = focus;
            if (this.mFocusMode == FOCUS_STATIC_DRAW) {
                this.mFocusFrameRate = 0;
            } else {
                this.mFocusFrameRate = mFocus.getParams().getFocusParams().getFocusFrameRate();
            }

            this.mScaleFrameRate = mFocus.getParams().getScaleParams().getScaleFrameRate();
            mAlphaFrameRate = mFocus.getParams().getAlphaParams().getAlphaFrameRate();
            reset();
        } else {
            Log.e(TAG, "reset focus is null");
        }

        if (DEBUG) {
            Log.i(TAG, TAG + ".reset.mFrame = " + mFrame + ".mScaleFrame = " + mScaleFrame + ".mFocusFrame = "
                    + mFocusFrame + ".mAlphaFrame = " + mAlphaFrame + ".mFocusFrameRate = " + mFocusFrameRate
                    + ".mScaleFrameRate = " + mScaleFrameRate + ".mAlphaFrameRate = " + mAlphaFrameRate);
        }
    }

    void offsetManualPadding(Rect r, ItemListener item) {
        Rect rPadding = item.getManualPadding();
        if (rPadding != null) {
            r.left += rPadding.left;
            r.right += rPadding.right;
            r.top += rPadding.top;
            r.bottom += rPadding.bottom;
        }
    }

    public boolean isFinished() {
        return mFrame > Math.max(mFocusFrameRate, mScaleFrameRate);
    }

    public boolean canDrawNext() {
        return true;
    }

    public int getFrame() {
        return mFrame;
    }

    public int getTotalFrame() {
        return Math.max(mFocusFrameRate, mScaleFrameRate);
    }

    public int getFocusFrameRate() {
        return mFocusFrameRate;
    }

    public int getCurFocusFrame() {
        return mFocusFrame;
    }

    public void resetSelector() {

    }

    public DrawListener getSelector() {
        return mSelector;
    }

    public DrawListener getConvertSelector() {
        return mConvertSelector;
    }

    class NodeManager {

        List<BaseList> mList = new LinkedList<BaseList>();

        public void init(BaseList list, Info info) {
            synchronized (this) {
                this.mList.add(list);
                list.add(info);
                if (DEBUG) {
                    Log.d(TAG, "NodeManager list size = " + this.mList.size());
                }
            }

            mListener.invalidate();
        }

        public void clear() {
            synchronized (this) {
                for (int index = 0; index < this.mList.size(); index++) {
                    BaseList list = this.mList.get(index);
                    list.clear();
                }
                mList.clear();
            }
        }

        public void release() {
            synchronized (this) {
                mList.clear();
            }
        }

        public void remove(ItemListener item) {
            if (DEBUG) {
                Log.i(TAG, TAG + ".NodeManager.remove.item 1= " + item + ".mList.size() = " + this.mList.size());
            }

            for (int index = 0; index < this.mList.size(); index++) {
                BaseList list = this.mList.get(index);
                list.remove(item);
            }

            if (DEBUG) {
                Log.i(TAG, TAG + ".NodeManager.remove.item 2= " + item + ".mList.size() = " + this.mList.size());
            }
        }

        public void preDrawOut(Canvas canvas) {
            for (int index = 0; index < this.mList.size(); index++) {
                BaseList list = mList.get(index);
                list.preDrawOut(canvas);
            }
        }

        public void drawOut(Canvas canvas) {
            if (DEBUG) {
                Log.i(TAG, TAG + ".drawOut.mList.size() = " + mList.size());
            }
            for (int index = 0; index < this.mList.size(); index++) {
                BaseList list = this.mList.get(index);
                list.drawOut(canvas);
            }
        }

        public void postDrawOut(Canvas canvas) {
            for (int index = 0; index < this.mList.size(); index++) {
                BaseList list = mList.get(index);
                list.postDrawOut(canvas);
            }
        }
    }

    class ScaledList extends BaseList {

        @Override
        protected void drawOut(Info info, Canvas canvas) {
            if (DEBUG) {
                Log.i(TAG, TAG + ".ScaledList.drawOut.info.getFrame() = " + info.getFrame() + "info = " + info);
            }

            if (info.getFrame() <= 0) {
                return;
            }
            if (info instanceof ScaledInfo) {
                ScaledInfo scaledInfo = (ScaledInfo) info;
                float itemDiffScaleXValue = scaledInfo.getScaleX() - 1.0f;
                float itemDiffScaleYValue = scaledInfo.getScaleY() - 1.0f;
                float coef = (float) (scaledInfo.getFrame() - 1) / scaledInfo.getFrameRate();
                coef = scaledInfo.getInterpolator().getInterpolation(coef);
                float dstScaleX = 1.0f + itemDiffScaleXValue * coef;
                float dstScaleY = 1.0f + itemDiffScaleYValue * coef;
                scaledInfo.getItem().setScaleX(dstScaleX);
                scaledInfo.getItem().setScaleY(dstScaleY);

                if (DEBUG) {
                    Log.d(TAG, "drawUnscale: dstScaleX = " + dstScaleX + ", dstScaleY = " + dstScaleY);
                }
                scaledInfo.subFrame();
            } else {
                info.subFrame();
                return;
            }

        }
    }

    class AlphaList extends BaseList {

        @Override
        protected void drawOut(Info info, Canvas canvas) {
            if (info.getFrame() <= 0) {
                return;
            }

            if (info instanceof AlphaInfo) {
                AlphaInfo alphaInfo = (AlphaInfo) info;
                float currentAlpha = alphaInfo.getAlpha();
                float coef = (float) (alphaInfo.getFrame() - 1) / alphaInfo.getFrameRate();
                coef = alphaInfo.getInterpolator().getInterpolation(coef);
                float dstAlpha = currentAlpha * coef;
                Rect rect = getAlphaListItemRect(info.getItem(), true);
                if (rect != null) {
                    offsetManualPadding(rect, info.getItem());
                }
                Rect clipFocusRect = getClipFocusRect(alphaInfo.mLastFocus);
                if (mLastItem != info.mItem) {
                    drawFocus(canvas, rect, dstAlpha, alphaInfo.mSelector, clipFocusRect);
                }
                if (DEBUG) {
                    Log.d(TAG, "drawOutAlpha: Alpha = " + dstAlpha);
                }
                info.subFrame();
            } else {
                info.subFrame();
                return;
            }

        }
    }

    private abstract class BaseList {

        List<Info> mList = new LinkedList<Info>();
        Object lock = new Object();
        boolean mIsBreak = false;

        public void add(Info info) {
            setBreak(true);
            synchronized (this) {
                this.mList.add(info);
                if (DEBUG) {
                    Log.d(TAG, "List:add list size = " + this.mList.size());
                }
            }

            mListener.invalidate();
        }

        public void clear() {
            if (DEBUG) {
                Log.i(TAG, TAG + ".BaseList.clear()");
            }
            synchronized (this) {
                mList.clear();
            }
        }

        public void remove(ItemListener item) {
            for (int index = 0; index < this.mList.size(); index++) {
                Info info = this.mList.get(index);
                if (DEBUG) {
                    Log.i(TAG, TAG + ".BaseList.remove.this.item = " + info.getItem());
                }
                if (info.getItem() == item) {
                    this.mList.remove(index);
                    break;
                }
            }
            if (DEBUG) {
                Log.i(TAG, TAG + ".BaseList.remove.item = " + item + ".mList.size() = " + this.mList.size());
            }
        }

        public void preDrawOut(Canvas canvas) {
            for (int index = 0; index < this.mList.size(); index++) {
                Info info = mList.get(index);
                if (info.getFrame() >= 0) {
                    drawBeforeFocus(info.getItem(), canvas);
                }
            }
        }

        public void drawOut(Canvas canvas) {
            setBreak(false);
            synchronized (this) {
                while (this.mList.size() > 0 && !isBreak()) {
                    Info info = this.mList.get(0);
                    if (info.isFinished()) {
                        this.mList.remove(0);
                    } else {
                        break;
                    }
                }

                if (DEBUG) {
                    Log.i(TAG, TAG + ".BaseList.drawOut.mList.size = " + mList.size());
                }
                for (int index = 0; index < this.mList.size() && !isBreak(); index++) {
                    drawOut(this.mList.get(index), canvas);
                }

                if (this.mList.size() > 0) {
                    mListener.invalidate();
                }
            }
        }

        public void postDrawOut(Canvas canvas) {
            for (int index = 0; index < this.mList.size(); index++) {
                Info info = mList.get(index);
                if (info.getFrame() >= 0) {
                    drawAfterFocus(info.getItem(), canvas);
                }
            }
        }

        protected void setBreak(boolean isBreak) {
            synchronized (lock) {
                this.mIsBreak = isBreak;
            }
        }

        protected boolean isBreak() {
            synchronized (lock) {
                return this.mIsBreak;
            }
        }

        protected abstract void drawOut(Info info, Canvas canvas);

    }

    private abstract class Info {

        protected ItemListener mItem;
        protected int mFrame;
        protected int mFrameRate;
        protected Interpolator mInterpolator;

        public boolean isFinished() {
            return getFrame() <= 0 && mItem.isFinished();
        }

        public ItemListener getItem() {
            return this.mItem;
        }

        public void subFrame() {
            this.mFrame--;
        }

        public int getFrame() {
            return this.mFrame;
        }

        public int getFrameRate() {
            return this.mFrameRate;
        }

        public Interpolator getInterpolator() {
            return this.mInterpolator;
        }
    }

    class AlphaInfo extends Info {

        DrawListener mSelector;
        float mAlpha;
        FocusListener mLastFocus = null;

        public AlphaInfo(ItemListener item, int frame, int frameRate, float alpha, Interpolator alphaInterpolator,
                DrawListener selector, FocusListener lastFocus) {
            this.mItem = item;
            AlphaParams alphaParams = mFocus.getParams().getAlphaParams();
            float fromAlpha = alphaParams.getFromAlpha();
            float toAlpha = alphaParams.getToAlpha();
            if (toAlpha > 1.0f || fromAlpha < 0.0f) {
                this.mFrame = -1;
            } else {
                this.mFrame = frame - 1;
            }

            this.mFrameRate = frameRate;
            this.mAlpha = alpha;
            this.mInterpolator = alphaInterpolator;
            this.mSelector = selector;
            this.mLastFocus = lastFocus;
        }

        public void setAlpha(float dstAlpha) {
            mAlpha = dstAlpha;
        }

        public float getAlpha() {
            return mAlpha;
        }
    }

    class ScaledInfo extends Info {

        float mScaleX;
        float mScaleY;

        public ScaledInfo() {

        }

        public ScaledInfo(ItemListener item, int frame, int frameRate, float scaleX, float scaleY,
                Interpolator scaleInterpolator) {
            this.mItem = item;
            if (item.getScaleX() <= 1.0f && item.getScaleY() <= 1.0f) {
                this.mFrame = -1;
            } else {
                this.mFrame = frame - 1;
            }

            this.mFrameRate = frameRate;
            this.mScaleX = scaleX;
            this.mScaleY = scaleY;
            this.mInterpolator = scaleInterpolator;
        }

        public float getScaleX() {
            return this.mScaleX;
        }

        public float getScaleY() {
            return this.mScaleY;
        }
    }

    public void setSelectorInterpolator(Interpolator selectorPolator) {
        this.mSelectorPolator = selectorPolator;
    }

    public Interpolator getSelectorInterpolator() {
        return mSelectorPolator;
    }

    public boolean clipCanvasIsNeeded(Rect focusRect, Rect clipRect) {
        if (SystemProUtils.getGlobalFocusMode() != PositionManager.FOCUS_STATIC_DRAW) {
            return false;
        }
        if (focusRect.left >= clipRect.left && focusRect.right <= clipRect.right && focusRect.top >= clipRect.top
                && focusRect.bottom <= clipRect.bottom) {
            return false;
        } else {
            return true;
        }
    }
}

package com.yunos.tvtaobao.takeoutbundle.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by haoxiang on 2017/12/13.  // 此view 会收集所有focusable View 根据方向进行focus 移动传递
 */

public class InnerFocusDispatchFrameLayout extends FrameLayout implements DeepListener, ItemListener {

    protected static final String TAG = "InnerFocusDispatch";
    protected static final boolean DEBUG = true;


    public InnerFocusDispatchFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setClickable(true);
    }

    public InnerFocusDispatchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
    }

    public InnerFocusDispatchFrameLayout(Context context) {
        super(context);
    }

    private final static int compareRadio = 2;

    protected Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();

    private List<View> allFocusable = new ArrayList<>();
    private View mFocusCurrent;
    private ArrayList<View> unFocus = new ArrayList<>();
    private OnFocusDropListener mOnFocusDropListener;
    private boolean mIsGainFocus = false;
    // 用于获取最合适 偏移位置 下一个focus；
    private int mDirection = 0;
    private boolean mShouldDropKeyEvent = false;
    private boolean isInitFocusDropListener;


    /**
     * 排序获取 最合适的下一个 focus位置
     */
    private Comparator<View> comparator = new Comparator<View>() {
        Rect rectL = new Rect();
        Rect rectR = new Rect();
        Rect rectFocus = new Rect();

        @Override
        public int compare(View o1, View o2) {
            if (mFocusCurrent == null) return 0;
            rectL.set(0, 0, 0, 0);
            offsetDescendantRectToMyCoords(o1, rectL);
            rectR.set(0, 0, 0, 0);
            offsetDescendantRectToMyCoords(o2, rectR);
            rectFocus.set(0, 0, 0, 0);
            offsetDescendantRectToMyCoords(mFocusCurrent, rectFocus);

            int i;      //直接方向  距离偏移
            int j;      //垂直方向  距离偏移
            int m;      //角度权重
            int suml;
            int sumr;
            switch (mDirection) {
                case FOCUS_LEFT:
                    i = (rectL.left + o1.getWidth()) + -rectFocus.left;
                    j = (rectL.top + rectL.top + o1.getHeight()) / 2 - (rectFocus.top + rectFocus.top + mFocusCurrent.getHeight()) / 2;
                    m = (rectL.left - rectFocus.left >= 0) ? Integer.MAX_VALUE >>> 16 : 0;
                    suml = (i > 0 ? i : -i) * compareRadio + (j > 0 ? j : -j) + m;
                    i = rectR.left + o2.getWidth() - rectFocus.left;
                    j = (rectR.top + rectR.top + o2.getHeight()) / 2 - (rectFocus.top + rectFocus.top + mFocusCurrent.getHeight()) / 2;
                    m = (rectR.left - rectFocus.left >= 0) ? Integer.MAX_VALUE >>> 16 : 0;
                    sumr = (i > 0 ? i : -i) * compareRadio + (j > 0 ? j : -j) + m;
                    return suml - sumr;
                case FOCUS_RIGHT:
                    i = rectL.left - (rectFocus.left + mFocusCurrent.getWidth());
                    j = (rectL.top + rectL.top + o1.getHeight()) / 2 - (rectFocus.top + rectFocus.top + mFocusCurrent.getHeight()) / 2;
                    m = rectL.left + o1.getWidth() - rectFocus.left - mFocusCurrent.getWidth() <= 0 ? Integer.MAX_VALUE >>> 16 : 0;
                    suml = (i > 0 ? i : -i) * compareRadio + (j > 0 ? j : -j) + m;
                    i = rectR.left - (rectFocus.left + mFocusCurrent.getWidth());
                    j = (rectR.top + rectR.top + o2.getHeight()) / 2 - (rectFocus.top + rectFocus.top + mFocusCurrent.getHeight()) / 2;
                    m = rectR.left + o2.getWidth() - rectFocus.left - mFocusCurrent.getWidth() <= 0 ? Integer.MAX_VALUE >>> 16 : 0;
                    sumr = (i > 0 ? i : -i) * compareRadio + (j > 0 ? j : -j) + m;
                    return suml - sumr;
                case FOCUS_UP:
                    i = (rectL.left + rectL.left + o1.getWidth()) / 2 - (rectFocus.left + mFocusCurrent.getWidth() + rectFocus.left) / 2;
                    j = rectL.top + o1.getHeight() - rectFocus.top;
                    m = (rectL.top - rectFocus.top > 0) ? Integer.MAX_VALUE >>> 16 : 0;
                    suml = (i > 0 ? i : -i) + (j > 0 ? j : -j) * compareRadio + m;
                    i = (rectR.left + rectR.left + o2.getWidth()) / 2 - (rectFocus.left + mFocusCurrent.getWidth() + rectFocus.left) / 2;
                    j = rectR.top + o2.getHeight() - rectFocus.top;
                    m = (rectR.top - rectFocus.top > 0) ? Integer.MAX_VALUE >>> 16 : 0;
                    sumr = (i > 0 ? i : -i) + (j > 0 ? j : -j) * compareRadio + m;
                    return suml - sumr;
                case FOCUS_DOWN:
                    i = (rectL.left + rectL.left + o1.getWidth()) / 2 - (rectFocus.left + mFocusCurrent.getWidth() + rectFocus.left) / 2;
                    j = rectL.top - (rectFocus.top + mFocusCurrent.getHeight());
                    m = rectL.top + o1.getHeight() - rectFocus.top - mFocusCurrent.getHeight() < 0 ? Integer.MAX_VALUE >>> 16 : 0;
                    suml = (i > 0 ? i : -i) + (j > 0 ? j : -j) * compareRadio + m;
                    i = (rectR.left + rectR.left + o2.getWidth()) / 2 - (rectFocus.left + mFocusCurrent.getWidth() + rectFocus.left) / 2;
                    j = rectR.top - (rectFocus.top + mFocusCurrent.getHeight());
                    m = rectR.top + o2.getHeight() - rectFocus.top - mFocusCurrent.getHeight() < 0 ? Integer.MAX_VALUE >>> 16 : 0;
                    sumr = (i > 0 ? i : -i) + (j > 0 ? j : -j) * compareRadio + m;
                    return suml - sumr;
            }
            return 0;
        }
    };

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mOnFocusDropListener != null && !isInitFocusDropListener && !mIsGainFocus) {        // 为了统一mOnFocusDropListener事件处理
            isInitFocusDropListener = true;
            mOnFocusDropListener.onFocusLeave(allFocusable.toArray(new View[allFocusable.size()]));
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        allFocusable.clear();
        reStoreFocusableViews(this);
        if (!allFocusable.contains(mFocusCurrent)) {
            mFocusCurrent = allFocusable.size() == 0 ? null : allFocusable.get(0);
        }
    }


    // 获取所以可以focus 对象
    private void reStoreFocusableViews(View view) {
        if (view.getVisibility() != VISIBLE) {
            return;
        }
        if (view.isFocusable() && view != this) {
            allFocusable.add(view);
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                reStoreFocusableViews(child);
            }
        }
    }


    // --------------------------------DeepListener and ItemListener Imp-----------------------------//
    @Override
    public void drawBeforeFocus(Canvas canvas) {
    }

    @Override
    public void drawAfterFocus(Canvas canvas) {
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void setScaleX(float scaleX) {
    }

    @Override
    public float getScaleX() {
        return 0;
    }

    @Override
    public void setScaleY(float scaleY) {
    }

    @Override
    public float getScaleY() {
        return 0;
    }

    @Override
    public boolean isScale() {
        return false;
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public boolean canDeep() {
        return true;
    }

    @Override
    public boolean hasDeepFocus() {
        return true;
    }

    @Override
    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mIsGainFocus = gainFocus;
        if (mIsGainFocus) {
            if (mOnFocusDropListener != null) {
                if (mFocusCurrent == null) {
                    // 获取focus 位置 默认第一项
                    allFocusable.clear();
                    reStoreFocusableViews(this);
                    mFocusCurrent = allFocusable.size() == 0 ? null : allFocusable.get(0);
                }
                unFocus.clear();
                unFocus.addAll(allFocusable);
                if (mFocusCurrent != null) {
                    unFocus.remove(mFocusCurrent);
                }
                mOnFocusDropListener.onFocusEnter(mFocusCurrent, unFocus.toArray(new View[unFocus.size()]));
            }
        } else {
            if (mOnFocusDropListener != null) {
                mOnFocusDropListener.onFocusLeave(allFocusable.toArray(new View[allFocusable.size()]));
            }
        }

    }

    @Override
    public void onItemSelected(boolean selected) {
    }

    @Override
    public void onItemClick() {
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        getFocusedRect(r);
        mFocusRectparams.set(r, 0.5f, 0.5f);
        return mFocusRectparams;
    }

    @Override
    public boolean canDraw() {
        return false;
    }

    @Override
    public boolean isAnimate() {
        return false;
    }

    @Override
    public ItemListener getItem() {
        return this;
    }

    @Override
    public Params getParams() {
        return mParams;
    }

    /**
     * 不需要上层提供二次分发focus 自己实现二次分发
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        // mLastDeep = null;
        Log.d(TAG, "preOnKeyDown keyCode = " + keyCode);
        View find = null;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:

                find = findNestFocus(FOCUS_LEFT);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:

                find = findNestFocus(FOCUS_RIGHT);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                find = findNestFocus(FOCUS_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                find = findNestFocus(FOCUS_UP);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                break;
            default:
                break;
        }

        if (find == mFocusCurrent || find == null) {  // 在此viewGroup 中没有找到下一个可以接受到 keFocusable 的对象
            mShouldDropKeyEvent = true;
            return false;
        } else {
            mShouldDropKeyEvent = false;
            mFocusCurrent = find;
            unFocus.clear();
            unFocus.addAll(allFocusable);
            if (mFocusCurrent != null) {
                unFocus.remove(mFocusCurrent);
            }
            mOnFocusDropListener.onFocusEnter(mFocusCurrent, unFocus.toArray(new View[unFocus.size()]));
            return true;
        }
    }

    public boolean isScrolling() {
        return false;
    }

    @Override
    public boolean isFocusBackground() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyDown(keyCode, event);
        if (mShouldDropKeyEvent) {
            return ret;
        } else {
            return true;
        }
    }

    // --------------------------------DeepListener and ItemListener end-----------------------------//

    @Override
    public boolean performClick() {
        if (mOnFocusDropListener != null) {
            unFocus.clear();
            unFocus.addAll(allFocusable);
            if (mFocusCurrent != null) {
                unFocus.remove(mFocusCurrent);
            }
            mOnFocusDropListener.onFocusClick(mFocusCurrent, unFocus.toArray(new View[unFocus.size()]));
        }
        return true;
    }

    @Override
    public void onFocusStart() {
    }

    @Override
    public void onFocusFinished() {
    }

    @Override
    public Rect getClipFocusRect() {
        return null;
    }




    // 自带带focusFinder查找失败 阿里库里面带focusFinder也查找失败； 原因应该是focus遗留在父Group中； so手动自己查找下级focusView
    private View findNestFocus(int direction) {

        if (mFocusCurrent == null || allFocusable.size() == 0) return null;

        Iterator<View> iterator = allFocusable.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getVisibility() != VISIBLE) {
                iterator.remove();
            }
        }

        mDirection = direction;
        Collections.sort(allFocusable, comparator);
        return allFocusable.size() > 0 ? allFocusable.get(0) : null;
    }


    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views == null) {
            return;
        }
        if (!isFocusable()) {
            return;
        }
        if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE && isInTouchMode()
                && !isFocusableInTouchMode()) {
            return;
        }
        views.add(this);
    }


    @Override
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }


    // --------------------------------------------------------------API-----------------------------------------------------------//


    /**
     * 如果有需要 重新获取focused View
     */
    public void notifyFocusChangeIfNeed() {

        allFocusable.clear();
        reStoreFocusableViews(this);
        View old = mFocusCurrent;
        if (!allFocusable.contains(mFocusCurrent)) {
            mFocusCurrent = allFocusable.size() == 0 ? null : allFocusable.get(allFocusable.size() - 1);
        }


        // 触发focus变化
        if (old != mFocusCurrent && mOnFocusDropListener != null) {
            if (mIsGainFocus) {
                unFocus.clear();
                unFocus.addAll(allFocusable);
                if (mFocusCurrent != null) {
                    unFocus.remove(mFocusCurrent);
                }
                mOnFocusDropListener.onFocusEnter(mFocusCurrent, unFocus.toArray(new View[unFocus.size()]));
            } else {
                mOnFocusDropListener.onFocusLeave(unFocus.toArray(new View[unFocus.size()]));
            }
        }
    }

    public void setOnFocusDropListener(OnFocusDropListener listener) {
        this.mOnFocusDropListener = listener;
    }


    public interface OnFocusDropListener {
        // 此group 失去焦点
        void onFocusLeave(View... unFocusView);

        // focusable View 第一次被选中 或者 此group内跳转
        void onFocusEnter(View focusView, View... unFocusView);

        // focusable View 点击
        void onFocusClick(View focus, View... unFocusView);

    }

    @Override
    public boolean isInEditMode() {
        return true;
    }
}

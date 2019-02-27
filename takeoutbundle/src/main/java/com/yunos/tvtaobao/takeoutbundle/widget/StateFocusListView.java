package com.yunos.tvtaobao.takeoutbundle.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;

import java.util.ArrayList;

/**
 * Created by haoxiang on 2017/12/15.
 * // 使用时 依赖外部 嵌套 ScrollView  完全平铺ListView;
 */
public class StateFocusListView extends ListView implements DeepListener, ItemListener {

    protected static final String TAG = "StateFocusListView";
    protected static final boolean DEBUG = false;
    private boolean mOnFocused = false;

    public StateFocusListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public StateFocusListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StateFocusListView(Context context) {
        super(context);
    }

    protected Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    protected FocusRectParams mFocusRectparams = new FocusRectParams();
    private int mFocusCurrent;
    private OnItemFocusListener mOnItemFocusListener;
    private boolean mIsGainFocus = false;
    private Rect mRect = new Rect();

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

        mIsGainFocus = gainFocus;
        if (gainFocus) {
            if (mOnItemFocusListener != null) {
                mOnItemFocusListener.onFocusEnter(mFocusCurrent, true);
            }
        } else {
            if (mOnItemFocusListener != null) {
                mOnItemFocusListener.onFocusLeave(mFocusCurrent, false);
            }
        }

        Log.i(TAG, TAG + "DeepListener = onFocusChanged innner    " + gainFocus + "  " + direction + "   " + previouslyFocusedRect + this);
    }


    // --------------------------------DeepListener and ItemListener imp-----------------------------//
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
        if (gainFocus) {
            if (mOnItemFocusListener != null) {
                mOnItemFocusListener.onFocusEnter(mFocusCurrent,mIsGainFocus);
            }
        } else {
            if (mOnItemFocusListener != null) {
                mOnItemFocusListener.onFocusLeave(mFocusCurrent, mIsGainFocus);
            }
        }
        Log.i(TAG, TAG + "DeepListener = onFocusDeeped innner    " + gainFocus + "  " + direction + "   " + previouslyFocusedRect + this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 8, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onItemSelected(boolean selected) {
        Log.i(TAG, TAG + "DeepListener = onItemSelected        " + this);
    }

    @Override
    public void onItemClick() {
        Log.i(TAG, TAG + "DeepListener = onItemClick        " + this);
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

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "preOnKeyDown keyCode = " + keyCode);
        int find = -1;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                find = -1;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                find = -1;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mFocusCurrent + 1 < (getAdapter() == null ? 0 : getAdapter().getCount())) {      // 下拉到底部时 不能释放焦点
                    find = mFocusCurrent + 1;
                } else {
                    find = mFocusCurrent;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mFocusCurrent - 1 >= 0) {
                    find = mFocusCurrent - 1;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:

                break;
            default:
                break;
        }

        if (find == -1) {                             // 在此viewGroup 中没有找到下一个可以接受到 keFocusable 的对象 可能需要释放 focus
            return false;
        } else {
            if (find != mFocusCurrent) {
                if (mOnItemFocusListener != null) {
                    mOnItemFocusListener.onFocusLeave(mFocusCurrent, true);
                }
                mFocusCurrent = find;
                if (mOnItemFocusListener != null) {
                    mOnItemFocusListener.onFocusEnter(mFocusCurrent,true);
                    ensureFocusToCenterLocation();
                }
            }
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
        // 自行消化掉方向键输入
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // 自行消化掉方向键输入
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                return true;
        }
        return super.onKeyUp(keyCode, event);
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

    // --------------------------------DeepListener and ItemListener end-----------------------------//

    // 焦点位置滚动到选中位置
    private void ensureFocusToCenterLocation() {
        mRect.set(0, 0, 0, 0);
        ScrollView scrollView = (ScrollView) getParent();
        View view = getChildAt(mFocusCurrent);

        if (view == null) {
            return;
        }

        scrollView.offsetDescendantRectToMyCoords(view, mRect);
        scrollView.smoothScrollTo(0, mRect.top - (scrollView.getHeight() / 2 - view.getHeight() / 2));
    }


    // --------------------------------------------------------------API-----------------------------------------------------------//

    // 设置焦点相关事件
    public void setOnItemFocusListener(OnItemFocusListener listener) {
        this.mOnItemFocusListener = listener;
    }

    // 得到当前focusIndex
    public int getFocusedItemIndex(){
        return mFocusCurrent;
    }

    // 设置隐试焦点选中   position 焦点位置  （处理了半选中状态）
    public void setHintFocusedItem(int position) {
        ListAdapter adapter = getAdapter();
        if (adapter == null) return;
        position = position < 0 ? 0 : position;
        position = position < adapter.getCount() ? position : adapter.getCount() - 1;
        if (position == mFocusCurrent) return;
        int oldFocusPositon = mFocusCurrent;
        mFocusCurrent = position;
        if (mOnItemFocusListener != null) {
            mOnItemFocusListener.onFocusLeave(oldFocusPositon, true);
            mOnItemFocusListener.onFocusEnter(mFocusCurrent, false);
        }
        ensureFocusToCenterLocation();
    }

    public interface OnItemFocusListener {
        // 焦点离开Item  index 焦点位置  listHasFocus 焦点是否留在此listView中
        void onFocusLeave(int index, boolean listHasFocus);
        // 焦点进入Item  index 焦点位置  listHasFocus 焦点是否留在此listView中
        void onFocusEnter(int index, boolean listHasFocus);
    }

}

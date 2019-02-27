package com.yunos.tvtaobao.takeoutbundle.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;

import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.listener.DeepListener;

import java.util.ArrayList;

/**
 * Created by haoxiang on 2017/12/14
 * //跳过headView and footView 被选中为focus
 * //可以滑动到指定位置
 * //加入过度滑动监听
 * //修改未layout但focus已经获得 hasDeepFocus()View焦点方法触发；
 */

public class SelectFocusListView extends FocusListView {

    private int mMoveTarget = -1;
    public OnPageOverScrollListener mOnPageOverScrollListener;
    //fixme mShouldDropKeyEvent设置有问题，当快速滑动到底后值如果按键非方向键则一直为true，导致back键无法退出
    private boolean mShouldDropKeyEvent = false;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMoveTarget == -1) return;
            if (mSelectedPosition > mMoveTarget) {
                if (moveUp()) {
                    postDelayed(mRunnable, 50);
                } else {
                    ;
                    mMoveTarget = -1;
                }
            } else if (mSelectedPosition < mMoveTarget) {
                if (moveDown()) {
                    postDelayed(mRunnable, 50);
                } else {
                    mMoveTarget = -1;
                }
            }
        }
    };

    public SelectFocusListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SelectFocusListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectFocusListView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (hasFocus() || hasDeepFocus()) {
            if (getLeftScrollDistance() == 0) {
                reset();
            }
        }

        if (getChildCount() > 0) {
            if (hasFocus()) {
                if (getSelectedView() instanceof DeepListener) {
                    mDeep = (DeepListener) getSelectedView();
                    if (mDeep != null && mDeep.canDeep()) {
                        mDeep.onFocusDeeped(true, FOCUS_LEFT, null);
                        reset();
                    }
                }
            }
        }
    }

    /**
     * 防止 head 或者 foot 被选中为focus对象  可以移植 到setSelect()
     *
     * @return
     */
    @Override
    public void setSelection(int position) {
        // fixed select index
        position = position >= getHeaderViewsCount() ? position : getHeaderViewsCount();
        position = position < mItemCount - getFooterViewsCount() ? position : mItemCount - getFooterViewsCount() - 1;
        super.setSelection(position);
    }


    // fixed 小米1 盒子bug 第一项条目停留两次;
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getSelectedItemPosition() == 0) {
                        try {
                            setSelection(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 500);
        }
    }

    /**
     * 跳过 head 或者 foot 被选中为focus对象
     *
     * @return
     */
    @Override
    protected boolean moveUp() {
        return mSelectedPosition > getHeaderViewsCount() && super.moveUp();
    }

    /**
     * 防止 head 或者 foot 被选中为focus对象
     *
     * @return
     */
    @Override
    protected boolean moveDown() {
        return mSelectedPosition < mItemCount - getFooterViewsCount() - 1 && super.moveDown();
    }

    /**
     * 防止 head 或者 foot 被选中为focus对象
     *
     * @return
     */
    @Override
    public View getSelectedView() {

        // fixed select index
        mSelectedPosition = mSelectedPosition >= getHeaderViewsCount() ? mSelectedPosition : getHeaderViewsCount();
        mSelectedPosition = mSelectedPosition < mItemCount - getFooterViewsCount() ? mSelectedPosition : mItemCount - getFooterViewsCount() - 1;

        if (mItemCount > 0 && mSelectedPosition >= 0) {
            lab:
            for (int i = mSelectedPosition - getFirstPosition(); ; i++) {
                View index = getChildAt(i);
                if (index == null) return null;
                // 跳过头部head view 被选中
                for (int j = 0; j < getHeaderViewsCount(); j++) {
                    if (index == getHeaderView(j)) {
                        continue lab;
                    }
                }

                for (int j = 0; j < getFooterViewsCount(); j++) {
                    if (index == mFooterViewInfos.get(j).view) {
                        continue lab;
                    }
                }

//                // fixed select index
//                if (getSelectedItemPosition() != (getFirstPosition() + i)) {
//                    setSelection((getFirstPosition() + i));
//                }

                // mSelectedPosition

                return index;
            }
        } else {
            return null;
        }
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        super.addFocusables(views, direction, focusableMode);
        for (int i = 0; i < getHeaderViewsCount(); i++) {
            View view = getHeaderView(i);
            if (views.contains(view)) {
                views.remove(view);
            }
        }
    }

    /**
     * 防止 head 或者 foot 被选中为focus对象
     * 实现滑动到 底部或者顶部 监听
     *
     * @return
     */
    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        mShouldDropKeyEvent = false;
        boolean ret;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                ret = mSelectedPosition < mItemCount - getFooterViewsCount() - 1 && super.preOnKeyDown(keyCode, event);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                ret = mSelectedPosition > getHeaderViewsCount() && super.preOnKeyDown(keyCode, event);
                break;
            default:
                ret = super.preOnKeyDown(keyCode, event);
                break;
        }

        if (ret) return true;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mOnPageOverScrollListener != null) {
                    mOnPageOverScrollListener.onPageReachBottom();
                }
                mShouldDropKeyEvent = true;
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mOnPageOverScrollListener != null) {
                    mOnPageOverScrollListener.onPageReachTop();
                }
                mShouldDropKeyEvent = true;
                return true;
        }
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mShouldDropKeyEvent) {
            mShouldDropKeyEvent = false;

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mShouldDropKeyEvent) {
            mShouldDropKeyEvent = false;
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }


    // --------------------------------------------------------------API-----------------------------------------------------------//

    /**
     * 加入移动到某一位置支持 最次办法 其他方法没实现
     *
     * @param position
     */
    public void smoothMoveToPosition(int position) {
        if (mMoveTarget != position) {
            mMoveTarget = position;
            post(mRunnable);
        }
    }

    /**
     * page 滑动到底部监听
     *
     * @param lisntener
     */
    public void setOnPageOverScrollLisntener(OnPageOverScrollListener lisntener) {
        this.mOnPageOverScrollListener = lisntener;
    }

    public interface OnPageOverScrollListener {
        void onPageReachBottom();

        void onPageReachTop();
    }


}

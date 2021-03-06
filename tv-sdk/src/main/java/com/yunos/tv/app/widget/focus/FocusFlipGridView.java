package com.yunos.tv.app.widget.focus;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;

import com.yunos.tv.app.widget.FlipGridView;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.DeepListener;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.lib.SystemProUtils;

import java.util.ArrayList;

/**
 * 可进行focus的弹性间距GridView的控件
 * 1.是针对本身居中滚动
 * 2.针对按键在特点的情况下做了一些限制（只能单个方向上滚动，也就是如果正在向下滚动不能向上按键需要在preOnKey里面做限制）
 * 注：目前只支持单个headerView，不支持多个跟footerView
 * 在滚动的过程中重新layout是无效的
 * @author tim
 */
public class FocusFlipGridView extends FlipGridView implements FocusListener, DeepListener, ItemListener {

    private final boolean DEBUG = false;
    private final static String TAG = "FocusFlipGridView";
    private final boolean DYNAMIC_ADD_CHILD_VIEW = true;//是否在滚动的时候添加列表的childView,
    //优点：提升速度，缺点：因为view的位置都是通过计算取得的所以会有使用上限制
    protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    private FocusRectParams mFocusRectparams = new FocusRectParams();
    protected Rect mClipFocusRect = new Rect();

    private boolean mIsAnimate = true;
    private int mDistance = -1;
    boolean mDeepFocus = false;
    private boolean mIsFirstLayout = true;//是否为首次布局
    private boolean mFirstAnimDone = true;//是否正在首次动画
    private OnFocusFlipGridViewListener mOnFocusFlipGridViewListener;//监听器
    private OutAnimationRunnable mOutAnimationRunnable;//出场的动画
    private boolean mNeedResetParam = false;//是否需要对focus区域相关参数的重置，因为reset()被放在layout完成的回调里面
    private int mOnKeyDirection = FOCUS_DOWN;//此时的按键的键值，为了当进入headerView的时候支持findFocus接口
    private Rect mPreFocusRect = new Rect();//上次focus的区域，为了当进入headerView的时候支持findFocus接口
    private boolean mCenterFocus = true;//是否需要居中显示focus框
    private ItemSelectedListener mItemSelectedListener;
    private boolean mNeedAutoSearchFocused = true;//只有第一次需要自动查找，后续focus的时候只需要使用上次选中的item就可以
    private boolean mAnimAlpha = true;//是否需要在动画的时候设置alpha值
    private RectF mAlphaRectF;
    private int mAnimAlphaValue = 255;
    private boolean mAimateWhenGainFocusFromLeft = true;
    private boolean mAimateWhenGainFocusFromRight = true;
    private boolean mAimateWhenGainFocusFromUp = true;
    private boolean mAimateWhenGainFocusFromDown = true;
    private boolean mGainFocus;
    private boolean forceResetFocusParams = false;

    public FocusFlipGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FocusFlipGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusFlipGridView(Context context) {
        super(context);
        init();
    }

    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        mItemSelectedListener = listener;
    }

    public void setAnimateWhenGainFocus(boolean fromleft, boolean fromUp, boolean fromRight, boolean fromDown) {
        mAimateWhenGainFocusFromLeft = fromleft;
        mAimateWhenGainFocusFromUp = fromUp;
        mAimateWhenGainFocusFromRight = fromRight;
        mAimateWhenGainFocusFromDown = fromDown;
    }

    /**
     * 初始化focus相关操作
     */
    public void initFocused() {
        setNeedAutoSearchFocused(true);
        //当焦点离开的时候清理之前headerView的focus信息
        if (getHeaderViewsCount() > 0) {
            for (int i = 0; i < getHeaderViewsCount(); i++) {
                View view = mHeaderViewInfos.get(i).view;
                if (view instanceof FocusRelativeLayout) {
                    FocusRelativeLayout headerView = (FocusRelativeLayout) view;
                    headerView.notifyLayoutChanged();
                    headerView.clearFocusedIndex();
                }
            }
        }
    }

    /**
     * 设置在onFocused的时候是否需要自己查找childView
     */
    public void setNeedAutoSearchFocused(boolean b) {
        mNeedAutoSearchFocused = b;
    }

    @Override
    public void setSelection(int position) {
        if (isFlipFinished()) {
            if (getChildCount() > 0 && !mIsFirstLayout) {
                View preSelectedView = getSelectedView();
                int preSelectedPos = getSelectedItemPosition();
                setSelectedPositionInt(position);
                setNextSelectedPositionInt(position);
                mLayoutMode = LAYOUT_FROM_MIDDLE;
                lockFlipAnimOnceLayout();
                mNeedLayout = true;
                mNeedAutoSearchFocused = false;
                mNeedResetParam = true;
                layoutChildren();
                if (isFocused()) {
                    checkSelected(preSelectedView, preSelectedPos);
                } else {
                    //如果没有focused就不需要设置false了，因为之前unfocused的时候已经设置false
                    //视图状态的变化与是否有监听器无关，状态改变后，如果有监听器则需要通知。  quanqing.hqq
                    View currSelectedView = getSelectedView();
                    int currSelectedPos = getSelectedItemPosition();
                    if (preSelectedPos != currSelectedPos) {
                        if (currSelectedView != null) {
                            currSelectedView.setSelected(true);
                        }
                        if (mItemSelectedListener != null) {
                            mItemSelectedListener.onItemSelected(currSelectedView, currSelectedPos, true, this);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (getAdapter() == null || getChildCount() <= 0) {
            return false;
        }

        View preSelectedView = getSelectedView();
        int preSelectedPos = getSelectedItemPosition();
        mIsAnimate = true;
        boolean hasFocused = hasFocus();
        if (!hasFocused) {
            Log.i(TAG, "requestFocus for touch event to onKeyUp");
            requestFocus();
        }
        //保存键值
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mOnKeyDirection = FOCUS_LEFT;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mOnKeyDirection = FOCUS_RIGHT;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                mOnKeyDirection = FOCUS_UP;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mOnKeyDirection = FOCUS_DOWN;
                break;
            default:
                mOnKeyDirection = FOCUS_DOWN;
                break;
        }
        int selectedPos = getSelectedItemPosition();
        //判断选中的是否是headerView，如果是的话就进行深入
        if (selectedPos < getHeaderViewsCount()) {
            //headerView
            View view = getSelectedView();
            if (view instanceof FocusRelativeLayout) {
                FocusRelativeLayout headerView = (FocusRelativeLayout) view;
                boolean headerViewRet = headerView.onKeyDown(keyCode, event);
                if (DEBUG) {
                    Log.i(TAG, "onKeyDown headerViewRet=" + headerViewRet);
                }
                if (headerViewRet == true) {
                    mNeedResetParam = true;
                    layoutResetParam();//因为在headerView内部不走onLayoutChildrenDone的方法所以这里需要手动运行
                    checkSelected(preSelectedView, preSelectedPos);
                    return headerViewRet;
                } else {
                    headerView.clearSelectedView();
                }
            }
        }

        //动画未完成
        if (isFlipFinished() == false) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    //在滚动过程中按左右键只改变选中的顺序
                    int nextSelectedPos = getSelectedItemPosition() - 1;
                    setSelectedPositionInt(nextSelectedPos);
                    checkSelectionChanged();
                    amountToCenterScroll(FOCUS_LEFT, nextSelectedPos);
                    checkSelected(preSelectedView, preSelectedPos);
                }
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    //在滚动过程中按左右键只改变选中的顺序
                    int nextSelectedPos = getSelectedItemPosition() + 1;
                    setSelectedPositionInt(nextSelectedPos);
                    checkSelectionChanged();
                    amountToCenterScroll(FOCUS_RIGHT, nextSelectedPos);
                    checkSelected(preSelectedView, preSelectedPos);
                }
                    return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (DYNAMIC_ADD_CHILD_VIEW == false) {
                        break;
                    }
                    if (!lockKeyEvent(KeyEvent.KEYCODE_DPAD_UP)) {
                        //在滚动中时不进入原来的addView逻辑，由动画在滚动的过程中动态添加，这个只计算需要滚动的距离跟focus框
                        int currselectedPos = getSelectedItemPosition();
                        int nextSelectedPosition;
                        int headerCount = mHeaderViewInfos.size();
                        if (headerCount > 0) {
                            if (currselectedPos >= headerCount) {
                                nextSelectedPosition = currselectedPos - getColumnNum();
                                if (nextSelectedPosition < headerCount) {
                                    nextSelectedPosition = headerCount - 1;
                                }
                            } else {
                                nextSelectedPosition = currselectedPos - 1;
                                if (nextSelectedPosition < 0) {
                                    nextSelectedPosition = INVALID_POSITION;
                                }
                            }
                        } else {
                            nextSelectedPosition = currselectedPos - getColumnNum();
                            if (nextSelectedPosition < 0) {
                                nextSelectedPosition = INVALID_POSITION;
                            }
                        }
                        if (DEBUG) {
                            Log.i(TAG, "KEYCODE_DPAD_UP nextSelectedPosition=" + nextSelectedPosition);
                        }
                        if (nextSelectedPosition != INVALID_POSITION) {
                            setSelectedPositionInt(nextSelectedPosition);
                            checkSelectionChanged();
                            //设置选中的当前行的第一个View，因为在下次layoutChild的时候需要，
                            //如果为空的话不进行设置，因为在添加新的View的时候会自动加入
                            int rowEnd = getRowEnd(nextSelectedPosition);
                            View selectedView = getChildAt(rowEnd - getFirstVisiblePosition());
                            if (selectedView != null) {
                                setReferenceViewInSelectedRow(selectedView);
                            }
                            //进入headerView的focus
                            if (nextSelectedPosition < headerCount) {
                                View view = mHeaderViewInfos.get(nextSelectedPosition).view;
                                //快速滚动的时候当选中headerView并且还未加入到列表里面的时候重新做一下布局，
                                //因为滚动未完成时其childView还没有还原在原始的位置就离开的界面
                                if (view.getParent() == null) {
                                    int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(),
                                            View.MeasureSpec.EXACTLY);
                                    int heightSpec = View.MeasureSpec.makeMeasureSpec(view.getHeight(),
                                            View.MeasureSpec.EXACTLY);
                                    view.measure(widthSpec, heightSpec);
                                    view.layout(0, 0, view.getWidth(), view.getHeight());
                                }
                                if (view instanceof FocusRelativeLayout
                                        && view instanceof FlipGridViewHeaderOrFooterInterface) {
                                    FocusRelativeLayout headerView = (FocusRelativeLayout) view;
                                    //如果当前选中的HeaderView还没有findFocus，就进行查找，目的是为了focus到离上次选中的item最近的位置
                                    if (headerView.isNeedFocusItem()) {
                                        //将上次选中的item的坐标拉回到跟headerView下面的位置
                                        if (DEBUG) {
                                            Log.i(TAG, "mFocusRectparams.focusRect() = " + mPreFocusRect);
                                            Log.i(TAG, "mPreFocusRect = " + mPreFocusRect);
                                        }

                                        int remainScrollDistance = getRemainScrollUpDistance(nextSelectedPosition);
                                        mPreFocusRect.top += remainScrollDistance;
                                        mPreFocusRect.bottom += remainScrollDistance;
                                        //由目标焦点位置查找焦点 quanqing.hqq
                                        headerView.onFocusChanged(true, mOnKeyDirection, mPreFocusRect, null);

                                        if (DEBUG) {
                                            Log.i(TAG, "remainScrollDistance offset, mPreFocusRect = " + mPreFocusRect);
                                        }
                                    }
                                    headerView.reset();
                                }
                            }
                            amountToCenterScroll(FOCUS_UP, nextSelectedPosition);
                            checkSelected(preSelectedView, preSelectedPos);
                            return true;
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (DYNAMIC_ADD_CHILD_VIEW == false) {
                        break;
                    }
                    if (!lockKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN)) {
                        //在滚动中时不进入原来的addView逻辑，由动画在滚动的过程中动态添加，这个只计算需要滚动的距离跟focus框
                        int nextSelectedPosition = getSelectedItemPosition() + getColumnNum() < mItemCount ? getSelectedItemPosition()
                                + getColumnNum()
                                : INVALID_POSITION;
                        //条件1：当倒数第二排向下的时候其正对的下面没有item，但是最后一排其实还剩余几个，这种情况下选中最后一排的最后一个item
                        boolean isLastMovePos = false;//是否为上述说的情况
                        int selectedRowStart = getRowStart(getSelectedItemPosition());
                        if (nextSelectedPosition == INVALID_POSITION
                                && (selectedRowStart + getColumnNum()) < mItemCount) {
                            nextSelectedPosition = mItemCount - 1;
                            isLastMovePos = true;
                        }
                        if (nextSelectedPosition != INVALID_POSITION) {
                            setSelectedPositionInt(nextSelectedPosition);
                            checkSelectionChanged();
                            //setFlipSelectedPosition(nextSelectedPosition);
                            //设置选中的当前行的第一个View，因为在下次layoutChild的时候需要，
                            //如果为空的话不进行设置，因为在添加新的View的时候会自动加入
                            int rowEnd = getRowEnd(nextSelectedPosition);
                            View selectedView = getChildAt(rowEnd - getFirstVisiblePosition());
                            if (selectedView != null) {
                                setReferenceViewInSelectedRow(selectedView);
                            }
                            amountToCenterScroll(FOCUS_DOWN, nextSelectedPosition);
                            //条件1：成立的时候处理focus框左右位置需要单独处理，取得存在列表里面的同一列位置取当其左右位置
                            if (isLastMovePos) {
                                int existItemPos = getRowStart(mFirstPosition);
                                //如果当前选中的是headerView，那么选中adapterView的第一个。
                                if (existItemPos < mHeaderViewInfos.size()) {
                                    existItemPos = mHeaderViewInfos.size();
                                }
                                if (existItemPos < mFirstPosition) {
                                    existItemPos += getColumnNum();
                                }
                                int nextSelectedRowStart = getRowStart(nextSelectedPosition);
                                int rowDelta = nextSelectedPosition - nextSelectedRowStart;
                                View existItem = getChildAt(existItemPos + rowDelta - mFirstPosition);
                                if (existItem != null && existItem instanceof ItemListener) {
                                    ItemListener item = (ItemListener) existItem;
                                    FocusRectParams rectParms = item.getFocusParams();
                                    if (rectParms != null) {
                                        Rect rect = rectParms.focusRect();
                                        offsetDescendantRectToMyCoords(existItem, rect);
                                        offsetFocusRectLeftAndRight(rect.left, rect.right);
                                    }
                                }
                            }
                            checkSelected(preSelectedView, preSelectedPos);
                            return true;
                        }
                    }
                    return true;
                default:
                    break;
            }
        } else {
            int navigation = SoundEffectConstants.NAVIGATION_LEFT;
            //向下按键从headerView到adapterView，满足以下条件：
            //1.headerView无法再下向focusedView，2.从headerView到adapterView
            if (selectedPos < getHeaderViewsCount() && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                int selectedIndex = selectedPos;
                View currSelectedView = getSelectedView();
                FocusRelativeLayout headerView = null;

                if (checkIsCanDown()) {
                    if (selectedIndex == (getHeaderViewsCount() - 1)) {
                        if (currSelectedView != null && currSelectedView instanceof FocusRelativeLayout) {
                            headerView = (FocusRelativeLayout) currSelectedView;
                            View focusedView = focusSearch(headerView.getSelectedView(), FOCUS_DOWN);
                            if (focusedView != null) {
                                int nextSelectedIndex = -1;
                                for (int i = 0; i < getChildCount(); i++) {
                                    if (getChildAt(i).equals(focusedView)) {
                                        nextSelectedIndex = i + getFirstVisiblePosition();
                                        break;
                                    }
                                }
                                if (nextSelectedIndex >= 0) {
                                    mNeedResetParam = true;
                                    setAdapterSelection(nextSelectedIndex);
                                    checkSelected(preSelectedView, preSelectedPos);
                                    this.playSoundEffect(navigation);
                                    return true;
                                }
                            }
                        }
                    } else {
                        int nextSelectedIndex = (selectedPos++);
                        if (nextSelectedIndex >= 0 && nextSelectedIndex < getHeaderViewsCount()) {
                            mNeedResetParam = true;
                            setAdapterSelection(nextSelectedIndex);
                            checkSelected(preSelectedView, preSelectedPos);
                            this.playSoundEffect(navigation);
                            return true;
                        }
                    }
                }

            } else if (selectedPos <= getHeaderViewsCount() && keyCode == KeyEvent.KEYCODE_DPAD_UP) {

                //向上按键从adapterView到headerView，满足以下条件：
                //1.adapterView无法再向上focusedView，2.headerView不为空

                int nextSelectedIndex = (selectedPos--);
                if (nextSelectedIndex >= 0 && nextSelectedIndex < getHeaderViewsCount()) {
                    mNeedResetParam = true;
                    setAdapterSelection(nextSelectedIndex);
                    checkSelected(preSelectedView, preSelectedPos);
                    this.playSoundEffect(navigation);
                    return true;
                }
            }

            /*
             * //向下按键从headerView到adapterView，满足以下条件：
             * //1.headerView无法再下向focusedView，2.从headerView到adapterView
             * if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN && mHeaderViewInfos.size() > 0){
             * int selectedIndex = getSelectedItemPosition();
             * View currSelectedView = getSelectedView();
             * if(currSelectedView != null && currSelectedView instanceof FocusRelativeLayout){
             * FocusRelativeLayout headerView = (FocusRelativeLayout)currSelectedView;
             * if(selectedIndex == (mHeaderViewInfos.size() - 1)){
             * View focusedView = focusSearch(headerView.getSelectedView(), View.FOCUS_DOWN);
             * if(focusedView != null){
             * int nextSelectedIndex = -1;
             * for(int i = 0; i < getChildCount(); i++){
             * if(getChildAt(i).equals(focusedView)){
             * nextSelectedIndex = i + getFirstVisiblePosition();
             * break;
             * }
             * }
             * if(nextSelectedIndex >= 0){
             * mNeedResetParam = true;
             * setAdapterSelection(nextSelectedIndex);
             * checkSelected(preSelectedView, preSelectedPos);
             * return true;
             * }
             * }
             * }
             * }
             * }
             */
        }

        mNeedResetParam = true;
        boolean ret = super.onKeyDown(keyCode, event);
        checkSelected(preSelectedView, preSelectedPos);
        return ret;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean hasFocused = hasFocus();
        if (!hasFocused) {
            Log.i(TAG, "requestFocus for touch event to onKeyUp");
            requestFocus();
        }

        if (getAdapter() == null || getChildCount() <= 0) {
            return false;
        }

        int selectedPos = getSelectedItemPosition();
        //判断选中的是否是headerView，如果是的话就进行深入
        if (selectedPos < getHeaderViewsCount()) {
            //headerView
            View view = getSelectedView();
            if (view instanceof FocusRelativeLayout) {
                FocusRelativeLayout headerView = (FocusRelativeLayout) view;
                boolean headerViewRet = headerView.onKeyUp(keyCode, event);
                if (DEBUG) {
                    Log.i(TAG, "onKeyUp headerViewRet=" + headerViewRet);
                }
                return headerViewRet;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mGainFocus = gainFocus;
        if (mNeedAutoSearchFocused) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
            if (gainFocus) {
                mNeedAutoSearchFocused = false;
            }
        } else {
            if (getOnFocusChangeListener() != null) {
                getOnFocusChangeListener().onFocusChange(this, gainFocus);
            }
            if (gainFocus) {
                setSelection(getSelectedItemPosition());
            }
        }
        //当该控件获得焦点的时候就需要设置焦点的位置
        if (gainFocus && getChildCount() > 0) {
            int selectedPos = getSelectedItemPosition();
            if (selectedPos < getHeaderViewsCount()) {
                //headerView
                View view = getSelectedView();
                if (view instanceof FocusRelativeLayout) {
                    FocusRelativeLayout headerView = (FocusRelativeLayout) view;
                    headerView.onFocusChanged(true, direction, previouslyFocusedRect, this);
                }
            }
            mNeedResetParam = true;
            layoutResetParam();
            performSelect(true);
        } else {
            performSelect(false);
        }
        mIsAnimate = checkAnimate(direction);
    }

    private boolean checkAnimate(int direction) {
        switch (direction) {
            case FOCUS_LEFT:
                return mAimateWhenGainFocusFromRight ? true : false;
            case FOCUS_UP:
                return mAimateWhenGainFocusFromDown ? true : false;
            case FOCUS_RIGHT:
                return mAimateWhenGainFocusFromLeft ? true : false;
            case FOCUS_DOWN:
                return mAimateWhenGainFocusFromUp ? true : false;
        }

        return true;
    }

    /**
     * 保持在居中滚动，计算出滚动的距离跟focus框的位置
     * @param direction
     * @param nextSelectedPosition
     */
    protected int amountToCenterScroll(int direction, int nextSelectedPosition) {
        int verticalSpacing = getVerticalSpacing();
        //		int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
        int center = getHeight() / 2;
        int amountToScroll = 0;
        int distanceLeft = getFlipColumnFirstItemLeftMoveDistance(nextSelectedPosition);
        switch (direction) {
            case FOCUS_DOWN: {
                View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
                int nextSelectedCenter = 0;
                boolean reset = false;
                int lastBottom = getChildAt(getChildCount() - 1).getBottom();
                if (nextSelctedView == null) {
                    //还未添加的时候通过计算来取得当前选中view的中心位置
                    nextSelctedView = getChildAt(getChildCount() - 1);
                    nextSelectedCenter = nextSelctedView.getBottom() - nextSelctedView.getHeight() / 2;
                    int oldRowStart = getRowStart(getLastVisiblePosition());
                    int rowStart = getRowStart(nextSelectedPosition);
                    int delta = (rowStart - oldRowStart) / getColumnNum();
                    nextSelectedCenter += (nextSelctedView.getHeight() + verticalSpacing) * delta;
                    reset = false;
                } else {
                    nextSelectedCenter = nextSelctedView.getBottom() - nextSelctedView.getHeight() / 2;
                    reset = true;
                }

                int finalNextSelectedCenter = nextSelectedCenter + distanceLeft;

                if (finalNextSelectedCenter > center) {
                    amountToScroll = finalNextSelectedCenter - center;
                    int maxDiff = getBottomLeftDistance(getLastVisiblePosition());
                    //lastBottom到达底点的距离
                    maxDiff = lastBottom + maxDiff + mListPadding.bottom - getHeight();
                    if (maxDiff < 0) {
                        maxDiff = 0;
                    }
                    int leftDistance = getFlipColumnFirstItemLeftMoveDistance(getLastVisiblePosition());
                    maxDiff += leftDistance;
                    if (amountToScroll > maxDiff) {
                        amountToScroll = maxDiff;
                    }

                    if (reset) {
                        resetParam(nextSelctedView, 0);
                        offsetFocusRectTopAndBottom(distanceLeft, distanceLeft);
                    }

                    if (amountToScroll > 0) {
                        if (reset) {
                            offsetFocusRectTopAndBottom(-amountToScroll, -amountToScroll);
                        } else {
                            //因为down是要求向下移一个宫格再加上滚动的距离
                            offsetFocusRectTopAndBottom(
                                    ((nextSelctedView.getHeight() + verticalSpacing) - amountToScroll),
                                    ((nextSelctedView.getHeight() + verticalSpacing) - amountToScroll));
                        }

                        if (DEBUG) {
                            Log.i(TAG, "amountToCenterScroll down: focus rect = " + mFocusRectparams.focusRect()
                                    + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = "
                                    + nextSelectedPosition + " amountToScroll=" + amountToScroll
                                    + " nextSelectedCenter=" + nextSelectedCenter);
                        }
                        startRealScroll(-amountToScroll);
                        mIsAnimate = true;
                    } else {
                        if (!reset) {
                            offsetFocusRectTopAndBottom(nextSelctedView.getHeight() + verticalSpacing,
                                    nextSelctedView.getHeight() + verticalSpacing);
                        }
                        mIsAnimate = true;
                    }
                } else {
                    resetParam(getSelectedView(), 0);
                    mIsAnimate = true;
                }

                return amountToScroll;
            }
            case FOCUS_UP: {
                View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
                int nextSelectedCenter = 0;
                View firstView = getChildAt(0);
                int firstTop = firstView.getTop();
                if (firstView instanceof GridViewHeaderViewExpandDistance) {
                    firstTop += ((GridViewHeaderViewExpandDistance) firstView).getUpExpandDistance();
                }
                boolean reset = false;
                boolean notResetParam = false;
                if (nextSelctedView == null) {
                    //还未添加的时候通过计算来取得当前选中view的中心位置，取最上一个item的位置来计算
                    nextSelctedView = getChildAt(0);
                    int oldRowStart = getRowStart(getFirstVisiblePosition());
                    int rowStart = getRowStart(nextSelectedPosition);
                    int headerCount = mHeaderViewInfos.size();
                    int delta;
                    if (headerCount > 0) {
                        if (rowStart < headerCount) {
                            delta = (oldRowStart - headerCount) / getColumnNum();
                            //先计算除item以外的每行的高度
                            nextSelectedCenter = nextSelctedView.getTop()
                                    - (nextSelctedView.getHeight() + verticalSpacing) * delta;
                            //再加上headerView的中心点的高度
                            for (int i = rowStart; i < headerCount; i++) {
                                View headerView = mHeaderViewInfos.get(i).view;
                                int headerViewHeight = headerView.getHeight();
                                if (headerView instanceof GridViewHeaderViewExpandDistance) {
                                    headerViewHeight -= ((GridViewHeaderViewExpandDistance) headerView)
                                            .getUpExpandDistance();
                                    headerViewHeight -= ((GridViewHeaderViewExpandDistance) headerView)
                                            .getDownExpandDistance();
                                }
                                nextSelectedCenter -= verticalSpacing + headerViewHeight / 2;
                                if (DEBUG) {
                                    Log.i(TAG, "amountToCenterScroll up rowStart=" + rowStart + " oldRowStart="
                                            + oldRowStart + " delta=" + delta + " headerViewHeight=" + headerViewHeight
                                            + " nextSelectedCenter=" + nextSelectedCenter);
                                }
                            }
                            //设置headerView的focus位置
                            View headerView = mHeaderViewInfos.get(0).view;

                            //取得item相对于整体headerView的位置
                            if (headerView != null && headerView instanceof ItemListener) {
                                ItemListener item = (ItemListener) headerView;
                                if (mFocusRectparams == null) {
                                    mFocusRectparams = new FocusRectParams();
                                }
                                mFocusRectparams.set(item.getFocusParams());
                            }
                            int leftOffset = getHeaderViewLeft(0);
                            int topOffset = mListPadding.top;
                            //去掉填充距离
                            if (headerView instanceof GridViewHeaderViewExpandDistance) {
                                topOffset -= ((GridViewHeaderViewExpandDistance) headerView).getUpExpandDistance();
                            }
                            //考虑滚动的过程中，子view跟整体的滚动距离偏移值
                            int secondIndex = getHeaderViewSecondIndex(headerView);
                            if (nextSelectedPosition >= getFirstVisiblePosition()
                                    && nextSelectedPosition <= getLastVisiblePosition()) {
                                int childLeftDistance = getFlipItemLeftMoveDistance(nextSelectedPosition, secondIndex);
                                topOffset += (distanceLeft - childLeftDistance);
                            }
                            offsetFocusRect(leftOffset, leftOffset, topOffset, topOffset);
                            notResetParam = true;

                        } else {
                            //rowStart >= headerCount
                            delta = (oldRowStart - rowStart) / getColumnNum();
                            nextSelectedCenter = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
                            nextSelectedCenter -= (nextSelctedView.getHeight() + verticalSpacing) * delta;
                        }
                    } else {
                        //headerCount <= 0
                        delta = (oldRowStart - rowStart) / getColumnNum();
                        nextSelectedCenter = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
                        nextSelectedCenter -= (nextSelctedView.getHeight() + verticalSpacing) * delta;
                    }
                    reset = false;
                } else {
                    //nextSelctedView != null
                    if (nextSelctedView instanceof GridViewHeaderViewExpandDistance) {
                        int upDistance = ((GridViewHeaderViewExpandDistance) nextSelctedView).getUpExpandDistance();
                        int downDistance = ((GridViewHeaderViewExpandDistance) nextSelctedView).getDownExpandDistance();
                        nextSelectedCenter = nextSelctedView.getTop() + upDistance
                                + (nextSelctedView.getHeight() - upDistance - downDistance) / 2;
                    } else {
                        nextSelectedCenter = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
                    }
                    reset = true;
                }

                int finalNextSelectedCenter = nextSelectedCenter + distanceLeft;

                if (finalNextSelectedCenter < center) {
                    amountToScroll = center - finalNextSelectedCenter;
                    int maxDiff = getTopLeftDistance(getFirstVisiblePosition());
                    //firstTop到达顶点的距离
                    maxDiff = mListPadding.top - (firstTop - maxDiff);
                    if (maxDiff < 0) {
                        maxDiff = 0;
                    }
                    int left = getFlipColumnFirstItemLeftMoveDistance(getFirstVisiblePosition());
                    maxDiff -= left;
                    if (maxDiff < 0) {
                        maxDiff = 0;
                    }
                    if (amountToScroll > maxDiff) {
                        amountToScroll = maxDiff;
                    }

                    if (reset) {
                        //使用headerView里面选中view进行剩余位置的计算，但是滚动的计算应该是针对整体的headerView
                        int headerCount = mHeaderViewInfos.size();
                        if (headerCount > 0) {
                            if (nextSelectedPosition < headerCount) {
                                int secondIndex = getHeaderViewSecondIndex(nextSelctedView);
                                if (secondIndex >= 0) {
                                    int childLeftDistance = getFlipItemLeftMoveDistance(nextSelectedPosition,
                                            secondIndex);
                                    resetParam(nextSelctedView, childLeftDistance);
                                }
                            } else {
                                resetParam(nextSelctedView, 0);
                                offsetFocusRectTopAndBottom(distanceLeft, distanceLeft);
                            }
                        } else {
                            resetParam(nextSelctedView, 0);
                            offsetFocusRectTopAndBottom(distanceLeft, distanceLeft);
                        }
                    }

                    if (amountToScroll > 0) {
                        if (notResetParam == false) {
                            if (reset) {
                                offsetFocusRectTopAndBottom(amountToScroll, amountToScroll);
                            } else {
                                offsetFocusRectTopAndBottom(
                                        -((nextSelctedView.getHeight() + verticalSpacing) - amountToScroll),
                                        -((nextSelctedView.getHeight() + verticalSpacing) - amountToScroll));
                            }
                        }
                        if (DEBUG) {
                            Log.i(TAG, "amountToCenterScroll up: focus rect = " + mFocusRectparams.focusRect()
                                    + ", distanceLeft = " + distanceLeft + ", nextSelectedPosition = "
                                    + nextSelectedPosition + " amountToScroll=" + amountToScroll
                                    + " nextSelectedCenter=" + nextSelectedCenter + " finalNextSelectedCenter="
                                    + finalNextSelectedCenter + " center=" + center);
                        }
                        startRealScroll(amountToScroll);
                        mIsAnimate = true;
                    } else {
                        if (!reset && notResetParam == false) {
                            offsetFocusRectTopAndBottom(-(nextSelctedView.getHeight() + verticalSpacing),
                                    -(nextSelctedView.getHeight() + verticalSpacing));
                        }
                        mIsAnimate = true;
                    }
                } else {
                    resetParam(getSelectedView(), 0);
                    mIsAnimate = true;
                }
                //保存位置
                mPreFocusRect.set(mFocusRectparams.focusRect());
                return amountToScroll;
            }
            case FOCUS_LEFT:
            case FOCUS_RIGHT: {
                //取得有效的参考View的序号
                int lastVisiblePos = getLastVisiblePosition();
                int firstVisiblePos = getFirstVisiblePosition();
                //向下
                if (nextSelectedPosition > lastVisiblePos) {
                    int visibleRowStart = getRowStart(lastVisiblePos);
                    //如果参考不足一行,最取上一行的
                    if ((lastVisiblePos - visibleRowStart) < (getColumnNum() - 1)) {
                        visibleRowStart = getRowStart(lastVisiblePos - getColumnNum());
                    }
                    int selectedRowStart = getRowStart(nextSelectedPosition);
                    int columnDelta = nextSelectedPosition - selectedRowStart;
                    View visibleView = getChildAt(visibleRowStart + columnDelta - firstVisiblePos);
                    int delta = (selectedRowStart - visibleRowStart) / getColumnNum();
                    int offset = (visibleView.getHeight() + verticalSpacing) * delta;
                    offset += getFlipItemLeftMoveDistance(visibleRowStart + columnDelta, 0);
                    if (DEBUG) {
                        Log.i(TAG, "amountToCenterScroll left right down visibleRowStart=" + visibleRowStart
                                + " offset=" + offset + " selectedRowStart=" + selectedRowStart);
                    }
                    resetParam(visibleView, offset);
                }
                //向上
                else if (nextSelectedPosition < firstVisiblePos) {
                    int visibleRowStart = getRowStart(firstVisiblePos);
                    //如果参考不足一行,最取下一行的
                    if (visibleRowStart != firstVisiblePos) {
                        visibleRowStart = getRowStart(firstVisiblePos + getColumnNum());
                    }
                    int selectedRowStart = getRowStart(nextSelectedPosition);
                    int columnDelta = nextSelectedPosition - selectedRowStart;
                    View visibleView = getChildAt(visibleRowStart + columnDelta - firstVisiblePos);
                    int delta = (visibleRowStart - selectedRowStart) / getColumnNum();
                    int offset = -((visibleView.getHeight() + verticalSpacing) * delta);
                    offset += getFlipItemLeftMoveDistance(visibleRowStart + columnDelta, 0);
                    if (DEBUG) {
                        Log.i(TAG, "amountToCenterScroll left right up visibleRowStart=" + visibleRowStart + " offset="
                                + offset + " selectedRowStart=" + selectedRowStart);
                    }
                    resetParam(visibleView, offset);
                } else {
                    int offset = getFlipItemLeftMoveDistance(nextSelectedPosition, 0);
                    resetParam(getSelectedView(), offset);
                }
            }
        }
        return 0;
    }

    /**
     * 计算出下边界的距离（选中的item居中）
     */
    @Override
    protected void adjustForBottomFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
        // Some of the newly selected item extends below the bottom of the
        // list
        //int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
        //不需要考虑padding，因为是针对整个GridView的长度
        int top = childInSelectedRow.getTop();
        if (childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
            top += ((GridViewHeaderViewExpandDistance) childInSelectedRow).getUpExpandDistance();
        }

        int bottom = childInSelectedRow.getBottom();
        if (childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
            bottom -= ((GridViewHeaderViewExpandDistance) childInSelectedRow).getDownExpandDistance();
        }

        //int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
        //不需要考虑padding，因为是针对整个GridView的长度
        int tempTopSelectionPixel;
        int tempBottomSelectionPixel;
        if (mCenterFocus) {
            int center = getHeight() / 2;
            int childHeight = childInSelectedRow.getHeight();
            if (childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
                childHeight -= ((GridViewHeaderViewExpandDistance) childInSelectedRow).getUpExpandDistance();
                childHeight -= ((GridViewHeaderViewExpandDistance) childInSelectedRow).getDownExpandDistance();
            }
            tempTopSelectionPixel = center - childHeight / 2;
            tempBottomSelectionPixel = center + childHeight / 2;
        } else {
            tempTopSelectionPixel = topSelectionPixel;
            tempBottomSelectionPixel = bottomSelectionPixel;
        }
        if (bottom > tempBottomSelectionPixel) {

            // Find space available above the selection into which we can
            // scroll upwards
            int spaceAbove = top - tempTopSelectionPixel;

            // Find space required to bring the bottom of the selected item
            // fully into view
            int spaceBelow = bottom - tempBottomSelectionPixel;
            //需要做上下均衡取最小值，因为会有小数点误差
            int offset = Math.min(spaceAbove, spaceBelow);
            if (mCenterFocus) {
                int maxDiff = getBottomLeftDistance(getSelectedItemPosition());
                //bottom到达底点的距离
                maxDiff = bottom + maxDiff + mListPadding.bottom - getHeight();
                if (maxDiff < 0) {
                    maxDiff = 0;
                }
                if (offset > maxDiff) {
                    offset = maxDiff;
                }
            }
            // Now offset the selected item to get it into view
            offsetChildrenTopAndBottom(-offset);
        }
    }

    /**
     * 计算移动到上边界的距离（选中的item居中）
     */
    @Override
    protected void adjustForTopFadingEdge(View childInSelectedRow, int topSelectionPixel, int bottomSelectionPixel) {
        // Some of the newly selected item extends above the top of the list
        int top = childInSelectedRow.getTop();
        if (childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
            top += ((GridViewHeaderViewExpandDistance) childInSelectedRow).getUpExpandDistance();
        }

        int bottom = childInSelectedRow.getBottom();
        if (childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
            bottom -= ((GridViewHeaderViewExpandDistance) childInSelectedRow).getDownExpandDistance();
        }

        //int center = (getHeight() - mListPadding.top - mListPadding.bottom) / 2 + mListPadding.top;
        //不需要考虑padding，因为是针对整个GridView的长度
        int tempTopSelectionPixel;
        int tempBottomSelectionPixel;
        if (mCenterFocus) {
            int center = getHeight() / 2;
            int childHeight = childInSelectedRow.getHeight();
            if (childInSelectedRow instanceof GridViewHeaderViewExpandDistance) {
                childHeight -= ((GridViewHeaderViewExpandDistance) childInSelectedRow).getUpExpandDistance();
                childHeight -= ((GridViewHeaderViewExpandDistance) childInSelectedRow).getDownExpandDistance();
            }
            tempTopSelectionPixel = center - childHeight / 2;
            tempBottomSelectionPixel = center + childHeight / 2;
        } else {
            tempTopSelectionPixel = topSelectionPixel;
            tempBottomSelectionPixel = bottomSelectionPixel;
        }
        if (top < tempTopSelectionPixel) {
            // Find space required to bring the top of the selected item
            // fully into view
            int spaceAbove = tempTopSelectionPixel - top;

            // Find space available below the selection into which we can
            // scroll downwards
            int spaceBelow = tempBottomSelectionPixel - bottom;
            //需要做上下均衡取最小值，因为会有小数点误差
            int offset = Math.min(spaceAbove, spaceBelow);
            if (mCenterFocus) {
                int maxDiff = getTopLeftDistance(getSelectedItemPosition());
                //top到达顶点的距离
                maxDiff = mListPadding.top - (top - maxDiff);
                if (maxDiff < 0) {
                    maxDiff = 0;
                }
                if (offset > maxDiff) {
                    offset = maxDiff;
                }
            }
            // Now offset the selected item to get it into view
            offsetChildrenTopAndBottom(offset);
        }
    }

    @Override
    protected void layoutChildren() {
        if (DYNAMIC_ADD_CHILD_VIEW && isFlipFinished() == false) {
            Log.i(TAG, "layoutChildren flip is running can not layout");
            return;
        }
        super.layoutChildren();

        mClipFocusRect.set(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onLayoutChildrenDone() {
        //如果为首次布局完成启动入场动画
        boolean isFirst = false;//使用临时变量为防止在onLayout的时候layoutChildren递归调用导致栈溢出
        if (mIsFirstLayout == true) {
            mIsFirstLayout = false;
            isFirst = true;
        }
        if (mOnFocusFlipGridViewListener != null) {
            mOnFocusFlipGridViewListener.onLayoutDone(isFirst);
        }
        //焦点框在每次重新布局后需要重新计算位置，否则数据由0变到n时，指定的GridView显示无焦点框
        resetFocusParam();
    }

    @Override
    public void offsetChildrenTopAndBottom(int offset) {
        super.offsetChildrenTopAndBottom(offset);
    }

    /**
     * 弹性间距的运动过程的回调
     */
    @Override
    protected void onFlipItemRunnableRunning(float moveRatio, View itemView, int index) {
        if (mAnimAlpha) {
            if (mFirstAnimDone == false && itemView != null) {
                mAnimAlphaValue = (int) (moveRatio * 255);
                setAlpha(moveRatio);
            }
        }
        super.onFlipItemRunnableRunning(moveRatio, itemView, index);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mAnimAlpha) {
            if (mAlphaRectF == null) {
                mAlphaRectF = new RectF(0, 0, getWidth(), getHeight());
            }
            //			canvas.saveLayerAlpha(mAlphaRectF, mAnimAlphaValue,Canvas.ALL_SAVE_FLAG);
            super.dispatchDraw(canvas);
            //			canvas.restore();
        } else {
            super.dispatchDraw(canvas);
        }
    }

    /**
     * 弹性间距的动画完成回调
     */
    @Override
    protected void onFlipItemRunnableFinished() {
        if (mFirstAnimDone == false) {
            //入场动画完成后使可聚焦
            setFocusable(true);
            mFirstAnimDone = true;
        }
        super.onFlipItemRunnableFinished();
    }

    @Override
    public Params getParams() {
        if (mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }

        return mParams;
    }

    @Override
    public void getFocusedRect(Rect r) {
        if (hasFocus()) {
            super.getFocusedRect(r);
            return;
        }

        getDrawingRect(r);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (hasFocus()) {
            super.addFocusables(views, direction, focusableMode);
            return;
        }

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

    /**
     * 强制重新设置focus框参数
     */
    public void forceResetFocusParams(FocusPositionManager positionManager) {
        if (positionManager != null) {
            forceResetFocusParams = true;
            positionManager.forceDrawFocus();
        }
    }

    @Override
    public FocusRectParams getFocusParams() {
        View v = getSelectedView();
        if (v != null) {
            if (mFocusRectparams == null || isScrolling()) {
                resetFocusParam();
            } else if (forceResetFocusParams) {
                forceResetFocusParams = false;
                resetFocusParam();
            }
            return mFocusRectparams;
        } else {
            Rect r = new Rect();
            getFocusedRect(r);

            mFocusRectparams.set(r, 0.5f, 0.5f);
            return mFocusRectparams;
        }
    }

    @Override
    public boolean canDraw() {
        return getSelectedView() != null;
    }

    @Override
    public boolean isAnimate() {
        return mIsAnimate;
    }

    @Override
    public ItemListener getItem() {
        View view = getSelectedView();
        if (view instanceof FocusRelativeLayout) {
            FocusRelativeLayout headerView = (FocusRelativeLayout) view;
            return headerView.getItem();
        } else {
            View v = getSelectedView();
            if (v == null) {
                Log.e(TAG, "getItem: getSelectedView is null! this:" + this.toString());
            }
            return (ItemListener) v;
        }
    }

    @Override
    public boolean isScrolling() {
        if (this.DEBUG) {
            Log.d(TAG, "isFliping =" + isFliping());
        }
        return isFliping();
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        //未布局好时，不接受按键事件
        if (getAdapter() == null || getChildCount() <= 0) {
            return false;
        }

        int selectedPos = getSelectedItemPosition();
        if (selectedPos < getHeaderViewsCount()) {
            //headerView
            View view = getSelectedView();
            if (view instanceof FocusRelativeLayout) {
                //动画过程中，长按按钮的事件不宜传到headerView, 否则会使headerView中的nextFocus在执行onKeyDown时控件不一致
                //add by quanqing.hqq
                if (isFlipFinished() == false) {
                    return false;
                } else {
                    FocusRelativeLayout headerView = (FocusRelativeLayout) view;
                    boolean header = headerView.preOnKeyDown(keyCode, event);
                    if (DEBUG) {
                        Log.i(TAG, "preOnKeyDown header=" + header);
                    }
                    if (header == true) {
                        return true;
                    }
                }
                //add end
            }

        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_PAGE_UP:
            case KeyEvent.KEYCODE_MOVE_HOME:
                int headerCount = mHeaderViewInfos.size();
                if (headerCount > 0) {
                    //向上滚动的时候如果选中是headerView并且向上不能focus的时候返回false
                    if (selectedPos > 0) {
                        int rowStart = getRowStart(selectedPos);
                        if (rowStart == headerCount) {
                            View lastHeaderView = mHeaderViewInfos.get(headerCount - 1).view;
                            if (!lastHeaderView.isFocusable()) {
                                return false;
                            }

                        }
                        return true;
                    } else {
                        //此行到这里说明向上方法上已经没有item可以选择了
                        if (mOnFocusFlipGridViewListener != null) {
                            mOnFocusFlipGridViewListener.onReachGridViewTop();
                        }
                        return false;
                    }
                } else {
                    //第一排
                    if (getSelectedItemPosition() < getColumnNum()) {
                        //此行到这里说明向上方法上已经没有item可以选择了
                        if (mOnFocusFlipGridViewListener != null) {
                            mOnFocusFlipGridViewListener.onReachGridViewTop();
                        }
                        return false;
                    } else {
                        return true;
                    }
                }
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_MOVE_END:
                boolean isCan = checkIsCanDown();
                if (!isCan) {
                    return false;
                }

                return getSelectedItemPosition() < mItemCount - 1 ? true : false;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            //当选中的item为一行的头部的时候返回false，为了避免选中的item不断的放大缩小的问题
            {
                int selected = getSelectedItemPosition();
                if (selected < getHeaderViewsCount()) {
                    return false;
                } else {
                    int adapterIndex = selected - getHeaderViewsCount();
                    if (DEBUG) {
                        Log.i(TAG, "preOnKeyDown left selected=" + selected + " headerCount=" + getHeaderViewsCount()
                                + " columnNum=" + getColumnNum());
                    }
                    return (adapterIndex % getColumnNum()) == 0 ? false : true;
                }
            }
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            //当选中的item为一行的尾部的时候返回false，为了避免选中的item不断的放大缩小的问题
            {
                int selected = getSelectedItemPosition();
                if (selected < getHeaderViewsCount()) {
                    return false;
                } else {
                    //最后一个
                    if (selected >= mItemCount - 1) {
                        return false;
                    } else {
                        int adapterIndex = selected - getHeaderViewsCount() + 1;
                        if (DEBUG) {
                            Log.i(TAG, "preOnKeyDown right selected=" + selected + " headerCount="
                                    + getHeaderViewsCount() + " columnNum=" + getColumnNum());
                        }
                        return (adapterIndex % getColumnNum()) == 0 ? false : true;
                    }
                }
            }
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                return true;

            default:
                break;
        }

        return false;
    }

    @Override
    public boolean hasFocus() {
        return super.hasFocus() || mGainFocus;
    }

    @Override
    public boolean hasDeepFocus() {
        return mDeepFocus;
    }

    @Override
    public boolean canDeep() {
        return true;
    }

    @Override
    public void onFocusDeeped(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        mDeepFocus = gainFocus;
        onFocusChanged(gainFocus, direction, previouslyFocusedRect);
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
    public void onItemSelected(boolean selected) {
        performSelect(selected);
    }

    @Override
    public void onItemClick() {
        View view = getSelectedView();
        Log.v(TAG, TAG + ".onItemClick.getSelectedView = " + view);
        if (getSelectedView() != null) {
            performItemClick(getSelectedView(), getSelectedItemPosition(), 0);
        }
    }

    /**
     * 设置focusFlipGridView的监听器
     * @param listener
     */
    public void setOnFocusFlipGridViewListener(OnFocusFlipGridViewListener listener) {
        mOnFocusFlipGridViewListener = listener;
    }

    /**
     * 停止出场动画
     */
    public void stopOutAnimation() {
        Log.i(TAG, "stopOutAnimation");
        mOutAnimationRunnable.stop();
    }

    /**
     * 开始出场动画
     * 注：可能需要延时启动，因为由layout需要一定的时间，由业务层来做处理
     */
    public void startOutAnimation() {
        mOutAnimationRunnable.start();
    }

    /**
     * 开始入场动画
     */
    public void startInAnimation() {
        if (mFirstAnimDone) {
            mFirstAnimDone = false;
            //在动画的过程中不让聚焦
            setFocusable(false);
            int count = getChildCount();
            int delta = getHeight() / 2;
            if (mAnimAlpha) {
                setAlpha(0);
                mAnimAlphaValue = 0;
            }
            for (int i = 0; i < count; i++) {
                View childView = getChildAt(i);
                childView.offsetTopAndBottom(delta);
            }
            startFlip(-delta);
        }
    }

    /**
     * 初始化
     */
    private void init() {
        mOutAnimationRunnable = new OutAnimationRunnable();
    }

    private int getHeaderViewLeft(int index) {
        int headerCount = mHeaderViewInfos.size();
        if (index < headerCount) {
            View headerView = mHeaderViewInfos.get(index).view;
            int childLeft = mListPadding.left;
            int width = getWidth() - mListPadding.left - mListPadding.right;
            final int absoluteGravity = Gravity.CENTER_HORIZONTAL;// Gravity.getAbsoluteGravity(mGravity,
            // layoutDirection);
            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.LEFT:
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    childLeft += ((width - headerView.getWidth()) / 2);
                    break;
                case Gravity.RIGHT:
                    childLeft += width - headerView.getWidth();
                    break;
                default:
                    break;
            }
            return childLeft;
        }
        return 0;
    }

    /**
     * 取得行最后一个index
     * @param position
     * @return
     */
    private int getRowEnd(int position) {
        int rowEnd = position;
        int headerCount = getHeaderViewsCount();
        if (position >= headerCount) {
            if (position < mItemCount - getFooterViewsCount()) {
                int newPosition = position - headerCount;
                int left = getColumnNum() - ((newPosition % getColumnNum()) + 1);
                rowEnd = newPosition + left + headerCount;
                if (rowEnd >= mItemCount) {
                    rowEnd = mItemCount - 1;
                }
            } else {
                rowEnd = position;
            }
        }

        return rowEnd;
    }

    /**
     * 重新检查是否选中,非系统的onSelectedListener（应用到onKeyDown处理后）
     * @param preSelectedView
     * @param preSelectedPos
     */
    private void checkSelected(View preSelectedView, int preSelectedPos) {
        if (DEBUG) {
            Log.i(TAG, "checkSelected prePos=" + preSelectedPos);
        }

        //视图状态的变化与是否有监听器无关，状态改变后，如果有监听器则需要通知。  quanqing.hqq
        View currSelectedView = getSelectedView();
        int currSelectedPos = getSelectedItemPosition();
        if (preSelectedPos != currSelectedPos) {
            if (preSelectedPos >= 0 && preSelectedView != null) {
                preSelectedView.setSelected(false);
                if (mItemSelectedListener != null) {
                    mItemSelectedListener.onItemSelected(preSelectedView, preSelectedPos, false, this);
                }
            }
            if (currSelectedView != null) {
                currSelectedView.setSelected(true);
            }
            if (mItemSelectedListener != null) {
                mItemSelectedListener.onItemSelected(currSelectedView, currSelectedPos, true, this);
            }
        }
    }

    /**
     * 设置选中的方法
     * @param select
     */
    private void performSelect(boolean select) {
        //视图状态的变化与是否有监听器无关，状态改变后，如果有监听器则需要通知。   quanqing.hqq
        View selectedView = getSelectedView();
        if (selectedView != null) {
            selectedView.setSelected(select);
            if (mItemSelectedListener != null) {
                mItemSelectedListener.onItemSelected(selectedView, getSelectedItemPosition(), select, this);
            }
        }
    }

    protected void resetFocusParam() {
        mNeedResetParam = true;
        layoutResetParam();
    }

    /**
     * 因为布局引起的重新设置focus参数，需要mNeedResetParam支持
     */
    private void layoutResetParam() {
        if (mNeedResetParam == false) {
            return;
        }
        mNeedResetParam = false;
        int scrollOffset = mScrollOffset;
        int selectedPos = getSelectedItemPosition();
        if (selectedPos < getHeaderViewsCount()) {
            //headerView
            View view = getSelectedView();
            if (view instanceof FocusRelativeLayout && view instanceof FlipGridViewHeaderOrFooterInterface) {
                FocusRelativeLayout headerView = (FocusRelativeLayout) view;
                //如果当前选中的HeaderView还没有findFocus，就进行查找，目的是为了focus到离上次选中的item最近的位置
                if (headerView.isNeedFocusItem() && (hasFocus() || hasDeepFocus())) {
                    headerView.onFocusChanged(true, mOnKeyDirection, mPreFocusRect, this);
                }
                FlipGridViewHeaderOrFooterInterface headerInt = (FlipGridViewHeaderOrFooterInterface) view;
                int validChildCount = headerInt.getHorCount() * headerInt.getVerticalCount();
                int secondIndex = -1;
                for (int i = 0; i < validChildCount; i++) {
                    View childView = headerInt.getView(i);
                    if (childView != null && childView.equals(headerView.getSelectedView())) {
                        secondIndex = i;
                        break;
                    }
                }
                if (secondIndex >= 0) {
                    //如果在滚动中焦点的位置应该在滚动目标位置 
                    int leftMove = getFlipItemLeftMoveDistance(selectedPos, secondIndex);
                    if (DEBUG) {
                        Log.i(TAG, "reset header index=" + selectedPos + " secondIndex=" + secondIndex
                                + " scrollOffset=" + scrollOffset + " leftMove=" + leftMove);
                    }
                    if (scrollOffset == 0) {
                        scrollOffset = leftMove;
                    }
                    headerView.reset();
                }
            }
        } else {
            //如果在滚动中焦点的位置应该在滚动目标位置
            int leftMove = getFlipItemLeftMoveDistance(selectedPos, 0);
            if (DEBUG) {
                Log.i(TAG, "reset header index=" + selectedPos + " scrollOffset=" + scrollOffset + " leftMove="
                        + leftMove);
            }
            if (scrollOffset == 0) {
                scrollOffset = leftMove;
            }
        }
        resetParam(getSelectedView(), scrollOffset);
        // offsetDescendantRectToMyCoords(getSelectedView(),
        // mFocusRectparams.focusRect());
    }

    /**
     * 重新设置focus框相关信息
     * @param view
     * @param offset
     */
    private void resetParam(View view, int offset) {
        if (DEBUG) {
            Log.i(TAG, "View=" + view + " offset=" + offset + " position=" + getSelectedItemPosition());
        }
        if (view != null && view instanceof ItemListener) {
            ItemListener item = (ItemListener) view;
            if (mFocusRectparams == null) {
                mFocusRectparams = new FocusRectParams();
            }
            mFocusRectparams.set(item.getFocusParams());

        } else if (view != null && view instanceof FocusListener) {
            FocusListener item = (FocusListener) view;
            if (mFocusRectparams == null) {
                mFocusRectparams = new FocusRectParams();
            }
            mFocusRectparams.set(item.getFocusParams());
        } else {
            Log.w(TAG, "resetParam error view=" + view + " mItemCount=" + mItemCount + " mFirstIndex=" + mFirstPosition
                    + " mSelectedIndex=" + mSelectedPosition);
            return;
            //mFocusRectparams = null;
        }
        if (mFocusRectparams != null) {
            Rect rect = mFocusRectparams.focusRect();
            if (rect != null) {
                offsetFocusRectTopAndBottom(offset, offset);
            }
            offsetDescendantRectToMyCoords(view, mFocusRectparams.focusRect());
            mPreFocusRect.set(mFocusRectparams.focusRect());
        }
    }

    /**
     * 计算出到最顶上的剩余距离
     * @return
     */
    private int getTopLeftDistance(int itemIndex) {
        View itemView = getChildAt(itemIndex - getFirstVisiblePosition());
        if (itemView == null) {
            return Integer.MAX_VALUE;
        }
        int bottomDistance = 0;
        int columnCount = getColumnNum();
        int headerCount = mHeaderViewInfos.size();
        int footerCount = mFooterViewInfos.size();
        if (itemIndex < headerCount) {
            //header
            for (int i = itemIndex - 1; i >= 0; i--) {
                View headerView = mHeaderViewInfos.get(i).view;
                bottomDistance += headerView.getHeight() + getVerticalSpacing();
                if (headerView instanceof GridViewHeaderViewExpandDistance) {
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) headerView).getUpExpandDistance();
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) headerView).getDownExpandDistance();
                }
            }
        } else if (itemIndex >= headerCount && itemIndex < (mItemCount - footerCount)) {
            //adatper
            int preColumnIndex = itemIndex - ((itemIndex - mHeaderViewInfos.size()) % columnCount) - 1;
            int firstAdapterIndex = mHeaderViewInfos.size();
            if (preColumnIndex >= firstAdapterIndex) {
                int bottomColumnCount = (preColumnIndex - firstAdapterIndex + 1) / columnCount;
                int columnLeft = (preColumnIndex - firstAdapterIndex + 1) % columnCount;
                if (columnLeft > 0) {
                    bottomColumnCount++;
                }
                if (bottomColumnCount > 0) {
                    bottomDistance += (itemView.getHeight() + getVerticalSpacing()) * bottomColumnCount;
                }
            }
            for (int i = headerCount - 1; i >= 0; i--) {
                View headerView = mHeaderViewInfos.get(i).view;
                bottomDistance += headerView.getHeight() + getVerticalSpacing();
                if (headerView instanceof GridViewHeaderViewExpandDistance) {
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) headerView).getUpExpandDistance();
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) headerView).getDownExpandDistance();
                }
            }
        } else {
            //footer
            int footerStart = itemIndex - (mItemCount - footerCount);
            if (footerStart > 0) {
                for (int i = footerStart - 1; i >= 0; i--) {
                    View footerView = mFooterViewInfos.get(i).view;
                    bottomDistance += footerView.getHeight() + getVerticalSpacing();
                    if (footerView instanceof GridViewHeaderViewExpandDistance) {
                        bottomDistance -= ((GridViewHeaderViewExpandDistance) footerView).getUpExpandDistance();
                        bottomDistance -= ((GridViewHeaderViewExpandDistance) footerView).getDownExpandDistance();
                    }
                }
            }

            int preColumnIndex = mItemCount - footerCount - 1;
            int firstAdapterIndex = headerCount;
            if (preColumnIndex >= firstAdapterIndex) {
                int bottomColumnCount = (preColumnIndex - firstAdapterIndex + 1) / columnCount;
                int columnLeft = (preColumnIndex - firstAdapterIndex + 1) % columnCount;
                if (columnLeft > 0) {
                    bottomColumnCount++;
                }
                if (bottomColumnCount > 0) {
                    View adatperView = getChildAt(preColumnIndex - mFirstPosition);
                    if (adatperView != null) {
                        bottomDistance += (adatperView.getHeight() + getVerticalSpacing()) * bottomColumnCount;
                    } else {
                        return Integer.MAX_VALUE;
                    }
                }
            }
            for (int i = headerCount - 1; i >= 0; i--) {
                View headerView = mHeaderViewInfos.get(i).view;
                bottomDistance += headerView.getHeight() + getVerticalSpacing();
                if (headerView instanceof GridViewHeaderViewExpandDistance) {
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) headerView).getUpExpandDistance();
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) headerView).getDownExpandDistance();
                }
            }
        }
        return bottomDistance;
    }

    /**
     * 计算出到最底下的剩余距离
     * @param itemIndex
     * @return
     */
    private int getBottomLeftDistance(int itemIndex) {
        View itemView = getChildAt(itemIndex - getFirstVisiblePosition());
        if (itemView == null) {
            return Integer.MAX_VALUE;
        }
        int bottomDistance = 0;
        int columnCount = getColumnNum();
        int headerCount = mHeaderViewInfos.size();
        int footerCount = mFooterViewInfos.size();
        if (itemIndex < headerCount) {
            //header
            for (int i = itemIndex + 1; i < headerCount; i++) {
                View headerView = mHeaderViewInfos.get(i).view;
                bottomDistance += headerView.getHeight() + getVerticalSpacing();
                if (headerView instanceof GridViewHeaderViewExpandDistance) {
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) headerView).getUpExpandDistance();
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) headerView).getDownExpandDistance();
                }
            }

            int nextColumnIndex = headerCount;
            int lastAdapterIndex = mItemCount - mFooterViewInfos.size() - 1;
            if (nextColumnIndex <= lastAdapterIndex) {
                int bottomColumnCount = (lastAdapterIndex - nextColumnIndex + 1) / columnCount;
                int columnLeft = (lastAdapterIndex - nextColumnIndex + 1) % columnCount;
                if (columnLeft > 0) {
                    bottomColumnCount++;
                }
                if (bottomColumnCount > 0) {
                    View adatperView = getChildAt(nextColumnIndex - mFirstPosition);
                    if (adatperView != null) {
                        bottomDistance += (adatperView.getHeight() + getVerticalSpacing()) * bottomColumnCount;
                    } else {
                        return Integer.MAX_VALUE;
                    }
                }
            }
            for (int i = 0; i < footerCount; i++) {
                View footerView = mFooterViewInfos.get(i).view;
                bottomDistance += footerView.getHeight() + getVerticalSpacing();
                if (footerView instanceof GridViewHeaderViewExpandDistance) {
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) footerView).getUpExpandDistance();
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) footerView).getDownExpandDistance();
                }
            }
        } else if (itemIndex >= headerCount && itemIndex < (mItemCount - footerCount)) {
            //adatper
            int nextColumnIndex = itemIndex + columnCount - ((itemIndex - mHeaderViewInfos.size()) % columnCount);
            int lastAdapterIndex = mItemCount - mFooterViewInfos.size() - 1;
            if (nextColumnIndex <= lastAdapterIndex) {
                int bottomColumnCount = (lastAdapterIndex - nextColumnIndex + 1) / columnCount;
                int columnLeft = (lastAdapterIndex - nextColumnIndex + 1) % columnCount;
                if (columnLeft > 0) {
                    bottomColumnCount++;
                }
                if (bottomColumnCount > 0) {
                    bottomDistance += (itemView.getHeight() + getVerticalSpacing()) * bottomColumnCount;
                }
            }
            for (int i = 0; i < footerCount; i++) {
                View footerView = mFooterViewInfos.get(i).view;
                bottomDistance += footerView.getHeight() + getVerticalSpacing();
                if (footerView instanceof GridViewHeaderViewExpandDistance) {
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) footerView).getUpExpandDistance();
                    bottomDistance -= ((GridViewHeaderViewExpandDistance) footerView).getDownExpandDistance();
                }
            }
        } else {
            //footer
            int footerStart = footerCount - (mItemCount - 1 - itemIndex);
            if (footerStart > 0) {
                for (int i = footerStart; i < footerCount; i++) {
                    View footerView = mFooterViewInfos.get(i).view;
                    bottomDistance += footerView.getHeight() + getVerticalSpacing();
                    if (footerView instanceof GridViewHeaderViewExpandDistance) {
                        bottomDistance -= ((GridViewHeaderViewExpandDistance) footerView).getUpExpandDistance();
                        bottomDistance -= ((GridViewHeaderViewExpandDistance) footerView).getDownExpandDistance();
                    }
                }
            }
        }
        return bottomDistance;
    }

    /**
     * 是否可以向下聚焦（如果一行的下面没有item的时候不让向下）
     * @return
     */
    private boolean checkIsCanDown() {
        int selectedIndex = getSelectedItemPosition();
        int headerCount = mHeaderViewInfos.size();
        int footerCount = mFooterViewInfos.size();
        if (selectedIndex < headerCount) {
            if (selectedIndex < (mItemCount - 1)) {
                return true;
            } else {
                if (mOnFocusFlipGridViewListener != null) {
                    mOnFocusFlipGridViewListener.onReachGridViewBottom();
                }
                return false;
            }
        } else {
            if (footerCount > 0) {
                if (selectedIndex < (mItemCount - 1)) {
                    return true;
                } else {
                    if (mOnFocusFlipGridViewListener != null) {
                        mOnFocusFlipGridViewListener.onReachGridViewBottom();
                    }
                    return false;
                }
            } else {
                int columnCount = getColumnNum();
                //				int nextDownIndex = selectedIndex + columnCount;
                //				if(nextDownIndex > (mItemCount - 1)){
                //					//下下一行的第一个位置是否大于列表总数
                //					//int nextColumnIndex = selectedIndex + columnCount - ((selectedIndex - headerCount + 1) % columnCount) + 1;
                //					//下一行的第一个位置是否大于列表总数
                //					int nextColumnIndex = getRowStart(nextDownIndex);
                //					Log.i("test", "nextColumnIndex="+nextColumnIndex);
                //					if(nextColumnIndex >= mItemCount){
                //						if(mOnFocusFlipGridViewListener != null){
                //							mOnFocusFlipGridViewListener.onReachGridViewBottom();
                //						}
                //					}
                //					return false;
                //				}
                int nextFirstIndex = getRowStart(selectedIndex) + columnCount;
                if (nextFirstIndex >= mItemCount) {
                    if (mOnFocusFlipGridViewListener != null) {
                        mOnFocusFlipGridViewListener.onReachGridViewBottom();
                    }
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    /**
     * 取得headerView的选中childView的序号
     * @param headerView
     * @return
     */
    private int getHeaderViewSecondIndex(View headerView) {
        int secondIndex = -1;
        if (headerView instanceof FocusRelativeLayout) {
            View selectedChildView = ((FocusRelativeLayout) headerView).getSelectedView();
            if (selectedChildView != null) {
                if (headerView instanceof FlipGridViewHeaderOrFooterInterface) {
                    FlipGridViewHeaderOrFooterInterface headerInterface = (FlipGridViewHeaderOrFooterInterface) headerView;
                    secondIndex = headerInterface.getViewIndex(selectedChildView);
                }
            }
        }
        return secondIndex;
    }

    /**
     * 设置selection(专供带有headerView使用的，因为setSelection()已经被重写)
     * @param index
     */
    private void setAdapterSelection(int index) {
        super.setSelection(index);
    }

    /**
     * 设置出场动画帧率
     * @param outAnimFrameCount
     */
    public void setOutAnimFrameCount(int outAnimFrameCount) {
        if (mOutAnimationRunnable != null) {
            mOutAnimationRunnable.setOutAnimFrameCount(outAnimFrameCount);
        }
    }

    /**
     * 出场动画
     * @author tim
     */
    private class OutAnimationRunnable implements Runnable {

        private int outAnimFrameCount = 15;

        public void setOutAnimFrameCount(int outAnimFrameCount) {
            this.outAnimFrameCount = outAnimFrameCount;
        }

        private int mCurrFrameCount;
        private boolean mIsFinished = true;

        public void start() {
            if (mIsFinished) {
                setFocusable(false);
                mIsFinished = false;
                mCurrFrameCount = 0;
                post(this);
            }
        }

        public void stop() {
            if (mIsFinished == false) {
                Log.i(TAG, "OutAnimationRunnable stop");
                setFocusable(true);
                mIsFinished = true;
                setChild(1.0f);
                if (mOnFocusFlipGridViewListener != null) {
                    mOnFocusFlipGridViewListener.onOutAnimationDone();
                }
            }
        }

        @Override
        public void run() {
            if (mIsFinished) {
                return;
            }
            if (mCurrFrameCount > outAnimFrameCount) {
                stop();
                return;
            }
            mCurrFrameCount++;
            float scale = 1.0f - (float) mCurrFrameCount / outAnimFrameCount;
            setChild(scale);
            post(this);
        }

        /**
         * 设置子view的参数
         * @param scale
         */
        private void setChild(float scale) {
            int itemCount = getChildCount();
            if (mAnimAlpha) {
                setAlpha(scale);
                mAnimAlphaValue = (int) (scale * 255);
            }
            for (int i = 0; i < itemCount; i++) {
                View itemView = getChildAt(i);
                if (itemView instanceof FlipGridViewHeaderOrFooterInterface) {
                    FlipGridViewHeaderOrFooterInterface headerOrFooterView = (FlipGridViewHeaderOrFooterInterface) itemView;
                    int childCount = headerOrFooterView.getHorCount() * headerOrFooterView.getVerticalCount();
                    for (int j = 0; j < childCount; j++) {
                        View childView = headerOrFooterView.getView(j);
                        if (childView != null) {
                            childView.setScaleX(scale);
                            childView.setScaleY(scale);
                        }
                    }
                } else {
                    itemView.setScaleX(scale);
                    itemView.setScaleY(scale);
                }
            }
        }

    }

    /**
     * FocusFlipView的监听器
     * @author tim
     */
    public interface OnFocusFlipGridViewListener {

        /**
         * 每次完成新的布局的回调（几乎每次按键都会重新布局）
         * @param isFirst 是否为首次布局
         */
        public void onLayoutDone(boolean isFirst);

        /**
         * 出场动画完成
         */
        public void onOutAnimationDone();

        /**
         * 已经滚动到最顶上
         */
        public void onReachGridViewTop();

        /**
         * 已经滚动到最底下
         */
        public void onReachGridViewBottom();
    }

    @Override
    public boolean isFocusBackground() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawAfterFocus(Canvas canvas) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFinished() {
        return true;
    }

    //向上滚动时，仍然需要滚动的距离，用于计算目标焦点位置 quanqing.hqq
    private int getRemainScrollUpDistance(int nextSelectedPosition) {

        int remainAmountToScroll = 0;

        int verticalSpacing = getVerticalSpacing();
        int center = getHeight() / 2;
        int distanceLeft = getFlipColumnFirstItemLeftMoveDistance(nextSelectedPosition);

        View nextSelctedView = getChildAt(nextSelectedPosition - mFirstPosition);
        int nextSelectedCenter = 0;
        View firstView = getChildAt(0);
        int firstTop = firstView.getTop();
        if (firstView instanceof GridViewHeaderViewExpandDistance) {
            firstTop += ((GridViewHeaderViewExpandDistance) firstView).getUpExpandDistance();
        }
        if (nextSelctedView == null) {
            //还未添加的时候通过计算来取得当前选中view的中心位置，取最上一个item的位置来计算
            nextSelctedView = getChildAt(0);
            int oldRowStart = getRowStart(getFirstVisiblePosition());
            int rowStart = getRowStart(nextSelectedPosition);
            int headerCount = mHeaderViewInfos.size();
            int delta;
            if (headerCount > 0) {
                if (rowStart < headerCount) {
                    delta = (oldRowStart - headerCount) / getColumnNum();
                    //先计算除item以外的每行的高度
                    nextSelectedCenter = nextSelctedView.getTop() - (nextSelctedView.getHeight() + verticalSpacing)
                            * delta;
                    //再加上headerView的中心点的高度
                    for (int i = rowStart; i < headerCount; i++) {
                        View headerView = mHeaderViewInfos.get(i).view;
                        int headerViewHeight = headerView.getHeight();
                        if (headerView instanceof GridViewHeaderViewExpandDistance) {
                            headerViewHeight -= ((GridViewHeaderViewExpandDistance) headerView).getUpExpandDistance();
                            headerViewHeight -= ((GridViewHeaderViewExpandDistance) headerView).getDownExpandDistance();
                        }
                        nextSelectedCenter -= verticalSpacing + headerViewHeight / 2;
                        if (DEBUG) {
                            Log.i(TAG, "getUpRect rowStart=" + rowStart + " oldRowStart=" + oldRowStart + " delta="
                                    + delta + " headerViewHeight=" + headerViewHeight + " nextSelectedCenter="
                                    + nextSelectedCenter);
                        }
                    }
                } else {
                    //rowStart >= headerCount
                    delta = (oldRowStart - rowStart) / getColumnNum();
                    nextSelectedCenter = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
                    nextSelectedCenter -= (nextSelctedView.getHeight() + verticalSpacing) * delta;
                }
            } else {
                //headerCount <= 0
                delta = (oldRowStart - rowStart) / getColumnNum();
                nextSelectedCenter = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
                nextSelectedCenter -= (nextSelctedView.getHeight() + verticalSpacing) * delta;
            }
        } else {
            //nextSelctedView != null
            if (nextSelctedView instanceof GridViewHeaderViewExpandDistance) {
                int upDistance = ((GridViewHeaderViewExpandDistance) nextSelctedView).getUpExpandDistance();
                int downDistance = ((GridViewHeaderViewExpandDistance) nextSelctedView).getDownExpandDistance();
                nextSelectedCenter = nextSelctedView.getTop() + upDistance
                        + (nextSelctedView.getHeight() - upDistance - downDistance) / 2;
            } else {
                nextSelectedCenter = nextSelctedView.getTop() + nextSelctedView.getHeight() / 2;
            }
        }

        int finalNextSelectedCenter = nextSelectedCenter + distanceLeft;

        if (finalNextSelectedCenter < center) {
            remainAmountToScroll = center - finalNextSelectedCenter;
            int maxDiff = getTopLeftDistance(getFirstVisiblePosition());
            //firstTop到达顶点的距离
            maxDiff = mListPadding.top - (firstTop - maxDiff);
            if (maxDiff < 0) {
                maxDiff = 0;
            }
            int left = getFlipColumnFirstItemLeftMoveDistance(getFirstVisiblePosition());
            maxDiff -= left;
            if (maxDiff < 0) {
                maxDiff = 0;
            }
            if (remainAmountToScroll > maxDiff) {
                remainAmountToScroll = maxDiff;
            }
        }

        return remainAmountToScroll;
    }

    public void offsetFocusRect(int offsetX, int offsetY) {
        if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
            mFocusRectparams.focusRect().offset(offsetX, offsetY);
        }
    }

    public void offsetFocusRect(int left, int right, int top, int bottom) {
        if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
            mFocusRectparams.focusRect().left += left;
            mFocusRectparams.focusRect().right += right;
            mFocusRectparams.focusRect().top += top;
            mFocusRectparams.focusRect().bottom += bottom;
        }
    }

    public void offsetFocusRectLeftAndRight(int left, int right) {
        if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
            mFocusRectparams.focusRect().left += left;
            mFocusRectparams.focusRect().right += right;
        }
    }

    public void offsetFocusRectTopAndBottom(int top, int bottom) {
        if (SystemProUtils.getGlobalFocusMode() == PositionManager.FOCUS_SYNC_DRAW) {
            mFocusRectparams.focusRect().top += top;
            mFocusRectparams.focusRect().bottom += bottom;
        }
    }

    @Override
    public Rect getClipFocusRect() {
        // TODO Auto-generated method stub
        return mClipFocusRect;
    }
}

package com.yunos.tvtaobao.tradelink.sku.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yunos.tv.core.common.AppDebug;

/**
 * sku选择的自定义控件，横向或者竖向滚动
 * @author tingmeng.ytm
 *
 */
public class SkuView extends ViewGroup {
    private final String TAG = "SkuView";
    // 滚动的状态
    public enum ScrollState{
        IDLE,
        SCROLLING
    }
    // item动作的方向
    protected enum ItemActionDirection{
        CURRENT,
        BEFORE,
        AFTER
    };
    private Drawable mFocusDrawable; // 选中框的drawable
    private boolean mHorizontal; // 是否是横向的
    private Scroller mScroller; // 滚动动画
    private int mSelectedIndex; // 选中的序号
    private View mSelectedView; // 选中的item view
    private int mReferenceDistance; // 参考轴的位置
    private boolean mIsLayoutDone; // 是否已经布局完成
    private OnFocusChangeListener mOnFocusChangeListener; // focus变化回调
    private OnSelectedItemListener mOnSelectedItemListener; // 选中item的监听方法
    private int mItemSpace; // 每个item之间的间隙
    private ScrollState mScrollState; // 滚动的状态
    private OnScrollStateChangedListener mOnScrollStateChangedListener; // 滚动状态变化的监听方法
    private int mScrollDuration; // 控件切换滚动时间 毫秒

    public SkuView(Context context) {
        super(context);
        init();
    }

    public SkuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SkuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 设置每个item之间的间隙
     * @param space
     */
    public void setItemSpace(int space){
        mItemSpace = space;
    }
    
    /**
     * 设置选中的框
     * @param focusDrawable
     */
    public void setFocusDrawable(Drawable focusDrawable){
        mFocusDrawable = focusDrawable;
    }

    /**
     * 设置参考轴的位置
     * @param distance
     */
    public void setReferenceDistance(int distance){
        mReferenceDistance = distance;
    }
    
    /**
     * 设置选中item的回调
     * @param l
     */
    public void setOnSelectedItemListener(OnSelectedItemListener l) {
        mOnSelectedItemListener = l;
    }

    /**
     * 设置滚动状态变化的监听方法
     * @param l
     */
    public void setOnScrollStateChangedListener(OnScrollStateChangedListener l) {
        mOnScrollStateChangedListener = l;
    }
    
    /**
     * 设置默认的选中item
     * @param index
     */
    public void setDefaultSelectedItem(int index) {
        mSelectedIndex = index;
    }

    /**
     * 设置控件切换滚动时间 毫秒
     * @param time
     */
    public void setScrollDuration(int time) {
        mScrollDuration = time;
    }

    /**
     * 取得选中的view
     * @return
     */
    public View getSelectedView(){
        return mSelectedView;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 左右移动选中的位置
        int selectedIndex = mSelectedIndex;
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (selectedIndex > 0) {
                int index = checkEnableItemIndex(selectedIndex, ItemActionDirection.BEFORE);
                if (index != selectedIndex) {
                    startScrollTarget(index);
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (selectedIndex < getChildCount() - 1) {
                int index = checkEnableItemIndex(selectedIndex, ItemActionDirection.AFTER);
                if (index != selectedIndex) {
                    startScrollTarget(index);
                }
                return true;
            }
        }
        
        if ((KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode) && mSelectedView != null) {
            // 点击事件
            mSelectedView.performClick();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        // 计算滚动位置
        if (mScroller.computeScrollOffset()) {
            scrollBy(mScroller.getCurrX() - getScrollX(), mScroller.getCurrY() - getScrollY());
            invalidate();
        } else {
            stopScroll();
        }
    }
    
    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mOnFocusChangeListener = l;
        super.setOnFocusChangeListener(l);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        Log.i(TAG, "onFocusChanged gainFocus="+gainFocus);
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(this, gainFocus);
        }
        if (gainFocus) {
            selectedDefaultPosition(mSelectedIndex);
        } else {
            performSelected(-1);
            // 重新刷新，重要是为了focus框的重画
            invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // focus的位置画在背景上面item的下面
        if (mFocusDrawable != null && mSelectedView != null) {
            int left = getScrollX();
            int top = getScrollY();
            // 考虑到参考位置轴的位置
            if (mHorizontal) {
                left += mReferenceDistance;
            } else {
                top += mReferenceDistance;
            }
            int right = left + mSelectedView.getWidth();
            int bottom = top + mSelectedView.getHeight();
            mFocusDrawable.setBounds(left, top, right, bottom);
            mFocusDrawable.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 按指定的方向布局子view
        int count = getChildCount();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for(int i = 0; i < count; i++){
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != View.GONE) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                child.layout(left, top, left + childWidth, top + childHeight);
                if (mHorizontal) {
                    // 横向
                    left += childWidth + mItemSpace;
                } else {
                    // 纵向
                    top += childHeight + mItemSpace;
                }
            }
        }
        mIsLayoutDone = true;
        Log.i(TAG, "onLayout focused="+isFocused()+" mSelectedView="+mSelectedView);
        // 已经focus并且还未进行选中，就重新选择一下默认
        if (mSelectedView == null) {
            selectedDefaultPosition(mSelectedIndex);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 计算每个item view的大小
        int count = getChildCount();
        for(int i = 0; i < count; i++){
            View child = getChildAt(i);
            if (child != null) {
                LayoutParams lp = child.getLayoutParams();
                if (mHorizontal) {
                    // 横向
                    if (lp != null && lp.height == LayoutParams.WRAP_CONTENT) {
                        child.measure(0, 0);
                    } else {
                        child.measure(0, heightMeasureSpec);
                    }
                } else {
                    // 纵向
                    if (lp != null && lp.width == LayoutParams.WRAP_CONTENT) {
                        child.measure(0, 0);                        
                    } else {
                        child.measure(widthMeasureSpec, 0);
                    }
                }
            }
        }
    }
    
    /**
     * 检查有效的选中值
     * @param position
     * @param direction
     * @return
     */
    protected int checkEnableItemIndex(int position, ItemActionDirection direction) {
        switch (direction) {
            case CURRENT:
                {
                    View childView = getChildAt(position);
                    if (childView != null) {
                        if (!childView.isEnabled()) {
                            int count = getChildCount();
                            // 如果当前无效就优先继续往后查找有效值
                            for (int i = position + 1;  i < count; i++) {
                                View view = getChildAt(i);
                                if (view != null && view.isEnabled()) {
                                    return i;
                                }
                            }
                            // 再往前查找有效值
                            for (int i = position - 1;  i >= 0; i--) {
                                View view = getChildAt(i);
                                if (view != null && view.isEnabled()) {
                                    return i;
                                }
                            }                            
                            // 如果还是没有找到就返回-1;
                            return -1;
                        } else {
                            // 有效就直接返回
                            return position;
                        }
                    } else {
                        // 当前没有找到有效的就返回无效的值
                        return -1;
                    }
                }
            case AFTER:
                {
                    int nextPosition = position + 1;
                    View childView = getChildAt(nextPosition);
                    if (childView != null) {
                        if (!childView.isEnabled()) {
                            int count = getChildCount();
                            for (int i = nextPosition + 1;  i < count; i++) {
                                View view = getChildAt(i);
                                if (view != null && view.isEnabled()) {
                                    return i;
                                }
                            }
                            // 如果没有找到后续有效的值就返回原来的值
                            return position;
                        } else {
                            // 有效的话就直接返回下个选中
                            return nextPosition;
                        }
                    } else {
                        // 下个为空就返回原来的值
                        return position;
                    }
                }
            case BEFORE:
                {
                    int prePosition = position - 1;
                    View childView = getChildAt(prePosition);
                    if (childView != null) {
                        if (!childView.isEnabled()) {
                            for (int i = prePosition -1;  i >= 0; i--) {
                                View view = getChildAt(i);
                                if (view != null && view.isEnabled()) {
                                    return i;
                                }
                            }
                            // 如果没有找到后续有效的值就返回原来的值
                            return position;
                        } else {
                            // 有效的话就直接返回上个选中
                            return prePosition;
                        }
                    } else {
                        // 上个为空就返回原来的值
                        return position;
                    }
                }
            default:
                break;
        }
        // 返回无效的值
        return -1;
    }
    
    /**
     * 移动到参考轴的位置
     */
    protected void moveItemToReferenceDistance(){
        if (mHorizontal) {
            scrollTo(-mReferenceDistance, 0);
        } else {
            scrollTo(0, -mReferenceDistance);            
        }
        invalidate();
    }

    /**
     * 开始滚动的目标位置
     * 以参考轴为目标位置
     * @param index
     */
    private void startScrollTarget(int index){
        performSelected(index);
        View selectedView = getChildAt(index);
        if (selectedView != null) {
            int target = mReferenceDistance;
            int moveX = 0;
            int moveY = 0;
            if (mHorizontal) {
                moveX = (selectedView.getLeft() - target) - getScrollX();
            } else {
                moveY = (selectedView.getTop() - target) - getScrollY();
            }
            Log.i(TAG, "startScrollTarget index="+index+" target="+target+" moveX="+moveX+" moveY="+moveY+" scroll="+getScrollX());
            if (moveX != 0 || moveY != 0) {
                // 开始滚动
                scrollStateChanged(ScrollState.SCROLLING);
                mScroller.abortAnimation();
                mScroller.startScroll(getScrollX(), getScrollY(), moveX, moveY, mScrollDuration);
            }
            invalidate();
        }
    }
    
    /**
     * 选中item
     * @param index
     */
    private void performSelected(int index){
        if (mSelectedView != null) {
            mSelectedView.setSelected(false);
            if (mOnSelectedItemListener != null) {
                mOnSelectedItemListener.onSelectedItemListener(mSelectedIndex, mSelectedView, false);
            }
            mSelectedView = null;
        }
        // 只有是focus状态，并且为有效的选中
        if (isFocused() && index >= 0) {
            View selectedView = getChildAt(index);
            if (selectedView != null) {
                selectedView.setSelected(true);            
            }
            if (mOnSelectedItemListener != null) {
                mOnSelectedItemListener.onSelectedItemListener(index, selectedView, true);
            }
            mSelectedView = selectedView;
            mSelectedIndex = index;
        }
    }
    
    /**
     * 选中默认的位置
     * @param defalutIndex
     */
    private void selectedDefaultPosition(int defalutIndex){
        Log.i(TAG, "selectedDefaultPosition mIsLayoutDone="+mIsLayoutDone+" count="+getChildCount()+" defalutIndex="+defalutIndex);
        // 需要布局完成后才能进入选中
        if (mIsLayoutDone) {
            int count = getChildCount();
            if (count > 0 && defalutIndex >= 0 && defalutIndex < count) {
                int index = checkEnableItemIndex(defalutIndex, ItemActionDirection.CURRENT);
                if (index >= 0) {
                    startScrollTarget(index);
                }
                //moveItemToReferenceDistance();
            }
        }
    }
    
    /**
     * 停止滚动
     */
    private void stopScroll(){
        if (mScrollState == ScrollState.SCROLLING) {
            // 状态变化
            scrollStateChanged(ScrollState.IDLE);
        }
    }
    
    /**
     * 滚动状态变化
     * @param newState
     */
    private void scrollStateChanged(ScrollState newState){
        AppDebug.i(TAG, "scrollStateChanged mScrollState="+mScrollState+" newState="+newState);
        if (mScrollState != newState) {
            mScrollState = newState;
            if (mOnScrollStateChangedListener != null) {
                mOnScrollStateChangedListener.onScrollStateChanged(newState);
            }
        }
    }
    
    
    private void init(){
        mScrollState = ScrollState.IDLE;
        mHorizontal = true; // 默认为横向
        mScroller = new Scroller(getContext());
        mScrollDuration = 1000;
    }
    
    /**
     * 选中的回调监听方法
     * @author tingmeng.ytm
     */
    public interface OnSelectedItemListener{
        public void onSelectedItemListener(int position, View view, boolean selected);
    }
    
    /**
     * 滚动状态发生变化
     * @author tingmeng.ytm
     */
    public interface OnScrollStateChangedListener{
        public void onScrollStateChanged(ScrollState state);
    }
}

package com.yunos.tvtaobao.biz.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tv.app.widget.FlipGridView.FlipGridViewHeaderOrFooterInterface;
import com.yunos.tv.app.widget.GridView.GridViewHeaderViewExpandDistance;
import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tv.core.common.AppDebug;

public abstract class TabFlipGridViewHeaderView extends FocusRelativeLayout
        implements GridViewHeaderViewExpandDistance, FlipGridViewHeaderOrFooterInterface {

    protected static final String TAG = "TabFlipGridViewHeaderView";

    /** HeadView中子view的列表 */
    private SparseArray<View> mChildViewMap;

    /** 显示在Header的图片 */
    private BitmapDrawable mBanderDrawable;
    
    private Rect mBanderDrawableRect;

    public TabFlipGridViewHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChildrenDrawingOrderEnabled(true);
    }

    public TabFlipGridViewHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    public TabFlipGridViewHeaderView(Context context) {
        super(context);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    public int getHorCount() {
        return 1;
    }

    @Override
    public View getView(int index) {
        // 因为此处的mChildViewMap出现空指针，但不太容易复现，一方面作判断， 第二方面先打个LOG，
        AppDebug.i(TAG, "getView --> index =  " + index + "; mChildViewMap = " + mChildViewMap
                + "; this = " + this);
        if (index < 0) {
            return null;
        }

        return mChildViewMap.get(index);
    }

    @Override
    public int getViewIndex(View view) {
        if ((view != null) && (mChildViewMap != null)) {
            for (int i = 0; i < mChildViewMap.size(); i++) {
                int key = mChildViewMap.keyAt(i);
                View childView = mChildViewMap.get(key);
                if (childView.equals(view)) {
                    return key;
                }
            }
        } else {
            return -1;
        }
        return -1;
    }

    private void init() {
        mChildViewMap = new SparseArray<View>();
        fillChildViewMap(mChildViewMap);
    }

    /**
     * 销毁数据
     */
    public void onDestory() {
        if (mChildViewMap != null) {
            mChildViewMap.clear();
            mChildViewMap = null;
        }

        mBanderDrawable = null;
        mBanderDrawableRect = null;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect, ViewGroup findRoot) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect, findRoot);
    }

    /**
     * 设置header显示的bitmap
     * @param bitmap
     */
    public void setHeaderBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        mBanderDrawable = null;
        mBanderDrawable = new BitmapDrawable(bitmap);
        mBanderDrawableRect = new Rect();
        mBanderDrawableRect.setEmpty();
        mBanderDrawableRect.right = -1; // 把这个值设为小于0，则即为宽度拉伸
        mBanderDrawableRect.top = getUpExpandDistance();
        mBanderDrawableRect.bottom = mBanderDrawableRect.top + bitmap.getHeight();

        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mBanderDrawable != null && mBanderDrawableRect != null) {
            mBanderDrawableRect.right = mBanderDrawableRect.left + getWidth(); // bander图片的宽度填满父视图的宽度
            mBanderDrawable.setBounds(mBanderDrawableRect);
            mBanderDrawable.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    // 把headerview中的子view填入到viewMap中
    protected abstract void fillChildViewMap(SparseArray<View> viewMap);

    public  abstract void onHandleHeaderContent(int cornerRadius);
}

package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.biz.widget.InnerFocusLayout;
import com.yunos.tvtaobao.takeoutbundle.R;

/**
 * 外卖订单列表的每个商品的布局
 * @author tingmeng.ytm
 */
public class TOOrderDetailItemFocusLayout extends InnerFocusLayout {

    private View mFirstFocusView; // 第一次选中的子view
    private View mShopItemInfoLayout; //取得商品信息的View
    private Rect mPaddingRect; // 设置自定义Focus框空隙
    //private DrawRect mDrawRect;

    public TOOrderDetailItemFocusLayout(Context context) {
        super(context);
        init();
    }

    public TOOrderDetailItemFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TOOrderDetailItemFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onFinishInflate() {
        mShopItemInfoLayout = findViewById(R.id.shop_item_info_layout);
        super.onFinishInflate();

        //TODO ORIGINAL
        //setPivotX(getWidth() / 2);
        //setPivotY(0);
    }

    public void setCustomerPaddingRect(Rect rect) {
        mPaddingRect = rect;
    }

    /**
     * 设置第一个focus的子view
     * @param view
     */
    public void setFirstFocusView(View view) {
        mFirstFocusView = view;
    }

    @Override
    public boolean isScale() {
        return true;
    }

    @Override
    protected View getFirstFocusView() {
        return mFirstFocusView;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        /*if (!selected) {
            mDrawRect.showMark();
        } else {
            mDrawRect.hideMark();
        }*/
    }

    @Override
    public Rect getManualPadding() {
        Rect rect = new Rect();
        rect.set(0, 0, 0, 0);
        return rect;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //移除蒙版效果.
        //mDrawRect.drawRect(canvas, mShopItemInfoLayout);
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        // focus区域为卡片位的有效区域
        if (mShopItemInfoLayout != null && mShopItemInfoLayout.getVisibility() == View.VISIBLE) {
            mShopItemInfoLayout.getFocusedRect(r);
            offsetDescendantRectToMyCoords(mShopItemInfoLayout, r);
        } else {
            getFocusedRect(r);
        }
        if (mPaddingRect != null) {
            r.left += mPaddingRect.left;
            r.top += mPaddingRect.top;
            r.right -= mPaddingRect.right;
            r.bottom -= mPaddingRect.bottom;
            //TODO Scale Center
        }
        r.top += getResources().getDimensionPixelSize(R.dimen.dp_12);
        r.left += getResources().getDimensionPixelSize(R.dimen.dp_13);
        r.right -= getResources().getDimensionPixelSize(R.dimen.dp_11);
        r.bottom -= getResources().getDimensionPixelSize(R.dimen.dp_11);

        mFocusRectparams.set(r, 0.5f, 0.5f);
        return mFocusRectparams;
    }

    private void init() {
        mFirstFocusView = null;
        //mDrawRect = new DrawRect(this);
    }
}

package com.yunos.tvtaobao.cartbag.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.biz.common.DrawRect;
import com.yunos.tvtaobao.biz.widget.InnerFocusLayout;
import com.yunos.tvtaobao.cartbag.R;

/**
 * 购物车列表的每个商品的布局
 * @author tingmeng.ytm
 */
public class ShopCartItemFocusLayout extends InnerFocusLayout {

    private View mFirstFocusVidew; // 第一次选中的子view
    private View mShopItemInfoLayout; //取得商品信息的View
    private Rect mPaddingRect; // 设置自定义Focus框空隙
    private DrawRect mDrawRect;

    public ShopCartItemFocusLayout(Context context) {
        super(context);
        init();
    }

    public ShopCartItemFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShopCartItemFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onFinishInflate() {
        mShopItemInfoLayout = findViewById(R.id.shop_item_info_layout);
        super.onFinishInflate();
    }

    public void setCustomerPaddingRect(Rect rect) {
        mPaddingRect = rect;
    }

    /**
     * 设置第一个focus的子view
     * @param view
     */
    public void setFirstFocusView(View view) {
        mFirstFocusVidew = view;
    }

    @Override
    public boolean isScale() {
        return true;
    }

    @Override
    protected View getFirstFocusView() {
        return mFirstFocusVidew;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (!selected) {
            mDrawRect.showMark();
        } else {
            mDrawRect.hideMark();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mDrawRect.drawRect(canvas, mShopItemInfoLayout);
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
        }
        mFocusRectparams.set(r, 0.5f, 0.5f);
        return mFocusRectparams;
    }

    private void init() {
        mFirstFocusVidew = null;
        mDrawRect = new DrawRect(this);
    }
}

package com.yunos.tvtaobao.newcart.view;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.biz.widget.InnerFocusLayout;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.newcart.itemview.NewShopCartButton;
import com.yunos.tvtaobao.newcart.itemview.NewShopCartGoodsSelectView;
import com.yunos.tvtaobao.newcart.itemview.NewShopCartItemInfoView;

/**
 * 购物车列表的每个商品的布局
 *
 * @author tingmeng.ytm
 */
public class ShopCartItemFocusLayout extends InnerFocusLayout implements InnerFocusLayout.OnInnerItemSelectedListener {

    private View mShopItemInfoLayout;

    private static final int PADDING_LEFT_MODIFY = 5;
    private static final int PADDING_BOTTOM_MODIFY = -1;

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
        mShopItemInfoLayout = findViewById(R.id.shopcart_item_infoview);
        super.onFinishInflate();

    }

    @Override
    protected View getFirstFocusView() {
        View view = findViewById(R.id.new_shop_cart_select);
        if (view != null && view.isFocusable() && view.getVisibility() == View.VISIBLE)
            return view;
        return super.getFirstFocusView();
    }


    @Override
    public boolean isScale() {
        return true;
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
        r.left += PADDING_LEFT_MODIFY;
        r.bottom += PADDING_BOTTOM_MODIFY;
        mFocusRectparams.set(r, 0.5f, 0.5f);
        return mFocusRectparams;
    }

    private void init() {
        setOnInnerItemSelectedListener(this);
    }


    @Override
    public boolean isChangedInnerKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN;
    }

    @Override
    public void onInnerItemSelected(View view, boolean isSelected, View parentView) {
        if (view instanceof NewShopCartButton) {
            if (isSelected) {
                view.setBackgroundResource(R.drawable.new_shop_cart_button_chose_focuse_bg);
                ((NewShopCartButton) view).setTextColor(getResources().getColor(R.color.new_shop_cart_white));
            } else {
                view.setBackgroundResource(R.drawable.new_shop_cart_button_divider_shape);
                ((NewShopCartButton) view).setTextColor(getResources().getColor(R.color.new_shop_cart_unselect));
            }
        }
        if (view instanceof NewShopCartGoodsSelectView) {
            ((NewShopCartGoodsSelectView) view).setItemSelected(isSelected);
        }
    }

    public void resetState() {
        if (mShopItemInfoLayout instanceof NewShopCartItemInfoView) {
            ((NewShopCartItemInfoView) mShopItemInfoLayout).resetState();
        }
    }
}

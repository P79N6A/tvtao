package com.yunos.tvtaobao.newcart.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.biz.widget.InnerFocusLayout;
import com.yunos.tvtaobao.newcart.R;

/**
 * 购物车列表分组的标识
 *
 * @author tingmeng.ytm
 */
public class ShopCartHintFocusLayout extends InnerFocusLayout implements InnerFocusLayout.OnInnerItemSelectedListener {
    private View mHintLayout;
    private Rect mPaddingRect;

    public ShopCartHintFocusLayout(Context context) {
        super(context);
        setOnInnerItemSelectedListener(this);
    }

    public ShopCartHintFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnInnerItemSelectedListener(this);
    }

    public ShopCartHintFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnInnerItemSelectedListener(this);
    }

    @Override
    protected void onFinishInflate() {
        mHintLayout = findViewById(R.id.layout_shop_info);
        super.onFinishInflate();
    }

    public void setCustomerPaddingRect(Rect rect) {
        mPaddingRect = rect;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        // focus区域为显示的有效区域
        if (mHintLayout != null && mHintLayout.getVisibility() == View.VISIBLE) {
            mHintLayout.getDrawingRect(r);
            offsetDescendantRectToMyCoords(mHintLayout, r);
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

    @Override
    public boolean isChangedInnerKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT;
    }

    @Override
    public boolean isScale() {
        return true;
    }


    @Override
    public void onInnerItemSelected(View view, boolean isSelected, View parentView) {
        if (view instanceof NewShopCartShopSelectView) {
            ((NewShopCartShopSelectView) view).setItemSelect(isSelected);
        } else if (view instanceof TextView) {
            if (isSelected) {
                if(view.getId() == R.id.tv_delete_invalid_goods){
                    view.setBackgroundResource(R.drawable.bg_delete_invalid_focus);
                }else {
                    view.setBackgroundResource(R.drawable.new_shop_cart_button_chose_focuse_bg);
                }
                ((TextView) view).setTextColor(getResources().getColor(R.color.new_shop_cart_white));
            } else {
                if(view.getId() == R.id.tv_delete_invalid_goods){
                    view.setBackgroundResource(R.drawable.bg_delete_invalid_unfocus);
                }else {
                    view.setBackgroundColor(Color.WHITE);
                }

                ((TextView) view).setTextColor(getResources().getColor(R.color.new_shop_cart_label_txt));
            }

        }
    }
}

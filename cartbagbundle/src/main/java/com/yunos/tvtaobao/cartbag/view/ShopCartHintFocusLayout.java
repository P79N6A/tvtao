package com.yunos.tvtaobao.cartbag.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.biz.widget.FocusNoDeepFrameLayout;
import com.yunos.tvtaobao.cartbag.R;

/**
 * 购物车列表分组的标识
 * @author tingmeng.ytm
 */
public class ShopCartHintFocusLayout extends FocusNoDeepFrameLayout {
    private View mHintTextLayout;
    private Rect mPaddingRect;
    public ShopCartHintFocusLayout(Context context) {
        super(context);
    }
    public ShopCartHintFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ShopCartHintFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        mHintTextLayout = findViewById(R.id.hint_text);
        super.onFinishInflate();
    }
    
    public void setCustomerPaddingRect(Rect rect){
        mPaddingRect = rect;
    }
    
    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        // focus区域为显示的有效区域
        if (mHintTextLayout != null && mHintTextLayout.getVisibility() == View.VISIBLE) {
            mHintTextLayout.getDrawingRect(r);
            offsetDescendantRectToMyCoords(mHintTextLayout, r);
        } else {
            getFocusedRect(r);
        }
        if (mPaddingRect != null) {
            r.left += mPaddingRect.left;
            r.top  += mPaddingRect.top;
            r.right -= mPaddingRect.right;
            r.bottom -= mPaddingRect.bottom;
        }
        mFocusRectparams.set(r, 0.5f, 0.5f);
        return mFocusRectparams;
    }
    
    @Override
    public boolean isScale() {
        return true;
    }
}

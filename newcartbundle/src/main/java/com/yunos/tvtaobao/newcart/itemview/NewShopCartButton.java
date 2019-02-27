package com.yunos.tvtaobao.newcart.itemview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yunos.tvtaobao.newcart.R;


/**
 * Created by linmu on 2018/6/21.
 * 修改、详情、删除按钮的View
 */

public class NewShopCartButton extends TextView {

    public NewShopCartButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NewShopCartButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewShopCartButton(Context context) {
        super(context);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }


        @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus){
            setTextColor(getResources().getColor(R.color.new_shop_cart_white));
        }else {
            setTextColor(getResources().getColor(R.color.new_shop_cart_unselect));

        }
    }


}

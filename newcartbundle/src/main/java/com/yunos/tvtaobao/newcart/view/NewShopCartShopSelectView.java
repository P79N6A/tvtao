package com.yunos.tvtaobao.newcart.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yunos.tvtaobao.newcart.R;


/**
 * Created by linmu on 2018/6/13.
 * 选种未选中按钮
 */

public class NewShopCartShopSelectView extends LinearLayout {
    private ImageView ivSelect;
    private boolean allChecked;
    private boolean canCheck;

    public NewShopCartShopSelectView(Context context) {
        super(context);
        initView();
    }

    public NewShopCartShopSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public NewShopCartShopSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View view = inflate(getContext(), R.layout.layout_new_shop_cart_shop_select, this);
        ivSelect = (ImageView) view.findViewById(R.id.iv_select);
        ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_unselect);
    }


    public void setCanCheck(boolean canCheck) {
        this.canCheck = canCheck;
        setState();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        //可选中购买
//        ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_select);
    }

    public void setAllChecked(boolean allchecked) {
        if (!canCheck)
            return;
        allChecked = allchecked;
        setState();
    }

    private boolean isSelected = false;

    public void setItemSelect(boolean select) {
        isSelected = select;
        setState();
    }

    private void setState() {
        if (canCheck) {
            if (isSelected) {
                if (allChecked) {
                    ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_select);
                } else {
                    ivSelect.setImageResource(R.drawable.new_shop_cart_focuse_unselect);

                }
                setBackgroundResource(R.drawable.new_shop_cart_button_select_focuse_bg);
            } else {//失去焦点时文字和颜色状态
                if (allChecked) {
                    ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_select);
                } else {
                    ivSelect.setImageResource(R.drawable.new_shop_cart_unfocuse_unselect);
                }
                setBackgroundColor(Color.TRANSPARENT);
            }
        } else {

            if (isSelected) {
                ivSelect.setImageResource(R.drawable.new_shop_cart_hint_disable_focus);
                setBackgroundResource(R.drawable.new_shop_cart_button_select_focuse_bg);
            } else {
                ivSelect.setImageResource(R.drawable.new_shop_cart_hint_disable);
                setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}

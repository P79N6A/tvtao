package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;

import com.taobao.wireless.trade.mbuy.sdk.co.biz.CouponComponent;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.Map;

public class CouponViewHolder extends CheckBoxViewHolder {

    // 以下两个变量需要对应  mOptionName 和  mOptionCheck
    private int mSelectPos = 0;

    public CouponViewHolder(Context context) {
        super(context);
    }

    @Override
    protected void bindData() {
        CouponComponent couponComponent = (CouponComponent) component;
        tvTitle.setText(couponComponent.getTotalValue());
        checked = couponComponent.getSelected();
        checkBox.setImageResource(checked ? R.drawable.gouxun : R.drawable.gouxuanqian);
    }

    @Override
    protected void handlerChecked(boolean realselect) {
        CouponComponent couponComponent = (CouponComponent) component;
        couponComponent.setSelected(realselect);
        if (getContext() instanceof BuildOrderActivity) {
            //TODO
            Map<String, String> params = Utils.getProperties();
            params.put("controltag", component.getTag());
            params.put("controlname", couponComponent.getDetailTitle());
            BuildOrderActivity activity = (BuildOrderActivity) getContext();
            activity.utControlHit(couponComponent.getTag(), params);
        }
    }

}

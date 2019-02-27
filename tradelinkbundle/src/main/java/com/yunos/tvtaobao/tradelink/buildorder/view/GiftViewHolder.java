package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.taobao.wireless.trade.mbuy.sdk.co.biz.ActivityComponent;
import com.yunos.tvtaobao.tradelink.R;

public class GiftViewHolder extends PurchaseViewHolder {

    public View view;

    protected TextView tvTitle;

    protected TextView tvDesc;

    protected View vArrow;

    public GiftViewHolder(Context context) {
        super(context);
    }

    @Override
    protected View makeView() {
        view = View.inflate(context, R.layout.ytm_buildorder_buy_item_layout, null);
        return view;
    }

    @Override
    protected void bindData() {
        ActivityComponent activityComponent = (ActivityComponent) component;
    }

    @Override
    protected boolean onKeyListener(View v, int keyCode, KeyEvent event) {
        return false;
    }
}

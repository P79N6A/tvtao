package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentPickerComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentToggleComponent;

public class InstallMentEntryViewHolder extends PurchaseViewHolder {
    private InstallmentToggleComponent toggleComponent;
    private InstallmentPickerComponent pickerComponent;

    public InstallMentEntryViewHolder(Context context) {
        super(context);
    }

    @Override
    protected View makeView() {
        return null;
    }

    @Override
    protected void bindData() {

    }

    @Override
    protected boolean onKeyListener(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean isFakeComponent(Component component) {
        return super.isFakeComponent(component);
    }
}

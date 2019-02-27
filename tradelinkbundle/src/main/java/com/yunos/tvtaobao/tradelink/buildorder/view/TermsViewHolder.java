package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;

import com.taobao.wireless.trade.mbuy.sdk.co.biz.TermsComponent;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.Map;

/**
 * Created by zhujun on 10/13/16.
 */

public class TermsViewHolder extends CheckBoxViewHolder {
    public TermsViewHolder(Context context) {
        super(context);
    }

    @Override
    protected void bindData() {
        TermsComponent termsComponent = (TermsComponent) component;
        tvTitle.setText(termsComponent.getTitle());
        setChecked(termsComponent.isAgree());
    }

    @Override
    protected void handlerChecked(boolean checked) {
        TermsComponent termsComponent = (TermsComponent) component;
        termsComponent.setAgree(checked);
        if (getContext() instanceof BuildOrderActivity) {
            //TODO
            Map<String, String> params = Utils.getProperties();
            params.put("controltag", component.getTag());
            params.put("controlname", termsComponent.getTitle());
            BuildOrderActivity activity = (BuildOrderActivity) getContext();
            activity.utControlHit(termsComponent.getTag(), params);
        }
    }
}

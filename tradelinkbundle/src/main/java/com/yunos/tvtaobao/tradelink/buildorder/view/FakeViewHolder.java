package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.Map;

public class FakeViewHolder extends PurchaseViewHolder {
    public FakeViewHolder(Context context) {
        super(context);
    }

    @Override
    protected View makeView() {
        View view = new View(context);
        view.setBackgroundColor(Color.TRANSPARENT);

        return view;
    }

    @Override
    protected void bindData() {
        Map<String, String> params = Utils.getProperties();
        if (component != null) {
            params.put("controltag", component.getTag());
            if (!TextUtils.isEmpty(component.getTopic()))
                params.put("controltopic", component.getTopic());
            Utils.utCustomHit("unknowncomponent_binddata", params);
        }
    }

    @Override
    protected boolean onKeyListener(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean isFakeComponent(Component component) {
        return true;
    }
}

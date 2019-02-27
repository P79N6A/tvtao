package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taobao.wireless.trade.mbuy.sdk.co.ComponentStatus;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.ToggleComponent;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.Map;

public class CheckBoxViewHolder extends PurchaseViewHolder {

    protected View view;
    protected TextView tvTitle;
    protected TextView subTitle;
    protected boolean checked;

    protected ImageView checkBox;

    public CheckBoxViewHolder(Context context) {
        super(context);
        checked = false;
    }

    @Override
    protected View makeView() {

        view = View.inflate(context, R.layout.ytm_buildorder_check_item_layout, null);
        tvTitle = (TextView) view.findViewById(R.id.goods_buy_documents);
        subTitle = view.findViewById(R.id.subtitle);
        checkBox = (ImageView) view.findViewById(R.id.goods_buy_checkbox);
        checkBox.setImageResource(R.drawable.gouxuanqian);
        return view;
    }

    @Override
    protected void bindData() {
        ToggleComponent toggleComponent = (ToggleComponent) component;
        tvTitle.setText(toggleComponent.getName());
        checked = toggleComponent.isChecked();
        checkBox.setImageResource(checked ? R.drawable.gouxun : R.drawable.gouxuanqian);
    }

    public void setChecked(boolean checked) {
        if (component.getStatus() == ComponentStatus.DISABLE)
            return;
        this.checked = checked;
        checkBox.setImageResource(checked ? R.drawable.gouxun : R.drawable.gouxuanqian);

    }

    @Override
    protected boolean onKeyListener(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                setChecked(!checked);
                handlerChecked(checked);
                return true;
        }

        return false;
    }

    /**
     * 处理按键
     *
     * @param checked 实际的pos值
     */
    protected void handlerChecked(boolean checked) {
        if (!(component instanceof ToggleComponent))
            return;
        ToggleComponent toggleComponent = (ToggleComponent) component;
        if (toggleComponent.getStatus() == ComponentStatus.DISABLE) {
            return;
        }
        toggleComponent.setChecked(checked);
        if (getContext() instanceof BuildOrderActivity) {
            //TODO
            Map<String, String> params = Utils.getProperties();
            params.put("controltag", component.getTag());
            params.put("controlname", toggleComponent.getName());
            BuildOrderActivity activity = (BuildOrderActivity) getContext();
            activity.utControlHit(toggleComponent.getTag(), params);
        }
    }

}

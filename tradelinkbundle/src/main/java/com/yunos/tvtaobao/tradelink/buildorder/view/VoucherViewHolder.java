package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;

import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectOption;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.SelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.SelectOption;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.Map;

/**
 * Created by zhujun on 9/22/16.
 */
public class VoucherViewHolder extends CheckBoxViewHolder {

    private String initialOptionId;
    private String offOptionId;


    public VoucherViewHolder(Context context) {
        super(context);
    }


    @Override
    protected void bindData() {
        findOptimalValue();
        boolean richSelect = component instanceof RichSelectComponent;
        if (richSelect) {
            RichSelectComponent selectComponent = (RichSelectComponent) component;

            tvTitle.setText(selectComponent.getTitle() + ": " + optimalValue / 100d + "元");

            initialOptionId = selectComponent.getSelectedId();
            checked = !offOptionId.equals(selectComponent.getSelectedId());
            checkBox.setImageResource(checked ? R.drawable.gouxun : R.drawable.gouxuanqian);
        } else {
            SelectComponent selectComponent = (SelectComponent) component;

            tvTitle.setText(selectComponent.getTitle() + ": " + optimalValue / 100d + "元");

            initialOptionId = selectComponent.getSelectedId();
            checked = !offOptionId.equals(selectComponent.getSelectedId());
            checkBox.setImageResource(checked ? R.drawable.gouxun : R.drawable.gouxuanqian);
        }
        // 获取当前选中项
    }

    private String optimalID;
    private double optimalValue;

    private void findOptimalValue() {
        boolean richSelect = component instanceof RichSelectComponent;
        optimalValue = 0;
        if (richSelect) {
            RichSelectComponent selectComponent = (RichSelectComponent) component;
            optimalID = selectComponent.getSelectedId();
            for (RichSelectOption option : selectComponent.getOptions()) {
                if (option.getValue() == null) continue;
                double val = Double.parseDouble(option.getValue());
                if (optimalValue < val) {
                    optimalID = option.getOptionId();
                    optimalValue = val;
                }
                if ("0".equals(option.getValue())) {
                    offOptionId = option.getOptionId();
                }
            }
        } else {
            SelectComponent selectComponent = (SelectComponent) component;
            optimalID = selectComponent.getSelectedId();
            for (SelectOption option : selectComponent.getOptions()) {
                if (option.getPrice() == null) continue;
                double val = Double.parseDouble(option.getPrice());
                if (optimalValue < val) {
                    optimalValue = val;
                    optimalID = option.getId();
                }
                if ("0".equals(option.getPrice())) {
                    offOptionId = option.getId();
                }
            }
        }
    }


    @Override
    protected void handlerChecked(boolean realselect) {
        // 处理选项
        boolean richSelect = component instanceof RichSelectComponent;
        String title = null;
        if (richSelect) {
            RichSelectComponent selectComponent = (RichSelectComponent) component;
            selectComponent.setSelectedId(realselect ? optimalID : offOptionId);
            title = selectComponent.getTitle();
        } else {
            SelectComponent selectComponent = (SelectComponent) component;
            selectComponent.setSelectedId(realselect ? optimalID : offOptionId);
            title = selectComponent.getTitle();
        }
        if (getContext() instanceof BuildOrderActivity) {
            Map<String, String> params = Utils.getProperties();
            params.put("controltag", component.getTag());
            params.put("controlname", title);
            BuildOrderActivity activity = (BuildOrderActivity) getContext();
            activity.utControlHit(component.getTag(), params);
        }
    }

}

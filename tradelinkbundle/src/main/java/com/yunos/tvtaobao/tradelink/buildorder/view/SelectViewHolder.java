package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.view.KeyEvent;
import android.view.View;

import com.taobao.wireless.trade.mbuy.sdk.co.basic.SelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.SelectOption;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectViewHolder extends CheckBoxViewHolder {

    private List<SelectOption> mList;
    private List<String> mOptionName;

    public SelectViewHolder(Context context) {
        super(context);
        mOptionName = new ArrayList<String>();
        mOptionName.clear();
    }


    @Override
    protected void bindData() {
        findOptimalValue();
        SelectComponent selectComponent = (SelectComponent) component;

        // 设置标题
        tvTitle.setText(selectComponent.getTitle());

        if (selectComponent.getSelectedOption()!=null){
            tvTitle.setText(selectComponent.getSelectedOption().getName());
        }

        checked = offOptionId != selectComponent.getSelectedOption();
        checkBox.setImageResource(checked ? R.drawable.gouxun : R.drawable.gouxuanqian);

    }

    private SelectOption onOptionId = null;
    private SelectOption offOptionId = null;

    private void findOptimalValue() {
        SelectComponent selectComponent = (SelectComponent) component;
        if (!"0".equals(selectComponent.getSelectedId())) {
            onOptionId = selectComponent.getSelectedOption();
        }

        for (SelectOption option : selectComponent.getOptions()) {
            if (!"0".equals(option.getId())) {
                if (onOptionId == null)
                    onOptionId = option;
            } else {
                offOptionId = option;
            }
        }


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

    @Override
    protected void handlerChecked(boolean realselect) {
        // 处理选项
        SelectComponent selectComponent = (SelectComponent) component;
        if (realselect)
            selectComponent.setSelectedId(onOptionId==null?"":onOptionId.getId());
        else
            selectComponent.setSelectedId(offOptionId==null?"":offOptionId.getId());
        if (getContext() instanceof BuildOrderActivity) {
            Map<String, String> params = Utils.getProperties();
            params.put("controltag", component.getTag());
            params.put("controlname", selectComponent.getTitle());
            BuildOrderActivity activity = (BuildOrderActivity) getContext();
            activity.utControlHit(component.getTag(), params);
        }
    }

}

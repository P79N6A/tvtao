package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.taobao.wireless.trade.mbuy.sdk.co.ComponentStatus;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.ToggleComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentToggleComponent;
import com.yunos.tv.app.widget.AbsBaseListView;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.Map;

public class InstallmentToggleViewHolder extends CheckBoxViewHolder {
    public InstallmentToggleViewHolder(Context context) {
        super(context);
    }

    @Override
    protected void bindData() {
        InstallmentToggleComponent toggleComponent = (InstallmentToggleComponent) component;
        tvTitle.setText(toggleComponent.getTitle());
        checked = toggleComponent.isChecked();
        checkBox.setImageResource(checked ? R.drawable.gouxun : R.drawable.gouxuanqian);
        if (!TextUtils.isEmpty(toggleComponent.getSubtitle())) {
            subTitle.setText(toggleComponent.getSubtitle());
            subTitle.setTextColor(0xffff6000);
            subTitle.setVisibility(View.VISIBLE);
        } else {
            subTitle.setVisibility(View.GONE);
        }
    }

    @Override
    protected void handlerChecked(boolean checked) {
        InstallmentToggleComponent toggleComponent = (InstallmentToggleComponent) component;
        if (toggleComponent.getStatus() == ComponentStatus.DISABLE) {
            return;
        }
        toggleComponent.setChecked(checked);
        if (getContext() instanceof BuildOrderActivity) {
            //TODO
            Map<String, String> params = Utils.getProperties();
            params.put("controltag", component.getTag());
            params.put("controlname", toggleComponent.getTitle());
            BuildOrderActivity activity = (BuildOrderActivity) getContext();
            activity.utControlHit(toggleComponent.getTag(), params);
        }
    }

    @Override
    public AbsBaseListView.LayoutParams getPreferedLayoutParams() {
        InstallmentToggleComponent toggleComponent = (InstallmentToggleComponent) component;
        if (TextUtils.isEmpty(toggleComponent.getSubtitle()))
            return new AbsBaseListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.dp_72));
        return new AbsBaseListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.dp_104));
    }
}

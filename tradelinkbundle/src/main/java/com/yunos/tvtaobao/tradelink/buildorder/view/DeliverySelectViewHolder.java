package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.text.TextUtils;

import com.taobao.wireless.trade.mbuy.sdk.co.biz.DeliveryMethodComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.DeliveryMethodOption;
import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;
import java.util.List;

public class DeliverySelectViewHolder extends GallryListViewHolder {

    private List<DeliveryMethodOption> mList;
    private List<String> mOptionName;
    private int defaultSelect;

    public DeliverySelectViewHolder(Context context) {
        super(context);
        mOptionName = new ArrayList<String>();
        mOptionName.clear();
    }

    @Override
    protected void bindData() {
        DeliveryMethodComponent deliveryComponent = (DeliveryMethodComponent) component;
        tvTitle.setText(deliveryComponent.getTitle());
        String optionId = deliveryComponent.getSelectedId();
        mList = deliveryComponent.getOptions();
        mOptionName.clear();
        defaultSelect = 0;
        for (int i = 0; i < mList.size(); i++) {
            DeliveryMethodOption deliveryMethodOption = mList.get(i);
            if (deliveryMethodOption != null) {
                mOptionName.add(deliveryMethodOption.getName());
                String id = deliveryMethodOption.getId();
                if (TextUtils.equals(id, optionId)) {
                    defaultSelect = i;
                }
            }
        }
        mGallryListAdapter.setOptionName(mOptionName);
        mGallryListAdapter.notifyDataSetChanged();
        setDefaultPosition();
        onVisibilityArrow();
    }

    @Override
    protected void handlerChecked(int realselect) {
        DeliveryMethodComponent deliveryComponent = (DeliveryMethodComponent) component;
        List<DeliveryMethodOption> options = deliveryComponent.getOptions();
        deliveryComponent.setSelectedId(options.get(realselect).getId());
        AppDebug.i(
                TAG,
                "handlerChecked    realselect = " + realselect + ";  options.get(realselect) = "
                        + options.get(realselect));
    }

    /**
     * 设置默认的值
     */
    private void setDefaultPosition() {
        int position = Integer.MAX_VALUE / 2;
        int size = mOptionName.size();
        int remainder = position % size;
        position -= remainder;
        mSelectPos = defaultSelect + position;
        mBuildOrderGallery.setSelection(mSelectPos);
        AppDebug.i(
                TAG,
                "setDefaultPosition --> mSelectPos = " + mSelectPos + "; name = "
                        + mGallryListAdapter.getItem(mSelectPos) + "; deliveryMethodOption = "
                        + mList.get(defaultSelect));
    }
}

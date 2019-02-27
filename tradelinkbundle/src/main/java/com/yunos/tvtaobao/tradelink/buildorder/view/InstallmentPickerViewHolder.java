package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentOption;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstallmentPickerViewHolder extends GallryListViewHolder {

    private List<String> mOptionName;

    public InstallmentPickerViewHolder(Context context) {
        super(context);
        mOptionName = new ArrayList<>();
    }

    @Override
    protected void handlerChecked(int realselect) {
        onVisibilityArrow();
        InstallmentComponent installmentPickerComponent = (InstallmentComponent) component;
        if (realselect == 0)
            installmentPickerComponent.setChecked(false);
        else {

            if (!installmentPickerComponent.isChecked()) {
                installmentPickerComponent.getFields().put("checked", true);
                installmentPickerComponent.setNum(installmentPickerComponent.getOptions().get(realselect - 1).getNum());
            } else {
                installmentPickerComponent.setNum(installmentPickerComponent.getOptions().get(realselect - 1).getNum());
            }
        }
        Map<String, String> params = Utils.getProperties();
        params.put("controltag", component.getTag());
        params.put("controlname", ((InstallmentComponent) component).getTitle());
        Utils.utControlHit(component.getTag(), params);
    }

    @Override
    protected void bindData() {
        InstallmentComponent installmentPickerComponent = (InstallmentComponent) component;
        tvTitle.setText(installmentPickerComponent.getTitle());
        mOptionName.clear();
        mOptionName.add("不使用分期");
        for (InstallmentOption option : installmentPickerComponent.getOptions()) {
            String optionName = option.getTitle();
            mOptionName.add(optionName);
        }
        mGallryListAdapter.setLoop(false);
        mGallryListAdapter.setOptionName(mOptionName);
        mGallryListAdapter.notifyDataSetChanged();
//        mBuildOrderGallery.setAdapter(mGallryListAdapter);
        if (mSelectPos >= 0 && mSelectPos < mOptionName.size()) {
//            mBuildOrderGallery.setSelection(mSelectPos);
            // do nothing
        } else if (mSelectPos >= mOptionName.size()) {
            mSelectPos = 0;
        }
        if (!installmentPickerComponent.isChecked()) {
            mSelectPos = 0;
        } else {
            for (int i = 0; i < installmentPickerComponent.getOptions().size(); i++) {
                InstallmentOption option = installmentPickerComponent.getOptions().get(i);
                if (option.getNum() == installmentPickerComponent.getNum()) {
                    mSelectPos = i + 1;
                    break;
                }
            }
        }
        mBuildOrderGallery.setSelection(mSelectPos);
        onVisibilityArrow();
        Map<String, String> params = Utils.getProperties();
        params.put("disc_name", ((InstallmentComponent) component).getTitle());
        Utils.utCustomHit("Expose_TbOrderSubmit_ticket_disc", params);
    }

    @Override
    protected void onVisibilityArrow() {
        ImageView leftView = (ImageView) view.findViewById(R.id.goods_buy_left_arrow);
        ImageView rightView = (ImageView) view.findViewById(R.id.goods_buy_right_arrow);
        int count = mGallryListAdapter.getCount();
        int pos = mBuildOrderGallery.getSelectedItemPosition();
        int leftVisibility = pos > 0 ? View.VISIBLE : View.INVISIBLE;
        int rightVisibility = pos < count - 1 ? View.VISIBLE : View.INVISIBLE;
        leftView.setVisibility(leftVisibility);
        rightView.setVisibility(rightVisibility);
    }
}

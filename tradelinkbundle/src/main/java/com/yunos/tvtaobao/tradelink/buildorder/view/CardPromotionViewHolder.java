package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectOption;
import com.yunos.tvtaobao.tradelink.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujun on 9/22/16.
 */
public class CardPromotionViewHolder extends GallryListViewHolder {

    private List<String> mOptionName;


    public CardPromotionViewHolder(Context context) {
        super(context);
        mOptionName = new ArrayList<>();
    }


    @Override
    protected void bindData() {
        RichSelectComponent richSelectComponent = (RichSelectComponent) component;
        tvTitle.setText(richSelectComponent.getTitle());
        mOptionName.clear();
        for (RichSelectOption option : richSelectComponent.getOptions()) {
            String optionName = option.getName();
            mOptionName.add(optionName);
        }
        mGallryListAdapter.setLoop(false);
        mGallryListAdapter.setOptionName(mOptionName);
        mGallryListAdapter.notifyDataSetChanged();
//        mBuildOrderGallery.setAdapter(mGallryListAdapter);
        if (mSelectPos >= 0 && mSelectPos < mOptionName.size()) {
//            mBuildOrderGallery.setSelection(mSelectPos);
            //do nothing
        } else if (mSelectPos >= mOptionName.size()) {
            mSelectPos = 0;
        }
        for (int i = 0; i < richSelectComponent.getOptions().size(); i++) {
            RichSelectOption option = richSelectComponent.getOptions().get(i);
            if (option == richSelectComponent.getSelectedOption()) {
                mSelectPos = i;
                break;
            }
        }
        mBuildOrderGallery.setSelection(mSelectPos);
        onVisibilityArrow();
        // 获取当前选中项
    }

    @Override
    protected void handlerChecked(int realselect) {
        onVisibilityArrow();
        RichSelectComponent richSelectComponent = (RichSelectComponent) component;
        richSelectComponent.setSelectedId(richSelectComponent.getOptions().get(realselect).getOptionId());
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

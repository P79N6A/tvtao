package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.bean.MultiOptionComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.InstallmentChoiceComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.Vip88CardComponent;

public class MultiOptionViewHolderFactory {
    public static MultiOptionViewHolder createViewHolder(MultiOptionComponent component, ViewGroup parent, int type, MultiPromotionAdapter adapter) {
        if (component instanceof Vip88CardComponent) {
            switch (type) {
                case 0: {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_multioption_item_title, null);
                    return new MultiOptionViewHolder.Vip88TitleViewHolder(parent.getContext());
                }
                case 1: {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_multioption_item, null);
                    return new MultiOptionViewHolder.Vip88ContentViewHolder(parent.getContext());
                }
                default:
                    return null;
            }
        } else if (component instanceof InstallmentChoiceComponent) {
            switch (type) {
                case 0: {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_multioption_item_title, null);
                    return new MultiOptionViewHolder.InstallmentTitleViewHolder(parent.getContext());
                }
                case 1: {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_multioption_item, null);
                    MultiOptionViewHolder vh = new MultiOptionViewHolder.InstallmentContentViewHolder(parent.getContext());
                    vh.setOptionChangeListener(adapter);
                    return vh;
                }
                default:
                    return null;
            }
        }
        return null;
    }
}

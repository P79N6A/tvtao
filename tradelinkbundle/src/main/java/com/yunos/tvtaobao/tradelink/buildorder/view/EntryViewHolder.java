package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentPickerComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentToggleComponent;
import com.yunos.tv.app.widget.AbsBaseListView;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.bean.MultiOptionComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.Vip88CardComponent;
import com.yunos.tvtaobao.tradelink.dialog.MultiOptionPickDialog;


public class EntryViewHolder extends PurchaseViewHolder {
    public EntryViewHolder(Context context) {
        super(context);
    }

    private TextView title;
    private TextView subtitle;
    private TextView description;


    @Override
    protected View makeView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.buildorder_viewholder_entry, null);
        title = (TextView) view.findViewById(R.id.title);
        subtitle = (TextView) view.findViewById(R.id.subtitle);
        description = (TextView) view.findViewById(R.id.description);
        return view;
    }

    @Override
    protected void bindData() {
        if (component instanceof Vip88CardComponent) {//vip88
            title.setText(((Vip88CardComponent) component).getEntryTitle());
            subtitle.setText(((Vip88CardComponent) component).getEntryDescription());
            description.setText(((Vip88CardComponent) component).getEntryTip());
        } else if (component instanceof InstallmentPickerComponent) {
            InstallmentPickerComponent pickerComponent = (InstallmentPickerComponent) component;
            title.setVisibility(View.GONE);
            subtitle.setText(pickerComponent.getTitle());
            description.setText(((InstallmentPickerComponent) component).getDesc());
        }
    }

    @Override
    protected boolean onKeyListener(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean isFakeComponent(Component component) {
        return super.isFakeComponent(component);
    }

    @Override
    public AbsBaseListView.LayoutParams getPreferedLayoutParams() {
        if (component instanceof InstallmentPickerComponent) {
            InstallmentPickerComponent pickerComponent = (InstallmentPickerComponent) component;
            if (!pickerComponent.getTitle().contains("\n"))
                return new AbsBaseListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.dp_72));
        }
        return new AbsBaseListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getContext().getResources().getDimensionPixelSize(R.dimen.dp_104));
    }
}

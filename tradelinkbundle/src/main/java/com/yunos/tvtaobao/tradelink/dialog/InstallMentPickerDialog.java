package com.yunos.tvtaobao.tradelink.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentOption;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentPickerComponent;
import com.yunos.tvtaobao.tradelink.buildorder.bean.MultiOptionComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.InstallmentChoiceComponent;

import java.util.HashMap;

public class InstallMentPickerDialog extends MultiOptionPickDialog<InstallmentChoiceComponent> {
    private HashMap<InstallmentPickerComponent.OrderInstallmentPicker, InstallmentOption> optionMap;

    public InstallMentPickerDialog(@NonNull Context context) {
        super(context);
        optionMap = new HashMap<>();
    }

    @Override
    public void setMultiOptionComponent(InstallmentChoiceComponent component) {
        super.setMultiOptionComponent(component);
        optionMap.clear();
        for (int i = 0; i < component.getComponentCount(); i++) {
            InstallmentPickerComponent.OrderInstallmentPicker picker = component.getComponentAt(i);
            InstallmentOption option = picker.getOptionBySelectedNum(picker.getSelectedNum());
            if (option != null) {
                optionMap.put(picker, option);
            }
        }
        calculateSum(component.getComponentAt(0).getCurrencySymbol(), false);

    }

    @Override
    public void onOptionChanged(MultiOptionComponent component, Object subComponent, Object option) {
        super.onOptionChanged(component, subComponent, option);
        if (component == null || !(component instanceof InstallmentChoiceComponent))
            return;
        if (!(subComponent instanceof InstallmentPickerComponent.OrderInstallmentPicker))
            return;
        if (option != null && !(option instanceof InstallmentOption))
            return;
        if (option != null) {
            optionMap.put((InstallmentPickerComponent.OrderInstallmentPicker) subComponent, (InstallmentOption) option);
        } else {
            optionMap.remove(subComponent);
        }

        calculateSum(((InstallmentPickerComponent.OrderInstallmentPicker) subComponent).getCurrencySymbol(), false);
    }


    private void calculateSum(String currencySymbol, boolean hasFocus) {
        double price = 0;
        double poundage = 0;
        for (InstallmentPickerComponent.OrderInstallmentPicker picker : optionMap.keySet()) {
            InstallmentOption option = optionMap.get(picker);
            price += picker.getOrderPrice();
            poundage += option.getPoundage();
        }
        if (!hasFocus) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            SpannableString orderText = new SpannableString(String.format("%s %.2f", currencySymbol, price));
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xffff6600);
            orderText.setSpan(colorSpan, 0, orderText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ssb.append("已选分期总额 ").append(orderText).append("  手续费 ").append(currencySymbol).append(String.format(" %.2f", poundage));
            setButtonContent(ssb);
        } else {
            String content = String.format("已选分期总额 %s %.2f  手续费 %s %.2f", currencySymbol, price, currencySymbol, poundage);
            setButtonContent(content);
        }
    }

    @Override
    public void confirmAndDismiss() {
        for (int i = 0; i < getComponent().getComponentCount(); i++) {
            InstallmentPickerComponent.OrderInstallmentPicker picker = getComponent().getComponentAt(i);
            InstallmentOption option = optionMap.get(picker);
            if (option == null)
                picker.setSelectedNum("0");
            else
                picker.setSelectedNum("" + option.getNum());
        }
        super.confirmAndDismiss();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if (v == getConfirmbutton()) {
            calculateSum(getComponent().getComponentAt(0).getCurrencySymbol(), hasFocus);
        }
    }
}

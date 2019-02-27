package com.yunos.tvtaobao.tradelink.buildorder.component;

import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentPickerComponent;
import com.yunos.tvtaobao.tradelink.buildorder.bean.MultiOptionComponent;

public class InstallmentChoiceComponent implements MultiOptionComponent<InstallmentPickerComponent.OrderInstallmentPicker> {
    private InstallmentPickerComponent pickerComponent;

    public InstallmentChoiceComponent(InstallmentPickerComponent component) {
        pickerComponent = component;
    }


    @Override
    public InstallmentPickerComponent.OrderInstallmentPicker getComponentAt(int index) {
        if (pickerComponent == null)
            return null;
        return pickerComponent.getDetails().get(index);
    }

    @Override
    public int getComponentCount() {
        if (pickerComponent == null)
            return 0;
        return pickerComponent.getDetails() == null ? 0 : pickerComponent.getDetails().size();
    }

    //fixme 在entryviewholder里改用InstallmentChoiceComponent
    @Override
    public String getEntryTitle() {
        if (pickerComponent == null)
            return "";
        return pickerComponent.getPageTitle();
    }

    @Override
    public String getEntryDescription() {
        if (pickerComponent == null)
            return null;
        return pickerComponent.getCreditTip();
    }

    @Override
    public String getEntryTip() {
        return null;
    }

    @Override
    public String getDetailButtonTip() {
        if (pickerComponent == null)
            return null;
        if (pickerComponent.getSelectedNumList().size() > 0) {
            String title = pickerComponent.getTitle();
            int pos = title.indexOf("\n");
            if (pos > 0)
                title = title.substring(0, pos);
            return title;
        }
        return null;
    }

    @Override
    public String getDetailTitle() {
        if (pickerComponent == null)
            return null;
        return pickerComponent.getPageTitle();
    }

    @Override
    public String getDetailSubtitle() {
        if (pickerComponent == null)
            return null;
        String credit = pickerComponent.getCreditTip();
        int pos = credit.indexOf("\n");
        if (pos > 0)
            return credit.substring(0, pos);
        return credit;
    }

    @Override
    public String getDetailSubtitle2() {
        if (pickerComponent == null)
            return null;
        String credit = pickerComponent.getCreditTip();
        int pos = credit.indexOf("\n");
        if (pos > 0 && pos < credit.length())
            return credit.substring(pos + 1);
        return null;
    }

    @Override
    public void applyChanges() {
        pickerComponent.notifyLinkageDelegate();
    }

    @Override
    public void discardChanges() {

    }

    public static class OrderInstallmentComponent {
        private InstallmentPickerComponent.OrderInstallmentPicker picker;

        public OrderInstallmentComponent(InstallmentPickerComponent.OrderInstallmentPicker picker) {
            this.picker = picker;
        }

    }
}

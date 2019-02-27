package com.yunos.tvtaobao.tradelink.buildorder.component;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectOption;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderInfoComponent;
import com.yunos.tvtaobao.tradelink.buildorder.bean.MultiOptionComponent;
import com.yunos.tvtaobao.tradelink.buildorder.view.CardPromotionViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vip88CardComponent extends Component implements MultiOptionComponent<Vip88CardComponent.VipOptionComponent> {
    private List<VipOptionComponent> components;

    public List<VipOptionComponent> getComponents() {
        return components;
    }

    public void addComponent(RichSelectComponent component, OrderInfoComponent infoComponent) {
        if (component == null || infoComponent == null)
            return;
        if (components == null)
            components = new ArrayList<>();
        components.add(new VipOptionComponent(component, infoComponent));
    }

    public void clear() {
        if (components != null)
            components.clear();
    }

    public static final class VipOptionComponent {
        private RichSelectComponent selectComponent;
        public OrderInfoComponent infoComponent;

        private String title = null;
        private String tip = null;
        private String checkedId = null;


        VipOptionComponent(RichSelectComponent selectComponent, OrderInfoComponent infoComponent) {
            this.selectComponent = selectComponent;
            this.infoComponent = infoComponent;
            this.checked = !"0".equals(selectComponent.getSelectedId());
            for (RichSelectOption option : selectComponent.getOptions()) {
                if (!"0".equals(option.getOptionId())) {
                    checkedId = option.getOptionId();
                    title = option.getName().replace(":", "  ");
                    if (option.getTips() != null && option.getTips().size() >= 1)
                        tip = option.getTips().get(0);
                    break;
                }
            }
        }

        private boolean checked;

        public boolean isChecked() {
            return checked;
        }

        public String getTitle() {
            return title;
        }

        public String getTip() {
            return tip;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void applyChanges() {
            boolean originalChecked = !"0".equals(selectComponent.getSelectedId());
            if (originalChecked == this.checked) return;
            else {
                if (!checked) {
                    selectComponent.setSelectedId("0");
                } else {
                    selectComponent.setSelectedId(checkedId);
                }
            }
        }

        public void discardChanges() {
            this.checked = !"0".equals(selectComponent.getSelectedId());
        }
    }

    @Override
    public int getComponentCount() {
        return components == null ? 0 : components.size();
    }

    @Override
    public VipOptionComponent getComponentAt(int index) {
        if (components == null)
            return null;
        if (index < 0 || index >= components.size())
            return null;
        return components.get(index);
    }

    @Override
    public ComponentType getType() {
        return ComponentType.SYNTHETIC;
    }

    @Override
    public String getTag() {
        return "88VIP";
    }

    @Override
    public String getId() {
        return "0";
    }

    @Override
    public boolean isSubmit() {
        return false;
    }

    @Override
    public String getEntryTitle() {
        return "88VIP专享";
    }

    @Override
    public String getDetailTitle() {
        return getEntryTitle();
    }

    @Override
    public String getDetailButtonTip() {
        return null;
    }

    @Override
    public String getDetailSubtitle() {
        return null;
    }

    @Override
    public String getDetailSubtitle2() {
        return null;
    }

    double amount = -1;
    private String promotion = null;

    @Override
    public String getEntryDescription() {
        if (components == null || components.size() <= 0)
            return null;
        if (promotion != null)
            return promotion;
        double amount = getPromotionAmount();
        return getPromotionDescription(amount);
    }

    private String getPromotionDescription(double amount) {
        if (amount <= 0) {
            return "未选择专享";
        }
        Pattern pattern = Pattern.compile("省(\\d+\\.{0,1}\\d*)元");
        for (VipOptionComponent component : components) {
            String description = component.selectComponent.getValue();
            Matcher matcher = pattern.matcher(description);
            if (matcher.find()) {
                return description.replaceFirst("省\\d+\\.{0,1}\\d*元", String.format("省%.2f元", amount)).replace(":", " ");
            }
        }
        return null;

    }

    @Override
    public String getEntryTip() {
        if (getPromotionAmount() > 0) {
            return "已专享";
        } else {
            return "有专享可用";
        }
    }


    private double getPromotionAmount() {
        if (amount >= 0)
            return amount;
        amount = 0;
        String description;
        Pattern pattern = Pattern.compile("省(\\d+\\.{0,1}\\d*)元");
        for (VipOptionComponent component : components) {
            description = component.selectComponent.getValue();
            Matcher matcher = pattern.matcher(description);
            if (matcher.find()) {
                String val = matcher.group(1);
                double dValue = Double.parseDouble(val);
                amount += dValue;
            }
        }
        return amount;
    }


    @Override
    public void applyChanges() {
        for (VipOptionComponent component : components) {
            component.applyChanges();
        }
    }

    @Override
    public void discardChanges() {
        for (VipOptionComponent component : components) {
            component.discardChanges();
        }
    }
}

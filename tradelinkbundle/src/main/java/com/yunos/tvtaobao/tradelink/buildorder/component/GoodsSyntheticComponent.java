package com.yunos.tvtaobao.tradelink.buildorder.component;


import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemPayComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.QuantityComponent;

public class GoodsSyntheticComponent extends Component {


    public GoodsSyntheticComponent() {
        super();
        this.type = ComponentType.SYNTHETIC;
    }

    private Component rebateComponent;

    private ItemComponent itemComponent;

    private ItemInfoComponent itemInfoComponent;

    private ItemPayComponent itemPayComponent;

    private QuantityComponent quantityComponent;

    public Component getRebateComponent() {
        return rebateComponent;
    }

    public void setRebateComponent(Component rebateComponent) {
        this.rebateComponent = rebateComponent;
    }

    public ItemComponent getItemComponent() {
        return itemComponent;
    }

    public void setItemComponent(ItemComponent itemComponent) {
        this.itemComponent = itemComponent;
    }

    public ItemInfoComponent getItemInfoComponent() {
        return itemInfoComponent;
    }

    public void setItemInfoComponent(ItemInfoComponent itemInfoComponent) {
        this.itemInfoComponent = itemInfoComponent;
    }

    public QuantityComponent getQuantityComponent() {
        return quantityComponent;
    }

    public void setQuantityComponent(QuantityComponent quantityComponent) {
        this.quantityComponent = quantityComponent;
    }

    public ItemPayComponent getItemPayComponent() {
        return itemPayComponent;
    }

    public void setItemPayComponent(ItemPayComponent itemPayComponent) {
        this.itemPayComponent = itemPayComponent;
    }
 
    @Override
    public String toString() {
        return "Component [type=" + type + "]"
                + (itemInfoComponent != null ? " - " + itemInfoComponent.getType() : "")
                + (itemPayComponent != null ? " - " + itemPayComponent.getPrice() : "")
                + (quantityComponent != null ? " - " + quantityComponent.getQuantity() : "");
    } 
}

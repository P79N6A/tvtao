package com.yunos.tvtaobao.tradelink.buildorder.component;

import android.text.SpannableStringBuilder;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderBondComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderPayComponent;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.buildorder.bean.GoodsDisplayInfo;

import java.util.ArrayList;
import java.util.List;

public class OrderBondSyntheticComponent extends Component implements SyntheticComponent {
    /**
     * "orderInfo_cad843c2e3cce2459dfe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d",
     * "order_cad843c2e3cce2459dfe0bcb69a4cc6c",
     * "order_4691ab06f3d840aac32fd3df4fdf5f1d",
     * "promotion_cad843c2e3cce2459dfe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d",
     * "cardPromotion_cad843c2e3cce2459dfe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d",
     * "orderPay_cad843c2e3cce2459dfe0bcb69a4cc6c_4691ab06f3d840aac32fd3df4fdf5f1d"
     */
    public OrderInfoComponent orderInfoComponent;

    public List<OrderSyntheticComponent> orders;

    public OrderPayComponent orderPayComponent;

    public OrderBondComponent orderBondComponent;

    public OrderBondSyntheticComponent() {
        super();
        this.type = ComponentType.SYNTHETIC;
        this.orders = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public void addGoodItem(String key, SpannableStringBuilder value) {
        GoodsDisplayInfo.GoodsItem goodsItem = new GoodsDisplayInfo.GoodsItem();
        goodsItem.documents = key;
        goodsItem.value = value;
        AppDebug.d("test", this + " add goods item: " + key + ", " + value);
        synchronized (items) {
            items.add(goodsItem);
        }
    }

    private List<GoodsDisplayInfo.GoodsItem> items;

    @Override
    public boolean isOrderBond() {
        return true;
    }
}

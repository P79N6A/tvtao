package com.yunos.tvtaobao.tradelink.buildorder.component;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.DeliveryMethodComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.OrderPayComponent;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.bean.GoodsDisplayInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhujun on 9/27/16.
 */

public class OrderSyntheticComponent extends Component implements SyntheticComponent {

    int foregroundColor = 0xffff6c0f;


    private OrderComponent orderComponent;
    private OrderInfoComponent orderInfoComponent;
    private OrderPayComponent orderPayComponent;
    private DeliveryMethodComponent deliveryMethodComponent;
    private List<GoodsSyntheticComponent> goodsSyntheticComponents;
    private List<GoodsDisplayInfo.GoodsItem> items;
    private List<GoodsDisplayInfo.GoodsItem> priorityItems;

    private Component presaleAddressComponent;

    private Component finalPayComponent;

    public OrderSyntheticComponent() {
        super();
        this.type = ComponentType.SYNTHETIC;//same as goodssysntheticComponent
        this.items = new ArrayList<GoodsDisplayInfo.GoodsItem>();
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

    public void addGoodItem(int index, String key, SpannableStringBuilder value) {
        GoodsDisplayInfo.GoodsItem goodsItem = new GoodsDisplayInfo.GoodsItem();
        goodsItem.documents = key;
        goodsItem.value = value;
        AppDebug.d("test", this + " add goods item: " + key + ", " + value);
        synchronized (items) {
            items.add(index, goodsItem);
        }
    }

    public Component getFinalPayComponent() {
        return finalPayComponent;
    }

    public void setFinalPayComponent(Component finalPayComponent) {
        this.finalPayComponent = finalPayComponent;
    }

    public Component getPresaleAddressComponent() {
        return presaleAddressComponent;
    }

    public void setPresaleAddressComponent(Component presaleAddressComponent) {
        this.presaleAddressComponent = presaleAddressComponent;
    }

    public void buildPriorityGoodItems(Context context) {
        priorityItems = new ArrayList<>();
        if (orderPayComponent != null) {
            String jian = context.getResources().getString(R.string.ytm_buildorder_unit_jian);
            SpannableStringBuilder quantity = onHandlerSpanned(orderPayComponent.getQuantity() + jian,
                    false);

            String shuliang = context.getResources().getString(
                    R.string.ytm_buildorder_quantity);

            priorityItems.add(new GoodsDisplayInfo.GoodsItem(shuliang, quantity));
            if (orderPayComponent != null && !TextUtils.isEmpty(orderPayComponent.getWeight())) {
                SpannableStringBuilder weight = onHandlerSpanned(orderPayComponent.getWeight() + "kg",
                        false);
                priorityItems.add(new GoodsDisplayInfo.GoodsItem("总重", weight));

            }
        } else {
            String jian = context.getResources().getString(R.string.ytm_buildorder_unit_jian);
            int number = 0;
            for (GoodsSyntheticComponent goodsSyntheticComponent : goodsSyntheticComponents) {
                if (goodsSyntheticComponent.getItemPayComponent() != null)
                    number += Integer.valueOf(goodsSyntheticComponent.getItemPayComponent().getQuantity());
            }
            SpannableStringBuilder quantity = onHandlerSpanned(number + jian,
                    false);
            String shuliang = context.getResources().getString(
                    R.string.ytm_buildorder_quantity);

            priorityItems.add(new GoodsDisplayInfo.GoodsItem(shuliang, quantity));
        }
        if (deliveryMethodComponent != null && !TextUtils.isEmpty(deliveryMethodComponent.getSelectedId())) {
            String yuan = context.getResources().getString(
                    R.string.ytm_buildorder_unit_yuan);
            SpannableStringBuilder value = onHandlerSpanned(
                    deliveryMethodComponent.getSelectedOption().getPrice() + yuan, true);
            String peisong = deliveryMethodComponent.getTitle();
            String peisongfangshi = context.getResources().getString(
                    R.string.ytm_buildorder_peisongfangshi);
            int pos = peisong.indexOf(peisongfangshi);
            if (pos >= 0) {
                String yunfei = context.getResources().getString(
                        R.string.ytm_buildorder_yunfei);
                priorityItems.add(new GoodsDisplayInfo.GoodsItem(yunfei, value));
            } else {
                priorityItems.add(new GoodsDisplayInfo.GoodsItem(peisong, value));
            }
        }
    }

    public List<GoodsDisplayInfo.GoodsItem> getPriorityItems() {
        return priorityItems;
    }

    public List<GoodsDisplayInfo.GoodsItem> getGoodsItems() {
        List<GoodsDisplayInfo.GoodsItem> result = new ArrayList<>();
        result.addAll(priorityItems);
        result.addAll(items);
        return result;
    }

    public void setOrderComponent(OrderComponent orderComponent) {
        this.orderComponent = orderComponent;
    }

    public void setOrderInfoComponent(OrderInfoComponent orderInfoComponent) {
        this.orderInfoComponent = orderInfoComponent;
    }

    public void setOrderPayComponent(OrderPayComponent orderPayComponent) {
        this.orderPayComponent = orderPayComponent;
    }

    public void setGoodsSyntheticComponents(List<GoodsSyntheticComponent> goodsSyntheticComponent) {
        this.goodsSyntheticComponents = goodsSyntheticComponent;
    }

    public List<GoodsSyntheticComponent> getGoodsSyntheticComponents() {
        return goodsSyntheticComponents;
    }

    public void

    setDeliveryMethodComponent(DeliveryMethodComponent deliveryMethodComponent) {
        this.deliveryMethodComponent = deliveryMethodComponent;
    }

    public DeliveryMethodComponent getDeliveryMethodComponent() {
        return deliveryMethodComponent;
    }

    public OrderComponent getOrderComponent() {
        return orderComponent;
    }

    public OrderInfoComponent getOrderInfoComponent() {
        return orderInfoComponent;
    }

    public OrderPayComponent getOrderPayComponent() {
        return orderPayComponent;
    }

    /**
     * 针对数字，替换为指定的颜色
     *
     * @param src    原来的文字内容
     * @param change 是否需要改为指定的颜色
     * @return
     */
    private SpannableStringBuilder onHandlerSpanned(String src, boolean change) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        if (change) {
            Pattern p = Pattern.compile("¥*\\d+(.\\d)*(元|件)*");
            Matcher m = p.matcher(src);
            while (m.find()) {
                int start = m.start();
                if (start > 0) {
                    String jianhao = src.substring(start - 1, start);
                    if ("-".equals(jianhao)) {
                        start--;
                    }
                }
                int end = m.end();
//                if (end < src.length()) {
//                    end += 1; // 带上单位, 或者小数点
//                }
                style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        return style;
    }

    @Override
    public boolean isOrderBond() {
        return false;
    }
}

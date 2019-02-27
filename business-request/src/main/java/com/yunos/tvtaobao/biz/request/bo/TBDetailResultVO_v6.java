package com.yunos.tvtaobao.biz.request.bo;

import com.taobao.detail.domain.base.Unit;
import com.yunos.tvtaobao.biz.request.bo.resource.entrances.Entrances;
import com.yunos.tvtaobao.biz.request.bo.resource.entrances.ShopProm;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huangdaju on 16/9/19.
 */
public class TBDetailResultVO_v6 {

    private List<Unit> apiStack;
    private Trade trade;
    private Buyer buyer;
    private ConsumerProtection consumerProtection;
    private Delivery delivery;
    private Feature feature;
    private Item item;
    private Price price;
    private Resource resource;
    private Vertical vertical;
    private Layout layout;
    private ShopProm mShopProm;

    public List<Unit> getApiStack() {
        return apiStack;
    }

    public void setApiStack(List<Unit> apiStack) {
        this.apiStack = apiStack;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public ConsumerProtection getConsumerProtection() {
        return consumerProtection;
    }

    public void setConsumerProtection(ConsumerProtection consumerProtection) {
        this.consumerProtection = consumerProtection;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Vertical getVertical() {
        return vertical;
    }

    public void setVertical(Vertical vertical) {
        this.vertical = vertical;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public static class Trade implements Serializable {

    }

    public static class Buyer implements Serializable {

    }

    public static class ConsumerProtection implements Serializable {

    }

    public static class Delivery implements Serializable {

    }

    public static class Feature implements Serializable {

    }

    public static class Item implements Serializable {

    }

    public static class Price implements Serializable {

        private List<ShopProm> mShopProm;

        public List<ShopProm> getShopProm() {
            return mShopProm;
        }

        public void setShopProm(List<ShopProm> shopProm) {
            mShopProm = shopProm;
        }
    }

    public static class Resource implements Serializable {

        private Entrances entrances;

        public Entrances getEntrances() {
            return entrances;
        }

        public void setEntrances(Entrances entrances) {
            this.entrances = entrances;
        }
    }

    public static class Vertical implements Serializable {

    }

    public static class Layout implements Serializable {

    }

    @Override
    public String toString() {
        return "TBDetailResultVO_v6{" +
                "apiStack=" + apiStack +
                ", trade=" + trade +
                ", buyer=" + buyer +
                ", consumerProtection=" + consumerProtection +
                ", delivery=" + delivery +
                ", feature=" + feature +
                ", item=" + item +
                ", price=" + price +
                ", resource=" + resource +
                ", vertical=" + vertical +
                ", layout=" + layout +
                ", mShopProm=" + mShopProm +
                '}';
    }

    public ShopProm getShopProm() {
        return mShopProm;
    }

    public void setShopProm(ShopProm shopProm) {
        mShopProm = shopProm;
    }
}

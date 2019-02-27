package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by chenjiajuan on 17/12/22.
 *
 * @describe
 */

public class SkuProp {
    private String itemId;
    private String title;
    private String count;
    private String skuId;
    private String shopId;
    private List<Prop> propList;
    private boolean hasSku;
    public static class  Prop{
        private String Name;
        private String value;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Prop{" +
                    "Name='" + Name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Prop> getPropList() {
        return propList;
    }

    public void setPropList(List<Prop> propList) {
        this.propList = propList;
    }
    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public boolean isHasSku() {
        return hasSku;
    }

    public void setHasSku(boolean hasSku) {
        this.hasSku = hasSku;
    }
}

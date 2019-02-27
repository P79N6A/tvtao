package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjiajuan on 17/12/21.
 *
 * @describe
 */

public class SkuBean implements Serializable{
    private String name;
    private String price;
    private String promotionPrice;
    private String quantity;
    private String skuId;
    private String title;
    private List<SkuBean> value;
    private List<SkuBean> attrList;

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public List<SkuBean> getValue() {
        return value;
    }

    public void setValue(List<SkuBean> value) {
        this.value = value;
    }

    public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPromotionPrice() {
            return promotionPrice;
        }

        public void setPromotionPrice(String promotionPrice) {
            this.promotionPrice = promotionPrice;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    public static class ValueBean implements Serializable {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

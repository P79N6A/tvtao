package com.yunos.tvtaobao.biz.request.bo;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

import static com.yunos.tvtaobao.biz.request.bo.ItemListBean.Status.outStock;
import static com.yunos.tvtaobao.biz.request.bo.ItemListBean.Status.rest;

/**
 * Created by yuanqihui on 2018/3/1.
 */


public class ItemListBean implements Serializable {
    public enum Status {
        normal,           // 正常可购买 0选中
        edit,             // 编辑状态
        outStock,         // 没货了
        rest,             // 商家休息中
    }

    // 店铺休息
    private boolean __isRest;
    // 用户购买数量
    public int __intentCount;
    //限购数量（通过购物车列表返回）
    public int __limitQuantity = 0;
    //限购的skuid
    public long __limitSkuId = 0;

    public void setIsRest(boolean rest) {
        __isRest = rest;
    }

    public Status getGoodStatus() {

        //售空判断，针对没有sku的商品
        if (stock <= 0&&!("true".equals(hasSku))) {
            return outStock;
        }


        if (__isRest) {
            return rest;
        }

        if (__intentCount > 0) {
            return Status.edit;
        }

        return Status.normal;
    }

    /**
     * bestSelling : false
     * checkoutMode : 0
     * description : 七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮
     * flashTimeValid : false
     * hasSku : true
     * itemAttrList : []
     * itemCateId : 23265088
     * itemId : 545834024255
     * itemPicts : //gw.alicdn.com/TLife/1513071278777/TB1EFL3h4rI8KJjy0FpK_65hVXa
     * multiAttr : {"attrList":[{"name":"甜度","value":["少糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]}
     * price : 1200
     * promotionPrice : 1200
     * promotioned : false
     * saleCount : 4
     * sellerId : 2832360567
     * serviceId : 0
     * shopId : 157891380
     * skuList : [{"price":"1200","promotionPrice":"0","quantity":"9989","skuId":"3537775644307","title":"中杯"},{"price":"1500","promotionPrice":"0","quantity":"9985","skuId":"3537775644308","title":"大杯"}]
     * [{text: "5折,限10份", type: "1"}]
     * soldMode : 1
     * status : 1
     * stock : 19974
     * storeId : 157891380
     * title : 阿华田
     */

    private String originStock;
    private String bestSelling;
    private String checkoutMode;
    private String description;
    private String flashTimeValid;
    private String hasSku;
    private String itemCateId;
    private String itemId;
    private String itemPicts;
    private MultiAttrBean multiAttr;
    private String price;
    private String promotionPrice;
    private String promotioned;
    private String saleCount;
    private String sellerId;
    private String serviceId;
    private String shopId;
    private String soldMode;
    private String status;
    private int stock;
    private String storeId;
    private String title;
    private List<ItemAttrListBean> itemAttrList;
    private List<SkuListBean> skuList;
    public List<Tag> tagList;


    public static class Tag implements Serializable{
        public String subText;
        public String text;
        public String type;
    }


    public String getOriginStock() {
        return originStock;
    }

    public void setOriginStock(String originStock) {
        this.originStock = originStock;
    }

    public String getBestSelling() {
        return bestSelling;
    }

    public void setBestSelling(String bestSelling) {
        this.bestSelling = bestSelling;
    }

    public String getCheckoutMode() {
        return checkoutMode;
    }

    public void setCheckoutMode(String checkoutMode) {
        this.checkoutMode = checkoutMode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFlashTimeValid() {
        return flashTimeValid;
    }

    public void setFlashTimeValid(String flashTimeValid) {
        this.flashTimeValid = flashTimeValid;
    }

    public String getHasSku() {
        return hasSku;
    }

    public void setHasSku(String hasSku) {
        this.hasSku = hasSku;
    }

    public String getItemCateId() {
        return itemCateId;
    }

    public void setItemCateId(String itemCateId) {
        this.itemCateId = itemCateId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemPicts() {
        return itemPicts;
    }

    public void setItemPicts(String itemPicts) {
        this.itemPicts = itemPicts;
    }

    public MultiAttrBean getMultiAttr() {
        return multiAttr;
    }

    public void setMultiAttr(MultiAttrBean multiAttr) {
        this.multiAttr = multiAttr;
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

    public String getPromotioned() {
        return promotioned;
    }

    public void setPromotioned(String promotioned) {
        this.promotioned = promotioned;
    }

    public String getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(String saleCount) {
        this.saleCount = saleCount;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getSoldMode() {
        return soldMode;
    }

    public void setSoldMode(String soldMode) {
        this.soldMode = soldMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ItemAttrListBean> getItemAttrList() {
        return itemAttrList;
    }

    public void setItemAttrList(List<ItemAttrListBean> itemAttrList) {
        this.itemAttrList = itemAttrList;
    }

    public List<SkuListBean> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<SkuListBean> skuList) {
        this.skuList = skuList;
    }

    public static class MultiAttrBean implements Serializable {
        private List<AttrListBean> attrList;

        public List<AttrListBean> getAttrList() {
            return attrList;
        }

        public void setAttrList(List<AttrListBean> attrList) {
            this.attrList = attrList;
        }

        public static class AttrListBean implements Serializable {
            /**
             * name : 甜度
             * value : ["少糖","标准糖","多糖"]
             */

            private String name;
            private List<String> value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<String> getValue() {
                return value;
            }

            public void setValue(List<String> value) {
                this.value = value;
            }
        }
    }

    public static class SkuListBean implements Serializable {
        /**
         * price : 1200
         * promotionPrice : 0
         * quantity : 9989
         * skuId : 3537775644307
         * title : 中杯
         */

        private String price;
        private String promotionPrice;
        private String quantity;
        private String skuId;
        private String title;

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

        @Override
        public String toString() {
            return "SkuListBean{" +
                    "price='" + price + '\'' +
                    ", promotionPrice='" + promotionPrice + '\'' +
                    ", quantity='" + quantity + '\'' +
                    ", skuId='" + skuId + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public static class ItemAttrListBean implements Serializable {
        /**
         * attrType : 1
         * desc :
         * icon : //gw.alicdn.com/tps/TB1_TQLKFXXXXbYXFXXXXXXXXXX-72-42.png
         * sortId : 0
         */

        private String attrType;
        private String desc;
        private String icon;
        private String sortId;

        public String getAttrType() {
            return attrType;
        }

        public void setAttrType(String attrType) {
            this.attrType = attrType;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getSortId() {
            return sortId;
        }

        public void setSortId(String sortId) {
            this.sortId = sortId;
        }
    }
}
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;

/**
 * Created by dingbin on 2017/5/31.
 * apistack这个字段挂掉了，mockData这个会作为替补
 */

public class MockData implements Serializable {


    /**
     * delivery : {}
     * feature : {"hasSku":true,"showSku":true}
     * price : {"price":{"priceText":"186.50"}}
     * skuCore : {"sku2info":{"0":{"price":{"priceMoney":18650,"priceText":"186.50","priceTitle":"价格"},"quantity":300},"3290521132334":{"price":{"priceMoney":18650,"priceText":"186.50","priceTitle":"价格"},"quantity":100},"3290521132335":{"price":{"priceMoney":21125,"priceText":"211.25","priceTitle":"价格"},"quantity":100},"3290521132336":{"price":{"priceMoney":22375,"priceText":"223.75","priceTitle":"价格"},"quantity":100}},"skuItem":{"hideQuantity":true}}
     * trade : {"buyEnable":true,"cartEnable":true}
     */

    private DeliveryBean delivery;
    private FeatureBean feature;
    private PriceBeanX price;
    private SkuCoreBean skuCore;
    private TradeBean trade;

    public DeliveryBean getDelivery() {
        return delivery;
    }

    public void setDelivery(DeliveryBean delivery) {
        this.delivery = delivery;
    }

    public FeatureBean getFeature() {
        return feature;
    }

    public void setFeature(FeatureBean feature) {
        this.feature = feature;
    }

    public PriceBeanX getPrice() {
        return price;
    }

    public void setPrice(PriceBeanX price) {
        this.price = price;
    }

    public SkuCoreBean getSkuCore() {
        return skuCore;
    }

    public void setSkuCore(SkuCoreBean skuCore) {
        this.skuCore = skuCore;
    }

    public TradeBean getTrade() {
        return trade;
    }

    public void setTrade(TradeBean trade) {
        this.trade = trade;
    }

    public static class DeliveryBean {
    }

    public static class FeatureBean {
        /**
         * hasSku : true 商品是否有sku
         * showSku : true 前段根据是否有这个字段决定是否需要唤起sku
         * hasCoupon: 是否有优惠券
         */

        private boolean hasSku;
        private boolean showSku;
        private String hasCoupon;

        public String getHasCoupon() {
            return hasCoupon;
        }

        public void setHasCoupon(String hasCoupon) {
            this.hasCoupon = hasCoupon;
        }

        public boolean isHasSku() {
            return hasSku;
        }

        public void setHasSku(boolean hasSku) {
            this.hasSku = hasSku;
        }

        public boolean isShowSku() {
            return showSku;
        }

        public void setShowSku(boolean showSku) {
            this.showSku = showSku;
        }
    }

    public static class PriceBeanX {
        /**
         * price : {"priceText":"186.50"}
         */

        private PriceBean price;

        public PriceBean getPrice() {
            return price;
        }

        public void setPrice(PriceBean price) {
            this.price = price;
        }

        public static class PriceBean {
            /**
             * priceText : 186.50 直接使用这个字段显示价格
             */

            private String priceText;

            public String getPriceText() {
                return priceText;
            }

            public void setPriceText(String priceText) {
                this.priceText = priceText;
            }
        }
    }

    public static class SkuCoreBean {
        /**
         * sku2info : {"0":{"price":{"priceMoney":18650,"priceText":"186.50","priceTitle":"价格"},"quantity":300},"3290521132334":{"price":{"priceMoney":18650,"priceText":"186.50","priceTitle":"价格"},"quantity":100},"3290521132335":{"price":{"priceMoney":21125,"priceText":"211.25","priceTitle":"价格"},"quantity":100},"3290521132336":{"price":{"priceMoney":22375,"priceText":"223.75","priceTitle":"价格"},"quantity":100}}
         * skuItem : {"hideQuantity":true}
         */

        private Sku2infoBean sku2info;
        private SkuItemBean skuItem;

        public Sku2infoBean getSku2info() {
            return sku2info;
        }

        public void setSku2info(Sku2infoBean sku2info) {
            this.sku2info = sku2info;
        }

        public SkuItemBean getSkuItem() {
            return skuItem;
        }

        public void setSkuItem(SkuItemBean skuItem) {
            this.skuItem = skuItem;
        }

        public static class Sku2infoBean {
            /**
             * 0 : {"price":{"priceMoney":18650,"priceText":"186.50","priceTitle":"价格"},"quantity":300}
             * 3290521132334 : {"price":{"priceMoney":18650,"priceText":"186.50","priceTitle":"价格"},"quantity":100}
             * 3290521132335 : {"price":{"priceMoney":21125,"priceText":"211.25","priceTitle":"价格"},"quantity":100}
             * 3290521132336 : {"price":{"priceMoney":22375,"priceText":"223.75","priceTitle":"价格"},"quantity":100}
             */

            private _$0Bean _$0;

            public _$0Bean get_$0() {
                return _$0;
            }

            public void set_$0(_$0Bean _$0) {
                this._$0 = _$0;
            }

            public static class _$0Bean {
                /**
                 * price : {"priceMoney":18650,"priceText":"186.50","priceTitle":"价格"}
                 * quantity : 300
                 */

                private PriceBeanXX price;
                private int quantity;

                public PriceBeanXX getPrice() {
                    return price;
                }

                public void setPrice(PriceBeanXX price) {
                    this.price = price;
                }

                public int getQuantity() {
                    return quantity;
                }

                public void setQuantity(int quantity) {
                    this.quantity = quantity;
                }

                public static class PriceBeanXX {
                    /**
                     * priceMoney : 18650
                     * priceText : 186.50
                     * priceTitle : 价格
                     */

                    private int priceMoney;
                    private String priceText;
                    private String priceTitle;

                    public int getPriceMoney() {
                        return priceMoney;
                    }

                    public void setPriceMoney(int priceMoney) {
                        this.priceMoney = priceMoney;
                    }

                    public String getPriceText() {
                        return priceText;
                    }

                    public void setPriceText(String priceText) {
                        this.priceText = priceText;
                    }

                    public String getPriceTitle() {
                        return priceTitle;
                    }

                    public void setPriceTitle(String priceTitle) {
                        this.priceTitle = priceTitle;
                    }
                }
            }

        }

        public static class SkuItemBean {
            /**
             * hideQuantity : true
             */

            private boolean hideQuantity;

            public boolean isHideQuantity() {
                return hideQuantity;
            }

            public void setHideQuantity(boolean hideQuantity) {
                this.hideQuantity = hideQuantity;
            }
        }
    }

    public static class TradeBean {
        /**
         * buyEnable : true  购买按钮是否可用
         * cartEnable : true 购物车按钮是否可用
         */

        private boolean buyEnable;
        private boolean cartEnable;

        public boolean isBuyEnable() {
            return buyEnable;
        }

        public void setBuyEnable(boolean buyEnable) {
            this.buyEnable = buyEnable;
        }

        public boolean isCartEnable() {
            return cartEnable;
        }

        public void setCartEnable(boolean cartEnable) {
            this.cartEnable = cartEnable;
        }
    }
}

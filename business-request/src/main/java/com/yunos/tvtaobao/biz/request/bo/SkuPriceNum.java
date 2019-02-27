package com.yunos.tvtaobao.biz.request.bo;

import java.math.BigDecimal;

/**
 * Created by dingbin on 2017/5/18.
 */

public class SkuPriceNum {
    @Override
    public String toString() {
        return "SkuPriceNum{" +
                "price=" + price +
                ", quantity=" + quantity +
                ", limit=" + limit +
                '}';
    }

    /**
     * price : {"priceMoney":28300,"priceText":"283.00","priceTitle":"价格"}
     * quantity : 100
     */

    private PriceBean price;
    private  SubPrice subPrice;
    private int quantity;
    private int limit;

    public SubPrice getSubPrice() {
        return subPrice;
    }

    public void setSubPrice(SubPrice subPrice) {
        this.subPrice = subPrice;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public PriceBean getPrice() {
        return price;
    }

    public void setPrice(PriceBean price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static class SubPrice{
        /**
         *  "priceMoney":"5000",
         "priceText":"50",
         "priceTitle":"定金",
         "showTitle":"true",
         "sugProm":"false"
         */
        private int priceMoney;
        private String priceText;
        private String priceTitle;
        private boolean showTitle;
        private boolean sugProm;

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

        public boolean isShowTitle() {
            return showTitle;
        }

        public void setShowTitle(boolean showTitle) {
            this.showTitle = showTitle;
        }

        public boolean isSugProm() {
            return sugProm;
        }

        public void setSugProm(boolean sugProm) {
            this.sugProm = sugProm;
        }
    }

    public static class PriceBean {
        /**
         * priceMoney : 28300
         * priceText : 283.00
         * priceTitle : 价格
         */

        private BigDecimal priceMoney;
        private String priceText;
        private String priceTitle;

        public BigDecimal getPriceMoney() {
            return priceMoney;
        }

        public void setPriceMoney(BigDecimal priceMoney) {
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

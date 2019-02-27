package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by linmu on 2018/9/12.
 */

public class BuildOrderRebateBo {


        private String coupon;
        private String orderId;
        private String payment;
        private String message;
        private String picUrl;
        private List<TradeDetailCoupon> tradeDetailCoupon;

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }
        public String getCoupon() {
            return coupon;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        public String getOrderId() {
            return orderId;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }
        public String getPayment() {
            return payment;
        }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setTradeDetailCoupon(List<TradeDetailCoupon> tradeDetailCoupon) {
            this.tradeDetailCoupon = tradeDetailCoupon;
        }
        public List<TradeDetailCoupon> getTradeDetailCoupon() {
            return tradeDetailCoupon;
        }

    }

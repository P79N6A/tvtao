package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by pan on 16/10/12.
 */

public class PromotionBean {

    /**
     * status : 0
     * msg : success.
     * data : {"ali_spread_id":"554680710","ali_seller_id":"2041482913","spread_id":"AAEK2ls7ABd3fHvb6NXDN0hv","amount":300,"coupon_type_name":"店铺优惠券","shop_name":"尊客母婴专营店","start_time":"2016-06-21T00:00:00+08:00","end_time":"2016-12-21T23:59:59+08:00","start_fee":6900,"title":"【会员专享】3元券"}
     */

    private int status;
    private String msg;
    /**
     * ali_spread_id : 554680710
     * ali_seller_id : 2041482913
     * spread_id : AAEK2ls7ABd3fHvb6NXDN0hv
     * amount : 300
     * coupon_type_name : 店铺优惠券
     * shop_name : 尊客母婴专营店
     * start_time : 2016-06-21T00:00:00+08:00
     * end_time : 2016-12-21T23:59:59+08:00
     * start_fee : 6900
     * title : 【会员专享】3元券
     */

    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String ali_spread_id;
        private String ali_seller_id;
        private String spread_id;
        private int amount;
        private String coupon_type_name;
        private String shop_name;
        private String start_time;
        private String end_time;
        private int start_fee;
        private String title;

        public String getAli_spread_id() {
            return ali_spread_id;
        }

        public void setAli_spread_id(String ali_spread_id) {
            this.ali_spread_id = ali_spread_id;
        }

        public String getAli_seller_id() {
            return ali_seller_id;
        }

        public void setAli_seller_id(String ali_seller_id) {
            this.ali_seller_id = ali_seller_id;
        }

        public String getSpread_id() {
            return spread_id;
        }

        public void setSpread_id(String spread_id) {
            this.spread_id = spread_id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getCoupon_type_name() {
            return coupon_type_name;
        }

        public void setCoupon_type_name(String coupon_type_name) {
            this.coupon_type_name = coupon_type_name;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public int getStart_fee() {
            return start_fee;
        }

        public void setStart_fee(int start_fee) {
            this.start_fee = start_fee;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}

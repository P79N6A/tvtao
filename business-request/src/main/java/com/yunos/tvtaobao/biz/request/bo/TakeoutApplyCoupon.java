package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/25
 * @Desc：
 */
public class TakeoutApplyCoupon {
    /**
     * coupons : [{"amount":"100","availableFee":"10","condition":"10","conditionText":"满0.1元使用","couponShowType":"0","endTime":"2018-07-01 17:31:10","id":"1098317510376572","scope":"淘宝外卖通用","source":"TAOBAO","startTime":"2018-06-28 17:31:10","title":"alitvtest","type":"100132","waimaiNewGuest":"false"},{"amount":"200","availableFee":"20","condition":"20","conditionText":"满0.2元使用","couponShowType":"0","endTime":"2018-07-01 17:31:10","id":"1098317311066572","scope":"淘宝外卖通用","source":"TAOBAO","startTime":"2018-06-28 17:31:10","title":"alitvtest1","type":"100132","waimaiNewGuest":"false"},{"amount":"400","availableFee":"10","condition":"10","conditionText":"满0.1元使用","couponShowType":"0","endTime":"2018-07-01 17:31:10","id":"1098317510376572","scope":"淘宝外卖通用","source":"TAOBAO","startTime":"2018-06-28 17:31:10","title":"alitvtest","type":"100132","waimaiNewGuest":"false"},{"amount":"20","availableFee":"20","condition":"20","conditionText":"满0.2元使用","couponShowType":"0","endTime":"2018-07-01 17:31:10","id":"1098317311066572","scope":"淘宝外卖通用","source":"TAOBAO","startTime":"2018-06-28 17:31:10","title":"alitvtest1","type":"100132","waimaiNewGuest":"false"},{"amount":"10","availableFee":"10","condition":"10","conditionText":"满0.1元使用","couponShowType":"0","endTime":"2018-07-01 17:31:10","id":"1098317510376572","scope":"淘宝外卖通用","source":"TAOBAO","startTime":"2018-06-28 17:31:10","title":"alitvtest","type":"100132","waimaiNewGuest":"false"}]
     * features : {"isTaobaoNewGuest":"false","isElemeNewGuest":"false","mobile":"17681886382"}
     * newRetCode : 200
     * newRetMsg : success
     * retCode : 200
     * showStyle : {"styleType":"1"}
     */

    private FeaturesBean features;
    private String newRetCode;
    private String newRetMsg;
    private String retCode;
    private ShowStyleBean showStyle;
    private List<CouponsBean> coupons;

    public FeaturesBean getFeatures() {
        return features;
    }

    public void setFeatures(FeaturesBean features) {
        this.features = features;
    }

    public String getNewRetCode() {
        return newRetCode;
    }

    public void setNewRetCode(String newRetCode) {
        this.newRetCode = newRetCode;
    }

    public String getNewRetMsg() {
        return newRetMsg;
    }

    public void setNewRetMsg(String newRetMsg) {
        this.newRetMsg = newRetMsg;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public ShowStyleBean getShowStyle() {
        return showStyle;
    }

    public void setShowStyle(ShowStyleBean showStyle) {
        this.showStyle = showStyle;
    }

    public List<CouponsBean> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponsBean> coupons) {
        this.coupons = coupons;
    }

    public static class FeaturesBean {
        /**
         * isTaobaoNewGuest : false
         * isElemeNewGuest : false
         * mobile : 17681886382
         */

        private String isTaobaoNewGuest;
        private String isElemeNewGuest;
        private String mobile;

        public String getIsTaobaoNewGuest() {
            return isTaobaoNewGuest;
        }

        public void setIsTaobaoNewGuest(String isTaobaoNewGuest) {
            this.isTaobaoNewGuest = isTaobaoNewGuest;
        }

        public String getIsElemeNewGuest() {
            return isElemeNewGuest;
        }

        public void setIsElemeNewGuest(String isElemeNewGuest) {
            this.isElemeNewGuest = isElemeNewGuest;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    public static class ShowStyleBean {
        /**
         * styleType : 1
         */

        private String styleType;

        public String getStyleType() {
            return styleType;
        }

        public void setStyleType(String styleType) {
            this.styleType = styleType;
        }
    }

    public static class CouponsBean {
        /**
         * amount : 100
         * availableFee : 10
         * condition : 10
         * conditionText : 满0.1元使用
         * couponShowType : 0
         * endTime : 2018-07-01 17:31:10
         * id : 1098317510376572
         * scope : 淘宝外卖通用
         * source : TAOBAO
         * startTime : 2018-06-28 17:31:10
         * title : alitvtest
         * type : 100132
         * waimaiNewGuest : false
         */

        private String amount;
        private String availableFee;
        private String condition;
        private String conditionText;
        private String couponShowType;
        private String endTime;
        private String id;
        private String scope;
        private String source;
        private String startTime;
        private String title;
        private String type;
        private String waimaiNewGuest;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getAvailableFee() {
            return availableFee;
        }

        public void setAvailableFee(String availableFee) {
            this.availableFee = availableFee;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getConditionText() {
            return conditionText;
        }

        public void setConditionText(String conditionText) {
            this.conditionText = conditionText;
        }

        public String getCouponShowType() {
            return couponShowType;
        }

        public void setCouponShowType(String couponShowType) {
            this.couponShowType = couponShowType;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getWaimaiNewGuest() {
            return waimaiNewGuest;
        }

        public void setWaimaiNewGuest(String waimaiNewGuest) {
            this.waimaiNewGuest = waimaiNewGuest;
        }
    }
}


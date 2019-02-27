package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by chenjiajuan on 17/12/25.
 *
 * @describe  外卖-优惠券
 */

public class VouchersMO {
        /**
         * desc : 成功领取到3张代金券
         * details : [{"beginTime":"2017.12.25 00:00","deductAmount":"1200","deductThreshold":"16800","endTime":"2018.01.09 23:59","serviceId":"62","status":"1","storeId":"168639074","storeName":"八鲜过海夜宵","voucherId":"53388022","voucherType":"1"},{"beginTime":"2017.12.25 00:00","deductAmount":"1050","deductThreshold":"15800","endTime":"2018.01.09 23:59","serviceId":"62","status":"1","storeId":"168639074","storeName":"八鲜过海夜宵","voucherId":"53388023","voucherType":"1"},{"beginTime":"2017.12.25 00:00","deductAmount":"1210","deductThreshold":"16800","endTime":"2018.01.09 23:59","serviceId":"62","status":"1","storeId":"168639074","storeName":"八鲜过海夜宵","voucherId":"53388024","voucherType":"1"}]
         * latestStatus : {"conditionDesc":"内含3张代金券","deductDesc":"￥34.6","description":"商家代金券","icon":"//gw.alicdn.com/tfs/TB1AxEXRXXXXXXtaXXXXXXXXXXX-54-54.png","status":"2","statusContent":"已领取","storeId":"168639074","storeName":"八鲜过海夜宵","targetUrl":"https://h5.m.taobao.com/takeout/market/myCoupon.html#/list/shopCoupon"}
         * status : 1
         * thresholdDesc : 使用门槛指商品总价与餐盒费的总和
         */
        private String desc;
        private LatestStatusBean latestStatus;
        private String status;
        private String thresholdDesc;
        private List<DetailsBean> details;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public LatestStatusBean getLatestStatus() {
            return latestStatus;
        }

        public void setLatestStatus(LatestStatusBean latestStatus) {
            this.latestStatus = latestStatus;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getThresholdDesc() {
            return thresholdDesc;
        }

        public void setThresholdDesc(String thresholdDesc) {
            this.thresholdDesc = thresholdDesc;
        }

        public List<DetailsBean> getDetails() {
            return details;
        }

        public void setDetails(List<DetailsBean> details) {
            this.details = details;
        }

        public static class LatestStatusBean {
            /**
             * conditionDesc : 内含3张代金券
             * deductDesc : ￥34.6
             * description : 商家代金券
             * icon : //gw.alicdn.com/tfs/TB1AxEXRXXXXXXtaXXXXXXXXXXX-54-54.png
             * status : 2
             * statusContent : 已领取
             * storeId : 168639074
             * storeName : 八鲜过海夜宵
             * targetUrl : https://h5.m.taobao.com/takeout/market/myCoupon.html#/list/shopCoupon
             */

            private String conditionDesc;
            private String deductDesc;
            private String description;
            private String icon;
            private String status;
            private String statusContent;
            private String storeId;
            private String storeName;
            private String targetUrl;

            public String getConditionDesc() {
                return conditionDesc;
            }

            public void setConditionDesc(String conditionDesc) {
                this.conditionDesc = conditionDesc;
            }

            public String getDeductDesc() {
                return deductDesc;
            }

            public void setDeductDesc(String deductDesc) {
                this.deductDesc = deductDesc;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStatusContent() {
                return statusContent;
            }

            public void setStatusContent(String statusContent) {
                this.statusContent = statusContent;
            }

            public String getStoreId() {
                return storeId;
            }

            public void setStoreId(String storeId) {
                this.storeId = storeId;
            }

            public String getStoreName() {
                return storeName;
            }

            public void setStoreName(String storeName) {
                this.storeName = storeName;
            }

            public String getTargetUrl() {
                return targetUrl;
            }

            public void setTargetUrl(String targetUrl) {
                this.targetUrl = targetUrl;
            }
        }

        public static class DetailsBean {
            /**
             * beginTime : 2017.12.25 00:00
             * deductAmount : 1200
             * deductThreshold : 16800
             * endTime : 2018.01.09 23:59
             * serviceId : 62
             * status : 1
             * storeId : 168639074
             * storeName : 八鲜过海夜宵
             * voucherId : 53388022
             * voucherType : 1
             */

            private String beginTime;
            private String deductAmount;
            private String deductThreshold;
            private String endTime;
            private String serviceId;
            private String status;
            private String storeId;
            private String storeName;
            private String voucherId;
            private String voucherType;

            public String getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(String beginTime) {
                this.beginTime = beginTime;
            }

            public String getDeductAmount() {
                return deductAmount;
            }

            public void setDeductAmount(String deductAmount) {
                this.deductAmount = deductAmount;
            }

            public String getDeductThreshold() {
                return deductThreshold;
            }

            public void setDeductThreshold(String deductThreshold) {
                this.deductThreshold = deductThreshold;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getServiceId() {
                return serviceId;
            }

            public void setServiceId(String serviceId) {
                this.serviceId = serviceId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStoreId() {
                return storeId;
            }

            public void setStoreId(String storeId) {
                this.storeId = storeId;
            }

            public String getStoreName() {
                return storeName;
            }

            public void setStoreName(String storeName) {
                this.storeName = storeName;
            }

            public String getVoucherId() {
                return voucherId;
            }

            public void setVoucherId(String voucherId) {
                this.voucherId = voucherId;
            }

            public String getVoucherType() {
                return voucherType;
            }

            public void setVoucherType(String voucherType) {
                this.voucherType = voucherType;
            }
        }
}

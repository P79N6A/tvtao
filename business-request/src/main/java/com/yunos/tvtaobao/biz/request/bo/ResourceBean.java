package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by dingbin on 2017/6/5.
 */

public class ResourceBean {

    /**
     * entrances : {"coupon":{"icon":"//img.alicdn.com/tps/TB1okcBKVXXXXbBXVXXXXXXXXXX-116-32.png","text":"全天猫实物商品通用","linkText":"去刮券"},"tmallCoupon":{"icon":"//img.alicdn.com/tps/TB1okcBKVXXXXbBXVXXXXXXXXXX-116-32.png","text":"全天猫实物商品通用","linkText":"去刮券"}}
     * coupon : {}
     */

    private EntrancesBean entrances;
    private CouponBeanX coupon;

    public EntrancesBean getEntrances() {
        return entrances;
    }

    public void setEntrances(EntrancesBean entrances) {
        this.entrances = entrances;
    }

    public CouponBeanX getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponBeanX coupon) {
        this.coupon = coupon;
    }

    public static class EntrancesBean {
        /**
         * coupon : {"icon":"//img.alicdn.com/tps/TB1okcBKVXXXXbBXVXXXXXXXXXX-116-32.png","text":"全天猫实物商品通用","linkText":"去刮券"}
         * tmallCoupon : {"icon":"//img.alicdn.com/tps/TB1okcBKVXXXXbBXVXXXXXXXXXX-116-32.png","text":"全天猫实物商品通用","linkText":"去刮券"}
         */

        private CouponBean coupon;
        private TmallCouponBean tmallCoupon;

        public CouponBean getCoupon() {
            return coupon;
        }

        public void setCoupon(CouponBean coupon) {
            this.coupon = coupon;
        }

        public TmallCouponBean getTmallCoupon() {
            return tmallCoupon;
        }

        public void setTmallCoupon(TmallCouponBean tmallCoupon) {
            this.tmallCoupon = tmallCoupon;
        }

        public static class CouponBean {
            /**
             * icon : //img.alicdn.com/tps/TB1okcBKVXXXXbBXVXXXXXXXXXX-116-32.png
             * text : 全天猫实物商品通用
             * linkText : 去刮券
             */

            private String icon;
            private String text;
            private String linkText;

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getLinkText() {
                return linkText;
            }

            public void setLinkText(String linkText) {
                this.linkText = linkText;
            }
        }

        public static class TmallCouponBean {
            /**
             * icon : //img.alicdn.com/tps/TB1okcBKVXXXXbBXVXXXXXXXXXX-116-32.png
             * text : 全天猫实物商品通用
             * linkText : 去刮券
             */

            private String icon;
            private String text;
            private String linkText;

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getLinkText() {
                return linkText;
            }

            public void setLinkText(String linkText) {
                this.linkText = linkText;
            }
        }
    }

    public static class CouponBeanX {
    }
}

package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by libin on 16/9/26.
 */

public class HasActivieBean {

    /**
     * background : https://img.alicdn.com/tps/TB1Ph_JNpXXXXctXFXXXXXXXXXX-210-212.png
     * content : 红包百万赶紧抢啊
     * title : 确定
     */

    private UpgradeBean upgrade;
    /**
     * date_only_create_order_start : 20161101
     * date_only_create_order_end : 20161112
     * bool_shop_cart_merge_orders : true
     */

    private Double11ShopCartBean double11_shop_cart;
    /**
     * is_acting : true
     * detail_icon : https://img.alicdn.com/tps/TB1Ph_JNpXXXXctXFXXXXXXXXXX-210-212.png
     * shop_cat_icon : https://img.alicdn.com/tps/TB1Ph_JNpXXXXctXFXXXXXXXXXX-210-212.png
     * side_bar_icon : https://img.alicdn.com/tps/TB1Ph_JNpXXXXctXFXXXXXXXXXX-210-212.png
     */

    private ShopCartFlagBean shop_cart_flag;

    public UpgradeBean getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(UpgradeBean upgrade) {
        this.upgrade = upgrade;
    }

    public Double11ShopCartBean getDouble11_shop_cart() {
        return double11_shop_cart;
    }

    public void setDouble11_shop_cart(Double11ShopCartBean double11_shop_cart) {
        this.double11_shop_cart = double11_shop_cart;
    }

    public ShopCartFlagBean getShop_cart_flag() {
        return shop_cart_flag;
    }

    public void setShop_cart_flag(ShopCartFlagBean shop_cart_flag) {
        this.shop_cart_flag = shop_cart_flag;
    }

    public static class UpgradeBean {
        private String background;
        private String content;
        private String title;

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class Double11ShopCartBean {
        private String date_only_create_order_start;
        private String date_only_create_order_end;
        private boolean bool_shop_cart_merge_orders;

        public String getDate_only_create_order_start() {
            return date_only_create_order_start;
        }

        public void setDate_only_create_order_start(String date_only_create_order_start) {
            this.date_only_create_order_start = date_only_create_order_start;
        }

        public String getDate_only_create_order_end() {
            return date_only_create_order_end;
        }

        public void setDate_only_create_order_end(String date_only_create_order_end) {
            this.date_only_create_order_end = date_only_create_order_end;
        }

        public boolean isBool_shop_cart_merge_orders() {
            return bool_shop_cart_merge_orders;
        }

        public void setBool_shop_cart_merge_orders(boolean bool_shop_cart_merge_orders) {
            this.bool_shop_cart_merge_orders = bool_shop_cart_merge_orders;
        }
    }

    public static class ShopCartFlagBean {
        private boolean is_acting;
        private String detail_icon;
        private String shop_cat_icon;
        private String side_bar_icon;

        public boolean isIs_acting() {
            return is_acting;
        }

        public void setIs_acting(boolean is_acting) {
            this.is_acting = is_acting;
        }

        public String getDetail_icon() {
            return detail_icon;
        }

        public void setDetail_icon(String detail_icon) {
            this.detail_icon = detail_icon;
        }

        public String getShop_cat_icon() {
            return shop_cat_icon;
        }

        public void setShop_cat_icon(String shop_cat_icon) {
            this.shop_cat_icon = shop_cat_icon;
        }

        public String getSide_bar_icon() {
            return side_bar_icon;
        }

        public void setSide_bar_icon(String side_bar_icon) {
            this.side_bar_icon = side_bar_icon;
        }
    }
}

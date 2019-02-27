package com.yunos.tvtaobao.biz.request.bo;


/**
 * Created by chenjiajuan on 17/12/22.
 *
 * @describe
 */

public class AddBagBo {
        /**
         * itemId : 545760438971
         * cartId : 703450040067
         * addedTotalItemPrice : 15.00
         * skuId : 3304349860622
         * cartQuantity : 8
         */

        private long itemId;
        private long cartId;
        private String addedTotalItemPrice;
        private long skuId;
        private int cartQuantity;

        public long getItemId() {
            return itemId;
        }

        public void setItemId(long itemId) {
            this.itemId = itemId;
        }

        public long getCartId() {
            return cartId;
        }

        public void setCartId(long cartId) {
            this.cartId = cartId;
        }

        public String getAddedTotalItemPrice() {
            return addedTotalItemPrice;
        }

        public void setAddedTotalItemPrice(String addedTotalItemPrice) {
            this.addedTotalItemPrice = addedTotalItemPrice;
        }

        public long getSkuId() {
            return skuId;
        }

        public void setSkuId(long skuId) {
            this.skuId = skuId;
        }

        public int getCartQuantity() {
            return cartQuantity;
        }

        public void setCartQuantity(int cartQuantity) {
            this.cartQuantity = cartQuantity;
        }
}

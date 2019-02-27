package com.yunos.tvtaobao.newcart.entity;

import com.taobao.wireless.trade.mcart.sdk.co.biz.ShopComponent;

import java.util.List;

/**
 * Created by yuanqihui on 2018/7/10.
 */

public class ShopBean {

    public boolean isInValid;
    public ShopComponent shopComponent;
    public List<CartGoodsComponent> cartGoodsComponents;

    public boolean isInValid() {
        return isInValid;
    }

    public void setInValid(boolean inValid) {
        isInValid = inValid;
    }

    public ShopComponent getShopComponent() {
        return shopComponent;
    }

    public void setShopComponent(ShopComponent shopComponent) {
        this.shopComponent = shopComponent;
    }

    public List<CartGoodsComponent> getCartGoodsComponents() {
        return cartGoodsComponents;
    }

    public void setCartGoodsComponents(List<CartGoodsComponent> cartGoodsComponents) {
        this.cartGoodsComponents = cartGoodsComponents;
    }
}

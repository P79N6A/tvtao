package com.yunos.tvtaobao.newcart.entity;

import java.io.Serializable;

/**
 * Created by yuanqihui on 2018/6/27.
 */

public class ShopCartGoodsBean implements Serializable{
    private boolean invalid;
    private CartGoodsComponent cartGoodsComponent;

    @Override
    public String toString() {
        return "ShopCartGoodsBean{" +
                "invalid=" + invalid +
                ", cartGoodsComponent=" + cartGoodsComponent +
                '}';
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public CartGoodsComponent getCartGoodsComponent() {
        return cartGoodsComponent;
    }

    public void setCartGoodsComponent(CartGoodsComponent cartGoodsComponent) {
        this.cartGoodsComponent = cartGoodsComponent;
    }
}

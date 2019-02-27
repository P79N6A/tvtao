package com.yunos.tvtaobao.tradelink.resconfig;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

public interface IResConfig {

    /**
     * 配置类型
     */
    enum GoodsType {
        TAOBAO, TMALL
    }

    public Context getContext();

    /**
     * 设置商品是否可购买
     */
    public void setCanBuy(boolean canBuy);

    /**
     * 设置即将购买
     * @param buyStatus
     */
    public void setGreenStatus(String buyStatus);

    /**
     * 商品类型，商城还是淘宝
     * @return
     */
    public GoodsType getGoodsType();

    /**
     * 获取主色调
     * @return
     */
    public int getColor();



    /**
     * 获取二维码中心的icon
     * @return
     */
    public Drawable getQRCodeIcon();



}

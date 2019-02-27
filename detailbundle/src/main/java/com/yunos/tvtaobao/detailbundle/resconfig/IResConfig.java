package com.yunos.tvtaobao.detailbundle.resconfig;


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
     * 购买图标
     * @return
     */
    public Drawable getBuyIcon();

    /**
     * 购买图标背景
     * @return
     */
    public Drawable getBuyBgIcon();

    /**
     * 购买字体的阴影
     * @return
     */
    public int getBuyTextShadowColor();

    /**
     * 购买图标背景焦点
     * @return
     */
    public Drawable getBuyBgFocusIcon();

    /**
     * 滚动条样式
     * @return
     */
    public Drawable getScrollIcon();

    /**
     * 按钮选中框
     * @return
     */
    public Drawable getButtonSelectedDraw();

    /**
     * 按钮未选中框
     * @return
     */
    public Drawable getButtonNormalDraw();

    /**
     * 购物车图标
     * @return
     */
    public Drawable getCartIcon();

    /**
     * 购物车图标焦点
     * @return
     */
    public Drawable getCartFocusIcon();

    /**
     * 扫码图标
     * @return
     */
    public Drawable getScanCodeIcon();

    /**
     * 扫码图标焦点
     * @return
     */
    public Drawable getScanCodeFocusIcon();

    /**
     * 获取二维码中心的icon
     * @return
     */
    public Drawable getQRCodeIcon();

    /**
     * 收藏图标
     * @return
     */
    public Drawable getFavIcon();

    /**
     * 收藏图标-焦点
     * @return
     */
    public Drawable getFavFocusIcon();

    /**
     * 店铺首页图标
     * @return
     */
    public Drawable getShopIndexIcon();

    /**
     * 店铺首页图标-焦点
     * @return
     */
    public Drawable getShopIndexFocusIcon();

    /**
     * 获取字体着色
     * @return
     */
    public ColorStateList getFontColor();

    /**
     * 获取相关焦点框样式
     * @return
     */
    public Drawable getRelevantSelectDraw();

    /**
     * 获取商品评价是否显示
     * @return
     */
    public int getGoodsEvaluateVisible();

    /**
     * 评价选中样式
     * @return
     */
    public Drawable getEvaluateFocusDraw();

    /**
     * 评价未选中样式
     * @return
     */
    public Drawable getEvaluateDraw();

    /**
     * 获取模块字体色
     * @return
     */
    public ColorStateList getModuleNameFontColor();

    /**
     * 获取模块聚焦字体色
     * @return
     */
    public ColorStateList getModuleNameFontFocusColor();

    /**
     * 评价列表选中条目背景
     * @return
     */
    public Drawable getCommentItemSelectDraw();

    /**
     * 文字状态颜色
     * @return
     */
    public ColorStateList getDetailStatusTextColor();

    /**
     * 设置活动价格的颜色
     * @return
     */
    public ColorStateList getActivityPriceColor();

    /**
     * 设置原价的颜色
     * @return
     */
    public ColorStateList getDetailOriginalPriceTextColor();

    /**
     * 设置原价描述文字的颜色
     * @return
     */
    public ColorStateList getDetailOriginalPriceTitleTextColor();

}

package com.yunos.tvtaobao.newcart.entity;


import com.taobao.wireless.trade.mcart.sdk.co.Component;
import com.taobao.wireless.trade.mcart.sdk.co.biz.AllItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.BannerComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.BundleComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.FoldingBarComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.FooterComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.GroupComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.PromotionBarComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.PromotionComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ShopComponent;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;

import java.io.Serializable;

/**
 * cart:商品区域
 */
public class CartGoodsComponent extends Component implements Serializable {

    public CartGoodsComponent(CartFrom cartFrom) {
        super(cartFrom);
    }

    //item info（包括标题、图片、跳转链接、数量、价格等）
    private ItemComponent itemComponent;
    private ShopComponent shopComponent;
    private PromotionComponent promotionComponent;

    private PromotionBarComponent promotionBarComponent;
    private AllItemComponent allItemComponent;
    private GroupComponent groupComponent;
    private BannerComponent bannerComponent;
    private FoldingBarComponent foldingBarComponent;
    private BundleComponent bundleComponent;
    private FooterComponent footerComponent;

    public ItemComponent getItemComponent() {
        return itemComponent;
    }

    public void setItemComponent(ItemComponent itemComponent) {
        this.itemComponent = itemComponent;
    }

    public ShopComponent getShopComponent() {
        return shopComponent;
    }

    public void setShopComponent(ShopComponent shopComponent) {
        this.shopComponent = shopComponent;
    }

    public PromotionComponent getPromotionComponent() {
        return promotionComponent;
    }

    public void setPromotionComponent(PromotionComponent promotionComponent) {
        this.promotionComponent = promotionComponent;
    }

    public PromotionBarComponent getPromotionBarComponent() {
        return promotionBarComponent;
    }

    public void setPromotionBarComponent(PromotionBarComponent promotionBarComponent) {
        this.promotionBarComponent = promotionBarComponent;
    }


    public FooterComponent getFooterComponent() {
        return footerComponent;
    }

    public void setFooterComponent(FooterComponent footerComponent) {
        this.footerComponent = footerComponent;
    }

    public AllItemComponent getAllItemComponent() {
        return allItemComponent;
    }

    public void setAllItemComponent(AllItemComponent allItemComponent) {
        this.allItemComponent = allItemComponent;
    }

    public GroupComponent getGroupComponent() {
        return groupComponent;
    }

    public void setGroupComponent(GroupComponent groupComponent) {
        this.groupComponent = groupComponent;
    }

    public BannerComponent getBannerComponent() {
        return bannerComponent;
    }

    public void setBannerComponent(BannerComponent bannerComponent) {
        this.bannerComponent = bannerComponent;
    }

    public FoldingBarComponent getFoldingBarComponent() {
        return foldingBarComponent;
    }

    public void setFoldingBarComponent(FoldingBarComponent foldingBarComponent) {
        this.foldingBarComponent = foldingBarComponent;
    }

    public BundleComponent getBundleComponent() {
        return bundleComponent;
    }

    public void setBundleComponent(BundleComponent bundleComponent) {
        this.bundleComponent = bundleComponent;
    }

    @Override
    public String toString() {
        return "CartGoodsComponent{" +
                "itemComponent=" + itemComponent +
                ", shopComponent=" + shopComponent +
                ", promotionComponent=" + promotionComponent +
                ", promotionBarComponent=" + promotionBarComponent +
                ", allItemComponent=" + allItemComponent +
                ", groupComponent=" + groupComponent +
                ", bannerComponent=" + bannerComponent +
                ", foldingBarComponent=" + foldingBarComponent +
                ", bundleComponent=" + bundleComponent +
                ", footerComponent=" + footerComponent +
                '}';
    }
}

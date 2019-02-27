package com.yunos.tvtaobao.cartbag.component;


import com.taobao.wireless.trade.mcart.sdk.co.Component;
//import com.taobao.wireless.trade.mcart.sdk.co.ComponentType;
import com.taobao.wireless.trade.mcart.sdk.co.biz.AllItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.BannerComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.BundleComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.FoldingBarComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.FooterComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.GroupComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemPay;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemQuantity;
import com.taobao.wireless.trade.mcart.sdk.co.biz.PromotionBarComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.PromotionComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.Quantity;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ShopComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.Sku;
import com.taobao.wireless.trade.mcart.sdk.co.biz.Weight;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemInfoComponent;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemOperateComponent;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemPayComponent;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.PromotionIconComponent;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.QuantityComponent;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.ServiceComponent;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.SkuComponent;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.StateIconComponent;
//import com.taobao.wireless.trade.mcart.sdk.co.biz.WeightComponent;

/**
 * cart:商品区域
 */
public class CartGoodsComponent extends Component {

    //private SyntheticType syntheticType = SyntheticType.UNKOWN_CELL;

    //	public CartGoodsComponent() {
//		super();
//		this.type = ComponentType.SYNTHETIC;
//		//this.syntheticType = SyntheticType.GOODS_CELL;
//	}
    public CartGoodsComponent(CartFrom cartFrom) {
        super(cartFrom);
    }

    //item info（包括标题、图片、跳转链接、数量、价格等）
    private ItemComponent itemComponent;
    private ShopComponent shopComponent;
    //private IconComponent iconComponent;
    //item的操作
//	private ItemOperateComponent itemOperateComponent;
    //图片下方的“38生活节”“双11”“库存紧张”之类
//	private StateIconComponent stateIconComponent;
    //“手机专享价” 之类，在价格的位置
    private PromotionComponent promotionComponent;

    private PromotionBarComponent promotionBarComponent;
    //    private Sku skuComponent;
    //sku下方的数量区域
//    private ItemQuantity quantityComponent;
    //原价与实际价格，都从这里取
//    private ItemPay itempayComponent;
    //之前Android不展示服务信息，这次要展示，服务的价格需要计入总价
//	private ServiceComponent serviceComponent;
    //重量
//    private Weight weightComponent;

    private AllItemComponent allItemComponent;
    private GroupComponent groupComponent;
    private BannerComponent bannerComponent;
    private FoldingBarComponent foldingBarComponent;
    private BundleComponent bundleComponent;
    private FooterComponent footerComponent;
    //实际pic url
    private String realPicUrl = null;
    //item的编辑态标志
    private boolean mIsEditStatus = false;
    //是否某个bundle的最后一个宝贝
    private boolean mIsLastOne = false;


    public boolean getIsLastOne() {
        return mIsLastOne;
    }

    public void setIsLastOne(boolean b) {
        mIsLastOne = b;
    }

    public boolean getEditStatus() {
        return mIsEditStatus;
    }

    public void setEditStatus(boolean isEditStatus) {
        this.mIsEditStatus = isEditStatus;
    }

    public String getRealPicUrl() {
        return realPicUrl;
    }

    public void setRealPicUrl(String url) {
        this.realPicUrl = url;
    }

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

    public boolean ismIsEditStatus() {
        return mIsEditStatus;
    }

    public void setmIsEditStatus(boolean mIsEditStatus) {
        this.mIsEditStatus = mIsEditStatus;
    }

    public boolean ismIsLastOne() {
        return mIsLastOne;
    }

    public void setmIsLastOne(boolean mIsLastOne) {
        this.mIsLastOne = mIsLastOne;
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
                ", realPicUrl='" + realPicUrl + '\'' +
                ", mIsEditStatus=" + mIsEditStatus +
                ", mIsLastOne=" + mIsLastOne +
                '}';
    }
}

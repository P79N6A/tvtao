package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * <pre>
 *     author : xutingting
 *     e-mail : xutingting@zhiping.tech
 *     time   : 2018/04/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class NewFeiZhuBean {

    private Shop shop;
    private Coupon coupon;
    private Banner banner;
    private BuyBanner buyBanner;
    private Services services;
    private Title title;
    private Price price;
    private Brand brand;
    private Mileage mileage;
    private Sku sku;
    private Jhs jhs;


    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    public BuyBanner getBuyBanner() {
        return buyBanner;
    }

    public void setBuyBanner(BuyBanner buyBanner) {
        this.buyBanner = buyBanner;
    }

    public Services getServices() {
        return services;
    }

    public void setServices(Services services) {
        this.services = services;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Mileage getMileage() {
        return mileage;
    }

    public void setMileage(Mileage mileage) {
        this.mileage = mileage;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public Jhs getJhs() {
        return jhs;
    }

    public void setJhs(Jhs jhs) {
        this.jhs = jhs;
    }

    public class JumpInfo {

        private String buyUrlType;
        private String jumpH5Url;
        private String jumpNative;
        private String showBuyButton;

        public void setBuyUrlType(String buyUrlType) {
            this.buyUrlType = buyUrlType;
        }

        public String getBuyUrlType() {
            return buyUrlType;
        }

        public void setJumpH5Url(String jumpH5Url) {
            this.jumpH5Url = jumpH5Url;
        }

        public String getJumpH5Url() {
            return jumpH5Url;
        }

        public void setJumpNative(String jumpNative) {
            this.jumpNative = jumpNative;
        }

        public String getJumpNative() {
            return jumpNative;
        }

        public void setShowBuyButton(String showBuyButton) {
            this.showBuyButton = showBuyButton;
        }

        public String getShowBuyButton() {
            return showBuyButton;
        }

    }
    public class ShopEvaluation {

        private String highGap;
        private String score;
        private String title;
        public void setHighGap(String highGap) {
            this.highGap = highGap;
        }
        public String getHighGap() {
            return highGap;
        }

        public void setScore(String score) {
            this.score = score;
        }
        public String getScore() {
            return score;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

    }
    public class ShopData {

        private JumpInfo jumpInfo;
        private String sellerId;
        private String sellerName;
        private List<ShopEvaluation> shopEvaluation;
        private String shopId;
        private String shopName;
        private String shopPic;
        private String shopTypePic;
        public void setJumpInfo(JumpInfo jumpInfo) {
            this.jumpInfo = jumpInfo;
        }
        public JumpInfo getJumpInfo() {
            return jumpInfo;
        }

        public void setSellerId(String sellerId) {
            this.sellerId = sellerId;
        }
        public String getSellerId() {
            return sellerId;
        }

        public void setSellerName(String sellerName) {
            this.sellerName = sellerName;
        }
        public String getSellerName() {
            return sellerName;
        }

        public void setShopEvaluation(List<ShopEvaluation> shopEvaluation) {
            this.shopEvaluation = shopEvaluation;
        }
        public List<ShopEvaluation> getShopEvaluation() {
            return shopEvaluation;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }
        public String getShopId() {
            return shopId;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }
        public String getShopName() {
            return shopName;
        }

        public void setShopPic(String shopPic) {
            this.shopPic = shopPic;
        }
        public String getShopPic() {
            return shopPic;
        }

        public void setShopTypePic(String shopTypePic) {
            this.shopTypePic = shopTypePic;
        }
        public String getShopTypePic() {
            return shopTypePic;
        }

    }
    public class Shop {

        private ShopData data;
        private String tag;
        public void setData(ShopData data) {
            this.data = data;
        }
        public ShopData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }


    public class Cells {

        private String icon;
        private String title;
        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

    }
    public class CouponData {

        private List<Cells> cells;
        private String titleColor;
        public void setCells(List<Cells> cells) {
            this.cells = cells;
        }
        public List<Cells> getCells() {
            return cells;
        }

        public void setTitleColor(String titleColor) {
            this.titleColor = titleColor;
        }
        public String getTitleColor() {
            return titleColor;
        }

    }
    public class Coupon {

        private CouponData data;
        private String tag;
        public void setData(CouponData data) {
            this.data = data;
        }
        public CouponData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }



    public class RightDetailDesc {

        private String desc;
        private String titleText;
        public void setDesc(String desc) {
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }

        public void setTitleText(String titleText) {
            this.titleText = titleText;
        }
        public String getTitleText() {
            return titleText;
        }

    }


    public class MainPicDescList {

        private List<String> descList;
        private String icon;
        private String title;
        public void setDescList(List<String> descList) {
            this.descList = descList;
        }
        public List<String> getDescList() {
            return descList;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

    }

    public class SpecificMainPicVO {

        private String mainPic;
        private List<MainPicDescList> mainPicDescList;
        private String title;
        public void setMainPic(String mainPic) {
            this.mainPic = mainPic;
        }
        public String getMainPic() {
            return mainPic;
        }

        public void setMainPicDescList(List<MainPicDescList> mainPicDescList) {
            this.mainPicDescList = mainPicDescList;
        }
        public List<MainPicDescList> getMainPicDescList() {
            return mainPicDescList;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

    }

    public class BannerData {

        private String leftDesc;
        private List<String> pics;
        private String rightDesc;
        private RightDetailDesc rightDetailDesc;
        private SpecificMainPicVO specificMainPicVO;
        public void setLeftDesc(String leftDesc) {
            this.leftDesc = leftDesc;
        }
        public String getLeftDesc() {
            return leftDesc;
        }

        public void setPics(List<String> pics) {
            this.pics = pics;
        }
        public List<String> getPics() {
            return pics;
        }

        public void setRightDesc(String rightDesc) {
            this.rightDesc = rightDesc;
        }
        public String getRightDesc() {
            return rightDesc;
        }

        public void setRightDetailDesc(RightDetailDesc rightDetailDesc) {
            this.rightDetailDesc = rightDetailDesc;
        }
        public RightDetailDesc getRightDetailDesc() {
            return rightDetailDesc;
        }

        public void setSpecificMainPicVO(SpecificMainPicVO specificMainPicVO) {
            this.specificMainPicVO = specificMainPicVO;
        }
        public SpecificMainPicVO getSpecificMainPicVO() {
            return specificMainPicVO;
        }

    }
    public class Banner {

        private BannerData data;
        private String tag;
        public void setData(BannerData data) {
            this.data = data;
        }
        public BannerData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }

    public class BuyJumpInfo {

        private String buyUrlType;
        private String jumpH5Url;
        private String jumpNative;
        private String showBuyButton;
        public void setBuyUrlType(String buyUrlType) {
            this.buyUrlType = buyUrlType;
        }
        public String getBuyUrlType() {
            return buyUrlType;
        }

        public void setJumpH5Url(String jumpH5Url) {
            this.jumpH5Url = jumpH5Url;
        }
        public String getJumpH5Url() {
            return jumpH5Url;
        }

        public void setJumpNative(String jumpNative) {
            this.jumpNative = jumpNative;
        }
        public String getJumpNative() {
            return jumpNative;
        }

        public void setShowBuyButton(String showBuyButton) {
            this.showBuyButton = showBuyButton;
        }
        public String getShowBuyButton() {
            return showBuyButton;
        }

    }
    public class CarItemJumpInfo {

        private String jumpH5Url;
        private String showBuyButton;
        public void setJumpH5Url(String jumpH5Url) {
            this.jumpH5Url = jumpH5Url;
        }
        public String getJumpH5Url() {
            return jumpH5Url;
        }

        public void setShowBuyButton(String showBuyButton) {
            this.showBuyButton = showBuyButton;
        }
        public String getShowBuyButton() {
            return showBuyButton;
        }

    }
    public class SellerContact {

        private String itemTips;
        private String notifyTips;
        private String phoneNO;
        private String phoneTips;
        private String sellerId;
        private String sellerNick;
        public void setItemTips(String itemTips) {
            this.itemTips = itemTips;
        }
        public String getItemTips() {
            return itemTips;
        }

        public void setNotifyTips(String notifyTips) {
            this.notifyTips = notifyTips;
        }
        public String getNotifyTips() {
            return notifyTips;
        }

        public void setPhoneNO(String phoneNO) {
            this.phoneNO = phoneNO;
        }
        public String getPhoneNO() {
            return phoneNO;
        }

        public void setPhoneTips(String phoneTips) {
            this.phoneTips = phoneTips;
        }
        public String getPhoneTips() {
            return phoneTips;
        }

        public void setSellerId(String sellerId) {
            this.sellerId = sellerId;
        }
        public String getSellerId() {
            return sellerId;
        }

        public void setSellerNick(String sellerNick) {
            this.sellerNick = sellerNick;
        }
        public String getSellerNick() {
            return sellerNick;
        }

    }
    public class ShopJumpInfo {

        private String jumpH5Url;
        private String jumpNative;
        private String showBuyButton;
        public void setJumpH5Url(String jumpH5Url) {
            this.jumpH5Url = jumpH5Url;
        }
        public String getJumpH5Url() {
            return jumpH5Url;
        }

        public void setJumpNative(String jumpNative) {
            this.jumpNative = jumpNative;
        }
        public String getJumpNative() {
            return jumpNative;
        }

        public void setShowBuyButton(String showBuyButton) {
            this.showBuyButton = showBuyButton;
        }
        public String getShowBuyButton() {
            return showBuyButton;
        }

    }
    public class BuyBannerData {

        private String buyButtonDesc;
        private String buyButtonStyle;
        private String buyButtonSupport;
        private BuyJumpInfo buyJumpInfo;
        private String carBizType;
        private String carButtonStyle;
        private String carButtonSupport;
        private String carDesc;
        private CarItemJumpInfo carItemJumpInfo;
        private String carType;
        private String saveIcon;
        private SellerContact sellerContact;
        private String sellerId;
        private String sellerName;
        private String shopIcon;
        private ShopJumpInfo shopJumpInfo;
        public void setBuyButtonDesc(String buyButtonDesc) {
            this.buyButtonDesc = buyButtonDesc;
        }
        public String getBuyButtonDesc() {
            return buyButtonDesc;
        }

        public void setBuyButtonStyle(String buyButtonStyle) {
            this.buyButtonStyle = buyButtonStyle;
        }
        public String getBuyButtonStyle() {
            return buyButtonStyle;
        }

        public void setBuyButtonSupport(String buyButtonSupport) {
            this.buyButtonSupport = buyButtonSupport;
        }
        public String getBuyButtonSupport() {
            return buyButtonSupport;
        }

        public void setBuyJumpInfo(BuyJumpInfo buyJumpInfo) {
            this.buyJumpInfo = buyJumpInfo;
        }
        public BuyJumpInfo getBuyJumpInfo() {
            return buyJumpInfo;
        }

        public void setCarBizType(String carBizType) {
            this.carBizType = carBizType;
        }
        public String getCarBizType() {
            return carBizType;
        }

        public void setCarButtonStyle(String carButtonStyle) {
            this.carButtonStyle = carButtonStyle;
        }
        public String getCarButtonStyle() {
            return carButtonStyle;
        }

        public void setCarButtonSupport(String carButtonSupport) {
            this.carButtonSupport = carButtonSupport;
        }
        public String getCarButtonSupport() {
            return carButtonSupport;
        }

        public void setCarDesc(String carDesc) {
            this.carDesc = carDesc;
        }
        public String getCarDesc() {
            return carDesc;
        }

        public void setCarItemJumpInfo(CarItemJumpInfo carItemJumpInfo) {
            this.carItemJumpInfo = carItemJumpInfo;
        }
        public CarItemJumpInfo getCarItemJumpInfo() {
            return carItemJumpInfo;
        }

        public void setCarType(String carType) {
            this.carType = carType;
        }
        public String getCarType() {
            return carType;
        }

        public void setSaveIcon(String saveIcon) {
            this.saveIcon = saveIcon;
        }
        public String getSaveIcon() {
            return saveIcon;
        }

        public void setSellerContact(SellerContact sellerContact) {
            this.sellerContact = sellerContact;
        }
        public SellerContact getSellerContact() {
            return sellerContact;
        }

        public void setSellerId(String sellerId) {
            this.sellerId = sellerId;
        }
        public String getSellerId() {
            return sellerId;
        }

        public void setSellerName(String sellerName) {
            this.sellerName = sellerName;
        }
        public String getSellerName() {
            return sellerName;
        }

        public void setShopIcon(String shopIcon) {
            this.shopIcon = shopIcon;
        }
        public String getShopIcon() {
            return shopIcon;
        }

        public void setShopJumpInfo(ShopJumpInfo shopJumpInfo) {
            this.shopJumpInfo = shopJumpInfo;
        }
        public ShopJumpInfo getShopJumpInfo() {
            return shopJumpInfo;
        }

    }
    public class BuyBanner {

        private BuyBannerData data;
        private String tag;
        public void setData(BuyBannerData data) {
            this.data = data;
        }
        public BuyBannerData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }

    public class ServicesCells {

        private String icon;
        private String title;
        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

    }

    public class ServicesData {

        private List<ServicesCells> cells;
        private String titleColor;
        public void setCells(List<ServicesCells> cells) {
            this.cells = cells;
        }
        public List<ServicesCells> getCells() {
            return cells;
        }

        public void setTitleColor(String titleColor) {
            this.titleColor = titleColor;
        }
        public String getTitleColor() {
            return titleColor;
        }

    }

    public class Services {

        private ServicesData data;
        private String tag;
        public void setData(ServicesData data) {
            this.data = data;
        }
        public ServicesData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }


    public class TitleData {

        private String itemTitle;
        public void setItemTitle(String itemTitle) {
            this.itemTitle = itemTitle;
        }
        public String getItemTitle() {
            return itemTitle;
        }

    }
    public class Title {

        private TitleData data;
        private String tag;
        public void setData(TitleData data) {
            this.data = data;
        }
        public TitleData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }


    public class Extra {

        private String content;
        private String type;
        public void setContent(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

    }

    public class AidedPrice {

        private List<Extra> extra;
        public void setExtra(List<Extra> extra) {
            this.extra = extra;
        }
        public List<Extra> getExtra() {
            return extra;
        }

    }


    public class MainPrice {

        private List<Extra> extra;
        private String price;
        public void setExtra(List<Extra> extra) {
            this.extra = extra;
        }
        public List<Extra> getExtra() {
            return extra;
        }

        public void setPrice(String price) {
            this.price = price;
        }
        public String getPrice() {
            return price;
        }

    }

    public class PriceData {

        private AidedPrice aidedPrice;
        private List<Extra> extra;
        private MainPrice mainPrice;
        public void setAidedPrice(AidedPrice aidedPrice) {
            this.aidedPrice = aidedPrice;
        }
        public AidedPrice getAidedPrice() {
            return aidedPrice;
        }

        public void setExtra(List<Extra> extra) {
            this.extra = extra;
        }
        public List<Extra> getExtra() {
            return extra;
        }

        public void setMainPrice(MainPrice mainPrice) {
            this.mainPrice = mainPrice;
        }
        public MainPrice getMainPrice() {
            return mainPrice;
        }

    }

    public class Price {
        private PriceData data;
        private String tag;
        public void setData(PriceData data) {
            this.data = data;
        }
        public PriceData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }


    public class SkuPropList {

        private String pvId;
        private String value;
        public void setPvId(String pvId) {
            this.pvId = pvId;
        }
        public String getPvId() {
            return pvId;
        }

        public void setValue(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }

    }
    public class Props {

        private String skuPropId;
        private List<SkuPropList> skuPropList;
        private String skuPropTitle;
        public void setSkuPropId(String skuPropId) {
            this.skuPropId = skuPropId;
        }
        public String getSkuPropId() {
            return skuPropId;
        }

        public void setSkuPropList(List<SkuPropList> skuPropList) {
            this.skuPropList = skuPropList;
        }
        public List<SkuPropList> getSkuPropList() {
            return skuPropList;
        }

        public void setSkuPropTitle(String skuPropTitle) {
            this.skuPropTitle = skuPropTitle;
        }
        public String getSkuPropTitle() {
            return skuPropTitle;
        }

    }

    public class SkuStrokeStatus {

        private String status;
        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

    }
    public class SkuData {

        private String enableSelectCount;
        private String hideActionPrice;
        private String mainPic;
        private List<Props> props;
        private String selectedDateTitle;
        private String skuShareInventory;
        private SkuStrokeStatus skuStrokeStatus;
        private String startDateTitle;
        private String status;
        public void setEnableSelectCount(String enableSelectCount) {
            this.enableSelectCount = enableSelectCount;
        }
        public String getEnableSelectCount() {
            return enableSelectCount;
        }

        public void setHideActionPrice(String hideActionPrice) {
            this.hideActionPrice = hideActionPrice;
        }
        public String getHideActionPrice() {
            return hideActionPrice;
        }

        public void setMainPic(String mainPic) {
            this.mainPic = mainPic;
        }
        public String getMainPic() {
            return mainPic;
        }

        public void setProps(List<Props> props) {
            this.props = props;
        }
        public List<Props> getProps() {
            return props;
        }

        public void setSelectedDateTitle(String selectedDateTitle) {
            this.selectedDateTitle = selectedDateTitle;
        }
        public String getSelectedDateTitle() {
            return selectedDateTitle;
        }


        public void setSkuShareInventory(String skuShareInventory) {
            this.skuShareInventory = skuShareInventory;
        }
        public String getSkuShareInventory() {
            return skuShareInventory;
        }

        public void setSkuStrokeStatus(SkuStrokeStatus skuStrokeStatus) {
            this.skuStrokeStatus = skuStrokeStatus;
        }
        public SkuStrokeStatus getSkuStrokeStatus() {
            return skuStrokeStatus;
        }

        public void setStartDateTitle(String startDateTitle) {
            this.startDateTitle = startDateTitle;
        }
        public String getStartDateTitle() {
            return startDateTitle;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

    }

    public class Sku {

        private SkuData data;
        private String tag;
        public void setData(SkuData data) {
            this.data = data;
        }
        public SkuData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }


    public class BrandData {

        private String titleColor;
        private String titleIcon;
        private String titleText;
        public void setTitleColor(String titleColor) {
            this.titleColor = titleColor;
        }
        public String getTitleColor() {
            return titleColor;
        }

        public void setTitleIcon(String titleIcon) {
            this.titleIcon = titleIcon;
        }
        public String getTitleIcon() {
            return titleIcon;
        }

        public void setTitleText(String titleText) {
            this.titleText = titleText;
        }
        public String getTitleText() {
            return titleText;
        }

    }
    public class Brand {

        private BrandData data;
        private String tag;
        public void setData(BrandData data) {
            this.data = data;
        }
        public BrandData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }

    public class MileageData {

        private String desc;
        private String flayerTitle;
        private String icon;
        private String title;
        private String titleColor;
        public void setDesc(String desc) {
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }

        public void setFlayerTitle(String flayerTitle) {
            this.flayerTitle = flayerTitle;
        }
        public String getFlayerTitle() {
            return flayerTitle;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
        public String getIcon() {
            return icon;
        }

        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setTitleColor(String titleColor) {
            this.titleColor = titleColor;
        }
        public String getTitleColor() {
            return titleColor;
        }

    }
    public class Mileage {

        private MileageData data;
        private String tag;
        public void setData(MileageData data) {
            this.data = data;
        }
        public MileageData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }



    public class Started {

        private String endSeconds;
        private String originalPrice;
        private String price;
        private String sold;
        public void setEndSeconds(String endSeconds) {
            this.endSeconds = endSeconds;
        }
        public String getEndSeconds() {
            return endSeconds;
        }

        public void setOriginalPrice(String originalPrice) {
            this.originalPrice = originalPrice;
        }
        public String getOriginalPrice() {
            return originalPrice;
        }

        public void setPrice(String price) {
            this.price = price;
        }
        public String getPrice() {
            return price;
        }

        public void setSold(String sold) {
            this.sold = sold;
        }
        public String getSold() {
            return sold;
        }

    }

    public class JhsData {

        private Started started;
        private String status;
        public void setStarted(Started started) {
            this.started = started;
        }
        public Started getStarted() {
            return started;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

    }
    public class Jhs {

        private JhsData data;
        private String tag;
        public void setData(JhsData data) {
            this.data = data;
        }
        public JhsData getData() {
            return data;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
        public String getTag() {
            return tag;
        }

    }


}

package com.yunos.tvtaobao.biz.request.bo;


import com.alibaba.fastjson.annotation.JSONField;
import com.yunos.tv.core.config.AppInfo;

import java.util.ArrayList;

/**
 * Created by huangdaju on 16/9/23.
 */
public class GlobalConfig {

    public static GlobalConfig instance;

    public GlobalConfig() {
        instance = this;//复制到全局变量，方便没有appholder类定义的模块调用
    }

    @JSONField(name = "double11_shop_cart")
    private Double11ShopCart double11ShopCart;   //双11购物车

    @JSONField(name = "upgrade")
    private Upgrade mUpgrade;

    @JSONField(name = "double11_action")
    private Double11Action mDouble11Action;


    @JSONField(name = "shop_cart_flag")
    public ShopCartFlag mShopCartFlag;    //购物车标记

    @JSONField(name = "isBlitzShop")
    private boolean isBlitzShop;

    @JSONField(name = "isMoHeLogOn")
    private boolean isMoHeLogOn;

    @JSONField(name = "isLianMengLogOn")
    private boolean isLianMengLogOn;

    @JSONField(name = "isYiTiJiLogOn")
    private boolean isYiTiJiLogOn;

    @JSONField(name = "isSimpleOn")
    private boolean isSimpleOn;

    @JSONField(name = "tmall_live")
    public TMallLive mTMallLive;

    @JSONField(name = "afp")
    private AfpBean afp;

    @JSONField(name = "custom_launcher_screen")
    private CustomLauncherScreenBean custom_launcher_screen;

    @JSONField(name = "waimai")
    private Waimai waimai;

    @JSONField(name = "taobaoPay")//全局通用，强制开启手淘扫码支付
    public boolean taobaoPay;

    public Waimai getWaimai() {
        return waimai;
    }

    public void setWaimai(Waimai waimai) {
        this.waimai = waimai;
    }

    @JSONField(name = "detail")
    public Detail detail;

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    @JSONField(name = "zhitongche")
    private ZhitongcheConfig ztcConfig;

    public ZhitongcheConfig getZtcConfig() {
        return ztcConfig;
    }

    public void setZtcConfig(ZhitongcheConfig ztcConfig) {
        this.ztcConfig = ztcConfig;
    }

    @JSONField(name = "coupon")
    public Coupon coupon;

    public Coupon getCoupon() {
        return coupon;
    }

    @JSONField(name = "tvacr")
    public TvACR tvacr;

    @JSONField(name = "beta")
    public Beta beta;

    @JSONField(name = "detail_goods_info")
    public DetailGoodsInfo detail_goods_info;

    @JSONField(name = "no_agreement_model")
    public String[] agreementModelBlockList;

    @JSONField(name = "no_agreement_channel")
    public String[] agreementChannelBlockList;

    public DetailGoodsInfo getDetail_goods_info() {
        return detail_goods_info;
    }

    public void setDetail_goods_info(DetailGoodsInfo detail_goods_info) {
        this.detail_goods_info = detail_goods_info;

    }

    public TvACR getTvacr() {
        return tvacr;
    }

    public void setTvacr(TvACR tvacr) {
        this.tvacr = tvacr;
    }

    public ShopCartFlag getShopCartFlag() {
        return mShopCartFlag;
    }

    public void setShopCartFlag(ShopCartFlag shopCartFlag) {
        mShopCartFlag = shopCartFlag;
    }

    public Upgrade getUpgrade() {
        return mUpgrade;
    }

    public void setDouble11ShopCart(Double11ShopCart double11ShopCart) {
        this.double11ShopCart = double11ShopCart;
    }

    public Double11ShopCart getDouble11ShopCart() {
        return double11ShopCart;
    }

    public void setUpgrade(Upgrade upgrade) {
        mUpgrade = upgrade;
    }

    public Double11Action getDouble11Action() {
        return mDouble11Action;
    }

    public void setDouble11Action(Double11Action double11Action) {
        mDouble11Action = double11Action;
    }

    public TMallLive getTMallLive() {
        return mTMallLive;
    }

    public void setmTMallLive(TMallLive tMallLive) {
        mTMallLive = tMallLive;
    }

    public AfpBean getAfp() {
        return afp;
    }

    public void setAfp(AfpBean afp) {
        this.afp = afp;
    }

    public CustomLauncherScreenBean getCustom_launcher_screen() {
        return custom_launcher_screen;
    }

    public void setCustom_launcher_screen(CustomLauncherScreenBean custom_launcher_screen) {
        this.custom_launcher_screen = custom_launcher_screen;
    }

    public boolean isBlitzShop() {
        return isBlitzShop;
    }

    public void setBlitzShop(boolean blitzShop) {
        isBlitzShop = blitzShop;
    }

    public boolean isMoHeLogOn() {
        return isMoHeLogOn;
    }

    public void setMoHeLogOn(boolean moHeLogOn) {
        isMoHeLogOn = moHeLogOn;
    }

    public boolean isLianMengLogOn() {
        return isLianMengLogOn;
    }

    public void setLianMengLogOn(boolean lianMengLogOn) {
        isLianMengLogOn = lianMengLogOn;
    }

    public boolean isYiTiJiLogOn() {
        return isYiTiJiLogOn;
    }

    public void setYiTiJiLogOn(boolean yiTiJiLogOn) {
        isYiTiJiLogOn = yiTiJiLogOn;
    }


    public boolean isSimpleOn() {
        return isSimpleOn;
    }

    public void setSimpleOn(boolean simpleOn) {
        isSimpleOn = simpleOn;
    }
    public static class Double11ShopCart {

        @JSONField(name = "bool_shop_cart_merge_orders")
        private boolean boolShopCartMergeOrders;

        public boolean isBoolShopCartMergeOrders() {
            return boolShopCartMergeOrders;
        }

        public void setBoolShopCartMergeOrders(boolean boolShopCartMergeOrders) {
            this.boolShopCartMergeOrders = boolShopCartMergeOrders;
        }
    }

    public static class Upgrade {

        private String background;
        private String content;
        private String title;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        private String text;

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

    public static class ShopCartFlag {

        private boolean isActing;
        private String detailIcon;          //详情购物车icon
        private String shopCatIcon;         //购物车icon
        @JSONField(name = "new_shop_cat_icon")
        private String newShopCatIcon;         //2018.7.17新版购物车icon
        private String sideBarIcon;         //侧边栏购物车icon

        public boolean isActing() {
            return isActing;
        }

        public void setActing(boolean acting) {
            isActing = acting;
        }

        public String getDetailIcon() {
            return detailIcon;
        }

        public void setDetailIcon(String detailIcon) {
            this.detailIcon = detailIcon;
        }

        public String getShopCatIcon() {
            return shopCatIcon;
        }

        public void setShopCatIcon(String shopCatIcon) {
            this.shopCatIcon = shopCatIcon;
        }

        public String getNewShopCatIcon() {
            return newShopCatIcon;
        }

        public void setNewShopCatIcon(String newShopCatIcon) {
            this.newShopCatIcon = newShopCatIcon;
        }

        public String getSideBarIcon() {
            return sideBarIcon;
        }

        public void setSideBarIcon(String sideBarIcon) {
            this.sideBarIcon = sideBarIcon;
        }
    }

    public static class Double11Action {
        @JSONField(name = "is_acting")
        private boolean isActing;
        @JSONField(name = "subtitle")
        private String subtitle;

        @JSONField(name = "step")
        private String step;

        public void setStep(String step) {
            this.step = step;
        }

        public String getStep() {
            return step;
        }

        public boolean isActing() {
            return isActing;
        }

        public void setActing(boolean acting) {
            isActing = acting;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }
    }

    public static class TMallLive {
        private String is_online;

        private String live_login_prompt;

        private String live_unlogin_prompt;

        private String red_envelopes_time;

        private String postfix;

        private ArrayList<Integer> coupon_offset;

        public String postfix() {
            return postfix;
        }

        public void setPostfix(String postfix) {
            this.postfix = postfix;
        }

        public String getIs_online() {
            return is_online;
        }

        public void setIs_online(String is_online) {
            this.is_online = is_online;
        }

        public String getLive_login_prompt() {
            return live_login_prompt;
        }

        public void setLive_login_prompt(String live_login_prompt) {
            this.live_login_prompt = live_login_prompt;
        }

        public String getLive_unlogin_prompt() {
            return live_unlogin_prompt;
        }

        public void setLive_unlogin_prompt(String live_unlogin_prompt) {
            this.live_unlogin_prompt = live_unlogin_prompt;
        }

        public String getRed_envelopes_time() {
            return red_envelopes_time;
        }

        public void setRed_envelopes_time(String red_envelopes_time) {
            this.red_envelopes_time = red_envelopes_time;
        }

        public ArrayList<Integer> getCoupon_offset() {
            return coupon_offset;
        }

        public void setCoupon_offset(ArrayList<Integer> coupon_offset) {
            this.coupon_offset = coupon_offset;
        }
    }

    public static class Coupon {
        @JSONField(name = "taobao_coupon")
        private boolean taobaoCoupon = false;

        @JSONField(name = "is_show_h5coupon")
        private boolean showH5Coupon = false;

        public void setShowH5Coupon(boolean showH5Coupon) {
            this.showH5Coupon = showH5Coupon;
        }

        public void setTaobaoCoupon(boolean taobaoCoupon) {
            this.taobaoCoupon = taobaoCoupon;
        }

        public boolean isShowH5Coupon() {
            return showH5Coupon;
        }


        public boolean supportTaobaoCoupon() {
            return taobaoCoupon;
        }
    }

    public static class Detail {

        private PresellBean presell;

        public PresellBean getPresell() {
            return presell;
        }

        public void setPresell(PresellBean presell) {
            this.presell = presell;
        }

        public static class PresellBean {
            private String icon;

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }
        }
    }

    public static class Waimai {
        @JSONField(name = "taobaoPay")
        private boolean taobaoPay = false;

        public void setTaobaoPay(boolean pay) {
            taobaoPay = pay;
        }

        public boolean isTaobaoPay() {
            return taobaoPay;
        }
    }


    public static class AfpBean {
        private boolean isbackhome;

        private ScreenBean screen;

        public boolean isIsbackhome() {
            return isbackhome;
        }

        public void setIsbackhome(boolean isbackhome) {
            this.isbackhome = isbackhome;
        }


        public ScreenBean getScreen() {
            return screen;
        }

        public void setScreen(ScreenBean screen) {
            this.screen = screen;
        }

        public static class ScreenBean {
            private int timeout;

            public int getTimeout() {
                return timeout;
            }

            public void setTimeout(int timeout) {
                this.timeout = timeout;
            }
        }

    }

    public static class CustomLauncherScreenBean {
        private boolean show_loading;

        public boolean isShowLoading() {
            return show_loading;
        }

        public void setIsShowLoading(boolean show_loading) {
            this.show_loading = show_loading;
        }
    }

    public static class TvACR {
        private boolean isShow;
        private boolean acrShow;

        public boolean isShow() {
            return isShow;
        }

        public void setShow(boolean show) {
            isShow = show;
        }

        public boolean isAcrShow() {
            return acrShow;
        }

        public void setAcrShow(boolean acrShow) {
            this.acrShow = acrShow;
        }
    }


    public static class ZhitongcheConfig {
        @JSONField(name = "black_list")
        public String[] blackList;

        @JSONField(name = "flag")
        public String flag = null;

        @JSONField(name = "is_show")
        public boolean is_show = false;

        @JSONField(name = "ztc_icon")
        public String icon_url = null;

        @JSONField(name = "s_request")
        public boolean singleRequest = false;

        public void setSingleRequest(boolean singleRequest) {
            this.singleRequest = singleRequest;
        }

        public boolean isSingleRequest() {
            return singleRequest;
        }

        public String[] getBlackList() {
            return blackList;
        }

        public void setBlackList(String[] blackList) {
            this.blackList = blackList;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public boolean is_show() {
            return is_show;
        }

        public void setIs_show(boolean is_show) {
            this.is_show = is_show;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }
    }

    public static class Beta {
        @JSONField(name = "version")
        public String[] versions;
    }

    public boolean isBeta() {
        if (beta != null && beta.versions != null) {
            String versionName = AppInfo.getAppVersionName();
            for (String version : beta.versions) {
                if (versionName.equals(version))
                    return true;
            }
        }
        return false;
    }

    public static class DetailGoodsInfo {
        @JSONField(name = "detail_panel")
        private String detailPanel;

        @JSONField(name = "detail_top_button")
        private String detailTopButton;

        @JSONField(name = "detail_share")
        private String detailShare;

        @JSONField(name = "is_show_all")
        private Boolean isShowAll;

        @JSONField(name = "is_show_all_price")
        private Boolean isShowAllPrice;

//        @JSONField(name = "detail_rebate_icon")
//        private String detailRebateIcon;
//
//        @JSONField(name = "detail_rebate_info")
//        private String detailRebateInfo;
//
//        @JSONField(name = "order_rebate_info")
//        private String orderRebateInfo;


        public Boolean getShowAllPrice() {
            return isShowAllPrice;
        }

        public void setShowAllPrice(Boolean showAllPrice) {
            isShowAllPrice = showAllPrice;
        }

        public Boolean getShowAll() {
            return isShowAll;
        }

        public void setShowAll(Boolean showAll) {
            isShowAll = showAll;
        }

        public String getDetailPanel() {
            return detailPanel;
        }

        public void setDetailPanel(String detailPanel) {
            this.detailPanel = detailPanel;
        }

        public String getDetailTopButton() {
            return detailTopButton;
        }

        public void setDetailTopButton(String detailTopButton) {
            this.detailTopButton = detailTopButton;
        }

        public String getDetailShare() {
            return detailShare;
        }

        public void setDetailShare(String detailShare) {
            this.detailShare = detailShare;
        }

//        public String getDetailRebateIcon() {
//            return detailRebateIcon;
//        }
//
//        public void setDetailRebateIcon(String detailRebateIcon) {
//            this.detailRebateIcon = detailRebateIcon;
//        }
//
//        public String getDetailRebateInfo() {
//            return detailRebateInfo;
//        }
//
//        public void setDetailRebateInfo(String detailRebateInfo) {
//            this.detailRebateInfo = detailRebateInfo;
//        }
//
//        public String getOrderRebateInfo() {
//            return orderRebateInfo;
//        }
//
//        public void setOrderRebateInfo(String orderRebateInfo) {
//            this.orderRebateInfo = orderRebateInfo;
//        }
    }


    @Override
    public String toString() {
        return "GlobalConfig{" +
                "double11ShopCart=" + double11ShopCart +
                ", mUpgrade=" + mUpgrade +
                ", mDouble11Action=" + mDouble11Action +
                ", mShopCartFlag=" + mShopCartFlag +
                ", mTMallLive=" + mTMallLive +
                ", afp=" + afp +
                ", custom_launcher_screen=" + custom_launcher_screen +
                ", detail=" + detail +
                ", ztcConfig=" + ztcConfig +
                ", coupon=" + coupon +
                ", tvacr=" + tvacr + ", taobaoPay= " + taobaoPay +
                '}';
    }
}

package com.yunos.tv.core.config;

/**
 * Created by linmu on 2018/5/25.
 */

public class SPMConfig {

    public static final String SPM = "spm";
    public static final String SPM_CNT = "spm-cnt";


    public static final String TB_SITE = "a2o0j";
    public static final String ADDRESS_EDIT_SPM = TB_SITE + ".11561598";
    public static final String ADDRESS_SPM = TB_SITE + ".11561581";
    //    搜索输入页面
    public static final String SEARCH_SPM = TB_SITE + ".11560333";
    //功能按钮点击事件:清空
    public static final String SEARCH_SPM_BUTTON_KEYBOARD_DELETE =SEARCH_SPM+".button.delete";

    //功能按钮点击事件：推格
    public static final String SEARCH_SPM_BUTTON_KEYBOARD_BACK =SEARCH_SPM+".button.back";

    //功能按钮点击事件：删除历史记录
    public static final String SEARCH_SPM_BUTTON_FORGET =SEARCH_SPM+".button.forget";

    //历史记录词点击事件
    public static final String SEARCH_SPM_HISTORY_ITEM =SEARCH_SPM+".keywords.history";

    //搜索发现词点击事件
    public static final String SEARCH_SPM_RECOMMEND_ITEM =SEARCH_SPM+".keywords.recommend";

    //联想词列表
    public static final String SEARCH_SPM_ASSOCIATION_ITEM =SEARCH_SPM+".listitem.p_name";



    //    结果关键词
    public static final String SEARCH_SPM_KEYWORDS_RESULT = SEARCH_SPM + ".keywords.result";
    public static final String SEARCH_SPM_KEYWORDS_RECOMMEND = SEARCH_SPM + ".keywords.recommend";

    //搜索结果
    public static final String GOODS_LIST_SPM = TB_SITE + ".11561617";
    public static final String GOODS_LIST_SPM_ITEM_P_NAME = GOODS_LIST_SPM + ".item.p_name";
    public static final String GOODS_LIST_SPM_LIST_P_NAME = GOODS_LIST_SPM + ".list.p_name";

    public static final String GOODS_LIST_SPM_FUCTION_HOME = GOODS_LIST_SPM + ".fuction.home";
    public static final String GOODS_LIST_SPM_FUCTION_MY = GOODS_LIST_SPM + ".fuction.my";
    public static final String GOODS_LIST_SPM_FUCTION_CART = GOODS_LIST_SPM + ".fuction.cart";

    public static final String GOODS_LIST_SPM_FUCTION_COUPON = GOODS_LIST_SPM + ".fuction.coupon";
    public static final String GOODS_LIST_SPM_FUCTION_CC = GOODS_LIST_SPM + ".fuction.cc";
    public static final String GOODS_LIST_SPM_FUCTION_POINT = GOODS_LIST_SPM + ".fuction.point";

    //    淘抢购
    public static final String TAOQIANGGOU_MAIN = TB_SITE + ".11561898";
    //    最后疯抢内容卡片
    public static final String TAOQIANGGOU_MAIN_LAST_ITEM_P_NAME = TAOQIANGGOU_MAIN + ".last_item.p_name";
    //    最后疯抢按钮
    public static final String TAOQIANGGOU_MAIN_BUTTON_LAST = TAOQIANGGOU_MAIN + ".button.last";
    //    场次
    public static final String TAOQIANGGOU_MAIN_TIMELINE_P_NAME = TAOQIANGGOU_MAIN + ".timeline.p_name";
    //    商品卡片
    public static final String TAOQIANGGOU_MAIN_ITEM_P_NAME = TAOQIANGGOU_MAIN + ".item.p_name";

    //    外卖店铺页
    public static final String WAIMAI_SHOP = TB_SITE + ".11562928";
    //    商品坑位-商品加购按钮
    public static final String WAIMAI_SHOP_ADD_CART = WAIMAI_SHOP + ".item.pit_name_p_AddToCart";
    //    规格选择层-曝光事件
    public static final String WAIMAI_SHOP_SKU_SELECT_EXPOSE = WAIMAI_SHOP + ".pop.spec_select";
    //    规格选择层-选好了按钮
    public static final String WAIMAI_SHOP_SKU_SELECT_DONE = WAIMAI_SHOP + ".spec_select.done";
    //    规格选择层-规格按钮
    public static final String WAIMAI_SHOP_SKU_SELECT_SPEC = WAIMAI_SHOP + ".spec_select.spec_p_name";
    //    规格选择层-增加数量按钮
    public static final String WAIMAI_SHOP_SKU_SELECT_NUM_INC = WAIMAI_SHOP + ".spec_select.increase";
    //    规格选择层-减少数量按钮
    public static final String WAIMAI_SHOP_SKU_SELECT_NUM_DES = WAIMAI_SHOP + ".spec_select.decrease";
    //    购物车-按钮点击事件
    public static final String WAIMAI_SHOP_SKU_SELECT_GOTO_CART = WAIMAI_SHOP + ".cart.GoToCart";
    //    购物车-商品增加按钮
    public static final String WAIMAI_SHOP_CART_INCREASE = WAIMAI_SHOP + ".cart.increase";
    //    购物车-商品减少按钮
    public static final String WAIMAI_SHOP_CART_DECREASE = WAIMAI_SHOP + ".cart.decrease";
    //    店铺详情层-曝光事件
    public static final String WAIMAI_SHOP_DETAILS_EXPOSE = WAIMAI_SHOP + ".expose.shopdetails";
    //    返回键退出
    public static final String WAIMAI_SHOP_DETAILS_KEY_BACK = WAIMAI_SHOP + ".system.key_back";
    //    侧边栏订单按钮
    public static final String WAIMAI_SHOP_SLIDE_ORDER = WAIMAI_SHOP + ".slide.p_order";
    //    详情
    public static final String WAIMAI_SHOP_SLIDE_DETAILS = WAIMAI_SHOP + ".slide.p_details";
    //    我的
    public static final String WAIMAI_SHOP_SLIDE_MY = WAIMAI_SHOP + ".slide.P_my";
    //    外卖首页
    public static final String WAIMAI_SHOP_SLIDE_GO_HOME = WAIMAI_SHOP + ".slide.p_waimaihome";

    //    外卖订单列表页面
    public static final String WAIMAI_ORDER = TB_SITE + ".11560736";
    //    外卖订单
    public static final String WAIMAI_ORDER_ITEM = WAIMAI_ORDER + ".focus.item";

    //    立即支付按钮
    public static final String WAIMAI_ORDER_PAY = WAIMAI_ORDER + ".button.Pay";

    //    订单详情按钮
    public static final String WAIMAI_ORDER_DETAIL = WAIMAI_ORDER + ".button.OrderDetail";

    //    取消订单按钮
    public static final String WAIMAI_ORDER_CANCEL = WAIMAI_ORDER + ".button.Cancel";

    //    再来一单按钮
    public static final String WAIMAI_ORDER_ONE_MORE = WAIMAI_ORDER + ".button.OneMore";

    //    申请退单按钮
    public static final String WAIMAI_ORDER_REFUND = WAIMAI_ORDER + ".button.Refund";

    //    查看物流按钮
    public static final String WAIMAI_ORDER_LOGISTICS = WAIMAI_ORDER + ".button.Logistics";


    ///////////////////

    //    外卖订单详情页
    public static final String WAIMAI_ORDER_PAGE_DETAIL = TB_SITE + ".11562948";

    //    “再来一单”按钮
    public static final String WAIMAI_ORDER_DETAIL_ONEMORE = WAIMAI_ORDER_PAGE_DETAIL + ".function.Onemore";

    //    视频播放页（单品购买）
    public static final String VIDEO_DETAIL = TB_SITE + ".11561932";
    //    视频播放页一键加购
    public static final String VIDEO_DETAIL_ADD = VIDEO_DETAIL + ".item.AddToCart";
    //    进入购物车
    public static final String VIDEO_DETAIL_GOTO_CART = VIDEO_DETAIL + ".item.GoToCart";

    //    挡板按钮点击事件
    public static final String VIDEO_DETAIL_BAFFLE_OK = VIDEO_DETAIL + ".item.baffle_ok";

    //    商详按钮点击事件
    public static final String VIDEO_DETAIL_DETAIL_CLICK = VIDEO_DETAIL + ".item.detail_ok";

    //    进度条访问事件
    public static final String VIDEO_DETAIL_BAR = VIDEO_DETAIL + ".video.bar";

    //    进度条按钮点击事件
    public static final String VIDEO_DETAIL_BAR_CLICK = VIDEO_DETAIL + ".video.bar_buttonname";
    //    上/下一个视频信息曝光事件
    public static final String VIDEO_DETAIL_CHANCE = VIDEO_DETAIL + ".video.changeone";

    //    开奖倒计时曝光事件
    public static final String VIDEO_DETAIL_FINALTIME_RUNLOTTERY = VIDEO_DETAIL + ".huodong.finaltime_RunLottery";

    //    领取倒计时曝光事件
    public static final String VIDEO_DETAIL_FINALTIME_RECEIVELOTTERY = VIDEO_DETAIL + ".huodong.finaltime_ReceiveLottery";

    //    积分领取点击事件
    public static final String VIDEO_DETAIL_RECEIVEPIONT = VIDEO_DETAIL + ".huodong.ReceivePionts";

    //    购物车
    public static final String SHOP_CART_LIST_SPM = "a2o0j.11561458";
    //    编辑商品
    public static final String SHOP_CART_LIST_SPM_ITEM_EDIT = SHOP_CART_LIST_SPM + ".item.edit";
    //    查看商品详情
    public static final String SHOP_CART_LIST_SPM_ITEM_DETAIL = SHOP_CART_LIST_SPM + ".item.details";
    //    删除商品
    public static final String SHOP_CART_LIST_SPM_ITEM_DELETE = SHOP_CART_LIST_SPM + ".item.delete";

    //    我的淘宝
    public final String MY_TAOBAO_SPM = "a2o0j.11516635";
    //    电视淘宝积分
    public final String MY_TAOBAO_SPM_JIFEN = MY_TAOBAO_SPM + ".Button.P_Point";
    //    购物车
    public final String MY_TAOBAO_SPM_CART = MY_TAOBAO_SPM + ".Button.P_Cart";
    //    收藏
    public final String MY_TAOBAO_SPM_COLLECT = MY_TAOBAO_SPM + ".Button.P_Collects";
    //    订单
    public final String MY_TAOBAO_SPM_ORDERS = MY_TAOBAO_SPM + ".Button.P_Orders";
    //    收货地址
    public final String MY_TAOBAO_SPM_ADDRESS = MY_TAOBAO_SPM + ".Button.P_Address";
    //    卡券包
    public final String MY_TAOBAO_SPM_COUPON = MY_TAOBAO_SPM + ".Button.P_Coupon";
    //    账号
    public final String MY_TAOBAO_SPM_ACCOUNT = MY_TAOBAO_SPM + ".Button.P_Account";


    //新版购物车
//    购物车页面
    public static final String NEW_SHOP_CART_LIST_SPM = TB_SITE + ".11561458";


//    选择店铺
    public static final String NEW_SHOP_CART_LIST_SPM_SHOP_SELECT = NEW_SHOP_CART_LIST_SPM + ".shop.select";
//    选择商品
    public static final String NEW_SHOP_CART_LIST_SPM_ITEM_SELECT = NEW_SHOP_CART_LIST_SPM + ".item.select";

//    进入店铺
    public static final String NEW_SHOP_CART_LIST_SPM_GOTO_SHOP = NEW_SHOP_CART_LIST_SPM + ".shop.enter";
//    购物车领券按钮
    public static final String NEW_SHOP_CART_LIST_SPM_COUPON_BUTTON = NEW_SHOP_CART_LIST_SPM + ".shop.coupons";
//    修改商品sku
    public static final String NEW_SHOP_CART_LIST_SPM_ITEM_EDIT = NEW_SHOP_CART_LIST_SPM + ".item.edit";
    //    查看商品详情
    public static final String NEW_SHOP_CART_LIST_SPM_ITEM_DETAILS = NEW_SHOP_CART_LIST_SPM + ".item.details";
    //    删除宝贝确认页
    public static final String NEW_SHOP_CART_LIST_SPM_ITEM_DELETE = NEW_SHOP_CART_LIST_SPM + ".item.delete";
    //    保留
    public static final String NEW_SHOP_CART_LIST_SPM_DELETE_NO = NEW_SHOP_CART_LIST_SPM + ".delete.no";
    //    删除
    public static final String NEW_SHOP_CART_LIST_SPM_DELETE_YES = NEW_SHOP_CART_LIST_SPM + ".delete.yes";
//    //    删除商品
    public static final String NEW_SHOP_CART_LIST_SPM_DELETE_YES_GOODS = NEW_SHOP_CART_LIST_SPM + ".item.delete";

//    删除全部失效宝贝
    public static final String NEW_SHOP_CART_LIST_SPM_DELETE_ALL_INVALID = NEW_SHOP_CART_LIST_SPM + ".item.DeleteInvalid";

    //    结算按钮
    public static final String SHOP_CART_LIST_SPM_CHECK = SHOP_CART_LIST_SPM + ".BulkOperation.chek";

    //找相似
    public static final String NEW_SHOP_CART_LIST_SPM_FIND_SIMILARITY =  NEW_SHOP_CART_LIST_SPM + ".item.similarity";

    //    新版购物车找相似页面
    public static final String NEW_SHOP_CART_LIST_SPM_ITEM_SIMILARITY =  TB_SITE + ".11801080";

    //    查看找相似商品的详情
    public static final String NEW_SHOP_CART_LIST_SPM_ITEM_SIMILARITY_DETAIL =  NEW_SHOP_CART_LIST_SPM_ITEM_SIMILARITY + ".similarity.click";


    //    猜你喜欢页面
    public static final String NEW_SHOP_CART_LIST_SPM_ITEM_LOVELY =  TB_SITE + ".11801148";
    //    猜你喜欢页面点击
    public static final String NEW_SHOP_CART_LIST_SPM_ITEM_LOVELY_CLICK =  NEW_SHOP_CART_LIST_SPM_ITEM_LOVELY + ".lovely.click";


//    购物车领券
    public static final String NEW_SHOP_CART_COUPON_SPM = TB_SITE + ".11801028";

//    点击领券
    public static final String NEW_SHOP_CART_LIST_SPM_COUPON_GET = NEW_SHOP_CART_COUPON_SPM + ".coupons.get";


    //详情页
    //视频直播页面
    public static final String LIVE_PAGE_SPM = TB_SITE + ".11975891";
    //直播页面"最热推荐"商品
    public static final String LIVE_HOT_SHOP_SPM = LIVE_PAGE_SPM + ".ItemList" + ".hot_item_p";
    //直播页面"全部商品"商品点击
    public static final String LIVE_ALL_SHOP_CLICK_SPM = LIVE_PAGE_SPM + ".ItemList" + "normal_item_p";
    //视频列表按钮的点击
    public static final String LIVE_LIST_MENU_CLICK_SPM = LIVE_PAGE_SPM + ".button" + ".VideoList";
    //关注按钮的点击
    public static final String LIVE_FOLLOW_CLICK_SPM = LIVE_PAGE_SPM + ".button" + "follow";
    //点赞按钮的点击
    public static final String LIVE_ZAN_CLICK_SPM = LIVE_PAGE_SPM + ".button" + "like";
    //开关灯按钮的点击
    public static final String LIVE_LIGHT_CLICK_SPM = LIVE_PAGE_SPM + ".button" + "light";
    //视频列表浮层的曝光
    public static final String LIVE_LIST_SHOW_SPM = LIVE_PAGE_SPM + "expose" + "VideoList";
    //视频列表浮层上的视频点击
    public static final String LIVE_LIST_CLICK_SPM = LIVE_PAGE_SPM + "Expose_VideoList" + "video_p";
    //红包发放浮层的曝光
    public static final String LIVE_RED_PACKET_SHOW_SPM = LIVE_PAGE_SPM + ".Expose" + "GiveRight_name";
    //提示浮层的曝光
    public static final String LIVE_TIPS_SHOW_SPM = LIVE_PAGE_SPM + "Expose" + "tips";

    public static final String NEW_DETAIL = TB_SITE + ".7984570";
    //侧边栏首页
    public static final String NEW_DETAIL_SIDEBAR_HOME = NEW_DETAIL+".HomeAbout.Home";
    //侧边栏我的淘宝
    public static final String NEW_DETAIL_SIDEBAR_MYTB = NEW_DETAIL+".HomeAbout.MyTb";
    //侧边栏购物车
    public static final String NEW_DETAIL_SIDEBAR_CART = NEW_DETAIL+".HomeAbout.TbDetail_Cart";
    //侧边栏我的积分
    public static final String NEW_DETAIL_SIDEBAR_POINT = NEW_DETAIL+".HomeAbout.Point";
    //侧边栏分享
    public static final String NEW_DETAIL_SIDEBAR_SHARE= NEW_DETAIL+".ItemAbout.Share";
    //侧边栏优惠券
    public static final String NEW_DETAIL_SIDEBAR_COUPON = NEW_DETAIL+".ItemAbout.Coupon";
    //侧边栏宝贝评价
    public static final String NEW_DETAIL_SIDEBAR_EVALUATE = NEW_DETAIL+".ItemAbout.Evaluate";
    //侧边栏收藏
    public static final String NEW_DETAIL_SIDEBAR_COLLECTION = NEW_DETAIL+".ItemAbout.Collection";

    //立即购买按钮
    public static final String NEW_DETAIL_BUTTON_BUY = NEW_DETAIL+".BuyAbout.Buy";

    //去店铺按钮
    public static final String NEW_DETAIL_BUTTON_SHOP = NEW_DETAIL+".BuyAbout.Shop";

    //购物车按钮
    public static final String NEW_DETAIL_BUTTON_CART = NEW_DETAIL+".BuyAbout.Cart";


}


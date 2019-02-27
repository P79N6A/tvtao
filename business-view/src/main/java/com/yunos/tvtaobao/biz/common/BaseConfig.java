package com.yunos.tvtaobao.biz.common;


/**
 * Created by huangdaju on 17/6/11.
 */

public class BaseConfig {

    //登录request code
    public static final int loginRequestCode = 1001;
    public static final int forceLoginRequestCode = 1002;

    // 如果是从购物车进入的，确认下单不成功时，用这个地址生成二维码
    public static final String CART_URL = "http://ma.taobao.com/Z7OJAr";

    //支付完成后退出activity的请求值
    public static final int ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE = 1;

    /**
     * 淘宝卖家
     */
    public final static String SELLER_TAOBAO = "C";
    /**
     * 天猫卖家
     */
    public final static String SELLER_TMALL = "B";
    //卖家类型，C&B
    public final static String SELLER_TYPE = "type";
    //卖家ID
    public final static String SELLER_NUMID = "sellerId";
    //店铺ID
    public final static String SELLER_SHOPID = "shopId";
    //店铺ID
    public final static String SHOP_FROM = "shopFrom";
    //频道ID

    public static final String ORDER_FROM_ITEM = "item"; // 从SKU进入确认下单界面
    public static final String ORDER_FROM_CART = "cart"; // 从购物车进入确认下单界面

    public static final String CATID_FROM_CART = "catid";
    public static final String NID_FROM_CART = "nid";

    //传输activity的key名称
    public static final String INTENT_KEY_MODULE = "module"; //模块Key
    public static final String INTENT_KEY_FROM = "from"; //调用来源Key
    public static final String INTENT_KEY_VOICE_FROM = "v_from";
    public static final String INTENT_KEY_FROM_CLASS = "fromClass"; //来源的类
    public static final String INTENT_KEY_MODULE_DETAIL = "detail"; //商品详情模块
    public static final String INTENT_KEY_MODULE_GOODSLIST = "goodsList"; //商品列表模块
    public static final String INTENT_KEY_MODULE_ORDERLIST = "orderList"; //订单列表模块
    public static final String INTENT_KEY_MODULE_NEW_TVBUY = "newtvshop";//专题下面的视频详情页模块
    public static final String INTENT_KEY_MODULE_ADDRESS = "address"; //收货地址模块
    public static final String INTENT_KEY_MODULE_MYTAOBAO = "mytaobao"; //我的淘宝模块
    public static final String INTENT_KEY_MODULE_WORKSHOP = "workshop"; //专题页面模块
    public static final String INTENT_KEY_MODULE_SUREJOIN = "sureJoin"; //商品属性选择页面模块
    public static final String INTENT_KEY_MODULE_TOPICS = "topics"; //专题主会场
    public static final String INTENT_KEY_MODULE_ABOUT = "about"; //关于模块
    public static final String INTENT_KEY_MODULE_COLLECTS = "collects"; //收藏模块
    public static final String INTENT_KEY_MODULE_COUPON = "coupon"; //优惠券模块
    public static final String INTENT_KEY_MODULE_SHOP = "shop"; //店铺
    public static final String INTENT_KEY_MODULE_SEARCH = "search"; //搜索
    public static final String INTENT_KEY_MODULE_MAIN = "main"; //首页
    public static final String INTENT_KEY_MODULE_RECOMMEND = "recommend"; //精选分类
    public static final String INTENT_KEY_MODULE_MENU = "menu"; //快捷菜单
    public static final String INTENT_KEY_MODULE_CART = "cart"; //购物车
    public static final String INTENT_KEY_MODULE_COMMON = "common"; //通用空白页面
    public static final String INTENT_KEY_MODULE_RELATIVE_RECOMMEND = "relative_recomment"; //相关推荐页面
    public static final String INTENT_KEY_MODULE_CHAOSHI = "chaoshi"; //超市
    public static final String INTENT_KEY_MODULE_TODAYGOODS = "todayGoods"; //今日好货
    public static final String INTENT_KEY_MODULE_CHONGZHI = "chongzhi"; //充值
    public static final String INTENT_KEY_MODULE_GRAPHICDETAILS = "graphicDetails";
    public static final String INTENT_KEY_MODULE_TAOBAOLIVE = "taobaolive";
    public static final String INTENT_KEY_MODULE_TMALLLIVE = "tmalllive";
    public static final String INTENT_KEY_MODULE_TVBUY = "tvbuy";
    public static final String INTENT_KEY_MODULE_TVBUY_SHOPPING = "tvshopping";
    public static final String INTENT_KEY_MODULE_VIDEO = "video";
    public static final String INTENT_KEY_MODULE_FLASHSALE_MAIN = "flashsale";
    public static final String INTENT_KEY_MODULE_JUHUASUAN = "juhuasuan";
    public static final String INTENT_KEY_MODULE_LOGIN = "login";
    public static final String INTENT_KEY_MODULE_ALIPAY = "alipay";
    public static final String INTENT_KEY_MODULE_TESTORDER = "testorder";
    public static final String INTENT_KEY_MODULE_CREATE_ORDER = "createorder";
    public static final String INTENT_KEY_MODULE_PAYRESULT = "payresult";
    public static final String INTENT_KEY_MODULE_ANSWER = "answer";
    public static final String INTENT_KEY_MODULE_HQLIVE = "hqlive";
    public static final String INTENT_KEY_MODULE_LIKE = "like";

    public static final String INTENT_KEY_MODULE_TAKEOUT_ORDER_DETAIL = "takeOutOrderDetail"; //外卖订单详情
    public static final String INTENT_KEY_MODULE_TAKEOUT_ORDER_LIST = "takeOutOrderList"; //外卖订单列表

    public static final String INTENT_KEY_ISZTC = "isZTC";
    public static final String INTENT_KEY_SOURCE = "source";

    public static final String INTENT_KEY_SHOPID = "shopId";
    public static final String INTENT_KEY_SERVIECID = "serviceId";
    public static final String INTENT_KEY_SCM = "scm";


    public static final String INTENT_KEY_ITEMID = "itemId"; //商品ID
    public static final String INTENT_KEY_AREAID = "areaId";//地区id
    public static final String INTENT_KEY_JUID = "juId"; //聚划算商品ID
    public static final String INTENT_KEY_KEYWORDS = "keywords"; //查找商品的关键字
    public static final String INTENT_KEY_CATEGORY_ID = "categoryId"; //查找商品所属类目的ID号
    public static final String INTENT_KEY_CATEGORY_NAME = "categoryName"; //查找商品所属类目的名称
    public static final String INTENT_KEY_IS_FIRST_REQUEST = "isFirstRequest"; //该activity是否为服务的第一页，用来购买完后做不同的处理
    public static final String INTENT_KEY_REQUEST_KEY = "requestKey"; //请求的方法的KEY，用来区分是哪个应用的回调
    public static final String INTENT_KEY_REQUEST_URL = "url"; // 关于专题模块传输的URL
    public static final String INTENT_KEY_REQUEST_TYPE = "type"; // 进入sku页面类型
    public static final String INTENT_KEY_REQUEST_SKUID = "skuId"; // 进入sku页面默认选择的skuId
    public static final String INTENT_KEY_SEARCH_TAB = "tab"; // 搜索引擎
    public static final String INTENT_KEY_TID = "tid"; //主题市场的ID号
    public static final String INTENT_KEY_EXTPARAMS = "extParams"; //详情扩展参数,如淘抢购商品显示渠道专享价
    public static final String INTENT_KEY_INDEX_VERSION = "version"; // 主界面版本
    public static final String INTENT_KEY_SKUID = "skuId"; //SKU的ID
    public static final String INTENT_KEY_SKU_TYPE = "type"; //SKU的动作类型
    public static final String INTENT_KEY_BUY_COUNT = "buyCount";//购买数量
    public static final String INTENT_KEY_IS_PRE = "isPre";// 是否预售
    public static final String INTENT_KEY_FLASHSALE_STATUS = "status";//抢购状态
    public static final String INTENT_KEY_FLASHSALE_TIMER = "time";//抢购时间
    public static final String INTENT_KEY_FLASHSALE_ID = "qianggouId";//抢购号
    public static final String INTENT_KEY_FLASHSALE_PRICE = "price";//抢购价格
    public static final String INTENT_HOME_PAGE_URL = "page";//首页加载地址
    public static final String INTENT_KEY_CARTFROM = "cartFrom";//购物车来源
    public static final String INTENT_KEY_PRICE = "price";//价格
    public static final String INTENT_KEY_PAGE_FROM = "from";//详情页阿里妈妈优惠券
    public static final String INTENT_KEY_PAGE_FROM_ZX_AMOUNT = "ZX_amount";//详情页阿里妈妈优惠券
    public static final String INTENT_KEY_LOGOUT = "logout";//详情页阿里妈妈优惠券
    public static final String INTENT_KEY_TAKEOUT_HOME = "takeouthome";//外卖店铺首页
    public static final String INTENT_KEY_TAKEOUT_SKU_DATA = "takeoutSkuData";//外卖Sku
    public static final String INTENT_KEY_IS_FROM_CART_TO_BUILDORDER ="is_from_cart_to_buildorder";//商品从购物车去下单
    public static final String INTENT_KEY_MODULE_SHOP_SEARCH = "takeoutShopSearch";
    public static final String INTENT_KEY_MODULE_TAKEOUT_WEB = "takeoutWeb";
    public static final String SWITCH_TO_HOME_ACTIVITY = "com.yunos.tvtaobao.homebundle.activity.HomeActivity";
    public static final String SWITCH_TO_COMMON_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.CommonActivity";
    public static final String SWITCH_TO_CHAOSHI_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.ChaoshiActivity";
    public static final String SWITCH_TO_CHONGZHI_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.ChongzhiActivity";
    public static final String SWITCH_TO_RELATIVERECOMMEND_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.RelativeRecommendActivity";
    public static final String SWITCH_TO_RECOMMEND_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.RecommendActivity";
    public static final String SWITCH_TO_TODAYGOODS_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.TodayGoodsActivity";
    public static final String SWITCH_TO_MYTAOBAO_ACTIVITY = "com.yunos.tvtaobao.mytaobao.activity.MyTaoBaoActivity";
    public static final String SWITCH_TO_ORDERLIST_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.OrderListActivity";
    public static final String SWITCH_TO_POINT_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.PointActivity";
    public static final String SWITCH_TO_SEARCH_ACTIVITY = "com.yunos.tvtaobao.search.activity.KeySearchActivity";
    public static final String SWITCH_TO_GOODLIST_ACTIVITY = "com.yunos.tvtaobao.goodlist.activity.GoodListActivity";
    public static final String SWITCH_TO_MAOCHAO_CART_LIST_ACTIVITY = "com.yunos.tvtaobao.cartbag.activity.ShopCartListActivity";
    public static final String SWITCH_TO_SHOPCART_LIST_ACTIVITY = "com.yunos.tvtaobao.newcart.ui.activity.NewShopCartListActivity";
    public static final String SWITCH_TO_SKU_ACTIVITY = "com.yunos.tvtaobao.tradelink.activity.SkuActivity";
    public static final String SWITCH_TO_NEW_SKU_ACTIVITY = "com.yunos.tvtaobao.sku_ui.SkuActivity";
    public static final String SWITCH_TO_DETAIL_ACTIVITY = "com.yunos.tvtaobao.detailbundle.activity.NewDetailActivity";
    public static final String SWITCH_TO_BUILDORDER_ACTIVITY = "com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity";
    public static final String SWITCH_TO_ADDRESSEDIT_ACTIVITY = "com.yunos.tvtaobao.tradelink.activity.AddressEditActivity";
    public static final String SWITCH_TO_SHOP_BLIZ_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.ShopBlizActivity";
    public static final String SWITCH_TO_SHOP_ACTIVITY = "com.yunos.tvtaobao.goodlist.activity.ShopActivity";
    public static final String SWITCH_TO_COLLECTS_ACTIVITY = "com.yunos.tvtaobao.mytaobao.activity.CollectsActivity";
    public static final String SWITCH_TO_COUPON_ACTIVITY = "com.yunos.tvtaobao.mytaobao.activity.CouponActivity";
    public static final String SWITCH_TO_ADDRESS_ACTIVITY = "com.yunos.tvtaobao.mytaobao.activity.AddressActivity";
    public static final String SWITCH_TO_NEW_TVBUY_ACTIVITY = "com.yunos.tvtaobao.zhuanti.activity.NewTvBuyActivity";
    public static final String SWITCH_TO_TVBUY_ACTIVITY = "com.yunos.tvtaobao.zhuanti.activity.TvBuyActivity";
    public static final String SWITCH_TO_FLASHSALE_MAIN_ACTIVITY = "com.yunos.tvtaobao.flashsale.activity.MainActivity";
    public static final String SWITCH_TO_JUHUASUAN_ACTIVITY = "com.yunos.tvtaobao.juhuasuan.activity.PreloadActivity";
    public static final String SWITCH_TO_MENU_ACTIVITY = "com.yunos.tvtaobao.menu.activity.MenuActivity";
    public static final String SWITCH_TO_SKILL_GRAPHICDETAILS_ACTIVITY = "com.yunos.tvtaobao.detailbundle.activity.SeckKillDetailFullDescActivity";
    public static final String SWITCH_TO_LOGIN_ACTIVITY = "com.yunos.tvtaobao.mytaobao.activity.LoginActivity";
    public static final String SWITCH_TO_ALIPAY_ACTIVITY = "com.yunos.tvtaobao.mytaobao.activity.AlipayBindActivity";
    public static final String SWITCH_TO_VOICE_CREATE_ORDER_ACTIVITY = "com.yunos.voice.activity.CreateOrderActivity";
    public static final String SWITCH_TO_LOGINOUT_ACTIVITY = "com.yunos.tvtaobao.payment.logout.LogoutActivity";
    public static final String SWITCH_TO_PAYRESULT_ACTIVITY = "com.yunos.tvtaobao.payresult.PayResultActivity";

    public static final String SWITCH_TO_ANSWER_ACTIVITY = "com.yunos.tvtaobao.answer.activity.HQIndexActivity";
    public static final String SWITCH_TO_HQLIVE_ACTIVITY = "com.yunos.tvtaobao.answer.activity.HQLiveActivity";
    public static final String SWITCH_TO_TAKEOUT_SHOP_HOME = "com.yunos.tvtaobao.takeoutbundle.activity.TakeOutShopHomeActivity";
    public static final String SWITCH_TO_TAKEOUT_ORDER_DETAIL_ACTIVITY = "com.yunos.tvtaobao.takeoutbundle.activity.TakeOutOrderDetailActivity";
    public static final String SWITCH_TO_TAKEOUT_ORDER_LIST_ACTIVITY = "com.yunos.tvtaobao.takeoutbundle.activity.TakeOutOrderListActivity";
    public static final String SWITCH_TO_YUNOSORDMODE_ACTIVITY = "com.yunos.tvtaobao.splashscreen.YunosOrDmodeActivity";
    public static final String SWITCH_TO_TAKEOUT_SHOP_SEARCH_ACTIVITY = "com.yunos.tvtaobao.takeoutbundle.activity.TakeOutShopSearchActivity";

    public static final String SWITCH_TO_TAKEOUT_WEB_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.TakeOutWebActivity";
    public static final String INTENT_KEY_MODULE_ADDRESS_WEB = "address";
    public static final String SWITCH_TO_ADDRESS_WEB_ACTIVITY = "com.yunos.tvtaobao.h5.commonbundle.activity.AddressWebActivity";

//    public static final String SWITCH_TO_GUESS_YOU_LIKE_ACTIVITY = "com.yunos.tvtaobao.newcart.ui.activity.GuessLikeActivity";

    public static final String SWITCH_TO_GUESS_YOU_LIKE_ACTIVITY = "com.yunos.tvtaobao.newcart.ui.activity.GuessLikeActivity";
    public static final String SWITCH_TO_FIND_SAME_ACTIVITY = "com.yunos.tvtaobao.newcart.ui.activity.FindSameActivity";

    public static final String SWITCH_TO_TABPBAO_LIVE_ACTIVITY = "com.yunos.tvtaobao.live.activity.TBaoLiveActivity";
}

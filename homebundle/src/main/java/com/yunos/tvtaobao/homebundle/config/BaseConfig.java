package com.yunos.tvtaobao.homebundle.config;


import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;

public class BaseConfig {

    public static final boolean IS_DEBUG = Config.isDebug();


    // 图片服务器配置
    private static final class ImageServer {

        public final static String[] DALIY = new String[] { "http://img01.daily.taobaocdn.net/bao/uploaded/",
                "http://img02.daily.taobaocdn.net/bao/uploaded/", "http://img03.daily.taobaocdn.net/bao/uploaded/" };
        public final static String[] PREDEPLOY = new String[] { "http://img01.taobaocdn.com/bao/uploaded/",
                "http://img02.taobaocdn.com/bao/uploaded/", "http://img03.taobaocdn.com/bao/uploaded/",
                "http://img04.taobaocdn.com/bao/uploaded/" };
        public final static String[] PRODUCTION = new String[] { "http://img01.taobaocdn.com/bao/uploaded/",
                "http://img02.taobaocdn.com/bao/uploaded/", "http://img03.taobaocdn.com/bao/uploaded/",
                "http://img04.taobaocdn.com/bao/uploaded/" };
    }


    //支付完成后退出activity的请求值
    public static final int ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE = 1;

    // 详细页获取PC网页详细接口
    public static final String DOMAIN_DETAIL_DESC_DAILY = "http://10.125.16.172/cache/mtop.wdetail.getItemFullDesc/4.1/";
    public static final String DOMAIN_DETAIL_DESC_PRODUCTION = "http://hws.alicdn.com/cache/mtop.wdetail.getItemFullDesc/4.1/";

    //传输activity的key名称
    public static final String INTENT_KEY_MODULE = "module"; //模块Key
    public static final String INTENT_KEY_FROM = "from"; //调用来源Key
    public static final String INTENT_KEY_MODULE_DETAIL = "detail"; //商品详情模块
    public static final String INTENT_KEY_MODULE_GOODSLIST = "goodsList"; //商品列表模块
    public static final String INTENT_KEY_MODULE_ORDERLIST = "orderList"; //订单列表模块
    public static final String INTENT_KEY_MODULE_ADDRESS = "address"; //收货地址模块
    public static final String INTENT_KEY_MODULE_MYTAOBAO = "mytaobao"; //我的淘宝模块
    public static final String INTENT_KEY_MODULE_WORKSHOP = "workshop"; //专题页面模块
    public static final String INTENT_KEY_MODULE_SUREJOIN = "sureJoin"; //商品属性选择页面模块
    public static final String INTENT_KEY_MODULE_TOPICS = "topics"; //专题主会场
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
    public static final String INTENT_KEY_MODULE_TVBUY = "tvbuy";
    public static final String INTENT_KEY_MODULE_VIDEO = "video";

    public static final String INTENT_KEY_ITEMID = "itemId"; //商品ID
    public static final String INTENT_KEY_JUID = "juId"; //聚划算商品ID
    public static final String INTENT_KEY_KEYWORDS = "keywords"; //查找商品的关键字
    public static final String INTENT_KEY_CATEGORY_ID = "categoryId"; //查找商品所属类目的ID号
    public static final String INTENT_KEY_CATEGORY_NAME = "categoryName"; //查找商品所属类目的名称
    public static final String INTENT_KEY_REQUEST_URL = "url"; // 关于专题模块传输的URL
    public static final String INTENT_KEY_REQUEST_TYPE = "type"; // 进入sku页面类型
    public static final String INTENT_KEY_REQUEST_SKUID = "skuId"; // 进入sku页面默认选择的skuId
    public static final String INTENT_KEY_SEARCH_TAB = "tab"; // 搜索引擎
    public static final String INTENT_KEY_EXTPARAMS = "extParams"; //详情扩展参数,如淘抢购商品显示渠道专享价
    public static final String INTENT_KEY_SKUID = "skuId"; //SKU的ID
    public static final String INTENT_KEY_SKU_TYPE = "type"; //SKU的动作类型
    public static final String INTENT_KEY_BUY_COUNT = "buyCount";//购买数量
    public static final String INTENT_KEY_FLASHSALE_STATUS = "status";//抢购状态
    public static final String INTENT_KEY_FLASHSALE_TIMER = "time";//抢购时间
    public static final String INTENT_KEY_FLASHSALE_ID = "qianggouId";//抢购号
    public static final String INTENT_KEY_FLASHSALE_PRICE = "price";//抢购价格
    public static final String INTENT_HOME_PAGE_URL = "page";//首页加载地址
    public static final String INTENT_KEY_CARTFROM = "cartFrom";//购物车来源
    public static final String INTENT_KEY_PRICE = "price";//价格


    //loading图缓存目录
    public static final String LOADING_CACHE_DIR = "loading";
    public static final String LOADING_CACHE_JSON = "loading_cache_json";

    public static final String PAGE_GUIDE_VERSION = "page_guide_version";

    public static final String ORDER_FROM_ITEM = "item"; // 从SKU进入确认下单界面
    public static final String ORDER_FROM_CART = "cart"; // 从购物车进入确认下单界面

    // 如果是从购物车进入的，确认下单不成功时，用这个地址生成二维码
    public static final String CART_URL = "http://ma.taobao.com/Z7OJAr";
    /**
     * 淘宝卖家
     */
    public final static String SELLER_TAOBAO = "C";
    /**
     * 天猫卖家
     */
    public final static String SELLER_TMALL = "B";
    //key
    //卖家类型，C&B
    public final static String SELLER_TYPE = "type";
    //卖家ID
    public final static String SELLER_NUMID = "sellerId";
    //店铺ID
    public final static String SELLER_SHOPID = "shopId";
    //店铺ID
    public final static String SHOP_FROM = "shopFrom";


    //登录request code
    public static final int loginRequestCode = 1001;
    public static final int forceLoginRequestCode = 1002;



}

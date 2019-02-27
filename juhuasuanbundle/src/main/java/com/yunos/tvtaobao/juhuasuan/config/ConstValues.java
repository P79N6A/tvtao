package com.yunos.tvtaobao.juhuasuan.config;


import java.util.Locale;

public class ConstValues {

    // 本地类目
    public static final String CATEGORY_TYPE_ORDER = "ORDER";
    public static final String CATEGORY_TYPE_CITY = "CITY";

    // Bundle常量

    public static final String INTENT_KEY_BUNDLE = "keys";

    public static final String INTENT_KEY_ADVERT = "key_Advert";
    public static final String INTENT_KEY_CATEGORYID = "key_Categoryid";
    public static final String INTENT_KEY_GOODTYPE = "key_GoodType";
    public static final String INTENT_KEY_NAME = "key_Name";
    public static final String INTENT_KEY_TITLE = "key_Title";
    public static final String INTENT_KEY_NAME_EN = "key_Name_En";
    public static final String INTENT_KEY_OPTION = "key_Option";
    public static final String INTENT_KEY_CITY = "key_City";
    public static final String INTENT_KEY_ACTIVITY_TO = "activity_to";

    public static final String INTENT_KEY_CATEGORYMO = "CategoryMO";
    public static final String INTENT_KEY_HOMEPAGEITEMS = "HomepageItems";
    public static final String INTENT_KEY_HOMEITEMBO = "HomeItemsBO";
    public static final String INTENT_KEY_HOMECATESBO = "HomeCatesBo";

    // home saveInstance bundle
    public static final String BUNDLE_KEY_CURRENTPOS = "bundle_currentPos";
    public static final String BUNDLE_KEY_CATEGORYLIST = "bundle_categorys";
    public static final String BUNDLE_KEY_SUMSIZE = "bundle_sunsize";

    public static final int ACTIVITY_TOBDCATE = 1;

    // 首页分类类别
    public static enum HomeItemTypes {
        HOME, // 首页
        JMP, // 聚名品
        PPT, // 品牌团 
        PPTDETAIL, //品牌团商品列表页
        CATE, // 普通类目 
        CATEGORY, //分类，根据具体参数确定是本地分类还是普通分类，为语音专供
        ORDER, // 订单 
        DETAIL; //商品详情

        /**
         * 将字符串转换成HomeItemTypes， 如果字符串对应的HomeItemTypes的不存在，则返回为null
         * @param enumType
         * @return HomeItemTypes
         */
        public static HomeItemTypes valueOfs(String arg0) {
            HomeItemTypes item;
            try {
                item = HomeItemTypes.valueOf(arg0.toUpperCase(Locale.getDefault()));
            } catch (Exception e) {
                item = null;
            }
            return item;
        }
    };

}

package com.yunos.tvtaobao.juhuasuan.common;


public class UrlKeyBaseConfig {

    //传输activity的key名称
    public static final String INTENT_KEY_MODULE = "module";//模块Key

    public static final String INTENT_KEY_FROM = "from";//调用来源Key

    public static final String INTENT_HOST_DETAIL = "detail"; // 老版本商品详情调用时用的host
    public static final String INTENT_KEY_ITEMID = "id";//商品ID,聚划算id
    public static final String INTENT_KEY_EXITCLOSE = "exitClose";//退出码

    public static final String INTENT_HOST_LIST = "list"; // 老版本商品分类调用时用的host
    public static final String INTENT_HOST_HOME = "home"; // 新版本所有接口调用时的host

    public static final String INTENT_HOST_HOME2 = "juhuasuan"; // 新版本所有接口调用时的host

    public static final String INTENT_KEY_CATEID = "cateId"; // 商品分类的类别
    public static final String INTENT_KEY_CATENAME = "cateName"; // 商品分类的类别
    public static final String INTENT_KEY_OPT_ID = "id"; // 参数
    public static final String INTENT_KEY_TYPE = "type"; //分类种类
    public static final String INTENT_KEY_TYPE_PPT = "ppt"; //品牌团
    public static final String INTENT_KEY_TYPE_JMP = "jmp"; //聚品名
    public static final String INTENT_KEY_CATE_PPTDETAIL = "pptdetail"; //品牌团详情列表
    public static final String INTENT_KEY_CATE_BDCATE = "bdcate"; //本地生鲜
    public static final String INTENT_KEY_CATE_TVGOU = "tvgou"; //TVGOU
    public static final String INTENT_KEY_CATE_CATE = "cate"; //cate
}

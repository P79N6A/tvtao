package com.yunos.tvtaobao.payment.utils;

/**
 * Created by linmu on 2018/5/31.
 */

public class SPMConfig {
    public static final String TB_SITE ="a2o0j" ;
    //    扫码登录页面
    public static final String CUSTOM_LOGIN_FRAGMENT=TB_SITE+".11527940";
//    支付宝二维码的曝光
    public static final String CUSTOM_LOGIN_ZHIFUBAO_EXPORE=CUSTOM_LOGIN_FRAGMENT+".Expore.zhifubao";
//    淘宝二维码的曝光
    public static final String CUSTOM_LOGIN_TB_EXPORE=CUSTOM_LOGIN_FRAGMENT+".Expore.taobao";
//    支付宝二维码失效提示浮层曝光事件
    public static final String CUSTOM_LOGIN_ZHIFUBAO_DISUSE=CUSTOM_LOGIN_FRAGMENT+".Expore.zhifubao_login_Disuse";
//    淘宝二维码失效提示浮层曝光事件
    public static final String CUSTOM_LOGIN_TB_DISUSE=CUSTOM_LOGIN_FRAGMENT+".Expore.taobao_login_Disuse";


}


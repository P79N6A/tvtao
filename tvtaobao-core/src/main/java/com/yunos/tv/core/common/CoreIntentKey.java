/**
 * $
 * PROJECT NAME: core
 * PACKAGE NAME: com.yunos.tv.core.bo
 * FILE NAME: IntentKey.java
 * CREATED TIME: 2014-11-25
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tv.core.common;


import com.yunos.tv.core.util.UnproguardClass;

/**
 * Class Descripton.
 * @version
 * @author hanqi
 * @data 2014-11-25 下午7:02:47
 */
public class CoreIntentKey implements UnproguardClass {

    public static final String URI_FROM = "from";
    public static final String URI_HUODONG = "huodong";
    public static final String URI_JOIN = "mmJoinUrl";//for alimama advertisement

    public static final String URI_FROM_APP = "from_app";

    public static final String URI_FROM_BUNDLE = "tvtaobao_from";
    public static final String URI_HUODONG_BUNDLE = "tvtaobao_huodong";
    public static final String URI_FROM_APP_BUNDLE = "tvtaobao_from_app";
    public static final String URI_CHANNEL_CODE = "channelcode";
    public static final String URI_CHANNEL_NAME = "channelname";
    public static final String URI_FROM_SYSTEM = "fromSystem";

    //是否为应用入口页面
    public static final String URI_IS_FIRST_ACTIVITY = "is_first_activity";

    // 调用startActivity时是否抓取异常
    public static final String IS_CATCH_EXCEPTION = "is_catch_exception";
}

/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.yunos.tvtaobao.juhuasuan.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * GSON Util
 * @author zhe.yangz
 */
public class GsonUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Gson cachedGson = null;

    public static Gson getGson() {
        if (cachedGson == null) {
            cachedGson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
        }
        return cachedGson;
    }

}

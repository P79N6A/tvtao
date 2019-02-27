/**
 * $
 * PROJECT NAME: business-view
 * PACKAGE NAME: com.yunos.tvtaobao.biz.common
 * FILE    NAME: NoPutToStack.java
 * CREATED TIME: 2015年9月8日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.common;

import java.util.HashMap;
import java.util.Map;


public class NoPutToStack {
    private static Map<String, Boolean> stackMap;

    public static Map<String, Boolean> getMap() {
        if (stackMap == null) {
            stackMap = new HashMap<String, Boolean>();
            stackMap.put("com.yunos.tvtaobao.splashscreen.StartActivity", true);
            stackMap.put("com.yunos.tvtaobao.activity.loading.WelcomeActivity", true);
            stackMap.put("com.yunos.tvtaobao.splashscreen.LoadingActivity", true);
            stackMap.put("com.yunos.tvtaobao.zhuanti.activity.PreviousActivity", true);
            stackMap.put("com.yunos.seckill.activity.HomeActivity", true);
            stackMap.put("com.yunos.tvtaobao.juhuasuan.activity.PreloadActivity", true);
            stackMap.put("com.yunos.tvtaobao.homebundle.activity.HomeActivity", true);
            stackMap.put("com.yunos.tvtaobao.menu.activity.MenuActivity", true);
            //广告页也不加入堆栈
            stackMap.put("com.yunos.tvtaobao.activity.advertisement.activity.AdvertisementActivity", true);
        }
        return stackMap;
    }

    /**
     * 语音不需要注册的页面
     * @return
     */
    public static Map<String, Boolean> getVoiceMap() {
        if (stackMap == null) {
            stackMap = new HashMap<String, Boolean>();
            stackMap.put("com.yunos.tvtaobao.splashscreen.StartActivity", true);
            stackMap.put("com.yunos.tvtaobao.activity.loading.WelcomeActivity", true);
            stackMap.put("com.yunos.tvtaobao.splashscreen.LoadingActivity", true);
            stackMap.put("com.yunos.tvtaobao.zhuanti.activity.PreviousActivity", true);
            stackMap.put("com.yunos.seckill.activity.HomeActivity", true);
            stackMap.put("com.yunos.tvtaobao.juhuasuan.activity.PreloadActivity", true);
            stackMap.put("com.yunos.tvtaobao.menu.activity.MenuActivity", true);
            //广告页也不加入堆栈
            stackMap.put("com.yunos.tvtaobao.activity.advertisement.activity.AdvertisementActivity", true);
        }
        return stackMap;
    }
}

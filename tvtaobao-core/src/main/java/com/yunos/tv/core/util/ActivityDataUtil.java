package com.yunos.tv.core.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pan on 2017/3/10.
 */

public class ActivityDataUtil {
    private static ActivityDataUtil activityData;
    public ActivityDataUtil() {}
    private static final Object initLock = new Object();
    private HashMap<String, Class<?>> mSelfActivityMap;
    private HashMap<String, String> mAppHostMap;
    private List<String> keyData;

    public static ActivityDataUtil getInstance() {
        if (activityData == null) {
            synchronized (initLock) {
                if (activityData == null) {
                    activityData = new ActivityDataUtil();
                    activityData.initKeyData();
                    activityData.initAppHostMap();
                }
            }
        }

        return activityData;
    }

    private void initKeyData() {
        keyData = new ArrayList<String>();
        keyData.add("shopId");
        keyData.add("itemId");
    }

    public void setSelfActivityMap(HashMap<String, Class<?>> selfActivityMap) {
        this.mSelfActivityMap = selfActivityMap;
    }

    /**
     * 初始化应用的查找表
     */
    private void initAppHostMap() {
        if (mAppHostMap == null) {
            mAppHostMap = new HashMap<String, String>();
            mAppHostMap.put("zhuanti", "tvtaobao://zhuanti?"); // 专题活动native
            mAppHostMap.put("browser", "tvtaobao://browser?"); // 专题活动H5
            mAppHostMap.put("taobaosdk", "tvtaobao://taobaosdk?"); // 淘宝sdk
            mAppHostMap.put("juhuasuan", "tvtaobao://juhuasuan?"); // 聚划算
            mAppHostMap.put("seckill", "tvtaobao://seckill?"); // 秒杀
            mAppHostMap.put("chaoshi", "tvtaobao://chaoshi?"); // 超市
            mAppHostMap.put("caipiao", "tvtaobao://caipiao?"); // 彩票
            mAppHostMap.put("tvshopping", "tvtaobao://tvshopping?"); // 边看边购
            mAppHostMap.put("flashsale", "tvtaobao://flashsale?"); // 淘抢购
        }
    }

    public void setKeyData(List<String> keys) {
        keyData.addAll(keys);
    }

    public Uri getPreviousUri(Class<?> c, Intent intent) {
        if (mSelfActivityMap != null) {
            for (String moudle : mSelfActivityMap.keySet()) {
                if (mSelfActivityMap.get(moudle) == c) {
                    Bundle bundle = intent.getExtras();
                    StringBuilder sb = new StringBuilder();
                    sb.append("tvtaobao://home?module=" + moudle);
                    for (String key : bundle.keySet()) {
                        for (int i = 0 ; i < keyData.size() ; i++) {
                            if (key.equals(keyData.get(i)))
                                sb.append("&" + key + "=" + bundle.get(key));
                        }
                    }
                    return Uri.parse(sb.toString());
                }
            }
        }
        return null;
    }

    public Uri getAppHostUri(Uri uri) {
        if (mAppHostMap != null) {
            String path = uri.toString();
            for (String key : mAppHostMap.keySet()) {
                if (path.startsWith(mAppHostMap.get(key))) {
                    if (path.contains("app=")) {
                        path = path.replace(mAppHostMap.get(key), "tvtaobao://home?");
                    } else {
                        path = path.replace(mAppHostMap.get(key), "tvtaobao://home?app=" + key);
                    }
                }
            }

            return Uri.parse(path);
        }
        return uri;
    }
}

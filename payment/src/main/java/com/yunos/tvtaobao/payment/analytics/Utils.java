package com.yunos.tvtaobao.payment.analytics;


import android.text.TextUtils;

import com.ut.mini.UTAnalytics;
import com.ut.mini.UTHitBuilders.UTControlHitBuilder;
import com.ut.mini.UTHitBuilders.UTCustomHitBuilder;
import com.ut.mini.UTPageHitHelper;
import com.yunos.tvtaobao.payment.utils.CloudUUIDWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具类，一些共用的方法可以放这
 *
 * @author hanqi
 * @date 2014-6-27
 */
public class Utils {

    /**
     * 拼装事件名称的方法
     *
     * @param pageName
     * @param controlName
     * @param position
     * @param args
     * @return
     */
    public static String getControlName(String pageName, String controlName, Integer position, String... args) {
        String name = "";
        if (!TextUtils.isEmpty(pageName)) {
            name = pageName + "_";
        }
        if (!TextUtils.isEmpty(controlName)) {
            name = name + controlName;
        }
        if (null != position) {
            name = name + "_" + position;
        }
        if (null != args && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                name = name + "_" + args[i];
            }
        }
        return name;
    }

    /**
     * 拼装控件事件名称的方法
     *
     * @param controlName
     * @param position
     * @param args
     * @return
     */
    public static String getControlName(String controlName, Integer position, String... args) {
        return getControlName(null, controlName, position, args);
    }

    /**
     * 统计用的Properties组装
     *
     * @param from    应用外部来源
     * @param act     应用内部来源
     * @param appName 集成的应用名称
     * @return
     */
    public static Map<String, String> getProperties(String from, String act, String appName) {
        Map<String, String> p = new HashMap<String, String>();
        if (!TextUtils.isEmpty(from)) {
            p.put("from_channel", from);
        }
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (!TextUtils.isEmpty(uuid)) {
            p.put("uuid", uuid);
        }
        if (!TextUtils.isEmpty(act)) {
            p.put("from_act", act);
        }
        if (!TextUtils.isEmpty(appName)) {
            p.put("from_app", appName);
        }
        return p;
    }

    /**
     * 获取统计最简的Properties
     *
     * @return
     */
    public static Map<String, String> getProperties() {
        Map<String, String> p = new HashMap<String, String>();
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (!TextUtils.isEmpty(uuid)) {
            p.put("uuid", uuid);
        }
        return p;
    }

    /**
     * 控件埋点，pagename为该页面所在page
     *
     * @param controlName 控件事件名称
     * @param p           参数
     */
    public static void utControlHit(String controlName, Map<String, String> p) {
        utControlHit(null, controlName, p);
    }

    /**
     * 控件埋点，强制指定pagename名称
     *
     * @param pageName    page名称
     * @param controlName 控件事件名称
     * @param p           参数
     */
    public static void utControlHit(String pageName, String controlName, Map<String, String> p) {
        UTControlHitBuilder lHitBuilder = null;
        if (TextUtils.isEmpty(pageName) && !TextUtils.isEmpty(utGetCurrentPage())) {
            lHitBuilder = new UTControlHitBuilder(controlName);
        } else {
            if (pageName != null) {
                if (!pageName.startsWith("Page_")) {
                    pageName = "Page_" + pageName;
                }
                lHitBuilder = new UTControlHitBuilder(pageName, controlName);
            }
        }
        if (lHitBuilder != null) {
            lHitBuilder.setProperties(p);
            UTAnalytics.getInstance().getDefaultTracker().send(lHitBuilder.build());
        }
    }

    /**
     * 自定义埋点
     *
     * @param eventName 事件名称
     * @param p         事件参数
     */
    public static void utCustomHit(String eventName, Map<String, String> p) {
        utCustomHit(null, eventName, p);
    }

    /**
     * 自定义埋点
     *
     * @param eventName 事件名称
     * @param pageName  页面名称
     * @param p         事件参数
     */
    public static void utCustomHit(String pageName, String eventName, Map<String, String> p) {
        UTCustomHitBuilder lHitBuilder = new UTCustomHitBuilder(eventName);

        if (!TextUtils.isEmpty(pageName)) {
            if (!pageName.startsWith("Page_")) {
                pageName = "Page_" + pageName;
            }
            lHitBuilder.setEventPage(pageName);
        }
        lHitBuilder.setProperties(p);
        UTAnalytics.getInstance().getDefaultTracker().send(lHitBuilder.build());
    }

    /**
     * 更新page参数
     *
     * @param pageName
     * @param p
     */
    public static void utUpdatePageProperties(String pageName, Map<String, String> p) {
        if (TextUtils.isEmpty(pageName)) {
            return;
        }

        if (!pageName.startsWith("Page_")) {
            pageName = "Page_" + pageName;
        }
        UTAnalytics.getInstance().getDefaultTracker().updatePageProperties(pageName, p);
    }

    /**
     * 获取当前的页面
     *
     * @return
     */
    public static String utGetCurrentPage() {
        return UTPageHitHelper.getInstance().getCurrentPageName();
    }

    /**
     * 进入页面埋点
     *
     * @param aPageObject
     * @param aCustomPageName
     */
    public static void utPageAppear(String aPageObject, String aCustomPageName) {
        if (TextUtils.isEmpty(aPageObject) || TextUtils.isEmpty(aCustomPageName)) {
            return;
        }

        if (!aPageObject.startsWith("Page_")) {
            aPageObject = "Page_" + aPageObject;
        }
        if (!aCustomPageName.startsWith("Page_")) {
            aCustomPageName = "Page_" + aCustomPageName;
        }
        UTAnalytics.getInstance().getDefaultTracker().pageAppear(aPageObject, aCustomPageName);
    }

    /**
     * 退出页面埋点
     *
     * @param aPageObject
     */
    public static void utPageDisAppear(String aPageObject) {
        if (TextUtils.isEmpty(aPageObject)) {
            return;
        }

        if (!aPageObject.startsWith("Page_")) {
            aPageObject = "Page_" + aPageObject;
        }
        UTAnalytics.getInstance().getDefaultTracker().pageDisAppear(aPageObject);
    }

}

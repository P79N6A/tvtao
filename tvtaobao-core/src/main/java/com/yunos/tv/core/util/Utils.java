package com.yunos.tv.core.util;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.Toast;

import com.ut.mini.UTAnalytics;
import com.ut.mini.UTHitBuilders;
import com.ut.mini.UTHitBuilders.UTControlHitBuilder;
import com.ut.mini.UTHitBuilders.UTCustomHitBuilder;
import com.ut.mini.UTPageHitHelper;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

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
     * 将Properties组装成String[]
     *
     * @param p
     * @return
     */
    public static String[] getKvs(Map<String, String> p) {
        String[] kvs = new String[p.size()];
        int i = 0;
        for (Object key : p.keySet()) {
            kvs[i++] = key + "=" + p.get(key);
        }
        return kvs;
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
     * 跳过某个页面
     * @param context
     */
    public static void utControlSkip(Context context) {
        UTAnalytics.getInstance().getDefaultTracker().skipPage(context);
    }

    /**
     * 2101
     * 控件埋点，pagename为该页面所在page
     *
     * @param controlName 控件事件名称
     * @param p           参数
     */
    public static void utControlHit(String controlName, Map<String, String> p) {
        utControlHit(null, controlName, p);
    }

    /**
     * 2101
     * 控件埋点，强制指定pagename名称
     *
     * @param pageName    page名称
     * @param controlName 控件事件名称
     * @param p           参数
     */
    public static void utControlHit(String pageName, String controlName, Map<String, String> p) {

        UTHitBuilders.UTControlHitBuilder lHitBuilder = null;
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
     * 19999
     * 自定义埋点
     *
     * @param eventName 事件名称
     * @param p         事件参数
     */
    public static void utCustomHit(String eventName, Map<String, String> p) {
        try {
            utCustomHit(null, eventName, p);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 19999
     * 自定义埋点
     *
     * @param eventName 事件名称
     * @param pageName  页面名称
     * @param p         事件参数
     */
    public static void utCustomHit(String pageName, String eventName, Map<String, String> p) {
        UTHitBuilders.UTCustomHitBuilder lHitBuilder = new UTCustomHitBuilder(eventName);

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
     * 进入页面埋点，页面开始展现的时候调用（onResume）
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

        try {
            UTAnalytics.getInstance().getDefaultTracker().pageAppear(aPageObject, aCustomPageName);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    /**
     * 退出页面埋点，页面啊退出的时候调用（onPause）
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

    /**
     * 打开网络设置的页面
     *
     * @param context         use activity context
     * @param openErrorString 打开失败后给出的提示
     */
    public static void startNetWorkSettingActivity(Context context, String openErrorString) {
        try {
            Intent intent = null;
            // 判断手机系统的版本 即API大于10 就是3.0或以上版本
            if (android.os.Build.VERSION.SDK_INT > 10) {
                intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            } else {
                intent = new Intent();
                ComponentName component = new ComponentName("com.android.settings", "com.android.settings.network");
                intent.setComponent(component);
                intent.setAction("android.intent.action.VIEW");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            AppDebug.e("NetWork", "openg network setting activity error");
            String error = "open setting error";
            if (!TextUtils.isEmpty(openErrorString)) {
                error = openErrorString;
            }
            Toast.makeText(CoreApplication.getApplication(), error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将一维的json字符串转为http参数形式的字符串,
     * 如{"a":"1","b":"2"} => a=1&b=2
     *
     * @param json
     * @return
     */
    public static String jsonString2HttpParam(String json) {
        if (TextUtils.isEmpty(json)) {
            return "";
        }

        JSONObject obj = null;
        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
        }
        if (obj == null) {
            return "";
        }

        String param = "";

        Iterator it = obj.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = obj.optString(key);
            if (TextUtils.isEmpty(param)) {
                param = key + "=" + value;
            } else {
                param = param + "&" + key + "=" + value;
            }
        }

        return param;
    }


    /**
     * 获取http 域名
     *
     * @param httpUrl
     * @return
     */
    public static String parseHost(String httpUrl) {
        String host = "";
        try {
            URL url = new URL(httpUrl);
            host = url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return host;
    }

    /**
     * dp 2 px
     */
    public static int dp2px(Context context, int dpVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
    }

    /**
     * 判断sdCard是否存在
     *
     * @return
     */
    public static boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    static long lastClickTime = 0;//上次点击的时间

    static int spaceTime = 1000;//时间间隔

    public static boolean isFastClick() {

        long currentTime = System.currentTimeMillis();//当前系统时间

        boolean isAllowClick;//是否允许点击

        if (currentTime - lastClickTime > spaceTime) {

            isAllowClick = false;

        } else {

            isAllowClick = true;

        }

        lastClickTime = currentTime;

        return isAllowClick;

    }

    public static void updateNextPageProperties(String spm_url) {
        if (!TextUtils.isEmpty(spm_url)) {
            Map<String, String> nextparam = new HashMap<>();
            nextparam.put("spm-url", spm_url);
            UTAnalytics.getInstance().getDefaultTracker().updateNextPageProperties(nextparam);
        }
    }


    public static String getRebateCoupon(String rebateBoCoupon) {
        try {
            Double coupon = Double.parseDouble(rebateBoCoupon);
            if (coupon > 0) {
                double num = coupon / 100;
                String numString = num + "";
                if (numString.indexOf(".") > 0) {
                    //正则表达
                    numString = numString.replaceAll("0+?$", "");//去掉后面无用的零
                    numString = numString.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
                }

                AppDebug.e("Rebate", "numString = " + numString);
                return numString;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;


    }

}

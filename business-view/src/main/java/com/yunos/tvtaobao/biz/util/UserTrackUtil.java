package com.yunos.tvtaobao.biz.util;


import android.text.TextUtils;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;

import java.util.Arrays;
import java.util.Map;

public class UserTrackUtil {

    private static final String TAG = "UserTrackUtil";

    private static String sCurVersionCode;

    private static String sNewVersionCode;

    private static boolean sIsForced;

    /**
     * 点击事件，可添加自定义参数，默认带上当前版本号、可更新版本号和是否强制安装
     * @param ct
     *            控件类型
     * @param name
     *            事件名
     * @param arg
     *            事件参数
     */
    public static void onCtrlClicked(CT ct, String name, String... arg) {
        if (arg == null)
            return;
        Map<String, String> p = getProperties();
        String[] fullArgs = new String[arg.length + p.size()];
        System.arraycopy(arg, 0, fullArgs, 0, arg.length);
        int i = 0;
        for (Object keyObject : p.keySet()) {
            String key = String.valueOf(keyObject);
            String value = p.put(key, "");
            fullArgs[arg.length + i] = key + "=" + value;
            i++;
        }
        AppDebug.d(TAG, "name: " + name + " args: " + Arrays.asList(fullArgs));
        TBS.Adv.ctrlClicked(ct, name, fullArgs);
    }

    /**
     * 自定义事件，默认带上当前版本号、可更新版本号和是否强制安装
     * @param name
     *            事件名
     */
    public static void onCustomEvent(String name) {
        onCustomEvent(name, null);
    }

    /**
     * 自定义事件，可自定参数，默认带上当前版本号、可更新版本号和是否强制安装
     * @param name
     *            事件名
     * @param properties
     *            事件参数
     */
    public static void onCustomEvent(String name, Map<String, String> properties) {
        Map<String, String> fullProperties = Utils.getProperties();
        // 取自定义的properties
        copyProperties(properties, fullProperties);
        // 取固定的properties
        copyProperties(getProperties(), fullProperties);
        AppDebug.d(TAG, "name: " + name + " properties: " + fullProperties);
        Utils.utCustomHit(Utils.utGetCurrentPage(), name, fullProperties);
    }

    /**
     * 错误信息收集
     * @param errorCode
     *            错误编号
     */
    public static void onErrorEvent(int errorCode) {
        String errorInfo = UpdatePreference.ERROR_TYPE_INFO_MAP.get(errorCode, "unknown");
        Map<String, String> properties = Utils.getProperties();
        properties.put("info_code", errorCode + "");
        properties.put("info", errorInfo);
        onCustomEvent(UpdatePreference.UT_ERROR, properties);
    }

    /**
     * 设置当前版本号
     * @param versionCode
     */
    public static void setCurVersionCode(String versionCode) {
        if (TextUtils.isEmpty(versionCode))
            return;
        sCurVersionCode = versionCode;
    }

    /**
     * 获取当前版本号
     * @return
     */
    public static String getCurVersionCode() {
        if (sCurVersionCode == null)
            return "";
        return sCurVersionCode;
    }

    /**
     * 设置可更新版本号
     * @param versionCode
     */
    public static void setNewVersionCode(String versionCode) {
        if (TextUtils.isEmpty(versionCode))
            return;
        sNewVersionCode = versionCode;
    }

    /**
     * 获取可更新版本号
     * @return
     */
    public static String getNewVersionCode() {
        if (sNewVersionCode == null)
            return "";
        return sNewVersionCode;
    }

    /**
     * 设置是否强制安装
     * @param isForced
     */
    public static void setIsForcedInstall(boolean isForced) {
        sIsForced = isForced;
    }

    /**
     * 获取是否强制安装
     * @return
     */
    public static Boolean getIsForcedInstall() {
        return sIsForced;
    }

    /**
     * 获取UUID
     * @return
     */
    public static String getUUID() {
        String uuid = CloudUUIDWrapper.getCloudUUID();
        uuid = TextUtils.isEmpty(uuid) ? "" : uuid;
        return uuid;
    }

    /**
     * 获取固定的properties
     * @return
     */
    public static Map<String, String> getProperties() {
        Map<String, String> p = Utils.getProperties(); // core中最简properties
        p.put("new_version", getNewVersionCode());
        p.put("now_version", getCurVersionCode());
        p.put("is_force", getIsForcedInstall() + "");
        return p;
    }

    private static void copyProperties(Map<String, String> src, Map<String, String> dest) {
        if (src == null || dest == null)
            return;
        for (Object keyObject : src.keySet()) {
            if (keyObject == null)
                continue;
            String key = keyObject.toString();
            String value = src.get(key);
            if (value == null)
                continue;
            dest.put(key, value);
        }
    }

}

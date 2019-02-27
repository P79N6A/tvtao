package com.tvtaobao.voicesdk.base;

import android.text.TextUtils;

import com.tvtaobao.voicesdk.register.LPR;
import com.tvtaobao.voicesdk.utils.JSONUtil;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.SharePreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yuanqihui on 2018/4/17.
 */

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/10/31
 *     desc : SDK初始化信息全部保存在这个类中
 *     version : 1.0
 * </pre>
 */

public class SDKInitConfig {
    private static final String TAG = "SDKInitConfig";

    private static String alrealdyInit;
    private static String currentPage;
    public static int sdkVersion = 0;
    private static String appkey = null;
    private static String locaStr;
    private static boolean needSearchUI = true;
    private static boolean needTakeOutUI = true;
    private static boolean needTakeOutTips = true;
    private static boolean needTVTaobaoSearch = true;
    private static boolean needRegister = false;

    public static void init(JSONObject data) {
        LogPrint.i(TAG, TAG + ".init " + data.toString());
        alrealdyInit = "true";
        appkey = JSONUtil.getString(data, "appkey");
        sdkVersion = JSONUtil.getInt(data, "sdkVersion");
        locaStr = JSONUtil.getString(data, "location");
        needSearchUI = JSONUtil.getBoolean(data, "needSearchUI", true);
        needTakeOutUI = JSONUtil.getBoolean(data, "needTakeOutUI", true);
        needTakeOutTips = SharePreferences.getBoolean("sdkInit_needTakeOutTips", true);
        needTVTaobaoSearch = SharePreferences.getBoolean("sdkInit_needTVTaobaoSearch", true);
        needRegister = JSONUtil.getBoolean(data,"needRegister");
        String packageName = JSONUtil.getString(data, "packageName"); //语音助手包名

        LPR.getInstance().init(packageName);
    }

    private static void init() {
        String sdkInit_Str = SharePreferences.getString("sdkInit_Str");
        if (TextUtils.isEmpty(sdkInit_Str)) {
            return;
        }

        try {
            JSONObject data = new JSONObject(sdkInit_Str);
            init(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getLocation() {
        if (TextUtils.isEmpty(locaStr)) {
            init();
        }
        return locaStr;
    }

    public static String getAppKey() {
        if (TextUtils.isEmpty(appkey)) {
            init();
        }
        return appkey;
    }

    public static void setAppkey(String appkey1) {
        appkey = appkey1;
    }

    public static String getCurrentPage() {
        if (CoreApplication.getApplication().getMyLifecycleHandler().isApplicationInForeground()) {
            return currentPage;
        }
        return null;
    }

    public static void setCurrentPage(String currentPage) {
        SDKInitConfig.currentPage = currentPage;
    }

    public static boolean needSearchUI() {
        if (TextUtils.isEmpty(alrealdyInit)) {
            init();
        }
        return needSearchUI;
    }

    public static boolean needTakeOutUI() {
        if (TextUtils.isEmpty(alrealdyInit)) {
            init();
        }
        return needTakeOutUI;
    }

    public static void setNeedTakeOutTips(boolean needTakeOutTips) {
        SDKInitConfig.needTakeOutTips = needTakeOutTips;
        SharePreferences.put("sdkInit_needTakeOutTips", needTakeOutTips);
    }

    public static boolean needTakeOutTips() {
        if (TextUtils.isEmpty(alrealdyInit)) {
            init();
        }
        return needTakeOutTips;
    }

    public static void setNeedTVTaobaoSearch(boolean needTVTaobaoSearch) {
        SDKInitConfig.needTVTaobaoSearch = needTVTaobaoSearch;
        SharePreferences.put("sdkInit_needTVTaobaoSearch", needTVTaobaoSearch);
    }

    public static boolean needRegister() {
        if (TextUtils.isEmpty(alrealdyInit)) {
            init();
        }
        return needRegister;
    }

    public static boolean needTVTaobaoSearch() {
        //TODO hisen目前sdk版本还没有更新1.4.5以上，没有此判断，先根据appkey版本号来
        if ("2016032917".equals(SDKInitConfig.getAppKey())) {
            return false;
        }

        if (TextUtils.isEmpty(alrealdyInit)) {
            init();
        }
        return needTVTaobaoSearch;
    }
}

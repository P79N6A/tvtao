package com.tvtaobao.voicesdk.request;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.Location;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/28
 *     desc : 上传服务器，用于记录用户ASR
 *     version : 1.0
 * </pre>
 */

public class ASRUTRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.speech.nlp.log";
    private final String VERSION = "1.0";

    String uuid = null;
    String osVersion = null;
    String model = null;
    String androidVersion = null;
    String appPackage = null;
    String appKey = null;
    String versionCode = null;
    String versionName = null;
    private Location location;
    private String lat = null;
    private String lon = null;
    public ASRUTRequest(String asr, String referrer) {
        JSONObject systemJson = new JSONObject();
        JSONObject sceneJson = new JSONObject();

        if (lat == null && lon == null) {
            String loc = SharePreferences.getString("location");
            if (loc != null) {
                location = JSON.parseObject(loc, Location.class);
            }
            if (location != null) {
                LogPrint.e(TAG, "NlpNewRequest userNick : " + location.userName + ", user : " + User.getNick());
                String userNick = location.userName;
                if (userNick != null && userNick.equals(User.getNick())) {
                    lat = location.x;
                    lon = location.y;
                }
            }
        }

        uuid = CloudUUIDWrapper.getCloudUUID();
        osVersion = SystemConfig.getSystemVersion();
        model = Build.MODEL;
        androidVersion = Build.VERSION.RELEASE;
        appPackage = AppInfo.getPackageName();
        appKey = Config.getChannel();
        versionCode = AppInfo.getAppVersionNum() + "";
        versionName = AppInfo.getAppVersionName();
        try {
            systemJson.put("uuid", uuid);
            systemJson.put("osVersion", osVersion);
            systemJson.put("model", model);
            systemJson.put("androidVersion", androidVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addParams("systemInfo", systemJson.toString());
        try {
            if (TextUtils.isEmpty(referrer)) {
                referrer = SDKInitConfig.getCurrentPage();
            }
            sceneJson.put("referrer", referrer);
            sceneJson.put("appPackage", appPackage);
            sceneJson.put("appKey", appKey);
            sceneJson.put("versionCode", versionCode);
            sceneJson.put("versionName", versionName);
            sceneJson.put("lat", lat);
            sceneJson.put("lon", lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addParams("sceneInfo", sceneJson.toString());
        addParams("asr", asr);
        LogPrint.e(TAG, "requestparams: system" + systemJson.toString() + "----scene---" + sceneJson.toString() + "-----asr---" + asr);
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return VERSION;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected JSONObject resolveResponse(JSONObject obj) throws Exception {
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}

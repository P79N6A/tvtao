package com.tvtaobao.voicesdk.request;

import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.Location;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
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
 * Created by yuanqihui on 2018/2/28.
 */

public class NlpNewRequest extends BaseMtopRequest {
    private final String API = "mtop.taobao.tvtao.speech.nlp.ask";
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

    public NlpNewRequest(String asr, String locaStr) {
        JSONObject systemJson = new JSONObject();
        JSONObject sceneJson = new JSONObject();
        if (!TextUtils.isEmpty(locaStr)) {
            try{
                location = JSON.parseObject(locaStr, Location.class);
            }catch (com.alibaba.fastjson.JSONException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

            lat = location.x;
            lon = location.y;
        }

        if (TextUtils.isEmpty(lat) && TextUtils.isEmpty(lon)
                || "TtCommon_tvtaobao-waimai".equals(SDKInitConfig.getCurrentPage())) {
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
        if (TextUtils.isEmpty(uuid)) { //取不到uuid时，传false
            uuid = "false";
        }
        osVersion = SystemConfig.getSystemVersion();
        model = Build.MODEL;
        androidVersion = Build.VERSION.SDK_INT + "";
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
            sceneJson.put("appPackage", appPackage);
            sceneJson.put("appKey", appKey);
            sceneJson.put("versionCode", versionCode);
            sceneJson.put("versionName", versionName);
            sceneJson.put("lat", lat);
            sceneJson.put("lon", lon);
            sceneJson.put("referrer", SDKInitConfig.getCurrentPage());
            if (ActivityUtil.isRunningForeground(CoreApplication.getApplication())) {
                if (ActivityUtil.getVoiceDialog() != null) {
                    sceneJson.put("className", ActivityUtil.getVoiceDialog().getClass().getName());
                } else {
                    if (ActivityUtil.getTopActivity() != null) {
                        sceneJson.put("className", ActivityUtil.getTopActivity().getClass().getName());
                    }
                }
                sceneJson.put("from","voice_application");
            } else {
                sceneJson.put("from","voice_system");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addParams("sceneInfo", sceneJson.toString());
        addParams("asr", asr);
        LogPrint.e(TAG, "requestparams: system---" + systemJson.toString() + "\n" +
                "scene---" + sceneJson.toString() + "\n" +
                "asr---" + asr);

        try {
            JSONObject extParams = new JSONObject();
            extParams.put("umToken", Config.getUmtoken(CoreApplication.getApplication()));
            extParams.put("wua", Config.getWua(CoreApplication.getApplication()));
            extParams.put("isSimulator", Config.isSimulator(CoreApplication.getApplication()));
            extParams.put("userAgent", Config.getAndroidSystem(CoreApplication.getApplication()));
            addParams("extParams", extParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected DomainResultVo resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(), DomainResultVo.class);
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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}

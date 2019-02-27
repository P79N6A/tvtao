package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/11/9
 *     desc : 语音业务动态注册接口.
 *     version : 1.0
 * </pre>
 */

public class VoiceRegisterRequest extends BaseMtopRequest {
    private final String TAG = "VoiceRegisterRequest";

    private String API = "mtop.taobao.tvtao.speech.nlp.register";
    private String VERSION = "1.0";
    public VoiceRegisterRequest(String referrer, String className, String type, String params) {
        AppDebug.d("TVTao_" + TAG, TAG + " referrer : " + referrer + " ,className : " + className + " ,type : " + type + " ,\n" +
                "parmas : " + params);
        addParams("className", className);
        addParams("referrer", referrer);
        addParams("behavior", type);
        addParams("params", params);
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (TextUtils.isEmpty(uuid)) {
            uuid = "false";
        }
        addParams("uuid", uuid);
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
        return obj;
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

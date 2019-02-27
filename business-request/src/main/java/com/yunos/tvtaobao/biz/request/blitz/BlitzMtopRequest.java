package com.yunos.tvtaobao.biz.request.blitz;


import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BlitzMtopRequest extends BaseMtopRequest {

    /**
     * 
     */
    private static final long serialVersionUID = -260150970821111706L;

    private static final String TAG = "BlitzMtopRequest";

    private boolean isNeedEcode = false;
    private boolean isNeedSession = false;
    private boolean isNeedLogin = false;
    private boolean isHttpType_Post = false;

    private String mApi = null;
    private String mApiVersion = null;

    private Map<String, String> mParamDataMap = null;

    public BlitzMtopRequest() {
        mParamDataMap = new HashMap<String, String>();
        mParamDataMap.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JSONObject resolveResponse(JSONObject obj) throws Exception {
        // AppDebug.i(TAG, "resolveResponse --> obj = " + obj);
        return obj;
    }

    @Override
    public boolean getNeedEcode() {
        return isNeedEcode;
    }

    @Override
    public boolean getNeedSession() {
        return isNeedSession;
    }

    @Override
    protected String getApi() {
        return mApi;
    }

    @Override
    protected String getApiVersion() {
        return mApiVersion;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : mParamDataMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!TextUtils.isEmpty(key)) {
                obj.put(key, value);
            }
        }
        return obj;
    }

    public boolean getBlitzMtopNeedLogin() {
        return isNeedLogin;
    }

    public boolean getBlitzMtopPost() {
        return isHttpType_Post;
    }

    private void setBlitzMtopApi(String api) {
        mApi = api;
    }

    private void setBlitzMtopApiVersion(String version) {
        mApiVersion = version;
    }

    private void setBlitzMtopNeedEcode(boolean need) {
        isNeedEcode = need;
    }

    private void setBlitzMtopNeedSession(boolean need) {
        isNeedSession = need;
    }

    private void setBlitzMtopNeedLogin(boolean need) {
        isNeedLogin = need;
    }

    private void setBlitzMtopPost(boolean post) {
        isHttpType_Post = post;
    }

    private void setBlitzMtopAppData(String data) {
        AppDebug.i(TAG, "setAppData --> data = " + data);
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject param_json = new JSONObject(data);
                Iterator it = param_json.keys();
                while (it.hasNext()) {
                    String key = it.next().toString();
                    if (!TextUtils.isEmpty(key)) {
                        Object _obj = param_json.opt(key);
                        if (_obj instanceof String) {

                            String value_str = param_json.optString(key);
                            if (!TextUtils.isEmpty(value_str)) {
                                mParamDataMap.put(key, value_str);
                            }
                            AppDebug.i(TAG, "setAppData --> key = " + key + ";  value_str = " + value_str);

                        } else if (_obj instanceof Boolean) {

                            Boolean value_boolean = param_json.optBoolean(key);
                            if (value_boolean != null) {
                                if (value_boolean) {
                                    mParamDataMap.put(key, "true");
                                } else {
                                    mParamDataMap.put(key, "false");
                                }
                            }
                            AppDebug.i(TAG, "setAppData --> key = " + key + ";  value_boolean = " + value_boolean);

                        } else if (_obj instanceof Integer) {

                            Integer value_Integer = param_json.optInt(key);
                            if (value_Integer != null) {
                                mParamDataMap.put(key, value_Integer.toString());
                            }
                            AppDebug.i(TAG, "setAppData --> key = " + key + ";  value_Integer = " + value_Integer);

                        } else if (_obj instanceof Double) {

                            Double value_Double = param_json.optDouble(key);
                            if (value_Double != null) {
                                mParamDataMap.put(key, value_Double.toString());
                            }
                            AppDebug.i(TAG, "setAppData --> key = " + key + ";  value_Double = " + value_Double);

                        } else if (_obj instanceof Long) {
                            Long value_Long = param_json.optLong(key);
                            if (value_Long != null) {
                                mParamDataMap.put(key, value_Long.toString());
                            }
                            AppDebug.i(TAG, "setAppData --> key = " + key + ";  value_Long = " + value_Long);
                        }

                    }
                }

            } catch (JSONException e) {
                AppDebug.i(TAG, "setAppData --> JSONException e  = " + e.toString());
            }
        }
    }

    public void resolveBlitzRequest(String request) {
        AppDebug.i(TAG, "resolveBlitzRequest --> request = " + request);
        try {
            JSONObject request_param = new JSONObject(request);

            String api = request_param.optString(BlitzRequestConfig.MTOP_REQUEST_API);
            String version = request_param.optString(BlitzRequestConfig.MTOP_REQUEST_VERSION);
            String data = request_param.optString(BlitzRequestConfig.MTOP_REQUEST_DATA);
            String need_login = request_param.optString(BlitzRequestConfig.MTOP_REQUEST_NEED_LOGIN);
            String use_encode = request_param.optString(BlitzRequestConfig.MTOP_REQUEST_ENCODE);
            String use_sid = request_param.optString(BlitzRequestConfig.MTOP_REQUEST_SID);
            String http_type = request_param.optString(BlitzRequestConfig.REQUEST_HTTP_TYPE);

            AppDebug.i(TAG, "resolveBlitzRequest --> api = " + api + ";  version = " + version + "; http_type = "
                    + http_type + "; need_login = " + need_login + "; use_encode = " + use_encode + "; use_sid = "
                    + use_sid + "; data = " + data);

            if (!TextUtils.isEmpty(api)) {
                setBlitzMtopApi(api);
            }

            if (!TextUtils.isEmpty(version)) {
                setBlitzMtopApiVersion(version);
            }

            if (!TextUtils.isEmpty(data)) {
                setBlitzMtopAppData(data);
            }

            if (TextUtils.equals(need_login, BlitzRequestConfig.REQUEST_VALUE_TRUE)) {
                setBlitzMtopNeedEcode(true);
                setBlitzMtopNeedSession(true);
                setBlitzMtopNeedLogin(true);
            } else {
                if (TextUtils.equals(use_encode, BlitzRequestConfig.REQUEST_VALUE_TRUE)) {
                    setBlitzMtopNeedEcode(true);
                } else {
                    setBlitzMtopNeedEcode(false);
                }

                if (TextUtils.equals(use_sid, BlitzRequestConfig.REQUEST_VALUE_TRUE)) {
                    setBlitzMtopNeedSession(true);
                } else {
                    setBlitzMtopNeedSession(false);
                }

                setBlitzMtopNeedLogin(false);
            }

            if (!TextUtils.isEmpty(http_type)) {

                if (TextUtils.equals(http_type, BlitzRequestConfig.HTTP_TYPE_POST)) {
                    setBlitzMtopPost(true);
                } else {
                    setBlitzMtopPost(false);
                }

            } else {
                setBlitzMtopPost(false);
            }

        } catch (JSONException e) {
            AppDebug.i(TAG, "resolveBlitzRequest --> JSONException e  = " + e.toString());
        }
    }

}

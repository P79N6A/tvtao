package com.yunos.tvtaobao.live.request;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveBlackList;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 16/12/22.
 */

public class LiveBlackListRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.taobaoliveapi.getblacklist";
    private String version = "1.0";

    @Override
    protected LiveBlackList resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            Log.e("LiveBlackListRequest", "obj : " + obj);
            return GsonUtil.parseJson(obj.toString(), new TypeToken<LiveBlackList>() {
            });
        }
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return version;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}

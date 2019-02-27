package com.yunos.tvtaobao.live.request;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveShopList;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 16/9/29.
 */

public class GetTMallShopRequest extends BaseMtopRequest {
    private String API = "mtop.tmall.wireless.fun.funcommodityservicemtopapi.getcommodities";
    private String version = "1.0";

    public GetTMallShopRequest(String app, String sourceId, int direction, int count) {
        addParams("app", app);
        addParams("sourceId", sourceId);
        addParams("direction", direction+"");
        addParams("count", count+"");
    }

    @Override
    protected TMallLiveShopList resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {

            Log.e("TMallLiveListBean",obj+"");
            return GsonUtil.parseJson(obj.toString(), new TypeToken<TMallLiveShopList>() {
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

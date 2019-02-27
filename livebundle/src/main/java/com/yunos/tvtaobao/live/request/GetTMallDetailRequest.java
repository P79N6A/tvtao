package com.yunos.tvtaobao.live.request;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveDetailBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 16/9/28.
 */

public class GetTMallDetailRequest extends BaseMtopRequest {
    private String API = "mtop.tmall.tlive.channel.detailinfo";
    private String version = "1.0";

    public GetTMallDetailRequest(String cid) {
        addParams("cid", cid);
    }

    @Override
    protected TMallLiveDetailBean resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {

            return GsonUtil.parseJson(obj.toString(), new TypeToken<TMallLiveDetailBean>() {
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

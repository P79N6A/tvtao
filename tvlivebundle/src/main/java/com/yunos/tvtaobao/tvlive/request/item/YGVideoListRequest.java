package com.yunos.tvtaobao.tvlive.request.item;

import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;
import com.yunos.tvtaobao.tvlive.utils.JsonResolver;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by pan on 2017/1/22.
 */

public class YGVideoListRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.tvtaoliveservice.getcnrlive";
    private String version = "1.0";

    @Override
    protected List<YGVideoInfo> resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            String result = obj.getString("result");
            JSONObject data = new JSONObject(result);
            return JsonResolver.resolveYGVideoList(data);
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

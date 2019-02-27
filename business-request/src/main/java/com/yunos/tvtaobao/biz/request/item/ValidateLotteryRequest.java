package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.ValidateLotteryBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 16/10/19.
 */

public class ValidateLotteryRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.tvtaolotteryservice.validatelotterycondition";
    private String version = "1.0";

    public ValidateLotteryRequest(String uuid) {
        addParams("source", "2");
        addParams("type", "2");
        addParams("uuid", uuid);
    }

    @Override
    protected ValidateLotteryBean resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            String result = obj.getString("result");
            JSONObject data = new JSONObject(result);
            return JSON.parseObject(data.toString(), ValidateLotteryBean.class);
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

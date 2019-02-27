package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 16/10/17.
 */

public class AddLotteryRecordRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.tvtaolotteryservice.addlotterydetailrecord";
    private String version = "1.0";

    public AddLotteryRecordRequest(String amount, String uuid) {
        addParams("amount", amount);
        addParams("source", "2");
        addParams("type", "2");
        addParams("uuid", uuid);
    }
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {

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

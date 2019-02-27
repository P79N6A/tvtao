package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.CollectList;

import org.json.JSONObject;

import java.util.Map;

public class GetCollectsRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 1132143426750772778L;
    private int pageSize = 10; // 每页返回个数
    private int currentPage = 1; //当前页码

    private final static String API = "com.taobao.mcl.fav.queryColGoods";

    public GetCollectsRequest(int page, int size) {
        this.currentPage = page;
        this.pageSize = size;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("pageSize", String.valueOf(pageSize));
        addParams("currentPage", String.valueOf(currentPage));
        addParams("method", "queryColGood");
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CollectList resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        JSONObject result = obj.optJSONObject("result");
        if (result == null) {
            return null;
        }
        return JSON.parseObject(result.toString(),CollectList.class);
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "2.0";
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

}

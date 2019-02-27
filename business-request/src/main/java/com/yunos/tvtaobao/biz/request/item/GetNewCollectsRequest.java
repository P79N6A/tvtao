package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.CollectList;
import com.yunos.tvtaobao.biz.request.bo.CollectionsInfo;
import com.yunos.tvtaobao.biz.request.bo.NewCollection;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class GetNewCollectsRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 1132143426750772778L;
    private int pageSize = 10; // 每页返回个数
    private String startTime = "0"; //当前页码

    private final static String API = "mtop.taobao.mercury.platform.collections.get";

    public GetNewCollectsRequest(String startTime, int size) {
        this.startTime = startTime;
        this.pageSize = size;
    }

    @Override
    protected Map<String, String> getAppData() {

//        addParams("pageSize", String.valueOf(pageSize));
//        addParams("currentPage", String.valueOf(currentPage));
//        addParams("method", "queryColGood");
        addParams("itemType" ,"1");
        addParams("platformCode" ,"0");
        addParams("appName","favorite");
        addParams("startTime",startTime);
        addParams("pageSize", String.valueOf(pageSize));
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CollectionsInfo resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
//        String result = obj.getString("favList");
//        if (result == null) {
//            return null;
//        }
        return JSON.parseObject(obj.toString(),CollectionsInfo.class);
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "3.0";
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

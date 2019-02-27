package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.CollectionsInfo;
import com.yunos.tvtaobao.biz.request.bo.FavModule;

import org.json.JSONObject;

import java.util.Map;

public class GetNewCollectsNumRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 1132143426750772778L;

    private final static String API = "mtop.taobao.mclaren.index.data.get";

    public GetNewCollectsNumRequest() {
        addParams("mytbVersion", AppInfo.getAppVersionName());
        addParams("moduleConfigVersion","-1");
        addParams("dataConfigVersion","-1");

    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected FavModule resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        if (obj.has("data")){
            JSONObject jsonObject = obj.getJSONObject("data");
            if(jsonObject.has("favModule")){
                JSONObject objFav = jsonObject.getJSONObject("favModule");
                return JSON.parseObject(objFav.toString(),FavModule.class);
            }
        }


        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "4.1";
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

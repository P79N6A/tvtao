package com.yunos.tvtaobao.biz.request.item;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.CartStyleBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by wuhaoteng on 2018/8/21.
 */

public class GetCartStyleRequest  extends BaseMtopRequest {
    private static final String API = "mtop.taobao.tvtao.cartstyle.getschedule";

    public GetCartStyleRequest() {
        addParams("appKey", Config.getChannel());
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected CartStyleBean resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(), CartStyleBean.class);
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}

package com.yunos.tvtaobao.biz.request.item;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/25
 * @Desc：
 */
public class TakeOutCouponStyleRequest extends BaseMtopRequest {
    private String API = "mtop.taobao.tvtao.takeoutcouponstyle.getpageinfo";
    private String version = "1.0";


    public TakeOutCouponStyleRequest() {
        addParams("appkey", Config.getChannel());
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

    @Override
    protected TakeOutCouponStyle resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        } else {
            AppDebug.e("TakeOutCouponStyleRequest", "response = " + obj.toString());
            return JSON.parseObject(obj.toString(), TakeOutCouponStyle.class);
        }
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

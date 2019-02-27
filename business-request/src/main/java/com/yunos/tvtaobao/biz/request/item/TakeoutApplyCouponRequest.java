package com.yunos.tvtaobao.biz.request.item;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeoutApplyCoupon;

import org.json.JSONObject;

import java.util.Map;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/25
 * @Desc：外卖天降红包接口
 */
public class TakeoutApplyCouponRequest extends BaseMtopRequest {
    private String API = "mtop.life.marketing.applyCoupon";
    private String version = "1.0";


    public TakeoutApplyCouponRequest(String asac,String channel) {
        addParams("type", "common");
        addParams("channel", channel);
        addParams("autoBindMobile", "true");
        addParams("asac", asac);
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
    protected TakeoutApplyCoupon resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        } else {
            AppDebug.e("TakeoutApplyCouponRequest", "response = " + obj.toString());
            return JSON.parseObject(obj.toString(), TakeoutApplyCoupon.class);
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
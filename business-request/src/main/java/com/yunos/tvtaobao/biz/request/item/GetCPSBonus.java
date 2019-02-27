package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.BonusBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pan on 16/10/12.
 */

public class GetCPSBonus extends BaseMtopRequest {
    private final String API = "mtop.aladdin.vegas.lottery.draw";
    private final String version = "1.0";

    public GetCPSBonus(String refpid, String e, String wua, String asac) {
        addParams("refpid", refpid);
        addParams("e", e);
        addParams("wua", wua);
        addParams("asac", asac);
    }

    @Override
    protected BonusBean resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            AppDebug.e("GetCPSBonus", "obj : " + obj);

            return JSON.parseObject(obj.toString(), BonusBean.class);
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

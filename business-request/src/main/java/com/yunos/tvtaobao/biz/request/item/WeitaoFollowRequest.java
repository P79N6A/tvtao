package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.WeitaoFollowBean;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/5/22.
 */

public class WeitaoFollowRequest extends BaseMtopRequest {
    private String API = "mtop.cybertron.follow.detail";
    private String mAccountType;
    private String mPubAccountId;

    public WeitaoFollowRequest(String accountType, String pubAccountId) {
        this.mAccountType = accountType;
        this.mPubAccountId = pubAccountId;
        if (!TextUtils.isEmpty(mAccountType))
            addParams("accountType", mAccountType);
        if (!TextUtils.isEmpty(mPubAccountId))
            addParams("pubAccountId", mPubAccountId);

    }

    @Override
    protected WeitaoFollowBean resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            return JSON.parseObject(obj.toString(), WeitaoFollowBean.class);
        }
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return true;
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
        return "2.0";
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}

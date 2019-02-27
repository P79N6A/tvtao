package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by chenjiajuan on 17/6/5.
 */

public class WeitaoSetDynamic extends BaseMtopRequest {
    private String API="mtop.cybertron.follow.setDynamic";
    private String mPubAccountId;
    private boolean mStatus;

    public WeitaoSetDynamic(String pubAccountId, boolean status){
        this.mPubAccountId=pubAccountId;
        this.mStatus=status;
        if (!TextUtils.isEmpty(mPubAccountId)){
            addParams("pubAccountId",mPubAccountId);
        }
        addParams("status",mStatus+"");

    }

    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        if (obj==null)
            return null;
        AppDebug.e(TAG," obj = "+obj.toString());
        return obj.toString();
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

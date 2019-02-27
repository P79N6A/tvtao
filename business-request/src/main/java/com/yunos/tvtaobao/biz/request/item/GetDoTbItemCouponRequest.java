package com.yunos.tvtaobao.biz.request.item;


import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

public class GetDoTbItemCouponRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 8102251196032160768L;

    private static final String API = "tvactivity.bonus.spend.taobaoItem";
    private static final String KEY_ITEM_ID_PARAMS = "itemId";
    private static final String KEY_SID_PARAMS = "sid";
    private static final String KEY_UUID_PARAMS = "uuid";

    private String mItemId = null;
    private String mSid = null;
    private String mUUID = null;

    public GetDoTbItemCouponRequest(String itemId) {
        mItemId = itemId;
        mSid = User.getSessionId();
        mUUID = CloudUUIDWrapper.getCloudUUID();
        addParams(KEY_ITEM_ID_PARAMS, mItemId);
        addParams(KEY_SID_PARAMS, mSid);
        addParams(KEY_UUID_PARAMS, mUUID);
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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        return obj.toString();
    }

}

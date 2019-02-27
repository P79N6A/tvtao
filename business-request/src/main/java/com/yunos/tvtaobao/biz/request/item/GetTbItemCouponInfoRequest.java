package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

public class GetTbItemCouponInfoRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 7129574033956976483L;

    private static final String API = "tvactivity.bonus.query.taobaoItemBouns";

    private static final String KEY_ITEM_NUM_ID = "itemId";

    private String itemNumId = null;

    public GetTbItemCouponInfoRequest(String itemNumId) {
        this.itemNumId = itemNumId;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams(KEY_ITEM_NUM_ID, itemNumId);
        return null;
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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }

        if (!obj.isNull("result")) {
            return obj.getString("result");
        }
        return null;
    }
}

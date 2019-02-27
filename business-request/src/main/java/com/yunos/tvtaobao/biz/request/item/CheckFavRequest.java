package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

public class CheckFavRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 8352342586453408484L;

    private String itemId = null;

    /**
     * 收藏类型 1-宝贝
     */
    private final static String TYPE_AUCTION = "1";

    private final static String API = "mtop.favorite.checkUserCollect";

    public CheckFavRequest(String itemId) {
        this.itemId = itemId;
    }


    @Override
    protected Map<String, String> getAppData() {
        addParams("sid", User.getSessionId());
        addParams("itemId", itemId);
        addParams("type", TYPE_AUCTION);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        AppDebug.d("CheckFavRequest",obj+"-------------");
        String isCollect = null;
        if (!obj.isNull("isCollect")) {
            isCollect = obj.getString("isCollect");
        }
        return isCollect;
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
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

}

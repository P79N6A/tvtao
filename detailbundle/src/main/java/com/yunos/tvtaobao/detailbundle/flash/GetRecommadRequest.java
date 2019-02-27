package com.yunos.tvtaobao.detailbundle.flash;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetRecommadRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 251274951061082994L;
    private String mItemId;
    private String mSellerId;
    private String mNeedShop;
    private String mXinxuan;
    private String mIsOffShelf;
    private String mIsInvalid;

    public GetRecommadRequest(String itemId, String sellerId) {
        mItemId = itemId;
        mSellerId = sellerId;
    }

    public GetRecommadRequest(String itemId, String sellerId, String needShop, String xinxuan, String isOffShelf,
                              String isInvalid) {
        mItemId = itemId;
        mSellerId = sellerId;
        mNeedShop = needShop;
        mXinxuan = xinxuan;
        mIsOffShelf = isOffShelf;
        mIsInvalid = isInvalid;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> data = new HashMap<String, String>();
        if (!TextUtils.isEmpty(mItemId)) {
            data.put("itemId", mItemId);
        }
        if (!TextUtils.isEmpty(mSellerId)) {
            data.put("sellerId", mSellerId);
        }

        if (!TextUtils.isEmpty(mNeedShop)) {
            data.put("needShop", mNeedShop);
        }
        if (!TextUtils.isEmpty(mXinxuan)) {
            data.put("xinxuan", mXinxuan);
        }

        if (!TextUtils.isEmpty(mIsOffShelf)) {
            data.put("isOffShelf", mIsOffShelf);
        }

        if (!TextUtils.isEmpty(mIsInvalid)) {
            data.put("isInvalid", mIsInvalid);
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<RecommendInfo> resolveResponse(JSONObject obj) throws Exception {
        if (null == obj) {
            return null;
        }
        JSONArray items = obj.optJSONArray("itemList");
        if (null == items) {
            return null;
        }
        List<RecommendInfo> ret = (List<RecommendInfo>) JSON.parseArray(items.toString(),
                RecommendInfo.class);
        return ret;
    }

    @Override
    protected String getApi() {
        return "mtop.shop.getWapRelatedItems";
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
        return true;
    }
}

package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.SellerInfo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetWapShopInfoRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -4340652315471148802L;
    //    卖家ID     sellerId    String      优先级  卖家ID>店铺ID>卖家nick   Y  
    //    店铺ID     shopId  String      优先级 卖家ID>店铺ID>卖家nick    Y  
    //    卖家nick   userNick    String      优先级 卖家ID>店铺ID>卖家nick    Y  
    //    卖家nick是否base64编码     IsUserNickEncoded       String      true or false   Y  
    private String sellerId;
    private String shopId;
    private String userNick;
    private String IsUserNickEncoded;
    private final static String API = "mtop.shop.getWapShopInfo";

    public GetWapShopInfoRequest(String sellerId, String shopId, String userNick, String IsUserNickEncoded) {
        super();
        this.sellerId = sellerId;
        this.shopId = shopId;
        this.userNick = userNick;
        this.IsUserNickEncoded = IsUserNickEncoded;
    }

    public GetWapShopInfoRequest(String sellerId, String shopId) {
        super();
        this.sellerId = sellerId;
        this.shopId = shopId;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        if (!TextUtils.isEmpty(sellerId)) {
            obj.put("sellerId", sellerId);
        }
        if (!TextUtils.isEmpty(shopId)) {
            obj.put("shopId", shopId);
        }
        if (!TextUtils.isEmpty(userNick)) {
            obj.put("userNick", userNick);
        }
        if (!TextUtils.isEmpty(IsUserNickEncoded)) {
            obj.put("IsUserNickEncoded", IsUserNickEncoded);
        }

        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected SellerInfo resolveResponse(JSONObject obj) throws Exception {
        if (!TextUtils.isEmpty(obj.toString())) {
            return JSON.parseObject(obj.toString(),SellerInfo.class);
        }
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
}

/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.item
 * FILE NAME: GetMyAlipayHongbaoList.java
 * CREATED TIME: 2016年3月3日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.MyAlipayHongbaoList;

import org.json.JSONObject;

import java.util.Map;

public class GetMyAlipayHongbaoList extends BaseMtopRequest {

    private static final long serialVersionUID = 3542412495554240732L;

    private String api = "mtop.wallet.alipay.tradepacket.queryReceiveList";
    private String v = "1.0";

    public GetMyAlipayHongbaoList(String currentPage) {
        addParams("currentPage", currentPage);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MyAlipayHongbaoList resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(), MyAlipayHongbaoList.class);
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
        return api;
    }

    @Override
    protected String getApiVersion() {
        return v;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }
}

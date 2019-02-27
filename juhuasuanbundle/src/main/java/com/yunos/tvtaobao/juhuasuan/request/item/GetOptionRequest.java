/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.item
 * FILE NAME: GetOptionRequest.java
 * CREATED TIME: 2014-10-29
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.item;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.juhuasuan.request.JsonResolver;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 获取类目
 * @version
 * @author hanqi
 * @data 2014-10-29 下午7:57:59
 */
public class GetOptionRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -1212813576928398578L;

    private String platformId;
    private String optStr;
    private Integer currentPage;
    private Integer pageSize;

    public GetOptionRequest(String platformId, String optStr, int page, int pageSize) {
        this.platformId = platformId;
        this.optStr = optStr;
        this.currentPage = page;
        this.pageSize = pageSize;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("platformId", this.platformId);
        addParams("optStr", this.optStr);
        addParams("currentPage", String.valueOf(this.currentPage));
        addParams("pageSize", String.valueOf(this.pageSize));
        return null;
    }

    @Override
    protected String getApi() {
        return "mtop.ju.option.get";
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

    @SuppressWarnings("unchecked")
    @Override
    protected CountList<Option> resolveResponse(JSONObject obj) throws Exception {
        return JsonResolver.resolveOptionList(obj);
    }
}

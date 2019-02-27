package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.PaginationItemRates;

import org.json.JSONObject;

import java.util.Map;

/**
 * 获取宝贝评价
 * @author shengchun
 */
public class GetItemRatesRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 7872347602822464697L;

    public GetItemRatesRequest(String itemId, int pageNo, int pageSize, String rateType) {
        addParams("hasRateContent", "1");
        addParams("auctionNumId", itemId);
        addParams("pageSize", String.valueOf(pageSize));
        addParams("pageNo", String.valueOf(pageNo));
        addParams("rateType", rateType);
    }

    @Override
    protected String getApi() {
        return "mtop.wdetail.getItemRates";
    }

    @Override
    protected String getApiVersion() {
        return "3.0";
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
    protected PaginationItemRates resolveResponse(JSONObject obj) throws Exception {
        return PaginationItemRates.resolveFromMTOP(obj);
    }
}

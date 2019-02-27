package com.yunos.tvtaobao.juhuasuan.request.item;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.PaginationItemRate;
import com.yunos.tvtaobao.juhuasuan.request.JsonResolver;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 获取商品评价记录
 * @author hanqi
 * @date 2014-6-4
 */
public class GetItemRateRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 3099167660741342553L;
    //商品ID
    private Long itemId;
    //页码
    private Integer pageNo;
    //每页返回个数
    private Integer pageSize;

    public GetItemRateRequest(long itemId, Integer pageNo, Integer pageSize) {
        this.itemId = itemId;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("auctionNumId", String.valueOf(itemId));
        addParams("hasRateContent", "1");
        addParams("pageNo", String.valueOf(pageNo));
        addParams("pageSize", String.valueOf(pageSize));
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected PaginationItemRate resolveResponse(JSONObject obj) throws Exception {
        return JsonResolver.resolveItemRate(obj);
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
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

}

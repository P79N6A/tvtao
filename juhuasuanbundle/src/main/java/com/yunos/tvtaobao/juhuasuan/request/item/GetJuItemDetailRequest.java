/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.item
 * FILE NAME: GetJuItemDetailRequest.java
 * CREATED TIME: 2014-11-7
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.item;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 获取聚划算商品详细，区别于获取淘宝商品详情
 * @version
 * @author hanqi
 * @data 2014-11-7 下午6:42:06
 */
public class GetJuItemDetailRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 5996334221384284511L;
    //聚划算ID
    private Long juId;

    public GetJuItemDetailRequest(Long juId) {
        this.juId = juId;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("juId", String.valueOf(juId));
        return null;
    }

    @Override
    public String getApi() {
        return "mtop.ju.detailitem.get";
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ItemMO resolveResponse(JSONObject obj) throws Exception {
        return ItemMO.fromMTOP(obj);
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

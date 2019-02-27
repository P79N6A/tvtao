/**
 * 
 */
package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 新增商品到购物车内，返回成功或者失败的提示信息
 */
public class AddBagRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -7010317316195443367L;

    private static final String API = "mtop.trade.addBag";
    //预发二套
//    private static final String API = "mtop.trade.addBag.pre2";

    //宝贝数字类型
    private String itemId;

    //宝贝的数量
    private int quantity;

    //skuId
    private String skuId;

    //扩展参数
    private String extParams;

    public AddBagRequest(String itemId, int quantity, String skuId, String extParams) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.skuId = skuId;
        this.extParams = extParams;
        AppDebug.e(TAG,"itemId = "+itemId +" quantity = "+quantity+"  skuId = "+skuId+" extParams = "+extParams);
        AppDebug.i("[tvOptionsDebug] mtop.trade.addBag exParams ----> ",extParams==null?"":extParams);
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> params = new HashMap<String, String>();
        if (!TextUtils.isEmpty(itemId)) {
            params.put("itemId", itemId);
        }
        params.put("quantity", String.valueOf(quantity));
//        params.put("cartFrom","tvtao_client");
        //skuId可以为空
        params.put("skuId", skuId);
        if (!TextUtils.isEmpty(extParams)) {
            params.put("exParams", extParams);
        }

        return params;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "3.1";
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected <T> T resolveResponse(JSONObject obj) throws Exception {
        return null;
    }
}

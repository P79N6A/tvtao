/**
 * 
 */
package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 获取地址列表请求
 * @author tianxiang
 * @date 2012-10-31 下午1:57:35
 */
public class SetDefaultAddressRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -4988470284016262595L;

    private static final String API = "com.taobao.mtop.deliver.editAddressStatus";

    // 用户32位sid用登陆后得到
    private String deliverId;

    public SetDefaultAddressRequest(String deliverId) {
        this.deliverId = deliverId;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("deliverId", deliverId);
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String resolveResponse(JSONObject obj) throws Exception {
        return null;
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "*";
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

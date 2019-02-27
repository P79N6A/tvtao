/**
 * 
 */
package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.Address;
import com.yunos.tvtaobao.biz.request.core.JsonResolver;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 获取地址列表请求
 * @author tianxiang
 * @date 2012-10-31 下午1:57:35
 */
public class GetAddressListRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -5188348129646949861L;
    private static final String API = "com.taobao.mtop.deliver.getAddressList";


    @Override
    protected Map<String, String> getAppData() {
        addParams("sid", CoreApplication.getLoginHelper(null).getSessionId());
        return null;
    }

    @Override
    protected List<Address> resolveResponse(JSONObject obj) throws Exception {
        return JsonResolver.resolveAddressList(obj);
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "3.0";
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

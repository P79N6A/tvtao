package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 获取服务器时间
 * @author tianxiang
 */
public class GetServerTimeRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 4985157177343215998L;
    // API 接口名和版本
    private static final String API = "mtop.common.getTimestamp";
    private static final String API_VERSION = "*";

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected Long resolveResponse(JSONObject json) throws Exception {
        Long serverTime = null;
        if (null != json && json.has("t")) {
            long t = json.optLong("t");
            if (t != 0) {
                serverTime = t;
            }
        }
        return serverTime;
    }

    //    /**
    //     * 重写,要绕过initialize()方法,因为这个方法里面要调用这个接口,会有死循环
    //     */
    //    @Override
    //    public String getUrl() {
    //        String url = null;
    //        try {
    //            url = getHttpDomain() + "?v=*&api=mtop.common.getTimestamp";
    //        } catch (Exception e) {
    //        }
    //
    //        return url;
    //    }

    //    @Override
    //    public String getHttpDomain() {
    //        return MtopRequestConfig.getHttpDomain();
    //    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return API_VERSION;
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

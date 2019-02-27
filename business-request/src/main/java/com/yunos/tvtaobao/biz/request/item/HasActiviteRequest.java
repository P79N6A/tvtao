package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import java.util.Map;

/**
 * Created by libin on 16/9/26.
 */

public class HasActiviteRequest extends BaseHttpRequest {
    @Override
    public String resolveResult(String result) throws Exception {
        return result;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected String getHttpDomain() {
        return "https://fragment.tmall.com/yunos/quanjupeizhi";
    }
}

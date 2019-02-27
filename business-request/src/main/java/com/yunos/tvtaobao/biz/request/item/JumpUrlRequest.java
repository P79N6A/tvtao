package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by libin on 16/11/2.
 */

public class JumpUrlRequest extends BaseHttpRequest {
    public JumpUrlRequest() {}

    @Override
    public String resolveResult(String result) throws Exception {

        return result;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> params = new TreeMap<String, String>();

        return null;
    }

    @Override
    protected String getHttpDomain() {
        return "https://fragment.tmall.com/yunos/voice/uri/mapping";
        //return "https://pre-wormhole.tmall.com/wh/fragment/yunos/voice/uri/mapping?wh_showError=true"; //预发
    }
}

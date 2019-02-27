package com.yunos.tvtaobao.biz.request.item;

import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import java.util.Map;

/**
 * Created by dingbin on 2017/4/20.
 */

public class VideoJsonRequest extends BaseHttpRequest {
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
        return "https://fragment.tmall.com/yunos/videoshoppingtest";
    }
}

package com.yunos.tvtaobao.biz.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.biz.request.bo.TopicsEntity;

import java.util.Map;

public class TopicsTmsRequest extends BaseHttpRequest {

    private static final long serialVersionUID = 4723801614094686062L;
    private String url = "";

    public TopicsTmsRequest(String url) {
        this.url = url;
    }

    @Override
    protected String getHttpDomain() {
        return this.url;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TopicsEntity resolveResult(String response) throws Exception {
        return JSON.parseObject(response,TopicsEntity.class);
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

}

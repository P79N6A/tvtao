/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.base
 * FILE NAME: BaseSimpleRequest.java
 * CREATED TIME: 2016年1月14日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.base;


import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.DataEncoder;
import com.yunos.tvtaobao.biz.request.core.DataRequest;
import com.yunos.tvtaobao.biz.request.core.DataRequestType;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.core.ServiceResponse;

import org.apache.http.Header;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import anetwork.channel.Param;

public abstract class BaseHttpRequest extends DataRequest {

    private static final long serialVersionUID = 3850312713226552530L;

    public BaseHttpRequest() {
        dataParams = new HashMap<String, String>();
    }

    /**
     * 增加参数
     * @param key
     * @param value
     */
    public void addParams(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            dataParams.put(key, value);
        }
    }

    @Override
    protected void initialize() {
        //获取自定义参数
        Map<String, String> param = getAppData();
        if (param != null) {
            dataParams.putAll(param);
        }
    }

    @Override
    public DataRequestType getRequestType() {
        return DataRequestType.OTHER;
    }

    @Override
    protected String getApi() {
        return null;
    }

    @Override
    protected String getApiVersion() {
        return null;
    }

    @Override
    protected String getHttpParams() {
        initialize();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int len = dataParams.size();
        for (Entry<String, String> entry : dataParams.entrySet()) {
            sb.append(entry.getKey()).append("=")
                    .append(DataEncoder.encodeUrl(DataEncoder.decodeUrl(entry.getValue())));
            if (i < (len - 1)) {
                sb.append("&");
            }
        }
        AppDebug.v(TAG, TAG + ".getHttpParams sb = " + sb.toString());
        return sb.toString();
    }

    @Deprecated
    @Override
    protected List<? extends NameValuePair> getPostParameters() {
        //TODO: 这个方法原先的实现并不能用于RequestImpl构建post参数！
        return null;
    }

    public List<Param> getPostParametersForRequestImpl() {
        List<Param> paramList = new ArrayList<Param>();
        if(dataParams != null){
            for (Entry<String, String> entry : dataParams.entrySet()) {
                paramList.add(new PostParam(entry.getKey(), entry.getValue()));
            }
        }
        return paramList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ServiceResponse<T> resolveResponse(String response) throws Exception {
        ServiceResponse<T> serviceResponse = new ServiceResponse<T>();
        T result = (T) resolveResult(response);
        if (null != result) {
            serviceResponse.update(ServiceCode.SERVICE_OK, result);
        } else {
            serviceResponse.update(ServiceCode.DATA_PARSE_ERROR.getCode(), null, ServiceCode.DATA_PARSE_ERROR.getMsg());
        }
        return serviceResponse;
    }

    public List<Header> getHttpHeader() {
        return null;
    }

    public abstract <T> T resolveResult(String result) throws Exception;

    //    /**
    //     * 获取参数数据
    //     * @return
    //     */
    //    protected abstract Map<String, String> getAppData();
}

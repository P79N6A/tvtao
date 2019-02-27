package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.taobao.detail.clientDomain.TBDetailResultVO;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 目前只用于解析数据返回,没有走默认的接口请求逻辑
 * @version
 * @author shengzhi.rensz
 * @data 2015-3-2 下午5:30:44
 */
public class GetItemDetailV5Request extends BaseMtopRequest {

    private static final long serialVersionUID = -2299233377170054112L;

    private String TAG = "GetItemDetailV5Request";
    private String extParams;

    public GetItemDetailV5Request(String itemId, String extParams) {
        this.extParams = extParams;

        if (!TextUtils.isEmpty(itemId)) {
            addParams("id", itemId);
        }
        addParams("ttid", Config.getTTid());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TBDetailResultVO resolveResponse(JSONObject obj) throws Exception {
        AppDebug.w(TAG, obj.toString());
        if (obj == null) {
            return null;
        }

        return JSON.parseObject(obj.toString(),TBDetailResultVO.class);
    }

    @Override
    protected String getHttpDomain() {
        String url = null;

        if (Config.getRunMode() == RunMode.DAILY) {
            url = "http://item.daily.taobao.net/modulet/v5/wdetailEsi.do";
        } else {
            url = "https://hws.alicdn.com/cache/wdetail/5.0/";
        }
        return url;
    }

    @Override
    protected String getHttpParams() {
        String params = super.getHttpParams();
        if (!TextUtils.isEmpty(extParams)) {
            params += ("&" + extParams);
        }
        AppDebug.v(TAG, TAG + ".getHttpParams.params = " + params);
        return params;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }

    @Override
    protected String getApi() {
        return null;
    }

    @Override
    protected String getApiVersion() {
        return null;
    }
}

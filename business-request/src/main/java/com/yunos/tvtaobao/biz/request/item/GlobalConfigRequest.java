package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;

import java.util.Map;

/**
 * 全局配置参数接口
 * Created by huangdaju on 16/9/20.
 */
public class GlobalConfigRequest extends BaseHttpRequest {

    private final static String HOST = "https://fragment.tmall.com/yunos/quanjupeizhi?spm=a312d.7832054.0.0.w14BnY"; //正式地址

    //    private final static String HOST = "https://pre-wormhole.tmall.com/wh/fragment/yunos/quanjupeizhi?wh_showError=true"; //预发地址
    @SuppressWarnings("unchecked")
    @Override
    public GlobalConfig resolveResult(String result) throws Exception {
        if (TextUtils.isEmpty(result))
            return null;
        return JSON.parseObject(result,GlobalConfig.class);
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    /**
     * 正式地址: https://fragment.tmall.com/yunos/quanjupeizhi?spm=a312d.7832054.0.0.w14BnY
     * 预发地址: https://pre-wormhole.tmall.com/wh/fragment/yunos/quanjupeizhi?wh_showError=true
     */


    @Override
    protected String getHttpDomain() {
        return HOST;
    }

}

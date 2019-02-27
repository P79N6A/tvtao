/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.item
 * FILE NAME: GetTmsHomeCatesRequest.java
 * CREATED TIME: 2014-11-1
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.item;


import com.yunos.alitvcompliance.TVCompliance;
import com.yunos.alitvcompliance.types.RetCode;
import com.yunos.alitvcompliance.types.RetData;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesResponse;
import com.yunos.tvtaobao.juhuasuan.util.SystemUtil;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * 聚划算首页配置
 * @version
 * @author hanqi
 * @data 2014-11-1 上午11:44:27
 */
public class GetTmsHomeCatesRequest extends BaseHttpRequest {

    private static String TAG = "GetTmsHomeCatesRequest";

    private static final long serialVersionUID = 2203205708031338515L;
    private String type;
    private Integer version;
    private static String domainName ;
    private static String hosts = "https://fragment.tmall.com/yunos/juhuasuan";


    static {
        RetData retData = TVCompliance.getComplianceDomain("https://fragment.tmall.com");
        if (retData.getCode() == RetCode.Success || retData.getCode() == RetCode.Default)
        {
            AppDebug.d(TAG, "Converted domain is "+retData.getResult());
        }
        else {
            AppDebug.d(TAG, "Original domain is "+retData.getResult());
        }
        domainName = retData.getResult();
        hosts = domainName + "/yunos/juhuasuan";
    }

    public GetTmsHomeCatesRequest() {
        type = SystemUtil.getMagnification() > 1.05 ? "big" : "small";
        if ("big".equals(type)){
            hosts = domainName + "/yunos/juhuasuan1080";
        }
        version = 20141113;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams("wh_type", type);
        addParams("wh_version", String.valueOf(version));
        return null;
    }

    @Override
    protected String getHttpDomain() {
        String domain = "";
        if (Config.getRunMode() != RunMode.PRODUCTION) {
            domain = "http://act.ju.taobao.com/go/rgn/common/tvjhs_inex_test.php";
        } else {
	        //TMS系统的老的聚划算后台地址
            //domain = "https://act.ju.taobao.com/go/rgn/tvjhs_index.php";
            //TODO 明天检测一下
	        domain = "https://fragment.tmall.com/yunos/juhuasuan";
        }
        return domain;
    }

    @SuppressWarnings("unchecked")
    @Override
    public HomeCatesResponse resolveResult(String result) throws Exception {
        int start = result.indexOf("{");
        if (start < 0) {
            start = 0;
        }
        result = result.substring(start);
        JSONObject obj = new JSONObject(result);
        return HomeCatesResponse.resolveFromMTOP(obj);
    }
}

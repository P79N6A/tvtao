package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultVO_v6;

import org.json.JSONObject;

import java.util.Map;

/**
 * 用于订单创建后,上报回调到自己服务器
 *
 * @author huangdaju
 * @data
 */
public class QueryCreateTvTaoOrderRequest extends BaseMtopRequest {


    private String TAG = "QueryCreateTvTaoOrderRequest";
    private String stbId = "XXX";    //机顶盒号
    private String nickname = "";  //用户昵称

    private final static String API = "mtop.taobao.tvtao.tvtaoorderapiservice.createtvtaoorder";

    /**
     * @param itemNumId 商品id
     * @param cartFlag  下单的入口方式(购物车;话费充值)
     * @param channel   电视淘宝渠道号
     */
    public QueryCreateTvTaoOrderRequest(String itemNumId, String orderId, String cartFlag, String channel,String versionName,String extParams) {
        stbId = DeviceUtil.getStbID();
        if (!TextUtils.isEmpty(itemNumId)) {
            addParams("itemNumId", itemNumId);
        }
        addParams("bizOrderId", orderId);
        if("cart".equals(cartFlag)){
            addParams("cartFlag", "1");
        }else{
            addParams("cartFlag", "0");
        }
        addParams("from", cartFlag);
        addParams("deviceId", stbId);
        addParams("appkey", channel);
        addParams("versionName", versionName);

        addParams("extParams", extParams);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TBDetailResultVO_v6 resolveResponse(JSONObject obj) throws Exception {
        return null;
    }


    @Override
    protected String getHttpParams() {
        return null;
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
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }


}

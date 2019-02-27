//package com.yunos.tvtaobao.biz.request.item;
//
//import android.text.TextUtils;
//
//import com.alibaba.fastjson.JSON;
//import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
//import com.yunos.tvtaobao.biz.request.bo.TakeOutBagAgain;
//import com.yunos.tvtaobao.biz.request.bo.TakeOutBagClear;
//
//import org.json.JSONObject;
//
//import java.util.Map;
//
///**
// * Created by chenjiajuan on 17/12/22.
// *
// * @describe
// */
//
//public class TakeOutBagClearRequest extends BaseMtopRequest {
//    private String API="mtop.taobao.waimai.order.again.get";
//    private String version="1.0";
//
//    public TakeOutBagClearRequest(String storeId, String orderItems) { //storeId,orderItems
//        if (!TextUtils.isEmpty(storeId)){
//            addParams("storeId",storeId);
//        }
//        if (!TextUtils.isEmpty(orderItems)){
//            addParams("orderItems",orderItems);
//        }
//    }
//
//    @Override
//    protected String getApi() {
//        return API;
//    }
//
//    @Override
//    protected String getApiVersion() {
//        return version;
//    }
//
//    @Override
//    protected Map<String, String> getAppData() {
//        return null;
//    }
//
//    @Override
//    protected TakeOutBagClear resolveResponse(JSONObject obj) throws Exception {
//        if (obj==null){
//            return  null;
//        }else {
//            return JSON.parseObject(obj.toString(),TakeOutBagClear.class);
//        }
//    }
//
//    @Override
//    public boolean getNeedEcode() {
//        return true;
//    }
//
//    @Override
//    public boolean getNeedSession() {
//        return false;
//    }
//}

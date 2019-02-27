package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.VouchersMO;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/12/25.
 *
 * @describe
 */

public class TakeOutVouchersRequest extends BaseMtopRequest {
    private final String TAG="mtop.taobao.waimai.myvouchers.put";
    private final String VERSION="1.0";

    public TakeOutVouchersRequest(String storeId){
        if (!TextUtils.isEmpty(storeId)){
            addParams("storeId",storeId);
        }
    }

    @Override
    protected String getApi() {
        return TAG;
    }

    @Override
    protected String getApiVersion() {
        return VERSION;
    }

    @Override
    protected Map<String, String> getAppData() {
        return null;
    }

    @Override
    protected VouchersMO resolveResponse(JSONObject obj) throws Exception {
        if (obj==null){
            return null;
        }else {
            return JSON.parseObject(obj.toString(),VouchersMO.class);
        }
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}

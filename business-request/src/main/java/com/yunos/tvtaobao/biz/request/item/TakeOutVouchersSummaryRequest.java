package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.VouchersMO;
import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.bo.VouchersSummary;

import org.json.JSONObject;

import java.util.Map;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/17
 * @Desc：
 */
public class TakeOutVouchersSummaryRequest extends BaseMtopRequest {
    private final String TAG = "mtop.taobao.waimai.shopvouchers.summary";
    private final String VERSION = "1.0";

    public TakeOutVouchersSummaryRequest(String storeId) {
        if (!TextUtils.isEmpty(storeId)) {
            addParams("storeId", storeId);
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
    protected VouchersSummary resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        } else {
            return JSON.parseObject(obj.toString(), VouchersSummary.class);
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
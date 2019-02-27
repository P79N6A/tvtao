package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.VouchersList;

import org.json.JSONObject;

import java.util.Map;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/17
 * @Desc：外卖--获取超级会员代金券+店铺代金券信息。领取、已经领取等状态。
 */
public class TakeOutVouchersListRequest extends BaseMtopRequest {
    private final String TAG = "mtop.taobao.waimai.shopvouchers.list";
    private final String VERSION = "1.0";

    public TakeOutVouchersListRequest(String storeId) {
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
    protected VouchersList resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        } else {
            return JSON.parseObject(obj.toString(), VouchersList.class);
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

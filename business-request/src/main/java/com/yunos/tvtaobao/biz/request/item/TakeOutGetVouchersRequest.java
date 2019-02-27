package com.yunos.tvtaobao.biz.request.item;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeVouchers;

import org.json.JSONObject;

import java.util.Map;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/17
 * @Desc：领取代金券（可能是超级会员代金券、店铺代金券） activityId number 券活动 id
 */
public class TakeOutGetVouchersRequest extends BaseMtopRequest {
    private final String TAG = "mtop.taobao.waimai.shopvouchers.take";
    private final String VERSION = "1.0";


    /**
     * @param storeId      餐厅 id
     * @param activityId   券活动 id
     * @param exchangeType 兑换类型 -1:年终奖 0:免费 1:权益 2:红包 3:奖励金
     * @param storeIdType  storeId 类型，0 为淘宝店铺id（默认），1 为饿了么中后台店铺id
     */
    public TakeOutGetVouchersRequest(String storeId, String activityId, String exchangeType, String storeIdType) {
        if (!TextUtils.isEmpty(storeId)) {
            addParams("storeId", storeId);
        }

        if (!TextUtils.isEmpty(activityId)) {
            addParams("activityId", activityId);
        }

        if (!TextUtils.isEmpty(exchangeType)) {
            addParams("exchangeType", exchangeType);
        }
        if (!TextUtils.isEmpty(storeIdType)) {
            addParams("storeIdType", storeIdType);
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
    protected TakeVouchers resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        } else {
            return JSON.parseObject(obj.toString(), TakeVouchers.class);
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

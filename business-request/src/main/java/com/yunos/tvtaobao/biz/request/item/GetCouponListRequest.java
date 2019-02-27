package com.yunos.tvtaobao.biz.request.item;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.CouponList;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetCouponListRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 6403615798555209809L;
    private String sid = "";
    private int page = 1;
    private int size = 20;
    //卡券包的优惠券类型（参考：0:店铺优惠券,1:商品优惠券,15:店铺红包） 可以是1_2_3...
    private String tag = "";
    private final static String API = "mtop.msp.coupon.getMyCouponListByType";

    public GetCouponListRequest(int page, int size, String tag) {
        super();
        this.sid = User.getSessionId();
        this.page = page;
        this.size = size;
        this.tag = tag;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> obj = new HashMap<String, String>();
        if (!TextUtils.isEmpty(sid)) {
            obj.put("sid", sid);
        }
        if (!TextUtils.isEmpty(tag)) {
            obj.put("tag", tag);
        }
        obj.put("page", String.valueOf(page));
        obj.put("size", String.valueOf(size));

        //优惠券类别 0:所有优惠 1:线上优惠券 2:生活优惠券 3:38券
        obj.put("couponType", "0");
        //优惠券状态 0:未使用 1:已使用 2：已过期
        obj.put("stateType", "0");
        //优惠券领用情况 0:最近领用 1:即将过期 2:距离最近
        obj.put("orderType", "0");

        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CouponList resolveResponse(JSONObject obj) throws Exception {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(obj.toString(),CouponList.class);
    }

    @Override
    protected String getApi() {
        return API;
    }

    @Override
    protected String getApiVersion() {
        return "1.0";
    }

    @Override
    public boolean getNeedEcode() {
        return true;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }
}

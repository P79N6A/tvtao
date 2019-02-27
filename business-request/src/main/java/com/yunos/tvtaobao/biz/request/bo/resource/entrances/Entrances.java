package com.yunos.tvtaobao.biz.request.bo.resource.entrances;

import java.io.Serializable;

/**
 * Created by huangdaju on 16/9/19.
 */
public class Entrances implements Serializable {

    private Coupon mCoupon;

    public Coupon getCoupon() {
        return mCoupon;
    }

    public void setCoupon(Coupon coupon) {
        mCoupon = coupon;
    }
}

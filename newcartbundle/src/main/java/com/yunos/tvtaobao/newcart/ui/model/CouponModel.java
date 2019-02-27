package com.yunos.tvtaobao.newcart.ui.model;

import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.biz.base.BaseModel;
import com.yunos.tvtaobao.newcart.ui.contract.CouponContract;

import java.util.List;

/**
 * Created by yuanqihui on 2018/6/28.
 */

public class CouponModel extends BaseModel implements CouponContract.Model {

    @Override
    public void getShopCoupon(String mSellerId, BizRequestListener<List<ShopCoupon>> listener) {
        mBusinessRequest.getShopCoupon(mSellerId, listener);
    }

}

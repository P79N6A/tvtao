package com.yunos.tvtaobao.newcart.ui.presenter;

import android.app.Activity;

import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.biz.base.BasePresenter;
import com.yunos.tvtaobao.newcart.ui.activity.CouponActivity;
import com.yunos.tvtaobao.newcart.ui.contract.CouponContract;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by yuanqihui on 2018/7/18.
 */

public class CouponPresenter extends BasePresenter<CouponContract.Model, CouponContract.View> {
    public CouponPresenter(CouponContract.Model model, CouponContract.View rootView) {
        super(model, rootView);
    }

    public void getShopCoupon(Activity activity, String mSellerId) {
        mModel.getShopCoupon(mSellerId, new GetShopCouponListener(new WeakReference<BaseActivity>((BaseActivity) activity)));
    }


    private class GetShopCouponListener extends BizRequestListener<List<ShopCoupon>> {

        public GetShopCouponListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            CouponActivity couponActivity = (CouponActivity) mBaseActivityRef.get();
            if (couponActivity != null) {
                // 允许按返回键取消进度条
                couponActivity.setProgressCancelable(true);
                couponActivity.OnWaitProgressDialog(false);
            }
            return true;
        }

        @Override
        public void onSuccess(List<ShopCoupon> data) {
            // 允许按返回键取消进度条
            mRootView.setProgressCancelable(true);
            mRootView.showProgressDialog(false);
            if (data != null && data.size() > 0) {
                mRootView.setEmptyShow(false);
                for (int i = 0; i < data.size(); i++) {
                    for (int j = 0; j < data.size() - i - 1; j++) {
                        if (Integer.parseInt(data.get(j).getDiscountFee()) > Integer.parseInt(data.get(j + 1).getDiscountFee())) {
                            ShopCoupon shopCoupon = data.get(j);
                            data.set(j, data.get(j + 1));
                            data.set(j + 1, shopCoupon);
                        }
                    }
                }
                mRootView.setCouponData(data);

            } else {
                mRootView.setEmptyShow(true);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }
}

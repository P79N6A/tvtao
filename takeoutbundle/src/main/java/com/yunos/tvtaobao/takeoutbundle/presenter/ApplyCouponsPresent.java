package com.yunos.tvtaobao.takeoutbundle.presenter;

import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeoutApplyCoupon;
import com.yunos.tvtaobao.biz.request.item.TakeOutCouponStyle;

import java.lang.ref.WeakReference;
import java.util.List;

import com.yunos.tvtaobao.takeoutbundle.dialog.ApplyCouponDialog;


/**
 * @Author：wuhaoteng
 * @Date:2018/12/26
 * @Desc：外卖天降红包逻辑
 */
public class ApplyCouponsPresent implements IApplyCouponsPresent {
    private BaseActivity baseActivity;
    private BusinessRequest businessRequest;
    private TakeOutCouponStyle takeOutCouponStyle;

    public ApplyCouponsPresent(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Override
    public void initCouponStyle() {
        businessRequest.requestTakeOutCouponStyle(new GetCouponStyleListener(new WeakReference<BaseActivity>(baseActivity)));
    }

    @Override
    public void applyCoupons(String asac, String channel) {
        businessRequest.requestTakeOutApplyCoupon(asac, channel, new ApplyCouponsListener(new WeakReference<BaseActivity>(baseActivity)));
    }

    @Override
    public void onStart() {
        businessRequest = BusinessRequest.getBusinessRequest();
        initCouponStyle();
    }

    @Override
    public void onDestroy() {

    }


    private class ApplyCouponsListener extends BizRequestListener<TakeoutApplyCoupon> {

        public ApplyCouponsListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {

            return false;
        }

        @Override
        public void onSuccess(TakeoutApplyCoupon data) {
            if (data != null) {
                String retCode = data.getRetCode();
                if ("200".equals(retCode)) {
                    //200代表红包领取成功
                    List<TakeoutApplyCoupon.CouponsBean> coupons = data.getCoupons();
                    ApplyCouponDialog dialog = new ApplyCouponDialog(baseActivity);
                    dialog.setData(coupons, takeOutCouponStyle);
                    if (!baseActivity.isHasDestroyActivity()) {
                        dialog.show();
                    }
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class GetCouponStyleListener extends BizRequestListener<TakeOutCouponStyle> {

        public GetCouponStyleListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(TakeOutCouponStyle data) {
            if (data != null) {
                applyCoupons(data.getSafeCode(), data.getCouponFlag());
                takeOutCouponStyle = data;
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }
}

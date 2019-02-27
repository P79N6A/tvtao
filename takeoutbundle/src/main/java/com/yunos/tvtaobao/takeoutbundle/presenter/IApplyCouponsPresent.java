package com.yunos.tvtaobao.takeoutbundle.presenter;

import com.yunos.tvtaobao.biz.base.IPresenter;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/26
 * @Desc：天降红包p层接口
 */
public interface IApplyCouponsPresent extends IPresenter {
    /**
     * 1.获取红包配置的样式
     * 2.拿到asac（安全码）和 红包渠道号com.yunos.tvtaobao.takeoutbundle.mvp.view.IApplyCouponsView;
     */
    void initCouponStyle();

    /**IApplyCouponsView;
     * 饿了么领券接口
     *
     * @param asac    安全码
     * @param channel 红包渠道号
     */
    void applyCoupons(String asac, String channel);
}

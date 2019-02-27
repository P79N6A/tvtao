package com.yunos.tvtaobao.payresult;

import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.DynamicRecommend;

import java.lang.ref.WeakReference;

/**
 * Created by wuhaoteng on 2018/8/30.
 * 获取支付结果页热门推荐运营位
 */

public class GetDynamicRecommendListener extends BizRequestListener<DynamicRecommend> {
    private PayResultDataFetcher.OnDynamicRecommendFetchListener mListener;
    public GetDynamicRecommendListener(WeakReference<BaseActivity> baseActivityRef,PayResultDataFetcher.OnDynamicRecommendFetchListener listener) {
        super(baseActivityRef);
        mListener = listener;
    }

    @Override
    public boolean onError(int resultCode, String msg) {
        DynamicRecommend dynamicRecommend = new DynamicRecommend();
        dynamicRecommend.setStatus("0");
        mListener.onComplete(dynamicRecommend);
        return false;
    }

    @Override
    public void onSuccess(DynamicRecommend data) {
        mListener.onComplete(data);
    }

    @Override
    public boolean ifFinishWhenCloseErrorDialog() {
        return false;
    }
}

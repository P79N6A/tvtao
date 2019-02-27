package com.yunos.tvtaobao.tradelink.request;


import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.tradelink.buildorder.bean.PaidAdvertBo;

public class TvTaobaoBusinessRequest extends BusinessRequest {

    public static final Long RECOMMEND_CATEGORY_ID = 0L;
    private static TvTaobaoBusinessRequest mBusinessRequest;

    /**
     * 单例
     */
    private TvTaobaoBusinessRequest() {
    }

    public static TvTaobaoBusinessRequest getBusinessRequest() {
        if (mBusinessRequest == null) {
            mBusinessRequest = new TvTaobaoBusinessRequest();
        }
        return mBusinessRequest;
    }

    /**
     * 销毁
     */
    public void destory() {
        mBusinessRequest = null;
    }


    /**
     * 获取支付完成后弹出的广告内容
     * @param bizOrderId 订单号
     * @param listener
     */
    public void getPaidAdvertRequest(final String bizOrderId, final RequestListener<PaidAdvertBo> listener) {
        baseRequest(new PaidAdvertRequest(bizOrderId), listener, false);
    }
}

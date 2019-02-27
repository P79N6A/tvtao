package com.yunos.tvtaobao.cartbag.request;


import com.yunos.tvtaobao.biz.request.BusinessRequest;

public class TvCartTaobaoBusinessRequest extends BusinessRequest {

    public static final Long RECOMMEND_CATEGORY_ID = 0L;
    private static TvCartTaobaoBusinessRequest mBusinessRequest;

    /**
     * 单例
     */
    private TvCartTaobaoBusinessRequest() {
    }

    public static TvCartTaobaoBusinessRequest getBusinessRequest() {
        if (mBusinessRequest == null) {
            mBusinessRequest = new TvCartTaobaoBusinessRequest();
        }
        return mBusinessRequest;
    }

    /**
     * 销毁
     */
    public void destory() {
        mBusinessRequest = null;
    }


}

package com.yunos.tvtaobao.request;


import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.bo.DeviceBo;
import com.yunos.tvtaobao.request.item.GetDeviceRequest;

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
     * 获取设备appkey
     * @param model
     * @param listener
     */
    public void requestDevice(String model, final RequestListener<DeviceBo> listener){
        baseRequest(new GetDeviceRequest(model),listener, false);
    }


}

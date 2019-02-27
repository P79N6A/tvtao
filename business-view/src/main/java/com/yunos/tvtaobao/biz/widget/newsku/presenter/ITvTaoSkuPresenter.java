package com.yunos.tvtaobao.biz.widget.newsku.presenter;

import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.base.IPresenter;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/4
 *     desc :
 *     version : 1.0
 * </pre>
 */

public interface ITvTaoSkuPresenter extends IPresenter {
    void doDetailRequest(String itemId, String areaId);
    void doDetailData(TBDetailResultV6 tbDetailResultV6);
    void setDefaultSku(String skuId);
}

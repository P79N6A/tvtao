/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.listener
 * FILE NAME: JuItemRetryRequestListener.java
 * CREATED TIME: 2015-2-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.listener;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

/**
 * 获取聚划算商品详情侦听
 * @version
 * @author hanqi
 * @data 2015-2-13 下午8:58:39
 */
public abstract class JuItemRetryRequestListener extends JuRetryRequestListener<ItemMO> {

    protected String mCateId;

    public JuItemRetryRequestListener(BaseActivity activity, String cateId) {
        super(activity);
        mCateId = cateId;
    }

    public JuItemRetryRequestListener(BaseActivity activity, String cateId, boolean finish) {
        super(activity, finish);
        mCateId = cateId;
    }

    @Override
    public ItemMO initData(ItemMO item) {
        if (null != mCateId) {
            MyBusinessRequest.getInstance().updateCategoryListGoodsInfo(mCateId, item);
        }
        return item;
    }

}

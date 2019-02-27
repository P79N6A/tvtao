/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.listener
 * FILE NAME: JuDetailItemsFilterRetryRequestListener.java
 * CREATED TIME: 2015-2-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.listener;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

/**
 * 详情页调用商品列表专用
 * @version
 * @author hanqi
 * @data 2015-2-13 下午8:14:45
 */
public abstract class JuDetailItemsFilterRetryRequestListener extends JuItemsFilterRetryRequestListener {

    private int mPosition;

    public JuDetailItemsFilterRetryRequestListener(BaseActivity activity, CategoryMO cate, int position) {
        super(activity, cate, false);
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

}

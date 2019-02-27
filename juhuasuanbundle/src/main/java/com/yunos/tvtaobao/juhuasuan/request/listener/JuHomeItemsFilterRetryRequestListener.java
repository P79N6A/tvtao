/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.listener
 * FILE NAME: JuHomeItemsFilterRetryRequestListener.java
 * CREATED TIME: 2015-2-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.listener;


import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * HomeActvity商品列表页调用商品列表专用
 * @version
 * @author hanqi
 * @data 2015-2-13 下午8:34:28
 */
public abstract class JuHomeItemsFilterRetryRequestListener<T> extends JuItemsFilterRetryRequestListener {

    protected WeakReference<T> mT;

    /**
     * @param activity
     * @param cate
     */
    public JuHomeItemsFilterRetryRequestListener(BaseActivity activity, CategoryMO cate, T t) {
        super(activity, cate);
        mT = new WeakReference<T>(t);
    }

}

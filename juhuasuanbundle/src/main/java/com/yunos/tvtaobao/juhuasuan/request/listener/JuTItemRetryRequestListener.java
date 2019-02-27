/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.listener
 * FILE NAME: JuViewItemRetryRequestListener.java
 * CREATED TIME: 2015-2-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.listener;


import com.yunos.tvtaobao.biz.activity.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * 在view中调用聚划算商品详情接口回调侦听
 * @version
 * @author hanqi
 * @data 2015-2-13 下午9:33:04
 */
public abstract class JuTItemRetryRequestListener<T> extends JuItemRetryRequestListener {

    protected WeakReference<T> mT;

    public JuTItemRetryRequestListener(BaseActivity activity, T t, String cateId) {
        super(activity, cateId);
        mT = new WeakReference<T>(t);
    }

    public JuTItemRetryRequestListener(BaseActivity activity, T t, String cateId, boolean finish) {
        super(activity, cateId, finish);
        mT = new WeakReference<T>(t);
    }

}

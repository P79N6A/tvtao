/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.request.listener
 * FILE NAME: JuTbItemRetryRequestListener.java
 * CREATED TIME: 2015-2-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.request.listener;


import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.bo.TbItemDetail;

import java.lang.ref.WeakReference;

/**
 * 获取淘宝商品详情接口回调
 * T 请传要调用接口所在类的对象
 * @version
 * @author hanqi
 * @data 2015-2-13 下午9:45:17
 */
public abstract class JuTbItemRetryRequestListener<T> extends JuRetryRequestListener<TbItemDetail> {

    protected WeakReference<T> mT;

    public JuTbItemRetryRequestListener(BaseActivity activity, T t) {
        super(activity);
        mT = new WeakReference<T>(t);
    }

    public JuTbItemRetryRequestListener(BaseActivity activity, T t, boolean finish) {
        super(activity, finish);
        mT = new WeakReference<T>(t);
    }

}

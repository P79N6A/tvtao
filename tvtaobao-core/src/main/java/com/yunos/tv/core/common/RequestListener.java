/**
 * $
 * PROJECT NAME: core
 * PACKAGE NAME: com.yunos.tv.core.common
 * FILE    NAME: BusinessRequestListener.java
 * CREATED TIME: 2015-1-16
 * COPYRIGHT: Copyright(c) 2013 ~ 2015  All Rights Reserved.
 */
package com.yunos.tv.core.common;


public interface RequestListener<T> {
    /**
     * 接口返回后执行,一般用于UI显示
     * @param data
     * @param resultCode
     * @param msg
     * @return
     */
    void onRequestDone(T data, int resultCode, String msg);

    public static interface RequestListenerWithLogin<T> extends RequestListener<T> {
        void onStartLogin();
    }

}

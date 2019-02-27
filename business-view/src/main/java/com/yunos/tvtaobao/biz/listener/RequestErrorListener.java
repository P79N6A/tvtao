/** $
 * PROJECT NAME: business-view
 * PACKAGE NAME: com.yunos.tvtaobao.biz.listener
 * FILE    NAME: RequestErrorListener.java
 * CREATED TIME: 2015-1-16
 *    COPYRIGHT: Copyright(c) 2013 ~ 2015  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.listener; 


public interface RequestErrorListener {

    /**
     * 根据错误码进行一些处理
     * @param errorCode
     * @param errorMsg
     * @return
     */
    public boolean onError(int errorCode, String errorMsg);
}

/** $
 * PROJECT NAME: business-view
 * PACKAGE NAME: com.yunos.tvtaobao.biz.listener
 * FILE    NAME: WebviewClientListener.java
 * CREATED TIME: 2015年5月13日
 *    COPYRIGHT: Copyright(c) 2013 ~ 2015  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.listener; 


public interface WebviewClientListener {
    void onPageStarted();
    void onPageFinished();
    void onReceivedError(int errorCode, String description, String failingUrl);
}

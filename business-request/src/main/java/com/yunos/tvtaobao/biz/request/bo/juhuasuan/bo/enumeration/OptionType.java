/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.bo.enumeration
 * FILE NAME: OptionType.java
 * CREATED TIME: 2014-10-29
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.enumeration;


/**
 * 聚划算平台类型
 * @version
 * @author hanqi
 * @data 2014-10-29 下午8:06:50
 */
public enum OptionType {
    /**
     * 今日团，品牌团， 聚名品
     */
    Today("1001"), Brand("1300"), JuMingPin("1013");

    private String platformId;

    OptionType(String id) {
        platformId = id;
    };

    public String getplatformId() {
        return platformId;
    }
}

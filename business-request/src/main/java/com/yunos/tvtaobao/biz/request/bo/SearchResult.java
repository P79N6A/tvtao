/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.bo
 * FILE NAME: SearchResult.java
 * CREATED TIME: 2014年10月23日
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2014年10月23日 下午8:11:49
 */
public class SearchResult implements Serializable {

    private static final long serialVersionUID = -76963708439296308L;

    // 返回的关键字
    private String keywords;
    // 关键字对应的商品权重
    private Long weight;

    public void setKeyword(String keyword) {
        this.keywords = keyword;
    }

    public String getKeyword() {
        return this.keywords;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getWeight() {
        return this.weight;
    }

    @Override
    public String toString() {
        return "keyword = " + keywords + ", weight = " + weight;
    }
}

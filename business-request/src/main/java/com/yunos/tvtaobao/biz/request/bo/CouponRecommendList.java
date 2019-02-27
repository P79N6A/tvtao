/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: CouponRecommendList.java
 * CREATED TIME: 2016年4月25日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import java.util.ArrayList;

public class CouponRecommendList {

    private String recommendTemplateKey;
    private String totalNum;
    private String recommendTitle;

    private ArrayList<CouponRecommend> list;

    /**
     * 推荐模板关键字
     * @return
     */
    public String getRecommendTemplateKey() {
        return recommendTemplateKey;
    }

    /**
     * 商品个数
     * @return
     */
    public String getTotalNum() {
        return totalNum;
    }

    /**
     * 推荐名称
     * @return
     */
    public String getRecommendTitle() {
        return recommendTitle;
    }

    /**
     * 推荐商品数据
     * @return
     */
    public ArrayList<CouponRecommend> getCouponRecommendList() {
        return list;
    }
}

/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


/**
 * Loading 广告
 * date: 2014-10-22 下午2:57:57 <br/>
 * @author 汉庭
 * @since JDK 1.6
 */
public class LoadingBo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 172222154019535038L;

    /** 图片地址 */
    private String imgUrl;

    /** 图片md5值 */
    private String md5;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;
    
    /** 停留时间 */
    private int               duration;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("imgUrl:" + this.getImgUrl() + ",");
        stringBuilder.append("md5:" + this.getMd5() + ",");
        stringBuilder.append("startTime" + this.getStartTime() + ",");
        stringBuilder.append("endTime" + this.getEndTime());
        return stringBuilder.toString();
    }
}

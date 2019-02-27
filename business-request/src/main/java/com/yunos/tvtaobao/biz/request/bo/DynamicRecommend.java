package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by wuhaoteng on 2018/8/7.
 * 热门推荐model
 */

public class DynamicRecommend {
    //0：无效  1：有效
    private String status;
    private String imgUrl;
    private String link;

    public boolean isValid() {
        if("0".equals(status)){
            return false;
        }else if("1".equals(status)){
            return true;
        }
        return false;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

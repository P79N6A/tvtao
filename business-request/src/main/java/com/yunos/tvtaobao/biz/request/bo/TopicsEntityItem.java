package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class TopicsEntityItem implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 3036583815162246231L;
    private String name;
    private String img;
    private String tms_url;
    private String URI;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTms_url() {
        return tms_url;
    }

    public void setTms_url(String tms_url) {
        this.tms_url = tms_url;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String uRI) {
        URI = uRI;
    }

}

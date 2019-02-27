package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class TopicsEntityLayout implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -4174664940715806550L;
    private String bg_color;
    private int    style;
    private int    border;

    public String getBg_color() {
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public int getBorder() {
        return border;
    }

    public void setBorder(int border) {
        this.border = border;
    }

}

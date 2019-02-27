package com.yunos.tvtaobao.biz.request.bo.resource.entrances;

/**
 * Created by huangdaju on 16/9/19.
 */
public class Coupon {
    private String icon;
    private String text;
    private String link;
    private String linkText;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }


    @Override
    public String toString() {
        return "Coupon{" +
                "icon='" + icon + '\'' +
                ", text='" + text + '\'' +
                ", link='" + link + '\'' +
                ", linkText='" + linkText + '\'' +
                '}';
    }
}

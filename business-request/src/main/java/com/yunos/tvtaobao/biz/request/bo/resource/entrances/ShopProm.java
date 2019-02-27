package com.yunos.tvtaobao.biz.request.bo.resource.entrances;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huangdaju on 16/10/19.
 */
public class ShopProm implements Serializable {
    private String activityId;
    private String iconText;
    private String title;
    private String period;
    private List<String> contentArrau;
    private String type;
    private String content;
    private String actionUrl;
    private int tag;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getIconText() {
        return iconText;
    }

    public void setIconText(String iconText) {
        this.iconText = iconText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<String> getContentArrau() {
        return contentArrau;
    }

    public void setContentArrau(List<String> contentArrau) {
        this.contentArrau = contentArrau;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public int getTag() {
        return tag;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
        if (!TextUtils.isEmpty(actionUrl)){
            if (actionUrl.contains("wh_tag=148226")){
                tag = 2;
            } else if (actionUrl.contains("wh_tag=148162")){
                tag = 3;
            } else if (actionUrl.contains("wh_tag=148098")){
                tag = 4;
            }
        }
    }

    @Override
    public String toString() {
        return "ShopProm{" +
                "activityId='" + activityId + '\'' +
                ", iconText='" + iconText + '\'' +
                ", title='" + title + '\'' +
                ", period='" + period + '\'' +
                ", contentArrau=" + contentArrau +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", actionUrl='" + actionUrl + '\'' +
                ", tag=" + tag +
                '}';
    }
}

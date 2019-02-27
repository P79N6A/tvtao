package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by libin on 16/11/2.
 */

public class UrlJumpBean implements Serializable{
    /**
     * words : ["收藏"]
     * uri : tvtaobao//home?app=taobaosdk&module=collects
     * weight : 1
     * since_v : 2100400506
     */

    private List<DataBean> data;
    private MetaBean meta;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public static class DataBean implements Comparable<DataBean>,Serializable{
        private String uri;
        private int weight;
        private int since_v;
        private String type;
        private List<String> words;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getSince_v() {
            return since_v;
        }

        public void setSince_v(int since_v) {
            this.since_v = since_v;
        }

        public List<String> getWords() {
            return words;
        }

        public void setWords(List<String> words) {
            this.words = words;
        }

        @Override
        public int compareTo(DataBean another) {
            return this.getWeight()-another.getWeight();
        }

    }

    public static class MetaBean {
        private String notify_title;
        private List<String> notification;

        public String getNotify_title() {
            return notify_title;
        }

        public void setNotify_title(String notify_title) {
            this.notify_title = notify_title;
        }

        public List<String> getNotification() {
            return notification;
        }

        public void setNotification(List<String> notification) {
            this.notification = notification;
        }
    }
}

package com.yunos.tvtaobao.zhuanti.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjiajuan on 17/4/20.
 */

public class TvBuyItems implements Serializable {

    public List<TvBuyItem> data;


    public List<TvBuyItem> getData() {
        return data;
    }

    public void setData(List<TvBuyItem> data) {
        this.data = data;
    }

    public static class TvBuyItem{
        private String itemId;
        private String video;
        private String name;
        private String cover;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }
    }


}

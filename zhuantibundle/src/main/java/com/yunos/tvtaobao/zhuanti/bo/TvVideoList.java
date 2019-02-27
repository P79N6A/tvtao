package com.yunos.tvtaobao.zhuanti.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjiajuan on 17/6/20.
 */

public class TvVideoList implements Serializable {

    private String offset;
    public List<VideoDoItem> videoDOList;

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public List<VideoDoItem> getVideoDOList() {
        return videoDOList;
    }

    public void setVideoDOList(List<VideoDoItem> videoDOList) {
        this.videoDOList = videoDOList;
    }

    public static class VideoDoItem{
        private String id;
        private String playUrl;
        private String cover;
        private String title;

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

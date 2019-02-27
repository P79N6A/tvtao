package com.yunos.tvtaobao.zhuanti.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjiajuan on 17/6/20.
 */

public class TvVideoDetail implements Serializable {
    private String id;
    private String playUrl;
    private String cover;
    private String title;
    private  UserDO userDO;
    private InteractionDO interactionDO;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public UserDO getUserDO() {
        return userDO;
    }

    public void setUserDO(UserDO userDO) {
        this.userDO = userDO;
    }

    public InteractionDO getInteractionDO() {
        return interactionDO;
    }

    public void setInteractionDO(InteractionDO interactionDO) {
        this.interactionDO = interactionDO;
    }

    public  static class UserDO{
    private String id;
    private String darenNick;
    private String darenPortraitUrl;
        private String followType;

        public String getDarenNick() {
            return darenNick;
        }

        public void setDarenNick(String darenNick) {
            this.darenNick = darenNick;
        }

        public String getDarenPortraitUrl() {
            return darenPortraitUrl;
        }

        public void setDarenPortraitUrl(String darenPortraitUrl) {
            this.darenPortraitUrl = darenPortraitUrl;
        }

        public String getFollowType() {
            return followType;
        }

        public void setFollowType(String followType) {
            this.followType = followType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
    public  static  class InteractionDO{
        private String id;
        private String title;
        private String cover;
        private List<TimeLines> timelines;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public List<TimeLines> getTimelines() {
            return timelines;
        }

        public void setTimelines(List<TimeLines> timelines) {
            this.timelines = timelines;
        }

        public  static class TimeLines{
           private String type;
           private String startTime;
            private String endTime;
            private String tid;

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
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

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }


    }
}

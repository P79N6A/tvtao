package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by pan on 16/9/29.
 */

public class TMallLiveCommentBean {
    private ModelBean model;

    public ModelBean getModel() {
        return model;
    }

    public void setModel(ModelBean model) {
        this.model = model;
    }

    public static class ModelBean {
        private String endId;
        private String endTime;
        private String startId;
        private String startTime;
        /**
         * author : {"avatar":"//img.alicdn.com/sns_logo/TB1aIXYIXXXXXa6XVXXApNeJVXX-360-360.png","displayName":"苏**8","userId":"2341287442"}
         * commentId : 17300226
         * dislikeCount : 0
         * disliked : false
         * floor : 347
         * gmtCreate : 1475094297000
         * images : []
         * likeCount : 0
         * liked : false
         * self : false
         * source : native
         * status : 1
         * subjectId : 81948
         * text : 红河谷
         * type : 1
         */

        private List<DataBean> data;

        public String getEndId() {
            return endId;
        }

        public void setEndId(String endId) {
            this.endId = endId;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getStartId() {
            return startId;
        }

        public void setStartId(String startId) {
            this.startId = startId;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            @Override
            public boolean equals(Object o) {
                if (o instanceof DataBean) {
                    DataBean dataBean = (DataBean) o;
                    return this.commentId.equals(dataBean.getCommentId());
                }
                return super.equals(o);
            }

            /**
             * avatar : //img.alicdn.com/sns_logo/TB1aIXYIXXXXXa6XVXXApNeJVXX-360-360.png
             * displayName : 苏**8
             * userId : 2341287442
             */

            private AuthorBean author;
            private String commentId;
            private String dislikeCount;
            private String disliked;
            private String floor;
            private String gmtCreate;
            private String likeCount;
            private String liked;
            private String self;
            private String source;
            private String status;
            private String subjectId;
            private String text;
            private String type;
            private List<?> images;

            public AuthorBean getAuthor() {
                return author;
            }

            public void setAuthor(AuthorBean author) {
                this.author = author;
            }

            public String getCommentId() {
                return commentId;
            }

            public void setCommentId(String commentId) {
                this.commentId = commentId;
            }

            public String getDislikeCount() {
                return dislikeCount;
            }

            public void setDislikeCount(String dislikeCount) {
                this.dislikeCount = dislikeCount;
            }

            public String getDisliked() {
                return disliked;
            }

            public void setDisliked(String disliked) {
                this.disliked = disliked;
            }

            public String getFloor() {
                return floor;
            }

            public void setFloor(String floor) {
                this.floor = floor;
            }

            public String getGmtCreate() {
                return gmtCreate;
            }

            public void setGmtCreate(String gmtCreate) {
                this.gmtCreate = gmtCreate;
            }

            public String getLikeCount() {
                return likeCount;
            }

            public void setLikeCount(String likeCount) {
                this.likeCount = likeCount;
            }

            public String getLiked() {
                return liked;
            }

            public void setLiked(String liked) {
                this.liked = liked;
            }

            public String getSelf() {
                return self;
            }

            public void setSelf(String self) {
                this.self = self;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getSubjectId() {
                return subjectId;
            }

            public void setSubjectId(String subjectId) {
                this.subjectId = subjectId;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<?> getImages() {
                return images;
            }

            public void setImages(List<?> images) {
                this.images = images;
            }

            public static class AuthorBean {
                private String avatar;
                private String displayName;
                private String userId;

                public String getAvatar() {
                    return avatar;
                }

                public void setAvatar(String avatar) {
                    this.avatar = avatar;
                }

                public String getDisplayName() {
                    return displayName;
                }

                public void setDisplayName(String displayName) {
                    this.displayName = displayName;
                }

                public String getUserId() {
                    return userId;
                }

                public void setUserId(String userId) {
                    this.userId = userId;
                }
            }
        }
    }
}

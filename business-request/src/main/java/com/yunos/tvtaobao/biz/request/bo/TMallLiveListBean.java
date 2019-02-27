package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by pan on 16/9/28.
 */

public class TMallLiveListBean {

    /**
     * status : 0
     * success : true
     * msg : 成功
     * data : [{"img_url":"https://img.alicdn.com/imgextra/i4/2939171834/TB2IjXTXgUc61BjSZFvXXXKfVXa_!!0-tmallfun.jpg","id":"57f79acdd4c623b55326a80c","end_time":"2016-10-26 11:30:00","stream_url":"rtmp://video-center.alivecdn.com/tvmall-live/57f79acdd4c623b55326a80c?vhost=live.zhiping.tv","name":"直播测试1","start_time":"2016-08-26 11:30:00","live_id":"5f25d9d9-689f-4676-a392-efad52e8b946"}]
     */

    private int status;
    private boolean success;
    private String msg;
    /**
     * img_url : https://img.alicdn.com/imgextra/i4/2939171834/TB2IjXTXgUc61BjSZFvXXXKfVXa_!!0-tmallfun.jpg
     * id : 57f79acdd4c623b55326a80c
     * end_time : 2016-10-26 11:30:00
     * stream_url : rtmp://video-center.alivecdn.com/tvmall-live/57f79acdd4c623b55326a80c?vhost=live.zhiping.tv
     * name : 直播测试1
     * start_time : 2016-08-26 11:30:00
     * live_id : 5f25d9d9-689f-4676-a392-efad52e8b946
     */

    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String img_url;
        private String id;
        private String end_time;
        private String stream_url;
        private String name;
        private String start_time;
        private String live_id;

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getStream_url() {
            return stream_url;
        }

        public void setStream_url(String stream_url) {
            this.stream_url = stream_url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getLive_id() {
            return live_id;
        }

        public void setLive_id(String live_id) {
            this.live_id = live_id;
        }
    }
}

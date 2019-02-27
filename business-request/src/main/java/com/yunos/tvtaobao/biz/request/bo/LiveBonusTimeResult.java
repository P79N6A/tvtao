package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by linmu on 2018/8/31.
 */

public class LiveBonusTimeResult {
    private String currentTime;
    private List<LiveBonusTimeItem> list;

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public List<LiveBonusTimeItem> getList() {
        return list;
    }

    public void setList(List<LiveBonusTimeItem> list) {
        this.list = list;
    }
}

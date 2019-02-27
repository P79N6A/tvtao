package com.yunos.tvtaobao.biz.request.bo;


/**
 * Created by libin on 16/9/17.
 * @延迟直播列表bean，Modify by wuhaoteng on 2018/8/24
 */

public class TBaoLiveListBean {
    private String accountId;
    private String accountNick;
    private String coverImg;
    private String coverImg169;
    private String headImg;
    private String inputStreamUrl;
    private String landScape;
    private String liveChannelId;
    private String liveId;
    private String location;
    //直播流状态 直播中：0 ，直播结束：1 ， 直播流中断：2  ，直播未开始：3
    private String status;
    private String title;
    //直播来源 淘宝直播：TAOBAO 天猫直播：TMALL  自定义直播：CUSTOM
    private String source;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNick() {
        return accountNick;
    }

    public void setAccountNick(String accountNick) {
        this.accountNick = accountNick;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getCoverImg169() {
        return coverImg169;
    }

    public void setCoverImg169(String coverImg169) {
        this.coverImg169 = coverImg169;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getInputStreamUrl() {
        return inputStreamUrl;
    }

    public void setInputStreamUrl(String inputStreamUrl) {
        this.inputStreamUrl = inputStreamUrl;
    }

    public String getLandScape() {
        return landScape;
    }

    public void setLandScape(String landScape) {
        this.landScape = landScape;
    }

    public String getLiveChannelId() {
        return liveChannelId;
    }

    public void setLiveChannelId(String liveChannelId) {
        this.liveChannelId = liveChannelId;
    }

    public String getLiveId() {
        return liveId;
    }

    public void setLiveId(String liveId) {
        this.liveId = liveId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

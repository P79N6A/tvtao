package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by chenjiajuan on 17/5/22.
 */

public class WeitaoFollowBean {
    private String accountId;
    private boolean dynamic;
    private boolean follow;
    private boolean quiet;

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }
}

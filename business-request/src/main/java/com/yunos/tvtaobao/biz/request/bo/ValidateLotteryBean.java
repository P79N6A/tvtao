package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by pan on 16/10/19.
 */

public class ValidateLotteryBean {

    /**
     * capacity : true
     * usercount : 15
     * uuidCount : 0
     */

    private boolean capacity;
    private int usercount;
    private int uuidCount;

    public boolean isCapacity() {
        return capacity;
    }

    public void setCapacity(boolean capacity) {
        this.capacity = capacity;
    }

    public int getUsercount() {
        return usercount;
    }

    public void setUsercount(int usercount) {
        this.usercount = usercount;
    }

    public int getUuidCount() {
        return uuidCount;
    }

    public void setUuidCount(int uuidCount) {
        this.uuidCount = uuidCount;
    }
}

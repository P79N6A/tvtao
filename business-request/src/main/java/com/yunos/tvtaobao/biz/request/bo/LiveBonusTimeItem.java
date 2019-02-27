package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by linmu on 2018/8/27.
 */

public class LiveBonusTimeItem {
    private String drawBeginAt;
    private String drawEndAt;
    private String id;
    private String ruleType;
    private String safeCode;
    private String showAt;
    private String timeType;
    public void setDrawBeginAt(String drawBeginAt) {
        this.drawBeginAt = drawBeginAt;
    }
    public String getDrawBeginAt() {
        return drawBeginAt;
    }

    public void setDrawEndAt(String drawEndAt) {
        this.drawEndAt = drawEndAt;
    }
    public String getDrawEndAt() {
        return drawEndAt;
    }


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }
    public String getRuleType() {
        return ruleType;
    }

    public void setSafeCode(String safeCode) {
        this.safeCode = safeCode;
    }
    public String getSafeCode() {
        return safeCode;
    }

    public void setShowAt(String showAt) {
        this.showAt = showAt;
    }
    public String getShowAt() {
        return showAt;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }
    public String getTimeType() {
        return timeType;
    }

}

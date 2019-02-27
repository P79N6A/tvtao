package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by pan on 16/10/12.
 */

public class BonusBean {
    /**
     * retCode : 0
     * awardType : 1
     * awardFace : 1
     * awardEffectDate : 2016-10-12 00:00:00
     * awardDisEffectDate : 2016-10-19 00:00:00
     */
    private String retCode;
    private String awardType;
    private String awardFace;
    private String awardEffectDate;
    private String awardDisEffectDate;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getAwardType() {
        return awardType;
    }

    public void setAwardType(String awardType) {
        this.awardType = awardType;
    }

    public String getAwardFace() {
        return awardFace;
    }

    public void setAwardFace(String awardFace) {
        this.awardFace = awardFace;
    }

    public String getAwardEffectDate() {
        return awardEffectDate;
    }

    public void setAwardEffectDate(String awardEffectDate) {
        this.awardEffectDate = awardEffectDate;
    }

    public String getAwardDisEffectDate() {
        return awardDisEffectDate;
    }

    public void setAwardDisEffectDate(String awardDisEffectDate) {
        this.awardDisEffectDate = awardDisEffectDate;
    }
}

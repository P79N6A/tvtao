package com.yunos.tvtaobao.biz.request.item;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/25
 * @Desc：天降红包样式
 */
public class TakeOutCouponStyle {
    //氛围图
    private String bgPic;
    //背景色值
    private String bgColor;
    //按钮图
    private String buttonPic;
    //按钮跳转链接
    private String buttonUrl;
    //红包参数
    private String couponFlag;
    //安全码
    private String safeCode;

    public String getBgPic() {
        return bgPic;
    }

    public void setBgPic(String bgPic) {
        this.bgPic = bgPic;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getButtonPic() {
        return buttonPic;
    }

    public void setButtonPic(String buttonPic) {
        this.buttonPic = buttonPic;
    }

    public String getButtonUrl() {
        return buttonUrl;
    }

    public void setButtonUrl(String buttonUrl) {
        this.buttonUrl = buttonUrl;
    }

    public String getCouponFlag() {
        return couponFlag;
    }

    public void setCouponFlag(String couponFlag) {
        this.couponFlag = couponFlag;
    }

    public String getSafeCode() {
        return safeCode;
    }

    public void setSafeCode(String safeCode) {
        this.safeCode = safeCode;
    }
}

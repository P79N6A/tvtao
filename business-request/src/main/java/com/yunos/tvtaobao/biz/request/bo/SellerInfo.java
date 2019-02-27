package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class SellerInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8507287277753520352L;

    private static final String TAG = "SellerInfo";
    //    店铺id     id      String      N   data={"id": "45982368"}
    //    店铺标题     title   String      N   "title": "【你好卡农】"
    //    是否是天猫店铺      isMall      String  true or false   N   data={"isMall": "false"}  表示店铺不是天猫店铺   为true表示是天猫店铺
    //    卖家Nick   nick    String      N   data={"nick": "zy123c"}
    //    卖家Id     sellerId    String      N   data={"sellerId": "21323"}
    //    无线店招     wapIcon     String      后期不再维护，请尽量不要使用  Y   图片url地址，data={"wapIcon"," http://img05.daily.taobaocdn.net/bao/uploaded/i3/T1aqlcXeXjXXb1upjX.jpg "}
    //    店铺图片     picUrl  String          Y   图片url地址，data={"picUrl"," test "}
    //    信用值  rateSum     String      N   "rankSum": "5",
    //    信用级别     rankType    String  信用级别，1：心级，2：钻级，3：冠级 4：金冠  天猫店铺不区分信用级别   N   "rankType": "1",
    //    信用级数     rankNum     String      1-5     N     "rankNum": "1"
    //    所在省  prov    String      Y   data={"prov": "江西"}
    //    所在市  city    String      Y   data={"city": "南昌"}
    //    客服电话     phone   String      Y   "phone": "010-81921555"
    //    分机号      phoneExt    String  可以为空    Y   "phoneExt": "111117"
    //    收藏人气     collectorCount  String      N   "collectorCount": "15"
    //    开店时间     starts  String      N   2011-04-09 18:56:14
    //    在售数量     productCount    String      N   246

    private String              id;
    private String              title;
    private String              isMall;
    private String              nick;
    private String              sellerId;
    private String              wapIcon;
    private String              picUrl;
    private String              rateSum;
    private String              rankType;
    private String              rankNum;
    private String              prov;
    private String              city;
    private String              phone;
    private String              phoneExt;
    private String              collectorCount;
    private String              starts;
    private String              productCount;
    private com.yunos.tvtaobao.biz.request.bo.shopDSRScore shopDSRScore;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsMall() {
        return this.isMall;
    }

    public void setIsMall(String isMall) {
        this.isMall = isMall;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSellerId() {
        return this.sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getWapIcon() {
        return this.wapIcon;
    }

    public void setWapIcon(String wapIcon) {
        this.wapIcon = wapIcon;
    }

    public String getPicUrl() {
        return this.picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getRateSum() {
        return this.rateSum;
    }

    public void setRateSum(String rateSum) {
        this.rateSum = rateSum;
    }

    public String getRankType() {
        return this.rankType;
    }

    public void setRankType(String rankType) {
        this.rankType = rankType;
    }

    public String getRankNum() {
        return this.rankNum;
    }

    public void setRankNum(String rankNum) {
        this.rankNum = rankNum;
    }

    public String getProv() {
        return this.prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneExt() {
        return this.phoneExt;
    }

    public void setPhoneExt(String phoneExt) {
        this.phoneExt = phoneExt;
    }

    public String getCollectorCount() {
        return this.collectorCount;
    }

    public void setCollectorCount(String collectorCount) {
        this.collectorCount = collectorCount;
    }

    public String getStarts() {
        return this.starts;
    }

    public void setStarts(String starts) {
        this.starts = starts;
    }

    public String getProductCount() {
        return this.productCount;
    }

    public void setProductCount(String productCount) {
        this.productCount = productCount;
    }

    public com.yunos.tvtaobao.biz.request.bo.shopDSRScore getShopDSRScore() {
        return this.shopDSRScore;
    }

    public void setShopDSRScore(com.yunos.tvtaobao.biz.request.bo.shopDSRScore shopDSRScore) {
        this.shopDSRScore = shopDSRScore;
    }

    @Override
    public String toString() {
        return "SellerInfo [id=" + id + ", title=" + title + ", isMall=" + isMall + ", nick=" + nick + ", sellerId=" + sellerId + ", wapIcon=" + wapIcon + ", picUrl=" + picUrl + ", rateSum=" + rateSum + ", rankType=" + rankType + ", rankNum=" + rankNum + ", prov=" + prov + ", city=" + city
                + ", phone=" + phone + ", phoneExt=" + phoneExt + ", collectorCount=" + collectorCount + ", starts=" + starts + ", productCount=" + productCount + ", shopDSRScore=" + shopDSRScore + "]";
    }

}

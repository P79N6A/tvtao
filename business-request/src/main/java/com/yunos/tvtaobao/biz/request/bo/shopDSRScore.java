package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class shopDSRScore  implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -260168997921881849L;
    private static final String TAG = "shopDSRScore";
    //    DSR信息    shopDSRScore    shopDSRScore    详细信息如下  Y  
    //    描述相符     shopDSRScore.merchandisScore    String  范围 0-5  N   4.9
    //    描述相符与行业平均分的差距    shopDSRScore. mg    String  正数代表高于，负数代表低于   N  
    //    负值为低于行业标准的百分值，正值为高于行业标准的百分值，如-2.57 表示低于行业标准2.57%，
    //
    //    2.09表示高于行业标准2.09%，正负0.5之间判断为持平（不包括正负0.5）
    //
    //    服务态度     shopDSRScore. serviceScore  String  范围 0-5  N   4.9
    //    服务态度与行业平均分的差距    shopDSRScore. sg    String      正数代表高于，负数代表低于   N   负值为低于行业标准的百分值，正值为高于行业标准的百分值，如-2.57 表示低于行业标准2.57%，
    //    2.09表示高于行业标准2.09%，正负0.5之间判断为持平（不包括正负0.5）
    //
    //    发货速度     shopDSRScore.consignmentScore   String  范围 0-5      N   4.9
    //    发货速度与行业平均分的差距    shopDSRScore. cg    String      正数代表高于，负数代表低于   N   负值为低于行业标准的百分值，正值为高于行业标准的百分值，如-2.57 表示低于行业标准2.57%，
    //    2.09表示高于行业标准2.09%，正负0.5之间判断为持平（不包括正负0.5）
    //
    //    好评率  shopDSRScore.sellerGoodPercent  String      N   62.50%
    private String              merchandisScore;
    private double              mg;
    private String              serviceScore;
    private double              sg;
    private String              consignmentScore;
    private double              cg;
    private String              sellerGoodPercent;

    public String getMerchandisScore() {
        return this.merchandisScore;
    }

    public void setMerchandisScore(String merchandisScore) {
        this.merchandisScore = merchandisScore;
    }

    public double getMg() {
        return this.mg;
    }

    public void setMg(double mg) {
        this.mg = mg;
    }

    public String getServiceScore() {
        return this.serviceScore;
    }

    public void setServiceScore(String serviceScore) {
        this.serviceScore = serviceScore;
    }

    public double getSg() {
        return this.sg;
    }

    public void setSg(double sg) {
        this.sg = sg;
    }

    public String getConsignmentScore() {
        return this.consignmentScore;
    }

    public void setConsignmentScore(String consignmentScore) {
        this.consignmentScore = consignmentScore;
    }

    public double getCg() {
        return this.cg;
    }

    public void setCg(double cg) {
        this.cg = cg;
    }

    public String getSellerGoodPercent() {
        return this.sellerGoodPercent;
    }

    public void setSellerGoodPercent(String sellerGoodPercent) {
        this.sellerGoodPercent = sellerGoodPercent;
    }

    @Override
    public String toString() {
        return "shopDSRScore [merchandisScore=" + merchandisScore + ", mg=" + mg + ", serviceScore=" + serviceScore + ", sg=" + sg + ", consignmentScore=" + consignmentScore + ", cg=" + cg + ", sellerGoodPercent=" + sellerGoodPercent + "]";
    }

}

package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 外卖订单的订单信息.
 */
public class TakeOutOrderInfoDetails4FeeDetail implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String feeContent;
    private int fee;
    private boolean liangpiao;

    public String getFeeContent() {
        return feeContent;
    }

    public void setFeeContent(String feeContent) {
        this.feeContent = feeContent;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public boolean isLiangpiao() {
        return liangpiao;
    }

    public void setLiangpiao(boolean liangpiao) {
        this.liangpiao = liangpiao;
    }

    public static TakeOutOrderInfoDetails4FeeDetail resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoDetails4FeeDetail details4FeeDetail = new TakeOutOrderInfoDetails4FeeDetail();

        if (obj != null) {
            details4FeeDetail.setFee(obj.optInt("fee", 0));
            details4FeeDetail.setFeeContent(obj.optString("feeContent"));
            details4FeeDetail.setLiangpiao(obj.optBoolean("liangpiao", false));
        }

        return details4FeeDetail;
    }
}

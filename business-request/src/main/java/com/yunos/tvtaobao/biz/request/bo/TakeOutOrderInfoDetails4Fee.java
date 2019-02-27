package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 外卖订单的订单信息.
 */
public class TakeOutOrderInfoDetails4Fee implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private int actualPaidFee;
    private int hongbao;
    private int postFee;
    private int totalPrice;

    private ArrayList<TakeOutOrderInfoDetails4FeeDetail> feeDetails;

    public ArrayList<TakeOutOrderInfoDetails4FeeDetail> getFeeDetails() {
        return feeDetails;
    }

    public void setFeeDetails(ArrayList<TakeOutOrderInfoDetails4FeeDetail> feeDetails) {
        this.feeDetails = feeDetails;
    }

    public int getActualPaidFee() {
        return actualPaidFee;
    }

    public void setActualPaidFee(int actualPaidFee) {
        this.actualPaidFee = actualPaidFee;
    }

    public int getHongbao() {
        return hongbao;
    }

    public void setHongbao(int hongbao) {
        this.hongbao = hongbao;
    }

    public int getPostFee() {
        return postFee;
    }

    public void setPostFee(int postFee) {
        this.postFee = postFee;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public static TakeOutOrderInfoDetails4Fee resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoDetails4Fee infoDetails4Fee = new TakeOutOrderInfoDetails4Fee();

        if (obj != null) {
            infoDetails4Fee.setTotalPrice(obj.optInt("totalPrice", 0));
            infoDetails4Fee.setHongbao(obj.optInt("hongbao", 0));
            infoDetails4Fee.setActualPaidFee(obj.optInt("actualPaidFee", 0));
            infoDetails4Fee.setPostFee(obj.optInt("postFee", 0));

            if (!obj.isNull("extraFeeDetail")) {
                JSONArray array = obj.getJSONArray("extraFeeDetail");
                ArrayList<TakeOutOrderInfoDetails4FeeDetail> temp = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    temp.add(TakeOutOrderInfoDetails4FeeDetail.resolverFromMtop(array.getJSONObject(i)));
                }
                infoDetails4Fee.setFeeDetails(temp);
            }
        }

        return infoDetails4Fee;
    }
}

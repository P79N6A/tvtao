package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class TakeOutOrderListData implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private int total;
    private String buyerId;
    private int errorCode;
    private int nextPageNo;
    private ArrayList<TakeOutOrderInfoBase> orderInfoBaseList;

    public ArrayList<TakeOutOrderInfoBase> getOrderInfoBaseList() {
        return orderInfoBaseList;
    }

    public void setOrderInfoBaseList(ArrayList<TakeOutOrderInfoBase> orderInfoBaseList) {
        this.orderInfoBaseList = orderInfoBaseList;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getNextPageNo() {
        return nextPageNo;
    }

    public void setNextPageNo(int nextPageNo) {
        this.nextPageNo = nextPageNo;
    }

    /**
     * 得到订单列表数量
     */
    public int getTotal() {
        return total;
    }

    /**
     * 设置订单类别数量
     */
    public void setTotal(int total) {
        this.total = total;
    }

    public static TakeOutOrderListData resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderListData orderListData = new TakeOutOrderListData();
        orderListData.setTotal(0);

        if (obj != null) {
            orderListData.setTotal(obj.optInt("totalNumber", 0));
            orderListData.setBuyerId(obj.optString("buyerId"));
            orderListData.setErrorCode(obj.optInt("errorCode", 0));
            orderListData.setNextPageNo(obj.optInt("nextPageNo", 0));

            if (!obj.isNull("orders")) {
                JSONArray array = obj.getJSONArray("orders");
                ArrayList<TakeOutOrderInfoBase> temp = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    temp.add(TakeOutOrderInfoBase.resolverFromMtop(array.getJSONObject(i)));
                }
                orderListData.setOrderInfoBaseList(temp);
            }
        }

        return orderListData;
    }
}

package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 外卖订单的详细订单信息.（订单详情页用）
 */
public class TakeOutOrderInfoDetails implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String appointOrderId;
    private String tbMainOrderId;
    private String buyerMessage;
    private String deliveryTime;
    private int deliveryType;
    private String expectDeliveryTime;
    private boolean hasDeliver;
    private String invoice;
    private boolean isFromOnlinePay;
    private boolean onlinePay;
    private String processNodeId;
    private String shopStatus;
    private String status;
    private String statusDesc;
    private String statusInterval;
    private String outOrderId;

    private ArrayList<TakeOutOrderProductInfoBase> productInfoList;
    private TakeOutOrderInfoDetails4Fee orderInfoDetails4Fee;
    private TakeOutOrderInfoDetails4OnTimeInfo onTimeInfo;
    private TakeOutOrderInfoDetails4ContactInfo contactInfo;
    private TakeOutOrderInfoDetails4Address details4Address;
    private TakeOutOrderInfoDetails4TradeInfo details4Trade;
    private TakeOutOrderInfoDetails4StoreInfo details4StoreInfo;
    private ArrayList<TakeOutOrderInfoDetails4Process> processArrayList;

    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getExpectDeliveryTime() {
        return expectDeliveryTime;
    }

    public void setExpectDeliveryTime(String expectDeliveryTime) {
        this.expectDeliveryTime = expectDeliveryTime;
    }

    public boolean isHasDeliver() {
        return hasDeliver;
    }

    public void setHasDeliver(boolean hasDeliver) {
        this.hasDeliver = hasDeliver;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public boolean isOnlinePay() {
        return onlinePay;
    }

    public void setOnlinePay(boolean onlinePay) {
        this.onlinePay = onlinePay;
    }

    public boolean isFromOnlinePay() {
        return isFromOnlinePay;
    }

    public void setFromOnlinePay(boolean fromOnlinePay) {
        isFromOnlinePay = fromOnlinePay;
    }

    public String getProcessNodeId() {
        return processNodeId;
    }

    public void setProcessNodeId(String processNodeId) {
        this.processNodeId = processNodeId;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getStatusInterval() {
        return statusInterval;
    }

    public void setStatusInterval(String statusInterval) {
        this.statusInterval = statusInterval;
    }

    public TakeOutOrderInfoDetails4Address getDetails4Address() {
        return details4Address;
    }

    public void setDetails4Address(TakeOutOrderInfoDetails4Address details4Address) {
        this.details4Address = details4Address;
    }

    public ArrayList<TakeOutOrderInfoDetails4Process> getProcessArrayList() {
        return processArrayList;
    }

    public void setProcessArrayList(ArrayList<TakeOutOrderInfoDetails4Process> processArrayList) {
        this.processArrayList = processArrayList;
    }

    public TakeOutOrderInfoDetails4TradeInfo getDetails4Trade() {
        return details4Trade;
    }

    public void setDetails4Trade(TakeOutOrderInfoDetails4TradeInfo details4Trade) {
        this.details4Trade = details4Trade;
    }

    public TakeOutOrderInfoDetails4StoreInfo getDetails4StoreInfo() {
        return details4StoreInfo;
    }

    public void setDetails4StoreInfo(TakeOutOrderInfoDetails4StoreInfo details4StoreInfo) {
        this.details4StoreInfo = details4StoreInfo;
    }

    public TakeOutOrderInfoDetails4Fee getOrderInfoDetails4Fee() {
        return orderInfoDetails4Fee;
    }

    public void setOrderInfoDetails4Fee(TakeOutOrderInfoDetails4Fee orderInfoDetails4Fee) {
        this.orderInfoDetails4Fee = orderInfoDetails4Fee;
    }

    public TakeOutOrderInfoDetails4ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(TakeOutOrderInfoDetails4ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public TakeOutOrderInfoDetails4OnTimeInfo getOnTimeInfo() {
        return onTimeInfo;
    }

    public void setOnTimeInfo(TakeOutOrderInfoDetails4OnTimeInfo onTimeInfo) {
        this.onTimeInfo = onTimeInfo;
    }

    public ArrayList<TakeOutOrderProductInfoBase> getProductInfoList() {
        return productInfoList;
    }

    public void setProductInfoList(ArrayList<TakeOutOrderProductInfoBase> productInfoList) {
        this.productInfoList = productInfoList;
    }

    public String getAppointOrderId() {
        return appointOrderId;
    }

    public void setAppointOrderId(String appointOrderId) {
        this.appointOrderId = appointOrderId;
    }

    public String getTbMainOrderId() {
        return tbMainOrderId;
    }

    public void setTbMainOrderId(String tbMainOrderId) {
        this.tbMainOrderId = tbMainOrderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public void setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId;
    }

    public static TakeOutOrderInfoDetails resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoDetails orderInfoDetails = new TakeOutOrderInfoDetails();

        if (obj != null) {

            orderInfoDetails.setTbMainOrderId(obj.optString("tbMainOrderId"));
            orderInfoDetails.setStatus(obj.optString("status"));
            orderInfoDetails.setShopStatus(obj.optString("shopStatus"));
            orderInfoDetails.setOnlinePay(obj.optBoolean("onlinePay", false));
            orderInfoDetails.setFromOnlinePay(obj.optBoolean("isOnlinePay", false));
            orderInfoDetails.setHasDeliver(obj.optBoolean("hasDeliver", false));
            orderInfoDetails.setDeliveryType(obj.optInt("deliveryType", 0));
            orderInfoDetails.setStatusDesc(obj.optString("statusDesc"));
            orderInfoDetails.setStatusInterval(obj.optString("statusInterval"));
            orderInfoDetails.setProcessNodeId(obj.optString("processNodeId"));
            orderInfoDetails.setExpectDeliveryTime(obj.optString("expectDeliveryTime"));
            orderInfoDetails.setDeliveryTime(obj.optString("deliveryTime"));
            orderInfoDetails.setBuyerMessage(obj.optString("buyerMessage"));
            // TODO LYLDEBUG 发票信息如何处理.
            // orderInfoDetails.setStatus(obj.optString("invoice"));

            orderInfoDetails.setOutOrderId(obj.optString("outOrderId"));
            orderInfoDetails.setAppointOrderId(obj.optString("appointOrderId"));

            if (!obj.isNull("onTimeInfo")) {
                JSONObject timeInfo = obj.getJSONObject("onTimeInfo");
                orderInfoDetails.setOnTimeInfo(TakeOutOrderInfoDetails4OnTimeInfo.resolverFromMtop(timeInfo));
            }

            if (!obj.isNull("orderContactInfo")) {
                JSONObject contactInfo = obj.getJSONObject("orderContactInfo");
                orderInfoDetails.setContactInfo(TakeOutOrderInfoDetails4ContactInfo.resolverFromMtop(contactInfo));
            }

            if (!obj.isNull("orderFee")) {
                JSONObject feeInfo = obj.getJSONObject("orderFee");
                orderInfoDetails.setOrderInfoDetails4Fee(TakeOutOrderInfoDetails4Fee.resolverFromMtop(feeInfo));
            }

            if (!obj.isNull("serviceAddress")) {
                JSONObject address = obj.getJSONObject("serviceAddress");
                orderInfoDetails.setDetails4Address(TakeOutOrderInfoDetails4Address.resolverFromMtop(address));
            }

            if (!obj.isNull("tradeInfo")) {
                JSONObject tradeInfo = obj.getJSONObject("tradeInfo");
                orderInfoDetails.setDetails4Trade(TakeOutOrderInfoDetails4TradeInfo.resolverFromMtop(tradeInfo));
            }

            if (!obj.isNull("storeInfo")) {
                JSONObject storeInfo = obj.getJSONObject("storeInfo");
                orderInfoDetails.setDetails4StoreInfo(TakeOutOrderInfoDetails4StoreInfo.resolverFromMtop(storeInfo));
            }

            if (!obj.isNull("orderItems")) {
                JSONArray array = obj.getJSONArray("orderItems");
                ArrayList<TakeOutOrderProductInfoBase> temp = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    temp.add(TakeOutOrderProductInfoBase.resolverFromMtop(array.getJSONObject(i)));
                }
                orderInfoDetails.setProductInfoList(temp);
            }

            if (!obj.isNull("processInfo")) {
                JSONArray array = obj.getJSONArray("processInfo");
                ArrayList<TakeOutOrderInfoDetails4Process> temp = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    temp.add(TakeOutOrderInfoDetails4Process.resolverFromMtop(array.getJSONObject(i)));
                }
                orderInfoDetails.setProcessArrayList(temp);
            }
        }

        return orderInfoDetails;
    }
}

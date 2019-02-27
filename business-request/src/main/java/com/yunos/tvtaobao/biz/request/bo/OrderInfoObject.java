package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderInfoObject implements Serializable {

    /**
     * 
     */
    private static final long          serialVersionUID = 1960216151087042450L;

    private String                     bizOrderId;
    private String                     createTime;
    private ArrayList<String>          icon;
    private ArrayList<OrderCellObject> orderCell;
    private String                     orderStatus;
    private ArrayList<String>          payDesc;
    private String                     payOrderId;
    private ArrayList<String>          promotion;
    private String                     totalPrice;
    private String                     postFee;
    private String                     type;
    private String                     version;
    private String                     sendTime;
    private String                     confirmTime;
    private ArrayList<String>          orderMessage;
    private String                     orderStatusCode;

    public String getOrderStatusCode() {
        return orderStatusCode;
    }

    public void setOrderStatusCode(String orderStatusCode) {
        this.orderStatusCode = orderStatusCode;
    }

    /**
     * @return the postFee
     */
    public String getPostFee() {
        return postFee;
    }

    /**
     * @param postFee
     *            the postFee to set
     */
    public void setPostFee(String postFee) {
        this.postFee = postFee;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public ArrayList<String> getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(ArrayList<String> orderMessage) {
        this.orderMessage = orderMessage;
    }

    public ArrayList<OrderCellObject> getOrderCell() {
        return orderCell;
    }

    public void setOrderCell(ArrayList<OrderCellObject> orderCell) {
        this.orderCell = orderCell;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public ArrayList<String> getPayDesc() {
        return payDesc;
    }

    public void setPayDesc(ArrayList<String> payDesc) {
        this.payDesc = payDesc;
    }

    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public ArrayList<String> getPromotion() {
        return promotion;
    }

    public void setPromotion(ArrayList<String> promotion) {
        this.promotion = promotion;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the bizOrderId
     */
    public String getBizOrderId() {
        return bizOrderId;
    }

    /**
     * @param bizOrderId
     *            the bizOrderId to set
     */
    public void setBizOrderId(String bizOrderId) {
        this.bizOrderId = bizOrderId;
    }

    /**
     * @return the createTime
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     *            the createTime to set
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the icon
     */
    public ArrayList<String> getIcon() {
        return icon;
    }

    /**
     * @param icon
     *            the icon to set
     */
    public void setIcon(ArrayList<String> icon) {
        this.icon = icon;
    }

    public static OrderInfoObject resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        OrderInfoObject orderInfo = new OrderInfoObject();

        if (!obj.isNull("bizOrderId")) {
            orderInfo.setBizOrderId(obj.getString("bizOrderId"));
        }

        if (!obj.isNull("createTime")) {
            orderInfo.setCreateTime(obj.getString("createTime"));
        }

        if (!obj.isNull("orderStatus")) {
            orderInfo.setOrderStatus(obj.getString("orderStatus"));
        }

        if (!obj.isNull("orderStatusCode")) {
            orderInfo.setOrderStatusCode(obj.getString("orderStatusCode"));
        }

        if (!obj.isNull("payOrderId")) {
            orderInfo.setPayOrderId(obj.getString("payOrderId"));
        }

        if (!obj.isNull("totalPrice")) {
            orderInfo.setTotalPrice(obj.getString("totalPrice"));
        }

        if (!obj.isNull("postFee")) {
            orderInfo.setPostFee(obj.getString("postFee"));
        }

        if (!obj.isNull("type")) {
            orderInfo.setType(obj.getString("type"));
        }

        if (!obj.isNull("version")) {
            orderInfo.setVersion(obj.getString("version"));
        }

        if (!obj.isNull("sendTime")) {
            orderInfo.setSendTime(obj.getString("sendTime"));
        }

        if (!obj.isNull("confirmTime")) {
            orderInfo.setConfirmTime(obj.getString("confirmTime"));
        }

        if (!obj.isNull("icon")) {
            JSONArray array = obj.getJSONArray("icon");
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                temp.add(array.getString(i));
            }
            orderInfo.setIcon(temp);
        }

        if (!obj.isNull("orderCell")) {
            JSONArray array = obj.getJSONArray("orderCell");
            ArrayList<OrderCellObject> temp = new ArrayList<OrderCellObject>();
            for (int i = 0; i < array.length(); i++) {
                temp.add(OrderCellObject.resolveFromMTOP(array.getJSONObject(i)));
            }
            orderInfo.setOrderCell(temp);
        }

        if (!obj.isNull("payDesc")) {
            JSONArray array = obj.getJSONArray("payDesc");
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                temp.add(array.getString(i));
            }
            orderInfo.setPayDesc(temp);
        }

        if (!obj.isNull("promotion")) {
            JSONArray array = obj.getJSONArray("promotion");
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                temp.add(array.getString(i));
            }
            orderInfo.setPromotion(temp);
        }

        if (!obj.isNull("orderMessage")) {
            JSONArray array = obj.getJSONArray("orderMessage");
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                temp.add(array.getString(i));
            }
            orderInfo.setOrderMessage(temp);
        }
        return orderInfo;
    }
}

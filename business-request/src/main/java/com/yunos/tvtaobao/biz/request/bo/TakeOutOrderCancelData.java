package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class TakeOutOrderCancelData implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String codeType;
    private int codeValue;
    private boolean success;
    private int errorCode;
    private String errorMsg;
    private String bizOrderId;

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public int getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(int codeValue) {
        this.codeValue = codeValue;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getBizOrderId() {
        return bizOrderId;
    }

    public void setBizOrderId(String bizOrderId) {
        this.bizOrderId = bizOrderId;
    }

    public static TakeOutOrderCancelData resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        TakeOutOrderCancelData orderListData = new TakeOutOrderCancelData();
        if (obj != null) {
            orderListData.setCodeValue(obj.optInt("codeValue", 0));
            orderListData.setErrorCode(obj.optInt("errorCode"));
            orderListData.setBizOrderId(obj.optString("bizOrderId"));
            orderListData.setCodeType(obj.optString("codeType"));
            orderListData.setErrorMsg(obj.optString("errorMsg"));
            orderListData.setSuccess(obj.optBoolean("success", false));
        }

        return orderListData;
    }
}

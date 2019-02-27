/**
 * $
 * PROJECT NAME: TopicBuy
 * PACKAGE NAME: com.yunos.tv.topicbuy.bo
 * FILE NAME: TejiaResult.java
 * CREATED TIME: 2014-8-25
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
package com.yunos.tvtaobao.zhuanti.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 天天特价接口返回信息
 * 
 * @version
 * @author hanqi
 * @data 2014-8-27 下午12:20:38
 */
public class TejiaResult implements Serializable {

    private static final long serialVersionUID = -3542847718064771892L;
    // 当前机器信息
    private String Host;
    // 可能返回的提示消息
    private String message;
    // 错误码
    private String code;
    // 是否成功
    private String success;
    // token
    private String token;
    // 当前页大小
    private String pageSize;
    // 当前的页码
    private String pageNum;
    // 总页数
    private String totalPage;
    // 服务器当前时间，毫秒数
    private String curTime;
    // 商品列表
    private List<ItemTejia> itemsArray;

    /**
     * @param obj
     * @return
     * @throws JSONException
     */
    public static TejiaResult fromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;

        }

        TejiaResult tejiaResult = null;
        try {
            tejiaResult = new TejiaResult();
            tejiaResult.setHost(obj.optString("Host"));
            tejiaResult.setMessage(obj.optString("message"));
            tejiaResult.setCode(obj.optString("code"));
            tejiaResult.setSuccess(obj.optString("success"));
            tejiaResult.setToken(obj.optString("token"));
            tejiaResult.setPageSize(obj.optString("pageSize"));
            tejiaResult.setPageNum(obj.optString("pageNum"));
            tejiaResult.setTotalPage(obj.optString("totalPage"));
            tejiaResult.setCurTime(obj.optString("curTime"));

            if (obj.has("itemList")) {
                JSONArray array = obj.getJSONArray("itemList");
                List<ItemTejia> itemTejias = new ArrayList<ItemTejia>();
                for (int i = 0; i < array.length(); i++) {
                    ItemTejia it = ItemTejia.fromMtop(array.getJSONObject(i));
                    itemTejias.add(it);
                }
                tejiaResult.setItemsArray(itemTejias);
            }

        }
        catch (Exception e) {
            tejiaResult = null;
            e.printStackTrace();
        }

        return tejiaResult;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TejiaResult [Host=" + Host + ", message=" + message + ", code="
                + code + ", success=" + success + ", token=" + token
                + ", pageSize=" + pageSize + ", pageNum=" + pageNum
                + ", totalPage=" + totalPage + ", curTime=" + curTime
                + ", itemsArray=" + itemsArray + "]";
    }

    /**
     * @return
     */
    public String getHost() {
        return Host;
    }

    /**
     * @param host
     */
    public void setHost(String host) {
        Host = host;
    }

    /**
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return
     */
    public String getSuccess() {
        return success;
    }

    /**
     * @param success
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return
     */
    public String getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize
     */
    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return
     */
    public String getPageNum() {
        return pageNum;
    }

    /**
     * @param pageNum
     */
    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return
     */
    public String getTotalPage() {
        return totalPage;
    }

    /**
     * @param totalPage
     */
    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    /**
     * @return
     */
    public String getCurTime() {
        return curTime;
    }

    /**
     * @param curTime
     */
    public void setCurTime(String curTime) {
        this.curTime = curTime;
    }

    /**
     * @return
     */
    public List<ItemTejia> getItemsArray() {
        return itemsArray;
    }

    /**
     * @param itemsArray
     */
    public void setItemsArray(List<ItemTejia> itemsArray) {
        this.itemsArray = itemsArray;
    }

}

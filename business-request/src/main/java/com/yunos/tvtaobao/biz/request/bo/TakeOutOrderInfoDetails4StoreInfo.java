package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 外卖订单的订单信息.
 */
public class TakeOutOrderInfoDetails4StoreInfo implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String storeName;
    private String storeLogo;
    private String storeId;
    private String shopStatus;
    private ArrayList<String> servingTimes;
    private String longitude;
    private String latitude;
    private boolean distRst;
    private int dataSourceType;
    private int dataSource;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public ArrayList<String> getServingTimes() {
        return servingTimes;
    }

    public void setServingTimes(ArrayList<String> servingTimes) {
        this.servingTimes = servingTimes;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public boolean isDistRst() {
        return distRst;
    }

    public void setDistRst(boolean distRst) {
        this.distRst = distRst;
    }

    public int getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(int dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public int getDataSource() {
        return dataSource;
    }

    public void setDataSource(int dataSource) {
        this.dataSource = dataSource;
    }

    public static TakeOutOrderInfoDetails4StoreInfo resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        TakeOutOrderInfoDetails4StoreInfo onTimeInfo = new TakeOutOrderInfoDetails4StoreInfo();

        if (obj != null) {
            if (!obj.isNull("servingTimes")) {
                //TODO LYLDEBUG 注意serviceTimes数组的解析
                JSONArray array = obj.getJSONArray("servingTimes");
                ArrayList<String> temp = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    String str = (String) array.get(i);
                    temp.add(str);
                }
                onTimeInfo.setServingTimes(temp);
            }

            onTimeInfo.setDataSourceType(obj.optInt("dataSourceType", 0));
            onTimeInfo.setDataSource(obj.optInt("dataSource", 0));
            onTimeInfo.setDistRst(obj.optBoolean("distRst", false));

            onTimeInfo.setLatitude(obj.optString("latitude"));
            onTimeInfo.setLongitude(obj.optString("longitude"));
            onTimeInfo.setStoreName(obj.optString("storeName"));
            onTimeInfo.setStoreLogo(obj.optString("storeLogo"));
            onTimeInfo.setStoreId(obj.optString("storeId"));
            onTimeInfo.setShopStatus(obj.optString("shopStatus"));
        }

        return onTimeInfo;
    }
}

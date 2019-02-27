package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 外卖订单的订单信息. (订单列表页)
 */
public class TakeOutOrderInfoBase implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private String appointOrderId;
    private String tbMainOrderId;

    private String storeName;
    private String storeLogo;
    private String storeId;

    private String status;
    private String outOrderId;

    private String itemShowTitle;
    private String itemShowPic;
    private String createGMT;

    private int totalFee;

    private String fullProducts;
    private String fullProductsCount;

    private ArrayList<TakeOutOrderProductInfoBase> productInfoBases;

    public ArrayList<TakeOutOrderProductInfoBase> getProductInfoBases() {
        return productInfoBases;
    }

    public void setProductInfoBases(ArrayList<TakeOutOrderProductInfoBase> productInfoBases) {
        this.productInfoBases = productInfoBases;
    }

    public String getFullProductsCount() {
        return fullProductsCount;
    }

    public void setFullProductsCount(String fullProductsCount) {
        this.fullProductsCount = fullProductsCount;
    }

    public String getFullProducts() {
        return fullProducts;
    }

    public void setFullProducts(String fullProducts) {
        this.fullProducts = fullProducts;
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

    public String getItemShowTitle() {
        return itemShowTitle;
    }

    public void setItemShowTitle(String itemShowTitle) {
        this.itemShowTitle = itemShowTitle;
    }

    public String getItemShowPic() {
        return itemShowPic;
    }

    public void setItemShowPic(String itemShowPic) {
        this.itemShowPic = itemShowPic;
    }

    public String getCreateGMT() {
        return createGMT;
    }

    public void setCreateGMT(String createGMT) {
        this.createGMT = createGMT;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public static TakeOutOrderInfoBase resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        TakeOutOrderInfoBase orderInfoBase = new TakeOutOrderInfoBase();

        if (obj != null) {
            orderInfoBase.setTotalFee(obj.optInt("totalFee", 0));
            orderInfoBase.setTbMainOrderId(obj.optString("tbMainOrderId"));
            orderInfoBase.setStoreName(obj.optString("storeName"));
            orderInfoBase.setStoreLogo(obj.optString("storeLogo"));
            orderInfoBase.setStoreId(obj.optString("storeId"));

            orderInfoBase.setStatus(obj.optString("status"));
            orderInfoBase.setOutOrderId(obj.optString("outOrderId"));
            orderInfoBase.setItemShowPic(obj.optString("itemShowPic"));
            orderInfoBase.setItemShowTitle(obj.optString("itemShowTitle"));
            orderInfoBase.setCreateGMT(obj.optString("gmtCreate"));
            orderInfoBase.setAppointOrderId(obj.optString("appointOrderId"));

            if (!obj.isNull("orderItems")) {
                JSONArray array = obj.getJSONArray("orderItems");
                StringBuilder builder = new StringBuilder();
                ArrayList<TakeOutOrderProductInfoBase> temp = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    TakeOutOrderProductInfoBase product =
                            TakeOutOrderProductInfoBase.resolverFromMtop(array.getJSONObject(i));
                    temp.add(product);
                    builder.append(product.getProductTitle());
                    if (i < array.length() - 1) {
                        builder.append(" + ");
                    }
                }
                orderInfoBase.setProductInfoBases(temp);
                orderInfoBase.setFullProducts(builder.toString());
                if (array.length() > 0) {
                    orderInfoBase.setFullProductsCount(array.length() + "件商品");
                }
            }
        }

        return orderInfoBase;
    }
}

package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class OrderCellObject implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5019801047356034179L;
    
    private ArrayList<String> icon;
    private String itemId;
    private String orderId;
    private String snapshot;
    private ArrayList<String> itemPromtion;
    private String pic;
    private String quantity;
    private String sPrice;
    private String title;

    private String itemProperty;

    // private String extraInfo;
    /**
     * @return the snapshot
     */
    public String getSnapshot() {
        return snapshot;
    }

    /**
     * @param snapshot
     *            the snapshot to set
     */
    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     *            the orderId to set
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * @return the icon
     */
    public ArrayList<String> getIcon() {
        return icon;
    }

    public String getItemProperty() {
        return itemProperty;
    }

    public void setItemProperty(String itemProperty) {
        this.itemProperty = itemProperty;
    }

    /**
     * @param icon
     *            the icon to set
     */
    public void setIcon(ArrayList<String> icon) {
        this.icon = icon;
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId
     *            the itemId to set
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the itemPromtion
     */
    public ArrayList<String> getItemPromtion() {
        return itemPromtion;
    }

    /**
     * @param itemPromtion
     *            the itemPromtion to set
     */
    public void setItemPromtion(ArrayList<String> itemPromtion) {
        this.itemPromtion = itemPromtion;
    }

    /**
     * @return the pic
     */
    public String getPic() {
        return pic;
    }

    /**
     * @param pic
     *            the pic to set
     */
    public void setPic(String pic) {
        this.pic = pic;
    }

    /**
     * @return the quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * @param quantity
     *            the quantity to set
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the sPrice
     */
    public String getSPrice() {
        return sPrice;
    }

    /**
     * @param sPrice
     *            the sPrice to set
     */
    public void setSPrice(String sPrice) {
        this.sPrice = sPrice;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    public static OrderCellObject resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        
        OrderCellObject orderCell = new OrderCellObject();
        if (!obj.isNull("itemId")) {
            orderCell.setItemId(obj.getString("itemId"));
        }
        
        if (!obj.isNull("orderId")) {
            orderCell.setOrderId(obj.getString("orderId"));
        }
        
        if (!obj.isNull("snapshot")) {
            orderCell.setSnapshot(obj.getString("snapshot"));
        }
        
        if (!obj.isNull("pic")) {
            orderCell.setPic(obj.getString("pic"));
        }
        
        if (!obj.isNull("quantity")) {
            orderCell.setQuantity(obj.getString("quantity"));
        }
        
        if (!obj.isNull("title")) {
            orderCell.setTitle(obj.getString("title"));
        }
        
        if (!obj.isNull("itemProperty")) {
            orderCell.setItemProperty(obj.getString("itemProperty"));
        }
        
        if (!obj.isNull("sPrice")) {
            orderCell.setSPrice(obj.getString("sPrice"));
        }
        
        if (!obj.isNull("icon")) {
            JSONArray array = obj.getJSONArray("icon");
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                temp.add(array.getString(i));
            }
            orderCell.setIcon(temp);
        }
        
        if (!obj.isNull("itemPromtion")) {
            JSONArray array = obj.getJSONArray("itemPromtion");
            ArrayList<String> temp = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                temp.add(array.getString(i));
            }
            orderCell.setItemPromtion(temp);
        }
        return orderCell;
    }
}

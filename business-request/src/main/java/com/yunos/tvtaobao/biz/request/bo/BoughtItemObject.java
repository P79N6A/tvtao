package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 子订单结构
 * @author shengchun
 *
 */
public class BoughtItemObject implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2232082809347042485L;
    
    /**
     * 商品相关icon
     */
    private ArrayList<String> icon;
    
    /**
     * 商品ID
     */
    private String itemId;
    
    /**
     * 图片
     */
    private String pic;
    
    /**
     * 价格
     */
    private String price;
    
    /**
     * 数量
     */
    private String quantity;
    
    /**
     * 总价
     */
    private String sPrice;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * SKU描述
     */
    private String skuDesc;

    // private String extraInfo;

    /**
     * @return the icon
     */
    public ArrayList<String> getIcon() {
        return icon;
    }

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
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
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price
     *            the price to set
     */
    public void setPrice(String price) {
        this.price = price;
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
    public String getsPrice() {
        return sPrice;
    }

    /**
     * @param sPrice
     *            the sPrice to set
     */
    public void setsPrice(String sPrice) {
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
    
    public static BoughtItemObject resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        BoughtItemObject boughtItemObject = new BoughtItemObject();
        boughtItemObject.setItemId(obj.optString("itemId"));
        boughtItemObject.setPic(obj.optString("pic"));
        boughtItemObject.setPrice(obj.optString("price"));
        boughtItemObject.setQuantity(obj.optString("quantity"));
        boughtItemObject.setSkuDesc(obj.optString("skuDesc"));
        boughtItemObject.setsPrice(obj.optString("sPrice"));
        boughtItemObject.setTitle(obj.optString("title"));
        if (obj.has("icon")) {
            JSONArray iconList = obj.getJSONArray("icon");
            ArrayList<String> icons = new ArrayList<String>();
            for (int i = 0; i < iconList.length(); i++) {
                icons.add(iconList.getString(i));
            }
            boughtItemObject.setIcon(icons);
        }
        return boughtItemObject;
    }
}

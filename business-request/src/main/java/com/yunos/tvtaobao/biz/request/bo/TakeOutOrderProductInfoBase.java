package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 外卖订单的（商品信息）.
 */
public class TakeOutOrderProductInfoBase implements Serializable {

    private static final long serialVersionUID = -610484326142980001L;

    private int discountPrice;
    private int originalPrice;
    private String skuId;
    private String skuTitle;
    private String skuName;
    private int quantity;
    private String productId;
    private String productLogo;
    private String productTitle;

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSkuTitle() {
        return skuTitle;
    }

    public void setSkuTitle(String skuTitle) {
        this.skuTitle = skuTitle;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductLogo() {
        return productLogo;
    }

    public void setProductLogo(String productLogo) {
        this.productLogo = productLogo;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public static TakeOutOrderProductInfoBase resolverFromMtop(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        TakeOutOrderProductInfoBase productInfoBase = new TakeOutOrderProductInfoBase();
        productInfoBase.setProductLogo("");
        if (obj != null) {
            productInfoBase.setSkuName(obj.optString("sku"));
            productInfoBase.setSkuId(obj.optString("skuId"));
            productInfoBase.setSkuTitle(obj.optString("skuTitle"));
            productInfoBase.setDiscountPrice(obj.optInt("discountPrice", 0));
            productInfoBase.setOriginalPrice(obj.optInt("originalPrice", 0));
            productInfoBase.setQuantity(obj.optInt("quantity", 0));

            productInfoBase.setProductLogo(obj.optString("itemLogo"));
            productInfoBase.setProductTitle(obj.optString("itemTitle"));
            productInfoBase.setProductId(obj.optString("itemId"));
        }

        return productInfoBase;
    }
}

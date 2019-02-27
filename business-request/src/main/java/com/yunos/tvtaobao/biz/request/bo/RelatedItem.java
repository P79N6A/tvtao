/** $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE    NAME: RelatedItem.java
 * CREATED TIME: 2015年5月13日
 *    COPYRIGHT: Copyright(c) 2013 ~ 2015  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class RelatedItem implements Serializable {
    
    private static final long serialVersionUID = 4098407391230509977L;
    
    private String title;
    private String picUrl;
    private String salePrice;
    private String reservePrice;
    private String sold;
    private String itemId;
    
    
    public String getItemId() {
        return itemId;
    }

    
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getPicUrl() {
        return picUrl;
    }
    
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    
    public String getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }
    
    public String getReservePrice() {
        return reservePrice;
    }
    
    public void setReservePrice(String reservePrice) {
        this.reservePrice = reservePrice;
    }
    
    public String getSold() {
        return sold;
    }
    
    public void setSold(String sold) {
        this.sold = sold;
    }

}

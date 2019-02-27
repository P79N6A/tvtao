package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by zhoubo on 2018/7/12.
 * zhoubo on 2018/7/12 16:30
 * describition 找相似的
 */

public class FindSameBean {

    private String price;
    private String pic;
    private String itemName;
    private String sold;
    private String monthSellCount;
    private String catId;
    private String itemId;
    private String nid;
    private RebateBo rebateBo;

    public RebateBo getRebateBo() {
        return rebateBo;
    }

    public void setRebateBo(RebateBo rebateBo) {
        this.rebateBo = rebateBo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getMonthSellCount() {
        return monthSellCount;
    }

    public void setMonthSellCount(String monthSellCount) {
        this.monthSellCount = monthSellCount;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }
}

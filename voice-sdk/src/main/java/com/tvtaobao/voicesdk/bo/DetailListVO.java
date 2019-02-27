package com.tvtaobao.voicesdk.bo;

import com.yunos.tvtaobao.biz.request.bo.SearchedGoods;

import java.io.Serializable;

/**
 * Created by yuanqihui on 2018/3/1.
 */

public class DetailListVO implements Serializable {

    private String id;
    private String name;
    private String pic;
    private String uri;
    /**
     * quantity : 1
     * skuId : 0
     */

    private String quantity;
    private String skuId;

    @Override
    public String toString() {
        return "DetailListVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pic='" + pic + '\'' +
                ", uri='" + uri + '\'' +
                ", quantity='" + quantity + '\'' +
                ", skuId='" + skuId + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
}

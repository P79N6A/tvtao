package com.yunos.tvtaobao.bo;

import java.io.Serializable;

/**
 * Created by huangdaju on 17/4/24.
 */

public class DeviceBo implements Serializable {

    private String appKey;
    private String brandName;
    private String productModel;
    private String model;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "DeviceBo{" +
                "appkey='" + appKey + '\'' +
                ", brandName='" + brandName + '\'' +
                ", productModel='" + productModel + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}

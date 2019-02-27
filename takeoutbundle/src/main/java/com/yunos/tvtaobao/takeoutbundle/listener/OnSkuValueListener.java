package com.yunos.tvtaobao.takeoutbundle.listener;



/**
 * Created by chenjiajuan on 17/12/20.
 *
 * @describe sku选择监听
 */

public interface OnSkuValueListener {
    //添加所有sku属性；
    void addSelectSkuData(String skuId, String skuName, String skuValue);
//    void updateSelectedView(String skuName, int lastPosition, int currentPosition);
    void removeSelectedView(String skuName, String sku);
    void updatePriceQuantity(String price,String quantity);


}

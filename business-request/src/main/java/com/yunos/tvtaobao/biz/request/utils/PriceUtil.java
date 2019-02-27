package com.yunos.tvtaobao.biz.request.utils;


import com.taobao.detail.domain.base.PriceUnit;

import java.util.List;

public class PriceUtil {

    /**
     * 获取目前在售价格
     * @param list
     * @return
     */
    public static String getPrice(List<PriceUnit> list) {
        if (list == null || (list != null && list.size() <= 0)) {
            return "";
        }
        
        String price = "";

        int size = list.size();
        for (int i = 0; i < size; i++) {
            PriceUnit tempPriceUnit = list.get(i);
            if (tempPriceUnit.display == 1 || i == 0) {
                price = tempPriceUnit.price;
            }
        }
        
        return price;
    }
    
    /**
     * 获取原价格
     * @param list
     * @return
     */
    public static String getOriginalPrice(List<PriceUnit> list) {
        if (list == null || (list != null && list.size() <= 0)) {
            return "";
        }
        
        String price = "";

        int size = list.size();
        for (int i = 0; i < size; i++) {
            PriceUnit tempPriceUnit = list.get(i);
            if (tempPriceUnit.display == 3 || i == 0) {
                price = tempPriceUnit.price;
            }
        }
        
        return price;
    }
    
    /**
     * 获取价格
     * @return
     */
    public static PriceUnit getPriceUnit(List<PriceUnit> list) {
        if (list == null || (list != null && list.size() <= 0)) {
            return null;
        }
        
        PriceUnit priceUnit = new PriceUnit();

        int size = list.size();
        for (int i = 0; i < size; i++) {
            PriceUnit tempPriceUnit = list.get(i);
            if (tempPriceUnit.display == 1 || i == 0) {
                priceUnit = tempPriceUnit;
                priceUnit.rangePrice = tempPriceUnit.price;
            }
            
            if (tempPriceUnit.display == 3) {
                priceUnit.rangePrice = tempPriceUnit.price;
            }
        }
        
        return priceUnit;
    }
}

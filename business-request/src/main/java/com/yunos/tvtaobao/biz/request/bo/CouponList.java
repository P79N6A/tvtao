/** $
 * PROJECT NAME: TaobaoSDK-2.1.x
 * PACKAGE NAME: com.yunos.tbsdk.bo
 * FILE    NAME: CouponList.java
 * CREATED TIME: 2014-10-8
 *    COPYRIGHT: Copyright(c) 2013 ~ 2014  All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.ArrayList;


public class CouponList implements Serializable {
    private static final long serialVersionUID = -2982865539369660900L;
    
    private ArrayList<Coupon> list;

    private String totalNums;

    public ArrayList<Coupon> getList() {
        return list;
    }

    public void setList(ArrayList<Coupon> list) {
        this.list = list;
    }

    public String getTotalNums() {
        return totalNums;
    }

    public void setTotalNums(String totalNums) {
        this.totalNums = totalNums;
    }
}

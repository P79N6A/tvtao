package com.yunos.tvtaobao.detailbundle.bean;


import java.io.Serializable;

public class FlashsaleGoodsInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5817932836758873941L;
    
    public final static String STATUS_PAST = "0"; // 0：过去

    public final static String STATUS_NOW = "1";  // 1：现在

    public final static String STATUS_FUTURE = "2"; //2：将来


    public String itemId;
    public String status;
    //根据status状态，确定这个time是startTime还是endTime
    public String time;
    public String qianggouId;
    public String price;

    @Override
    public String toString() {
        return "FlashsaleGoodsInfo [itemId=" + itemId + ", status=" + status + ", time=" + time + ", qianggouId="
                + qianggouId + ", price=" + price + "]";
    }

}

package com.yunos.tvtaobao.zhuanti.bo.enumration;


import android.util.SparseArray;

/**
 * 拍品竞拍状态
 * 
 * @author hanqi
 * @date 2014-7-4
 */
public enum BidStatus {
    NOTBID(0, "未出价"), //刚发完宝贝，未有人出价
    BIDDING(1, "出价中"), //如火如荼的出价中，一般均为该状态
    FINISH(2, "已完成"), //生成订单
    FAILED(3, "失败"), //生成订单失败
    CANCLE(4, "流拍");//某些原因取消竞拍，取消生成订单 

    private static SparseArray<BidStatus> pool = new SparseArray<BidStatus>();
    static {
        for (BidStatus each : BidStatus.values()) {
            pool.put(each.value, each);
        }
    }
    private int value;
    private String desc;

    private BidStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static BidStatus index(int value) {
        return pool.get(value);
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}

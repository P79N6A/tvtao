package com.yunos.tvtaobao.zhuanti.bo.enumration;


/**
 * 订单状态
 * 
 * @author hanqi
 * @date 2014-7-11
 */
public enum OrderStatus {
    //等待买家付款
    WAIT_BUYER_PAY,
    //买家已付款，等待卖家发货
    WAIT_SELLER_SEND_GOODS,
    //交易关闭
    CREATE_CLOSED_OF_TAOBAO,
    //支付宝确认汇款中
    WAIT_SYS_CONFIRM_PAY,
    //卖家已发货
    WAIT_BUYER_CONFIRM_GOODS,
    //买家确认收货
    WAIT_SYS_PAY_SELLER,
    //交易成功
    TRADE_FINISHED,
    //交易关闭
    TRADE_CLOSED,
    //交易关闭
    TRADE_REFUSE,
    //交易关闭
    TRADE_REFUSE_DEALING,
    //交易关闭
    TRADE_CANCEL;
}

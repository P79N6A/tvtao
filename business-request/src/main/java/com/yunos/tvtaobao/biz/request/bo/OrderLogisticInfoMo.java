package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;

/**
 * 存储订单时间和信息
 * @author zhuli.zhul
 * @date 2013 2013-6-26 下午5:53:31
 */
public class OrderLogisticInfoMo implements Serializable {

    /** */
    private static final long serialVersionUID = -3000507618972262600L;

    /** 物流时间 */
    private String            time;

    /** 物流信息 */
    private String            message;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

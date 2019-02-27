/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author tianxiang
 * @date 2012-11-3 下午1:22:02
 */
public class CreateOrderResult implements Serializable {

	private String bizOrderId;// 淘宝订单id

	private String alipayOrderId;// 支付宝订单id

	private String buyerNumId;// 买家id

	private String nextUrl;// 下一步跳转的url

	private String secrityPay;

	private long time;

	private String orderKey; 

    // 是否调起支付
	private boolean simplePay;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4393098295706332235L;

	public static CreateOrderResult fromMTOP(JSONObject json) throws Exception {
		CreateOrderResult result = new CreateOrderResult();
		result.setBizOrderId(json.optString("bizOrderId"));
		result.setAlipayOrderId(json.optString("alipayOrderId"));
		result.setBuyerNumId(json.optString("buyerNumId"));
		result.setNextUrl(json.optString("nextUrl"));
		result.setSecrityPay(json.optString("secrityPay"));
		result.setTime(json.optLong("time"));
		result.setOrderKey(json.optString("orderKey"));
		result.setSimplePay(json.optBoolean("simplePay"));
		return result;
	}
	
    public boolean isSimplePay() {
        return simplePay;
    }

    
    public void setSimplePay(boolean simplePay) {
        this.simplePay = simplePay;
    }

	public String getBizOrderId() {
		return bizOrderId;
	}

	public void setBizOrderId(String bizOrderId) {
		this.bizOrderId = bizOrderId;
	}

	public String getAlipayOrderId() {
		return alipayOrderId;
	}

	public void setAlipayOrderId(String alipayOrderId) {
		this.alipayOrderId = alipayOrderId;
	}

	public String getBuyerNumId() {
		return buyerNumId;
	}

	public void setBuyerNumId(String buyerNumId) {
		this.buyerNumId = buyerNumId;
	}

	public String getNextUrl() {
		return nextUrl;
	}

	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	}

	public String getSecrityPay() {
		return secrityPay;
	}

	public void setSecrityPay(String secrityPay) {
		this.secrityPay = secrityPay;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}

    @Override
    public String toString() {
        return "CreateOrderResult [bizOrderId=" + bizOrderId + ", alipayOrderId=" + alipayOrderId + ", buyerNumId="
                + buyerNumId + ", nextUrl=" + nextUrl + ", secrityPay=" + secrityPay + ", time=" + time + ", orderKey="
                + orderKey + ", simplePay=" + simplePay + "]";
    }
}

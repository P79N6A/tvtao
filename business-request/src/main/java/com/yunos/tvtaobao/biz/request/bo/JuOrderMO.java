package com.yunos.tvtaobao.biz.request.bo;

import com.yunos.tv.core.util.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 聚划算订单
 * 
 * @author longyi.hsl 2012-5-11 下午3:01:41
 */
public class JuOrderMO extends BaseMO {
	private static final long serialVersionUID = 1532851975519956232L;

	/**
	 * 订单的商品项
	 */
	private ItemMO itemMO;
	// 创建时间
	private Date gmtCreate;
	// 修改时间
	private Date gmtModified;

	// 记录主键
	private long id;

	/**
	 * 业务订单id,交易线返回的订单编号
	 */
	private long bizOrderId;
	/**
	 * 商品线的商品编号
	 */
	private long itemId;
	/**
	 * 买家用户id
	 */
	private long userId;
	/**
	 * 订单状态
	 */
	private Integer orderStatus;
	/**
	 * 商品销售属性id
	 */
	private long skuId;
	/**
	 * 商品销售属性id
	 */
	private String sku;
	/**
	 * 销售件数
	 */
	private int buyNum;
	/**
	 * 交易类型
	 */
	private int bizType;
	/**
	 * 请求方式，web:0 手机:1
	 */
	private int requestType;

	// 交易标志 0:满团成交 1:定金 2:尾款
	private Integer orderType;

	// 满团成交填写自己的id，定金填写尾款的交易订单id，尾款填写定金的交易订单id
	private String attributes;

	// '通知是否发送 1:尾款确认通知已发送 2:尾款支付通知已发送 3:不生成尾款通知已发送 '
	private Integer noticeStatus;

	// 聚划算活动类型：用于区分聚划算不同活动产生的订单；默认值0：普通聚划算活动，1：聚客活动
	private Integer activityType;

	private long postFee;

	/**
	 * 物流状态 0 ：未发货 1：已发货
	 * 
	 * @author qiunian.zh
	 */
	private Integer logisticsStatus;

	/**
	 * 发货时间
	 * 
	 * @author qiunian.zh
	 */
	private Date deliveryDate;
	/**
	 * 付款时间
	 */
	private Date payTime;

	/**
	 * 订单创建时间
	 */
	private Date orderCreateTime;

	/**
	 * 退款状态
	 */
	private int refundStatus;
	/**
	 * 订单价格
	 */
	private long finalPrice;

	@Override
	public String toString() {
		return "JuOrderMO [itemMO=" + itemMO + ", gmtCreate=" + gmtCreate
				+ ", gmtModified=" + gmtModified + ", id=" + id
				+ ", bizOrderId=" + bizOrderId + ", itemId=" + itemId
				+ ", userId=" + userId + ", orderStatus=" + orderStatus
				+ ", skuId=" + skuId + ", sku=" + sku + ", buyNum=" + buyNum
				+ ", bizType=" + bizType + ", requestType=" + requestType
				+ ", orderType=" + orderType + ", attributes=" + attributes
				+ ", noticeStatus=" + noticeStatus + ", activityType="
				+ activityType + ", postFee=" + postFee + ", logisticsStatus="
				+ logisticsStatus + ", deliveryDate=" + deliveryDate
				+ ", payTime=" + payTime + ", orderCreateTime="
				+ orderCreateTime + ", refundStatus=" + refundStatus
				+ ", finalPrice=" + finalPrice + "]";
	}

	public long getPostFee() {
		return postFee;
	}

	public void setPostFee(long postFee) {
		this.postFee = postFee;
	}

	public ItemMO getItemMO() {
		return itemMO;
	}

	public void setItemMO(ItemMO itemMO) {
		this.itemMO = itemMO;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// public String getSupportTwoCode() {
	// return
	// AttributeUtil.getMapAttributes(attributes).get(OrderAttrKeys.SPT_TWO_CODE);
	// }
	//
	// /**
	// * 订单的单价
	// * @return
	// */
	// public Long getFinalPrice() {
	// try {
	// return
	// Long.parseLong(AttributeUtil.getMapAttributes(attributes).get(OrderAttrKeys.FINAL_PRICE));
	// } catch (NumberFormatException e) {
	// return null;
	// }
	// }
	//
	// /**
	// * 物流费用
	// * @return
	// */
	// public Long getPostFee() {
	// try {
	// return
	// Long.parseLong(AttributeUtil.getMapAttributes(attributes).get(OrderAttrKeys.POST_FEE));
	// } catch (NumberFormatException e) {
	// return null;
	// }
	// }
	//
	// /**
	// * 运送方式
	// * @return
	// */
	// public Long getShipping() {
	// try {
	// return
	// Long.parseLong(AttributeUtil.getMapAttributes(attributes).get(OrderAttrKeys.SHIPPING));
	// } catch (NumberFormatException e) {
	// return null;
	// }
	// }

	public long getBizOrderId() {
		return bizOrderId;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setBizOrderId(long bizOrderId) {
		this.bizOrderId = bizOrderId;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public long getSkuId() {
		return skuId;
	}

	public void setSkuId(long skuId) {
		this.skuId = skuId;
	}

	public int getBuyNum() {
		return buyNum;
	}

	public void setBuyNum(int buyNum) {
		this.buyNum = buyNum;
	}

	public int getBizType() {
		return bizType;
	}

	public void setBizType(int bizType) {
		this.bizType = bizType;
	}

	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public Integer getNoticeStatus() {
		return noticeStatus;
	}

	public void setNoticeStatus(Integer noticeStatus) {
		this.noticeStatus = noticeStatus;
	}

	public Integer getActivityType() {
		return activityType;
	}

	public void setActivityType(Integer activityType) {
		this.activityType = activityType;
	}

	public Integer getLogisticsStatus() {
		return logisticsStatus;
	}

	public void setLogisticsStatus(Integer logisticsStatus) {
		this.logisticsStatus = logisticsStatus;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public int getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(int refundStatus) {
		this.refundStatus = refundStatus;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public long getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(long finalPrice) {
		this.finalPrice = finalPrice;
	}

	public static JuOrderMO fromMTOP(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}

		JuOrderMO juOrder = new JuOrderMO();
		// "activityType":"0",
		// "attributes":"SH:5;FP:1999;PF:0;",
		// "bizOrderId":"150947331386062",
		// "bizType":"200",
		// "buyNum":"1",
		// "finalPrice":"1999",
		// "gmtCreate":"2012-08-13 10:50:43",
		// "gmtModified":"2012-08-13 11:37:41",
		// "id":"0",
		// "itemId":"18359748023",
		// "logisticsStatus":"0",
		// "noticeStatus":"0",
		// "orderCreateTime":"2012-08-13 10:50:43",
		// "orderStatus":"2",
		// "orderType":"0",
		// "payTime":"2012-08-13 11:37:40",
		// "postFee":"0",
		// "refundStatus":"-1",
		// "requestType":"0",
		// "shipping":"5",
		// "skuId":"27841852130",
		// "userId":"870736260"
		if (obj.has("activityType")) {
			juOrder.setActivityType(obj.getInt("activityType"));
		}
		if (obj.has("attributes")) {
			juOrder.setAttributes(obj.getString("attributes"));
		}
		if (obj.has("bizOrderId")) {
			juOrder.setBizOrderId(obj.getLong("bizOrderId"));
		}
		if (obj.has("bizType")) {
			juOrder.setBizType(obj.getInt("bizType"));
		}
		if (obj.has("buyNum")) {
			juOrder.setBuyNum(obj.getInt("buyNum"));
		}
		if (obj.has("finalPrice")) {
			juOrder.setFinalPrice(obj.getLong("finalPrice"));
		}

		if (obj.has("gmtCreate")) {
			juOrder.setGmtCreate(SystemUtil.convertStringToDate(obj
					.getString("gmtCreate")));
		}
		if (obj.has("gmtModified")) {
			juOrder.setGmtModified(SystemUtil.convertStringToDate(obj
					.getString("gmtModified")));
		}
		if (obj.has("id")) {
			juOrder.setId(obj.getLong("id"));
		}
		if (obj.has("itemId")) {
			juOrder.setItemId(obj.getLong("itemId"));
		}
		if (obj.has("logisticsStatus")) {
			juOrder.setLogisticsStatus(obj.getInt("logisticsStatus"));
		}
		if (obj.has("noticeStatus")) {
			juOrder.setNoticeStatus(obj.getInt("noticeStatus"));
		}
		if (obj.has("orderCreateTime")) {
			juOrder.setOrderCreateTime(SystemUtil.convertStringToDate(obj
					.getString("orderCreateTime")));
		}
		if (obj.has("orderStatus")) {
			juOrder.setOrderStatus(obj.getInt("orderStatus"));
		}
		if (obj.has("orderType")) {
			juOrder.setOrderType(obj.getInt("orderType"));
		}
		if (obj.has("payTime")) {
			juOrder.setPayTime(SystemUtil.convertStringToDate(obj
					.getString("payTime")));
		}
		if (obj.has("postFee")) {
			juOrder.setPostFee(obj.getLong("postFee"));
		}
		if (obj.has("refundStatus")) {
			juOrder.setRefundStatus(obj.getInt("refundStatus"));
		}
		if (obj.has("requestType")) {
			juOrder.setRequestType(obj.getInt("requestType"));
		}
		// if (obj.has("shipping")) {
		// j.sets(obj.getString("shipping"));
		// }
		if (obj.has("skuId")) {
			juOrder.setSkuId(obj.getLong("skuId"));
		}
		if (obj.has("sku")) {
			juOrder.setSku(obj.getString("sku"));
		}
		if (obj.has("userId")) {
			juOrder.setUserId(obj.getLong("userId"));
		}

		return juOrder;
	}
}

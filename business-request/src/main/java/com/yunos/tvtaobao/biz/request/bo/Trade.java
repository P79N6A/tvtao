/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 宝贝交易信息
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:23:07
 */
public class Trade implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6696163235927330971L;

	/**
	 * 业务类型及宝贝特性标识,通过数值的二进制位表示， 1000 拍卖(8) 10000 一口价(16) 100000 直充(32) 1000000
	 * 自动发货(64)
	 * 
	 * 10000000 团购(128) (暂时无法判断该标识) 100000000 网游虚拟交易(256)(暂时无法判断该标识) 1000000000
	 * 酒店交易(512) (暂时无法判断该标识)
	 * 
	 * 10000000000 旅游产品(1024) (暂时无法判断该标识) 例如： 10000 第5位为1是一口价 判断方式可以通过与运算： 如
	 * (tag & 16) == 16 为true 表示一口价，false为非一口价
	 */
	private Long tag;

	// true表示客户端支持购物车，false表示客户端不支持购物车(描述的是客户端native功能)
	private Boolean cartSupport;

	// true表示客户端支持直接购买，false表示客户端不支持直接购买 (描述的是客户端native功能)
	private Boolean buySupport;

	/**
	 * 用于通知客户端wap页面上用于支持该业务的URL，如果为空，说明wap页面不支持此业务
	 * 
	 * （这个参数描述的是wap页面的交易能力），对于android客户端（通过ttid判断），会生成如范例所示的URL
	 */
	private String url;

	public Long getTag() {
		return tag;
	}

	public void setTag(Long tag) {
		this.tag = tag;
	}

	public Boolean getCartSupport() {
		return cartSupport;
	}

	public void setCartSupport(Boolean cartSupport) {
		this.cartSupport = cartSupport;
	}

	public Boolean getBuySupport() {
		return buySupport;
	}

	public void setBuySupport(Boolean buySupport) {
		this.buySupport = buySupport;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static Trade resolveFromMTOP(JSONObject obj) throws JSONException {
		if (obj == null) {
			return null;
		}

		Trade trade = new Trade();
		if (!obj.isNull("buySupport")) {
			trade.setBuySupport(obj.getBoolean("buySupport"));
		}

		if (!obj.isNull("cartSupport")) {
			trade.setCartSupport(obj.getBoolean("cartSupport"));
		}

		if (!obj.isNull("tag")) {
			trade.setTag(obj.getLong("tag"));
		}

		if (!obj.isNull("url")) {
			trade.setUrl(obj.getString("url"));
		}

		return trade;
	}

}
